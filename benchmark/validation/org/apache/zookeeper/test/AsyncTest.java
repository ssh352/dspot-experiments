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


import Code.NOAUTH;
import CreateMode.PERSISTENT;
import Ids.CREATOR_ALL_ACL;
import Ids.READ_ACL_UNSAFE;
import java.util.LinkedList;
import java.util.List;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncTest extends ZKTestCase implements DataCallback , StringCallback , VoidCallback {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncTest.class);

    private QuorumBase qb = new QuorumBase();

    List<Integer> results = new LinkedList<Integer>();

    @Test
    public void testAsync() throws Exception {
        ZooKeeper zk = null;
        zk = createClient();
        try {
            zk.addAuthInfo("digest", "ben:passwd".getBytes());
            zk.create("/ben", new byte[0], READ_ACL_UNSAFE, PERSISTENT, this, results);
            zk.create("/ben/2", new byte[0], CREATOR_ALL_ACL, PERSISTENT, this, results);
            zk.delete("/ben", (-1), this, results);
            zk.create("/ben2", new byte[0], CREATOR_ALL_ACL, PERSISTENT, this, results);
            zk.getData("/ben2", false, this, results);
            synchronized(results) {
                while ((results.size()) < 5) {
                    results.wait();
                } 
            }
            Assert.assertEquals(0, ((int) (results.get(0))));
            Assert.assertEquals(NOAUTH, Code.get(results.get(1)));
            Assert.assertEquals(0, ((int) (results.get(2))));
            Assert.assertEquals(0, ((int) (results.get(3))));
            Assert.assertEquals(0, ((int) (results.get(4))));
        } finally {
            zk.close();
        }
        zk = createClient();
        try {
            zk.addAuthInfo("digest", "ben:passwd2".getBytes());
            try {
                zk.getData("/ben2", false, new Stat());
                Assert.fail("Should have received a permission error");
            } catch (KeeperException e) {
                Assert.assertEquals(NOAUTH, e.code());
            }
        } finally {
            zk.close();
        }
        zk = createClient();
        try {
            zk.addAuthInfo("digest", "ben:passwd".getBytes());
            zk.getData("/ben2", false, new Stat());
        } finally {
            zk.close();
        }
    }
}
