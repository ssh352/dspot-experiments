/**
 * *****************************************************************************
 * Copyright 2011 Netflix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package com.netflix.astyanax.connectionpool.impl;


import TestHostType.CONNECT_WITH_UNCHECKED_EXCEPTION;
import TestHostType.GOOD_IMMEDIATE;
import com.netflix.astyanax.connectionpool.ConnectionContext;
import com.netflix.astyanax.connectionpool.ConnectionPool;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.HostConnectionPool;
import com.netflix.astyanax.connectionpool.Operation;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NoAvailableHostsException;
import com.netflix.astyanax.connectionpool.exceptions.OperationException;
import com.netflix.astyanax.retry.RunOnce;
import com.netflix.astyanax.test.TestClient;
import com.netflix.astyanax.test.TestConstants;
import com.netflix.astyanax.test.TestOperation;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class RoundRobinConnectionPoolImplTest extends BaseConnectionPoolTest {
    private static Logger LOG = Logger.getLogger(RoundRobinConnectionPoolImplTest.class);

    private static Operation<TestClient, String> dummyOperation = new TestOperation();

    @Test
    public void testUncheckedException() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.initialize();
        ConnectionPool<TestClient> pool = new RoundRobinConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        pool.addHost(new Host("127.0.0.1", GOOD_IMMEDIATE.ordinal()), true);
        OperationResult<String> result;
        try {
            result = pool.executeWithFailover(new TestOperation() {
                @Override
                public String execute(TestClient client, ConnectionContext context) throws ConnectionException, OperationException {
                    throw new RuntimeException("Unkecked Exception");
                }
            }, RunOnce.get());
            RoundRobinConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
        }
        Assert.assertEquals(monitor.getConnectionClosedCount(), 1);
    }

    @Test
    public void testUncheckedExceptionInOpen() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.initialize();
        config.setInitConnsPerHost(0);
        ConnectionPool<TestClient> pool = new RoundRobinConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        pool.addHost(new Host("127.0.0.1", CONNECT_WITH_UNCHECKED_EXCEPTION.ordinal()), true);
        OperationResult<String> result;
        try {
            result = pool.executeWithFailover(RoundRobinConnectionPoolImplTest.dummyOperation, RunOnce.get());
            RoundRobinConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
            RoundRobinConnectionPoolImplTest.LOG.info(e.getMessage());
        }
        think(1000);
        try {
            result = pool.executeWithFailover(RoundRobinConnectionPoolImplTest.dummyOperation, RunOnce.get());
            RoundRobinConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
            RoundRobinConnectionPoolImplTest.LOG.info(e.getMessage());
        }
        think(1000);
        Assert.assertEquals(monitor.getConnectionClosedCount(), 0);
    }

    @Test
    public void testHostDown() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.setRetryBackoffStrategy(new FixedRetryBackoffStrategy(200, 2000));
        config.setMaxConnsPerHost(3);
        config.setMaxPendingConnectionsPerHost(2);
        config.initialize();
        ConnectionPool<TestClient> cp = new RoundRobinConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        Host host = new Host("127.0.0.1", GOOD_IMMEDIATE.ordinal());
        cp.addHost(host, true);
        OperationResult<String> result;
        try {
            result = cp.executeWithFailover(new TestOperation(), RunOnce.get());
        } catch (ConnectionException e) {
            RoundRobinConnectionPoolImplTest.LOG.error(e.getMessage());
            Assert.fail();
        }
        HostConnectionPool<TestClient> pool = cp.getHostPool(host);
        Assert.assertNotNull(pool);
        pool.markAsDown(null);
        try {
            result = cp.executeWithFailover(new TestOperation(), RunOnce.get());
            Assert.fail();
        } catch (NoAvailableHostsException e) {
        } catch (ConnectionException e) {
            RoundRobinConnectionPoolImplTest.LOG.info(e);
            Assert.fail();
        }
        this.think(1000);
        try {
            result = cp.executeWithFailover(new TestOperation(), RunOnce.get());
        } catch (ConnectionException e) {
            RoundRobinConnectionPoolImplTest.LOG.error(e.getMessage());
            Assert.fail();
        }
    }
}

