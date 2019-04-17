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
package org.apache.flink.queryablestate.network;


import PredefinedOptions.DEFAULT;
import RocksDBStateBackend.PriorityQueueStateType;
import VoidNamespaceSerializer.INSTANCE;
import java.util.Collections;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.contrib.streaming.state.RocksDBKeyedStateBackend;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.queryablestate.client.VoidNamespace;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.state.AbstractStateBackend;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.TestLocalRecoveryConfig;
import org.apache.flink.runtime.state.internal.InternalListState;
import org.apache.flink.runtime.state.internal.InternalMapState;
import org.apache.flink.runtime.state.ttl.TtlTimeProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.rocksdb.DBOptions;


/**
 * Additional tests for the serialization and deserialization using
 * the KvStateSerializer with a RocksDB state back-end.
 */
public final class KVStateRequestSerializerRocksDBTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests list serialization and deserialization match.
     *
     * @see KvStateRequestSerializerTest#testListSerialization()
    KvStateRequestSerializerTest#testListSerialization() using the heap state back-end
    test
     */
    @Test
    public void testListSerialization() throws Exception {
        final long key = 0L;
        // objects for RocksDB state list serialisation
        DBOptions dbOptions = DEFAULT.createDBOptions();
        dbOptions.setCreateIfMissing(true);
        ExecutionConfig executionConfig = new ExecutionConfig();
        final RocksDBKeyedStateBackend<Long> longHeapKeyedStateBackend = new org.apache.flink.contrib.streaming.state.RocksDBKeyedStateBackendBuilder("no-op", ClassLoader.getSystemClassLoader(), temporaryFolder.getRoot(), dbOptions, ( stateName) -> PredefinedOptions.DEFAULT.createColumnOptions(), Mockito.mock(TaskKvStateRegistry.class), LongSerializer.INSTANCE, 1, new KeyGroupRange(0, 0), executionConfig, TestLocalRecoveryConfig.disabled(), PriorityQueueStateType.HEAP, TtlTimeProvider.DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), AbstractStateBackend.getCompressionDecorator(executionConfig), new CloseableRegistry()).build();
        longHeapKeyedStateBackend.setCurrentKey(key);
        final InternalListState<Long, VoidNamespace, Long> listState = longHeapKeyedStateBackend.createInternalState(INSTANCE, new org.apache.flink.api.common.state.ListStateDescriptor("test", LongSerializer.INSTANCE));
        KvStateRequestSerializerTest.testListSerialization(key, listState);
        longHeapKeyedStateBackend.dispose();
    }

    /**
     * Tests map serialization and deserialization match.
     *
     * @see KvStateRequestSerializerTest#testMapSerialization()
    KvStateRequestSerializerTest#testMapSerialization() using the heap state back-end
    test
     */
    @Test
    public void testMapSerialization() throws Exception {
        final long key = 0L;
        // objects for RocksDB state list serialisation
        DBOptions dbOptions = DEFAULT.createDBOptions();
        dbOptions.setCreateIfMissing(true);
        ExecutionConfig executionConfig = new ExecutionConfig();
        final RocksDBKeyedStateBackend<Long> longHeapKeyedStateBackend = new org.apache.flink.contrib.streaming.state.RocksDBKeyedStateBackendBuilder("no-op", ClassLoader.getSystemClassLoader(), temporaryFolder.getRoot(), dbOptions, ( stateName) -> PredefinedOptions.DEFAULT.createColumnOptions(), Mockito.mock(TaskKvStateRegistry.class), LongSerializer.INSTANCE, 1, new KeyGroupRange(0, 0), executionConfig, TestLocalRecoveryConfig.disabled(), PriorityQueueStateType.HEAP, TtlTimeProvider.DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), AbstractStateBackend.getCompressionDecorator(executionConfig), new CloseableRegistry()).build();
        longHeapKeyedStateBackend.setCurrentKey(key);
        final InternalMapState<Long, VoidNamespace, Long, String> mapState = ((InternalMapState<Long, VoidNamespace, Long, String>) (longHeapKeyedStateBackend.getPartitionedState(VoidNamespace.INSTANCE, INSTANCE, new org.apache.flink.api.common.state.MapStateDescriptor("test", LongSerializer.INSTANCE, StringSerializer.INSTANCE))));
        KvStateRequestSerializerTest.testMapSerialization(key, mapState);
        longHeapKeyedStateBackend.dispose();
    }
}
