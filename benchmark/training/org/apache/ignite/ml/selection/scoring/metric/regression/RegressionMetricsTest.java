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
package org.apache.ignite.ml.selection.scoring.metric.regression;


import java.util.Arrays;
import org.apache.ignite.ml.selection.scoring.cursor.LabelPairCursor;
import org.apache.ignite.ml.selection.scoring.metric.Metric;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RegressionMetrics}.
 */
public class RegressionMetricsTest {
    /**
     *
     */
    @Test
    public void testDefaultBehaviour() {
        Metric scoreCalculator = new RegressionMetrics();
        LabelPairCursor<Double> cursor = new org.apache.ignite.ml.selection.scoring.TestLabelPairCursor(Arrays.asList(1.0, 1.0, 1.0, 1.0), Arrays.asList(1.0, 1.0, 0.0, 1.0));
        double score = scoreCalculator.score(cursor.iterator());
        Assert.assertEquals(0.5, score, 1.0E-12);
    }

    /**
     *
     */
    @Test
    public void testDefaultBehaviourForScoreAll() {
        RegressionMetrics scoreCalculator = new RegressionMetrics();
        LabelPairCursor<Double> cursor = new org.apache.ignite.ml.selection.scoring.TestLabelPairCursor(Arrays.asList(1.0, 1.0, 1.0, 1.0), Arrays.asList(1.0, 1.0, 0.0, 1.0));
        RegressionMetricValues metricValues = scoreCalculator.scoreAll(cursor.iterator());
        Assert.assertEquals(1.0, metricValues.rss(), 1.0E-12);
    }

    /**
     *
     */
    @Test
    public void testCustomMetric() {
        RegressionMetrics scoreCalculator = ((RegressionMetrics) (new RegressionMetrics().withMetric(RegressionMetricValues::mae)));
        LabelPairCursor<Double> cursor = new org.apache.ignite.ml.selection.scoring.TestLabelPairCursor(Arrays.asList(2.0, 2.0, 2.0, 2.0), Arrays.asList(2.0, 2.0, 1.0, 2.0));
        double score = scoreCalculator.score(cursor.iterator());
        Assert.assertEquals(0.25, score, 1.0E-12);
    }

    /**
     *
     */
    @Test
    public void testNullCustomMetric() {
        RegressionMetrics scoreCalculator = ((RegressionMetrics) (new RegressionMetrics().withMetric(null)));
        LabelPairCursor<Double> cursor = new org.apache.ignite.ml.selection.scoring.TestLabelPairCursor(Arrays.asList(2.0, 2.0, 2.0, 2.0), Arrays.asList(2.0, 2.0, 1.0, 2.0));
        double score = scoreCalculator.score(cursor.iterator());
        // rmse as default metric
        Assert.assertEquals(0.5, score, 1.0E-12);
    }
}
