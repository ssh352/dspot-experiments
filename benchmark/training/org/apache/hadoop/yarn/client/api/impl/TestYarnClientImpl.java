/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.yarn.client.api.impl;


import UserGroupInformation.AuthenticationMethod.KERBEROS;
import YarnApplicationState.RUNNING;
import YarnConfiguration.RM_ADDRESS;
import YarnConfiguration.RM_PRINCIPAL;
import YarnConfiguration.TIMELINE_SERVICE_CLIENT_BEST_EFFORT;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This class is to test class {@link YarnClientImpl ).
 */
public class TestYarnClientImpl extends ParameterizedSchedulerTestBase {
    public TestYarnClientImpl(SchedulerType type) throws IOException {
        super(type);
    }

    @Test
    public void testStartWithTimelineV15() {
        Configuration conf = getConf();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setFloat(TIMELINE_SERVICE_VERSION, 1.5F);
        YarnClientImpl client = ((YarnClientImpl) (YarnClient.createYarnClient()));
        client.init(conf);
        client.start();
        client.stop();
    }

    @Test
    public void testAsyncAPIPollTimeout() {
        testAsyncAPIPollTimeoutHelper(null, false);
        testAsyncAPIPollTimeoutHelper(0L, true);
        testAsyncAPIPollTimeoutHelper(1L, true);
    }

    @Test
    public void testBestEffortTimelineDelegationToken() throws Exception {
        Configuration conf = getConf();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        YarnClientImpl client = Mockito.spy(new YarnClientImpl() {
            @Override
            TimelineClient createTimelineClient() throws IOException, YarnException {
                timelineClient = Mockito.mock(TimelineClient.class);
                Mockito.when(timelineClient.getDelegationToken(ArgumentMatchers.any())).thenThrow(new RuntimeException("Best effort test exception"));
                return timelineClient;
            }
        });
        client.init(conf);
        conf.setBoolean(TIMELINE_SERVICE_CLIENT_BEST_EFFORT, true);
        client.serviceInit(conf);
        client.getTimelineDelegationToken();
        try {
            conf.setBoolean(TIMELINE_SERVICE_CLIENT_BEST_EFFORT, false);
            client.serviceInit(conf);
            client.getTimelineDelegationToken();
            Assert.fail("Get delegation token should have thrown an exception");
        } catch (IOException e) {
            // Success
        }
    }

    @Test
    public void testAutomaticTimelineDelegationTokenLoading() throws Exception {
        Configuration conf = getConf();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        TimelineDelegationTokenIdentifier timelineDT = new TimelineDelegationTokenIdentifier();
        final Token<TimelineDelegationTokenIdentifier> dToken = new Token(timelineDT.getBytes(), new byte[0], timelineDT.getKind(), new Text());
        // create a mock client
        YarnClientImpl client = Mockito.spy(new YarnClientImpl() {
            @Override
            TimelineClient createTimelineClient() throws IOException, YarnException {
                timelineClient = Mockito.mock(TimelineClient.class);
                Mockito.when(timelineClient.getDelegationToken(ArgumentMatchers.any())).thenReturn(dToken);
                return timelineClient;
            }

            @Override
            protected void serviceStart() {
                rmClient = Mockito.mock(ApplicationClientProtocol.class);
            }

            @Override
            protected void serviceStop() {
            }

            @Override
            public ApplicationReport getApplicationReport(ApplicationId appId) {
                ApplicationReport report = Mockito.mock(ApplicationReport.class);
                Mockito.when(report.getYarnApplicationState()).thenReturn(RUNNING);
                return report;
            }

            @Override
            public boolean isSecurityEnabled() {
                return true;
            }
        });
        client.init(conf);
        client.start();
        try {
            // when i == 0, timeline DT already exists, no need to get one more
            // when i == 1, timeline DT doesn't exist, need to get one more
            for (int i = 0; i < 2; ++i) {
                ApplicationSubmissionContext context = Mockito.mock(ApplicationSubmissionContext.class);
                ApplicationId applicationId = ApplicationId.newInstance(0, (i + 1));
                Mockito.when(context.getApplicationId()).thenReturn(applicationId);
                DataOutputBuffer dob = new DataOutputBuffer();
                Credentials credentials = new Credentials();
                if (i == 0) {
                    credentials.addToken(client.timelineService, dToken);
                }
                credentials.writeTokenStorageToStream(dob);
                ByteBuffer tokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
                ContainerLaunchContext clc = ContainerLaunchContext.newInstance(null, null, null, null, tokens, null);
                Mockito.when(context.getAMContainerSpec()).thenReturn(clc);
                client.submitApplication(context);
                if (i == 0) {
                    // GetTimelineDelegationToken shouldn't be called
                    Mockito.verify(client, Mockito.never()).getTimelineDelegationToken();
                }
                // In either way, token should be there
                credentials = new Credentials();
                DataInputByteBuffer dibb = new DataInputByteBuffer();
                tokens = clc.getTokens();
                if (tokens != null) {
                    dibb.reset(tokens);
                    credentials.readTokenStorageStream(dibb);
                    tokens.rewind();
                }
                Collection<Token<? extends TokenIdentifier>> dTokens = credentials.getAllTokens();
                Assert.assertEquals(1, dTokens.size());
                Assert.assertEquals(dToken, dTokens.iterator().next());
            }
        } finally {
            client.stop();
        }
    }

    @Test
    public void testParseTimelineDelegationTokenRenewer() {
        // Client side
        YarnClientImpl client = ((YarnClientImpl) (YarnClient.createYarnClient()));
        Configuration conf = getConf();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.set(RM_PRINCIPAL, "rm/_HOST@EXAMPLE.COM");
        conf.set(RM_ADDRESS, "localhost:8188");
        try {
            client.init(conf);
            client.start();
            Assert.assertEquals("rm/localhost@EXAMPLE.COM", client.timelineDTRenewer);
        } finally {
            client.stop();
        }
    }
}

