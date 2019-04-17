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


import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.subscriber.AssertSubscriber;


public class MonoAllTest {
    @Test(expected = NullPointerException.class)
    public void sourceNull() {
        new MonoAll(null, ( v) -> true);
    }

    @Test(expected = NullPointerException.class)
    public void predicateNull() {
        new MonoAll(null, null);
    }

    @Test
    public void normal() {
        AssertSubscriber<Boolean> ts = AssertSubscriber.create();
        Flux.range(1, 10).all(( v) -> true).subscribe(ts);
        ts.assertValues(true).assertComplete().assertNoError();
    }

    @Test
    public void normalBackpressured() {
        AssertSubscriber<Boolean> ts = AssertSubscriber.create(0);
        Flux.range(1, 10).all(( v) -> true).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertNoError();
        ts.request(1);
        ts.assertValues(true).assertComplete().assertNoError();
    }

    @Test
    public void someMatch() {
        AssertSubscriber<Boolean> ts = AssertSubscriber.create();
        Flux.range(1, 10).all(( v) -> v < 6).subscribe(ts);
        ts.assertValues(false).assertComplete().assertNoError();
    }

    @Test
    public void someMatchBackpressured() {
        AssertSubscriber<Boolean> ts = AssertSubscriber.create(0);
        Flux.range(1, 10).all(( v) -> v < 6).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertNoError();
        ts.request(1);
        ts.assertValues(false).assertComplete().assertNoError();
    }

    @Test
    public void predicateThrows() {
        AssertSubscriber<Boolean> ts = AssertSubscriber.create();
        Flux.range(1, 10).all(( v) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertError(RuntimeException.class).assertErrorWith(( e) -> Assert.assertTrue(e.getMessage().contains("forced failure")));
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Boolean> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoAll.AllSubscriber<String> test = new MonoAll.AllSubscriber<>(actual, String::isEmpty);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}
