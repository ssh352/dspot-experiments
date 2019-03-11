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
package org.janusgraph.diskstorage.cassandra;


import StandardBaseTransactionConfig.Builder;
import TimestampProviders.MICRO;
import TimestampProviders.MILLI;
import TimestampProviders.NANO;
import com.google.common.base.Preconditions;
import org.janusgraph.diskstorage.BaseTransactionConfig;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.util.StandardBaseTransactionConfig;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
            Assertions.assertEquals(writeLevel, ct.getWriteConsistencyLevel());
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
            Assertions.assertEquals(writeLevel, ct.getReadConsistencyLevel());
            levelsChecked++;
        }
        // Sanity check: if CLevel.values was empty, something is wrong with the test
        Preconditions.checkState((0 < levelsChecked));
    }

    @Test
    public void testTimestampProvider() {
        BaseTransactionConfig baseTransactionConfig = StandardBaseTransactionConfig.of(NANO);
        CassandraTransaction ct = new CassandraTransaction(baseTransactionConfig);
        Assertions.assertEquals(NANO, ct.getConfiguration().getTimestampProvider());
        baseTransactionConfig = StandardBaseTransactionConfig.of(MICRO);
        ct = new CassandraTransaction(baseTransactionConfig);
        Assertions.assertEquals(MICRO, ct.getConfiguration().getTimestampProvider());
        baseTransactionConfig = StandardBaseTransactionConfig.of(MILLI);
        ct = new CassandraTransaction(baseTransactionConfig);
        Assertions.assertEquals(MILLI, ct.getConfiguration().getTimestampProvider());
    }
}

