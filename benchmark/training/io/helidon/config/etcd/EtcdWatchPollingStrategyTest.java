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
package io.helidon.config.etcd;


import io.helidon.common.reactive.Flow;
import io.helidon.config.etcd.EtcdConfigSourceBuilder.EtcdApi;
import io.helidon.config.etcd.EtcdConfigSourceBuilder.EtcdEndpoint;
import io.helidon.config.etcd.client.MockEtcdClient;
import io.helidon.config.etcd.internal.client.EtcdClient;
import io.helidon.config.etcd.internal.client.EtcdClientException;
import io.helidon.config.spi.PollingStrategy.PollingEvent;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link EtcdWatchPollingStrategy}.
 */
public class EtcdWatchPollingStrategyTest {
    private static final URI DEFAULT_URI = URI.create("http://localhost:2379");

    @Test
    public void testBasics() throws EtcdClientException, InterruptedException {
        MockEtcdClient etcdClient = new MockEtcdClient(EtcdWatchPollingStrategyTest.DEFAULT_URI);
        EtcdWatchPollingStrategy etcdWatchPollingStrategy = new EtcdWatchPollingStrategyTest.MockEtcdWatchPollingStrategy(new EtcdEndpoint(EtcdWatchPollingStrategyTest.DEFAULT_URI, "key", EtcdApi.v2), etcdClient);
        CountDownLatch initLatch = new CountDownLatch(1);
        CountDownLatch nextLatch = new CountDownLatch(3);
        etcdWatchPollingStrategy.ticks().subscribe(new Flow.Subscriber<PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
                initLatch.countDown();
            }

            @Override
            public void onNext(PollingEvent item) {
                nextLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        });
        MatcherAssert.assertThat(initLatch.await(1000, TimeUnit.MILLISECONDS), Is.is(true));
        etcdClient.put("key", "value1");
        etcdClient.put("key", "value2");
        etcdClient.put("key", "value3");
        MatcherAssert.assertThat(nextLatch.await(1000, TimeUnit.MILLISECONDS), Is.is(true));
    }

    @Test
    public void testSubscribeOnce() throws InterruptedException {
        MockEtcdClient etcdClient = new MockEtcdClient(EtcdWatchPollingStrategyTest.DEFAULT_URI);
        EtcdWatchPollingStrategy etcdWatchPollingStrategy = new EtcdWatchPollingStrategyTest.MockEtcdWatchPollingStrategy(new EtcdEndpoint(EtcdWatchPollingStrategyTest.DEFAULT_URI, "key", EtcdApi.v2), etcdClient);
        int count = 5;
        CountDownLatch initLatch = new CountDownLatch(5);
        for (int i = 0; i < count; i++) {
            etcdWatchPollingStrategy.ticks().subscribe(new Flow.Subscriber<PollingEvent>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                    initLatch.countDown();
                }

                @Override
                public void onNext(PollingEvent item) {
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onComplete() {
                }
            });
        }
        MatcherAssert.assertThat(initLatch.await(1000, TimeUnit.MILLISECONDS), Is.is(true));
        MatcherAssert.assertThat(etcdClient.watchPublisher("key").getNumberOfSubscribers(), Is.is(1));
    }

    private static class MockEtcdWatchPollingStrategy extends EtcdWatchPollingStrategy {
        private final MockEtcdClient etcdClient;

        MockEtcdWatchPollingStrategy(EtcdEndpoint etcdEndpoint, MockEtcdClient etcdClient) {
            super(etcdEndpoint);
            this.etcdClient = etcdClient;
        }

        @Override
        EtcdClient etcdClient() {
            return etcdClient;
        }
    }
}

