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
package org.apache.ignite.ml.dataset.impl.local;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LocalDatasetBuilder}.
 */
public class LocalDatasetBuilderTest {
    /**
     * Tests {@code build()} method.
     */
    @Test
    public void testBuild() {
        Map<Integer, Integer> data = new HashMap<>();
        for (int i = 0; i < 100; i++)
            data.put(i, i);

        LocalDatasetBuilder<Integer, Integer> builder = new LocalDatasetBuilder(data, 10);
        LocalDataset<Serializable, LocalDatasetBuilderTest.TestPartitionData> dataset = buildDataset(builder);
        Assert.assertEquals(10, dataset.getCtx().size());
        Assert.assertEquals(10, dataset.getData().size());
        AtomicLong cnt = new AtomicLong();
        dataset.compute(( partData, env) -> {
            cnt.incrementAndGet();
            int[] arr = partData.data;
            assertEquals(10, arr.length);
            for (int i = 0; i < 10; i++)
                assertEquals((((env.partition()) * 10) + i), arr[i]);

        });
        Assert.assertEquals(10, cnt.intValue());
    }

    /**
     * Tests {@code build()} method with predicate.
     */
    @Test
    public void testBuildWithPredicate() {
        Map<Integer, Integer> data = new HashMap<>();
        for (int i = 0; i < 100; i++)
            data.put(i, i);

        LocalDatasetBuilder<Integer, Integer> builder = new LocalDatasetBuilder(data, ( k, v) -> (k % 2) == 0, 10);
        LocalDataset<Serializable, LocalDatasetBuilderTest.TestPartitionData> dataset = buildDataset(builder);
        AtomicLong cnt = new AtomicLong();
        dataset.compute(( partData, env) -> {
            cnt.incrementAndGet();
            int[] arr = partData.data;
            assertEquals(5, arr.length);
            for (int i = 0; i < 5; i++)
                assertEquals(((((env.partition()) * 5) + i) * 2), arr[i]);

        });
        Assert.assertEquals(10, cnt.intValue());
    }

    /**
     * Test partition {@code data}.
     */
    private static class TestPartitionData implements AutoCloseable {
        /**
         * Data.
         */
        private int[] data;

        /**
         * Constructs a new test partition data instance.
         *
         * @param data
         * 		Data.
         */
        TestPartitionData(int[] data) {
            this.data = data;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void close() {
            // Do nothing, GC will clean up.
        }
    }
}

