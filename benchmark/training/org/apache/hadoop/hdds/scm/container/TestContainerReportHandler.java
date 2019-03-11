/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdds.scm.container;


import ContainerReplicaProto.State.OPEN;
import LifeCycleEvent.CLOSE;
import LifeCycleEvent.FORCE_CLOSE;
import LifeCycleEvent.QUASI_CLOSE;
import LifeCycleState.CLOSED;
import LifeCycleState.CLOSING;
import LifeCycleState.QUASI_CLOSED;
import NodeState.HEALTHY;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.ContainerReportsProto;
import org.apache.hadoop.hdds.scm.container.replication.ReplicationActivityStatus;
import org.apache.hadoop.hdds.scm.node.NodeManager;
import org.apache.hadoop.hdds.scm.node.states.NodeNotFoundException;
import org.apache.hadoop.hdds.scm.pipeline.PipelineManager;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.ContainerReportFromDatanode;
import org.apache.hadoop.hdds.server.events.EventPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Test the behaviour of the ContainerReportHandler.
 */
public class TestContainerReportHandler {
    @Test
    public void testUnderReplicatedContainer() throws ContainerNotFoundException, ContainerReplicaNotFoundException, NodeNotFoundException {
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(CLOSED);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            Object[] args = invocation.getArguments();
            if (args[0].equals(containerOne.containerID())) {
                ContainerReplica replica = ((ContainerReplica) (args[1]));
                containerOneReplicas.remove(replica);
            }
            return null;
        }))).when(containerManager).removeContainerReplica(Mockito.any(ContainerID.class), Mockito.any(ContainerReplica.class));
        Mockito.when(containerManager.getContainerReplicas(containerOne.containerID())).thenReturn(containerOneReplicas);
        Mockito.when(containerManager.getContainerReplicas(containerTwo.containerID())).thenReturn(containerTwoReplicas);
        // SCM expects both containerOne and containerTwo to be in all the three
        // datanodes datanodeOne, datanodeTwo and datanodeThree
        // Now datanodeOne is sending container report in which containerOne is
        // missing.
        // containerOne becomes under replicated.
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        // Now we should get a replication request for containerOne
        Mockito.verify(publisher, Mockito.times(1)).fireEvent(Mockito.any(), Mockito.any());
        // TODO: verify whether are actually getting a replication request event
        // for containerOne
    }

    @Test
    public void testOverReplicatedContainer() throws ContainerNotFoundException, NodeNotFoundException {
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final DatanodeDetails datanodeFour = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(CLOSED);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            Object[] args = invocation.getArguments();
            if (args[0].equals(containerOne.containerID())) {
                containerOneReplicas.add(((ContainerReplica) (args[1])));
            }
            return null;
        }))).when(containerManager).updateContainerReplica(Mockito.any(ContainerID.class), Mockito.any(ContainerReplica.class));
        // SCM expects both containerOne and containerTwo to be in all the three
        // datanodes datanodeOne, datanodeTwo and datanodeThree
        // Now datanodeFour is sending container report which has containerOne.
        // containerOne becomes over replicated.
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeFour, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        Mockito.verify(publisher, Mockito.times(1)).fireEvent(Mockito.any(), Mockito.any());
        // TODO: verify whether are actually getting a replication request event
        // for containerOne
    }

    @Test
    public void testOpenToClosing() throws ContainerNotFoundException, NodeNotFoundException {
        /* The container is in CLOSING state and all the replicas are either in
        OPEN or CLOSING state.

        The datanode reports that the replica is still in OPEN state.

        In this case SCM should trigger close container event to the datanode.
         */
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(CLOSING);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), OPEN, datanodeOne);
        containerOneReplicas.addAll(TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSING, datanodeTwo, datanodeThree));
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, containerOne, containerOneReplicas);
        // Replica in datanodeOne of containerOne is in OPEN state.
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), OPEN, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        // Now we should get close container event for containerOne on datanodeOne
        Mockito.verify(publisher, Mockito.times(1)).fireEvent(Mockito.any(), Mockito.any());
        // TODO: verify whether are actually getting a close container
        // datanode command for containerOne/datanodeOne
        /* The container is in CLOSING state and all the replicas are either in
        OPEN or CLOSING state.

        The datanode reports that the replica is in CLOSING state.

        In this case SCM should trigger close container event to the datanode.
         */
        // Replica in datanodeOne of containerOne is in OPEN state.
        final ContainerReportsProto containerReportTwo = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), OPEN, datanodeOne.getUuidString());
        final ContainerReportFromDatanode containerReportTwoFromDatanode = new ContainerReportFromDatanode(datanodeOne, containerReportTwo);
        reportHandler.onMessage(containerReportTwoFromDatanode, publisher);
        // Now we should get close container event for containerOne on datanodeOne
        Mockito.verify(publisher, Mockito.times(2)).fireEvent(Mockito.any(), Mockito.any());
        // TODO: verify whether are actually getting a close container
        // datanode command for containerOne/datanodeOne
    }

    @Test
    public void testClosingToClosed() throws IOException, NodeNotFoundException {
        /* The container is in CLOSING state and all the replicas are in
        OPEN/CLOSING state.

        The datanode reports that one of the replica is now CLOSED.

        In this case SCM should mark the container as CLOSED.
         */
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(CLOSING);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSING, datanodeOne);
        containerOneReplicas.addAll(TestContainerReportHelper.getReplicas(containerOne.containerID(), OPEN, datanodeTwo, datanodeThree));
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, containerOne, CLOSE, CLOSED);
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        Assert.assertEquals(CLOSED, containerOne.getState());
    }

    @Test
    public void testClosingToQuasiClosed() throws IOException, NodeNotFoundException {
        /* The container is in CLOSING state and all the replicas are in
        OPEN/CLOSING state.

        The datanode reports that the replica is now QUASI_CLOSED.

        In this case SCM should move the container to QUASI_CLOSED.
         */
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(CLOSING);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSING, datanodeOne, datanodeTwo);
        containerOneReplicas.addAll(TestContainerReportHelper.getReplicas(containerOne.containerID(), OPEN, datanodeThree));
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, containerOne, QUASI_CLOSE, QUASI_CLOSED);
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        Assert.assertEquals(QUASI_CLOSED, containerOne.getState());
    }

    @Test
    public void testQuasiClosedWithDifferentOriginNodeReplica() throws IOException, NodeNotFoundException {
        /* The container is in QUASI_CLOSED state.
         - One of the replica is in QUASI_CLOSED state
         - The other two replica are in OPEN/CLOSING state

        The datanode reports the second replica is now QUASI_CLOSED.

        In this case SCM should CLOSE the container with highest BCSID and
        send force close command to the datanode.
         */
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(QUASI_CLOSED);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, 10000L, datanodeOne);
        containerOneReplicas.addAll(TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSING, datanodeTwo, datanodeThree));
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, containerIDSet);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, containerOne, FORCE_CLOSE, CLOSED);
        // Container replica with datanodeOne as originNodeId is already
        // QUASI_CLOSED. Now we will tell SCM that container replica from
        // datanodeTwo is also QUASI_CLOSED, but has higher sequenceId.
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeTwo.getUuidString(), 999999L);
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeTwo, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        // Now we should get force close container event for containerOne on
        // datanodeTwo
        Mockito.verify(publisher, Mockito.times(1)).fireEvent(Mockito.any(), Mockito.any());
        // TODO: verify whether are actually getting a force close container
        // datanode command for containerOne/datanodeTwo
        // The sequence id of the container should have been updated.
        Assert.assertEquals(999999L, containerOne.getSequenceId());
        // Now datanodeTwo should close containerOne.
        final ContainerReportsProto containerReportTwo = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.CLOSED, datanodeTwo.getUuidString(), 999999L);
        final ContainerReportFromDatanode containerReportFromDatanodeTwo = new ContainerReportFromDatanode(datanodeTwo, containerReportTwo);
        reportHandler.onMessage(containerReportFromDatanodeTwo, publisher);
        // The container should be closed in SCM now.
        Assert.assertEquals(CLOSED, containerOne.getState());
    }

    @Test
    public void testQuasiClosedWithSameOriginNodeReplica() throws IOException, NodeNotFoundException {
        /* The container is in QUASI_CLOSED state.
         - One of the replica is in QUASI_CLOSED state
         - The other two replica are in OPEN/CLOSING state

        The datanode reports a QUASI_CLOSED replica which has the same
        origin node id as the existing QUASI_CLOSED replica.

        In this case SCM should not CLOSE the container.
         */
        final NodeManager nodeManager = new MockNodeManager(true, 10);
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final ReplicationActivityStatus replicationActivityStatus = new ReplicationActivityStatus();
        replicationActivityStatus.enableReplication();
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final ContainerReportHandler reportHandler = new ContainerReportHandler(nodeManager, pipelineManager, containerManager, replicationActivityStatus);
        final Iterator<DatanodeDetails> nodeIterator = nodeManager.getNodes(HEALTHY).iterator();
        final DatanodeDetails datanodeOne = nodeIterator.next();
        final DatanodeDetails datanodeTwo = nodeIterator.next();
        final DatanodeDetails datanodeThree = nodeIterator.next();
        final ContainerInfo containerOne = TestContainerReportHelper.getContainer(QUASI_CLOSED);
        final ContainerInfo containerTwo = TestContainerReportHelper.getContainer(CLOSED);
        final Set<ContainerID> containerIDSet = Stream.of(containerOne.containerID(), containerTwo.containerID()).collect(Collectors.toSet());
        final Set<ContainerReplica> containerOneReplicas = TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeOne);
        containerOneReplicas.addAll(TestContainerReportHelper.getReplicas(containerOne.containerID(), ContainerReplicaProto.State.CLOSING, datanodeTwo));
        final Set<ContainerReplica> containerTwoReplicas = TestContainerReportHelper.getReplicas(containerTwo.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne, datanodeTwo, datanodeThree);
        nodeManager.setContainers(datanodeOne, containerIDSet);
        nodeManager.setContainers(datanodeTwo, containerIDSet);
        nodeManager.setContainers(datanodeThree, Collections.singleton(containerTwo.containerID()));
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, containerTwo, containerTwoReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, containerOne, containerOneReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, containerOne, FORCE_CLOSE, CLOSED);
        // containerOne is QUASI_CLOSED in datanodeOne and CLOSING in datanodeTwo.
        // Now datanodeThree is sending container report which says that it has
        // containerOne replica, but the originNodeId of this replica is
        // datanodeOne. In this case we should not force close the container even
        // though we got two QUASI_CLOSED replicas.
        final ContainerReportsProto containerReport = TestContainerReportHandler.getContainerReportsProto(containerOne.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final ContainerReportFromDatanode containerReportFromDatanode = new ContainerReportFromDatanode(datanodeThree, containerReport);
        reportHandler.onMessage(containerReportFromDatanode, publisher);
        Mockito.verify(publisher, Mockito.times(0)).fireEvent(Mockito.any(), Mockito.any());
    }
}

