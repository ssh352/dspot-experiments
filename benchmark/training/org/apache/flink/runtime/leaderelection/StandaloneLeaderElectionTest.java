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
package org.apache.flink.runtime.leaderelection;


import HighAvailabilityServices.DEFAULT_LEADER_ID;
import org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class StandaloneLeaderElectionTest extends TestLogger {
    private static final String TEST_URL = "akka://users/jobmanager";

    /**
     * Tests that the standalone leader election and retrieval service return the same leader
     * URL.
     */
    @Test
    public void testStandaloneLeaderElectionRetrieval() throws Exception {
        StandaloneLeaderElectionService leaderElectionService = new StandaloneLeaderElectionService();
        StandaloneLeaderRetrievalService leaderRetrievalService = new StandaloneLeaderRetrievalService(StandaloneLeaderElectionTest.TEST_URL);
        TestingContender contender = new TestingContender(StandaloneLeaderElectionTest.TEST_URL, leaderElectionService);
        TestingListener testingListener = new TestingListener();
        try {
            leaderElectionService.start(contender);
            leaderRetrievalService.start(testingListener);
            contender.waitForLeader(1000L);
            Assert.assertTrue(contender.isLeader());
            Assert.assertEquals(DEFAULT_LEADER_ID, contender.getLeaderSessionID());
            testingListener.waitForNewLeader(1000L);
            Assert.assertEquals(StandaloneLeaderElectionTest.TEST_URL, testingListener.getAddress());
            Assert.assertEquals(DEFAULT_LEADER_ID, testingListener.getLeaderSessionID());
        } finally {
            leaderElectionService.stop();
            leaderRetrievalService.stop();
        }
    }
}

