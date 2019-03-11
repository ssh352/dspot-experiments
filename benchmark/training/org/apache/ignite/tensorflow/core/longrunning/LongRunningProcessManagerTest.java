/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.tensorflow.core.longrunning;


import LongRunningProcessState.RUNNING;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.tensorflow.core.longrunning.task.LongRunningProcessClearTask;
import org.apache.ignite.tensorflow.core.longrunning.task.LongRunningProcessPingTask;
import org.apache.ignite.tensorflow.core.longrunning.task.LongRunningProcessStartTask;
import org.apache.ignite.tensorflow.core.longrunning.task.LongRunningProcessStopTask;
import org.apache.ignite.tensorflow.core.longrunning.task.util.LongRunningProcessState;
import org.apache.ignite.tensorflow.core.longrunning.task.util.LongRunningProcessStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link LongRunningProcessManager}.
 */
public class LongRunningProcessManagerTest {
    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testStart() {
        UUID nodeId = UUID.randomUUID();
        UUID procId = UUID.randomUUID();
        Ignite ignite = Mockito.mock(Ignite.class);
        IgniteCluster cluster = Mockito.mock(IgniteCluster.class);
        ClusterGroup clusterGrp = Mockito.mock(ClusterGroup.class);
        IgniteCompute igniteCompute = Mockito.mock(IgniteCompute.class);
        Mockito.doReturn(cluster).when(ignite).cluster();
        Mockito.doReturn(igniteCompute).when(ignite).compute(ArgumentMatchers.eq(clusterGrp));
        Mockito.doReturn(clusterGrp).when(cluster).forNodeId(ArgumentMatchers.eq(nodeId));
        Mockito.doReturn(Collections.singletonList(procId)).when(igniteCompute).call(ArgumentMatchers.any(IgniteCallable.class));
        List<LongRunningProcess> list = Collections.singletonList(new LongRunningProcess(nodeId, () -> {
        }));
        LongRunningProcessManager mgr = new LongRunningProcessManager(ignite);
        Map<UUID, List<UUID>> res = mgr.start(list);
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.containsKey(nodeId));
        Assert.assertEquals(procId, res.get(nodeId).iterator().next());
        Mockito.verify(igniteCompute).call(ArgumentMatchers.any(LongRunningProcessStartTask.class));
    }

    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPing() {
        UUID nodeId = UUID.randomUUID();
        UUID procId = UUID.randomUUID();
        Ignite ignite = Mockito.mock(Ignite.class);
        IgniteCluster cluster = Mockito.mock(IgniteCluster.class);
        ClusterGroup clusterGrp = Mockito.mock(ClusterGroup.class);
        IgniteCompute igniteCompute = Mockito.mock(IgniteCompute.class);
        Mockito.doReturn(cluster).when(ignite).cluster();
        Mockito.doReturn(igniteCompute).when(ignite).compute(ArgumentMatchers.eq(clusterGrp));
        Mockito.doReturn(clusterGrp).when(cluster).forNodeId(ArgumentMatchers.eq(nodeId));
        Mockito.doReturn(Collections.singletonList(new LongRunningProcessStatus(LongRunningProcessState.RUNNING))).when(igniteCompute).call(ArgumentMatchers.any(IgniteCallable.class));
        Map<UUID, List<UUID>> procIds = new HashMap<>();
        procIds.put(nodeId, Collections.singletonList(procId));
        LongRunningProcessManager mgr = new LongRunningProcessManager(ignite);
        Map<UUID, List<LongRunningProcessStatus>> res = mgr.ping(procIds);
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.containsKey(nodeId));
        Assert.assertEquals(RUNNING, res.get(nodeId).iterator().next().getState());
        Mockito.verify(igniteCompute).call(ArgumentMatchers.any(LongRunningProcessPingTask.class));
    }

    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testStop() {
        UUID nodeId = UUID.randomUUID();
        UUID procId = UUID.randomUUID();
        Ignite ignite = Mockito.mock(Ignite.class);
        IgniteCluster cluster = Mockito.mock(IgniteCluster.class);
        ClusterGroup clusterGrp = Mockito.mock(ClusterGroup.class);
        IgniteCompute igniteCompute = Mockito.mock(IgniteCompute.class);
        Mockito.doReturn(cluster).when(ignite).cluster();
        Mockito.doReturn(igniteCompute).when(ignite).compute(ArgumentMatchers.eq(clusterGrp));
        Mockito.doReturn(clusterGrp).when(cluster).forNodeId(ArgumentMatchers.eq(nodeId));
        Mockito.doReturn(Collections.singletonList(new LongRunningProcessStatus(LongRunningProcessState.RUNNING))).when(igniteCompute).call(ArgumentMatchers.any(IgniteCallable.class));
        Map<UUID, List<UUID>> procIds = new HashMap<>();
        procIds.put(nodeId, Collections.singletonList(procId));
        LongRunningProcessManager mgr = new LongRunningProcessManager(ignite);
        Map<UUID, List<LongRunningProcessStatus>> res = mgr.stop(procIds, true);
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.containsKey(nodeId));
        Assert.assertEquals(RUNNING, res.get(nodeId).iterator().next().getState());
        Mockito.verify(igniteCompute).call(ArgumentMatchers.any(LongRunningProcessStopTask.class));
    }

    /**
     *
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        UUID nodeId = UUID.randomUUID();
        UUID procId = UUID.randomUUID();
        Ignite ignite = Mockito.mock(Ignite.class);
        IgniteCluster cluster = Mockito.mock(IgniteCluster.class);
        ClusterGroup clusterGrp = Mockito.mock(ClusterGroup.class);
        IgniteCompute igniteCompute = Mockito.mock(IgniteCompute.class);
        Mockito.doReturn(cluster).when(ignite).cluster();
        Mockito.doReturn(igniteCompute).when(ignite).compute(ArgumentMatchers.eq(clusterGrp));
        Mockito.doReturn(clusterGrp).when(cluster).forNodeId(ArgumentMatchers.eq(nodeId));
        Mockito.doReturn(Collections.singletonList(new LongRunningProcessStatus(LongRunningProcessState.RUNNING))).when(igniteCompute).call(ArgumentMatchers.any(IgniteCallable.class));
        Map<UUID, List<UUID>> procIds = new HashMap<>();
        procIds.put(nodeId, Collections.singletonList(procId));
        LongRunningProcessManager mgr = new LongRunningProcessManager(ignite);
        Map<UUID, List<LongRunningProcessStatus>> res = mgr.clear(procIds);
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.containsKey(nodeId));
        Assert.assertEquals(RUNNING, res.get(nodeId).iterator().next().getState());
        Mockito.verify(igniteCompute).call(ArgumentMatchers.any(LongRunningProcessClearTask.class));
    }
}

