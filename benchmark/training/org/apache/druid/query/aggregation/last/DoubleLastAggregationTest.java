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
package org.apache.druid.query.aggregation.last;


import java.nio.ByteBuffer;
import org.apache.druid.collections.SerializablePair;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.BufferAggregator;
import org.apache.druid.query.aggregation.TestDoubleColumnSelectorImpl;
import org.apache.druid.query.aggregation.TestLongColumnSelector;
import org.apache.druid.query.aggregation.TestObjectColumnSelector;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Test;


public class DoubleLastAggregationTest {
    private DoubleLastAggregatorFactory doubleLastAggFactory;

    private DoubleLastAggregatorFactory combiningAggFactory;

    private ColumnSelectorFactory colSelectorFactory;

    private TestLongColumnSelector timeSelector;

    private TestDoubleColumnSelectorImpl valueSelector;

    private TestObjectColumnSelector objectSelector;

    private double[] doubles = new double[]{ 1.1897, 0.001, 86.23, 166.228 };

    private long[] times = new long[]{ 8224, 6879, 2436, 7888 };

    private SerializablePair[] pairs = new SerializablePair[]{ new SerializablePair(52782L, 134.3), new SerializablePair(65492L, 1232.212), new SerializablePair(69134L, 18.1233), new SerializablePair(11111L, 233.5232) };

    @Test
    public void testDoubleLastAggregator() {
        Aggregator agg = doubleLastAggFactory.factorize(colSelectorFactory);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        Pair<Long, Double> result = ((Pair<Long, Double>) (agg.get()));
        Assert.assertEquals(times[0], result.lhs.longValue());
        Assert.assertEquals(doubles[0], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (doubles[0])), agg.getLong());
        Assert.assertEquals(doubles[0], agg.getDouble(), 1.0E-4);
    }

    @Test
    public void testDoubleLastBufferAggregator() {
        BufferAggregator agg = doubleLastAggFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[doubleLastAggFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        Pair<Long, Double> result = ((Pair<Long, Double>) (agg.get(buffer, 0)));
        Assert.assertEquals(times[0], result.lhs.longValue());
        Assert.assertEquals(doubles[0], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (doubles[0])), agg.getLong(buffer, 0));
        Assert.assertEquals(doubles[0], agg.getDouble(buffer, 0), 1.0E-4);
    }

    @Test
    public void testCombine() {
        SerializablePair pair1 = new SerializablePair(1467225000L, 3.621);
        SerializablePair pair2 = new SerializablePair(1467240000L, 785.4);
        Assert.assertEquals(pair2, doubleLastAggFactory.combine(pair1, pair2));
    }

    @Test
    public void testDoubleLastCombiningAggregator() {
        Aggregator agg = combiningAggFactory.factorize(colSelectorFactory);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        Pair<Long, Double> result = ((Pair<Long, Double>) (agg.get()));
        Pair<Long, Double> expected = ((Pair<Long, Double>) (pairs[2]));
        Assert.assertEquals(expected.lhs, result.lhs);
        Assert.assertEquals(expected.rhs, result.rhs, 1.0E-4);
        Assert.assertEquals(expected.rhs.longValue(), agg.getLong());
        Assert.assertEquals(expected.rhs, agg.getDouble(), 1.0E-4);
    }

    @Test
    public void testDoubleLastCombiningBufferAggregator() {
        BufferAggregator agg = combiningAggFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[doubleLastAggFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        Pair<Long, Double> result = ((Pair<Long, Double>) (agg.get(buffer, 0)));
        Pair<Long, Double> expected = ((Pair<Long, Double>) (pairs[2]));
        Assert.assertEquals(expected.lhs, result.lhs);
        Assert.assertEquals(expected.rhs, result.rhs, 1.0E-4);
        Assert.assertEquals(expected.rhs.longValue(), agg.getLong(buffer, 0));
        Assert.assertEquals(expected.rhs, agg.getDouble(buffer, 0), 1.0E-4);
    }

    @Test
    public void testSerde() throws Exception {
        DefaultObjectMapper mapper = new DefaultObjectMapper();
        String doubleSpecJson = "{\"type\":\"doubleLast\",\"name\":\"billy\",\"fieldName\":\"nilly\"}";
        Assert.assertEquals(doubleLastAggFactory, mapper.readValue(doubleSpecJson, AggregatorFactory.class));
    }
}

