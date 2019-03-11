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


import StaticHostProvider.Resolver;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.StaticHostProvider;
import org.apache.zookeeper.common.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StaticHostProviderTest extends ZKTestCase {
    private Random r = new Random(1);

    @Test
    public void testNextGoesRound() {
        HostProvider hostProvider = getHostProvider(((byte) (2)));
        InetSocketAddress first = hostProvider.next(0);
        Assert.assertTrue((first != null));
        hostProvider.next(0);
        Assert.assertEquals(first, hostProvider.next(0));
    }

    @Test
    public void testNextGoesRoundAndSleeps() {
        byte size = 2;
        HostProvider hostProvider = getHostProvider(size);
        while (size > 0) {
            hostProvider.next(0);
            --size;
        } 
        long start = Time.currentElapsedTime();
        hostProvider.next(1000);
        long stop = Time.currentElapsedTime();
        Assert.assertTrue((900 <= (stop - start)));
    }

    @Test
    public void testNextDoesNotSleepForZero() {
        byte size = 2;
        HostProvider hostProvider = getHostProvider(size);
        while (size > 0) {
            hostProvider.next(0);
            --size;
        } 
        long start = Time.currentElapsedTime();
        hostProvider.next(0);
        long stop = Time.currentElapsedTime();
        Assert.assertTrue((5 > (stop - start)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyServerAddressesList() {
        HostProvider hp = new StaticHostProvider(new ArrayList());
    }

    @Test
    public void testInvalidHostAddresses() {
        // Arrange
        final List<InetSocketAddress> invalidAddresses = new ArrayList<>();
        InetSocketAddress unresolved = InetSocketAddress.createUnresolved("a", 1234);
        invalidAddresses.add(unresolved);
        StaticHostProvider.Resolver resolver = new StaticHostProvider.Resolver() {
            @Override
            public InetAddress[] getAllByName(String name) throws UnknownHostException {
                throw new UnknownHostException();
            }
        };
        StaticHostProvider sp = new StaticHostProvider(invalidAddresses, resolver);
        // Act & Assert
        InetSocketAddress n1 = sp.next(0);
        Assert.assertTrue("Provider should return unresolved address is host is unresolvable", n1.isUnresolved());
        Assert.assertSame("Provider should return original address is host is unresolvable", unresolved, n1);
    }

    @Test
    public void testTwoConsequitiveCallsToNextReturnDifferentElement() {
        HostProvider hostProvider = getHostProvider(((byte) (2)));
        Assert.assertNotSame(hostProvider.next(0), hostProvider.next(0));
    }

    @Test
    public void testOnConnectDoesNotReset() {
        HostProvider hostProvider = getHostProvider(((byte) (2)));
        InetSocketAddress first = hostProvider.next(0);
        hostProvider.onConnected();
        InetSocketAddress second = hostProvider.next(0);
        Assert.assertNotSame(first, second);
    }

    /* Reconfig tests with IP addresses */
    private final double slackPercent = 10;

    private final int numClients = 10000;

    @Test
    public void testUpdateClientMigrateOrNot() throws UnknownHostException {
        HostProvider hostProvider = getHostProvider(((byte) (4)));// 10.10.10.4:1238, 10.10.10.3:1237, 10.10.10.2:1236, 10.10.10.1:1235

        Collection<InetSocketAddress> newList = getServerAddresses(((byte) (3)));// 10.10.10.3:1237, 10.10.10.2:1236, 10.10.10.1:1235

        InetSocketAddress myServer = new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, 3 }), 1237);
        // Number of machines becomes smaller, my server is in the new cluster
        boolean disconnectRequired = hostProvider.updateServerList(newList, myServer);
        Assert.assertFalse(disconnectRequired);
        hostProvider.onConnected();
        // Number of machines stayed the same, my server is in the new cluster
        disconnectRequired = hostProvider.updateServerList(newList, myServer);
        Assert.assertFalse(disconnectRequired);
        hostProvider.onConnected();
        // Number of machines became smaller, my server is not in the new
        // cluster
        newList = getServerAddresses(((byte) (2)));// 10.10.10.2:1236, 10.10.10.1:1235

        disconnectRequired = hostProvider.updateServerList(newList, myServer);
        Assert.assertTrue(disconnectRequired);
        hostProvider.onConnected();
        // Number of machines stayed the same, my server is not in the new
        // cluster
        disconnectRequired = hostProvider.updateServerList(newList, myServer);
        Assert.assertTrue(disconnectRequired);
        hostProvider.onConnected();
        // Number of machines increased, my server is not in the new cluster
        newList = new ArrayList<InetSocketAddress>(3);
        for (byte i = 4; i > 1; i--) {
            // 10.10.10.4:1238, 10.10.10.3:1237, 10.10.10.2:1236
            newList.add(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, i }), (1234 + i)));
        }
        myServer = new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, 1 }), 1235);
        disconnectRequired = hostProvider.updateServerList(newList, myServer);
        Assert.assertTrue(disconnectRequired);
        hostProvider.onConnected();
        // Number of machines increased, my server is in the new cluster
        // Here whether to move or not depends on the difference of cluster
        // sizes
        // With probability 1 - |old|/|new} the client disconnects
        // In the test below 1-9/10 = 1/10 chance of disconnecting
        HostProvider[] hostProviderArray = new HostProvider[numClients];
        newList = getServerAddresses(((byte) (10)));
        int numDisconnects = 0;
        for (int i = 0; i < (numClients); i++) {
            hostProviderArray[i] = getHostProvider(((byte) (9)));
            disconnectRequired = hostProviderArray[i].updateServerList(newList, myServer);
            if (disconnectRequired)
                numDisconnects++;

        }
        hostProvider.onConnected();
        // should be numClients/10 in expectation, we test that its numClients/10 +- slackPercent
        Assert.assertTrue((numDisconnects < (upperboundCPS(numClients, 10))));
    }

    @Test
    public void testUpdateMigrationGoesRound() throws UnknownHostException {
        HostProvider hostProvider = getHostProvider(((byte) (4)));
        // old list (just the ports): 1238, 1237, 1236, 1235
        Collection<InetSocketAddress> newList = new ArrayList<InetSocketAddress>(10);
        for (byte i = 12; i > 2; i--) {
            // 1246, 1245, 1244, 1243, 1242, 1241,
            // 1240, 1239, 1238, 1237
            newList.add(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, i }), (1234 + i)));
        }
        // servers from the old list that appear in the new list
        Collection<InetSocketAddress> oldStaying = new ArrayList<InetSocketAddress>(2);
        for (byte i = 4; i > 2; i--) {
            // 1238, 1237
            oldStaying.add(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, i }), (1234 + i)));
        }
        // servers in the new list that are not in the old list
        Collection<InetSocketAddress> newComing = new ArrayList<InetSocketAddress>(10);
        for (byte i = 12; i > 4; i--) {
            // 1246, 1245, 1244, 1243, 1242, 1241, 1240, 1139
            newComing.add(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, i }), (1234 + i)));
        }
        // Number of machines increases, my server is not in the new cluster
        // load on old servers must be decreased, so must connect to one of the
        // new servers
        // i.e., pNew = 1.
        boolean disconnectRequired = hostProvider.updateServerList(newList, new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, 1 }), 1235));
        Assert.assertTrue(disconnectRequired);
        // This means reconfigMode = true, and nextHostInReconfigMode will be
        // called from next
        // Since pNew = 1 we should first try the new servers
        ArrayList<InetSocketAddress> seen = new ArrayList<InetSocketAddress>();
        for (int i = 0; i < (newComing.size()); i++) {
            InetSocketAddress addr = hostProvider.next(0);
            Assert.assertTrue(newComing.contains(addr));
            Assert.assertTrue((!(seen.contains(addr))));
            seen.add(addr);
        }
        // Next the old servers
        seen.clear();
        for (int i = 0; i < (oldStaying.size()); i++) {
            InetSocketAddress addr = hostProvider.next(0);
            Assert.assertTrue(oldStaying.contains(addr));
            Assert.assertTrue((!(seen.contains(addr))));
            seen.add(addr);
        }
        // And now it goes back to normal next() so it should be everything
        // together like in testNextGoesRound()
        InetSocketAddress first = hostProvider.next(0);
        Assert.assertTrue((first != null));
        for (int i = 0; i < ((newList.size()) - 1); i++) {
            hostProvider.next(0);
        }
        Assert.assertEquals(first, hostProvider.next(0));
        hostProvider.onConnected();
    }

    @Test
    public void testUpdateLoadBalancing() throws UnknownHostException {
        // Start with 9 servers and 10000 clients
        boolean disconnectRequired;
        HostProvider[] hostProviderArray = new HostProvider[numClients];
        InetSocketAddress[] curHostForEachClient = new InetSocketAddress[numClients];
        int[] numClientsPerHost = new int[9];
        // initialization
        for (int i = 0; i < (numClients); i++) {
            hostProviderArray[i] = getHostProvider(((byte) (9)));
            curHostForEachClient[i] = hostProviderArray[i].next(0);
            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 9))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 9))));
            numClientsPerHost[i] = 0;// prepare for next test

        }
        // remove host number 8 (the last one in a list of 9 hosts)
        Collection<InetSocketAddress> newList = getServerAddresses(((byte) (8)));
        for (int i = 0; i < (numClients); i++) {
            disconnectRequired = hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 8; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 8))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 8))));
            numClientsPerHost[i] = 0;// prepare for next test

        }
        Assert.assertTrue(((numClientsPerHost[8]) == 0));
        // remove hosts number 6 and 7 (the currently last two in the list)
        newList = getServerAddresses(((byte) (6)));
        for (int i = 0; i < (numClients); i++) {
            disconnectRequired = hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 6))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 6))));
            numClientsPerHost[i] = 0;// prepare for next test

        }
        Assert.assertTrue(((numClientsPerHost[6]) == 0));
        Assert.assertTrue(((numClientsPerHost[7]) == 0));
        Assert.assertTrue(((numClientsPerHost[8]) == 0));
        // remove host number 0 (the first one in the current list)
        // and add back hosts 6, 7 and 8
        newList = new ArrayList<InetSocketAddress>(8);
        for (byte i = 9; i > 1; i--) {
            newList.add(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 10, 10, 10, i }), (1234 + i)));
        }
        for (int i = 0; i < (numClients); i++) {
            disconnectRequired = hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        Assert.assertTrue(((numClientsPerHost[0]) == 0));
        for (int i = 1; i < 9; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 8))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 8))));
            numClientsPerHost[i] = 0;// prepare for next test

        }
        // add back host number 0
        newList = getServerAddresses(((byte) (9)));
        for (int i = 0; i < (numClients); i++) {
            disconnectRequired = hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 9))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 9))));
        }
    }

    @Test
    public void testNoCurrentHostDuringNormalMode() throws UnknownHostException {
        // Start with 9 servers and 10000 clients
        boolean disconnectRequired;
        StaticHostProvider[] hostProviderArray = new StaticHostProvider[numClients];
        InetSocketAddress[] curHostForEachClient = new InetSocketAddress[numClients];
        int[] numClientsPerHost = new int[9];
        // initialization
        for (int i = 0; i < (numClients); i++) {
            hostProviderArray[i] = getHostProvider(((byte) (9)));
            if (i >= ((numClients) / 2)) {
                curHostForEachClient[i] = hostProviderArray[i].next(0);
            } else {
                // its supposed to be the first server on serverList.
                // we'll set it later, see below (*)
                curHostForEachClient[i] = null;
            }
        }
        // remove hosts 7 and 8 (the last two in a list of 9 hosts)
        Collection<InetSocketAddress> newList = getServerAddresses(((byte) (7)));
        for (int i = 0; i < (numClients); i++) {
            // tests the case currentHost == null && lastIndex == -1
            // calls next for clients with index < numClients/2
            disconnectRequired = hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);
            else
                if ((curHostForEachClient[i]) == null) {
                    // (*) setting it to what it should be
                    curHostForEachClient[i] = hostProviderArray[i].getServerAtIndex(0);
                }

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            // sets lastIndex, resets reconfigMode
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 7))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 7))));
            numClientsPerHost[i] = 0;// prepare for next test

        }
        Assert.assertTrue(((numClientsPerHost[7]) == 0));
        Assert.assertTrue(((numClientsPerHost[8]) == 0));
        // add back server 7
        newList = getServerAddresses(((byte) (8)));
        for (int i = 0; i < (numClients); i++) {
            InetSocketAddress myServer = (i < ((numClients) / 2)) ? null : curHostForEachClient[i];
            // tests the case currentHost == null && lastIndex >= 0
            disconnectRequired = hostProviderArray[i].updateServerList(newList, myServer);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);

            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 8; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 8))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 8))));
        }
    }

    @Test
    public void testReconfigDuringReconfigMode() throws UnknownHostException {
        // Start with 9 servers and 10000 clients
        boolean disconnectRequired;
        StaticHostProvider[] hostProviderArray = new StaticHostProvider[numClients];
        InetSocketAddress[] curHostForEachClient = new InetSocketAddress[numClients];
        int[] numClientsPerHost = new int[9];
        // initialization
        for (int i = 0; i < (numClients); i++) {
            hostProviderArray[i] = getHostProvider(((byte) (9)));
            curHostForEachClient[i] = hostProviderArray[i].next(0);
        }
        // remove hosts 7 and 8 (the last two in a list of 9 hosts)
        Collection<InetSocketAddress> newList = getServerAddresses(((byte) (7)));
        for (int i = 0; i < (numClients); i++) {
            // sets reconfigMode
            hostProviderArray[i].updateServerList(newList, curHostForEachClient[i]);
        }
        // add back servers 7 and 8 while still in reconfigMode (we didn't call
        // next)
        newList = getServerAddresses(((byte) (9)));
        for (int i = 0; i < (numClients); i++) {
            InetSocketAddress myServer = (i < ((numClients) / 2)) ? null : curHostForEachClient[i];
            // for i < (numClients/2) this tests the case currentHost == null &&
            // reconfigMode = true
            // for i >= (numClients/2) this tests the case currentHost!=null &&
            // reconfigMode = true
            disconnectRequired = hostProviderArray[i].updateServerList(newList, myServer);
            if (disconnectRequired)
                curHostForEachClient[i] = hostProviderArray[i].next(0);
            else {
                // currentIndex was set by the call to updateServerList, which
                // called next
                curHostForEachClient[i] = hostProviderArray[i].getServerAtCurrentIndex();
            }
            (numClientsPerHost[((curHostForEachClient[i].getPort()) - 1235)])++;
            hostProviderArray[i].onConnected();
        }
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(((numClientsPerHost[i]) <= (upperboundCPS(numClients, 9))));
            Assert.assertTrue(((numClientsPerHost[i]) >= (lowerboundCPS(numClients, 9))));
        }
    }

    /* Reconfig test with unresolved hostnames */
    /**
     * Number of machines becomes smaller, my server is in the new cluster
     */
    @Test
    public void testUpdateServerList_UnresolvedHostnames_NoDisconnection1() {
        // Arrange
        // [testhost-4.testdomain.com:1238, testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        HostProvider hostProvider = getHostProviderWithUnresolvedHostnames(4);
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        Collection<InetSocketAddress> newList = getUnresolvedHostnames(3);
        InetSocketAddress myServer = InetSocketAddress.createUnresolved("testhost-3.testdomain.com", 1237);
        // Act
        boolean disconnectRequired = hostProvider.updateServerList(newList, myServer);
        // Assert
        Assert.assertFalse(disconnectRequired);
        hostProvider.onConnected();
    }

    /**
     * Number of machines stayed the same, my server is in the new cluster
     */
    @Test
    public void testUpdateServerList_UnresolvedHostnames_NoDisconnection2() {
        // Arrange
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        HostProvider hostProvider = getHostProviderWithUnresolvedHostnames(3);
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        Collection<InetSocketAddress> newList = getUnresolvedHostnames(3);
        InetSocketAddress myServer = InetSocketAddress.createUnresolved("testhost-3.testdomain.com", 1237);
        // Act
        boolean disconnectRequired = hostProvider.updateServerList(newList, myServer);
        // Assert
        Assert.assertFalse(disconnectRequired);
        hostProvider.onConnected();
    }

    /**
     * Number of machines became smaller, my server is not in the new cluster
     */
    @Test
    public void testUpdateServerList_UnresolvedHostnames_Disconnection1() {
        // Arrange
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        HostProvider hostProvider = getHostProviderWithUnresolvedHostnames(3);
        // [testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        Collection<InetSocketAddress> newList = getUnresolvedHostnames(2);
        InetSocketAddress myServer = InetSocketAddress.createUnresolved("testhost-3.testdomain.com", 1237);
        // Act
        boolean disconnectRequired = hostProvider.updateServerList(newList, myServer);
        // Assert
        Assert.assertTrue(disconnectRequired);
        hostProvider.onConnected();
    }

    /**
     * Number of machines stayed the same, my server is not in the new cluster
     */
    @Test
    public void testUpdateServerList_UnresolvedHostnames_Disconnection2() {
        // Arrange
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        HostProvider hostProvider = getHostProviderWithUnresolvedHostnames(3);
        // [testhost-3.testdomain.com:1237, testhost-2.testdomain.com:1236, testhost-1.testdomain.com:1235]
        Collection<InetSocketAddress> newList = getUnresolvedHostnames(3);
        InetSocketAddress myServer = InetSocketAddress.createUnresolved("testhost-4.testdomain.com", 1237);
        // Act
        boolean disconnectRequired = hostProvider.updateServerList(newList, myServer);
        // Assert
        Assert.assertTrue(disconnectRequired);
        hostProvider.onConnected();
    }

    @Test
    public void testUpdateServerList_ResolvedWithUnResolvedAddress_ForceDisconnect() {
        // Arrange
        // Create a HostProvider with a list of unresolved server address(es)
        List<InetSocketAddress> addresses = Collections.singletonList(InetSocketAddress.createUnresolved("testhost-1.resolvable.zk", 1235));
        HostProvider hostProvider = new StaticHostProvider(addresses, new StaticHostProviderTest.TestResolver());
        InetSocketAddress currentHost = hostProvider.next(100);
        Assert.assertThat("CurrentHost is which the client is currently connecting to, it should be resolved", currentHost.isUnresolved(), Is.is(false));
        // Act
        InetSocketAddress replaceHost = InetSocketAddress.createUnresolved("testhost-1.resolvable.zk", 1235);
        Assert.assertThat("Replace host must be unresolved in this test case", replaceHost.isUnresolved(), Is.is(true));
        boolean disconnect = hostProvider.updateServerList(new ArrayList(Collections.singletonList(replaceHost)), currentHost);
        // Assert
        Assert.assertThat(disconnect, Is.is(false));
    }

    @Test
    public void testUpdateServerList_ResolvedWithResolvedAddress_NoDisconnect() throws UnknownHostException {
        // Arrange
        // Create a HostProvider with a list of unresolved server address(es)
        List<InetSocketAddress> addresses = Collections.singletonList(InetSocketAddress.createUnresolved("testhost-1.resolvable.zk", 1235));
        HostProvider hostProvider = new StaticHostProvider(addresses, new StaticHostProviderTest.TestResolver());
        InetSocketAddress currentHost = hostProvider.next(100);
        Assert.assertThat("CurrentHost is which the client is currently connecting to, it should be resolved", currentHost.isUnresolved(), Is.is(false));
        // Act
        InetSocketAddress replaceHost = new InetSocketAddress(InetAddress.getByAddress(currentHost.getHostString(), currentHost.getAddress().getAddress()), currentHost.getPort());
        Assert.assertThat("Replace host must be resolved in this test case", replaceHost.isUnresolved(), Is.is(false));
        boolean disconnect = hostProvider.updateServerList(new ArrayList(Collections.singletonList(replaceHost)), currentHost);
        // Assert
        Assert.assertThat(disconnect, CoreMatchers.equalTo(false));
    }

    @Test
    public void testUpdateServerList_UnResolvedWithUnResolvedAddress_ForceDisconnect() {
        // Arrange
        // Create a HostProvider with a list of unresolved server address(es)
        List<InetSocketAddress> addresses = Collections.singletonList(InetSocketAddress.createUnresolved("testhost-1.zookeepertest.zk", 1235));
        HostProvider hostProvider = new StaticHostProvider(addresses, new StaticHostProviderTest.TestResolver());
        InetSocketAddress currentHost = hostProvider.next(100);
        Assert.assertThat("CurrentHost is not resolvable in this test case", currentHost.isUnresolved(), Is.is(true));
        // Act
        InetSocketAddress replaceHost = InetSocketAddress.createUnresolved("testhost-1.resolvable.zk", 1235);
        Assert.assertThat("Replace host must be unresolved in this test case", replaceHost.isUnresolved(), Is.is(true));
        boolean disconnect = hostProvider.updateServerList(new ArrayList(Collections.singletonList(replaceHost)), currentHost);
        // Assert
        Assert.assertThat(disconnect, Is.is(true));
    }

    @Test
    public void testUpdateServerList_UnResolvedWithResolvedAddress_ForceDisconnect() throws UnknownHostException {
        // Arrange
        // Create a HostProvider with a list of unresolved server address(es)
        List<InetSocketAddress> addresses = Collections.singletonList(InetSocketAddress.createUnresolved("testhost-1.zookeepertest.zk", 1235));
        HostProvider hostProvider = new StaticHostProvider(addresses, new StaticHostProviderTest.TestResolver());
        InetSocketAddress currentHost = hostProvider.next(100);
        Assert.assertThat("CurrentHost not resolvable in this test case", currentHost.isUnresolved(), Is.is(true));
        // Act
        byte[] addr = new byte[]{ 10, 0, 0, 1 };
        InetSocketAddress replaceHost = new InetSocketAddress(InetAddress.getByAddress(currentHost.getHostString(), addr), currentHost.getPort());
        Assert.assertThat("Replace host must be resolved in this test case", replaceHost.isUnresolved(), Is.is(false));
        boolean disconnect = hostProvider.updateServerList(new ArrayList(Collections.singletonList(replaceHost)), currentHost);
        // Assert
        Assert.assertThat(disconnect, CoreMatchers.equalTo(false));
    }

    private class TestResolver implements StaticHostProvider.Resolver {
        private byte counter = 1;

        @Override
        public InetAddress[] getAllByName(String name) throws UnknownHostException {
            if (name.contains("resolvable")) {
                byte[] addr = new byte[]{ 10, 0, 0, ((byte) (((counter)++) % 10)) };
                return new InetAddress[]{ InetAddress.getByAddress(name, addr) };
            }
            throw new UnknownHostException();
        }
    }

    /* DNS resolution tests */
    @Test
    public void testLiteralIPNoReverseNS() {
        byte size = 30;
        HostProvider hostProvider = getHostProviderUnresolved(size);
        for (int i = 0; i < size; i++) {
            InetSocketAddress next = hostProvider.next(0);
            Assert.assertThat(next, CoreMatchers.instanceOf(InetSocketAddress.class));
            Assert.assertFalse(next.isUnresolved());
            Assert.assertTrue(next.toString().startsWith("/"));
            // Do NOT trigger the reverse name service lookup.
            String hostname = next.getHostString();
            // In this case, the hostname equals literal IP address.
            Assert.assertEquals(next.getAddress().getHostAddress(), hostname);
        }
    }

    @Test
    public void testReResolvingSingle() throws UnknownHostException {
        // Arrange
        byte size = 1;
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>(size);
        // Test a hostname that resolves to a single address
        list.add(InetSocketAddress.createUnresolved("issues.apache.org", 1234));
        final InetAddress issuesApacheOrg = Mockito.mock(InetAddress.class);
        Mockito.when(issuesApacheOrg.getHostAddress()).thenReturn("192.168.1.1");
        Mockito.when(issuesApacheOrg.toString()).thenReturn("issues.apache.org");
        Mockito.when(issuesApacheOrg.getHostName()).thenReturn("issues.apache.org");
        StaticHostProvider.Resolver resolver = new StaticHostProvider.Resolver() {
            @Override
            public InetAddress[] getAllByName(String name) {
                return new InetAddress[]{ issuesApacheOrg };
            }
        };
        StaticHostProvider.Resolver spyResolver = Mockito.spy(resolver);
        // Act
        StaticHostProvider hostProvider = new StaticHostProvider(list, spyResolver);
        for (int i = 0; i < 10; i++) {
            InetSocketAddress next = hostProvider.next(0);
            Assert.assertEquals(issuesApacheOrg, next.getAddress());
        }
        // Assert
        // Resolver called 10 times, because we shouldn't cache the resolved addresses
        Mockito.verify(spyResolver, Mockito.times(10)).getAllByName("issues.apache.org");// resolution occurred

    }

    @Test
    public void testReResolvingMultiple() throws UnknownHostException {
        // Arrange
        byte size = 1;
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>(size);
        // Test a hostname that resolves to multiple addresses
        list.add(InetSocketAddress.createUnresolved("www.apache.org", 1234));
        final InetAddress apacheOrg1 = Mockito.mock(InetAddress.class);
        Mockito.when(apacheOrg1.getHostAddress()).thenReturn("192.168.1.1");
        Mockito.when(apacheOrg1.toString()).thenReturn("www.apache.org");
        Mockito.when(apacheOrg1.getHostName()).thenReturn("www.apache.org");
        final InetAddress apacheOrg2 = Mockito.mock(InetAddress.class);
        Mockito.when(apacheOrg2.getHostAddress()).thenReturn("192.168.1.2");
        Mockito.when(apacheOrg2.toString()).thenReturn("www.apache.org");
        Mockito.when(apacheOrg2.getHostName()).thenReturn("www.apache.org");
        final List<InetAddress> resolvedAddresses = new ArrayList<InetAddress>();
        resolvedAddresses.add(apacheOrg1);
        resolvedAddresses.add(apacheOrg2);
        StaticHostProvider.Resolver resolver = new StaticHostProvider.Resolver() {
            @Override
            public InetAddress[] getAllByName(String name) {
                return resolvedAddresses.toArray(new InetAddress[resolvedAddresses.size()]);
            }
        };
        StaticHostProvider.Resolver spyResolver = Mockito.spy(resolver);
        // Act & Assert
        StaticHostProvider hostProvider = new StaticHostProvider(list, spyResolver);
        Assert.assertEquals(1, hostProvider.size());// single address not extracted

        for (int i = 0; i < 10; i++) {
            InetSocketAddress next = hostProvider.next(0);
            Assert.assertThat("Bad IP address returned", next.getAddress().getHostAddress(), CoreMatchers.anyOf(CoreMatchers.equalTo(apacheOrg1.getHostAddress()), CoreMatchers.equalTo(apacheOrg2.getHostAddress())));
            Assert.assertEquals(1, hostProvider.size());// resolve() call keeps the size of provider

        }
        // Resolver called 10 times, because we shouldn't cache the resolved addresses
        Mockito.verify(spyResolver, Mockito.times(10)).getAllByName("www.apache.org");// resolution occurred

    }

    @Test
    public void testReResolveMultipleOneFailing() throws UnknownHostException {
        // Arrange
        final List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
        list.add(InetSocketAddress.createUnresolved("www.apache.org", 1234));
        final List<String> ipList = new ArrayList<String>();
        final List<InetAddress> resolvedAddresses = new ArrayList<InetAddress>();
        for (int i = 0; i < 3; i++) {
            ipList.add(String.format("192.168.1.%d", (i + 1)));
            final InetAddress apacheOrg = Mockito.mock(InetAddress.class);
            Mockito.when(apacheOrg.getHostAddress()).thenReturn(String.format("192.168.1.%d", (i + 1)));
            Mockito.when(apacheOrg.toString()).thenReturn(String.format("192.168.1.%d", (i + 1)));
            Mockito.when(apacheOrg.getHostName()).thenReturn("www.apache.org");
            resolvedAddresses.add(apacheOrg);
        }
        StaticHostProvider.Resolver resolver = new StaticHostProvider.Resolver() {
            @Override
            public InetAddress[] getAllByName(String name) {
                return resolvedAddresses.toArray(new InetAddress[resolvedAddresses.size()]);
            }
        };
        StaticHostProvider.Resolver spyResolver = Mockito.spy(resolver);
        StaticHostProvider hostProvider = new StaticHostProvider(list, spyResolver);
        // Act & Assert
        InetSocketAddress resolvedFirst = hostProvider.next(0);
        Assert.assertFalse("HostProvider should return resolved addresses", resolvedFirst.isUnresolved());
        Assert.assertThat("Bad IP address returned", ipList, CoreMatchers.hasItems(resolvedFirst.getAddress().getHostAddress()));
        hostProvider.onConnected();// first address worked

        InetSocketAddress resolvedSecond = hostProvider.next(0);
        Assert.assertFalse("HostProvider should return resolved addresses", resolvedSecond.isUnresolved());
        Assert.assertThat("Bad IP address returned", ipList, CoreMatchers.hasItems(resolvedSecond.getAddress().getHostAddress()));
        // Second address doesn't work, so we don't call onConnected() this time
        // StaticHostProvider should try to re-resolve the address in this case
        InetSocketAddress resolvedThird = hostProvider.next(0);
        Assert.assertFalse("HostProvider should return resolved addresses", resolvedThird.isUnresolved());
        Assert.assertThat("Bad IP address returned", ipList, CoreMatchers.hasItems(resolvedThird.getAddress().getHostAddress()));
        Mockito.verify(spyResolver, Mockito.times(3)).getAllByName("www.apache.org");// resolution occured every time

    }

    @Test
    public void testEmptyResolution() throws UnknownHostException {
        // Arrange
        final List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
        list.add(InetSocketAddress.createUnresolved("www.apache.org", 1234));
        list.add(InetSocketAddress.createUnresolved("www.google.com", 1234));
        final List<InetAddress> resolvedAddresses = new ArrayList<InetAddress>();
        final InetAddress apacheOrg1 = Mockito.mock(InetAddress.class);
        Mockito.when(apacheOrg1.getHostAddress()).thenReturn("192.168.1.1");
        Mockito.when(apacheOrg1.toString()).thenReturn("www.apache.org");
        Mockito.when(apacheOrg1.getHostName()).thenReturn("www.apache.org");
        resolvedAddresses.add(apacheOrg1);
        StaticHostProvider.Resolver resolver = new StaticHostProvider.Resolver() {
            @Override
            public InetAddress[] getAllByName(String name) {
                if ("www.apache.org".equalsIgnoreCase(name)) {
                    return resolvedAddresses.toArray(new InetAddress[resolvedAddresses.size()]);
                } else {
                    return new InetAddress[0];
                }
            }
        };
        StaticHostProvider.Resolver spyResolver = Mockito.spy(resolver);
        StaticHostProvider hostProvider = new StaticHostProvider(list, spyResolver);
        // Act & Assert
        for (int i = 0; i < 10; i++) {
            InetSocketAddress resolved = hostProvider.next(0);
            hostProvider.onConnected();
            if (resolved.getHostName().equals("www.google.com")) {
                Assert.assertTrue("HostProvider should return unresolved address if host is unresolvable", resolved.isUnresolved());
            } else {
                Assert.assertFalse("HostProvider should return resolved addresses", resolved.isUnresolved());
                Assert.assertEquals("192.168.1.1", resolved.getAddress().getHostAddress());
            }
        }
        Mockito.verify(spyResolver, Mockito.times(5)).getAllByName("www.apache.org");
        Mockito.verify(spyResolver, Mockito.times(5)).getAllByName("www.google.com");
    }

    @Test
    public void testReResolvingLocalhost() {
        byte size = 2;
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>(size);
        // Test a hostname that resolves to multiple addresses
        list.add(InetSocketAddress.createUnresolved("localhost", 1234));
        list.add(InetSocketAddress.createUnresolved("localhost", 1235));
        StaticHostProvider hostProvider = new StaticHostProvider(list);
        int sizeBefore = hostProvider.size();
        InetSocketAddress next = hostProvider.next(0);
        next = hostProvider.next(0);
        Assert.assertTrue((((("Different number of addresses in the list: " + (hostProvider.size())) + " (after), ") + sizeBefore) + " (before)"), ((hostProvider.size()) == sizeBefore));
    }
}

