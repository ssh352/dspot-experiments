/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.util;


import com.twitter.util.Await;
import com.twitter.util.ExecutorServiceFuturePool;
import com.twitter.util.Future;
import com.twitter.util.FuturePool;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSafeQueueingFuturePool {
    static final Logger LOG = LoggerFactory.getLogger(TestSafeQueueingFuturePool.class);

    @Rule
    public TestName runtime = new TestName();

    class TestFuturePool<T> {
        final ScheduledExecutorService executor;

        final FuturePool pool;

        final SafeQueueingFuturePool<T> wrapper;

        TestFuturePool() {
            executor = Executors.newScheduledThreadPool(1);
            pool = new ExecutorServiceFuturePool(executor);
            wrapper = new SafeQueueingFuturePool<T>(pool);
        }

        public void shutdown() {
            executor.shutdown();
        }
    }

    @Test
    public void testSimpleSuccess() throws Exception {
        TestSafeQueueingFuturePool.TestFuturePool<Void> pool = new TestSafeQueueingFuturePool.TestFuturePool<Void>();
        final AtomicBoolean result = new AtomicBoolean(false);
        Future<Void> future = pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
            public Void apply() {
                result.set(true);
                return null;
            }
        });
        Await.result(future);
        Assert.assertTrue(result.get());
        pool.shutdown();
    }

    @Test
    public void testSimpleFailure() throws Exception {
        TestSafeQueueingFuturePool.TestFuturePool<Void> pool = new TestSafeQueueingFuturePool.TestFuturePool<Void>();
        Future<Void> future = pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
            public Void apply() {
                throw new RuntimeException("failed");
            }
        });
        try {
            Await.result(future);
            Assert.fail("should have thrown");
        } catch (Exception ex) {
        }
        pool.shutdown();
    }

    @Test
    public void testFailedDueToClosed() throws Exception {
        TestSafeQueueingFuturePool.TestFuturePool<Void> pool = new TestSafeQueueingFuturePool.TestFuturePool<Void>();
        pool.wrapper.close();
        Future<Void> future = pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
            public Void apply() {
                throw new RuntimeException("failed");
            }
        });
        try {
            Await.result(future);
            Assert.fail("should have thrown");
        } catch (RejectedExecutionException ex) {
        }
        pool.shutdown();
    }

    @Test
    public void testRejectedFailure() throws Exception {
        TestSafeQueueingFuturePool.TestFuturePool<Void> pool = new TestSafeQueueingFuturePool.TestFuturePool<Void>();
        final AtomicBoolean result = new AtomicBoolean(false);
        pool.executor.shutdown();
        final CountDownLatch latch = new CountDownLatch(1);
        Future<Void> future = pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
            public Void apply() {
                result.set(true);
                latch.countDown();
                return null;
            }
        });
        try {
            Await.result(future);
            Assert.fail("should have thrown");
        } catch (RejectedExecutionException ex) {
        }
        Assert.assertFalse(result.get());
        pool.wrapper.close();
        latch.await();
        Assert.assertTrue(result.get());
        pool.shutdown();
    }

    @Test
    public void testRejectedBackupFailure() throws Exception {
        TestSafeQueueingFuturePool.TestFuturePool<Void> pool = new TestSafeQueueingFuturePool.TestFuturePool<Void>();
        final AtomicBoolean result = new AtomicBoolean(false);
        pool.executor.shutdownNow();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        Future<Void> future1 = pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
            public Void apply() {
                try {
                    latch1.await();
                } catch (Exception ex) {
                }
                return null;
            }
        });
        // Enqueue a set of futures behind.
        final int blockedCount = 100;
        final ArrayList<Future<Void>> blockedFutures = new ArrayList<Future<Void>>(blockedCount);
        final int[] doneArray = new int[blockedCount];
        final AtomicInteger doneCount = new AtomicInteger(0);
        for (int i = 0; i < blockedCount; i++) {
            final int index = i;
            blockedFutures.add(pool.wrapper.apply(new com.twitter.util.Function0<Void>() {
                public Void apply() {
                    doneArray[index] = doneCount.getAndIncrement();
                    return null;
                }
            }));
        }
        // All the futures fail when the executor is force closed.
        latch1.countDown();
        pool.executor.shutdownNow();
        for (int i = 0; i < blockedCount; i++) {
            try {
                Await.result(blockedFutures.get(i));
                Assert.fail("should have thrown");
            } catch (RejectedExecutionException ex) {
            }
        }
        // None of them have completed.
        for (int i = 0; i < blockedCount; i++) {
            Assert.assertEquals(0, doneArray[i]);
        }
        // Close cleans up all pending ops in order.
        pool.wrapper.close();
        for (int i = 0; i < blockedCount; i++) {
            Assert.assertEquals(i, doneArray[i]);
        }
        pool.shutdown();
    }
}

