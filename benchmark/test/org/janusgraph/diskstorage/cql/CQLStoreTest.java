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
package org.janusgraph.diskstorage.cql;


import java.util.Collections;
import java.util.Map;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.KeyColumnValueStoreTest;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.keycolumnvalue.StoreFeatures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ExtendWith(MockitoExtension.class)
public class CQLStoreTest extends KeyColumnValueStoreTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CQLStoreTest.class);

    private static final String TEST_CF_NAME = "testcf";

    private static final String DEFAULT_COMPRESSOR_PACKAGE = "org.apache.cassandra.io.compress";

    private static final String TEST_KEYSPACE_NAME = "test_keyspace";

    public CQLStoreTest() throws BackendException {
    }

    @Test
    public void testDefaultCompactStorage() throws BackendException {
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_defaultcompact";
        final CQLStoreManager cqlStoreManager = openStorageManager();
        cqlStoreManager.openDatabase(cf);
        // COMPACT STORAGE is allowed on Cassandra 2 or earlier
        // when COMPACT STORAGE is allowed, the default is to enable it
        Assertions.assertTrue(((cqlStoreManager.isCompactStorageAllowed()) == (cqlStoreManager.getTableMetadata(cf).getOptions().isCompactStorage())));
    }

    @Test
    public void testUseCompactStorage() throws BackendException {
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_usecompact";
        final ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPACT_STORAGE, true);
        final CQLStoreManager cqlStoreManager = openStorageManager(config);
        cqlStoreManager.openDatabase(cf);
        Assertions.assertTrue(cqlStoreManager.getTableMetadata(cf).getOptions().isCompactStorage());
    }

    @Test
    public void testNoCompactStorage() throws BackendException {
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_nocompact";
        final ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPACT_STORAGE, false);
        final CQLStoreManager cqlStoreManager = openStorageManager(config);
        cqlStoreManager.openDatabase(cf);
        Assertions.assertFalse(cqlStoreManager.getTableMetadata(cf).getOptions().isCompactStorage());
    }

    @Test
    public void testDefaultCFCompressor() throws BackendException {
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_snappy";
        final CQLStoreManager cqlStoreManager = openStorageManager();
        cqlStoreManager.openDatabase(cf);
        final Map<String, String> opts = cqlStoreManager.getCompressionOptions(cf);
        Assertions.assertEquals(2, opts.size());
        // chunk length key differs between 2.x (chunk_length_kb) and 3.x (chunk_length_in_kb)
        Assertions.assertEquals("64", opts.getOrDefault("chunk_length_kb", opts.get("chunk_length_in_kb")));
        // compression class key differs between 2.x (sstable_compression) and 3.x (class)
        Assertions.assertEquals((((CQLStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + (CF_COMPRESSION_TYPE.getDefaultValue())), opts.getOrDefault("sstable_compression", opts.get("class")));
    }

    @Test
    public void testCustomCFCompressor() throws BackendException {
        final String cname = "DeflateCompressor";
        final int ckb = 128;
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_gzip";
        final ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION_TYPE, cname);
        config.set(CF_COMPRESSION_BLOCK_SIZE, ckb);
        final CQLStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        final Map<String, String> opts = mgr.getCompressionOptions(cf);
        Assertions.assertEquals(2, opts.size());
        // chunk length key differs between 2.x (chunk_length_kb) and 3.x (chunk_length_in_kb)
        Assertions.assertEquals(String.valueOf(ckb), opts.getOrDefault("chunk_length_kb", opts.get("chunk_length_in_kb")));
        // compression class key differs between 2.x (sstable_compression) and 3.x (class)
        Assertions.assertEquals((((CQLStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + cname), opts.getOrDefault("sstable_compression", opts.get("class")));
    }

    @Test
    public void testDisableCFCompressor() throws BackendException {
        final String cf = (CQLStoreTest.TEST_CF_NAME) + "_nocompress";
        final ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION, false);
        final CQLStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        final Map<String, String> opts = new java.util.HashMap(mgr.getCompressionOptions(cf));
        if ("false".equals(opts.get("enabled"))) {
            // Cassandra 3.x contains {"enabled": false"} mapping not found in 2.x
            opts.remove("enabled");
        }
        Assertions.assertEquals(Collections.emptyMap(), opts);
    }

    @Test
    public void testTTLSupported() {
        final StoreFeatures features = this.manager.getFeatures();
        Assertions.assertTrue(features.hasCellTTL());
    }

    @Mock
    private Cluster cluster;

    @Mock
    private Session session;

    @InjectMocks
    private CQLStoreManager mockManager = new CQLStoreManager(getBaseStorageConfiguration());

    @Test
    public void testExistKeyspaceSession() {
        Metadata metadata = Mockito.mock(Metadata.class);
        KeyspaceMetadata keyspaceMetadata = Mockito.mock(KeyspaceMetadata.class);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getKeyspace(CQLStoreTest.TEST_KEYSPACE_NAME)).thenReturn(keyspaceMetadata);
        Mockito.when(cluster.connect()).thenReturn(session);
        mockManager.initializeSession(CQLStoreTest.TEST_KEYSPACE_NAME);
        Mockito.verify(cluster).connect();
        Mockito.verify(session, Mockito.never()).execute(ArgumentMatchers.any(Statement.class));
    }

    @Test
    public void testNewKeyspaceSession() {
        Metadata metadata = Mockito.mock(Metadata.class);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getKeyspace(CQLStoreTest.TEST_KEYSPACE_NAME)).thenReturn(null);
        Mockito.when(cluster.connect()).thenReturn(session);
        mockManager.initializeSession(CQLStoreTest.TEST_KEYSPACE_NAME);
        Mockito.verify(cluster).connect();
        Mockito.verify(session, Mockito.times(1)).execute(ArgumentMatchers.any(Statement.class));
    }

    @Test
    public void testExistTableOpenDatabase() throws BackendException {
        // arrange
        String someTableName = "foo";
        Metadata metadata = Mockito.mock(Metadata.class);
        KeyspaceMetadata keyspaceMetadata = Mockito.mock(KeyspaceMetadata.class);
        Mockito.when(keyspaceMetadata.getTable(someTableName)).thenReturn(Mockito.mock(TableMetadata.class));
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getKeyspace(mockManager.getKeyspaceName())).thenReturn(keyspaceMetadata);
        // act
        mockManager.openDatabase(someTableName, null);
        // assert
        Mockito.verify(session, Mockito.never()).execute(ArgumentMatchers.any(Statement.class));
    }

    @Test
    public void testNewTableOpenDatabase() throws BackendException {
        // arrange
        String someTableName = "foo";
        Metadata metadata = Mockito.mock(Metadata.class);
        KeyspaceMetadata keyspaceMetadata = Mockito.mock(KeyspaceMetadata.class);
        Mockito.when(keyspaceMetadata.getTable(someTableName)).thenReturn(null);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getKeyspace(mockManager.getKeyspaceName())).thenReturn(keyspaceMetadata);
        // act
        mockManager.openDatabase(someTableName, null);
        // assert
        Mockito.verify(session, Mockito.times(1)).execute(ArgumentMatchers.any(Statement.class));
    }
}

