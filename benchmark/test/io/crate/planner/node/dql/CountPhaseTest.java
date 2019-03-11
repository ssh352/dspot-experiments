/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.planner.node.dql;


import DistributionInfo.DEFAULT_BROADCAST;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIndexedContainer;
import io.crate.common.collections.TreeMapBuilder;
import io.crate.execution.dsl.phases.CountPhase;
import io.crate.expression.symbol.Literal;
import io.crate.metadata.Routing;
import io.crate.planner.distribution.DistributionInfo;
import io.crate.test.integration.CrateUnitTest;
import java.util.Map;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CountPhaseTest extends CrateUnitTest {
    @Test
    public void testStreaming() throws Exception {
        Routing routing = new Routing(TreeMapBuilder.<String, Map<String, IntIndexedContainer>>newMapBuilder().put("n1", TreeMapBuilder.<String, IntIndexedContainer>newMapBuilder().put("i1", IntArrayList.from(1, 2)).put("i2", IntArrayList.from(1, 2)).map()).put("n2", TreeMapBuilder.<String, IntIndexedContainer>newMapBuilder().put("i1", IntArrayList.from(3)).map()).map());
        CountPhase countPhase = new CountPhase(1, routing, Literal.BOOLEAN_TRUE, DistributionInfo.DEFAULT_BROADCAST);
        BytesStreamOutput out = new BytesStreamOutput(10);
        countPhase.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        CountPhase streamedNode = new CountPhase(in);
        assertThat(streamedNode.phaseId(), Matchers.is(1));
        assertThat(streamedNode.nodeIds(), Matchers.containsInAnyOrder("n1", "n2"));
        assertThat(streamedNode.routing(), Matchers.equalTo(routing));
        assertThat(streamedNode.distributionInfo(), Matchers.equalTo(DEFAULT_BROADCAST));
    }
}

