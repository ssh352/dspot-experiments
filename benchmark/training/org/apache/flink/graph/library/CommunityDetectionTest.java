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
package org.apache.flink.graph.library;


import org.apache.flink.graph.Graph;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CommunityDetection}.
 */
public class CommunityDetectionTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        Graph<IntValue, Long, Double> result = undirectedSimpleGraph.mapVertices(( v) -> ((long) (v.getId().getValue())), getTypeInfo()).mapEdges(( e) -> ((double) (e.getTarget().getValue())) + (e.getSource().getValue()), getTypeInfo()).run(new CommunityDetection(10, 0.5));
        String expectedResult = "(0,3)\n" + (((("(1,5)\n" + "(2,5)\n") + "(3,3)\n") + "(4,5)\n") + "(5,5)\n");
        TestBaseUtils.compareResultAsText(result.getVertices().collect(), expectedResult);
    }

    @Test
    public void testWithSingletonEdgeGraph() throws Exception {
        Graph<LongValue, Long, Double> result = generate().mapVertices(( v) -> v.getId().getValue(), getTypeInfo()).mapEdges(( e) -> 1.0, getTypeInfo()).run(new CommunityDetection(10, 0.5));
        String expectedResult = "(0,0)\n" + "(1,1)\n";
        TestBaseUtils.compareResultAsText(result.getVertices().collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        emptyGraphWithVertices.mapVertices(( v) -> 0L, getTypeInfo()).mapEdges(( e) -> 0.0, getTypeInfo()).run(new CommunityDetection(10, 0.5));
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        emptyGraphWithoutVertices.mapVertices(( v) -> 0L, getTypeInfo()).mapEdges(( e) -> 0.0, getTypeInfo()).run(new CommunityDetection(10, 0.5));
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        Graph<LongValue, Long, Double> result = undirectedRMatGraph(8, 4).mapVertices(( v) -> v.getId().getValue(), getTypeInfo()).mapEdges(( e) -> ((double) (e.getTarget().getValue())) - (e.getSource().getValue()), getTypeInfo()).run(new CommunityDetection(10, 0.5));
        Checksum checksum = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<org.apache.flink.graph.Vertex<LongValue, Long>>().run(result.getVertices()).execute();
        Assert.assertEquals(184, checksum.getCount());
        Assert.assertEquals(842902L, checksum.getChecksum());
    }
}

