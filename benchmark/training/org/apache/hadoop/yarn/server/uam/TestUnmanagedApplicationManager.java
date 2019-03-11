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
package org.apache.hadoop.yarn.server.uam;


import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.server.AMHeartbeatRequestHandler;
import org.apache.hadoop.yarn.server.AMRMClientRelayer;
import org.apache.hadoop.yarn.server.MockResourceManagerFacade;
import org.apache.hadoop.yarn.util.AsyncCallback;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for UnmanagedApplicationManager.
 */
public class TestUnmanagedApplicationManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestUnmanagedApplicationManager.class);

    private TestUnmanagedApplicationManager.TestableUnmanagedApplicationManager uam;

    private Configuration conf = new YarnConfiguration();

    private TestUnmanagedApplicationManager.CountingCallback callback;

    private ApplicationAttemptId attemptId;

    @Test(timeout = 10000)
    public void testBasicUsage() throws IOException, InterruptedException, YarnException {
        launchUAM(attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Wait for outstanding async allocate callback
        waitForCallBackCountAndCheckZeroPending(callback, 1);
        finishApplicationMaster(FinishApplicationMasterRequest.newInstance(null, null, null), attemptId);
        while (isHeartbeatThreadAlive()) {
            TestUnmanagedApplicationManager.LOG.info("waiting for heartbeat thread to finish");
            Thread.sleep(100);
        } 
    }

    /* Test re-attaching of an existing UAM. This is for HA of UAM client. */
    @Test(timeout = 5000)
    public void testUAMReAttach() throws IOException, InterruptedException, YarnException {
        launchUAM(attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Wait for outstanding async allocate callback
        waitForCallBackCountAndCheckZeroPending(callback, 1);
        MockResourceManagerFacade rmProxy = uam.getRMProxy();
        uam = new TestUnmanagedApplicationManager.TestableUnmanagedApplicationManager(conf, attemptId.getApplicationId(), null, "submitter", "appNameSuffix", true, "rm");
        uam.setRMProxy(rmProxy);
        reAttachUAM(null, attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Wait for outstanding async allocate callback
        waitForCallBackCountAndCheckZeroPending(callback, 2);
        finishApplicationMaster(FinishApplicationMasterRequest.newInstance(null, null, null), attemptId);
    }

    @Test(timeout = 5000)
    public void testReRegister() throws IOException, InterruptedException, YarnException {
        launchUAM(attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        uam.setShouldReRegisterNext();
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Wait for outstanding async allocate callback
        waitForCallBackCountAndCheckZeroPending(callback, 1);
        uam.setShouldReRegisterNext();
        finishApplicationMaster(FinishApplicationMasterRequest.newInstance(null, null, null), attemptId);
    }

    /**
     * If register is slow, async allocate requests in the meanwhile should not
     * throw or be dropped.
     */
    @Test(timeout = 5000)
    public void testSlowRegisterCall() throws IOException, InterruptedException, YarnException {
        // Register with wait() in RM in a separate thread
        Thread registerAMThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    launchUAM(attemptId);
                    registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 1001, null), attemptId);
                } catch (Exception e) {
                    TestUnmanagedApplicationManager.LOG.info("Register thread exception", e);
                }
            }
        });
        // Sync obj from mock RM
        Object syncObj = MockResourceManagerFacade.getRegisterSyncObj();
        // Wait for register call in the thread get into RM and then wake us
        synchronized(syncObj) {
            TestUnmanagedApplicationManager.LOG.info("Starting register thread");
            registerAMThread.start();
            try {
                TestUnmanagedApplicationManager.LOG.info("Test main starts waiting");
                syncObj.wait();
                TestUnmanagedApplicationManager.LOG.info("Test main wait finished");
            } catch (Exception e) {
                TestUnmanagedApplicationManager.LOG.info("Test main wait interrupted", e);
            }
        }
        // First allocate before register succeeds
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Notify the register thread
        synchronized(syncObj) {
            syncObj.notifyAll();
        }
        TestUnmanagedApplicationManager.LOG.info("Test main wait for register thread to finish");
        registerAMThread.join();
        TestUnmanagedApplicationManager.LOG.info("Register thread finished");
        // Second allocate, normal case
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        // Both allocate before should respond
        waitForCallBackCountAndCheckZeroPending(callback, 2);
        finishApplicationMaster(FinishApplicationMasterRequest.newInstance(null, null, null), attemptId);
        // Allocates after finishAM should be ignored
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
        Assert.assertEquals(0, callback.requestQueueSize);
        // A short wait just in case the allocates get executed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        Assert.assertEquals(2, callback.callBackCount);
    }

    @Test(expected = Exception.class)
    public void testAllocateWithoutRegister() throws IOException, InterruptedException, YarnException {
        allocateAsync(AllocateRequest.newInstance(0, 0, null, null, null), callback, attemptId);
    }

    @Test(expected = Exception.class)
    public void testFinishWithoutRegister() throws IOException, InterruptedException, YarnException {
        finishApplicationMaster(FinishApplicationMasterRequest.newInstance(null, null, null), attemptId);
    }

    @Test(timeout = 10000)
    public void testForceKill() throws IOException, InterruptedException, YarnException {
        launchUAM(attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        forceKillApplication();
        while (isHeartbeatThreadAlive()) {
            TestUnmanagedApplicationManager.LOG.info("waiting for heartbeat thread to finish");
            Thread.sleep(100);
        } 
        try {
            forceKillApplication();
            Assert.fail("Should fail because application is already killed");
        } catch (YarnException t) {
        }
    }

    @Test(timeout = 10000)
    public void testShutDownConnections() throws IOException, InterruptedException, YarnException {
        launchUAM(attemptId);
        registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null), attemptId);
        shutDownConnections();
        while (isHeartbeatThreadAlive()) {
            TestUnmanagedApplicationManager.LOG.info("waiting for heartbeat thread to finish");
            Thread.sleep(100);
        } 
    }

    protected class CountingCallback implements AsyncCallback<AllocateResponse> {
        private int callBackCount;

        private int requestQueueSize;

        @Override
        public void callback(AllocateResponse response) {
            synchronized(this) {
                (callBackCount)++;
                requestQueueSize = getRequestQueueSize();
                this.notifyAll();
            }
        }
    }

    /**
     * Testable UnmanagedApplicationManager that talks to a mock RM.
     */
    public class TestableUnmanagedApplicationManager extends UnmanagedApplicationManager {
        private MockResourceManagerFacade rmProxy;

        public TestableUnmanagedApplicationManager(Configuration conf, ApplicationId appId, String queueName, String submitter, String appNameSuffix, boolean keepContainersAcrossApplicationAttempts, String rmName) {
            super(conf, appId, queueName, submitter, appNameSuffix, keepContainersAcrossApplicationAttempts, rmName);
        }

        @Override
        protected AMHeartbeatRequestHandler createAMHeartbeatRequestHandler(Configuration config, ApplicationId appId, AMRMClientRelayer rmProxyRelayer) {
            return new TestUnmanagedApplicationManager.TestableAMRequestHandlerThread(config, appId, rmProxyRelayer);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <T> T createRMProxy(final Class<T> protocol, Configuration config, UserGroupInformation user, Token<AMRMTokenIdentifier> token) {
            if ((rmProxy) == null) {
                rmProxy = new MockResourceManagerFacade(config, 0);
            }
            return ((T) (rmProxy));
        }

        public void setShouldReRegisterNext() {
            if ((rmProxy) != null) {
                rmProxy.setShouldReRegisterNext();
            }
        }

        public MockResourceManagerFacade getRMProxy() {
            return rmProxy;
        }

        public void setRMProxy(MockResourceManagerFacade proxy) {
            this.rmProxy = proxy;
        }
    }

    /**
     * Wrap the handler thread so it calls from the same user.
     */
    public class TestableAMRequestHandlerThread extends AMHeartbeatRequestHandler {
        public TestableAMRequestHandlerThread(Configuration conf, ApplicationId applicationId, AMRMClientRelayer rmProxyRelayer) {
            super(conf, applicationId, rmProxyRelayer);
        }

        @Override
        public void run() {
            try {
                getUGIWithToken(attemptId).doAs(new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() {
                        TestUnmanagedApplicationManager.TestableAMRequestHandlerThread.super.run();
                        return null;
                    }
                });
            } catch (Exception e) {
                TestUnmanagedApplicationManager.LOG.error("Exception running TestableAMRequestHandlerThread", e);
            }
        }
    }
}

