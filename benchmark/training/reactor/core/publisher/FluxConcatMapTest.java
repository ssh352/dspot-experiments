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


import FluxConcatMap.ConcatMapDelayed;
import FluxConcatMap.ConcatMapImmediate;
import FluxConcatMap.ErrorMode.END;
import FluxConcatMap.ErrorMode.IMMEDIATE;
import Hooks.KEY_ON_DISCARD;
import Queues.XS_BUFFER_SIZE;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.DELAY_ERROR;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;
import reactor.util.context.Context;


public class FluxConcatMapTest extends FluxOperatorTest<String, String> {
    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).concatMap(( v) -> Flux.range(v, 2)).subscribe(ts);
        ts.assertValues(1, 2, 2, 3).assertNoError().assertComplete();
    }

    @Test
    public void normal2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).hide().concatMap(( v) -> Flux.range(v, 2)).subscribe(ts);
        ts.assertValues(1, 2, 2, 3).assertNoError().assertComplete();
    }

    @Test
    public void normalBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).concatMapDelayError(( v) -> Flux.range(v, 2)).subscribe(ts);
        ts.assertValues(1, 2, 2, 3).assertNoError().assertComplete();
    }

    @Test
    public void normalBoundary2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).hide().concatMapDelayError(( v) -> Flux.range(v, 2)).subscribe(ts);
        ts.assertValues(1, 2, 2, 3).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRun() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000).concatMap(( v) -> Flux.range(v, 1000)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRunJust() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).concatMap(( v) -> Flux.just(v)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRun2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000).hide().concatMap(( v) -> Flux.range(v, 1000)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRunBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000).concatMapDelayError(( v) -> Flux.range(v, 1000)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRunJustBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).concatMapDelayError(( v) -> Flux.just(v)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void normalLongRunBoundary2() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000).hide().concatMapDelayError(( v) -> Flux.range(v, 1000)).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    // see https://github.com/reactor/reactor-core/issues/1302
    @Test
    public void boundaryFusion() {
        Flux.range(1, 10000).publishOn(Schedulers.single()).map(( t) -> Thread.currentThread().getName().contains("single-") ? "single" : ("BAD-" + t) + (Thread.currentThread().getName())).concatMap(Flux::just).publishOn(Schedulers.elastic()).distinct().as(StepVerifier::create).expectFusion().expectNext("single").expectComplete().verify(Duration.ofSeconds(5));
    }

    // see https://github.com/reactor/reactor-core/issues/1302
    @Test
    public void boundaryFusionDelayError() {
        Flux.range(1, 10000).publishOn(Schedulers.single()).map(( t) -> Thread.currentThread().getName().contains("single-") ? "single" : ("BAD-" + t) + (Thread.currentThread().getName())).concatMapDelayError(Flux::just).publishOn(Schedulers.elastic()).distinct().as(StepVerifier::create).expectFusion().expectNext("single").expectComplete().verify(Duration.ofSeconds(5));
    }

    @Test
    public void singleSubscriberOnly() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMap(( v) -> v == 1 ? source1 : source2).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        source.onNext(2);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source2.onNext(10);
        source1.onComplete();
        source.onComplete();
        source2.onNext(2);
        source2.onComplete();
        ts.assertValues(1, 2).assertNoError().assertComplete();
    }

    @Test
    public void singleSubscriberOnlyBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMapDelayError(( v) -> v == 1 ? source1 : source2).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source2.onNext(10);
        source1.onComplete();
        source.onNext(2);
        source.onComplete();
        source2.onNext(2);
        source2.onComplete();
        ts.assertValues(1, 2).assertNoError().assertComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void mainErrorsImmediate() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMap(( v) -> v == 1 ? source1 : source2).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source.onError(new RuntimeException("forced failure"));
        ts.assertValues(1).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void mainErrorsBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMapDelayError(( v) -> v == 1 ? source1 : source2).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source.onError(new RuntimeException("forced failure"));
        ts.assertValues(1).assertNoError().assertNotComplete();
        source1.onNext(2);
        source1.onComplete();
        ts.assertValues(1, 2).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void innerErrorsImmediate() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMap(( v) -> v == 1 ? source1 : source2).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source1.onError(new RuntimeException("forced failure"));
        ts.assertValues(1).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void innerErrorsBoundary() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        // gh-1101: default changed from BOUNDARY to END
        source.concatMapDelayError(( v) -> v == 1 ? source1 : source2, false, XS_BUFFER_SIZE).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source1.onError(new RuntimeException("forced failure"));
        ts.assertValues(1).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void innerErrorsEnd() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source = DirectProcessor.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        source.concatMapDelayError(( v) -> v == 1 ? source1 : source2, true, 32).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        source.onNext(1);
        Assert.assertTrue("source1 no subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
        source1.onNext(1);
        source1.onError(new RuntimeException("forced failure"));
        source.onNext(2);
        Assert.assertTrue("source2 no subscribers?", source2.hasDownstreams());
        source2.onNext(2);
        source2.onComplete();
        source.onComplete();
        ts.assertValues(1, 2).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("source1 has subscribers?", source1.hasDownstreams());
        Assert.assertFalse("source2 has subscribers?", source2.hasDownstreams());
    }

    @Test
    public void syncFusionMapToNull() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).map(( v) -> v == 2 ? null : v).concatMap(Flux::just).subscribe(ts);
        ts.assertValues(1).assertError(NullPointerException.class).assertNotComplete();
    }

    @Test
    public void syncFusionMapToNullFilter() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).map(( v) -> v == 2 ? null : v).filter(( v) -> true).concatMap(Flux::just).subscribe(ts);
        ts.assertValues(1).assertError(NullPointerException.class).assertNotComplete();
    }

    @Test
    public void asyncFusionMapToNull() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        UnicastProcessor<Integer> up = UnicastProcessor.create(Queues.<Integer>get(2).get());
        up.onNext(1);
        up.onNext(2);
        up.onComplete();
        up.map(( v) -> v == 2 ? null : v).concatMap(Flux::just).subscribe(ts);
        ts.assertValues(1).assertError(NullPointerException.class).assertNotComplete();
    }

    @Test
    public void asyncFusionMapToNullFilter() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        UnicastProcessor<Integer> up = UnicastProcessor.create(Queues.<Integer>get(2).get());
        up.onNext(1);
        up.onNext(2);
        up.onComplete();
        up.map(( v) -> v == 2 ? null : v).filter(( v) -> true).concatMap(Flux::just).subscribe(ts);
        ts.assertValues(1).assertError(NullPointerException.class).assertNotComplete();
    }

    @Test
    public void scalarAndRangeBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        @SuppressWarnings("unchecked")
        Publisher<Integer>[] sources = new Publisher[]{ Flux.just(1), Flux.range(2, 3) };
        Flux.range(0, 2).concatMap(( v) -> sources[v]).subscribe(ts);
        ts.assertNoValues().assertNoError();
        ts.request(5);
        ts.assertValues(1, 2, 3, 4).assertComplete().assertNoError();
    }

    @Test
    public void allEmptyBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.range(0, 10).hide().concatMap(( v) -> Flux.<Integer>empty(), 2).subscribe(ts);
        ts.assertNoValues().assertComplete().assertNoError();
    }

    @Test
    public void publisherOfPublisher() {
        StepVerifier.create(Flux.concat(Flux.just(Flux.just(1, 2), Flux.just(3, 4)))).expectNext(1, 2, 3, 4).verifyComplete();
    }

    @Test
    public void publisherOfPublisherDelay() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2), Flux.just(3, 4)))).expectNext(1, 2, 3, 4).verifyComplete();
    }

    @Test
    public void publisherOfPublisherDelayError() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)))).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    @Test
    public void publisherOfPublisherDelayError2() {
        StepVerifier.create(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)).concatMap(( f) -> f)).expectNext(1, 2).verifyErrorMessage("test");
    }

    @Test
    public void publisherOfPublisherDelayEnd3() {
        StepVerifier.create(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)).concatMapDelayError(( f) -> f, true, 128)).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    @Test
    public void publisherOfPublisherDelayEndNot3() {
        StepVerifier.create(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)).concatMapDelayError(( f) -> f, false, 128)).expectNext(1, 2).verifyErrorMessage("test");
    }

    @Test
    public void publisherOfPublisherDelayEnd() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2), Flux.just(3, 4)), false, 128)).expectNext(1, 2, 3, 4).verifyComplete();
    }

    @Test
    public void publisherOfPublisherDelayEndError() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)), false, 128)).expectNext(1, 2).verifyErrorMessage("test");
    }

    @Test
    public void publisherOfPublisherDelayEnd2() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2), Flux.just(3, 4)), true, 128)).expectNext(1, 2, 3, 4).verifyComplete();
    }

    @Test
    public void publisherOfPublisherDelayEndError2() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2).concatWith(Flux.error(new Exception("test"))), Flux.just(3, 4)), true, 128)).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    @Test
    public void issue422() {
        Flux<Integer> source = Flux.create(( sink) -> {
            for (int i = 0; i < 300; i++) {
                sink.next(i);
            }
            sink.complete();
        });
        Flux<Integer> cached = source.cache();
        long cachedCount = cached.concatMapIterable(Collections::singleton).distinct().count().block();
        // System.out.println("source: " + sourceCount);
        System.out.println(("cached: " + cachedCount));
    }

    @Test
    public void prefetchMaxTranslatesToUnboundedRequest() {
        AtomicLong requested = new AtomicLong();
        StepVerifier.create(Flux.just(1, 2, 3).hide().doOnRequest(requested::set).concatMap(( i) -> Flux.range(0, i), Integer.MAX_VALUE)).expectNext(0, 0, 1, 0, 1, 2).verifyComplete();
        assertThat(requested.get()).isNotEqualTo(Integer.MAX_VALUE).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void prefetchMaxTranslatesToUnboundedRequest2() {
        AtomicLong requested = new AtomicLong();
        StepVerifier.create(Flux.just(1, 2, 3).hide().doOnRequest(requested::set).concatMapDelayError(( i) -> Flux.range(0, i), Integer.MAX_VALUE)).expectNext(0, 0, 1, 0, 1, 2).verifyComplete();
        assertThat(requested.get()).isNotEqualTo(Integer.MAX_VALUE).isEqualTo(Long.MAX_VALUE);
    }

    // see https://github.com/reactor/reactor-core/issues/936
    @Test
    public void concatDelayErrorWithFluxError() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2), Flux.error(new Exception("test")), Flux.just(3, 4)))).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    // see https://github.com/reactor/reactor-core/issues/936
    @Test
    public void concatDelayErrorWithMonoError() {
        StepVerifier.create(Flux.concatDelayError(Flux.just(Flux.just(1, 2), Mono.error(new Exception("test")), Flux.just(3, 4)))).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    // see https://github.com/reactor/reactor-core/issues/936
    @Test
    public void concatMapDelayErrorWithFluxError() {
        StepVerifier.create(Flux.just(Flux.just(1, 2), Flux.<Integer>error(new Exception("test")), Flux.just(3, 4)).concatMapDelayError(( f) -> f, true, 32)).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    // see https://github.com/reactor/reactor-core/issues/936
    @Test
    public void concatMapDelayErrorWithMonoError() {
        StepVerifier.create(Flux.just(Flux.just(1, 2), Mono.<Integer>error(new Exception("test")), Flux.just(3, 4)).concatMapDelayError(( f) -> f, true, 32)).expectNext(1, 2, 3, 4).verifyErrorMessage("test");
    }

    @Test
    public void scanConcatMapDelayed() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ConcatMapDelayed<String, Integer> test = new FluxConcatMap.ConcatMapDelayed<>(actual, ( s) -> Mono.just(s.length()), Queues.one(), 123, true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        test.error = new IllegalStateException("boom");
        test.queue.offer("foo");
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
        assertThat(test.scan(DELAY_ERROR)).isTrue();
        assertThat(test.scan(ERROR)).hasMessage("boom");
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancelled = true;
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanConcatMapImmediate() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ConcatMapImmediate<String, Integer> test = new FluxConcatMap.ConcatMapImmediate<>(actual, ( s) -> Mono.just(s.length()), Queues.one(), 123);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        test.queue.offer("foo");
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(PREFETCH)).isEqualTo(123);
        assertThat(test.scan(DELAY_ERROR)).isFalse();
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancelled = true;
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanConcatMapImmediateError() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ConcatMapImmediate<String, Integer> test = new FluxConcatMap.ConcatMapImmediate<>(actual, ( s) -> Mono.just(s.length()), Queues.one(), 123);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(DELAY_ERROR)).isFalse();
        // note that most of the time, the error will be hidden by TERMINATED as soon as it has been propagated downstream :(
        test.error = new IllegalStateException("boom");
        assertThat(test.scan(ERROR)).hasMessage("boom");
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom2"));
        assertThat(test.scan(ERROR)).isSameAs(Exceptions.TERMINATED);
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void errorModeContinueNullPublisher() {
        Flux<Integer> test = Flux.just(1, 2).hide().<Integer>concatMap(( f) -> null).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectComplete().verifyThenAssertThat().hasDropped(1, 2).hasDroppedErrors(2);
    }

    @Test
    public void errorModeContinueInternalError() {
        Flux<Integer> test = Flux.just(1, 2).hide().concatMap(( f) -> {
            if (f == 1) {
                return Mono.error(new NullPointerException());
            } else {
                return Mono.just(f);
            }
        }).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasDropped(1).hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueInternalErrorHidden() {
        Flux<Integer> test = Flux.just(1, 2).hide().concatMap(( f) -> {
            if (f == 1) {
                return Mono.<Integer>error(new NullPointerException()).hide();
            } else {
                return Mono.just(f);
            }
        }).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueWithCallable() {
        Flux<Integer> test = Flux.just(1, 2).hide().concatMap(( f) -> Mono.<Integer>fromRunnable(() -> {
            if (f == 1) {
                throw new ArithmeticException("boom");
            }
        })).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectComplete().verifyThenAssertThat().hasDropped(1).hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueDelayErrors() {
        Flux<Integer> test = Flux.just(1, 2).hide().concatMapDelayError(( f) -> {
            if (f == 1) {
                return Mono.<Integer>error(new NullPointerException()).hide();
            } else {
                return Mono.just(f);
            }
        }).onErrorContinue(OnNextFailureStrategyTest::drop);
        // When inner is not a Callable error value is not available.
        StepVerifier.create(test).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueDelayErrorsWithCallable() {
        Flux<Integer> test = Flux.just(1, 2).hide().concatMapDelayError(( f) -> {
            if (f == 1) {
                return Mono.<Integer>error(new NullPointerException());
            } else {
                return Mono.just(f);
            }
        }).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasDropped(1).hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueInternalErrorStopStrategy() {
        Flux<Integer> test = Flux.just(0, 1).hide().concatMap(( f) -> Flux.range(f, 1).map(( i) -> 1 / i).onErrorStop()).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(1).expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueInternalErrorStopStrategyAsync() {
        Flux<Integer> test = Flux.just(0, 1).hide().concatMap(( f) -> Flux.range(f, 1).publishOn(Schedulers.parallel()).map(( i) -> 1 / i).onErrorStop()).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(1).expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueInternalErrorMono() {
        Flux<Integer> test = Flux.just(0, 1).hide().concatMap(( f) -> Mono.just(f).map(( i) -> 1 / i)).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(1).expectComplete().verifyThenAssertThat().hasDropped(0).hasDroppedErrors(1);
    }

    @Test
    public void errorModeContinueInternalErrorMonoAsync() {
        Flux<Integer> test = Flux.just(0, 1).hide().concatMap(( f) -> Mono.just(f).publishOn(Schedulers.parallel()).map(( i) -> 1 / i)).onErrorContinue(OnNextFailureStrategyTest::drop);
        StepVerifier.create(test).expectNoFusionSupport().expectNext(1).expectComplete().verifyThenAssertThat().hasDropped(0).hasDroppedErrors(1);
    }

    @Test
    public void discardOnNextQueueReject() {
        List<Object> discarded = new ArrayList<>();
        AssertSubscriber<Object> discardSubscriber = new AssertSubscriber(Context.of(KEY_ON_DISCARD, ((Consumer<?>) (discarded::add))));
        final CoreSubscriber<Object> subscriber = FluxConcatMap.subscriber(discardSubscriber, Mono::just, Queues.get(0), 1, IMMEDIATE);
        subscriber.onSubscribe(Operators.emptySubscription());
        subscriber.onNext(1);
        assertThat(discarded).containsExactly(1);
    }

    @Test
    public void discardOnError() {
        // also tests WeakScalar
        StepVerifier.create(Flux.just(1, 2, 3).concatWith(Mono.error(new IllegalStateException("boom"))).concatMap(( i) -> Mono.just(("value" + i))), 0).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly("value1", 2, 3);// "value1" comes from error cancelling the only inner in flight, the 2 other values are still raw in the queue

    }

    @Test
    public void discardOnCancel() {
        StepVerifier.create(Flux.just(1, 2, 3).concatMap(( i) -> Mono.just(("value" + i)), 1), 0).thenCancel().verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardOnDrainMapperError() {
        StepVerifier.create(Flux.just(1, 2, 3).concatMap(( i) -> {
            throw new IllegalStateException("boom");
        })).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardDelayedOnNextQueueReject() {
        List<Object> discarded = new ArrayList<>();
        AssertSubscriber<Object> testSubscriber = new AssertSubscriber(Context.of(KEY_ON_DISCARD, ((Consumer<?>) (discarded::add))));
        final CoreSubscriber<Object> subscriber = FluxConcatMap.subscriber(testSubscriber, Mono::just, Queues.get(0), 1, END);
        subscriber.onSubscribe(Operators.emptySubscription());
        subscriber.onNext(1);
        assertThat(discarded).containsExactly(1);
    }

    @Test
    public void discardDelayedOnCancel() {
        StepVerifier.create(Flux.just(1, 2, 3).concatMapDelayError(( i) -> Mono.just(("value" + i)), 1), 0).thenCancel().verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardDelayedOnDrainMapperError() {
        StepVerifier.create(Flux.just(1, 2, 3).concatMapDelayError(( i) -> {
            throw new IllegalStateException("boom");
        })).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }
}
