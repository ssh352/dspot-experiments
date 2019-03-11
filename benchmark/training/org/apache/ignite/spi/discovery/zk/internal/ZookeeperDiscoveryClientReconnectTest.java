/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.spi.discovery.zk.internal;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for Zookeeper SPI discovery client reconnect.
 */
public class ZookeeperDiscoveryClientReconnectTest extends ZookeeperDiscoverySpiTestBase {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReconnectServersRestart_1() throws Exception {
        reconnectServersRestart(1);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReconnectServersRestart_2() throws Exception {
        reconnectServersRestart(3);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReconnectServersRestart_3() throws Exception {
        startGrid(0);
        helper.clientMode(true);
        startGridsMultiThreaded(5, 5);
        stopGrid(getTestIgniteInstanceName(0), true, false);
        final int srvIdx = ThreadLocalRandom.current().nextInt(5);
        final AtomicInteger idx = new AtomicInteger();
        info("Restart nodes.");
        // Test concurrent start when there are disconnected nodes from previous cluster.
        GridTestUtils.runMultiThreaded(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                int threadIdx = idx.getAndIncrement();
                helper.clientModeThreadLocal(((threadIdx == srvIdx) || (ThreadLocalRandom.current().nextBoolean())));
                startGrid(threadIdx);
                return null;
            }
        }, 5, "start-node");
        waitForTopology(10);
        ZookeeperDiscoverySpiTestBase.evts.clear();
    }

    /**
     * Checks that a client will reconnect after previous cluster data was cleaned.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReconnectServersRestart_4() throws Exception {
        startGrid(0);
        helper.clientMode(true);
        IgniteEx client = startGrid(1);
        helper.clientMode(false);
        CountDownLatch latch = new CountDownLatch(1);
        client.events().localListen(( event) -> {
            latch.countDown();
            return true;
        }, EVT_CLIENT_NODE_DISCONNECTED);
        waitForTopology(2);
        stopGrid(0);
        ZookeeperDiscoverySpiTestBase.evts.clear();
        // Waiting for client starts to reconnect and create join request.
        assertTrue("Failed to wait for client node disconnected.", latch.await(15, TimeUnit.SECONDS));
        // Restart cluster twice for incrementing internal order. (alive zk-nodes having lower order and containing
        // client join request will be removed). See {@link ZookeeperDiscoveryImpl#cleanupPreviousClusterData}.
        startGrid(0);
        stopGrid(0);
        ZookeeperDiscoverySpiTestBase.evts.clear();
        startGrid(0);
        waitForTopology(2);
    }
}

