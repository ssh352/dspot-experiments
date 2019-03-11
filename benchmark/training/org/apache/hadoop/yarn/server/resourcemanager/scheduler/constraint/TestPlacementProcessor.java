/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint;


import CapacitySchedulerConfiguration.ROOT;
import RejectionReason.COULD_NOT_PLACE_ON_NODE;
import RejectionReason.COULD_NOT_SCHEDULE_ON_NODE;
import YarnConfiguration.PROCESSOR_RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.RM_PLACEMENT_CONSTRAINTS_RETRY_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.RejectedSchedulingRequest;
import org.apache.hadoop.yarn.api.records.RejectionReason;
import org.apache.hadoop.yarn.api.records.SchedulingRequest;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint;
import org.apache.hadoop.yarn.api.resource.PlacementConstraints;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PlacementTargets.allocationTag;


/**
 * This tests end2end workflow of the constraint placement framework.
 */
public class TestPlacementProcessor {
    private static final int GB = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(TestPlacementProcessor.class);

    private MockRM rm;

    private DrainDispatcher dispatcher;

    @Test(timeout = 300000)
    public void testAntiAffinityPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")))));
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        // kick the scheduler
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 4);
        Assert.assertEquals(4, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes (antiaffinity)
        Assert.assertEquals(4, nodeIds.size());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 11264, 11, 5120, 5, 5);
    }

    @Test(timeout = 300000)
    public void testMutualAntiAffinityPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        MockNM nm5 = new MockNM("h5:1234", 4096, getResourceTrackerService());
        nodes.put(nm5.getNodeId(), nm5);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        nm5.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        Map<Set<String>, PlacementConstraint> pcMap = new HashMap<>();
        pcMap.put(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo"))));
        pcMap.put(Collections.singleton("bar"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo"))));
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, pcMap);
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        // kick the scheduler
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 5);
        Assert.assertEquals(5, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes (antiaffinity)
        Assert.assertEquals(5, nodeIds.size());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 14336, 14, 6144, 6, 6);
    }

    @Test(timeout = 300000)
    public void testCardinalityPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 8192, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 8192, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 8192, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 8192, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' should not exceed 4 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetCardinality(NODE, 0, 3, allocationTag("foo")))));
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 6, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 7, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 8, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        // kick the scheduler
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 8);
        Assert.assertEquals(8, allocatedContainers.size());
        Map<NodeId, Long> nodeIdContainerIdMap = allocatedContainers.stream().collect(Collectors.groupingBy(( c) -> c.getNodeId(), Collectors.counting()));
        // Ensure no more than 4 containers per node
        for (NodeId n : nodeIdContainerIdMap.keySet()) {
            Assert.assertTrue(((nodeIdContainerIdMap.get(n)) < 5));
        }
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 23552, 23, 9216, 9, 9);
    }

    @Test(timeout = 300000)
    public void testAffinityPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 8192, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 8192, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 8192, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 8192, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' should be placed where
        // containers with allocationTag 'bar' are already running
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetIn(NODE, allocationTag("bar")))));
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        // kick the scheduler
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 5);
        Assert.assertEquals(5, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure all containers end up on the same node (affinity)
        Assert.assertEquals(1, nodeIds.size());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 26624, 26, 6144, 6, 6);
    }

    @Test(timeout = 300000)
    public void testComplexPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        Map<Set<String>, PlacementConstraint> constraintMap = new HashMap<>();
        // Containers with allocationTag 'bar' should not exceed 1 per NODE
        constraintMap.put(Collections.singleton("bar"), PlacementConstraints.build(targetNotIn(NODE, allocationTag("bar"))));
        // Containers with allocationTag 'foo' should be placed where 'bar' exists
        constraintMap.put(Collections.singleton("foo"), PlacementConstraints.build(targetIn(NODE, allocationTag("bar"))));
        // Containers with allocationTag 'foo' should not exceed 2 per NODE
        constraintMap.put(Collections.singleton("foo"), PlacementConstraints.build(targetCardinality(NODE, 0, 1, allocationTag("foo"))));
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, constraintMap);
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 6, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        // kick the scheduler
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 6);
        Assert.assertEquals(6, allocatedContainers.size());
        Map<NodeId, Long> nodeIdContainerIdMap = allocatedContainers.stream().collect(Collectors.groupingBy(( c) -> c.getNodeId(), Collectors.counting()));
        // Ensure no more than 3 containers per node (1 'bar', 2 'foo')
        for (NodeId n : nodeIdContainerIdMap.keySet()) {
            Assert.assertTrue(((nodeIdContainerIdMap.get(n)) < 4));
        }
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 9216, 9, 7168, 7, 7);
    }

    @Test(timeout = 300000)
    public void testSchedulerRejection() throws Exception {
        stopRM();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ "a", "b" });
        csConf.setCapacity(((CapacitySchedulerConfiguration.ROOT) + ".a"), 15.0F);
        csConf.setCapacity(((CapacitySchedulerConfiguration.ROOT) + ".b"), 85.0F);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, PROCESSOR_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        startRM(conf);
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "a");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")))));
        am1.addSchedulingRequest(// Ask for a container larger than the node
        Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        List<RejectedSchedulingRequest> rejectedReqs = new ArrayList<>();
        int allocCount = 1;
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
        // kick the scheduler
        while (allocCount < 11) {
            nm1.nodeHeartbeat(true);
            nm2.nodeHeartbeat(true);
            nm3.nodeHeartbeat(true);
            nm4.nodeHeartbeat(true);
            TestPlacementProcessor.LOG.info("Waiting for containers to be created for app 1...");
            Thread.sleep(1000);
            allocResponse = am1.schedule();
            allocatedContainers.addAll(allocResponse.getAllocatedContainers());
            rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
            allocCount++;
            if (((rejectedReqs.size()) > 0) && ((allocatedContainers.size()) > 2)) {
                break;
            }
        } 
        Assert.assertEquals(3, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes
        Assert.assertEquals(3, nodeIds.size());
        RejectedSchedulingRequest rej = rejectedReqs.get(0);
        Assert.assertEquals(4, rej.getRequest().getAllocationRequestId());
        Assert.assertEquals(COULD_NOT_SCHEDULE_ON_NODE, rej.getReason());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 12288, 12, 4096, 4, 4);
    }

    @Test(timeout = 300000)
    public void testNodeCapacityRejection() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")))));
        am1.addSchedulingRequest(// Ask for a container larger than the node
        Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 5120, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        List<RejectedSchedulingRequest> rejectedReqs = new ArrayList<>();
        int allocCount = 1;
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
        // kick the scheduler
        while (allocCount < 11) {
            nm1.nodeHeartbeat(true);
            nm2.nodeHeartbeat(true);
            nm3.nodeHeartbeat(true);
            nm4.nodeHeartbeat(true);
            TestPlacementProcessor.LOG.info("Waiting for containers to be created for app 1...");
            Thread.sleep(1000);
            allocResponse = am1.schedule();
            allocatedContainers.addAll(allocResponse.getAllocatedContainers());
            rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
            allocCount++;
            if (((rejectedReqs.size()) > 0) && ((allocatedContainers.size()) > 2)) {
                break;
            }
        } 
        Assert.assertEquals(3, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes
        Assert.assertEquals(3, nodeIds.size());
        RejectedSchedulingRequest rej = rejectedReqs.get(0);
        Assert.assertEquals(4, rej.getRequest().getAllocationRequestId());
        Assert.assertEquals(COULD_NOT_PLACE_ON_NODE, rej.getReason());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 12288, 12, 4096, 4, 4);
    }

    @Test(timeout = 300000)
    public void testRePlacementAfterSchedulerRejection() throws Exception {
        stopRM();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, PROCESSOR_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        conf.setInt(RM_PLACEMENT_CONSTRAINTS_RETRY_ATTEMPTS, 2);
        startRM(conf);
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        MockNM nm5 = new MockNM("h5:1234", 8192, getResourceTrackerService());
        nodes.put(nm5.getNodeId(), nm5);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        // Do not register nm5 yet..
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")))));
        am1.addSchedulingRequest(// Ask for a container larger than the node
        Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 5120, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        List<RejectedSchedulingRequest> rejectedReqs = new ArrayList<>();
        int allocCount = 1;
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
        // Register node5 only after first allocate - so the initial placement
        // for the large schedReq goes to some other node..
        nm5.registerNode();
        // kick the scheduler
        while (allocCount < 11) {
            nm1.nodeHeartbeat(true);
            nm2.nodeHeartbeat(true);
            nm3.nodeHeartbeat(true);
            nm4.nodeHeartbeat(true);
            nm5.nodeHeartbeat(true);
            TestPlacementProcessor.LOG.info("Waiting for containers to be created for app 1...");
            Thread.sleep(1000);
            allocResponse = am1.schedule();
            allocatedContainers.addAll(allocResponse.getAllocatedContainers());
            rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
            allocCount++;
            if ((allocatedContainers.size()) > 3) {
                break;
            }
        } 
        Assert.assertEquals(4, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes
        Assert.assertEquals(4, nodeIds.size());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 15360, 19, 9216, 5, 5);
    }

    @Test(timeout = 300000)
    public void testPlacementRejection() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Containers with allocationTag 'foo' are restricted to 1 per NODE
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, Collections.singletonMap(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")))));
        am1.addSchedulingRequest(// Ask for more containers than nodes
        Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 5, 1, 512, "foo")));
        AllocateResponse allocResponse = am1.schedule();// send the request

        List<Container> allocatedContainers = new ArrayList<>();
        List<RejectedSchedulingRequest> rejectedReqs = new ArrayList<>();
        int allocCount = 1;
        allocatedContainers.addAll(allocResponse.getAllocatedContainers());
        rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
        // kick the scheduler
        while (allocCount < 11) {
            nm1.nodeHeartbeat(true);
            nm2.nodeHeartbeat(true);
            nm3.nodeHeartbeat(true);
            nm4.nodeHeartbeat(true);
            TestPlacementProcessor.LOG.info("Waiting for containers to be created for app 1...");
            Thread.sleep(1000);
            allocResponse = am1.schedule();
            allocatedContainers.addAll(allocResponse.getAllocatedContainers());
            rejectedReqs.addAll(allocResponse.getRejectedSchedulingRequests());
            allocCount++;
            if (((rejectedReqs.size()) > 0) && ((allocatedContainers.size()) > 3)) {
                break;
            }
        } 
        Assert.assertEquals(4, allocatedContainers.size());
        Set<NodeId> nodeIds = allocatedContainers.stream().map(( x) -> x.getNodeId()).collect(Collectors.toSet());
        // Ensure unique nodes
        Assert.assertEquals(4, nodeIds.size());
        RejectedSchedulingRequest rej = rejectedReqs.get(0);
        Assert.assertEquals(RejectionReason.COULD_NOT_PLACE_ON_NODE, rej.getReason());
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        // Verify Metrics
        TestPlacementProcessor.verifyMetrics(metrics, 11264, 11, 5120, 5, 5);
    }

    @Test(timeout = 300000)
    public void testAndOrPlacement() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 40960, 100, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 40960, 100, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 40960, 100, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 40960, 100, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (TestPlacementProcessor.GB)), "app", "user", null, "default");
        // Register app1 with following constraints
        // 1) foo anti-affinity with foo on node
        // 2) bar anti-affinity with foo on node AND maxCardinality = 2
        // 3) moo affinity with foo OR bar
        Map<Set<String>, PlacementConstraint> app1Constraints = new HashMap<>();
        app1Constraints.put(Collections.singleton("foo"), PlacementConstraints.build(PlacementConstraints.targetNotIn(NODE, allocationTag("foo"))));
        app1Constraints.put(Collections.singleton("bar"), PlacementConstraints.build(PlacementConstraints.and(PlacementConstraints.targetNotIn(NODE, allocationTag("foo")), PlacementConstraints.maxCardinality(NODE, 2, "bar"))));
        app1Constraints.put(Collections.singleton("moo"), PlacementConstraints.build(PlacementConstraints.or(PlacementConstraints.targetIn(NODE, allocationTag("foo")), PlacementConstraints.targetIn(NODE, allocationTag("bar")))));
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2, app1Constraints);
        // Allocates 3 foo containers on 3 different nodes,
        // in anti-affinity fashion.
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "foo"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "foo")));
        List<Container> allocatedContainers = new ArrayList<>();
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 3);
        TestPlacementProcessor.printTags(nodes.values(), getRMContext().getAllocationTagsManager());
        Assert.assertEquals(3, allocatedContainers.size());
        /**
         * Testing AND placement constraint*
         */
        // Now allocates a bar container, as restricted by the AND constraint,
        // bar could be only allocated to the node without foo
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 1, 1, 512, "bar")));
        allocatedContainers.clear();
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 1);
        TestPlacementProcessor.printTags(nodes.values(), getRMContext().getAllocationTagsManager());
        Assert.assertEquals(1, allocatedContainers.size());
        NodeId barNode = allocatedContainers.get(0).getNodeId();
        // Sends another 3 bar request, 2 of them can be allocated
        // as maxCardinality is 2, for placed containers, they should be all
        // on the node where the last bar was placed.
        allocatedContainers.clear();
        List<RejectedSchedulingRequest> rejectedContainers = new ArrayList<>();
        am1.addSchedulingRequest(Arrays.asList(TestPlacementProcessor.schedulingRequest(1, 2, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 3, 1, 512, "bar"), TestPlacementProcessor.schedulingRequest(1, 4, 1, 512, "bar")));
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, rejectedContainers, 2);
        TestPlacementProcessor.printTags(nodes.values(), getRMContext().getAllocationTagsManager());
        Assert.assertEquals(2, allocatedContainers.size());
        Assert.assertTrue(allocatedContainers.stream().allMatch(( container) -> container.getNodeId().equals(barNode)));
        // The third request could not be satisfied because it violates
        // the cardinality constraint. Validate rejected request correctly
        // capture this.
        Assert.assertEquals(1, rejectedContainers.size());
        Assert.assertEquals(RejectionReason.COULD_NOT_PLACE_ON_NODE, rejectedContainers.get(0).getReason());
        /**
         * Testing OR placement constraint*
         */
        // Register one more NM for testing
        MockNM nm5 = new MockNM("h5:1234", 4096, 100, getResourceTrackerService());
        nodes.put(nm5.getNodeId(), nm5);
        nm5.registerNode();
        nm5.nodeHeartbeat(true);
        List<SchedulingRequest> mooRequests = new ArrayList<>();
        for (int i = 5; i < 25; i++) {
            mooRequests.add(TestPlacementProcessor.schedulingRequest(1, i, 1, 100, "moo"));
        }
        am1.addSchedulingRequest(mooRequests);
        allocatedContainers.clear();
        TestPlacementProcessor.waitForContainerAllocation(nodes.values(), am1, allocatedContainers, new ArrayList(), 20);
        // All 20 containers should be allocated onto nodes besides nm5,
        // because moo affinity to foo or bar which only exists on rest of nodes.
        Assert.assertEquals(20, allocatedContainers.size());
        for (Container mooContainer : allocatedContainers) {
            // nm5 has no moo allocated containers.
            Assert.assertFalse(mooContainer.getNodeId().equals(nm5.getNodeId()));
        }
    }
}

