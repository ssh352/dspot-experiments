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


import java.util.NoSuchElementException;
import java.util.UUID;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class DomainEventStreamTest {
    @Test
    public void testPeek() {
        DomainEventMessage event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventMessage event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventStream testSubject = DomainEventStream.of(event1, event2);
        Assert.assertSame(event1, testSubject.peek());
        Assert.assertSame(event1, testSubject.peek());
    }

    @Test
    public void testPeek_EmptyStream() {
        DomainEventStream testSubject = DomainEventStream.of();
        Assert.assertFalse(testSubject.hasNext());
        try {
            testSubject.peek();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // what we expect
        }
    }

    @Test
    public void testNextAndHasNext() {
        DomainEventMessage event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventMessage event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventStream testSubject = DomainEventStream.of(event1, event2);
        Assert.assertTrue(testSubject.hasNext());
        Assert.assertSame(event1, testSubject.next());
        Assert.assertTrue(testSubject.hasNext());
        Assert.assertSame(event2, testSubject.next());
        Assert.assertFalse(testSubject.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNext_ReadBeyondEnd() {
        DomainEventMessage event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventStream testSubject = DomainEventStream.of(event1);
        testSubject.next();
        testSubject.next();
    }
}

