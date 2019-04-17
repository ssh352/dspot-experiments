/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Attr.ERROR;
import Queues.SMALL_BUFFER_SIZE;
import Scannable.Attr.PARENT;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.annotation.Nullable;


/**
 *
 *
 * @author Stephane Maldini
 */
public class EmitterProcessorTest {
    // see https://github.com/reactor/reactor-core/issues/1364
    @Test
    public void subscribeWithSyncFusionUpstreamFirst() {
        EmitterProcessor<String> processor = EmitterProcessor.create(16);
        StepVerifier.create(Mono.just("DATA").subscribeWith(processor).map(String::toLowerCase)).expectNext("data").expectComplete().verify(Duration.ofSeconds(1));
        assertThat(processor.blockFirst()).as("later subscription").isNull();
    }

    // see https://github.com/reactor/reactor-core/issues/1290
    @Test
    public void subscribeWithSyncFusionSingle() {
        Processor<Integer, Integer> processor = EmitterProcessor.create(16);
        StepVerifier.create(processor).then(() -> Flux.just(1).subscribe(processor)).expectNext(1).expectComplete().verify(Duration.ofSeconds(1));
    }

    // see https://github.com/reactor/reactor-core/issues/1290
    @Test
    public void subscribeWithSyncFusionMultiple() {
        Processor<Integer, Integer> processor = EmitterProcessor.create(16);
        StepVerifier.create(processor).then(() -> Flux.range(1, 5).subscribe(processor)).expectNext(1, 2, 3, 4, 5).expectComplete().verify(Duration.ofSeconds(1));
    }

    // see https://github.com/reactor/reactor-core/issues/1290
    @Test
    public void subscribeWithAsyncFusion() {
        Processor<Integer, Integer> processor = EmitterProcessor.create(16);
        StepVerifier.create(processor).then(() -> Flux.range(1, 5).publishOn(Schedulers.elastic()).subscribe(processor)).expectNext(1, 2, 3, 4, 5).expectComplete().verify(Duration.ofSeconds(1));
    }

    @Test
    public void testColdIdentityProcessor() throws InterruptedException {
        final int elements = 10;
        CountDownLatch latch = new CountDownLatch((elements + 1));
        Processor<Integer, Integer> processor = EmitterProcessor.create(16);
        List<Integer> list = new ArrayList<>();
        processor.subscribe(new reactor.core.CoreSubscriber<Integer>() {
            Subscription s;

            @Override
            public void onSubscribe(Subscription s) {
                this.s = s;
                s.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                synchronized(list) {
                    list.add(integer);
                }
                latch.countDown();
                if ((latch.getCount()) > 0) {
                    s.request(1);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("completed!");
                latch.countDown();
            }
        });
        Flux.range(1, 10).subscribe(processor);
        // stream.broadcastComplete();
        latch.await(8, TimeUnit.SECONDS);
        long count = latch.getCount();
        Assert.assertTrue((((((("Count > 0 : " + count) + " (") + list) + ")  , Running on ") + (Schedulers.DEFAULT_POOL_SIZE)) + " CPU"), ((latch.getCount()) == 0));
    }

    /* @Test
    public void test100Hot() throws InterruptedException {
    for (int i = 0; i < 10000; i++) {
    testHotIdentityProcessor();
    }
    }
     */
    @Test
    public void testHotIdentityProcessor() throws InterruptedException {
        final int elements = 10000;
        CountDownLatch latch = new CountDownLatch(elements);
        Processor<Integer, Integer> processor = EmitterProcessor.create(1024);
        EmitterProcessor<Integer> stream = EmitterProcessor.create();
        FluxSink<Integer> session = stream.sink();
        stream.subscribe(processor);
        processor.subscribe(new reactor.core.CoreSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(elements);
            }

            @Override
            public void onNext(Integer integer) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(("error! " + t));
            }

            @Override
            public void onComplete() {
                System.out.println("completed!");
                // latch.countDown();
            }
        });
        for (int i = 0; i < elements; i++) {
            session.next(i);
        }
        // stream.then();
        latch.await(8, TimeUnit.SECONDS);
        long count = latch.getCount();
        Assert.assertTrue((((("Count > 0 : " + count) + "  , Running on ") + (Schedulers.DEFAULT_POOL_SIZE)) + " CPU"), ((latch.getCount()) == 0));
        stream.onComplete();
    }

    @Test(expected = NullPointerException.class)
    public void onNextNull() {
        EmitterProcessor.create().onNext(null);
    }

    @Test(expected = NullPointerException.class)
    public void onErrorNull() {
        EmitterProcessor.create().onError(null);
    }

    @Test(expected = NullPointerException.class)
    public void onSubscribeNull() {
        EmitterProcessor.create().onSubscribe(null);
    }

    @Test(expected = NullPointerException.class)
    public void subscribeNull() {
        EmitterProcessor.create().subscribe(((Subscriber<Object>) (null)));
    }

    @Test
    public void normal() {
        EmitterProcessor<Integer> tp = EmitterProcessor.create();
        StepVerifier.create(tp).then(() -> {
            Assert.assertTrue("No subscribers?", tp.hasDownstreams());
            Assert.assertFalse("Completed?", tp.isTerminated());
            Assert.assertNull("Has error?", tp.getError());
        }).then(() -> {
            tp.onNext(1);
            tp.onNext(2);
        }).expectNext(1, 2).then(() -> {
            tp.onNext(3);
            tp.onComplete();
        }).expectNext(3).expectComplete().verify();
        Assert.assertFalse("Subscribers present?", tp.hasDownstreams());
        Assert.assertTrue("Not completed?", tp.isTerminated());
        Assert.assertNull("Has error?", tp.getError());
    }

    @Test
    public void normalBackpressured() {
        EmitterProcessor<Integer> tp = EmitterProcessor.create();
        StepVerifier.create(tp, 0L).then(() -> {
            Assert.assertTrue("No subscribers?", tp.hasDownstreams());
            Assert.assertFalse("Completed?", tp.isTerminated());
            Assert.assertNull("Has error?", tp.getError());
        }).then(() -> {
            tp.onNext(1);
            tp.onNext(2);
            tp.onComplete();
        }).thenRequest(10L).expectNext(1, 2).expectComplete().verify();
        Assert.assertFalse("Subscribers present?", tp.hasDownstreams());
        Assert.assertTrue("Not completed?", tp.isTerminated());
        Assert.assertNull("Has error?", tp.getError());
    }

    @Test
    public void normalAtomicRingBufferBackpressured() {
        EmitterProcessor<Integer> tp = EmitterProcessor.create(100);
        StepVerifier.create(tp, 0L).then(() -> {
            Assert.assertTrue("No subscribers?", tp.hasDownstreams());
            Assert.assertFalse("Completed?", tp.isTerminated());
            Assert.assertNull("Has error?", tp.getError());
        }).then(() -> {
            tp.onNext(1);
            tp.onNext(2);
            tp.onComplete();
        }).thenRequest(10L).expectNext(1, 2).expectComplete().verify();
        Assert.assertFalse("Subscribers present?", tp.hasDownstreams());
        Assert.assertTrue("Not completed?", tp.isTerminated());
        Assert.assertNull("Has error?", tp.getError());
    }

    @Test
    public void state() {
        EmitterProcessor<Integer> tp = EmitterProcessor.create();
        assertThat(tp.getPending()).isEqualTo(0);
        assertThat(tp.getBufferSize()).isEqualTo(SMALL_BUFFER_SIZE);
        assertThat(tp.isCancelled()).isFalse();
        assertThat(tp.inners()).isEmpty();
        Disposable d1 = tp.subscribe();
        assertThat(tp.inners()).hasSize(1);
        FluxSink<Integer> s = tp.sink();
        s.next(2);
        s.next(3);
        s.next(4);
        assertThat(tp.getPending()).isEqualTo(0);
        AtomicReference<Subscription> d2 = new AtomicReference<>();
        tp.subscribe(new reactor.core.CoreSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                d2.set(s);
            }

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        s.next(5);
        s.next(6);
        s.next(7);
        assertThat(tp.scan(BUFFERED)).isEqualTo(3);
        assertThat(tp.isTerminated()).isFalse();
        s.complete();
        assertThat(tp.isTerminated()).isFalse();
        d1.dispose();
        d2.get().cancel();
        assertThat(tp.isTerminated()).isTrue();
        StepVerifier.create(tp).verifyComplete();
        tp.onNext(8);// noop

        EmitterProcessor<Void> empty = EmitterProcessor.create();
        empty.onComplete();
        assertThat(empty.isTerminated()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNullBufferSize() {
        EmitterProcessor.create(0);
    }

    @Test(expected = NullPointerException.class)
    public void failNullNext() {
        EmitterProcessor.create().onNext(null);
    }

    @Test(expected = NullPointerException.class)
    public void failNullError() {
        EmitterProcessor.create().onError(null);
    }

    @Test
    public void failDoubleError() {
        EmitterProcessor<Integer> ep = EmitterProcessor.create();
        StepVerifier.create(ep).then(() -> {
            assertThat(ep.getError()).isNull();
            ep.onError(new Exception("test"));
            assertThat(ep.getError()).hasMessage("test");
            ep.onError(new Exception("test2"));
        }).expectErrorMessage("test").verifyThenAssertThat().hasDroppedErrorWithMessage("test2");
    }

    @Test
    public void failCompleteThenError() {
        EmitterProcessor<Integer> ep = EmitterProcessor.create();
        StepVerifier.create(ep).then(() -> {
            ep.onComplete();
            ep.onComplete();// noop

            ep.onError(new Exception("test"));
        }).expectComplete().verifyThenAssertThat().hasDroppedErrorWithMessage("test");
    }

    @Test
    public void ignoreDoubleOnSubscribe() {
        EmitterProcessor<Integer> ep = EmitterProcessor.create();
        ep.sink();
        assertThat(ep.sink().isCancelled()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNegativeBufferSize() {
        EmitterProcessor.create((-1));
    }

    static final List<String> DATA = new ArrayList<>();

    static final int MAX_SIZE = 100;

    static {
        for (int i = 1; i <= (EmitterProcessorTest.MAX_SIZE); i++) {
            EmitterProcessorTest.DATA.add(("" + i));
        }
    }

    @Test
    public void testRed() {
        FluxProcessor<String, String> processor = EmitterProcessor.create();
        AssertSubscriber<String> subscriber = AssertSubscriber.create(1);
        processor.subscribe(subscriber);
        Flux.fromIterable(EmitterProcessorTest.DATA).log().subscribe(processor);
        subscriber.awaitAndAssertNextValues("1");
    }

    @Test
    public void testGreen() {
        FluxProcessor<String, String> processor = EmitterProcessor.create();
        AssertSubscriber<String> subscriber = AssertSubscriber.create(1);
        processor.subscribe(subscriber);
        Flux.fromIterable(EmitterProcessorTest.DATA).log().subscribe(processor);
        subscriber.awaitAndAssertNextValues("1");
    }

    @Test
    public void testHanging() {
        FluxProcessor<String, String> processor = EmitterProcessor.create(2);
        AssertSubscriber<String> first = AssertSubscriber.create(0);
        processor.log("after-1").subscribe(first);
        AssertSubscriber<String> second = AssertSubscriber.create(0);
        processor.log("after-2").subscribe(second);
        Flux.fromIterable(EmitterProcessorTest.DATA).log().subscribe(processor);
        second.request(1);
        second.assertNoValues();
        first.request(3);
        second.awaitAndAssertNextValues("1");
        second.cancel();
        first.awaitAndAssertNextValues("1", "2", "3");
        first.cancel();
        assertThat(processor.scanOrDefault(CANCELLED, false)).isTrue();
    }

    @Test
    public void testNPE() {
        FluxProcessor<String, String> processor = EmitterProcessor.create(8);
        AssertSubscriber<String> first = AssertSubscriber.create(1);
        processor.log().take(1).subscribe(first);
        AssertSubscriber<String> second = AssertSubscriber.create(3);
        processor.log().subscribe(second);
        Flux.fromIterable(EmitterProcessorTest.DATA).log().subscribe(processor);
        first.awaitAndAssertNextValues("1");
        second.awaitAndAssertNextValues("1", "2", "3");
    }

    static class MyThread extends Thread {
        private final Flux<String> processor;

        private final CyclicBarrier barrier;

        private final int n;

        private volatile Throwable lastException;

        class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                lastException = e;
            }
        }

        public MyThread(FluxProcessor<String, String> processor, CyclicBarrier barrier, int n, int index) {
            this.processor = processor.log(("consuming." + index));
            this.barrier = barrier;
            this.n = n;
            setUncaughtExceptionHandler(new EmitterProcessorTest.MyThread.MyUncaughtExceptionHandler());
        }

        @Override
        public void run() {
            try {
                doRun();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void doRun() throws Exception {
            AssertSubscriber<String> subscriber = AssertSubscriber.create(5);
            processor.subscribe(subscriber);
            barrier.await();
            subscriber.request(3);
            subscriber.request(4);
            subscriber.request(1);
            subscriber.await().assertValueCount(n).assertComplete();
        }

        @Nullable
        public Throwable getLastException() {
            return lastException;
        }
    }

    @Test
    public void testThreadAffinity() throws InterruptedException {
        int count = 10;
        Scheduler[] schedulers = new Scheduler[4];
        CountDownLatch[] latches = new CountDownLatch[schedulers.length];
        for (int i = 0; i < (schedulers.length); i++) {
            schedulers[i] = Schedulers.newSingle((("scheduler" + i) + '-'));
            int expectedCount = (i == 1) ? count * 2 : count;
            latches[i] = new CountDownLatch(expectedCount);
        }
        EmitterProcessor<Integer> processor = EmitterProcessor.create();
        processor.publishOn(schedulers[0]).share();
        processor.publishOn(schedulers[1]).subscribe(( i) -> {
            assertThat(Thread.currentThread().getName().contains("scheduler1")).isTrue();
            latches[1].countDown();
        });
        for (int i = 0; i < count; i++)
            processor.onNext(i);

        processor.publishOn(schedulers[2]).map(( i) -> {
            assertThat(Thread.currentThread().getName().contains("scheduler2")).isTrue();
            latches[2].countDown();
            return i;
        }).publishOn(schedulers[3]).doOnNext(( i) -> {
            assertThat(Thread.currentThread().getName().contains("scheduler3")).isTrue();
            latches[3].countDown();
        }).subscribe();
        for (int i = 0; i < count; i++)
            processor.onNext(i);

        processor.onComplete();
        for (int i = 1; i < (latches.length); i++)
            assertThat(latches[i].await(5, TimeUnit.SECONDS)).isTrue();

        assertThat(latches[0].getCount()).isEqualTo(count);
    }

    @Test
    public void createDefault() {
        EmitterProcessor<Integer> processor = EmitterProcessor.create();
        assertProcessor(processor, null, null);
    }

    @Test
    public void createOverrideBufferSize() {
        int bufferSize = 1024;
        EmitterProcessor<Integer> processor = EmitterProcessor.create(bufferSize);
        assertProcessor(processor, bufferSize, null);
    }

    @Test
    public void createOverrideAutoCancel() {
        boolean autoCancel = false;
        EmitterProcessor<Integer> processor = EmitterProcessor.create(autoCancel);
        assertProcessor(processor, null, autoCancel);
    }

    @Test
    public void createOverrideAll() {
        int bufferSize = 1024;
        boolean autoCancel = false;
        EmitterProcessor<Integer> processor = EmitterProcessor.create(bufferSize, autoCancel);
        assertProcessor(processor, bufferSize, autoCancel);
    }

    @Test
    public void scanMain() {
        EmitterProcessor<Integer> test = EmitterProcessor.create(123);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
        assertThat(test.scan(CAPACITY)).isEqualTo(123);
        Disposable d1 = test.subscribe();
        FluxSink<Integer> sink = test.sink();
        sink.next(2);
        sink.next(3);
        sink.next(4);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        AtomicReference<Subscription> d2 = new AtomicReference<>();
        test.subscribe(new reactor.core.CoreSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                d2.set(s);
            }

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        sink.next(5);
        sink.next(6);
        sink.next(7);
        assertThat(test.scan(BUFFERED)).isEqualTo(3);
        assertThat(test.scan(TERMINATED)).isFalse();
        sink.complete();
        assertThat(test.scan(TERMINATED)).isFalse();
        d1.dispose();
        d2.get().cancel();
        assertThat(test.scan(TERMINATED)).isTrue();
        // other values
        assertThat(test.scan(PARENT)).isNotNull();
        assertThat(test.scan(ERROR)).isNull();
    }

    @Test
    public void scanMainCancelled() {
        EmitterProcessor test = EmitterProcessor.create();
        test.onSubscribe(Operators.cancelledSubscription());
        assertThat(test.scan(CANCELLED)).isTrue();
        assertThat(test.isCancelled()).isTrue();
    }

    @Test
    public void scanMainError() {
        EmitterProcessor test = EmitterProcessor.create();
        test.sink().error(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).hasMessage("boom");
    }
}
