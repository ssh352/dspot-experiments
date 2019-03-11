/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.workload;


import Histogram.Summary;
import org.junit.Assert;
import org.junit.Test;


public class HistogramTest {
    @Test
    public void testHistogramAverage() {
        Histogram empty = HistogramTest.createHistogram(1);
        Assert.assertEquals(0, ((int) (empty.summarize(new float[0]).average())));
        Histogram histogram = HistogramTest.createHistogram(70, 1, 2, 3, 4, 5, 6, 1);
        Assert.assertEquals(3, ((int) (histogram.summarize(new float[0]).average())));
        histogram.add(60);
        Assert.assertEquals(10, ((int) (histogram.summarize(new float[0]).average())));
    }

    @Test
    public void testHistogramSamples() {
        Histogram empty = HistogramTest.createHistogram(100);
        Assert.assertEquals(0, empty.summarize(new float[0]).numSamples());
        Histogram histogram = HistogramTest.createHistogram(100, 4, 8, 2, 4, 1, 100, 150);
        Assert.assertEquals(7, histogram.summarize(new float[0]).numSamples());
        histogram.add(60);
        Assert.assertEquals(8, histogram.summarize(new float[0]).numSamples());
    }

    @Test
    public void testHistogramPercentiles() {
        Histogram histogram = HistogramTest.createHistogram(100, 1, 2, 3, 4, 5, 6, 80, 90);
        float[] percentiles = new float[]{ 0.5F, 0.9F, 0.99F, 1.0F };
        Histogram.Summary summary = histogram.summarize(percentiles);
        Assert.assertEquals(8, summary.numSamples());
        Assert.assertEquals(4, summary.percentiles().get(0).value());
        Assert.assertEquals(80, summary.percentiles().get(1).value());
        Assert.assertEquals(80, summary.percentiles().get(2).value());
        Assert.assertEquals(90, summary.percentiles().get(3).value());
        histogram.add(30);
        histogram.add(30);
        histogram.add(30);
        summary = histogram.summarize(new float[]{ 0.5F });
        Assert.assertEquals(11, summary.numSamples());
        Assert.assertEquals(5, summary.percentiles().get(0).value());
        Histogram empty = HistogramTest.createHistogram(100);
        summary = empty.summarize(new float[]{ 0.5F });
        Assert.assertEquals(0, summary.percentiles().get(0).value());
        histogram = HistogramTest.createHistogram(1000);
        histogram.add(100);
        histogram.add(200);
        summary = histogram.summarize(new float[]{ 0.0F, 0.5F, 1.0F });
        Assert.assertEquals(0, summary.percentiles().get(0).value());
        Assert.assertEquals(100, summary.percentiles().get(1).value());
        Assert.assertEquals(200, summary.percentiles().get(2).value());
    }
}

