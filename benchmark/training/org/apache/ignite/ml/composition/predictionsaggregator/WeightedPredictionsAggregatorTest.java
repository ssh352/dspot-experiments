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
package org.apache.ignite.ml.composition.predictionsaggregator;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class WeightedPredictionsAggregatorTest {
    /**
     *
     */
    @Test
    public void testApply1() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[]{  });
        Assert.assertEquals(0.0, aggregator.apply(new double[]{  }), 0.001);
    }

    /**
     *
     */
    @Test
    public void testApply2() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[]{ 1.0, 0.5, 0.25 });
        Assert.assertEquals(3.0, aggregator.apply(new double[]{ 1.0, 2.0, 4.0 }), 0.001);
    }

    /**
     * Non-equal weight vector and predictions case
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArguments() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[]{ 1.0, 0.5, 0.25 });
        aggregator.apply(new double[]{  });
    }

    /**
     *
     */
    @Test
    public void testToString() {
        PredictionsAggregator aggr = ((PredictionsAggregator) (( doubles) -> null));
        Assert.assertTrue(((aggr.toString().length()) > 0));
        Assert.assertTrue(((aggr.toString(true).length()) > 0));
        Assert.assertTrue(((aggr.toString(false).length()) > 0));
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[]{  });
        Assert.assertTrue(((aggregator.toString().length()) > 0));
        Assert.assertTrue(((aggregator.toString(true).length()) > 0));
        Assert.assertTrue(((aggregator.toString(false).length()) > 0));
    }
}

