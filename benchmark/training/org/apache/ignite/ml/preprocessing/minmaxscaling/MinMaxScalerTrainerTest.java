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
package org.apache.ignite.ml.preprocessing.minmaxscaling;


import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MinMaxScalerTrainer}.
 */
public class MinMaxScalerTrainerTest extends TrainerTest {
    /**
     * Tests {@code fit()} method.
     */
    @Test
    public void testFit() {
        Map<Integer, Vector> data = new HashMap<>();
        data.put(1, VectorUtils.of(2, 4, 1));
        data.put(2, VectorUtils.of(1, 8, 22));
        data.put(3, VectorUtils.of(4, 10, 100));
        data.put(4, VectorUtils.of(0, 22, 300));
        DatasetBuilder<Integer, Vector> datasetBuilder = new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts);
        MinMaxScalerTrainer<Integer, Vector> standardizationTrainer = new MinMaxScalerTrainer();
        MinMaxScalerPreprocessor<Integer, Vector> preprocessor = standardizationTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(new double[]{ 0, 4, 1 }, preprocessor.getMin(), 1.0E-8);
        Assert.assertArrayEquals(new double[]{ 4, 22, 300 }, preprocessor.getMax(), 1.0E-8);
    }
}
