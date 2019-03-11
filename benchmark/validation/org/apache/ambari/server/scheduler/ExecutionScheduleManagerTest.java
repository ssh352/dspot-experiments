/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.scheduler;


import BatchRequestJob.BATCH_REQUEST_FAILED_TASKS_IN_CURRENT_BATCH_KEY;
import BatchRequestJob.BATCH_REQUEST_FAILED_TASKS_KEY;
import BatchRequestJob.BATCH_REQUEST_TOTAL_TASKS_KEY;
import ExecutionJob.LINEAR_EXECUTION_JOB_GROUP;
import ExecutionJob.NEXT_EXECUTION_JOB_NAME_KEY;
import HostRoleStatus.FAILED;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.PENDING;
import RequestExecution.Status.COMPLETED;
import RequestExecution.Status.PAUSED;
import RequestExecution.Status.SCHEDULED;
import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionDBAccessor;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.security.authorization.internal.InternalTokenStorage;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.scheduler.Batch;
import org.apache.ambari.server.state.scheduler.BatchRequest;
import org.apache.ambari.server.state.scheduler.BatchRequestResponse;
import org.apache.ambari.server.state.scheduler.BatchSettings;
import org.apache.ambari.server.state.scheduler.RequestExecution;
import org.apache.ambari.server.state.scheduler.RequestExecutionFactory;
import org.apache.ambari.server.state.scheduler.Schedule;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class ExecutionScheduleManagerTest {
    private static Clusters clusters;

    private static Cluster cluster;

    private static String clusterName;

    private static Injector injector;

    private static AmbariMetaInfo metaInfo;

    private static ExecutionScheduleManager executionScheduleManager;

    private static RequestExecutionFactory requestExecutionFactory;

    private static ExecutionScheduler executionScheduler;

    private static Scheduler scheduler;

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionScheduleManagerTest.class);

    public static class TestExecutionScheduler extends ExecutionSchedulerImpl {
        @Inject
        public TestExecutionScheduler(Injector injector) {
            super(injector);
            try {
                StdSchedulerFactory factory = new StdSchedulerFactory();
                ExecutionScheduleManagerTest.scheduler = factory.getScheduler();
                isInitialized = true;
            } catch (SchedulerException e) {
                e.printStackTrace();
                throw new ExceptionInInitializerError(("Unable to instantiate " + "scheduler"));
            }
        }

        public Scheduler getScheduler() {
            return ExecutionScheduleManagerTest.scheduler;
        }
    }

    public static class ExecutionSchedulerTestModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(ExecutionScheduler.class).to(ExecutionScheduleManagerTest.TestExecutionScheduler.class);
        }
    }

    @Test
    public void testScheduleBatch() throws Exception {
        RequestExecution requestExecution = createRequestExecution(true);
        Assert.assertNotNull(requestExecution);
        ExecutionScheduleManagerTest.executionScheduleManager.scheduleAllBatches(requestExecution);
        String jobName1 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 10L);
        String jobName2 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 12L);
        JobDetail jobDetail1 = null;
        JobDetail jobDetail2 = null;
        Trigger trigger1 = null;
        Trigger trigger2 = null;
        // enumerate each job group
        for (String group : ExecutionScheduleManagerTest.scheduler.getJobGroupNames()) {
            // enumerate each job in group
            for (JobKey jobKey : ExecutionScheduleManagerTest.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(LINEAR_EXECUTION_JOB_GROUP))) {
                ExecutionScheduleManagerTest.LOG.info(("Found job identified by: " + jobKey));
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                List<Trigger> triggers = ((List<Trigger>) (ExecutionScheduleManagerTest.scheduler.getTriggersOfJob(jobKey)));
                Trigger trigger = ((triggers != null) && (!(triggers.isEmpty()))) ? triggers.get(0) : null;
                Date nextFireTime = (trigger != null) ? trigger.getNextFireTime() : null;
                ExecutionScheduleManagerTest.LOG.info(((((("[jobName] : " + jobName) + " [groupName] : ") + jobGroup) + " - ") + nextFireTime));
                if (jobName.equals(jobName1)) {
                    jobDetail1 = ExecutionScheduleManagerTest.scheduler.getJobDetail(jobKey);
                    trigger1 = trigger;
                } else
                    if (jobName.equals(jobName2)) {
                        jobDetail2 = ExecutionScheduleManagerTest.scheduler.getJobDetail(jobKey);
                        trigger2 = trigger;
                    }

            }
        }
        Assert.assertNotNull(jobDetail1);
        Assert.assertNotNull(trigger1);
        Assert.assertNotNull(jobDetail2);
        Assert.assertNull(trigger2);
        CronTrigger cronTrigger = ((CronTrigger) (trigger1));
        Schedule schedule = new Schedule();
        schedule.setMinutes("10");
        schedule.setHours("2");
        schedule.setMonth("*");
        schedule.setDaysOfMonth("*");
        schedule.setDayOfWeek("?");
        Assert.assertEquals(schedule.getScheduleExpression(), cronTrigger.getCronExpression());
        Assert.assertEquals(jobName1, jobDetail1.getKey().getName());
        Assert.assertEquals(jobName2, jobDetail2.getKey().getName());
    }

    @Test
    public void testDeleteAllJobs() throws Exception {
        RequestExecution requestExecution = createRequestExecution(true);
        Assert.assertNotNull(requestExecution);
        ExecutionScheduleManagerTest.executionScheduleManager.scheduleAllBatches(requestExecution);
        String jobName1 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 10L);
        String jobName2 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 12L);
        JobDetail jobDetail1 = ExecutionScheduleManagerTest.scheduler.getJobDetail(JobKey.jobKey(jobName1, LINEAR_EXECUTION_JOB_GROUP));
        JobDetail jobDetail2 = ExecutionScheduleManagerTest.scheduler.getJobDetail(JobKey.jobKey(jobName2, LINEAR_EXECUTION_JOB_GROUP));
        Assert.assertNotNull(jobDetail1);
        Assert.assertNotNull(jobDetail2);
        Assert.assertTrue((!(ExecutionScheduleManagerTest.scheduler.getTriggersOfJob(JobKey.jobKey(jobName1, LINEAR_EXECUTION_JOB_GROUP)).isEmpty())));
        ExecutionScheduleManagerTest.executionScheduleManager.deleteAllJobs(requestExecution);
        Assert.assertTrue(ExecutionScheduleManagerTest.scheduler.getTriggersOfJob(JobKey.jobKey(jobName1, LINEAR_EXECUTION_JOB_GROUP)).isEmpty());
    }

    @Test
    public void testPointInTimeExecutionJob() throws Exception {
        RequestExecution requestExecution = createRequestExecution(false);
        Assert.assertNotNull(requestExecution);
        ExecutionScheduleManagerTest.executionScheduleManager.scheduleAllBatches(requestExecution);
        String jobName1 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 10L);
        String jobName2 = ExecutionScheduleManagerTest.executionScheduleManager.getJobName(requestExecution.getId(), 12L);
        JobDetail jobDetail1 = ExecutionScheduleManagerTest.scheduler.getJobDetail(JobKey.jobKey(jobName1, LINEAR_EXECUTION_JOB_GROUP));
        JobDetail jobDetail2 = ExecutionScheduleManagerTest.scheduler.getJobDetail(JobKey.jobKey(jobName2, LINEAR_EXECUTION_JOB_GROUP));
        Assert.assertNotNull(jobDetail1);
        Assert.assertNotNull(jobDetail2);
        List<? extends Trigger> triggers = ExecutionScheduleManagerTest.scheduler.getTriggersOfJob(JobKey.jobKey(jobName1, LINEAR_EXECUTION_JOB_GROUP));
        Assert.assertNotNull(triggers);
        Assert.assertEquals(1, triggers.size());
        assertThat(triggers.get(0), CoreMatchers.instanceOf(SimpleTrigger.class));
        Assert.assertNull(jobDetail2.getJobDataMap().getString(NEXT_EXECUTION_JOB_NAME_KEY));
        int waitCount = 0;
        while (((ExecutionScheduleManagerTest.scheduler.getCurrentlyExecutingJobs().size()) != 0) && (waitCount < 10)) {
            Thread.sleep(100);
            waitCount++;
        } 
    }

    @Test
    public void testExecuteBatchRequest() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        BatchRequest batchRequestMock = createMock(BatchRequest.class);
        long executionId = 11L;
        long batchId = 1L;
        long requestId = 5L;
        String clusterName = "mycluster";
        String uri = "clusters";
        String type = "post";
        String body = "body";
        Integer userId = 1;
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        BatchRequestResponse batchRequestResponse = new BatchRequestResponse();
        batchRequestResponse.setStatus(IN_PROGRESS.toString());
        batchRequestResponse.setRequestId(requestId);
        batchRequestResponse.setReturnCode(202);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).addMockedMethods("performApiRequest", "updateBatchRequest").createNiceMock();
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatchRequest(eq(batchId))).andReturn(batchRequestMock).once();
        expect(requestExecutionMock.getRequestBody(eq(batchId))).andReturn(body).once();
        expect(requestExecutionMock.getAuthenticatedUserId()).andReturn(userId).once();
        expect(batchRequestMock.getUri()).andReturn(uri).once();
        expect(batchRequestMock.getType()).andReturn(type).once();
        batchRequestMock.setRequestId(5L);
        expectLastCall().once();
        expect(scheduleManager.performApiRequest(eq(uri), eq(body), eq(type), eq(userId))).andReturn(batchRequestResponse).once();
        scheduleManager.updateBatchRequest(eq(executionId), eq(batchId), eq(clusterName), eq(batchRequestResponse), eq(false));
        expectLastCall().once();
        actionDBAccessorMock.setSourceScheduleForRequest(eq(requestId), eq(executionId));
        expectLastCall().once();
        replay(clusterMock, clustersMock, requestExecutionMock, executionSchedulerMock, tokenStorageMock, batchRequestMock, scheduleManager, actionDBAccessorMock);
        scheduleManager.executeBatchRequest(executionId, batchId, clusterName);
        verify(clusterMock, clustersMock, configurationMock, requestExecutionMock, executionSchedulerMock, tokenStorageMock, batchRequestMock, scheduleManager, actionDBAccessorMock);
    }

    @Test
    public void testUpdateBatchRequest() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        BatchRequest batchRequestMock = createMock(BatchRequest.class);
        long executionId = 11L;
        long batchId = 1L;
        long requestId = 5L;
        String clusterName = "mycluster";
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        BatchRequestResponse batchRequestResponse = new BatchRequestResponse();
        batchRequestResponse.setStatus(IN_PROGRESS.toString());
        batchRequestResponse.setRequestId(requestId);
        batchRequestResponse.setReturnCode(202);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).addMockedMethods("performApiRequest").createNiceMock();
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        requestExecutionMock.updateBatchRequest(eq(batchId), eq(batchRequestResponse), eq(true));
        expectLastCall().once();
        replay(clusterMock, clustersMock, requestExecutionMock, executionSchedulerMock, tokenStorageMock, batchRequestMock, scheduleManager);
        scheduleManager.updateBatchRequest(executionId, batchId, clusterName, batchRequestResponse, true);
        verify(clusterMock, clustersMock, configurationMock, requestExecutionMock, executionSchedulerMock, tokenStorageMock, batchRequestMock, scheduleManager);
    }

    @Test
    public void testGetBatchRequestResponse() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        long requestId = 5L;
        String clusterName = "mycluster";
        String apiUri = "api/v1/clusters/mycluster/requests/5";
        Capture<String> uriCapture = EasyMock.newCapture();
        BatchRequestResponse batchRequestResponse = new BatchRequestResponse();
        batchRequestResponse.setStatus(IN_PROGRESS.toString());
        batchRequestResponse.setRequestId(requestId);
        batchRequestResponse.setReturnCode(202);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).addMockedMethods("performApiGetRequest").createNiceMock();
        expect(scheduleManager.performApiGetRequest(capture(uriCapture), eq(true))).andReturn(batchRequestResponse).once();
        replay(clusterMock, clustersMock, executionSchedulerMock, tokenStorageMock, scheduleManager);
        scheduleManager.getBatchRequestResponse(requestId, clusterName);
        verify(clusterMock, clustersMock, configurationMock, executionSchedulerMock, tokenStorageMock, scheduleManager);
        assertEquals(apiUri, uriCapture.getValue());
    }

    @Test
    public void testHasToleranceThresholdExceeded() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Batch batchMock = createMock(Batch.class);
        long executionId = 11L;
        String clusterName = "c1";
        BatchSettings batchSettings = new BatchSettings();
        batchSettings.setTaskFailureToleranceLimit(1);
        batchSettings.setTaskFailureToleranceLimitPerBatch(1);
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatch()).andReturn(batchMock).anyTimes();
        expect(batchMock.getBatchSettings()).andReturn(batchSettings).anyTimes();
        replay(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, batchMock);
        ExecutionScheduleManager scheduleManager = new ExecutionScheduleManager(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson);
        HashMap<String, Integer> taskCounts = new HashMap<String, Integer>() {
            {
                put(BATCH_REQUEST_FAILED_TASKS_KEY, 2);
                put(BATCH_REQUEST_TOTAL_TASKS_KEY, 10);
            }
        };
        boolean exceeded = scheduleManager.hasToleranceThresholdExceeded(executionId, clusterName, taskCounts);
        Assert.assertTrue(exceeded);
        verify(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, batchMock);
    }

    @Test
    public void testHasToleranceThresholdPerBatchExceeded() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Batch batchMock = createMock(Batch.class);
        long executionId = 11L;
        String clusterName = "c1";
        BatchSettings batchSettings = new BatchSettings();
        batchSettings.setTaskFailureToleranceLimitPerBatch(1);
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatch()).andReturn(batchMock).anyTimes();
        expect(batchMock.getBatchSettings()).andReturn(batchSettings).anyTimes();
        replay(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, batchMock);
        ExecutionScheduleManager scheduleManager = new ExecutionScheduleManager(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson);
        HashMap<String, Integer> taskCounts = new HashMap<String, Integer>() {
            {
                put(BATCH_REQUEST_FAILED_TASKS_IN_CURRENT_BATCH_KEY, 2);
                put(BATCH_REQUEST_TOTAL_TASKS_KEY, 10);
            }
        };
        boolean exceeded = scheduleManager.hasToleranceThresholdExceeded(executionId, clusterName, taskCounts);
        Assert.assertTrue(exceeded);
        verify(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, batchMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFinalizeBatch() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Batch batchMock = createMock(Batch.class);
        JobDetail jobDetailMock = createMock(JobDetail.class);
        final BatchRequest batchRequestMock = createMock(BatchRequest.class);
        final Trigger triggerMock = createNiceMock(Trigger.class);
        final List<Trigger> triggers = new ArrayList<Trigger>() {
            {
                add(triggerMock);
            }
        };
        long executionId = 11L;
        String clusterName = "c1";
        Date pastDate = new Date(((new Date().getTime()) - 2));
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).createMock();
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatch()).andReturn(batchMock).anyTimes();
        expect(batchMock.getBatchRequests()).andReturn(new ArrayList<BatchRequest>() {
            {
                add(batchRequestMock);
            }
        });
        expect(batchRequestMock.getOrderId()).andReturn(1L).anyTimes();
        expect(executionSchedulerMock.getJobDetail(((JobKey) (anyObject())))).andReturn(jobDetailMock).anyTimes();
        expect(((List<Trigger>) (executionSchedulerMock.getTriggersForJob(((JobKey) (anyObject())))))).andReturn(triggers).anyTimes();
        expect(triggerMock.mayFireAgain()).andReturn(true).anyTimes();
        expect(triggerMock.getFinalFireTime()).andReturn(pastDate).anyTimes();
        requestExecutionMock.updateStatus(COMPLETED);
        expectLastCall();
        replay(clustersMock, clusterMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock, triggerMock, jobDetailMock, actionDBAccessorMock);
        scheduleManager.finalizeBatch(executionId, clusterName);
        verify(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock, triggerMock, jobDetailMock, actionDBAccessorMock);
    }

    @Test
    public void testFinalizeBeforeExit() throws Exception {
        ExecutionScheduleManager scheduleManagerMock = createMock(ExecutionScheduleManager.class);
        AbstractLinearExecutionJob executionJob = createMockBuilder(AbstractLinearExecutionJob.class).addMockedMethods("finalizeExecution", "doWork").withConstructor(scheduleManagerMock).createMock();
        JobExecutionContext context = createMock(JobExecutionContext.class);
        JobDetail jobDetail = createMock(JobDetail.class);
        JobDataMap jobDataMap = createMock(JobDataMap.class);
        expect(context.getJobDetail()).andReturn(jobDetail).anyTimes();
        expect(context.getMergedJobDataMap()).andReturn(jobDataMap).anyTimes();
        expect(jobDetail.getKey()).andReturn(new JobKey("TestJob"));
        expect(jobDataMap.getWrappedMap()).andReturn(new HashMap());
        expect(scheduleManagerMock.continueOnMisfire(context)).andReturn(true);
        executionJob.doWork(EasyMock.anyObject());
        expectLastCall().andThrow(new AmbariException("Test Exception")).anyTimes();
        executionJob.finalizeExecution(EasyMock.anyObject());
        expectLastCall().once();
        replay(scheduleManagerMock, executionJob, context, jobDataMap, jobDetail);
        try {
            executionJob.execute(context);
        } catch (Exception ae) {
            assertThat(ae, CoreMatchers.instanceOf(JobExecutionException.class));
            JobExecutionException je = ((JobExecutionException) (ae));
            Assert.assertEquals("Test Exception", je.getUnderlyingException().getMessage());
        }
        verify(scheduleManagerMock, executionJob, context, jobDataMap, jobDetail);
    }

    @Test
    public void testExtendApiResource() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        WebResource webResource = Client.create().resource("http://localhost:8080/");
        String clustersEndpoint = "http://localhost:8080/api/v1/clusters";
        Clusters clustersMock = createMock(Clusters.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        replay(clustersMock, configurationMock, executionSchedulerMock, tokenStorageMock, actionDBAccessorMock);
        ExecutionScheduleManager scheduleManager = new ExecutionScheduleManager(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson);
        assertEquals(clustersEndpoint, scheduleManager.extendApiResource(webResource, "clusters").getURI().toString());
        assertEquals(clustersEndpoint, scheduleManager.extendApiResource(webResource, "/clusters").getURI().toString());
        assertEquals(clustersEndpoint, scheduleManager.extendApiResource(webResource, "/api/v1/clusters").getURI().toString());
        assertEquals(clustersEndpoint, scheduleManager.extendApiResource(webResource, "api/v1/clusters").getURI().toString());
        assertEquals("http://localhost:8080/", scheduleManager.extendApiResource(webResource, "").getURI().toString());
    }

    @Test
    public void testUpdateBatchSchedulePause() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Batch batchMock = createMock(Batch.class);
        JobDetail jobDetailMock = createMock(JobDetail.class);
        final BatchRequest batchRequestMock1 = createMock(BatchRequest.class);
        final BatchRequest batchRequestMock2 = createMock(BatchRequest.class);
        final Trigger triggerMock = createNiceMock(Trigger.class);
        final List<Trigger> triggers = new ArrayList<Trigger>() {
            {
                add(triggerMock);
            }
        };
        long executionId = 11L;
        String clusterName = "c1";
        Date pastDate = new Date(((new Date().getTime()) - 2));
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).addMockedMethods("deleteJobs", "abortRequestById").createMock();
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatch()).andReturn(batchMock).anyTimes();
        expect(batchMock.getBatchRequests()).andReturn(new ArrayList<BatchRequest>() {
            {
                add(batchRequestMock1);
                add(batchRequestMock2);
            }
        });
        expect(batchRequestMock1.getOrderId()).andReturn(1L).anyTimes();
        expect(batchRequestMock1.getStatus()).andReturn(HostRoleStatus.COMPLETED.name()).anyTimes();
        expect(batchRequestMock1.compareTo(batchRequestMock2)).andReturn((-1)).anyTimes();
        expect(batchRequestMock2.compareTo(batchRequestMock1)).andReturn(1).anyTimes();
        expect(batchRequestMock2.getOrderId()).andReturn(3L).anyTimes();
        expect(batchRequestMock2.getStatus()).andReturn(IN_PROGRESS.name()).anyTimes();
        expect(executionSchedulerMock.getJobDetail(((JobKey) (anyObject())))).andReturn(jobDetailMock).anyTimes();
        expect(((List<Trigger>) (executionSchedulerMock.getTriggersForJob(((JobKey) (anyObject())))))).andReturn(triggers).anyTimes();
        expect(triggerMock.mayFireAgain()).andReturn(true).anyTimes();
        expect(triggerMock.getFinalFireTime()).andReturn(pastDate).anyTimes();
        expect(requestExecutionMock.getStatus()).andReturn(PAUSED.name()).anyTimes();
        expect(requestExecutionMock.getId()).andReturn(executionId).anyTimes();
        expect(requestExecutionMock.getBatchRequestRequestsIDs(3L)).andReturn(Collections.singleton(5L)).anyTimes();
        // deletes only second batch, the first was completed
        scheduleManager.deleteJobs(eq(requestExecutionMock), eq(3L));
        expectLastCall().once();
        // second batch request needs to be aborted
        expect(scheduleManager.abortRequestById(requestExecutionMock, 5L)).andReturn(null).once();
        replay(clustersMock, clusterMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock1, batchRequestMock2, triggerMock, jobDetailMock, actionDBAccessorMock);
        scheduleManager.updateBatchSchedule(requestExecutionMock);
        verify(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock1, batchRequestMock2, triggerMock, jobDetailMock, actionDBAccessorMock);
    }

    @Test
    public void testUpdateBatchScheduleUnpause() throws Exception {
        Clusters clustersMock = createMock(Clusters.class);
        Cluster clusterMock = createMock(Cluster.class);
        Configuration configurationMock = createNiceMock(Configuration.class);
        ExecutionScheduler executionSchedulerMock = createMock(ExecutionScheduler.class);
        InternalTokenStorage tokenStorageMock = createMock(InternalTokenStorage.class);
        ActionDBAccessor actionDBAccessorMock = createMock(ActionDBAccessor.class);
        Gson gson = new Gson();
        RequestExecution requestExecutionMock = createMock(RequestExecution.class);
        Batch batchMock = createMock(Batch.class);
        JobDetail jobDetailMock = createMock(JobDetail.class);
        final BatchRequest batchRequestMock1 = createMock(BatchRequest.class);
        final BatchRequest batchRequestMock2 = createMock(BatchRequest.class);
        final Trigger triggerMock = createNiceMock(Trigger.class);
        final List<Trigger> triggers = new ArrayList<Trigger>() {
            {
                add(triggerMock);
            }
        };
        long executionId = 11L;
        String clusterName = "c1";
        Date pastDate = new Date(((new Date().getTime()) - 2));
        Map<Long, RequestExecution> executionMap = new HashMap<>();
        executionMap.put(executionId, requestExecutionMock);
        EasyMock.expect(configurationMock.getApiSSLAuthentication()).andReturn(Boolean.FALSE);
        EasyMock.replay(configurationMock);
        ExecutionScheduleManager scheduleManager = createMockBuilder(ExecutionScheduleManager.class).withConstructor(configurationMock, executionSchedulerMock, tokenStorageMock, clustersMock, actionDBAccessorMock, gson).addMockedMethods("scheduleBatch").createMock();
        expect(clustersMock.getCluster(clusterName)).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getAllRequestExecutions()).andReturn(executionMap).anyTimes();
        expect(requestExecutionMock.getBatch()).andReturn(batchMock).anyTimes();
        expect(batchMock.getBatchRequests()).andReturn(new ArrayList<BatchRequest>() {
            {
                add(batchRequestMock1);
                add(batchRequestMock2);
            }
        });
        expect(batchRequestMock1.getOrderId()).andReturn(1L).anyTimes();
        expect(batchRequestMock1.getStatus()).andReturn(FAILED.name()).anyTimes();
        expect(batchRequestMock1.compareTo(batchRequestMock2)).andReturn((-1)).anyTimes();
        expect(batchRequestMock2.compareTo(batchRequestMock1)).andReturn(1).anyTimes();
        expect(batchRequestMock2.getOrderId()).andReturn(3L).anyTimes();
        expect(batchRequestMock2.getStatus()).andReturn(PENDING.name()).anyTimes();
        expect(executionSchedulerMock.getJobDetail(((JobKey) (anyObject())))).andReturn(jobDetailMock).anyTimes();
        expect(((List<Trigger>) (executionSchedulerMock.getTriggersForJob(((JobKey) (anyObject())))))).andReturn(triggers).anyTimes();
        expect(triggerMock.mayFireAgain()).andReturn(true).anyTimes();
        expect(triggerMock.getFinalFireTime()).andReturn(pastDate).anyTimes();
        expect(requestExecutionMock.getStatus()).andReturn(SCHEDULED.name()).anyTimes();
        expect(requestExecutionMock.getId()).andReturn(executionId).anyTimes();
        expect(requestExecutionMock.getBatchRequestRequestsIDs(3L)).andReturn(Collections.singleton(5L)).anyTimes();
        // schedule staring from second batch, the first was completed
        scheduleManager.scheduleBatch(eq(requestExecutionMock), eq(3L));
        expectLastCall().once();
        replay(clustersMock, clusterMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock1, batchRequestMock2, triggerMock, jobDetailMock, actionDBAccessorMock);
        scheduleManager.updateBatchSchedule(requestExecutionMock);
        verify(clustersMock, clusterMock, configurationMock, requestExecutionMock, executionSchedulerMock, scheduleManager, batchMock, batchRequestMock1, batchRequestMock2, triggerMock, jobDetailMock, actionDBAccessorMock);
    }
}

