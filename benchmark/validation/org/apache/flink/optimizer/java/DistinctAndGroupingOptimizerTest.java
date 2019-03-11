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
package org.apache.flink.optimizer.java;


import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SinkPlanNode;
import org.apache.flink.optimizer.testfunctions.IdentityMapper;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class DistinctAndGroupingOptimizerTest extends CompilerTestBase {
    @Test
    public void testDistinctPreservesPartitioningOfDistinctFields() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4);
            @SuppressWarnings("unchecked")
            DataSet<Tuple2<Long, Long>> data = env.fromElements(new Tuple2<Long, Long>(0L, 0L), new Tuple2<Long, Long>(1L, 1L)).map(new IdentityMapper<Tuple2<Long, Long>>()).setParallelism(4);
            data.distinct(0).groupBy(0).sum(1).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Long, Long>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode distinctReducer = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            // reducer can be forward, reuses partitioning from distinct
            Assert.assertEquals(FORWARD, reducer.getInput().getShipStrategy());
            // distinct reducer is partitioned
            Assert.assertEquals(PARTITION_HASH, distinctReducer.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDistinctDestroysPartitioningOfNonDistinctFields() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4);
            @SuppressWarnings("unchecked")
            DataSet<Tuple2<Long, Long>> data = env.fromElements(new Tuple2<Long, Long>(0L, 0L), new Tuple2<Long, Long>(1L, 1L)).map(new IdentityMapper<Tuple2<Long, Long>>()).setParallelism(4);
            data.distinct(1).groupBy(0).sum(1).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Tuple2<Long, Long>>());
            Plan p = env.createProgramPlan();
            OptimizedPlan op = compileNoStats(p);
            SinkPlanNode sink = op.getDataSinks().iterator().next();
            SingleInputPlanNode reducer = ((SingleInputPlanNode) (sink.getInput().getSource()));
            SingleInputPlanNode combiner = ((SingleInputPlanNode) (reducer.getInput().getSource()));
            SingleInputPlanNode distinctReducer = ((SingleInputPlanNode) (combiner.getInput().getSource()));
            Assert.assertEquals(FORWARD, sink.getInput().getShipStrategy());
            // reducer must repartition, because it works on a different field
            Assert.assertEquals(PARTITION_HASH, reducer.getInput().getShipStrategy());
            Assert.assertEquals(FORWARD, combiner.getInput().getShipStrategy());
            // distinct reducer is partitioned
            Assert.assertEquals(PARTITION_HASH, distinctReducer.getInput().getShipStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

