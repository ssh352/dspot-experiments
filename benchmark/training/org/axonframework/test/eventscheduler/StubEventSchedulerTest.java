/**
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.test.eventscheduler;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.axonframework.eventhandling.EventMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Allard Buijze
 */
public class StubEventSchedulerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private StubEventScheduler testSubject;

    @Test
    public void testScheduleEvent() {
        testSubject.schedule(Instant.now().plus(Duration.ofDays(1)), event(new StubEventSchedulerTest.MockEvent()));
        Assert.assertEquals(1, testSubject.getScheduledItems().size());
    }

    @Test
    public void testEventContainsTimestampOfScheduledTime() {
        Instant triggerTime = Instant.now().plusSeconds(60);
        testSubject.schedule(triggerTime, "gone");
        List<EventMessage<?>> triggered = new ArrayList<>();
        testSubject.advanceTimeBy(Duration.ofMinutes(75), triggered::add);
        Assert.assertEquals(1, triggered.size());
        Assert.assertEquals(triggerTime, triggered.get(0).getTimestamp());
    }

    @Test
    public void testInitializeAtDateTimeAfterSchedulingEvent() {
        testSubject.schedule(Instant.now().plus(Duration.ofDays(1)), event(new StubEventSchedulerTest.MockEvent()));
        exception.expect(IllegalStateException.class);
        testSubject.initializeAt(Instant.now().minus(10, ChronoUnit.MINUTES));
    }

    private static class MockEvent {}
}

