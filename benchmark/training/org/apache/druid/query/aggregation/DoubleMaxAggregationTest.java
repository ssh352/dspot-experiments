/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.aggregation;


import java.nio.ByteBuffer;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DoubleMaxAggregationTest {
    private DoubleMaxAggregatorFactory doubleMaxAggFactory;

    private ColumnSelectorFactory colSelectorFactory;

    private TestDoubleColumnSelectorImpl selector;

    private double[] values = new double[]{ 1.1, 2.7, 3.5, 1.3 };

    public DoubleMaxAggregationTest() throws Exception {
        String aggSpecJson = "{\"type\": \"doubleMax\", \"name\": \"billy\", \"fieldName\": \"nilly\"}";
        doubleMaxAggFactory = TestHelper.makeJsonMapper().readValue(aggSpecJson, DoubleMaxAggregatorFactory.class);
    }

    @Test
    public void testDoubleMaxAggregator() {
        Aggregator agg = doubleMaxAggFactory.factorize(colSelectorFactory);
        aggregate(selector, agg);
        aggregate(selector, agg);
        aggregate(selector, agg);
        aggregate(selector, agg);
        Assert.assertEquals(values[2], ((Double) (agg.get())).doubleValue(), 1.0E-4);
        Assert.assertEquals(((long) (values[2])), agg.getLong());
        Assert.assertEquals(values[2], agg.getFloat(), 1.0E-4);
    }

    @Test
    public void testDoubleMaxBufferAggregator() {
        BufferAggregator agg = doubleMaxAggFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[(Double.BYTES) + (Byte.BYTES)]);
        agg.init(buffer, 0);
        aggregate(selector, agg, buffer, 0);
        aggregate(selector, agg, buffer, 0);
        aggregate(selector, agg, buffer, 0);
        aggregate(selector, agg, buffer, 0);
        Assert.assertEquals(values[2], ((Double) (agg.get(buffer, 0))).doubleValue(), 1.0E-4);
        Assert.assertEquals(((long) (values[2])), agg.getLong(buffer, 0));
        Assert.assertEquals(values[2], agg.getFloat(buffer, 0), 1.0E-4);
    }

    @Test
    public void testCombine() {
        Assert.assertEquals(3.4, ((Double) (doubleMaxAggFactory.combine(1.2, 3.4))).doubleValue(), 1.0E-4);
    }

    @Test
    public void testEqualsAndHashCode() {
        DoubleMaxAggregatorFactory one = new DoubleMaxAggregatorFactory("name1", "fieldName1");
        DoubleMaxAggregatorFactory oneMore = new DoubleMaxAggregatorFactory("name1", "fieldName1");
        DoubleMaxAggregatorFactory two = new DoubleMaxAggregatorFactory("name2", "fieldName2");
        Assert.assertEquals(one.hashCode(), oneMore.hashCode());
        Assert.assertTrue(one.equals(oneMore));
        Assert.assertFalse(one.equals(two));
    }
}

