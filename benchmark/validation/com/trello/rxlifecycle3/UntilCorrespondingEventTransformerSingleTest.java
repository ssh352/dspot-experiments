/**
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
package com.trello.rxlifecycle3;


import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class UntilCorrespondingEventTransformerSingleTest {
    PublishSubject<String> lifecycle;

    TestScheduler testScheduler;// Since Single is not backpressure aware, use this to simulate it taking time


    @Test
    public void noEvents() {
        TestObserver<String> testObserver = Single.just("1").delay(1, TimeUnit.MILLISECONDS, testScheduler).compose(RxLifecycle.<String, String>bind(lifecycle, UntilCorrespondingEventTransformerSingleTest.CORRESPONDING_EVENTS)).test();
        testObserver.assertNoValues();
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<String> testObserver = Single.just("1").delay(1, TimeUnit.MILLISECONDS, testScheduler).compose(RxLifecycle.<String, String>bind(lifecycle, UntilCorrespondingEventTransformerSingleTest.CORRESPONDING_EVENTS)).test();
        testObserver.assertNoValues();
        lifecycle.onNext("create");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<String> testObserver = Single.just("1").delay(1, TimeUnit.MILLISECONDS, testScheduler).compose(RxLifecycle.<String, String>bind(lifecycle, UntilCorrespondingEventTransformerSingleTest.CORRESPONDING_EVENTS)).test();
        testObserver.assertNoValues();
        lifecycle.onNext("create");
        lifecycle.onNext("start");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<String> testObserver = Single.just("1").delay(1, TimeUnit.MILLISECONDS, testScheduler).compose(RxLifecycle.<String, String>bind(lifecycle, UntilCorrespondingEventTransformerSingleTest.CORRESPONDING_EVENTS)).test();
        lifecycle.onNext("create");
        testObserver.assertNoErrors();
        lifecycle.onNext("destroy");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertNoValues();
        testObserver.assertError(CancellationException.class);
    }

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("create")) {
                return "destroy";
            }
            throw new IllegalArgumentException(("Cannot handle: " + s));
        }
    };
}

