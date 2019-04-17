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
package org.apache.camel.component.activemq;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComplexRequestReplyTest {
    private static final Logger LOG = LoggerFactory.getLogger(ComplexRequestReplyTest.class);

    private BrokerService brokerA;

    private BrokerService brokerB;

    private CamelContext senderContext;

    private CamelContext brokerAContext;

    private CamelContext brokerBContext;

    private String brokerAUri;

    private String brokerBUri;

    private String connectionUri;

    private final String fromEndpoint = "direct:test";

    private final String toEndpoint = "activemq:queue:send";

    private final String brokerEndpoint = "activemq:send";

    @Test
    public void testSendThenFailoverThenSend() throws Exception {
        ProducerTemplate requester = senderContext.createProducerTemplate();
        ComplexRequestReplyTest.LOG.info("*** Sending Request 1");
        String response = ((String) (requester.requestBody(fromEndpoint, "This is a request")));
        Assert.assertNotNull((response != null));
        ComplexRequestReplyTest.LOG.info(("Got response: " + response));
        /**
         * You actually don't need to restart the broker, just wait long enough
         * and the next next send will take out a closed connection and
         * reconnect, and if you happen to hit the broker you weren't on last
         * time, then you will see the failure.
         */
        TimeUnit.SECONDS.sleep(20);
        /**
         * I restart the broker after the wait that exceeds the idle timeout
         * value of the PooledConnectionFactory to show that it doesn't matter
         * now as the older connection has already been closed.
         */
        ComplexRequestReplyTest.LOG.info("Restarting Broker A now.");
        shutdownBrokerA();
        createBrokerA();
        ComplexRequestReplyTest.LOG.info("*** Sending Request 2");
        response = ((String) (requester.requestBody(fromEndpoint, "This is a request")));
        Assert.assertNotNull((response != null));
        ComplexRequestReplyTest.LOG.info(("Got response: " + response));
    }
}
