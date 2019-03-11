/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.chillmode;


import HddsConfigKeys.HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK;
import HddsConfigKeys.OZONE_METADATA_DIRS;
import HddsProtos.LifeCycleState.CLOSED;
import HddsProtos.LifeCycleState.OPEN;
import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import PipelineReportsProto.Builder;
import SCMEvents.NODE_REGISTRATION_CONT_REPORT;
import SCMEvents.PROCESSED_PIPELINE_REPORT;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.PipelineReport;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.PipelineReportsProto;
import org.apache.hadoop.hdds.scm.HddsTestUtils;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.MockNodeManager;
import org.apache.hadoop.hdds.scm.pipeline.MockRatisPipelineProvider;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineProvider;
import org.apache.hadoop.hdds.scm.pipeline.SCMPipelineManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test class for SCMChillModeManager.
 */
public class TestSCMChillModeManager {
    private static EventQueue queue;

    private SCMChillModeManager scmChillModeManager;

    private static Configuration config;

    private List<ContainerInfo> containers;

    @Rule
    public Timeout timeout = new Timeout((1000 * 35));

    @Test
    public void testChillModeState() throws Exception {
        // Test 1: test for 0 containers
        testChillMode(0);
        // Test 2: test for 20 containers
        testChillMode(20);
    }

    @Test
    public void testChillModeStateWithNullContainers() {
        new SCMChillModeManager(TestSCMChillModeManager.config, null, null, TestSCMChillModeManager.queue);
    }

    @Test
    public void testChillModeExitRule() throws Exception {
        containers = new ArrayList();
        containers.addAll(HddsTestUtils.getContainerInfo((25 * 4)));
        // Assign open state to containers to be included in the chill mode
        // container list
        for (ContainerInfo container : containers) {
            container.setState(CLOSED);
        }
        scmChillModeManager = new SCMChillModeManager(TestSCMChillModeManager.config, containers, null, TestSCMChillModeManager.queue);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        testContainerThreshold(containers.subList(0, 25), 0.25);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        testContainerThreshold(containers.subList(25, 50), 0.5);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        testContainerThreshold(containers.subList(50, 75), 0.75);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        testContainerThreshold(containers.subList(75, 100), 1.0);
        GenericTestUtils.waitFor(() -> {
            return !(scmChillModeManager.getInChillMode());
        }, 100, (1000 * 5));
    }

    @Test
    public void testChillModeDataNodeExitRule() throws Exception {
        containers = new ArrayList();
        testChillModeDataNodes(0);
        testChillModeDataNodes(3);
        testChillModeDataNodes(5);
    }

    /**
     * Check that containers in Allocated state are not considered while
     * computing percentage of containers with at least 1 reported replica in
     * chill mode exit rule.
     */
    @Test
    public void testContainerChillModeRule() throws Exception {
        containers = new ArrayList();
        // Add 100 containers to the list of containers in SCM
        containers.addAll(HddsTestUtils.getContainerInfo((25 * 4)));
        // Assign CLOSED state to first 25 containers and OPEM state to rest
        // of the containers
        for (ContainerInfo container : containers.subList(0, 25)) {
            container.setState(CLOSED);
        }
        for (ContainerInfo container : containers.subList(25, 100)) {
            container.setState(OPEN);
        }
        scmChillModeManager = new SCMChillModeManager(TestSCMChillModeManager.config, containers, null, TestSCMChillModeManager.queue);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        // When 10 CLOSED containers are reported by DNs, the computed container
        // threshold should be 10/25 as there are only 25 CLOSED containers.
        // Containers in OPEN state should not contribute towards list of
        // containers while calculating container threshold in SCMChillNodeManager
        testContainerThreshold(containers.subList(0, 10), 0.4);
        Assert.assertTrue(scmChillModeManager.getInChillMode());
        // When remaining 15 OPEN containers are reported by DNs, the container
        // threshold should be (10+15)/25.
        testContainerThreshold(containers.subList(10, 25), 1.0);
        GenericTestUtils.waitFor(() -> {
            return !(scmChillModeManager.getInChillMode());
        }, 100, (1000 * 5));
    }

    @Test
    public void testChillModePipelineExitRule() throws Exception {
        containers = new ArrayList();
        containers.addAll(HddsTestUtils.getContainerInfo((25 * 4)));
        String storageDir = GenericTestUtils.getTempPath(((TestSCMChillModeManager.class.getName()) + (UUID.randomUUID())));
        try {
            MockNodeManager nodeManager = new MockNodeManager(true, 3);
            TestSCMChillModeManager.config.set(OZONE_METADATA_DIRS, storageDir);
            // enable pipeline check
            TestSCMChillModeManager.config.setBoolean(HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK, true);
            SCMPipelineManager pipelineManager = new SCMPipelineManager(TestSCMChillModeManager.config, nodeManager, TestSCMChillModeManager.queue);
            PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(nodeManager, pipelineManager.getStateManager(), TestSCMChillModeManager.config);
            pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
            Pipeline pipeline = pipelineManager.createPipeline(RATIS, THREE);
            PipelineReportsProto.Builder reportBuilder = PipelineReportsProto.newBuilder();
            reportBuilder.addPipelineReport(PipelineReport.newBuilder().setPipelineID(pipeline.getId().getProtobuf()));
            scmChillModeManager = new SCMChillModeManager(TestSCMChillModeManager.config, containers, pipelineManager, TestSCMChillModeManager.queue);
            TestSCMChillModeManager.queue.fireEvent(NODE_REGISTRATION_CONT_REPORT, HddsTestUtils.createNodeRegistrationContainerReport(containers));
            Assert.assertTrue(scmChillModeManager.getInChillMode());
            // Trigger the processed pipeline report event
            TestSCMChillModeManager.queue.fireEvent(PROCESSED_PIPELINE_REPORT, new org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.PipelineReportFromDatanode(pipeline.getNodes().get(0), reportBuilder.build()));
            GenericTestUtils.waitFor(() -> {
                return !(scmChillModeManager.getInChillMode());
            }, 100, (1000 * 10));
            pipelineManager.close();
        } finally {
            TestSCMChillModeManager.config.setBoolean(HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK, false);
            FileUtil.fullyDelete(new File(storageDir));
        }
    }
}

