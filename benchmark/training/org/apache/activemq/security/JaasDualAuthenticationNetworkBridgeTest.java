/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.security;


import java.util.Collection;
import java.util.List;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkBridge;
import org.apache.activemq.network.NetworkConnector;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for https://issues.apache.org/jira/browse/AMQ-5943.
 * Creates a network bridge to a broker that is configured for
 * JaasDualAuthenticationPlugin.
 * The broker that creates the network bridge does not set a
 * username/password on the nc configuration but expects to be
 * authenticated via its SSL certificate.
 * This test uses these external configuration files from
 * src/test/resources/
 * - org/apache/activemq/security/JaasDualAuthenticationNetworkBridgeTest.xml
 * - login-JaasDualAuthenticationNetworkBridgeTest.config
 * - users-JaasDualAuthenticationNetworkBridgeTest.properties
 * - groups-JaasDualAuthenticationNetworkBridgeTest.properties
 * - ssl-domain-JaasDualAuthenticationNetworkBridgeTest.properties
 */
public class JaasDualAuthenticationNetworkBridgeTest {
    protected String CONFIG_FILE = "org/apache/activemq/security/JaasDualAuthenticationNetworkBridge.xml";

    protected static Logger LOG = LoggerFactory.getLogger(JaasDualAuthenticationNetworkBridgeTest.class);

    private BrokerService broker1 = null;

    private BrokerService broker2 = null;

    /**
     * Waits 5 seconds for the network bridge between broker 1 and 2 to be
     * established, then checks if the bridge exists, by querying broker1.
     *
     * @throws Exception
     * 		is network bridge does not exist between both
     * 		broker instances.
     */
    @Test
    public void testNetworkBridgeUsingJaasDualAuthenticationPlugin() throws Exception {
        JaasDualAuthenticationNetworkBridgeTest.LOG.info("testNetworkBridgeUsingJaasDualAuthenticationPlugin() called.");
        try {
            // give 5 seconds for broker instances to establish network bridge
            Thread.sleep(5000);
            // verify that network bridge is established
            Assert.assertNotNull(broker1);
            List<NetworkConnector> ncs = broker1.getNetworkConnectors();
            Assert.assertNotNull("Network Connector not found.", ncs);
            Assert.assertFalse("Network Connector not found.", ncs.isEmpty());
            NetworkConnector nc = ((NetworkConnector) (ncs.get(0)));
            Collection<NetworkBridge> bridges = nc.activeBridges();
            Assert.assertFalse("Network bridge not established to broker 2", bridges.isEmpty());
            Assert.assertTrue("Network bridge not established to broker 2", ((bridges.size()) == 1));
            for (NetworkBridge nb : bridges) {
                Assert.assertTrue(((nb.getRemoteBrokerId()) != null));
            }
            JaasDualAuthenticationNetworkBridgeTest.LOG.info("Network bridge is correctly established.");
        } catch (InterruptedException ex) {
            JaasDualAuthenticationNetworkBridgeTest.LOG.warn(ex.getMessage());
        }
    }
}

