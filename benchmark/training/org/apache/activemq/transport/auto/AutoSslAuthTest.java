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
package org.apache.activemq.transport.auto;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AutoSslAuthTest {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private String uri;

    private final String protocol;

    private AtomicInteger hasCertificateCount = new AtomicInteger();

    private BrokerService brokerService;

    static {
        System.setProperty("javax.net.ssl.trustStore", AutoSslAuthTest.TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", AutoSslAuthTest.PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", AutoSslAuthTest.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", AutoSslAuthTest.SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", AutoSslAuthTest.PASSWORD);
        System.setProperty("javax.net.ssl.keyStoreType", AutoSslAuthTest.KEYSTORE_TYPE);
    }

    /**
     *
     *
     * @param isNio
     * 		
     */
    public AutoSslAuthTest(String protocol) {
        this.protocol = protocol;
    }

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(uri);
        // Create 5 connections to make sure all are properly set
        for (int i = 0; i < 5; i++) {
            factory.createConnection().start();
        }
        Assert.assertTrue(((hasCertificateCount.get()) == 5));
        for (TransportConnection connection : brokerService.getTransportConnectorByName("auto").getConnections()) {
            Assert.assertTrue(((connection.getTransport().getPeerCertificates()) != null));
        }
    }
}

