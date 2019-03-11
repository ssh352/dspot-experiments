/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.partitioner;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.JobException;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.blob.VoidBlobWriter;
import org.apache.flink.runtime.executiongraph.ExecutionEdge;
import org.apache.flink.runtime.executiongraph.ExecutionGraph;
import org.apache.flink.runtime.executiongraph.ExecutionJobVertex;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.executiongraph.JobInformation;
import org.apache.flink.runtime.executiongraph.TestingSlotProvider;
import org.apache.flink.runtime.executiongraph.failover.RestartAllStrategy;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RescalePartitioner}.
 */
@SuppressWarnings("serial")
public class RescalePartitionerTest extends StreamPartitionerTest {
    @Test
    public void testSelectChannelsInterval() {
        streamPartitioner.setup(3);
        assertSelectedChannel(0);
        assertSelectedChannel(1);
        assertSelectedChannel(2);
        assertSelectedChannel(0);
    }

    @Test
    public void testExecutionGraphGeneration() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        // get input data
        DataStream<String> text = env.addSource(new org.apache.flink.streaming.api.functions.source.ParallelSourceFunction<String>() {
            private static final long serialVersionUID = 7772338606389180774L;

            @Override
            public void run(SourceContext<String> ctx) throws Exception {
            }

            @Override
            public void cancel() {
            }
        }).setParallelism(2);
        DataStream<Tuple2<String, Integer>> counts = text.rescale().flatMap(new org.apache.flink.api.common.functions.FlatMapFunction<String, Tuple2<String, Integer>>() {
            private static final long serialVersionUID = -5255930322161596829L;

            @Override
            public void flatMap(String value, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) throws Exception {
            }
        });
        counts.rescale().print().setParallelism(2);
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        final JobID jobId = new JobID();
        final String jobName = "Semi-Rebalance Test Job";
        final Configuration cfg = new Configuration();
        List<JobVertex> jobVertices = jobGraph.getVerticesSortedTopologicallyFromSources();
        JobVertex sourceVertex = jobVertices.get(0);
        JobVertex mapVertex = jobVertices.get(1);
        JobVertex sinkVertex = jobVertices.get(2);
        Assert.assertEquals(2, sourceVertex.getParallelism());
        Assert.assertEquals(4, mapVertex.getParallelism());
        Assert.assertEquals(2, sinkVertex.getParallelism());
        final JobInformation jobInformation = new org.apache.flink.runtime.executiongraph.DummyJobInformation(jobId, jobName);
        ExecutionGraph eg = new ExecutionGraph(jobInformation, TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new RestartAllStrategy.Factory(), new TestingSlotProvider(( ignored) -> new CompletableFuture<>()), ExecutionGraph.class.getClassLoader(), VoidBlobWriter.getInstance(), AkkaUtils.getDefaultTimeout());
        try {
            eg.attachJobGraph(jobVertices);
        } catch (JobException e) {
            e.printStackTrace();
            Assert.fail(("Building ExecutionGraph failed: " + (e.getMessage())));
        }
        ExecutionJobVertex execSourceVertex = eg.getJobVertex(sourceVertex.getID());
        ExecutionJobVertex execMapVertex = eg.getJobVertex(mapVertex.getID());
        ExecutionJobVertex execSinkVertex = eg.getJobVertex(sinkVertex.getID());
        Assert.assertEquals(0, execSourceVertex.getInputs().size());
        Assert.assertEquals(1, execMapVertex.getInputs().size());
        Assert.assertEquals(4, execMapVertex.getParallelism());
        ExecutionVertex[] mapTaskVertices = execMapVertex.getTaskVertices();
        // verify that we have each parallel input partition exactly twice, i.e. that one source
        // sends to two unique mappers
        Map<Integer, Integer> mapInputPartitionCounts = new HashMap<>();
        for (ExecutionVertex mapTaskVertex : mapTaskVertices) {
            Assert.assertEquals(1, mapTaskVertex.getNumberOfInputs());
            Assert.assertEquals(1, mapTaskVertex.getInputEdges(0).length);
            ExecutionEdge inputEdge = mapTaskVertex.getInputEdges(0)[0];
            Assert.assertEquals(sourceVertex.getID(), inputEdge.getSource().getProducer().getJobvertexId());
            int inputPartition = inputEdge.getSource().getPartitionNumber();
            if (!(mapInputPartitionCounts.containsKey(inputPartition))) {
                mapInputPartitionCounts.put(inputPartition, 1);
            } else {
                mapInputPartitionCounts.put(inputPartition, ((mapInputPartitionCounts.get(inputPartition)) + 1));
            }
        }
        Assert.assertEquals(2, mapInputPartitionCounts.size());
        for (int count : mapInputPartitionCounts.values()) {
            Assert.assertEquals(2, count);
        }
        Assert.assertEquals(1, execSinkVertex.getInputs().size());
        Assert.assertEquals(2, execSinkVertex.getParallelism());
        ExecutionVertex[] sinkTaskVertices = execSinkVertex.getTaskVertices();
        // verify each sink instance has two inputs from the map and that each map subpartition
        // only occurs in one unique input edge
        Set<Integer> mapSubpartitions = new HashSet<>();
        for (ExecutionVertex sinkTaskVertex : sinkTaskVertices) {
            Assert.assertEquals(1, sinkTaskVertex.getNumberOfInputs());
            Assert.assertEquals(2, sinkTaskVertex.getInputEdges(0).length);
            ExecutionEdge inputEdge1 = sinkTaskVertex.getInputEdges(0)[0];
            ExecutionEdge inputEdge2 = sinkTaskVertex.getInputEdges(0)[1];
            Assert.assertEquals(mapVertex.getID(), inputEdge1.getSource().getProducer().getJobvertexId());
            Assert.assertEquals(mapVertex.getID(), inputEdge2.getSource().getProducer().getJobvertexId());
            int inputPartition1 = inputEdge1.getSource().getPartitionNumber();
            Assert.assertFalse(mapSubpartitions.contains(inputPartition1));
            mapSubpartitions.add(inputPartition1);
            int inputPartition2 = inputEdge2.getSource().getPartitionNumber();
            Assert.assertFalse(mapSubpartitions.contains(inputPartition2));
            mapSubpartitions.add(inputPartition2);
        }
        Assert.assertEquals(4, mapSubpartitions.size());
    }
}

