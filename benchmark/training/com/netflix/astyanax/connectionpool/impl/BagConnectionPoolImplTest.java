package com.netflix.astyanax.connectionpool.impl;


import TestHostType.CONNECT_WITH_UNCHECKED_EXCEPTION;
import TestHostType.GOOD_IMMEDIATE;
import TestHostType.OPERATION_TIMEOUT;
import com.netflix.astyanax.connectionpool.ConnectionContext;
import com.netflix.astyanax.connectionpool.ConnectionPool;
import com.netflix.astyanax.connectionpool.Operation;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.OperationException;
import com.netflix.astyanax.retry.RunOnce;
import com.netflix.astyanax.test.TestClient;
import com.netflix.astyanax.test.TestConstants;
import com.netflix.astyanax.test.TestOperation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BagConnectionPoolImplTest extends BaseConnectionPoolTest {
    private static Logger LOG = LoggerFactory.getLogger(BagConnectionPoolImplTest.class);

    private static Operation<TestClient, String> dummyOperation = new TestOperation();

    @Test
    public void testUncheckedException() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.initialize();
        ConnectionPool<TestClient> pool = new BagOfConnectionsConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        pool.addHost(new com.netflix.astyanax.connectionpool.Host("127.0.0.1", GOOD_IMMEDIATE.ordinal()), true);
        OperationResult<String> result;
        try {
            result = pool.executeWithFailover(new TestOperation() {
                @Override
                public String execute(TestClient client, ConnectionContext context) throws ConnectionException, OperationException {
                    throw new RuntimeException("Unkecked Exception");
                }
            }, RunOnce.get());
            BagConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
            BagConnectionPoolImplTest.LOG.info(e.getMessage());
        }
        Assert.assertEquals(monitor.getConnectionClosedCount(), 1);
    }

    @Test
    public void testUncheckedExceptionInOpen() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.setInitConnsPerHost(0);
        config.initialize();
        ConnectionPool<TestClient> pool = new BagOfConnectionsConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        pool.addHost(new com.netflix.astyanax.connectionpool.Host("127.0.0.1", CONNECT_WITH_UNCHECKED_EXCEPTION.ordinal()), true);
        OperationResult<String> result;
        try {
            result = pool.executeWithFailover(BagConnectionPoolImplTest.dummyOperation, RunOnce.get());
            BagConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
            BagConnectionPoolImplTest.LOG.info(e.getMessage());
        }
        think(1000);
        try {
            result = pool.executeWithFailover(BagConnectionPoolImplTest.dummyOperation, RunOnce.get());
            BagConnectionPoolImplTest.LOG.info(pool.toString());
            Assert.fail();
        } catch (ConnectionException e) {
            BagConnectionPoolImplTest.LOG.info(e.getMessage());
        }
        think(1000);
        Assert.assertEquals(monitor.getConnectionClosedCount(), 0);
    }

    @Test
    public void testOperationTimeout() {
        CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
        ConnectionPoolConfigurationImpl config = new ConnectionPoolConfigurationImpl((((TestConstants.CLUSTER_NAME) + "_") + (TestConstants.KEYSPACE_NAME)));
        config.setInitConnsPerHost(0);
        config.initialize();
        ConnectionPool<TestClient> pool = new BagOfConnectionsConnectionPoolImpl<TestClient>(config, new com.netflix.astyanax.test.TestConnectionFactory(config, monitor), monitor);
        pool.addHost(new com.netflix.astyanax.connectionpool.Host("127.0.0.1", OPERATION_TIMEOUT.ordinal()), true);
        pool.addHost(new com.netflix.astyanax.connectionpool.Host("127.0.0.2", OPERATION_TIMEOUT.ordinal()), true);
        for (int i = 0; i < 5; i++) {
            OperationResult<String> result;
            try {
                result = pool.executeWithFailover(BagConnectionPoolImplTest.dummyOperation, RunOnce.get());
                BagConnectionPoolImplTest.LOG.info(pool.toString());
                Assert.fail();
            } catch (ConnectionException e) {
                BagConnectionPoolImplTest.LOG.info(e.getMessage());
            }
        }
        Assert.assertEquals(15, monitor.getConnectionCreatedCount());
        Assert.assertEquals(15, monitor.getConnectionClosedCount());
    }
}

