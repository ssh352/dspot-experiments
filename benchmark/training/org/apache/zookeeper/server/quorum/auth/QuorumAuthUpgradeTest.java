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
package org.apache.zookeeper.server.quorum.auth;


import CreateMode.PERSISTENT;
import CreateMode.PERSISTENT_SEQUENTIAL;
import Ids.OPEN_ACL_UNSAFE;
import QuorumAuth.QUORUM_LEARNER_SASL_AUTH_REQUIRED;
import QuorumAuth.QUORUM_SASL_AUTH_ENABLED;
import QuorumAuth.QUORUM_SERVER_SASL_AUTH_REQUIRED;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.quorum.QuorumPeerTestBase;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Rolling upgrade should do in three steps:
 *
 * step-1) Stop the server and set the flags and restart the server.
 * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=false and quorum.auth.serverRequireSasl=false
 * Ensure that all the servers should complete this step. Now, move to next step.
 *
 * step-2) Stop the server one by one and change the flags and restart the server.
 * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true and quorum.auth.serverRequireSasl=false
 * Ensure that all the servers should complete this step. Now, move to next step.
 *
 * step-3) Stop the server one by one and change the flags and restart the server.
 * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true and quorum.auth.serverRequireSasl=true
 * Now, all the servers are fully upgraded and running in secured mode.
 */
public class QuorumAuthUpgradeTest extends QuorumAuthTestBase {
    static {
        String jaasEntries = new String(("" + (((((((("QuorumServer {\n" + "       org.apache.zookeeper.server.auth.DigestLoginModule required\n") + "       user_test=\"mypassword\";\n") + "};\n") + "QuorumLearner {\n") + "       org.apache.zookeeper.server.auth.DigestLoginModule required\n") + "       username=\"test\"\n") + "       password=\"mypassword\";\n") + "};\n")));
        QuorumAuthTestBase.setupJaasConfig(jaasEntries);
    }

    /**
     * Test to verify that servers are able to start without any authentication.
     * peer0 -> quorum.auth.enableSasl=false
     * peer1 -> quorum.auth.enableSasl=false
     */
    @Test(timeout = 30000)
    public void testNullAuthLearnerServer() throws Exception {
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "false");
        String connectStr = startQuorum(2, authConfigs, 0);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.close();
    }

    /**
     * Test to verify that servers are able to form quorum.
     * peer0 -> quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
     * peer1 -> quorum.auth.enableSasl=false, quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
     */
    @Test(timeout = 30000)
    public void testAuthLearnerAgainstNullAuthServer() throws Exception {
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        String connectStr = startQuorum(2, authConfigs, 1);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.close();
    }

    /**
     * Test to verify that servers are able to form quorum.
     * peer0 -> quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
     * peer1 -> quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
     */
    @Test(timeout = 30000)
    public void testAuthLearnerAgainstNoAuthRequiredServer() throws Exception {
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        String connectStr = startQuorum(2, authConfigs, 2);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.close();
    }

    /**
     * Test to verify that servers are able to form quorum.
     * peer0 -> quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true, quorum.auth.serverRequireSasl=true
     * peer1 -> quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true, quorum.auth.serverRequireSasl=true
     */
    @Test(timeout = 30000)
    public void testAuthLearnerServer() throws Exception {
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "true");
        String connectStr = startQuorum(2, authConfigs, 2);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.close();
    }

    /**
     * Rolling upgrade should do in three steps:
     *
     * step-1) Stop the server and set the flags and restart the server.
     * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=false and quorum.auth.serverRequireSasl=false
     * Ensure that all the servers should complete this step. Now, move to next step.
     *
     * step-2) Stop the server one by one and change the flags and restart the server.
     * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true and quorum.auth.serverRequireSasl=false
     * Ensure that all the servers should complete this step. Now, move to next step.
     *
     * step-3) Stop the server one by one and change the flags and restart the server.
     * quorum.auth.enableSasl=true, quorum.auth.learnerRequireSasl=true and quorum.auth.serverRequireSasl=true
     * Now, all the servers are fully upgraded and running in secured mode.
     */
    @Test(timeout = 90000)
    public void testRollingUpgrade() throws Exception {
        // Start peer0,1,2 servers with quorum.auth.enableSasl=false and
        // quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
        // Assume this is an existing cluster.
        Map<String, String> authConfigs = new HashMap<String, String>();
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "false");
        String connectStr = startQuorum(3, authConfigs, 0);
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(connectStr, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL);
        // 1. Upgrade peer0,1,2 with quorum.auth.enableSasl=true and
        // quorum.auth.learnerRequireSasl=false, quorum.auth.serverRequireSasl=false
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "false");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "false");
        restartServer(authConfigs, 0, zk, watcher);
        restartServer(authConfigs, 1, zk, watcher);
        restartServer(authConfigs, 2, zk, watcher);
        // 2. Upgrade peer0,1,2 with quorum.auth.enableSasl=true and
        // quorum.auth.learnerRequireSasl=true, quorum.auth.serverRequireSasl=false
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "false");
        restartServer(authConfigs, 0, zk, watcher);
        restartServer(authConfigs, 1, zk, watcher);
        restartServer(authConfigs, 2, zk, watcher);
        // 3. Upgrade peer0,1,2 with quorum.auth.enableSasl=true and
        // quorum.auth.learnerRequireSasl=true, quorum.auth.serverRequireSasl=true
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "true");
        authConfigs.put(QUORUM_LEARNER_SASL_AUTH_REQUIRED, "true");
        authConfigs.put(QUORUM_SERVER_SASL_AUTH_REQUIRED, "true");
        restartServer(authConfigs, 0, zk, watcher);
        restartServer(authConfigs, 1, zk, watcher);
        restartServer(authConfigs, 2, zk, watcher);
        // 4. Restart peer2 with quorum.auth.learnerEnableSasl=false and
        // quorum.auth.serverRequireSasl=false. It should fail to join the
        // quorum as this needs auth.
        authConfigs.put(QUORUM_SASL_AUTH_ENABLED, "false");
        QuorumPeerTestBase.MainThread m = shutdown(2);
        startServer(m, authConfigs);
        Assert.assertFalse("waiting for server 2 being up", ClientBase.waitForServerUp(("127.0.0.1:" + (m.getClientPort())), 5000));
    }
}

