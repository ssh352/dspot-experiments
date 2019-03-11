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
package org.apache.ignite.ml.common;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.ml.clustering.kmeans.KMeansModel;
import org.apache.ignite.ml.clustering.kmeans.KMeansTrainer;
import org.apache.ignite.ml.dataset.impl.cache.CacheBasedDatasetBuilder;
import org.apache.ignite.ml.math.functions.IgniteBiFunction;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for IGNITE-10700.
 */
public class KeepBinaryTest extends GridCommonAbstractTest {
    /**
     * Number of nodes in grid.
     */
    private static final int NODE_COUNT = 2;

    /**
     * Number of samples.
     */
    public static final int NUMBER_OF_SAMPLES = 1000;

    /**
     * Half of samples.
     */
    public static final int HALF = (KeepBinaryTest.NUMBER_OF_SAMPLES) / 2;

    /**
     * Ignite instance.
     */
    private Ignite ignite;

    /**
     * Startup Ignite, populate cache and train some model.
     */
    @Test
    public void test() {
        IgniteCache<Integer, BinaryObject> dataCache = populateCache(ignite);
        IgniteBiFunction<Integer, BinaryObject, Vector> featureExtractor = ( k, v) -> VectorUtils.of(new double[]{ v.field("feature1") });
        IgniteBiFunction<Integer, BinaryObject, Double> lbExtractor = ( k, v) -> ((double) (v.field("label")));
        KMeansTrainer trainer = new KMeansTrainer();
        CacheBasedDatasetBuilder<Integer, BinaryObject> datasetBuilder = new CacheBasedDatasetBuilder(ignite, dataCache).withKeepBinary(true);
        KMeansModel kmdl = trainer.fit(datasetBuilder, featureExtractor, lbExtractor);
        Integer zeroCentre = kmdl.predict(VectorUtils.num2Vec(0.0));
        assertTrue(((kmdl.getCenters()[zeroCentre].get(0)) == 0));
    }
}

