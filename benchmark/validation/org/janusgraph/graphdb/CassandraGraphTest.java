/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb;


import AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY;
import AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY;
import GraphDatabaseConfiguration.IDS_STORE_NAME;
import GraphDatabaseConfiguration.INITIAL_JANUSGRAPH_VERSION;
import GraphDatabaseConfiguration.INITIAL_STORAGE_VERSION;
import GraphDatabaseConfiguration.TITAN_COMPATIBLE_VERSIONS;
import JanusGraphConstants.STORAGE_VERSION;
import JanusGraphConstants.TITAN_ID_STORE_NAME;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.diskstorage.cassandra.AbstractCassandraStoreManager;
import org.janusgraph.diskstorage.configuration.ConfigElement;
import org.janusgraph.diskstorage.configuration.WriteConfiguration;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.transaction.StandardJanusGraphTx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public abstract class CassandraGraphTest extends JanusGraphTest {
    @Test
    public void testHasTTL() {
        Assertions.assertTrue(features.hasCellTTL());
    }

    @Test
    public void testStorageVerisonSet() {
        close();
        WriteConfiguration wc = getConfiguration();
        Assertions.assertNull(wc.get(ConfigElement.getPath(INITIAL_STORAGE_VERSION), INITIAL_STORAGE_VERSION.getDatatype()));
        wc.set(ConfigElement.getPath(INITIAL_STORAGE_VERSION), STORAGE_VERSION);
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
        mgmt = graph.openManagement();
        Assertions.assertEquals(STORAGE_VERSION, mgmt.get("graph.storage-version"));
        mgmt.rollback();
    }

    @Test
    public void testGraphConfigUsedByThreadBoundTx() {
        close();
        WriteConfiguration wc = getConfiguration();
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY), "ALL");
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY), "LOCAL_QUORUM");
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
        StandardJanusGraphTx tx = ((StandardJanusGraphTx) (graph.getCurrentThreadTx()));
        Assertions.assertEquals("ALL", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_READ_CONSISTENCY));
        Assertions.assertEquals("LOCAL_QUORUM", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_WRITE_CONSISTENCY));
    }

    @Test
    public void testGraphConfigUsedByTx() {
        close();
        WriteConfiguration wc = getConfiguration();
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY), "TWO");
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY), "THREE");
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
        StandardJanusGraphTx tx = ((StandardJanusGraphTx) (graph.newTransaction()));
        Assertions.assertEquals("TWO", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_READ_CONSISTENCY));
        Assertions.assertEquals("THREE", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_WRITE_CONSISTENCY));
        tx.rollback();
    }

    @Test
    public void testCustomConfigUsedByTx() {
        close();
        WriteConfiguration wc = getConfiguration();
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY), "ALL");
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY), "ALL");
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
        StandardJanusGraphTx tx = ((StandardJanusGraphTx) (graph.buildTransaction().customOption(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY), "ONE").customOption(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY), "TWO").start()));
        Assertions.assertEquals("ONE", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_READ_CONSISTENCY));
        Assertions.assertEquals("TWO", tx.getTxHandle().getBaseTransactionConfig().getCustomOptions().get(CASSANDRA_WRITE_CONSISTENCY));
        tx.rollback();
    }

    @Test
    public void testTitanGraphBackwardCompatibility() {
        close();
        WriteConfiguration wc = getConfiguration();
        wc.set(ConfigElement.getPath(AbstractCassandraStoreManager.CASSANDRA_KEYSPACE), "titan");
        wc.set(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), "x.x.x");
        Assertions.assertNull(wc.get(ConfigElement.getPath(INITIAL_JANUSGRAPH_VERSION), INITIAL_JANUSGRAPH_VERSION.getDatatype()));
        Assertions.assertFalse(JanusGraphConstants.TITAN_COMPATIBLE_VERSIONS.contains(wc.get(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), TITAN_COMPATIBLE_VERSIONS.getDatatype())));
        wc.set(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), "1.0.0");
        Assertions.assertTrue(JanusGraphConstants.TITAN_COMPATIBLE_VERSIONS.contains(wc.get(ConfigElement.getPath(TITAN_COMPATIBLE_VERSIONS), TITAN_COMPATIBLE_VERSIONS.getDatatype())));
        wc.set(ConfigElement.getPath(IDS_STORE_NAME), TITAN_ID_STORE_NAME);
        Assertions.assertTrue(TITAN_ID_STORE_NAME.equals(wc.get(ConfigElement.getPath(IDS_STORE_NAME), IDS_STORE_NAME.getDatatype())));
        graph = ((StandardJanusGraph) (JanusGraphFactory.open(wc)));
    }
}

