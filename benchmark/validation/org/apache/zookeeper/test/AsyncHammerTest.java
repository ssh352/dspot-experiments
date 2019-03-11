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
package org.apache.zookeeper.test;


import CreateMode.PERSISTENT_SEQUENTIAL;
import Ids.OPEN_ACL_UNSAFE;
import KeeperException.Code.OK;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncHammerTest extends ZKTestCase implements DataCallback , StringCallback , VoidCallback {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncHammerTest.class);

    private QuorumBase qb = new QuorumBase();

    private volatile boolean bang;

    /**
     * Create /test- sequence nodes asynchronously, max 30 outstanding
     */
    class HammerThread extends Thread implements StringCallback , VoidCallback {
        private static final int MAX_OUTSTANDING = 30;

        private TestableZooKeeper zk;

        private int outstanding;

        private volatile boolean failed = false;

        public HammerThread(String name) {
            super(name);
        }

        public void run() {
            try {
                ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
                zk = new TestableZooKeeper(qb.hostPort, ClientBase.CONNECTION_TIMEOUT, watcher);
                watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
                while (bang) {
                    incOutstanding();// before create otw race

                    zk.create("/test-", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL, this, null);
                } 
            } catch (InterruptedException e) {
                if (bang) {
                    AsyncHammerTest.LOG.error("sanity check Assert.failed!!!");// sanity check

                    return;
                }
            } catch (Exception e) {
                AsyncHammerTest.LOG.error("Client create operation Assert.failed", e);
                return;
            } finally {
                if ((zk) != null) {
                    try {
                        if (!(close(ClientBase.CONNECTION_TIMEOUT))) {
                            failed = true;
                            AsyncHammerTest.LOG.error("Client did not shutdown");
                        }
                    } catch (InterruptedException e) {
                        AsyncHammerTest.LOG.info("Interrupted", e);
                    }
                }
            }
        }

        private synchronized void incOutstanding() throws InterruptedException {
            (outstanding)++;
            while ((outstanding) > (AsyncHammerTest.HammerThread.MAX_OUTSTANDING)) {
                wait();
            } 
        }

        private synchronized void decOutstanding() {
            (outstanding)--;
            Assert.assertTrue("outstanding >= 0", ((outstanding) >= 0));
            notifyAll();
        }

        public void process(WatchedEvent event) {
            // ignore for purposes of this test
        }

        public void processResult(int rc, String path, Object ctx, String name) {
            if (rc != (OK.intValue())) {
                if (bang) {
                    failed = true;
                    AsyncHammerTest.LOG.error(((((("Create Assert.failed for 0x" + (Long.toHexString(getSessionId()))) + "with rc:") + rc) + " path:") + path));
                }
                decOutstanding();
                return;
            }
            try {
                decOutstanding();
                delete(name, (-1), this, null);
            } catch (Exception e) {
                if (bang) {
                    failed = true;
                    AsyncHammerTest.LOG.error("Client delete Assert.failed", e);
                }
            }
        }

        public void processResult(int rc, String path, Object ctx) {
            if (rc != (OK.intValue())) {
                if (bang) {
                    failed = true;
                    AsyncHammerTest.LOG.error(((((("Delete Assert.failed for 0x" + (Long.toHexString(getSessionId()))) + "with rc:") + rc) + " path:") + path));
                }
            }
        }
    }

    @Test
    public void testHammer() throws Exception {
        setUp(false);
        bang = true;
        AsyncHammerTest.LOG.info("Starting hammers");
        AsyncHammerTest.HammerThread[] hammers = new AsyncHammerTest.HammerThread[100];
        for (int i = 0; i < (hammers.length); i++) {
            hammers[i] = new AsyncHammerTest.HammerThread(("HammerThread-" + i));
            hammers[i].start();
        }
        AsyncHammerTest.LOG.info("Started hammers");
        Thread.sleep(5000);// allow the clients to run for max 5sec

        bang = false;
        AsyncHammerTest.LOG.info("Stopping hammers");
        for (int i = 0; i < (hammers.length); i++) {
            hammers[i].interrupt();
            ClientBase.verifyThreadTerminated(hammers[i], 60000);
            Assert.assertFalse(hammers[i].failed);
        }
        // before restart
        AsyncHammerTest.LOG.info("Hammers stopped, verifying consistency");
        qb.verifyRootOfAllServersMatch(qb.hostPort);
        restart();
        // after restart
        AsyncHammerTest.LOG.info("Verifying hammers 2");
        qb.verifyRootOfAllServersMatch(qb.hostPort);
        tearDown();
    }

    @Test
    public void testObserversHammer() throws Exception {
        setUp(true);
        bang = true;
        Thread[] hammers = new Thread[100];
        for (int i = 0; i < (hammers.length); i++) {
            hammers[i] = new AsyncHammerTest.HammerThread(("HammerThread-" + i));
            hammers[i].start();
        }
        Thread.sleep(5000);// allow the clients to run for max 5sec

        bang = false;
        for (int i = 0; i < (hammers.length); i++) {
            hammers[i].interrupt();
            ClientBase.verifyThreadTerminated(hammers[i], 60000);
        }
        // before restart
        qb.verifyRootOfAllServersMatch(qb.hostPort);
        tearDown();
    }
}

