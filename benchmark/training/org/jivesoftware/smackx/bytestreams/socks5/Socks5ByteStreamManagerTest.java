/**
 * Copyright the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.bytestreams.socks5;


import Bytestream.NAMESPACE;
import StanzaError.Builder;
import StanzaError.Condition.not_acceptable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.util.Protocol;
import org.jivesoftware.util.Verification;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.mockito.Mockito;


/**
 * Test for Socks5BytestreamManager.
 *
 * @author Henning Staib
 */
public class Socks5ByteStreamManagerTest {
    // settings
    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private static final DomainBareJid xmppServer = Socks5ByteStreamManagerTest.initiatorJID.asDomainBareJid();

    private static final DomainBareJid proxyJID = JidTestUtil.MUC_EXAMPLE_ORG;

    private static final String proxyAddress = "127.0.0.1";

    private static final String sessionID = "session_id";

    // protocol verifier
    private Protocol protocol;

    // mocked XMPP connection
    private XMPPConnection connection;

    /**
     * Test that {@link Socks5BytestreamManager#getBytestreamManager(XMPPConnection)} returns one
     * bytestream manager for every connection.
     */
    @Test
    public void shouldHaveOneManagerForEveryConnection() {
        // mock two connections
        XMPPConnection connection1 = Mockito.mock(XMPPConnection.class);
        XMPPConnection connection2 = Mockito.mock(XMPPConnection.class);
        /* create service discovery managers for the connections because the
        ConnectionCreationListener is not called when creating mocked connections
         */
        ServiceDiscoveryManager.getInstanceFor(connection1);
        ServiceDiscoveryManager.getInstanceFor(connection2);
        // get bytestream manager for the first connection twice
        Socks5BytestreamManager conn1ByteStreamManager1 = Socks5BytestreamManager.getBytestreamManager(connection1);
        Socks5BytestreamManager conn1ByteStreamManager2 = Socks5BytestreamManager.getBytestreamManager(connection1);
        // get bytestream manager for second connection
        Socks5BytestreamManager conn2ByteStreamManager1 = Socks5BytestreamManager.getBytestreamManager(connection2);
        // assertions
        Assert.assertEquals(conn1ByteStreamManager1, conn1ByteStreamManager2);
        Assert.assertNotSame(conn1ByteStreamManager1, conn2ByteStreamManager1);
    }

    /**
     * The SOCKS5 Bytestream feature should be removed form the service discovery manager if Socks5
     * bytestream feature is disabled.
     */
    @Test
    public void shouldDisableService() {
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        Assert.assertTrue(discoveryManager.includesFeature(NAMESPACE));
        byteStreamManager.disableService();
        Assert.assertFalse(discoveryManager.includesFeature(NAMESPACE));
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid)} should throw an exception
     * if the given target does not support SOCKS5 Bytestream.
     *
     * @throws XMPPException
     * 		
     */
    @Test
    public void shouldFailIfTargetDoesNotSupportSocks5() throws XMPPException {
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        try {
            // build empty discover info as reply if targets features are queried
            DiscoverInfo discoverInfo = new DiscoverInfo();
            protocol.addResponse(discoverInfo);
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID);
            Assert.fail("exception should be thrown");
        } catch (FeatureNotSupportedException e) {
            Assert.assertTrue(e.getFeature().equals("SOCKS5 Bytestream"));
            Assert.assertTrue(e.getJid().equals(Socks5ByteStreamManagerTest.targetJID));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if XMPP
     * server doesn't return any proxies.
     */
    @Test
    public void shouldFailIfNoSocks5ProxyFound1() {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items with no proxy items
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        // return the item with no proxy if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (SmackException e) {
            protocol.verifyAll();
            Assert.assertTrue(e.getMessage().contains("no SOCKS5 proxies available"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if no
     * proxy is a SOCKS5 proxy.
     */
    @Test
    public void shouldFailIfNoSocks5ProxyFound2() {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about NOT being a Socks5
        // proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("noproxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (SmackException e) {
            protocol.verifyAll();
            Assert.assertTrue(e.getMessage().contains("no SOCKS5 proxies available"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if no
     * SOCKS5 proxy can be found. If it turns out that a proxy is not a SOCKS5 proxy it should not
     * be queried again.
     */
    @Test
    public void shouldBlacklistNonSocks5Proxies() {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about NOT being a Socks5
        // proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("noproxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (SmackException e) {
            protocol.verifyAll();
            Assert.assertTrue(e.getMessage().contains("no SOCKS5 proxies available"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        /* retry to establish SOCKS5 Bytestream */
        // add responses for service discovery again
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (SmackException e) {
            /* #verifyAll() tests if the number of requests and responses corresponds and should
            fail if the invalid proxy is queried again
             */
            protocol.verifyAll();
            Assert.assertTrue(e.getMessage().contains("no SOCKS5 proxies available"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if the
     * target does not accept a SOCKS5 Bytestream. See <a
     * href="http://xmpp.org/extensions/xep-0065.html#usecase-alternate">XEP-0065 Section 5.2 A2</a>
     */
    @Test
    public void shouldFailIfTargetDoesNotAcceptSocks5Bytestream() {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about being a SOCKS5 proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("proxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the socks5 bytestream proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build a socks5 stream host info containing the address and the port of the
        // proxy
        Bytestream streamHostInfo = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostInfo.addStreamHost(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.proxyAddress, 7778);
        // return stream host info if it is queried
        protocol.addResponse(streamHostInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build error packet to reject SOCKS5 Bytestream
        StanzaError.Builder builder = StanzaError.getBuilder(not_acceptable);
        IQ rejectPacket = new org.jivesoftware.smack.packet.ErrorIQ(builder);
        rejectPacket.setFrom(Socks5ByteStreamManagerTest.targetJID);
        rejectPacket.setTo(Socks5ByteStreamManagerTest.initiatorJID);
        // return error packet as response to the bytestream initiation
        protocol.addResponse(rejectPacket, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (XMPPErrorException e) {
            protocol.verifyAll();
            Assert.assertEquals(rejectPacket.getError(), e.getStanzaError());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if the
     * proxy used by target is invalid.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void shouldFailIfTargetUsesInvalidSocks5Proxy() throws XmppStringprepException {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about being a SOCKS5 proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("proxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the socks5 bytestream proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build a socks5 stream host info containing the address and the port of the
        // proxy
        Bytestream streamHostInfo = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostInfo.addStreamHost(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.proxyAddress, 7778);
        // return stream host info if it is queried
        protocol.addResponse(streamHostInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build used stream host response with unknown proxy
        Bytestream streamHostUsedPacket = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostUsedPacket.setSessionID(Socks5ByteStreamManagerTest.sessionID);
        streamHostUsedPacket.setUsedHost(JidCreate.from("invalid.proxy"));
        // return used stream host info as response to the bytestream initiation
        protocol.addResponse(streamHostUsedPacket, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (SmackException e) {
            protocol.verifyAll();
            Assert.assertTrue(e.getMessage().contains("Remote user responded with unknown host"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should fail if
     * initiator can not connect to the SOCKS5 proxy used by target.
     */
    @Test
    public void shouldFailIfInitiatorCannotConnectToSocks5Proxy() {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about being a SOCKS5 proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("proxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the socks5 bytestream proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build a socks5 stream host info containing the address and the port of the
        // proxy
        Bytestream streamHostInfo = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostInfo.addStreamHost(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.proxyAddress, 7778);
        // return stream host info if it is queried
        protocol.addResponse(streamHostInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build used stream host response
        Bytestream streamHostUsedPacket = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostUsedPacket.setSessionID(Socks5ByteStreamManagerTest.sessionID);
        streamHostUsedPacket.setUsedHost(Socks5ByteStreamManagerTest.proxyJID);
        // return used stream host info as response to the bytestream initiation
        protocol.addResponse(streamHostUsedPacket, new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                // verify SOCKS5 Bytestream request
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                Assert.assertEquals(1, request.getStreamHosts().size());
                StreamHost streamHost = ((StreamHost) (request.getStreamHosts().toArray()[0]));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost.getJID());
            }
        }, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        try {
            // start SOCKS5 Bytestream
            byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID);
            Assert.fail("exception should be thrown");
        } catch (IOException e) {
            // initiator can't connect to proxy because it is not running
            protocol.verifyAll();
            Assert.assertEquals(ConnectException.class, e.getClass());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} should successfully
     * negotiate and return a SOCKS5 Bytestream connection.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNegotiateSocks5BytestreamAndTransferData() throws Exception {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing a proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        Item item = new Item(Socks5ByteStreamManagerTest.proxyJID);
        discoverItems.addItem(item);
        // return the proxy item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover info for proxy containing information about being a SOCKS5 proxy
        DiscoverInfo proxyInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        Identity identity = new Identity("proxy", Socks5ByteStreamManagerTest.proxyJID.toString(), "bytestreams");
        proxyInfo.addIdentity(identity);
        // return the socks5 bytestream proxy identity if proxy is queried
        protocol.addResponse(proxyInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build a socks5 stream host info containing the address and the port of the
        // proxy
        Bytestream streamHostInfo = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostInfo.addStreamHost(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.proxyAddress, 7778);
        // return stream host info if it is queried
        protocol.addResponse(streamHostInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build used stream host response
        Bytestream streamHostUsedPacket = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostUsedPacket.setSessionID(Socks5ByteStreamManagerTest.sessionID);
        streamHostUsedPacket.setUsedHost(Socks5ByteStreamManagerTest.proxyJID);
        // return used stream host info as response to the bytestream initiation
        protocol.addResponse(streamHostUsedPacket, new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                Assert.assertEquals(1, request.getStreamHosts().size());
                StreamHost streamHost = ((StreamHost) (request.getStreamHosts().toArray()[0]));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost.getJID());
            }
        }, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        // build response to proxy activation
        IQ activationResponse = Socks5PacketUtils.createActivationConfirmation(Socks5ByteStreamManagerTest.proxyJID, Socks5ByteStreamManagerTest.initiatorJID);
        // return proxy activation response if proxy should be activated
        protocol.addResponse(activationResponse, new Verification<Bytestream, IQ>() {
            @Override
            public void verify(Bytestream request, IQ response) {
                Assert.assertEquals(Socks5ByteStreamManagerTest.targetJID, request.getToActivate().getTarget());
            }
        }, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        // start a local SOCKS5 proxy
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7778);
        socks5Proxy.start();
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamManagerTest.sessionID, Socks5ByteStreamManagerTest.initiatorJID, Socks5ByteStreamManagerTest.targetJID);
        // finally call the method that should be tested
        OutputStream outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        InputStream inputStream = socks5Proxy.getSocket(digest).getInputStream();
        byte[] data = new byte[]{ 1, 2, 3 };
        outputStream.write(data);
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
    }

    /**
     * If multiple network addresses are added to the local SOCKS5 proxy, all of them should be
     * contained in the SOCKS5 Bytestream request.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldUseMultipleAddressesForLocalSocks5Proxy() throws Exception {
        // enable clients local SOCKS5 proxy on port 7778
        Socks5Proxy.setLocalSocks5ProxyEnabled(true);
        Socks5Proxy.setLocalSocks5ProxyPort(7778);
        // start a local SOCKS5 proxy
        Socks5Proxy socks5Proxy = Socks5Proxy.getSocks5Proxy();
        socks5Proxy.start();
        Assert.assertTrue(socks5Proxy.isRunning());
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        /**
         * create responses in the order they should be queried specified by the XEP-0065
         * specification
         */
        // build discover info that supports the SOCKS5 feature
        DiscoverInfo discoverInfo = Socks5PacketUtils.createDiscoverInfo(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        discoverInfo.addFeature(NAMESPACE);
        // return that SOCKS5 is supported if target is queried
        protocol.addResponse(discoverInfo, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build discover items containing no proxy item
        DiscoverItems discoverItems = Socks5PacketUtils.createDiscoverItems(Socks5ByteStreamManagerTest.xmppServer, Socks5ByteStreamManagerTest.initiatorJID);
        // return the discover item if XMPP server is queried
        protocol.addResponse(discoverItems, Verification.correspondingSenderReceiver, Verification.requestTypeGET);
        // build used stream host response
        Bytestream streamHostUsedPacket = Socks5PacketUtils.createBytestreamResponse(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.initiatorJID);
        streamHostUsedPacket.setSessionID(Socks5ByteStreamManagerTest.sessionID);
        streamHostUsedPacket.setUsedHost(Socks5ByteStreamManagerTest.initiatorJID);// local proxy used

        // return used stream host info as response to the bytestream initiation
        protocol.addResponse(streamHostUsedPacket, new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                StreamHost streamHost1 = request.getStreamHosts().get(0);
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost1.getJID());
                StreamHost streamHost2 = request.getStreamHosts().get(((request.getStreamHosts().size()) - 1));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost2.getJID());
                Assert.assertEquals("localAddress", streamHost2.getAddress());
            }
        }, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamManagerTest.sessionID, Socks5ByteStreamManagerTest.initiatorJID, Socks5ByteStreamManagerTest.targetJID);
        // connect to proxy as target
        socks5Proxy.addTransfer(digest);
        StreamHost streamHost = new StreamHost(Socks5ByteStreamManagerTest.targetJID, socks5Proxy.getLocalAddresses().get(0), socks5Proxy.getPort());
        Socks5Client socks5Client = new Socks5Client(streamHost, digest);
        InputStream inputStream = socks5Client.getSocket(2000).getInputStream();
        // add another network address before establishing SOCKS5 Bytestream
        socks5Proxy.addLocalAddress("localAddress");
        // finally call the method that should be tested
        OutputStream outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        byte[] data = new byte[]{ 1, 2, 3 };
        outputStream.write(data);
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
        // reset proxy settings
        socks5Proxy.stop();
        socks5Proxy.removeLocalAddress("localAddress");
        Socks5Proxy.setLocalSocks5ProxyPort(7777);
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} the first time
     * should successfully negotiate a SOCKS5 Bytestream via the second SOCKS5 proxy and should
     * prioritize this proxy for a second SOCKS5 Bytestream negotiation.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldPrioritizeSecondSocks5ProxyOnSecondAttempt() throws Exception {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        Assert.assertTrue(byteStreamManager.isProxyPrioritizationEnabled());
        Verification<Bytestream, Bytestream> streamHostUsedVerification1 = new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                Assert.assertEquals(2, request.getStreamHosts().size());
                // verify that the used stream host is the second in list
                StreamHost streamHost = ((StreamHost) (request.getStreamHosts().toArray()[1]));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost.getJID());
            }
        };
        createResponses(streamHostUsedVerification1);
        // start a local SOCKS5 proxy
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7778);
        socks5Proxy.start();
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamManagerTest.sessionID, Socks5ByteStreamManagerTest.initiatorJID, Socks5ByteStreamManagerTest.targetJID);
        // call the method that should be tested
        OutputStream outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        InputStream inputStream = socks5Proxy.getSocket(digest).getInputStream();
        byte[] data = new byte[]{ 1, 2, 3 };
        outputStream.write(data);
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
        Verification<Bytestream, Bytestream> streamHostUsedVerification2 = new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                Assert.assertEquals(2, request.getStreamHosts().size());
                // verify that the used stream host is the first in list
                StreamHost streamHost = ((StreamHost) (request.getStreamHosts().toArray()[0]));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost.getJID());
            }
        };
        createResponses(streamHostUsedVerification2);
        // call the method that should be tested again
        outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        inputStream = socks5Proxy.getSocket(digest).getInputStream();
        outputStream.write(data);
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
    }

    /**
     * Invoking {@link Socks5BytestreamManager#establishSession(org.jxmpp.jid.Jid, String)} the first time
     * should successfully negotiate a SOCKS5 Bytestream via the second SOCKS5 proxy. The second
     * negotiation should run in the same manner if prioritization is disabled.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotPrioritizeSocks5ProxyIfPrioritizationDisabled() throws Exception {
        // disable clients local SOCKS5 proxy
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        // get Socks5ByteStreamManager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        byteStreamManager.setProxyPrioritizationEnabled(false);
        Assert.assertFalse(byteStreamManager.isProxyPrioritizationEnabled());
        Verification<Bytestream, Bytestream> streamHostUsedVerification = new Verification<Bytestream, Bytestream>() {
            @Override
            public void verify(Bytestream request, Bytestream response) {
                Assert.assertEquals(response.getSessionID(), request.getSessionID());
                Assert.assertEquals(2, request.getStreamHosts().size());
                // verify that the used stream host is the second in list
                StreamHost streamHost = ((StreamHost) (request.getStreamHosts().toArray()[1]));
                Assert.assertEquals(response.getUsedHost().getJID(), streamHost.getJID());
            }
        };
        createResponses(streamHostUsedVerification);
        // start a local SOCKS5 proxy
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7778);
        socks5Proxy.start();
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamManagerTest.sessionID, Socks5ByteStreamManagerTest.initiatorJID, Socks5ByteStreamManagerTest.targetJID);
        // call the method that should be tested
        OutputStream outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        InputStream inputStream = socks5Proxy.getSocket(digest).getInputStream();
        byte[] data = new byte[]{ 1, 2, 3 };
        outputStream.write(data);
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
        createResponses(streamHostUsedVerification);
        // call the method that should be tested again
        outputStream = byteStreamManager.establishSession(Socks5ByteStreamManagerTest.targetJID, Socks5ByteStreamManagerTest.sessionID).getOutputStream();
        // test the established bytestream
        inputStream = socks5Proxy.getSocket(digest).getInputStream();
        outputStream.write(data);
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        protocol.verifyAll();
        byteStreamManager.setProxyPrioritizationEnabled(true);
    }
}
