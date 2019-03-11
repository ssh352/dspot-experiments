/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config;


import io.helidon.common.reactive.Flow;
import io.helidon.config.spi.PollingStrategy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link PollingStrategies}.
 */
public class PollingStrategiesTest {
    @Test
    public void testTicksNop() throws InterruptedException {
        PollingStrategy pollingStrategy = PollingStrategies.nop();
        CountDownLatch onComplete = new CountDownLatch(1);
        pollingStrategy.ticks().subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                fail("onNext should not be invoked");
            }

            @Override
            public void onError(Throwable throwable) {
                fail("onError should not be invoked");
            }

            @Override
            public void onComplete() {
                onComplete.countDown();
            }
        });
        Assert.assertThat(onComplete.await(10, TimeUnit.MILLISECONDS), Matchers.is(true));
    }
}

