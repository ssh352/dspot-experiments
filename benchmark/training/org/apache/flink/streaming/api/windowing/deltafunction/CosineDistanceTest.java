/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.windowing.deltafunction;


import org.apache.flink.streaming.api.functions.windowing.delta.CosineDistance;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CosineDistance}.
 */
public class CosineDistanceTest {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCosineDistance() {
        // Reference calculated using wolfram alpha
        double[][][] testdata = new double[][][]{ new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 0, 0 } }, new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 1, 2, 3 } }, new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 0, 0, 0 } }, new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 4, 5, 6 } }, new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ -4, -5, -6 } }, new double[][]{ new double[]{ 1, 2, -3 }, new double[]{ -4, 5, -6 } }, new double[][]{ new double[]{ 1, 2, 3, 4 }, new double[]{ 5, 6, 7, 8 } }, new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 } }, new double[][]{ new double[]{ 1 }, new double[]{ 2 } } };
        double[] referenceSolutions = new double[]{ 0, 0, 0, 0.025368, 1.974631, 0.269026, 0.031136, 0.01613, 0 };
        for (int i = 0; i < (testdata.length); i++) {
            Assert.assertEquals(((("Wrong result for inputs " + (arrayToString(testdata[i][0]))) + " and ") + (arrayToString(testdata[i][0]))), referenceSolutions[i], new CosineDistance().getDelta(testdata[i][0], testdata[i][1]), 1.0E-6);
        }
    }
}
