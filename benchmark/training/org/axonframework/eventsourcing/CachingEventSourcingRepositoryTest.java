/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing;


import java.util.ArrayList;
import java.util.List;
import org.axonframework.common.caching.Cache;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.utils.MockException;
import org.axonframework.eventsourcing.utils.StubAggregate;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.LockAwareAggregate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class CachingEventSourcingRepositoryTest {
    private CachingEventSourcingRepository<StubAggregate> testSubject;

    private EventStore mockEventStore;

    private Cache cache;

    private Cache ehCache;

    @SuppressWarnings("unchecked")
    @Test
    public void testAggregatesRetrievedFromCache() throws Exception {
        startAndGetUnitOfWork();
        LockAwareAggregate<StubAggregate, EventSourcedAggregate<StubAggregate>> aggregate1 = testSubject.newInstance(() -> new StubAggregate("aggregateId"));
        aggregate1.execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        startAndGetUnitOfWork();
        LockAwareAggregate<StubAggregate, EventSourcedAggregate<StubAggregate>> reloadedAggregate1 = testSubject.load("aggregateId", null);
        Assert.assertSame(aggregate1.getWrappedAggregate(), reloadedAggregate1.getWrappedAggregate());
        aggregate1.execute(StubAggregate::doSomething);
        aggregate1.execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        DefaultUnitOfWork.startAndGet(null);
        DomainEventStream events = mockEventStore.readEvents("aggregateId");
        List<EventMessage> eventList = new ArrayList<>();
        while (events.hasNext()) {
            eventList.add(events.next());
        } 
        Assert.assertEquals(3, eventList.size());
        ehCache.removeAll();
        reloadedAggregate1 = testSubject.load(aggregate1.identifierAsString(), null);
        Assert.assertNotSame(aggregate1.getWrappedAggregate(), reloadedAggregate1.getWrappedAggregate());
        Assert.assertEquals(aggregate1.version(), reloadedAggregate1.version());
    }

    @Test
    public void testLoadDeletedAggregate() throws Exception {
        String identifier = "aggregateId";
        startAndGetUnitOfWork();
        Aggregate<StubAggregate> aggregate1 = testSubject.newInstance(() -> new StubAggregate(identifier));
        CurrentUnitOfWork.commit();
        startAndGetUnitOfWork();
        testSubject.load(identifier).execute(( r) -> AggregateLifecycle.markDeleted());
        CurrentUnitOfWork.commit();
        startAndGetUnitOfWork();
        try {
            testSubject.load(identifier);
            Assert.fail("Expected AggregateDeletedException");
        } catch (AggregateDeletedException e) {
            Assert.assertTrue(e.getMessage().contains(identifier));
        } finally {
            CurrentUnitOfWork.commit();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCacheClearedAfterRollbackOfAddedAggregate() throws Exception {
        UnitOfWork<?> uow = startAndGetUnitOfWork();
        Mockito.doThrow(new MockException()).when(mockEventStore).publish(ArgumentMatchers.anyList());
        try {
            testSubject.newInstance(() -> new StubAggregate("id1")).execute(StubAggregate::doSomething);
            Assert.fail("Applied aggregate should have caused an exception");
        } catch (MockException e) {
            uow.rollback();
        }
        Assert.assertNull(cache.get("id1"));
    }

    private static class StubAggregateFactory extends AbstractAggregateFactory<StubAggregate> {
        public StubAggregateFactory() {
            super(StubAggregate.class);
        }

        @Override
        public StubAggregate doCreateAggregate(String aggregateIdentifier, DomainEventMessage firstEvent) {
            return new StubAggregate(aggregateIdentifier);
        }

        @Override
        public Class<StubAggregate> getAggregateType() {
            return StubAggregate.class;
        }
    }
}

