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
package org.apache.flink.runtime.checkpoint;


import JobStatus.FINISHED;
import JobStatus.SUSPENDED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.runtime.zookeeper.ZooKeeperTestEnvironment;
import org.apache.flink.util.TestLogger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public abstract class CheckpointIDCounterTest extends TestLogger {
    public static class StandaloneCheckpointIDCounterTest extends CheckpointIDCounterTest {
        @Override
        protected CheckpointIDCounter createCompletedCheckpoints() throws Exception {
            return new StandaloneCheckpointIDCounter();
        }
    }

    public static class ZooKeeperCheckpointIDCounterITCase extends CheckpointIDCounterTest {
        private static final ZooKeeperTestEnvironment ZooKeeper = new ZooKeeperTestEnvironment(1);

        @AfterClass
        public static void tearDown() throws Exception {
            CheckpointIDCounterTest.ZooKeeperCheckpointIDCounterITCase.ZooKeeper.shutdown();
        }

        @Before
        public void cleanUp() throws Exception {
            CheckpointIDCounterTest.ZooKeeperCheckpointIDCounterITCase.ZooKeeper.deleteAll();
        }

        /**
         * Tests that counter node is removed from ZooKeeper after shutdown.
         */
        @Test
        public void testShutdownRemovesState() throws Exception {
            CheckpointIDCounter counter = createCompletedCheckpoints();
            counter.start();
            CuratorFramework client = CheckpointIDCounterTest.ZooKeeperCheckpointIDCounterITCase.ZooKeeper.getClient();
            Assert.assertNotNull(client.checkExists().forPath("/checkpoint-id-counter"));
            counter.shutdown(FINISHED);
            Assert.assertNull(client.checkExists().forPath("/checkpoint-id-counter"));
        }

        /**
         * Tests that counter node is NOT removed from ZooKeeper after suspend.
         */
        @Test
        public void testSuspendKeepsState() throws Exception {
            CheckpointIDCounter counter = createCompletedCheckpoints();
            counter.start();
            CuratorFramework client = CheckpointIDCounterTest.ZooKeeperCheckpointIDCounterITCase.ZooKeeper.getClient();
            Assert.assertNotNull(client.checkExists().forPath("/checkpoint-id-counter"));
            counter.shutdown(SUSPENDED);
            Assert.assertNotNull(client.checkExists().forPath("/checkpoint-id-counter"));
        }

        @Override
        protected CheckpointIDCounter createCompletedCheckpoints() throws Exception {
            return new ZooKeeperCheckpointIDCounter(CheckpointIDCounterTest.ZooKeeperCheckpointIDCounterITCase.ZooKeeper.getClient(), "/checkpoint-id-counter");
        }
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Tests serial increment and get calls.
     */
    @Test
    public void testSerialIncrementAndGet() throws Exception {
        final CheckpointIDCounter counter = createCompletedCheckpoints();
        try {
            counter.start();
            Assert.assertEquals(1, counter.getAndIncrement());
            Assert.assertEquals(2, counter.getAndIncrement());
            Assert.assertEquals(3, counter.getAndIncrement());
            Assert.assertEquals(4, counter.getAndIncrement());
        } finally {
            counter.shutdown(FINISHED);
        }
    }

    /**
     * Tests concurrent increment and get calls from multiple Threads and verifies that the numbers
     * counts strictly increasing.
     */
    @Test
    public void testConcurrentGetAndIncrement() throws Exception {
        // Config
        final int numThreads = 8;
        // Setup
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CheckpointIDCounter counter = createCompletedCheckpoints();
        counter.start();
        ExecutorService executor = null;
        try {
            executor = Executors.newFixedThreadPool(numThreads);
            List<Future<List<Long>>> resultFutures = new ArrayList<>(numThreads);
            for (int i = 0; i < numThreads; i++) {
                resultFutures.add(executor.submit(new CheckpointIDCounterTest.Incrementer(startLatch, counter)));
            }
            // Kick off the incrementing
            startLatch.countDown();
            final int expectedTotal = numThreads * (CheckpointIDCounterTest.Incrementer.NumIncrements);
            List<Long> all = new ArrayList<>(expectedTotal);
            // Get the counts
            for (Future<List<Long>> result : resultFutures) {
                List<Long> counts = result.get();
                for (long val : counts) {
                    all.add(val);
                }
            }
            // Verify
            Collections.sort(all);
            Assert.assertEquals(expectedTotal, all.size());
            long current = 0;
            for (long val : all) {
                // Incrementing counts
                Assert.assertEquals((++current), val);
            }
            // The final count
            Assert.assertEquals((expectedTotal + 1), counter.getAndIncrement());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
            counter.shutdown(FINISHED);
        }
    }

    /**
     * Tests a simple {@link CheckpointIDCounter#setCount(long)} operation.
     */
    @Test
    public void testSetCount() throws Exception {
        final CheckpointIDCounter counter = createCompletedCheckpoints();
        counter.start();
        // Test setCount
        counter.setCount(1337);
        Assert.assertEquals(1337, counter.getAndIncrement());
        Assert.assertEquals(1338, counter.getAndIncrement());
        counter.shutdown(FINISHED);
    }

    /**
     * Task repeatedly incrementing the {@link CheckpointIDCounter}.
     */
    private static class Incrementer implements Callable<List<Long>> {
        /**
         * Total number of {@link CheckpointIDCounter#getAndIncrement()} calls.
         */
        private static final int NumIncrements = 128;

        private final CountDownLatch startLatch;

        private final CheckpointIDCounter counter;

        public Incrementer(CountDownLatch startLatch, CheckpointIDCounter counter) {
            this.startLatch = startLatch;
            this.counter = counter;
        }

        @Override
        public List<Long> call() throws Exception {
            final Random rand = new Random();
            final List<Long> counts = new ArrayList<>();
            // Wait for the main thread to kick off execution
            this.startLatch.await();
            for (int i = 0; i < (CheckpointIDCounterTest.Incrementer.NumIncrements); i++) {
                counts.add(counter.getAndIncrement());
                // To get some "random" interleaving ;)
                Thread.sleep(rand.nextInt(20));
            }
            return counts;
        }
    }
}

