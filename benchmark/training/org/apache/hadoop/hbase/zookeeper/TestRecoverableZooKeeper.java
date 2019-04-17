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
package org.apache.hadoop.hbase.zookeeper;


import CreateMode.PERSISTENT;
import HConstants.DEFAULT_ZK_SESSION_TIMEOUT;
import HConstants.ZK_SESSION_TIMEOUT;
import Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ZKTests.class, MediumTests.class })
public class TestRecoverableZooKeeper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRecoverableZooKeeper.class);

    private static final HBaseZKTestingUtility TEST_UTIL = new HBaseZKTestingUtility();

    Abortable abortable = new Abortable() {
        @Override
        public void abort(String why, Throwable e) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }
    };

    @Test
    public void testSetDataVersionMismatchInLoop() throws Exception {
        String znode = "/hbase/splitWAL/9af7cfc9b15910a0b3d714bf40a3248f";
        Configuration conf = getConfiguration();
        ZKWatcher zkw = new ZKWatcher(conf, "testSetDataVersionMismatchInLoop", abortable, true);
        String ensemble = ZKConfig.getZKQuorumServersString(conf);
        RecoverableZooKeeper rzk = ZKUtil.connect(conf, ensemble, zkw);
        rzk.create(znode, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        rzk.setData(znode, Bytes.toBytes("OPENING"), 0);
        Field zkField = RecoverableZooKeeper.class.getDeclaredField("zk");
        zkField.setAccessible(true);
        int timeout = conf.getInt(ZK_SESSION_TIMEOUT, DEFAULT_ZK_SESSION_TIMEOUT);
        TestRecoverableZooKeeper.ZookeeperStub zkStub = new TestRecoverableZooKeeper.ZookeeperStub(ensemble, timeout, zkw);
        zkStub.setThrowExceptionInNumOperations(1);
        zkField.set(rzk, zkStub);
        byte[] opened = Bytes.toBytes("OPENED");
        rzk.setData(znode, opened, 1);
        byte[] data = rzk.getData(znode, false, new Stat());
        Assert.assertTrue(Bytes.equals(opened, data));
    }

    class ZookeeperStub extends ZooKeeper {
        private int throwExceptionInNumOperations;

        public ZookeeperStub(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
            super(connectString, sessionTimeout, watcher);
        }

        public void setThrowExceptionInNumOperations(int throwExceptionInNumOperations) {
            this.throwExceptionInNumOperations = throwExceptionInNumOperations;
        }

        private void checkThrowKeeperException() throws KeeperException {
            if ((throwExceptionInNumOperations) == 1) {
                throwExceptionInNumOperations = 0;
                throw new KeeperException.ConnectionLossException();
            }
            if ((throwExceptionInNumOperations) > 0) {
                (throwExceptionInNumOperations)--;
            }
        }

        @Override
        public Stat setData(String path, byte[] data, int version) throws InterruptedException, KeeperException {
            Stat stat = super.setData(path, data, version);
            checkThrowKeeperException();
            return stat;
        }
    }
}
