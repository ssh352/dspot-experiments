/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.config.Config;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationServiceImpl_invokeOnPartitionsTest extends HazelcastTestSupport {
    @Test
    public void test_onAllPartitions() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Map<Integer, Object> result = opService.invokeOnAllPartitions(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl());
        Assert.assertEquals(100, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void test_onSelectedPartitions() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Collection<Integer> partitions = new LinkedList<Integer>();
        Collections.addAll(partitions, 1, 2, 3);
        Map<Integer, Object> result = opService.invokeOnPartitions(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), partitions);
        Assert.assertEquals(3, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void test_onEmptyPartitionLIst() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Map<Integer, Object> result = opService.invokeOnPartitions(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), Collections.EMPTY_LIST);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testAsync_onAllPartitions_getResponeViaFuture() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Future<Map<Integer, Object>> future = opService.invokeOnAllPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl());
        Map<Integer, Object> result = future.get();
        Assert.assertEquals(100, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void testAsync_onSelectedPartitions_getResponeViaFuture() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Collection<Integer> partitions = new LinkedList<Integer>();
        Collections.addAll(partitions, 1, 2, 3);
        Future<Map<Integer, Object>> future = opService.invokeOnPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), partitions);
        Map<Integer, Object> result = future.get();
        Assert.assertEquals(3, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void testAsync_onEmptyPartitionList_getResponeViaFuture() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Future<Map<Integer, Object>> future = opService.invokeOnPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), Collections.EMPTY_LIST);
        Map<Integer, Object> result = future.get();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testAsync_onAllPartitions_getResponseViaCallback() {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        final AtomicReference<Map<Integer, Object>> resultReference = new AtomicReference<Map<Integer, Object>>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        ExecutionCallback<Map<Integer, Object>> executionCallback = new ExecutionCallback<Map<Integer, Object>>() {
            @Override
            public void onResponse(Map<Integer, Object> response) {
                resultReference.set(response);
                responseLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        };
        opService.invokeOnAllPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl()).andThen(executionCallback);
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Map<Integer, Object> result = resultReference.get();
        Assert.assertEquals(100, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void testAsync_onSelectedPartitions_getResponseViaCallback() {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Collection<Integer> partitions = new LinkedList<Integer>();
        Collections.addAll(partitions, 1, 2, 3);
        final AtomicReference<Map<Integer, Object>> resultReference = new AtomicReference<Map<Integer, Object>>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        ExecutionCallback<Map<Integer, Object>> executionCallback = new ExecutionCallback<Map<Integer, Object>>() {
            @Override
            public void onResponse(Map<Integer, Object> response) {
                resultReference.set(response);
                responseLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        };
        opService.invokeOnPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), partitions).andThen(executionCallback);
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Map<Integer, Object> result = resultReference.get();
        Assert.assertEquals(3, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void testAsync_onEmptyPartitionList_getResponseViaCallback() {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        final AtomicReference<Map<Integer, Object>> resultReference = new AtomicReference<Map<Integer, Object>>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        ExecutionCallback<Map<Integer, Object>> executionCallback = new ExecutionCallback<Map<Integer, Object>>() {
            @Override
            public void onResponse(Map<Integer, Object> response) {
                resultReference.set(response);
                responseLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        };
        opService.invokeOnPartitionsAsync(null, new OperationServiceImpl_invokeOnPartitionsTest.OperationFactoryImpl(), Collections.EMPTY_LIST).andThen(executionCallback);
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Map<Integer, Object> result = resultReference.get();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLongRunning() throws Exception {
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), "2000").setProperty(GroupProperty.PARTITION_COUNT.getName(), "10");
        config.getSerializationConfig().addDataSerializableFactory(123, new OperationServiceImpl_invokeOnPartitionsTest.SlowOperationSerializationFactory());
        TestHazelcastInstanceFactory hzFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = hzFactory.newHazelcastInstance(config);
        HazelcastInstance hz2 = hzFactory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz1, hz2);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz1);
        Map<Integer, Object> result = opService.invokeOnAllPartitions(null, new OperationServiceImpl_invokeOnPartitionsTest.SlowOperationFactoryImpl());
        Assert.assertEquals(10, result.size());
        for (Map.Entry<Integer, Object> entry : result.entrySet()) {
            int partitionId = entry.getKey();
            Assert.assertEquals((partitionId * 2), entry.getValue());
        }
    }

    @Test
    public void testPartitionScopeIsRespectedForPartitionAwareFactories() throws Exception {
        Config config = new Config().setProperty(GroupProperty.PARTITION_COUNT.getName(), "100");
        config.getSerializationConfig().addDataSerializableFactory(321, new OperationServiceImpl_invokeOnPartitionsTest.PartitionAwareOperationFactoryDataSerializableFactory());
        HazelcastInstance hz = createHazelcastInstance(config);
        OperationServiceImpl opService = HazelcastTestSupport.getOperationServiceImpl(hz);
        Map<Integer, Object> result = opService.invokeOnPartitions(null, new OperationServiceImpl_invokeOnPartitionsTest.PartitionAwareOperationFactoryImpl(new int[]{ 0, 1, 2 }), new int[]{ 1 });
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(2, result.values().iterator().next());
    }

    private static class OperationFactoryImpl extends OperationServiceImpl_invokeOnPartitionsTest.AbstractOperationFactor {
        @Override
        public Operation createOperation() {
            return new OperationServiceImpl_invokeOnPartitionsTest.OperationImpl();
        }

        @Override
        public int getFactoryId() {
            return 0;
        }

        @Override
        public int getId() {
            return 0;
        }
    }

    private static class SlowOperationFactoryImpl extends OperationServiceImpl_invokeOnPartitionsTest.AbstractOperationFactor {
        @Override
        public Operation createOperation() {
            return new OperationServiceImpl_invokeOnPartitionsTest.SlowOperation();
        }

        @Override
        public int getFactoryId() {
            return 123;
        }

        @Override
        public int getId() {
            return 145;
        }
    }

    private static class SlowOperationSerializationFactory implements DataSerializableFactory {
        @Override
        public IdentifiedDataSerializable create(int typeId) {
            return new OperationServiceImpl_invokeOnPartitionsTest.SlowOperationFactoryImpl();
        }
    }

    private abstract static class AbstractOperationFactor implements OperationFactory {
        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
        }
    }

    private static class OperationImpl extends Operation {
        private int response;

        @Override
        public void run() {
            response = (HazelcastTestSupport.getPartitionId()) * 2;
        }

        @Override
        public Object getResponse() {
            return response;
        }
    }

    private static class SlowOperation extends Operation {
        private int response;

        @Override
        public void run() {
            HazelcastTestSupport.sleepSeconds(5);
            response = (HazelcastTestSupport.getPartitionId()) * 2;
        }

        @Override
        public Object getResponse() {
            return response;
        }
    }

    private static class PartitionAwareOperationFactoryImpl extends PartitionAwareOperationFactory {
        public PartitionAwareOperationFactoryImpl(int[] partitions) {
            this.partitions = partitions;
        }

        public PartitionAwareOperationFactoryImpl() {
        }

        @Override
        public Operation createPartitionOperation(int partition) {
            return new OperationServiceImpl_invokeOnPartitionsTest.OperationImpl();
        }

        @Override
        public int getFactoryId() {
            return 321;
        }

        @Override
        public int getId() {
            return 654;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeIntArray(partitions);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.partitions = in.readIntArray();
        }
    }

    private static class PartitionAwareOperationFactoryDataSerializableFactory implements DataSerializableFactory {
        @Override
        public IdentifiedDataSerializable create(int typeId) {
            return new OperationServiceImpl_invokeOnPartitionsTest.PartitionAwareOperationFactoryImpl();
        }
    }
}
