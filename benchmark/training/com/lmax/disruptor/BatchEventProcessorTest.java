/**
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;


import com.lmax.disruptor.support.StubEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public final class BatchEventProcessorTest {
    private final RingBuffer<StubEvent> ringBuffer = RingBuffer.createMultiProducer(StubEvent.EVENT_FACTORY, 16);

    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionOnSettingNullExceptionHandler() {
        final BatchEventProcessor<StubEvent> batchEventProcessor = new BatchEventProcessor<StubEvent>(ringBuffer, sequenceBarrier, new BatchEventProcessorTest.ExceptionEventHandler());
        batchEventProcessor.setExceptionHandler(null);
    }

    @Test
    public void shouldCallMethodsInLifecycleOrderForBatch() throws Exception {
        CountDownLatch eventLatch = new CountDownLatch(3);
        BatchEventProcessorTest.LatchEventHandler eventHandler = new BatchEventProcessorTest.LatchEventHandler(eventLatch);
        final BatchEventProcessor<StubEvent> batchEventProcessor = new BatchEventProcessor<StubEvent>(ringBuffer, sequenceBarrier, eventHandler);
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
        ringBuffer.publish(ringBuffer.next());
        ringBuffer.publish(ringBuffer.next());
        ringBuffer.publish(ringBuffer.next());
        Thread thread = new Thread(batchEventProcessor);
        thread.start();
        Assert.assertTrue(eventLatch.await(2, TimeUnit.SECONDS));
        batchEventProcessor.halt();
        thread.join();
    }

    @Test
    public void shouldCallExceptionHandlerOnUncaughtException() throws Exception {
        CountDownLatch exceptionLatch = new CountDownLatch(1);
        BatchEventProcessorTest.LatchExceptionHandler latchExceptionHandler = new BatchEventProcessorTest.LatchExceptionHandler(exceptionLatch);
        final BatchEventProcessor<StubEvent> batchEventProcessor = new BatchEventProcessor<StubEvent>(ringBuffer, sequenceBarrier, new BatchEventProcessorTest.ExceptionEventHandler());
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
        batchEventProcessor.setExceptionHandler(latchExceptionHandler);
        Thread thread = new Thread(batchEventProcessor);
        thread.start();
        ringBuffer.publish(ringBuffer.next());
        Assert.assertTrue(exceptionLatch.await(2, TimeUnit.SECONDS));
        batchEventProcessor.halt();
        thread.join();
    }

    private static class LatchEventHandler implements EventHandler<StubEvent> {
        private final CountDownLatch latch;

        LatchEventHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onEvent(StubEvent event, long sequence, boolean endOfBatch) throws Exception {
            latch.countDown();
        }
    }

    private static class LatchExceptionHandler implements ExceptionHandler<StubEvent> {
        private final CountDownLatch latch;

        LatchExceptionHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void handleEventException(Throwable ex, long sequence, StubEvent event) {
            latch.countDown();
        }

        @Override
        public void handleOnStartException(Throwable ex) {
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
        }
    }

    private static class ExceptionEventHandler implements EventHandler<StubEvent> {
        @Override
        public void onEvent(StubEvent event, long sequence, boolean endOfBatch) throws Exception {
            throw new NullPointerException(null);
        }
    }

    @Test
    public void reportAccurateBatchSizesAtBatchStartTime() throws Exception {
        final List<Long> batchSizes = new ArrayList<Long>();
        final CountDownLatch eventLatch = new CountDownLatch(6);
        final class LoopbackEventHandler implements BatchStartAware , EventHandler<StubEvent> {
            @Override
            public void onBatchStart(long batchSize) {
                batchSizes.add(batchSize);
            }

            @Override
            public void onEvent(StubEvent event, long sequence, boolean endOfBatch) throws Exception {
                if (!endOfBatch) {
                    ringBuffer.publish(ringBuffer.next());
                }
                eventLatch.countDown();
            }
        }
        final BatchEventProcessor<StubEvent> batchEventProcessor = new BatchEventProcessor<StubEvent>(ringBuffer, sequenceBarrier, new LoopbackEventHandler());
        ringBuffer.publish(ringBuffer.next());
        ringBuffer.publish(ringBuffer.next());
        ringBuffer.publish(ringBuffer.next());
        Thread thread = new Thread(batchEventProcessor);
        thread.start();
        eventLatch.await();
        batchEventProcessor.halt();
        thread.join();
        Assert.assertEquals(Arrays.asList(3L, 2L, 1L), batchSizes);
    }

    @Test
    public void shouldAlwaysHalt() throws InterruptedException {
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        final SingleProducerSequencer sequencer = new SingleProducerSequencer(8, waitStrategy);
        final ProcessingSequenceBarrier barrier = new ProcessingSequenceBarrier(sequencer, waitStrategy, new Sequence((-1)), new Sequence[0]);
        DataProvider<Object> dp = new DataProvider<Object>() {
            @Override
            public Object get(long sequence) {
                return null;
            }
        };
        final BatchEventProcessorTest.LatchLifeCycleHandler h1 = new BatchEventProcessorTest.LatchLifeCycleHandler();
        final BatchEventProcessor p1 = new BatchEventProcessor(dp, barrier, h1);
        Thread t1 = new Thread(p1);
        p1.halt();
        t1.start();
        Assert.assertTrue(h1.awaitStart(2, TimeUnit.SECONDS));
        Assert.assertTrue(h1.awaitStop(2, TimeUnit.SECONDS));
        for (int i = 0; i < 1000; i++) {
            final BatchEventProcessorTest.LatchLifeCycleHandler h2 = new BatchEventProcessorTest.LatchLifeCycleHandler();
            final BatchEventProcessor p2 = new BatchEventProcessor(dp, barrier, h2);
            Thread t2 = new Thread(p2);
            t2.start();
            p2.halt();
            Assert.assertTrue(h2.awaitStart(2, TimeUnit.SECONDS));
            Assert.assertTrue(h2.awaitStop(2, TimeUnit.SECONDS));
        }
        for (int i = 0; i < 1000; i++) {
            final BatchEventProcessorTest.LatchLifeCycleHandler h2 = new BatchEventProcessorTest.LatchLifeCycleHandler();
            final BatchEventProcessor p2 = new BatchEventProcessor(dp, barrier, h2);
            Thread t2 = new Thread(p2);
            t2.start();
            Thread.yield();
            p2.halt();
            Assert.assertTrue(h2.awaitStart(2, TimeUnit.SECONDS));
            Assert.assertTrue(h2.awaitStop(2, TimeUnit.SECONDS));
        }
    }

    private static class LatchLifeCycleHandler implements EventHandler<Object> , LifecycleAware {
        private final CountDownLatch startLatch = new CountDownLatch(1);

        private final CountDownLatch stopLatch = new CountDownLatch(1);

        @Override
        public void onEvent(Object event, long sequence, boolean endOfBatch) throws Exception {
        }

        @Override
        public void onStart() {
            startLatch.countDown();
        }

        @Override
        public void onShutdown() {
            stopLatch.countDown();
        }

        public boolean awaitStart(long time, TimeUnit unit) throws InterruptedException {
            return startLatch.await(time, unit);
        }

        public boolean awaitStop(long time, TimeUnit unit) throws InterruptedException {
            return stopLatch.await(time, unit);
        }
    }
}

