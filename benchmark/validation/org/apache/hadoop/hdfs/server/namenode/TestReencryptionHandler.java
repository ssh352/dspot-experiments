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
package org.apache.hadoop.hdfs.server.namenode;


import ReencryptionUpdater.ZoneSubmissionTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.apache.hadoop.util.StopWatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for ReencryptionHandler.
 */
public class TestReencryptionHandler {
    protected static final Logger LOG = LoggerFactory.getLogger(TestReencryptionHandler.class);

    @Rule
    public Timeout globalTimeout = new Timeout((180 * 1000));

    @Test
    public void testThrottle() throws Exception {
        final Configuration conf = new Configuration();
        conf.setDouble(DFSConfigKeys.DFS_NAMENODE_REENCRYPT_THROTTLE_LIMIT_HANDLER_RATIO_KEY, 0.5);
        final ReencryptionHandler rh = mockReencryptionhandler(conf);
        // mock StopWatches so all = 30s, locked = 20s. With ratio = .5, throttle
        // should wait for 30 * 0.5 - 20 = 5s.
        final StopWatch mockAll = Mockito.mock(StopWatch.class);
        Mockito.when(mockAll.now(TimeUnit.MILLISECONDS)).thenReturn(((long) (30000)));
        Mockito.when(mockAll.reset()).thenReturn(mockAll);
        final StopWatch mockLocked = Mockito.mock(StopWatch.class);
        Mockito.when(mockLocked.now(TimeUnit.MILLISECONDS)).thenReturn(((long) (20000)));
        Mockito.when(mockLocked.reset()).thenReturn(mockLocked);
        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        Whitebox.setInternalState(rh, "throttleTimerAll", mockAll);
        Whitebox.setInternalState(rh, "throttleTimerLocked", mockLocked);
        Whitebox.setInternalState(rh, "taskQueue", queue);
        final StopWatch sw = new StopWatch().start();
        rh.getTraverser().throttle();
        sw.stop();
        Assert.assertTrue("should have throttled for at least 8 second", ((sw.now(TimeUnit.MILLISECONDS)) > 8000));
        Assert.assertTrue("should have throttled for at most 12 second", ((sw.now(TimeUnit.MILLISECONDS)) < 12000));
    }

    @Test
    public void testThrottleNoOp() throws Exception {
        final Configuration conf = new Configuration();
        conf.setDouble(DFSConfigKeys.DFS_NAMENODE_REENCRYPT_THROTTLE_LIMIT_HANDLER_RATIO_KEY, 0.5);
        final ReencryptionHandler rh = mockReencryptionhandler(conf);
        // mock StopWatches so all = 30s, locked = 10s. With ratio = .5, throttle
        // should not happen.
        StopWatch mockAll = Mockito.mock(StopWatch.class);
        Mockito.when(mockAll.now()).thenReturn(new Long(30000));
        Mockito.when(mockAll.reset()).thenReturn(mockAll);
        StopWatch mockLocked = Mockito.mock(StopWatch.class);
        Mockito.when(mockLocked.now()).thenReturn(new Long(10000));
        Mockito.when(mockLocked.reset()).thenReturn(mockLocked);
        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        Whitebox.setInternalState(rh, "throttleTimerAll", mockAll);
        Whitebox.setInternalState(rh, "throttleTimerLocked", mockLocked);
        Whitebox.setInternalState(rh, "taskQueue", queue);
        final Map<Long, ReencryptionUpdater.ZoneSubmissionTracker> submissions = new HashMap<>();
        Whitebox.setInternalState(rh, "submissions", submissions);
        StopWatch sw = new StopWatch().start();
        rh.getTraverser().throttle();
        sw.stop();
        Assert.assertTrue("should not have throttled", ((sw.now(TimeUnit.MILLISECONDS)) < 1000));
    }

    @Test
    public void testThrottleConfigs() throws Exception {
        final Configuration conf = new Configuration();
        conf.setDouble(DFSConfigKeys.DFS_NAMENODE_REENCRYPT_THROTTLE_LIMIT_HANDLER_RATIO_KEY, (-1.0));
        try {
            mockReencryptionhandler(conf);
            Assert.fail("Should not be able to init");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(" is not positive", e);
        }
        conf.setDouble(DFSConfigKeys.DFS_NAMENODE_REENCRYPT_THROTTLE_LIMIT_HANDLER_RATIO_KEY, 0.0);
        try {
            mockReencryptionhandler(conf);
            Assert.fail("Should not be able to init");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(" is not positive", e);
        }
    }

    @Test
    public void testThrottleAccumulatingTasks() throws Exception {
        final Configuration conf = new Configuration();
        final ReencryptionHandler rh = mockReencryptionhandler(conf);
        // mock tasks piling up
        final Map<Long, ReencryptionUpdater.ZoneSubmissionTracker> submissions = new HashMap<>();
        final ReencryptionUpdater.ZoneSubmissionTracker zst = new ReencryptionUpdater.ZoneSubmissionTracker();
        submissions.put(new Long(1), zst);
        Future mock = Mockito.mock(Future.class);
        for (int i = 0; i < ((Runtime.getRuntime().availableProcessors()) * 3); ++i) {
            zst.addTask(mock);
        }
        Thread removeTaskThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    TestReencryptionHandler.LOG.info("removeTaskThread interrupted.");
                    Thread.currentThread().interrupt();
                }
                zst.getTasks().clear();
            }
        };
        Whitebox.setInternalState(rh, "submissions", submissions);
        final StopWatch sw = new StopWatch().start();
        removeTaskThread.start();
        rh.getTraverser().throttle();
        sw.stop();
        TestReencryptionHandler.LOG.info("Throttle completed, consumed {}", sw.now(TimeUnit.MILLISECONDS));
        Assert.assertTrue("should have throttled for at least 3 second", ((sw.now(TimeUnit.MILLISECONDS)) >= 3000));
    }
}

