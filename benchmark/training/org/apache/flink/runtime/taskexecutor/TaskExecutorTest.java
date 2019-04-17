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
package org.apache.flink.runtime.taskexecutor;


import ResourceProfile.UNKNOWN;
import TaskManagerOptions.REGISTRATION_TIMEOUT;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.memory.MemoryType;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.blob.BlobCacheService;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.runtime.clusterframework.types.SlotID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.entrypoint.ClusterInformation;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.execution.librarycache.LibraryCacheManager;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.JobInformation;
import org.apache.flink.runtime.executiongraph.TaskInformation;
import org.apache.flink.runtime.heartbeat.HeartbeatListener;
import org.apache.flink.runtime.heartbeat.HeartbeatManager;
import org.apache.flink.runtime.heartbeat.HeartbeatManagerImpl;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.heartbeat.HeartbeatTarget;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.instance.HardwareDescription;
import org.apache.flink.runtime.instance.InstanceID;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsync;
import org.apache.flink.runtime.io.network.NetworkEnvironment;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.netty.PartitionProducerStateChecker;
import org.apache.flink.runtime.io.network.partition.NoOpResultPartitionConsumableNotifier;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmaster.JMTMRegistrationSuccess;
import org.apache.flink.runtime.jobmaster.JobMasterGateway;
import org.apache.flink.runtime.jobmaster.JobMasterId;
import org.apache.flink.runtime.jobmaster.utils.TestingJobMasterGateway;
import org.apache.flink.runtime.jobmaster.utils.TestingJobMasterGatewayBuilder;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalListener;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.registration.RetryingRegistrationConfiguration;
import org.apache.flink.runtime.resourcemanager.ResourceManagerGateway;
import org.apache.flink.runtime.resourcemanager.ResourceManagerId;
import org.apache.flink.runtime.resourcemanager.utils.TestingResourceManagerGateway;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.state.TaskExecutorLocalStateStoresManager;
import org.apache.flink.runtime.taskexecutor.exceptions.RegistrationTimeoutException;
import org.apache.flink.runtime.taskexecutor.exceptions.TaskManagerException;
import org.apache.flink.runtime.taskexecutor.slot.SlotOffer;
import org.apache.flink.runtime.taskexecutor.slot.TaskSlotTable;
import org.apache.flink.runtime.taskexecutor.slot.TimerService;
import org.apache.flink.runtime.taskmanager.CheckpointResponder;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.NoOpTaskManagerActions;
import org.apache.flink.runtime.taskmanager.Task;
import org.apache.flink.runtime.taskmanager.TaskExecutionState;
import org.apache.flink.runtime.taskmanager.TaskManagerActions;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.apache.flink.util.function.FunctionUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;


/**
 * Tests for the {@link TaskExecutor}.
 */
public class TaskExecutorTest extends TestLogger {
    public static final HeartbeatServices HEARTBEAT_SERVICES = new HeartbeatServices(1000L, 1000L);

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public final TestName testName = new TestName();

    private static final Time timeout = Time.milliseconds(10000L);

    private TestingRpcService rpc;

    private BlobCacheService dummyBlobCacheService;

    private TimerService<AllocationID> timerService;

    private Configuration configuration;

    private TaskManagerConfiguration taskManagerConfiguration;

    private TaskManagerLocation taskManagerLocation;

    private JobID jobId;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    private TestingHighAvailabilityServices haServices;

    private SettableLeaderRetrievalService resourceManagerLeaderRetriever;

    private SettableLeaderRetrievalService jobManagerLeaderRetriever;

    @Test
    public void testShouldShutDownTaskManagerServicesInPostStop() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final JobLeaderService jobLeaderService = new JobLeaderService(taskManagerLocation, RetryingRegistrationConfiguration.defaultConfiguration());
        final IOManager ioManager = new IOManagerAsync(tmp.newFolder().getAbsolutePath());
        final TaskExecutorLocalStateStoresManager localStateStoresManager = new TaskExecutorLocalStateStoresManager(false, ioManager.getSpillingDirectories(), Executors.directExecutor());
        final MemoryManager memoryManager = new MemoryManager(4096, 1, 4096, MemoryType.HEAP, false);
        final NetworkEnvironment networkEnvironment = new NetworkEnvironment(1, 1, 0, 0, 2, 8, true);
        networkEnvironment.start();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setMemoryManager(memoryManager).setIoManager(ioManager).setNetworkEnvironment(networkEnvironment).setTaskSlotTable(taskSlotTable).setJobLeaderService(jobLeaderService).setTaskStateManager(localStateStoresManager).build();
        final long heartbeatInterval = 1000L;
        final long heartbeatTimeout = 1000L;
        final HeartbeatServices heartbeatServices = new HeartbeatServices(heartbeatInterval, heartbeatTimeout);
        final TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, heartbeatServices, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
        Assert.assertThat(memoryManager.isShutdown(), Matchers.is(true));
        Assert.assertThat(networkEnvironment.isShutdown(), Matchers.is(true));
        Assert.assertThat(ioManager.isProperlyShutDown(), Matchers.is(true));
    }

    @Test
    public void testHeartbeatTimeoutWithJobManager() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Arrays.asList(UNKNOWN), timerService);
        final JobLeaderService jobLeaderService = new JobLeaderService(taskManagerLocation, RetryingRegistrationConfiguration.defaultConfiguration());
        final long heartbeatInterval = 1L;
        final long heartbeatTimeout = 3L;
        HeartbeatServices heartbeatServices = new HeartbeatServices(heartbeatInterval, heartbeatTimeout);
        final String jobMasterAddress = "jm";
        final UUID jmLeaderId = UUID.randomUUID();
        final ResourceID jmResourceId = ResourceID.generate();
        final CountDownLatch registrationAttempts = new CountDownLatch(2);
        final CompletableFuture<TaskManagerLocation> taskManagerLocationFuture = new CompletableFuture<>();
        final CompletableFuture<ResourceID> disconnectTaskManagerFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setRegisterTaskManagerFunction(( s, taskManagerLocation) -> {
            registrationAttempts.countDown();
            taskManagerLocationFuture.complete(taskManagerLocation);
            return CompletableFuture.completedFuture(new JMTMRegistrationSuccess(jmResourceId));
        }).setDisconnectTaskManagerFunction(( resourceID) -> {
            disconnectTaskManagerFuture.complete(resourceID);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).build();
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setJobLeaderService(jobLeaderService).setTaskStateManager(localStateStoresManager).build();
        final TestingTaskExecutor taskManager = new TestingTaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, heartbeatServices, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            start();
            taskManager.waitUntilStarted();
            rpc.registerGateway(jobMasterAddress, jobMasterGateway);
            // we have to add the job after the TaskExecutor, because otherwise the service has not
            // been properly started.
            jobLeaderService.addJob(jobId, jobMasterAddress);
            // now inform the task manager about the new job leader
            jobManagerLeaderRetriever.notifyListener(jobMasterAddress, jmLeaderId);
            // register task manager success will trigger monitoring heartbeat target between tm and jm
            final TaskManagerLocation taskManagerLocation1 = taskManagerLocationFuture.get(TaskExecutorTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.assertThat(taskManagerLocation1, Matchers.equalTo(taskManagerLocation));
            // the timeout should trigger disconnecting from the JobManager
            final ResourceID resourceID = disconnectTaskManagerFuture.get((heartbeatTimeout * 50L), TimeUnit.MILLISECONDS);
            Assert.assertThat(resourceID, Matchers.equalTo(taskManagerLocation.getResourceID()));
            Assert.assertTrue("The TaskExecutor should try to reconnect to the JM", registrationAttempts.await(TaskExecutorTest.timeout.toMilliseconds(), TimeUnit.SECONDS));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    @Test
    public void testHeartbeatTimeoutWithResourceManager() throws Exception {
        final String rmAddress = "rm";
        final ResourceID rmResourceId = new ResourceID(rmAddress);
        final long heartbeatInterval = 1L;
        final long heartbeatTimeout = 3L;
        final ResourceManagerId rmLeaderId = ResourceManagerId.generate();
        TestingResourceManagerGateway rmGateway = new TestingResourceManagerGateway(rmLeaderId, rmResourceId, rmAddress, rmAddress);
        final TaskExecutorRegistrationSuccess registrationResponse = new TaskExecutorRegistrationSuccess(new InstanceID(), rmResourceId, new ClusterInformation("localhost", 1234));
        final CompletableFuture<ResourceID> taskExecutorRegistrationFuture = new CompletableFuture<>();
        final CountDownLatch registrationAttempts = new CountDownLatch(2);
        rmGateway.setRegisterTaskExecutorFunction(( registration) -> {
            taskExecutorRegistrationFuture.complete(registration.f1);
            registrationAttempts.countDown();
            return CompletableFuture.completedFuture(registrationResponse);
        });
        final CompletableFuture<ResourceID> taskExecutorDisconnectFuture = new CompletableFuture<>();
        rmGateway.setDisconnectTaskExecutorConsumer(( disconnectInfo) -> taskExecutorDisconnectFuture.complete(disconnectInfo.f0));
        rpc.registerGateway(rmAddress, rmGateway);
        final TaskSlotTable taskSlotTable = Mockito.mock(TaskSlotTable.class);
        final SlotReport slotReport = new SlotReport();
        Mockito.when(taskSlotTable.createSlotReport(ArgumentMatchers.any(ResourceID.class))).thenReturn(slotReport);
        HeartbeatServices heartbeatServices = new HeartbeatServices(heartbeatInterval, heartbeatTimeout);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        final TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, heartbeatServices, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            // define a leader and see that a registration happens
            resourceManagerLeaderRetriever.notifyListener(rmAddress, rmLeaderId.toUUID());
            // register resource manager success will trigger monitoring heartbeat target between tm and rm
            Assert.assertThat(taskExecutorRegistrationFuture.get(TaskExecutorTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS), Matchers.equalTo(taskManagerLocation.getResourceID()));
            // heartbeat timeout should trigger disconnect TaskManager from ResourceManager
            Assert.assertThat(taskExecutorDisconnectFuture.get((heartbeatTimeout * 50L), TimeUnit.MILLISECONDS), Matchers.equalTo(taskManagerLocation.getResourceID()));
            Assert.assertTrue("The TaskExecutor should try to reconnect to the RM", registrationAttempts.await(TaskExecutorTest.timeout.toMilliseconds(), TimeUnit.SECONDS));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the correct slot report is sent as part of the heartbeat response.
     */
    @Test
    public void testHeartbeatSlotReporting() throws Exception {
        final String rmAddress = "rm";
        final UUID rmLeaderId = UUID.randomUUID();
        // register the mock resource manager gateway
        final TestingResourceManagerGateway rmGateway = new TestingResourceManagerGateway();
        final CompletableFuture<ResourceID> taskExecutorRegistrationFuture = new CompletableFuture<>();
        final ResourceID rmResourceId = rmGateway.getOwnResourceId();
        final CompletableFuture<RegistrationResponse> registrationResponse = CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), rmResourceId, new ClusterInformation("localhost", 1234)));
        rmGateway.setRegisterTaskExecutorFunction(( stringResourceIDIntegerHardwareDescriptionTuple4) -> {
            taskExecutorRegistrationFuture.complete(stringResourceIDIntegerHardwareDescriptionTuple4.f1);
            return registrationResponse;
        });
        final CompletableFuture<SlotReport> initialSlotReportFuture = new CompletableFuture<>();
        rmGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
            initialSlotReportFuture.complete(resourceIDInstanceIDSlotReportTuple3.f2);
            return CompletableFuture.completedFuture(Acknowledge.get());
        });
        final CompletableFuture<SlotReport> heartbeatSlotReportFuture = new CompletableFuture<>();
        rmGateway.setTaskExecutorHeartbeatConsumer(( resourceID, slotReport) -> heartbeatSlotReportFuture.complete(slotReport));
        rpc.registerGateway(rmAddress, rmGateway);
        final SlotID slotId = new SlotID(taskManagerLocation.getResourceID(), 0);
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotReport slotReport1 = new SlotReport(new SlotStatus(slotId, resourceProfile));
        final SlotReport slotReport2 = new SlotReport(new SlotStatus(slotId, resourceProfile, new JobID(), new AllocationID()));
        final TaskExecutorTest.TestingTaskSlotTable taskSlotTable = new TaskExecutorTest.TestingTaskSlotTable(new java.util.ArrayDeque(Arrays.asList(slotReport1, slotReport2)));
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        final TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            // define a leader and see that a registration happens
            resourceManagerLeaderRetriever.notifyListener(rmAddress, rmLeaderId);
            // register resource manager success will trigger monitoring heartbeat target between tm and rm
            Assert.assertThat(taskExecutorRegistrationFuture.get(), Matchers.equalTo(taskManagerLocation.getResourceID()));
            Assert.assertThat(initialSlotReportFuture.get(), Matchers.equalTo(slotReport1));
            TaskExecutorGateway taskExecutorGateway = taskManager.getSelfGateway(TaskExecutorGateway.class);
            // trigger the heartbeat asynchronously
            taskExecutorGateway.heartbeatFromResourceManager(rmResourceId);
            // wait for heartbeat response
            SlotReport actualSlotReport = heartbeatSlotReportFuture.get();
            // the new slot report should be reported
            Assert.assertEquals(slotReport2, actualSlotReport);
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    @Test
    public void testImmediatelyRegistersIfLeaderIsKnown() throws Exception {
        final String resourceManagerAddress = "/resource/manager/address/one";
        final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
        final CountDownLatch taskManagerRegisteredLatch = new CountDownLatch(1);
        testingResourceManagerGateway.setRegisterTaskExecutorFunction(FunctionUtils.uncheckedFunction(( ignored) -> {
            taskManagerRegisteredLatch.countDown();
            return CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), new ResourceID(resourceManagerAddress), new ClusterInformation("localhost", 1234)));
        }));
        rpc.registerGateway(resourceManagerAddress, testingResourceManagerGateway);
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        final TaskExecutor taskManager = createTaskExecutor(taskManagerServices);
        try {
            taskManager.start();
            resourceManagerLeaderRetriever.notifyListener(resourceManagerAddress, UUID.randomUUID());
            Assert.assertTrue(taskManagerRegisteredLatch.await(TaskExecutorTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    @Test
    public void testTriggerRegistrationOnLeaderChange() throws Exception {
        final String address1 = "/resource/manager/address/one";
        final String address2 = "/resource/manager/address/two";
        final UUID leaderId1 = UUID.randomUUID();
        final UUID leaderId2 = UUID.randomUUID();
        final ResourceID rmResourceId1 = new ResourceID(address1);
        final ResourceID rmResourceId2 = new ResourceID(address2);
        // register the mock resource manager gateways
        ResourceManagerGateway rmGateway1 = Mockito.mock(ResourceManagerGateway.class);
        ResourceManagerGateway rmGateway2 = Mockito.mock(ResourceManagerGateway.class);
        Mockito.when(rmGateway1.registerTaskExecutor(ArgumentMatchers.anyString(), ArgumentMatchers.any(ResourceID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(HardwareDescription.class), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), rmResourceId1, new ClusterInformation("localhost", 1234))));
        Mockito.when(rmGateway2.registerTaskExecutor(ArgumentMatchers.anyString(), ArgumentMatchers.any(ResourceID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(HardwareDescription.class), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), rmResourceId2, new ClusterInformation("localhost", 1234))));
        rpc.registerGateway(address1, rmGateway1);
        rpc.registerGateway(address2, rmGateway2);
        final TaskSlotTable taskSlotTable = Mockito.mock(TaskSlotTable.class);
        final SlotReport slotReport = new SlotReport();
        Mockito.when(taskSlotTable.createSlotReport(ArgumentMatchers.any(ResourceID.class))).thenReturn(slotReport);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            String taskManagerAddress = taskManager.getAddress();
            // no connection initially, since there is no leader
            Assert.assertNull(taskManager.getResourceManagerConnection());
            // define a leader and see that a registration happens
            resourceManagerLeaderRetriever.notifyListener(address1, leaderId1);
            Mockito.verify(rmGateway1, Mockito.timeout(TaskExecutorTest.timeout.toMilliseconds())).registerTaskExecutor(ArgumentMatchers.eq(taskManagerAddress), ArgumentMatchers.eq(taskManagerLocation.getResourceID()), ArgumentMatchers.anyInt(), ArgumentMatchers.any(HardwareDescription.class), ArgumentMatchers.any(Time.class));
            Assert.assertNotNull(taskManager.getResourceManagerConnection());
            // cancel the leader
            resourceManagerLeaderRetriever.notifyListener(null, null);
            // set a new leader, see that a registration happens
            resourceManagerLeaderRetriever.notifyListener(address2, leaderId2);
            Mockito.verify(rmGateway2, Mockito.timeout(TaskExecutorTest.timeout.toMilliseconds())).registerTaskExecutor(ArgumentMatchers.eq(taskManagerAddress), ArgumentMatchers.eq(taskManagerLocation.getResourceID()), ArgumentMatchers.anyInt(), ArgumentMatchers.any(HardwareDescription.class), ArgumentMatchers.any(Time.class));
            Assert.assertNotNull(taskManager.getResourceManagerConnection());
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that we can submit a task to the TaskManager given that we've allocated a slot there.
     */
    @Test(timeout = 10000L)
    public void testTaskSubmission() throws Exception {
        final AllocationID allocationId = new AllocationID();
        final JobMasterId jobMasterId = JobMasterId.generate();
        final JobVertexID jobVertexId = new JobVertexID();
        JobInformation jobInformation = new JobInformation(jobId, testName.getMethodName(), new SerializedValue(new ExecutionConfig()), new Configuration(), Collections.emptyList(), Collections.emptyList());
        TaskInformation taskInformation = new TaskInformation(jobVertexId, "test task", 1, 1, TaskExecutorTest.TestInvokable.class.getName(), new Configuration());
        SerializedValue<JobInformation> serializedJobInformation = new SerializedValue(jobInformation);
        SerializedValue<TaskInformation> serializedJobVertexInformation = new SerializedValue(taskInformation);
        final TaskDeploymentDescriptor tdd = new TaskDeploymentDescriptor(jobId, new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobInformation), new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobVertexInformation), new ExecutionAttemptID(), allocationId, 0, 0, 0, null, Collections.emptyList(), Collections.emptyList());
        final LibraryCacheManager libraryCacheManager = Mockito.mock(LibraryCacheManager.class);
        Mockito.when(libraryCacheManager.getClassLoader(ArgumentMatchers.any(JobID.class))).thenReturn(ClassLoader.getSystemClassLoader());
        final JobMasterGateway jobMasterGateway = Mockito.mock(JobMasterGateway.class);
        Mockito.when(jobMasterGateway.getFencingToken()).thenReturn(jobMasterId);
        final OneShotLatch taskInTerminalState = new OneShotLatch();
        final TaskManagerActions taskManagerActions = new NoOpTaskManagerActions() {
            @Override
            public void updateTaskExecutionState(TaskExecutionState taskExecutionState) {
                if (taskExecutionState.getExecutionState().isTerminal()) {
                    taskInTerminalState.trigger();
                }
            }
        };
        final JobManagerConnection jobManagerConnection = new JobManagerConnection(jobId, ResourceID.generate(), jobMasterGateway, taskManagerActions, Mockito.mock(CheckpointResponder.class), new TestGlobalAggregateManager(), libraryCacheManager, new NoOpResultPartitionConsumableNotifier(), Mockito.mock(PartitionProducerStateChecker.class));
        final JobManagerTable jobManagerTable = new JobManagerTable();
        jobManagerTable.put(jobId, jobManagerConnection);
        final TaskSlotTable taskSlotTable = Mockito.mock(TaskSlotTable.class);
        Mockito.when(taskSlotTable.tryMarkSlotActive(ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(allocationId))).thenReturn(true);
        Mockito.when(taskSlotTable.addTask(ArgumentMatchers.any(Task.class))).thenReturn(true);
        TaskEventDispatcher taskEventDispatcher = new TaskEventDispatcher();
        final NetworkEnvironment networkEnvironment = Mockito.mock(NetworkEnvironment.class);
        Mockito.when(networkEnvironment.createKvStateTaskRegistry(ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(jobVertexId))).thenReturn(Mockito.mock(TaskKvStateRegistry.class));
        Mockito.when(networkEnvironment.getTaskEventDispatcher()).thenReturn(taskEventDispatcher);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setNetworkEnvironment(networkEnvironment).setTaskSlotTable(taskSlotTable).setJobManagerTable(jobManagerTable).setTaskStateManager(localStateStoresManager).build();
        TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            final TaskExecutorGateway tmGateway = taskManager.getSelfGateway(TaskExecutorGateway.class);
            tmGateway.submitTask(tdd, jobMasterId, TaskExecutorTest.timeout);
            CompletableFuture<Boolean> completionFuture = TaskExecutorTest.TestInvokable.COMPLETABLE_FUTURE;
            completionFuture.get();
            taskInTerminalState.await();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * Test invokable which completes the given future when executed.
     */
    public static class TestInvokable extends AbstractInvokable {
        static final CompletableFuture<Boolean> COMPLETABLE_FUTURE = new CompletableFuture<>();

        public TestInvokable(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            TaskExecutorTest.TestInvokable.COMPLETABLE_FUTURE.complete(true);
        }
    }

    /**
     * Tests that a TaskManager detects a job leader for which it has reserved slots. Upon detecting
     * the job leader, it will offer all reserved slots to the JobManager.
     */
    @Test
    public void testJobLeaderDetection() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final JobManagerTable jobManagerTable = new JobManagerTable();
        final JobLeaderService jobLeaderService = new JobLeaderService(taskManagerLocation, RetryingRegistrationConfiguration.defaultConfiguration());
        final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();
        CompletableFuture<Void> initialSlotReportFuture = new CompletableFuture<>();
        resourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
            initialSlotReportFuture.complete(null);
            return CompletableFuture.completedFuture(Acknowledge.get());
        });
        final CompletableFuture<Collection<SlotOffer>> offeredSlotsFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setOfferSlotsFunction(( resourceID, slotOffers) -> {
            offeredSlotsFuture.complete(new java.util.ArrayList(slotOffers));
            return CompletableFuture.completedFuture(slotOffers);
        }).build();
        rpc.registerGateway(resourceManagerGateway.getAddress(), resourceManagerGateway);
        rpc.registerGateway(jobMasterGateway.getAddress(), jobMasterGateway);
        final AllocationID allocationId = new AllocationID();
        final SlotID slotId = new SlotID(taskManagerLocation.getResourceID(), 0);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setJobManagerTable(jobManagerTable).setJobLeaderService(jobLeaderService).setTaskStateManager(localStateStoresManager).build();
        TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            final TaskExecutorGateway tmGateway = taskManager.getSelfGateway(TaskExecutorGateway.class);
            // tell the task manager about the rm leader
            resourceManagerLeaderRetriever.notifyListener(resourceManagerGateway.getAddress(), resourceManagerGateway.getFencingToken().toUUID());
            // wait for the initial slot report
            initialSlotReportFuture.get();
            // request slots from the task manager under the given allocation id
            CompletableFuture<Acknowledge> slotRequestAck = tmGateway.requestSlot(slotId, jobId, allocationId, jobMasterGateway.getAddress(), resourceManagerGateway.getFencingToken(), TaskExecutorTest.timeout);
            slotRequestAck.get();
            // now inform the task manager about the new job leader
            jobManagerLeaderRetriever.notifyListener(jobMasterGateway.getAddress(), jobMasterGateway.getFencingToken().toUUID());
            final Collection<SlotOffer> offeredSlots = offeredSlotsFuture.get();
            final Collection<AllocationID> allocationIds = offeredSlots.stream().map(SlotOffer::getAllocationId).collect(Collectors.toList());
            Assert.assertThat(allocationIds, Matchers.containsInAnyOrder(allocationId));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that accepted slots go into state assigned and the others are returned to the resource
     * manager.
     */
    @Test
    public void testSlotAcceptance() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Arrays.asList(Mockito.mock(ResourceProfile.class), Mockito.mock(ResourceProfile.class)), timerService);
        final JobManagerTable jobManagerTable = new JobManagerTable();
        final JobLeaderService jobLeaderService = new JobLeaderService(taskManagerLocation, RetryingRegistrationConfiguration.defaultConfiguration());
        final String resourceManagerAddress = "rm";
        final UUID resourceManagerLeaderId = UUID.randomUUID();
        final String jobManagerAddress = "jm";
        final UUID jobManagerLeaderId = UUID.randomUUID();
        resourceManagerLeaderRetriever.notifyListener(resourceManagerAddress, resourceManagerLeaderId);
        jobManagerLeaderRetriever.notifyListener(jobManagerAddress, jobManagerLeaderId);
        final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();
        final ResourceID resourceManagerResourceId = resourceManagerGateway.getOwnResourceId();
        final InstanceID registrationId = new InstanceID();
        final CompletableFuture<ResourceID> registrationFuture = new CompletableFuture<>();
        resourceManagerGateway.setRegisterTaskExecutorFunction(( stringResourceIDIntegerHardwareDescriptionTuple4) -> {
            registrationFuture.complete(stringResourceIDIntegerHardwareDescriptionTuple4.f1);
            return CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(registrationId, resourceManagerResourceId, new ClusterInformation("localhost", 1234)));
        });
        final CompletableFuture<Tuple3<InstanceID, SlotID, AllocationID>> availableSlotFuture = new CompletableFuture<>();
        resourceManagerGateway.setNotifySlotAvailableConsumer(availableSlotFuture::complete);
        final ResourceID jmResourceId = new ResourceID(jobManagerAddress);
        final AllocationID allocationId1 = new AllocationID();
        final AllocationID allocationId2 = new AllocationID();
        final SlotOffer offer1 = new SlotOffer(allocationId1, 0, ResourceProfile.UNKNOWN);
        final JobMasterGateway jobMasterGateway = Mockito.mock(JobMasterGateway.class);
        Mockito.when(jobMasterGateway.registerTaskManager(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(taskManagerLocation), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(new JMTMRegistrationSuccess(jmResourceId)));
        Mockito.when(jobMasterGateway.getHostname()).thenReturn(jobManagerAddress);
        Mockito.when(jobMasterGateway.offerSlots(ArgumentMatchers.any(ResourceID.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(((Collection<SlotOffer>) (Collections.singleton(offer1)))));
        rpc.registerGateway(resourceManagerAddress, resourceManagerGateway);
        rpc.registerGateway(jobManagerAddress, jobMasterGateway);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setJobManagerTable(jobManagerTable).setJobLeaderService(jobLeaderService).setTaskStateManager(localStateStoresManager).build();
        TaskExecutor taskManager = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskManager.start();
            Assert.assertThat(registrationFuture.get(), Matchers.equalTo(taskManagerLocation.getResourceID()));
            taskSlotTable.allocateSlot(0, jobId, allocationId1, Time.milliseconds(10000L));
            taskSlotTable.allocateSlot(1, jobId, allocationId2, Time.milliseconds(10000L));
            // we have to add the job after the TaskExecutor, because otherwise the service has not
            // been properly started.
            jobLeaderService.addJob(jobId, jobManagerAddress);
            final Tuple3<InstanceID, SlotID, AllocationID> instanceIDSlotIDAllocationIDTuple3 = availableSlotFuture.get();
            final Tuple3<InstanceID, SlotID, AllocationID> expectedResult = Tuple3.of(registrationId, new SlotID(taskManagerLocation.getResourceID(), 1), allocationId2);
            Assert.assertThat(instanceIDSlotIDAllocationIDTuple3, Matchers.equalTo(expectedResult));
            Assert.assertTrue(taskSlotTable.tryMarkSlotActive(jobId, allocationId1));
            Assert.assertFalse(taskSlotTable.tryMarkSlotActive(jobId, allocationId2));
            Assert.assertTrue(taskSlotTable.isSlotFree(1));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * This tests task executor receive SubmitTask before OfferSlot response.
     */
    @Test
    public void testSubmitTaskBeforeAcceptSlot() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Arrays.asList(Mockito.mock(ResourceProfile.class), Mockito.mock(ResourceProfile.class)), timerService);
        final JobManagerTable jobManagerTable = new JobManagerTable();
        final JobLeaderService jobLeaderService = new JobLeaderService(taskManagerLocation, RetryingRegistrationConfiguration.defaultConfiguration());
        final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();
        resourceManagerLeaderRetriever.notifyListener(resourceManagerGateway.getAddress(), resourceManagerGateway.getFencingToken().toUUID());
        final CompletableFuture<Tuple3<InstanceID, SlotID, AllocationID>> availableSlotFuture = new CompletableFuture<>();
        resourceManagerGateway.setNotifySlotAvailableConsumer(availableSlotFuture::complete);
        final AllocationID allocationId1 = new AllocationID();
        final AllocationID allocationId2 = new AllocationID();
        final SlotOffer offer1 = new SlotOffer(allocationId1, 0, ResourceProfile.UNKNOWN);
        final OneShotLatch offerSlotsLatch = new OneShotLatch();
        final OneShotLatch taskInTerminalState = new OneShotLatch();
        final CompletableFuture<Collection<SlotOffer>> offerResultFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setOfferSlotsFunction(( resourceID, slotOffers) -> {
            offerSlotsLatch.trigger();
            return offerResultFuture;
        }).setUpdateTaskExecutionStateFunction(( taskExecutionState) -> {
            if (taskExecutionState.getExecutionState().isTerminal()) {
                taskInTerminalState.trigger();
            }
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).build();
        jobManagerLeaderRetriever.notifyListener(jobMasterGateway.getAddress(), jobMasterGateway.getFencingToken().toUUID());
        rpc.registerGateway(resourceManagerGateway.getAddress(), resourceManagerGateway);
        rpc.registerGateway(jobMasterGateway.getAddress(), jobMasterGateway);
        final NetworkEnvironment networkMock = Mockito.mock(NetworkEnvironment.class, Mockito.RETURNS_MOCKS);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setNetworkEnvironment(networkMock).setTaskSlotTable(taskSlotTable).setJobLeaderService(jobLeaderService).setJobManagerTable(jobManagerTable).setTaskStateManager(localStateStoresManager).build();
        final TestingTaskExecutor taskManager = new TestingTaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            start();
            taskManager.waitUntilStarted();
            final TaskExecutorGateway tmGateway = getSelfGateway(TaskExecutorGateway.class);
            taskSlotTable.allocateSlot(0, jobId, allocationId1, Time.milliseconds(10000L));
            taskSlotTable.allocateSlot(1, jobId, allocationId2, Time.milliseconds(10000L));
            final JobVertexID jobVertexId = new JobVertexID();
            JobInformation jobInformation = new JobInformation(jobId, testName.getMethodName(), new SerializedValue(new ExecutionConfig()), new Configuration(), Collections.emptyList(), Collections.emptyList());
            TaskInformation taskInformation = new TaskInformation(jobVertexId, "test task", 1, 1, NoOpInvokable.class.getName(), new Configuration());
            SerializedValue<JobInformation> serializedJobInformation = new SerializedValue(jobInformation);
            SerializedValue<TaskInformation> serializedJobVertexInformation = new SerializedValue(taskInformation);
            final TaskDeploymentDescriptor tdd = new TaskDeploymentDescriptor(jobId, new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobInformation), new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobVertexInformation), new ExecutionAttemptID(), allocationId1, 0, 0, 0, null, Collections.emptyList(), Collections.emptyList());
            // we have to add the job after the TaskExecutor, because otherwise the service has not
            // been properly started. This will also offer the slots to the job master
            jobLeaderService.addJob(jobId, jobMasterGateway.getAddress());
            offerSlotsLatch.await();
            // submit the task without having acknowledge the offered slots
            tmGateway.submitTask(tdd, jobMasterGateway.getFencingToken(), TaskExecutorTest.timeout).get();
            // acknowledge the offered slots
            offerResultFuture.complete(Collections.singleton(offer1));
            final Tuple3<InstanceID, SlotID, AllocationID> instanceIDSlotIDAllocationIDTuple3 = availableSlotFuture.get();
            Assert.assertThat(instanceIDSlotIDAllocationIDTuple3.f1, Matchers.equalTo(new SlotID(taskManagerLocation.getResourceID(), 1)));
            Assert.assertTrue(taskSlotTable.tryMarkSlotActive(jobId, allocationId1));
            Assert.assertFalse(taskSlotTable.tryMarkSlotActive(jobId, allocationId2));
            Assert.assertTrue(taskSlotTable.isSlotFree(1));
            // wait for the task completion
            taskInTerminalState.await();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskManager, TaskExecutorTest.timeout);
        }
    }

    /**
     * This tests makes sure that duplicate JobMaster gained leadership messages are filtered out
     * by the TaskExecutor. See FLINK-7526.
     */
    @Test
    public void testFilterOutDuplicateJobMasterRegistrations() throws Exception {
        final long verificationTimeout = 500L;
        final JobLeaderService jobLeaderService = Mockito.mock(JobLeaderService.class);
        final HeartbeatServices heartbeatServicesMock = Mockito.mock(HeartbeatServices.class, Mockito.RETURNS_MOCKS);
        final JobMasterGateway jobMasterGateway = Mockito.mock(JobMasterGateway.class);
        Mockito.when(jobMasterGateway.getHostname()).thenReturn("localhost");
        final JMTMRegistrationSuccess registrationMessage = new JMTMRegistrationSuccess(ResourceID.generate());
        final JobManagerTable jobManagerTableMock = Mockito.spy(new JobManagerTable());
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setJobManagerTable(jobManagerTableMock).setJobLeaderService(jobLeaderService).setTaskStateManager(localStateStoresManager).build();
        final TaskExecutor taskExecutor = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, heartbeatServicesMock, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskExecutor.start();
            ArgumentCaptor<JobLeaderListener> jobLeaderListenerArgumentCaptor = ArgumentCaptor.forClass(JobLeaderListener.class);
            Mockito.verify(jobLeaderService).start(ArgumentMatchers.anyString(), ArgumentMatchers.any(RpcService.class), ArgumentMatchers.any(HighAvailabilityServices.class), jobLeaderListenerArgumentCaptor.capture());
            JobLeaderListener taskExecutorListener = jobLeaderListenerArgumentCaptor.getValue();
            taskExecutorListener.jobManagerGainedLeadership(jobId, jobMasterGateway, registrationMessage);
            // duplicate job manager gained leadership message
            taskExecutorListener.jobManagerGainedLeadership(jobId, jobMasterGateway, registrationMessage);
            ArgumentCaptor<JobManagerConnection> jobManagerConnectionArgumentCaptor = ArgumentCaptor.forClass(JobManagerConnection.class);
            Mockito.verify(jobManagerTableMock, Mockito.timeout(verificationTimeout).times(1)).put(ArgumentMatchers.eq(jobId), jobManagerConnectionArgumentCaptor.capture());
            JobManagerConnection jobManagerConnection = jobManagerConnectionArgumentCaptor.getValue();
            Assert.assertEquals(jobMasterGateway, jobManagerConnection.getJobManagerGateway());
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the heartbeat is stopped once the TaskExecutor detects that the RM is no longer leader.
     *
     * <p>See FLINK-8462
     */
    @Test
    public void testRMHeartbeatStopWhenLeadershipRevoked() throws Exception {
        final long heartbeatInterval = 1L;
        final long heartbeatTimeout = 10000L;
        final long pollTimeout = 1000L;
        final TaskExecutorTest.RecordingHeartbeatServices heartbeatServices = new TaskExecutorTest.RecordingHeartbeatServices(heartbeatInterval, heartbeatTimeout);
        final ResourceID rmResourceID = ResourceID.generate();
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final String rmAddress = "rm";
        final TestingResourceManagerGateway rmGateway = new TestingResourceManagerGateway(ResourceManagerId.generate(), rmResourceID, rmAddress, rmAddress);
        rpc.registerGateway(rmAddress, rmGateway);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        final TaskExecutor taskExecutor = new TaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, heartbeatServices, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            taskExecutor.start();
            final BlockingQueue<ResourceID> unmonitoredTargets = heartbeatServices.getUnmonitoredTargets();
            final BlockingQueue<ResourceID> monitoredTargets = heartbeatServices.getMonitoredTargets();
            resourceManagerLeaderRetriever.notifyListener(rmAddress, rmGateway.getFencingToken().toUUID());
            // wait for TM registration by checking the registered heartbeat targets
            Assert.assertThat(monitoredTargets.poll(pollTimeout, TimeUnit.MILLISECONDS), Matchers.equalTo(rmResourceID));
            // let RM lose leadership
            resourceManagerLeaderRetriever.notifyListener(null, null);
            // the timeout should not have triggered since it is much higher
            Assert.assertThat(unmonitoredTargets.poll(pollTimeout, TimeUnit.MILLISECONDS), Matchers.equalTo(rmResourceID));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that a job is removed from the JobLeaderService once a TaskExecutor has
     * no more slots assigned to this job.
     *
     * <p>See FLINK-8504
     */
    @Test
    public void testRemoveJobFromJobLeaderService() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskExecutorLocalStateStoresManager localStateStoresManager = createTaskExecutorLocalStateStoresManager();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskManagerLocation(taskManagerLocation).setTaskSlotTable(taskSlotTable).setTaskStateManager(localStateStoresManager).build();
        final TestingTaskExecutor taskExecutor = new TestingTaskExecutor(rpc, taskManagerConfiguration, haServices, taskManagerServices, TaskExecutorTest.HEARTBEAT_SERVICES, UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        try {
            final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();
            final CompletableFuture<Void> initialSlotReport = new CompletableFuture<>();
            resourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
                initialSlotReport.complete(null);
                return CompletableFuture.completedFuture(Acknowledge.get());
            });
            final ResourceManagerId resourceManagerId = resourceManagerGateway.getFencingToken();
            rpc.registerGateway(resourceManagerGateway.getAddress(), resourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(resourceManagerGateway.getAddress(), resourceManagerId.toUUID());
            final CompletableFuture<LeaderRetrievalListener> startFuture = new CompletableFuture<>();
            final CompletableFuture<Void> stopFuture = new CompletableFuture<>();
            final TaskExecutorTest.StartStopNotifyingLeaderRetrievalService jobMasterLeaderRetriever = new TaskExecutorTest.StartStopNotifyingLeaderRetrievalService(startFuture, stopFuture);
            haServices.setJobMasterLeaderRetriever(jobId, jobMasterLeaderRetriever);
            start();
            taskExecutor.waitUntilStarted();
            final TaskExecutorGateway taskExecutorGateway = getSelfGateway(TaskExecutorGateway.class);
            final SlotID slotId = new SlotID(taskManagerLocation.getResourceID(), 0);
            final AllocationID allocationId = new AllocationID();
            Assert.assertThat(startFuture.isDone(), Matchers.is(false));
            final JobLeaderService jobLeaderService = taskManagerServices.getJobLeaderService();
            Assert.assertThat(jobLeaderService.containsJob(jobId), Matchers.is(false));
            // wait for the initial slot report
            initialSlotReport.get();
            taskExecutorGateway.requestSlot(slotId, jobId, allocationId, "foobar", resourceManagerId, TaskExecutorTest.timeout).get();
            // wait until the job leader retrieval service for jobId is started
            startFuture.get();
            Assert.assertThat(jobLeaderService.containsJob(jobId), Matchers.is(true));
            taskExecutorGateway.freeSlot(allocationId, new FlinkException("Test exception"), TaskExecutorTest.timeout).get();
            // wait that the job leader retrieval service for jobId stopped becaue it should get removed
            stopFuture.get();
            Assert.assertThat(jobLeaderService.containsJob(jobId), Matchers.is(false));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    @Test
    public void testMaximumRegistrationDuration() throws Exception {
        configuration.setString(REGISTRATION_TIMEOUT, "10 ms");
        final TaskExecutor taskExecutor = createTaskExecutor(new TaskManagerServicesBuilder().build());
        taskExecutor.start();
        try {
            final Throwable error = testingFatalErrorHandler.getErrorFuture().get();
            Assert.assertThat(error, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(ExceptionUtils.stripExecutionException(error), Matchers.instanceOf(RegistrationTimeoutException.class));
            testingFatalErrorHandler.clearError();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    @Test
    public void testMaximumRegistrationDurationAfterConnectionLoss() throws Exception {
        configuration.setString(REGISTRATION_TIMEOUT, "100 ms");
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final long heartbeatInterval = 10L;
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).build();
        final TaskExecutor taskExecutor = new TaskExecutor(rpc, TaskManagerConfiguration.fromConfiguration(configuration), haServices, taskManagerServices, new HeartbeatServices(heartbeatInterval, 10L), UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        taskExecutor.start();
        final CompletableFuture<ResourceID> registrationFuture = new CompletableFuture<>();
        final OneShotLatch secondRegistration = new OneShotLatch();
        try {
            final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
            testingResourceManagerGateway.setRegisterTaskExecutorFunction(( tuple) -> {
                if (registrationFuture.complete(tuple.f1)) {
                    return CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), testingResourceManagerGateway.getOwnResourceId(), new ClusterInformation("localhost", 1234)));
                } else {
                    secondRegistration.trigger();
                    return CompletableFuture.completedFuture(new RegistrationResponse.Decline("Only the first registration should succeed."));
                }
            });
            rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), UUID.randomUUID());
            final ResourceID registrationResourceId = registrationFuture.get();
            Assert.assertThat(registrationResourceId, Matchers.equalTo(taskManagerServices.getTaskManagerLocation().getResourceID()));
            secondRegistration.await();
            final Throwable error = testingFatalErrorHandler.getErrorFuture().get();
            Assert.assertThat(error, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(ExceptionUtils.stripExecutionException(error), Matchers.instanceOf(RegistrationTimeoutException.class));
            testingFatalErrorHandler.clearError();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that we ignore slot requests if the TaskExecutor is not
     * registered at a ResourceManager.
     */
    @Test
    public void testIgnoringSlotRequestsIfNotRegistered() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).build();
        final TaskExecutor taskExecutor = createTaskExecutor(taskManagerServices);
        taskExecutor.start();
        try {
            final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
            final CompletableFuture<RegistrationResponse> registrationFuture = new CompletableFuture<>();
            final CompletableFuture<ResourceID> taskExecutorResourceIdFuture = new CompletableFuture<>();
            testingResourceManagerGateway.setRegisterTaskExecutorFunction(( stringResourceIDSlotReportIntegerHardwareDescriptionTuple5) -> {
                taskExecutorResourceIdFuture.complete(stringResourceIDSlotReportIntegerHardwareDescriptionTuple5.f1);
                return registrationFuture;
            });
            rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway.getFencingToken().toUUID());
            final TaskExecutorGateway taskExecutorGateway = taskExecutor.getSelfGateway(TaskExecutorGateway.class);
            final ResourceID resourceId = taskExecutorResourceIdFuture.get();
            final SlotID slotId = new SlotID(resourceId, 0);
            final CompletableFuture<Acknowledge> slotRequestResponse = taskExecutorGateway.requestSlot(slotId, jobId, new AllocationID(), "foobar", testingResourceManagerGateway.getFencingToken(), TaskExecutorTest.timeout);
            try {
                slotRequestResponse.get();
                Assert.fail("We should not be able to request slots before the TaskExecutor is registered at the ResourceManager.");
            } catch (ExecutionException ee) {
                Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.instanceOf(TaskManagerException.class));
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the TaskExecutor tries to reconnect to a ResourceManager from which it
     * was explicitly disconnected.
     */
    @Test
    public void testReconnectionAttemptIfExplicitlyDisconnected() throws Exception {
        final long heartbeatInterval = 1000L;
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
        final TaskExecutor taskExecutor = new TaskExecutor(rpc, TaskManagerConfiguration.fromConfiguration(configuration), haServices, new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).setTaskManagerLocation(taskManagerLocation).build(), new HeartbeatServices(heartbeatInterval, 1000L), UnregisteredMetricGroups.createUnregisteredTaskManagerMetricGroup(), null, dummyBlobCacheService, testingFatalErrorHandler);
        taskExecutor.start();
        try {
            final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
            final ClusterInformation clusterInformation = new ClusterInformation("foobar", 1234);
            final CompletableFuture<RegistrationResponse> registrationResponseFuture = CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), ResourceID.generate(), clusterInformation));
            final BlockingQueue<ResourceID> registrationQueue = new ArrayBlockingQueue<>(1);
            testingResourceManagerGateway.setRegisterTaskExecutorFunction(( stringResourceIDSlotReportIntegerHardwareDescriptionTuple5) -> {
                registrationQueue.offer(stringResourceIDSlotReportIntegerHardwareDescriptionTuple5.f1);
                return registrationResponseFuture;
            });
            rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway.getFencingToken().toUUID());
            final ResourceID firstRegistrationAttempt = registrationQueue.take();
            Assert.assertThat(firstRegistrationAttempt, Matchers.equalTo(taskManagerLocation.getResourceID()));
            final TaskExecutorGateway taskExecutorGateway = taskExecutor.getSelfGateway(TaskExecutorGateway.class);
            Assert.assertThat(registrationQueue, Matchers.is(Matchers.empty()));
            taskExecutorGateway.disconnectResourceManager(new FlinkException("Test exception"));
            final ResourceID secondRegistrationAttempt = registrationQueue.take();
            Assert.assertThat(secondRegistrationAttempt, Matchers.equalTo(taskManagerLocation.getResourceID()));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the {@link TaskExecutor} sends the initial slot report after it
     * registered at the ResourceManager.
     */
    @Test
    public void testInitialSlotReport() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).setTaskManagerLocation(taskManagerLocation).build();
        final TaskExecutor taskExecutor = createTaskExecutor(taskManagerServices);
        taskExecutor.start();
        try {
            final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
            final CompletableFuture<ResourceID> initialSlotReportFuture = new CompletableFuture<>();
            testingResourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
                initialSlotReportFuture.complete(resourceIDInstanceIDSlotReportTuple3.f0);
                return CompletableFuture.completedFuture(Acknowledge.get());
            });
            rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway.getFencingToken().toUUID());
            Assert.assertThat(initialSlotReportFuture.get(), Matchers.equalTo(taskManagerLocation.getResourceID()));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the {@link TaskExecutor} tries to reconnect if the initial slot report
     * fails.
     */
    @Test
    public void testInitialSlotReportFailure() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Collections.singleton(UNKNOWN), timerService);
        final TaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).setTaskManagerLocation(taskManagerLocation).build();
        final TaskExecutor taskExecutor = createTaskExecutor(taskManagerServices);
        taskExecutor.start();
        try {
            final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
            final BlockingQueue<CompletableFuture<Acknowledge>> responseQueue = new ArrayBlockingQueue<>(2);
            testingResourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
                try {
                    return responseQueue.take();
                } catch (InterruptedException e) {
                    return FutureUtils.completedExceptionally(e);
                }
            });
            final CompletableFuture<RegistrationResponse> registrationResponse = CompletableFuture.completedFuture(new TaskExecutorRegistrationSuccess(new InstanceID(), testingResourceManagerGateway.getOwnResourceId(), new ClusterInformation("foobar", 1234)));
            final CountDownLatch numberRegistrations = new CountDownLatch(2);
            testingResourceManagerGateway.setRegisterTaskExecutorFunction(new Function<Tuple4<String, ResourceID, Integer, HardwareDescription>, CompletableFuture<RegistrationResponse>>() {
                @Override
                public CompletableFuture<RegistrationResponse> apply(Tuple4<String, ResourceID, Integer, HardwareDescription> stringResourceIDIntegerHardwareDescriptionTuple4) {
                    numberRegistrations.countDown();
                    return registrationResponse;
                }
            });
            responseQueue.offer(FutureUtils.completedExceptionally(new FlinkException("Test exception")));
            responseQueue.offer(CompletableFuture.completedFuture(Acknowledge.get()));
            rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
            resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway.getFencingToken().toUUID());
            // wait for the second registration attempt
            numberRegistrations.await();
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that offers slots to job master timeout and retry.
     */
    @Test
    public void testOfferSlotToJobMasterAfterTimeout() throws Exception {
        final TaskSlotTable taskSlotTable = new TaskSlotTable(Arrays.asList(UNKNOWN, UNKNOWN), timerService);
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(taskSlotTable).build();
        final TaskExecutor taskExecutor = createTaskExecutor(taskManagerServices);
        final AllocationID allocationId = new AllocationID();
        final CompletableFuture<ResourceID> initialSlotReportFuture = new CompletableFuture<>();
        final TestingResourceManagerGateway testingResourceManagerGateway = new TestingResourceManagerGateway();
        testingResourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
            initialSlotReportFuture.complete(null);
            return CompletableFuture.completedFuture(Acknowledge.get());
        });
        rpc.registerGateway(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway);
        resourceManagerLeaderRetriever.notifyListener(testingResourceManagerGateway.getAddress(), testingResourceManagerGateway.getFencingToken().toUUID());
        final CountDownLatch slotOfferings = new CountDownLatch(3);
        final CompletableFuture<AllocationID> offeredSlotFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setOfferSlotsFunction(( resourceID, slotOffers) -> {
            Assert.assertThat(slotOffers.size(), Matchers.is(1));
            slotOfferings.countDown();
            if ((slotOfferings.getCount()) == 0) {
                offeredSlotFuture.complete(slotOffers.iterator().next().getAllocationId());
                return CompletableFuture.completedFuture(slotOffers);
            } else {
                return FutureUtils.completedExceptionally(new TimeoutException());
            }
        }).build();
        final String jobManagerAddress = jobMasterGateway.getAddress();
        rpc.registerGateway(jobManagerAddress, jobMasterGateway);
        jobManagerLeaderRetriever.notifyListener(jobManagerAddress, jobMasterGateway.getFencingToken().toUUID());
        try {
            taskExecutor.start();
            final TaskExecutorGateway taskExecutorGateway = taskExecutor.getSelfGateway(TaskExecutorGateway.class);
            // wait for the connection to the ResourceManager
            initialSlotReportFuture.get();
            taskExecutorGateway.requestSlot(new SlotID(taskExecutor.getResourceID(), 0), jobId, allocationId, jobManagerAddress, testingResourceManagerGateway.getFencingToken(), TaskExecutorTest.timeout).get();
            slotOfferings.await();
            Assert.assertThat(offeredSlotFuture.get(), Matchers.is(allocationId));
            Assert.assertTrue(taskSlotTable.isSlotFree(1));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    /**
     * Tests that the TaskExecutor disconnects from the JobMaster if a new leader
     * is detected.
     */
    @Test
    public void testDisconnectFromJobMasterWhenNewLeader() throws Exception {
        final TaskManagerServices taskManagerServices = new TaskManagerServicesBuilder().setTaskSlotTable(new TaskSlotTable(Collections.singleton(UNKNOWN), timerService)).build();
        final TaskExecutor taskExecutor = createTaskExecutor(taskManagerServices);
        final CompletableFuture<Integer> offeredSlotsFuture = new CompletableFuture<>();
        final CompletableFuture<ResourceID> disconnectFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setOfferSlotsFunction(( resourceID, slotOffers) -> {
            offeredSlotsFuture.complete(slotOffers.size());
            return CompletableFuture.completedFuture(slotOffers);
        }).setDisconnectTaskManagerFunction(( resourceID) -> {
            disconnectFuture.complete(resourceID);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).build();
        final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();
        final CompletableFuture<Void> initialSlotReportFuture = new CompletableFuture<>();
        resourceManagerGateway.setSendSlotReportFunction(( resourceIDInstanceIDSlotReportTuple3) -> {
            initialSlotReportFuture.complete(null);
            return CompletableFuture.completedFuture(Acknowledge.get());
        });
        rpc.registerGateway(resourceManagerGateway.getAddress(), resourceManagerGateway);
        rpc.registerGateway(jobMasterGateway.getAddress(), jobMasterGateway);
        try {
            taskExecutor.start();
            TaskExecutorGateway taskExecutorGateway = taskExecutor.getSelfGateway(TaskExecutorGateway.class);
            resourceManagerLeaderRetriever.notifyListener(resourceManagerGateway.getAddress(), resourceManagerGateway.getFencingToken().toUUID());
            initialSlotReportFuture.get();
            ResourceID resourceID = taskManagerServices.getTaskManagerLocation().getResourceID();
            taskExecutorGateway.requestSlot(new SlotID(resourceID, 0), jobId, new AllocationID(), "foobar", resourceManagerGateway.getFencingToken(), TaskExecutorTest.timeout).get();
            jobManagerLeaderRetriever.notifyListener(jobMasterGateway.getAddress(), UUID.randomUUID());
            Assert.assertThat(offeredSlotsFuture.get(), Matchers.is(1));
            // notify loss of leadership
            jobManagerLeaderRetriever.notifyListener(null, null);
            Assert.assertThat(disconnectFuture.get(), Matchers.is(resourceID));
        } finally {
            RpcUtils.terminateRpcEndpoint(taskExecutor, TaskExecutorTest.timeout);
        }
    }

    private static final class StartStopNotifyingLeaderRetrievalService implements LeaderRetrievalService {
        private final CompletableFuture<LeaderRetrievalListener> startFuture;

        private final CompletableFuture<Void> stopFuture;

        private StartStopNotifyingLeaderRetrievalService(CompletableFuture<LeaderRetrievalListener> startFuture, CompletableFuture<Void> stopFuture) {
            this.startFuture = startFuture;
            this.stopFuture = stopFuture;
        }

        @Override
        public void start(LeaderRetrievalListener listener) throws Exception {
            startFuture.complete(listener);
        }

        @Override
        public void stop() throws Exception {
            stopFuture.complete(null);
        }
    }

    /**
     * Special {@link HeartbeatServices} which creates a {@link RecordingHeartbeatManagerImpl}.
     */
    private static final class RecordingHeartbeatServices extends HeartbeatServices {
        private final BlockingQueue<ResourceID> unmonitoredTargets;

        private final BlockingQueue<ResourceID> monitoredTargets;

        public RecordingHeartbeatServices(long heartbeatInterval, long heartbeatTimeout) {
            super(heartbeatInterval, heartbeatTimeout);
            this.unmonitoredTargets = new ArrayBlockingQueue(1);
            this.monitoredTargets = new ArrayBlockingQueue(1);
        }

        @Override
        public <I, O> HeartbeatManager<I, O> createHeartbeatManager(ResourceID resourceId, HeartbeatListener<I, O> heartbeatListener, ScheduledExecutor scheduledExecutor, Logger log) {
            return new TaskExecutorTest.RecordingHeartbeatManagerImpl(heartbeatTimeout, resourceId, heartbeatListener, scheduledExecutor, scheduledExecutor, log, unmonitoredTargets, monitoredTargets);
        }

        public BlockingQueue<ResourceID> getUnmonitoredTargets() {
            return unmonitoredTargets;
        }

        public BlockingQueue<ResourceID> getMonitoredTargets() {
            return monitoredTargets;
        }
    }

    /**
     * {@link HeartbeatManagerImpl} which records the unmonitored targets.
     */
    private static final class RecordingHeartbeatManagerImpl<I, O> extends HeartbeatManagerImpl<I, O> {
        private final BlockingQueue<ResourceID> unmonitoredTargets;

        private final BlockingQueue<ResourceID> monitoredTargets;

        public RecordingHeartbeatManagerImpl(long heartbeatTimeoutIntervalMs, ResourceID ownResourceID, HeartbeatListener<I, O> heartbeatListener, Executor executor, ScheduledExecutor scheduledExecutor, Logger log, BlockingQueue<ResourceID> unmonitoredTargets, BlockingQueue<ResourceID> monitoredTargets) {
            super(heartbeatTimeoutIntervalMs, ownResourceID, heartbeatListener, executor, scheduledExecutor, log);
            this.unmonitoredTargets = unmonitoredTargets;
            this.monitoredTargets = monitoredTargets;
        }

        @Override
        public void unmonitorTarget(ResourceID resourceID) {
            super.unmonitorTarget(resourceID);
            unmonitoredTargets.offer(resourceID);
        }

        @Override
        public void monitorTarget(ResourceID resourceID, HeartbeatTarget<O> heartbeatTarget) {
            super.monitorTarget(resourceID, heartbeatTarget);
            monitoredTargets.offer(resourceID);
        }
    }

    private static final class TestingTaskSlotTable extends TaskSlotTable {
        private final Queue<SlotReport> slotReports;

        private TestingTaskSlotTable(Queue<SlotReport> slotReports) {
            super(Collections.singleton(UNKNOWN), new TimerService(TestingUtils.defaultExecutor(), 10000L));
            this.slotReports = slotReports;
        }

        @Override
        public SlotReport createSlotReport(ResourceID resourceId) {
            return slotReports.poll();
        }
    }
}
