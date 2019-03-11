package com.thinkaurelius.titan.diskstorage.cassandra;


import StandardBaseTransactionConfig.Builder;
import TimestampProviders.MICRO;
import TimestampProviders.MILLI;
import TimestampProviders.NANO;
import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.diskstorage.BaseTransactionConfig;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.util.StandardBaseTransactionConfig;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class CassandraTransactionTest {
    /* testRead/WriteConsistencyLevel have unnecessary code duplication
    that could be avoided by creating a common helper method that takes
    a ConfigOption parameter and a function that converts a
    CassandraTransaction to a consistency level by calling either
    ct.getReadConsistencyLevel() or .getWriteConsistencyLevel(),
    but it doesn't seem worth the complexity.
     */
    @Test
    public void testWriteConsistencyLevel() {
        int levelsChecked = 0;
        // Test whether CassandraTransaction honors the write consistency level option
        for (CLevel writeLevel : CLevel.values()) {
            StandardBaseTransactionConfig.Builder b = new StandardBaseTransactionConfig.Builder();
            ModifiableConfiguration mc = GraphDatabaseConfiguration.buildGraphConfiguration();
            mc.set(AbstractCassandraStoreManager.CASSANDRA_WRITE_CONSISTENCY, writeLevel.name());
            b.customOptions(mc);
            b.timestampProvider(MICRO);
            CassandraTransaction ct = new CassandraTransaction(b.build());
            Assert.assertEquals(writeLevel, ct.getWriteConsistencyLevel());
            levelsChecked++;
        }
        // Sanity check: if CLevel.values was empty, something is wrong with the test
        Preconditions.checkState((0 < levelsChecked));
    }

    @Test
    public void testReadConsistencyLevel() {
        int levelsChecked = 0;
        // Test whether CassandraTransaction honors the write consistency level option
        for (CLevel writeLevel : CLevel.values()) {
            StandardBaseTransactionConfig.Builder b = new StandardBaseTransactionConfig.Builder();
            ModifiableConfiguration mc = GraphDatabaseConfiguration.buildGraphConfiguration();
            mc.set(AbstractCassandraStoreManager.CASSANDRA_READ_CONSISTENCY, writeLevel.name());
            b.timestampProvider(MICRO);
            b.customOptions(mc);
            CassandraTransaction ct = new CassandraTransaction(b.build());
            Assert.assertEquals(writeLevel, ct.getReadConsistencyLevel());
            levelsChecked++;
        }
        // Sanity check: if CLevel.values was empty, something is wrong with the test
        Preconditions.checkState((0 < levelsChecked));
    }

    @Test
    public void testTimestampProvider() {
        BaseTransactionConfig txcfg = StandardBaseTransactionConfig.of(NANO);
        CassandraTransaction ct = new CassandraTransaction(txcfg);
        Assert.assertEquals(NANO, ct.getConfiguration().getTimestampProvider());
        txcfg = StandardBaseTransactionConfig.of(MICRO);
        ct = new CassandraTransaction(txcfg);
        Assert.assertEquals(MICRO, ct.getConfiguration().getTimestampProvider());
        txcfg = StandardBaseTransactionConfig.of(MILLI);
        ct = new CassandraTransaction(txcfg);
        Assert.assertEquals(MILLI, ct.getConfiguration().getTimestampProvider());
    }
}

