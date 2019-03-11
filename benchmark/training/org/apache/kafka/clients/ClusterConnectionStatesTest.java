/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients;


import ClientDnsLookup.DEFAULT;
import ClientDnsLookup.USE_ALL_DNS_IPS;
import ConnectionState.AUTHENTICATION_FAILED;
import ConnectionState.CONNECTING;
import ConnectionState.DISCONNECTED;
import ConnectionState.READY;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.utils.MockTime;
import org.junit.Assert;
import org.junit.Test;


public class ClusterConnectionStatesTest {
    private final MockTime time = new MockTime();

    private final long reconnectBackoffMs = 10 * 1000;

    private final long reconnectBackoffMax = 60 * 1000;

    private final double reconnectBackoffJitter = 0.2;

    private final String nodeId1 = "1001";

    private final String nodeId2 = "2002";

    private final String hostTwoIps = "kafka.apache.org";

    private ClusterConnectionStates connectionStates;

    @Test
    public void testClusterConnectionStateChanges() {
        Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
        // Start connecting to Node and check state
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        Assert.assertEquals(connectionStates.connectionState(nodeId1), CONNECTING);
        Assert.assertTrue(connectionStates.isConnecting(nodeId1));
        Assert.assertFalse(connectionStates.isReady(nodeId1, time.milliseconds()));
        Assert.assertFalse(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
        time.sleep(100);
        // Successful connection
        connectionStates.ready(nodeId1);
        Assert.assertEquals(connectionStates.connectionState(nodeId1), READY);
        Assert.assertTrue(connectionStates.isReady(nodeId1, time.milliseconds()));
        Assert.assertTrue(connectionStates.hasReadyNodes(time.milliseconds()));
        Assert.assertFalse(connectionStates.isConnecting(nodeId1));
        Assert.assertFalse(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        Assert.assertEquals(connectionStates.connectionDelay(nodeId1, time.milliseconds()), Long.MAX_VALUE);
        time.sleep(15000);
        // Disconnected from broker
        connectionStates.disconnected(nodeId1, time.milliseconds());
        Assert.assertEquals(connectionStates.connectionState(nodeId1), DISCONNECTED);
        Assert.assertTrue(connectionStates.isDisconnected(nodeId1));
        Assert.assertTrue(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        Assert.assertFalse(connectionStates.isConnecting(nodeId1));
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
        Assert.assertFalse(connectionStates.canConnect(nodeId1, time.milliseconds()));
        // After disconnecting we expect a backoff value equal to the reconnect.backoff.ms setting (plus minus 20% jitter)
        double backoffTolerance = (reconnectBackoffMs) * (reconnectBackoffJitter);
        long currentBackoff = connectionStates.connectionDelay(nodeId1, time.milliseconds());
        Assert.assertEquals(reconnectBackoffMs, currentBackoff, backoffTolerance);
        time.sleep((currentBackoff + 1));
        // after waiting for the current backoff value we should be allowed to connect again
        Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
    }

    @Test
    public void testMultipleNodeConnectionStates() {
        // Check initial state, allowed to connect to all nodes, but no nodes shown as ready
        Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
        Assert.assertTrue(connectionStates.canConnect(nodeId2, time.milliseconds()));
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
        // Start connecting one node and check that the pool only shows ready nodes after
        // successful connect
        connectionStates.connecting(nodeId2, time.milliseconds(), "localhost", DEFAULT);
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
        time.sleep(1000);
        connectionStates.ready(nodeId2);
        Assert.assertTrue(connectionStates.hasReadyNodes(time.milliseconds()));
        // Connect second node and check that both are shown as ready, pool should immediately
        // show ready nodes, since node2 is already connected
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        Assert.assertTrue(connectionStates.hasReadyNodes(time.milliseconds()));
        time.sleep(1000);
        connectionStates.ready(nodeId1);
        Assert.assertTrue(connectionStates.hasReadyNodes(time.milliseconds()));
        time.sleep(12000);
        // disconnect nodes and check proper state of pool throughout
        connectionStates.disconnected(nodeId2, time.milliseconds());
        Assert.assertTrue(connectionStates.hasReadyNodes(time.milliseconds()));
        Assert.assertTrue(connectionStates.isBlackedOut(nodeId2, time.milliseconds()));
        Assert.assertFalse(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        time.sleep(connectionStates.connectionDelay(nodeId2, time.milliseconds()));
        // by the time node1 disconnects node2 should have been unblocked again
        connectionStates.disconnected(nodeId1, ((time.milliseconds()) + 1));
        Assert.assertTrue(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        Assert.assertFalse(connectionStates.isBlackedOut(nodeId2, time.milliseconds()));
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
    }

    @Test
    public void testAuthorizationFailed() {
        // Try connecting
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        time.sleep(100);
        connectionStates.authenticationFailed(nodeId1, time.milliseconds(), new AuthenticationException("No path to CA for certificate!"));
        time.sleep(1000);
        Assert.assertEquals(connectionStates.connectionState(nodeId1), AUTHENTICATION_FAILED);
        Assert.assertTrue(((connectionStates.authenticationException(nodeId1)) instanceof AuthenticationException));
        Assert.assertFalse(connectionStates.hasReadyNodes(time.milliseconds()));
        Assert.assertFalse(connectionStates.canConnect(nodeId1, time.milliseconds()));
        time.sleep(((connectionStates.connectionDelay(nodeId1, time.milliseconds())) + 1));
        Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
        connectionStates.ready(nodeId1);
        Assert.assertNull(connectionStates.authenticationException(nodeId1));
    }

    @Test
    public void testRemoveNode() {
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        time.sleep(1000);
        connectionStates.ready(nodeId1);
        time.sleep(10000);
        connectionStates.disconnected(nodeId1, time.milliseconds());
        // Node is disconnected and blocked, removing it from the list should reset all blocks
        connectionStates.remove(nodeId1);
        Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
        Assert.assertFalse(connectionStates.isBlackedOut(nodeId1, time.milliseconds()));
        Assert.assertEquals(connectionStates.connectionDelay(nodeId1, time.milliseconds()), 0L);
    }

    @Test
    public void testMaxReconnectBackoff() {
        long effectiveMaxReconnectBackoff = Math.round(((reconnectBackoffMax) * (1 + (reconnectBackoffJitter))));
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        time.sleep(1000);
        connectionStates.disconnected(nodeId1, time.milliseconds());
        // Do 100 reconnect attempts and check that MaxReconnectBackoff (plus jitter) is not exceeded
        for (int i = 0; i < 100; i++) {
            long reconnectBackoff = connectionStates.connectionDelay(nodeId1, time.milliseconds());
            Assert.assertTrue((reconnectBackoff <= effectiveMaxReconnectBackoff));
            Assert.assertFalse(connectionStates.canConnect(nodeId1, time.milliseconds()));
            time.sleep((reconnectBackoff + 1));
            Assert.assertTrue(connectionStates.canConnect(nodeId1, time.milliseconds()));
            connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
            time.sleep(10);
            connectionStates.disconnected(nodeId1, time.milliseconds());
        }
    }

    @Test
    public void testExponentialReconnectBackoff() {
        // Calculate fixed components for backoff process
        final int reconnectBackoffExpBase = 2;
        double reconnectBackoffMaxExp = (Math.log(((reconnectBackoffMax) / ((double) (Math.max(reconnectBackoffMs, 1)))))) / (Math.log(reconnectBackoffExpBase));
        // Run through 10 disconnects and check that reconnect backoff value is within expected range for every attempt
        for (int i = 0; i < 10; i++) {
            connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
            connectionStates.disconnected(nodeId1, time.milliseconds());
            // Calculate expected backoff value without jitter
            long expectedBackoff = Math.round(((Math.pow(reconnectBackoffExpBase, Math.min(i, reconnectBackoffMaxExp))) * (reconnectBackoffMs)));
            long currentBackoff = connectionStates.connectionDelay(nodeId1, time.milliseconds());
            Assert.assertEquals(expectedBackoff, currentBackoff, ((reconnectBackoffJitter) * expectedBackoff));
            time.sleep(((connectionStates.connectionDelay(nodeId1, time.milliseconds())) + 1));
        }
    }

    @Test
    public void testThrottled() {
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        time.sleep(1000);
        connectionStates.ready(nodeId1);
        time.sleep(10000);
        // Initially not throttled.
        Assert.assertEquals(0, connectionStates.throttleDelayMs(nodeId1, time.milliseconds()));
        // Throttle for 100ms from now.
        connectionStates.throttle(nodeId1, ((time.milliseconds()) + 100));
        Assert.assertEquals(100, connectionStates.throttleDelayMs(nodeId1, time.milliseconds()));
        // Still throttled after 50ms. The remaining delay is 50ms. The poll delay should be same as throttling delay.
        time.sleep(50);
        Assert.assertEquals(50, connectionStates.throttleDelayMs(nodeId1, time.milliseconds()));
        Assert.assertEquals(50, connectionStates.pollDelayMs(nodeId1, time.milliseconds()));
        // Not throttled anymore when the deadline is reached. The poll delay should be same as connection delay.
        time.sleep(50);
        Assert.assertEquals(0, connectionStates.throttleDelayMs(nodeId1, time.milliseconds()));
        Assert.assertEquals(connectionStates.connectionDelay(nodeId1, time.milliseconds()), connectionStates.pollDelayMs(nodeId1, time.milliseconds()));
    }

    @Test
    public void testSingleIPWithDefault() throws UnknownHostException {
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        InetAddress currAddress = connectionStates.currentAddress(nodeId1);
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        Assert.assertSame(currAddress, connectionStates.currentAddress(nodeId1));
    }

    @Test
    public void testSingleIPWithUseAll() throws UnknownHostException {
        Assert.assertEquals(1, ClientUtils.resolve("localhost", USE_ALL_DNS_IPS).size());
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", USE_ALL_DNS_IPS);
        InetAddress currAddress = connectionStates.currentAddress(nodeId1);
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", USE_ALL_DNS_IPS);
        Assert.assertSame(currAddress, connectionStates.currentAddress(nodeId1));
    }

    @Test
    public void testMultipleIPsWithDefault() throws UnknownHostException {
        Assert.assertEquals(2, ClientUtils.resolve(hostTwoIps, USE_ALL_DNS_IPS).size());
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, DEFAULT);
        InetAddress currAddress = connectionStates.currentAddress(nodeId1);
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, DEFAULT);
        Assert.assertSame(currAddress, connectionStates.currentAddress(nodeId1));
    }

    @Test
    public void testMultipleIPsWithUseAll() throws UnknownHostException {
        Assert.assertEquals(2, ClientUtils.resolve(hostTwoIps, USE_ALL_DNS_IPS).size());
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, USE_ALL_DNS_IPS);
        InetAddress addr1 = connectionStates.currentAddress(nodeId1);
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, USE_ALL_DNS_IPS);
        InetAddress addr2 = connectionStates.currentAddress(nodeId1);
        Assert.assertNotSame(addr1, addr2);
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, USE_ALL_DNS_IPS);
        InetAddress addr3 = connectionStates.currentAddress(nodeId1);
        Assert.assertSame(addr1, addr3);
    }

    @Test
    public void testHostResolveChange() throws ReflectiveOperationException, UnknownHostException {
        Assert.assertEquals(2, ClientUtils.resolve(hostTwoIps, USE_ALL_DNS_IPS).size());
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, DEFAULT);
        InetAddress addr1 = connectionStates.currentAddress(nodeId1);
        // reflection to simulate host change in DNS lookup
        Method nodeStateMethod = connectionStates.getClass().getDeclaredMethod("nodeState", String.class);
        nodeStateMethod.setAccessible(true);
        Object nodeState = nodeStateMethod.invoke(connectionStates, nodeId1);
        Field hostField = nodeState.getClass().getDeclaredField("host");
        hostField.setAccessible(true);
        hostField.set(nodeState, "localhost");
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        InetAddress addr2 = connectionStates.currentAddress(nodeId1);
        Assert.assertNotSame(addr1, addr2);
    }

    @Test
    public void testNodeWithNewHostname() throws UnknownHostException {
        connectionStates.connecting(nodeId1, time.milliseconds(), "localhost", DEFAULT);
        InetAddress addr1 = connectionStates.currentAddress(nodeId1);
        connectionStates.connecting(nodeId1, time.milliseconds(), hostTwoIps, DEFAULT);
        InetAddress addr2 = connectionStates.currentAddress(nodeId1);
        Assert.assertNotSame(addr1, addr2);
    }
}

