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


import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test operator Filter.
 */
public class FilterTest {
    @Test
    public void testBuild() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> filtered = Filter.named("Filter1").of(dataset).by(( s) -> !(s.equals(""))).output();
        final Filter filter = ((Filter) (TestUtils.getProducer(filtered)));
        Assert.assertTrue(filter.getName().isPresent());
        Assert.assertEquals("Filter1", filter.getName().get());
        Assert.assertNotNull(filter.getPredicate());
    }

    @Test
    public void testBuild_implicitName() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> filtered = Filter.of(dataset).by(( s) -> !(s.equals(""))).output();
        final Filter filter = ((Filter) (TestUtils.getProducer(filtered)));
        Assert.assertFalse(filter.getName().isPresent());
    }
}

