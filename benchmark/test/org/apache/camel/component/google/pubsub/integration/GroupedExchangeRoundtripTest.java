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
package org.apache.camel.component.google.pubsub.integration;


import Exchange.GROUPED_EXCHANGE;
import java.util.List;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.google.pubsub.PubsubTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class GroupedExchangeRoundtripTest extends PubsubTestSupport {
    private static final String TOPIC_NAME = "groupTopic";

    private static final String SUBSCRIPTION_NAME = "groupSubscription";

    @EndpointInject(uri = "direct:aggregator")
    private Endpoint aggregator;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (GroupedExchangeRoundtripTest.TOPIC_NAME))
    private Endpoint topic;

    @EndpointInject(uri = "mock:sendResult")
    private MockEndpoint sendResult;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (GroupedExchangeRoundtripTest.SUBSCRIPTION_NAME))
    private Endpoint pubsubSubscription;

    @EndpointInject(uri = "mock:receiveResult")
    private MockEndpoint receiveResult;

    @Produce(uri = "direct:aggregator")
    private ProducerTemplate producer;

    /**
     * Tests that a grouped exhcange is successfully received
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendGrouped() throws Exception {
        Exchange exchange1 = new org.apache.camel.support.DefaultExchange(context);
        Exchange exchange2 = new org.apache.camel.support.DefaultExchange(context);
        String body1 = "Group 1.1 : " + (exchange1.getExchangeId());
        String body2 = "Group 1.2 : " + (exchange2.getExchangeId());
        receiveResult.expectedMessageCount(2);
        receiveResult.expectedBodiesReceivedInAnyOrder(body1, body2);
        exchange1.getIn().setBody(body1);
        exchange2.getIn().setBody(body2);
        producer.send(exchange1);
        producer.send(exchange2);
        receiveResult.assertIsSatisfied(3000);
        // Send result section
        List<Exchange> results = sendResult.getExchanges();
        assertEquals("Received exchanges", 1, results.size());
        List exchangeGrouped = ((List) (results.get(0).getProperty(GROUPED_EXCHANGE)));
        assertEquals("Received messages within the exchange", 2, exchangeGrouped.size());
    }
}
