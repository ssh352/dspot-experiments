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
package org.apache.flink.graph.library.metric.undirected;


import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.library.metric.undirected.EdgeMetrics.Result;
import org.junit.Test;


/**
 * Tests for {@link EdgeMetrics}.
 */
public class EdgeMetricsTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        EdgeMetricsTest.validate(undirectedSimpleGraph, new Result(2, 6, 1, 3));
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        long expectedMaximumTriplets = CombinatoricsUtils.binomialCoefficient(((int) (expectedDegree)), 2);
        long expectedTriplets = (completeGraphVertexCount) * expectedMaximumTriplets;
        Result expectedResult = new Result((expectedTriplets / 3), ((2 * expectedTriplets) / 3), expectedMaximumTriplets, expectedMaximumTriplets);
        EdgeMetricsTest.validate(completeGraph, expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        EdgeMetricsTest.validate(emptyGraphWithVertices, new Result(0, 0, 0, 0));
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        EdgeMetricsTest.validate(emptyGraphWithoutVertices, new Result(0, 0, 0, 0));
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        EdgeMetricsTest.validate(undirectedRMatGraph(10, 16), new Result(107817, 315537, 820, 3822));
    }
}
