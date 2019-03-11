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
package org.axonframework.eventhandling.async;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class AsynchronousEventProcessorConcurrencyTest {
    private ExecutorService executor;

    private AsynchronousEventProcessingStrategy testSubject;

    @Test(timeout = AsynchronousEventProcessorConcurrencyTest.EventsPublisher.EVENTS_COUNT)
    public void testHandleEvents() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Consumer<List<? extends EventMessage<?>>> processor = ( eventMessages) -> counter.addAndGet(eventMessages.size());
        int threadCount = 50;
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new AsynchronousEventProcessorConcurrencyTest.EventsPublisher(processor));
        }
        while ((counter.get()) < (threadCount * (AsynchronousEventProcessorConcurrencyTest.EventsPublisher.EVENTS_COUNT))) {
            Thread.sleep(10);
        } 
        executor.shutdown();
        Assert.assertTrue("Executor not closed within a reasonable timeframe", executor.awaitTermination(10, TimeUnit.SECONDS));
        Assert.assertEquals((threadCount * (AsynchronousEventProcessorConcurrencyTest.EventsPublisher.EVENTS_COUNT)), counter.get());
    }

    private class EventsPublisher implements Runnable {
        private static final int ITERATIONS = 10000;

        private static final int EVENTS_COUNT = (AsynchronousEventProcessorConcurrencyTest.EventsPublisher.ITERATIONS) * 3;

        private final Consumer<List<? extends EventMessage<?>>> processor;

        public EventsPublisher(Consumer<List<? extends EventMessage<?>>> processor) {
            this.processor = processor;
        }

        @Override
        public void run() {
            for (int i = 0; i < (AsynchronousEventProcessorConcurrencyTest.EventsPublisher.ITERATIONS); i++) {
                testSubject.handle(Collections.singletonList(GenericEventMessage.asEventMessage("1")), processor);
                testSubject.handle(Collections.singletonList(GenericEventMessage.asEventMessage("2")), processor);
                testSubject.handle(Collections.singletonList(GenericEventMessage.asEventMessage("3")), processor);
            }
        }
    }
}

