/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.euphoria.core.client.operator;


import java.math.BigDecimal;
import org.apache.beam.sdk.extensions.euphoria.core.client.io.Collector;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test operator FlatMap.
 */
public class FlatMapTest {
    @Test
    public void testBuild() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> mapped = FlatMap.named("FlatMap1").of(dataset).using((String s,Collector<String> c) -> c.collect(s)).output();
        final FlatMap map = ((FlatMap) (TestUtils.getProducer(mapped)));
        Assert.assertTrue(map.getName().isPresent());
        Assert.assertEquals("FlatMap1", map.getName().get());
        Assert.assertNotNull(map.getFunctor());
        Assert.assertFalse(map.getEventTimeExtractor().isPresent());
    }

    @Test
    public void testBuild_EventTimeExtractor() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<BigDecimal> mapped = // ~ consuming the original input elements
        FlatMap.named("FlatMap2").of(dataset).using((String s,Collector<BigDecimal> c) -> c.collect(null)).eventTimeBy(Long::parseLong).output();
        final FlatMap map = ((FlatMap) (TestUtils.getProducer(mapped)));
        Assert.assertTrue(map.getName().isPresent());
        Assert.assertEquals("FlatMap2", map.getName().get());
        Assert.assertNotNull(map.getFunctor());
        Assert.assertTrue(map.getEventTimeExtractor().isPresent());
    }

    @Test
    public void testBuild_WithCounters() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> mapped = FlatMap.named("FlatMap1").of(dataset).using((String s,Collector<String> c) -> {
            c.getCounter("my-counter").increment();
            c.collect(s);
        }).output();
        final FlatMap map = ((FlatMap) (TestUtils.getProducer(mapped)));
        Assert.assertTrue(map.getName().isPresent());
        Assert.assertEquals("FlatMap1", map.getName().get());
        Assert.assertNotNull(map.getFunctor());
    }

    @Test
    public void testBuild_ImplicitName() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> mapped = FlatMap.of(dataset).using((String s,Collector<String> c) -> c.collect(s)).output();
        final FlatMap map = ((FlatMap) (TestUtils.getProducer(mapped)));
        Assert.assertFalse(map.getName().isPresent());
    }
}

