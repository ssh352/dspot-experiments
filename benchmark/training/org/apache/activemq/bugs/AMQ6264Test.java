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
package org.apache.activemq.bugs;


import ActiveMQDestination.TOPIC_TYPE;
import Session.AUTO_ACKNOWLEDGE;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ6264Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6264Test.class);

    @Rule
    public final TestName testName = new TestName();

    protected final int MESSAGE_COUNT = 2000;

    private final String topicPrefix = "topic.";

    private final String topicFilter = (topicPrefix) + ">";

    private final String topicA = "topic.A";

    private BrokerService broker;

    private Connection connection;

    private String connectionURI;

    @Test(timeout = 60000)
    public void testSlowConsumerAdvisory() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicFilter);
        MessageConsumer consumer = session.createDurableSubscriber(topic, testName.getMethodName());
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getSlowConsumerAdvisoryTopic(ActiveMQDestination.createDestination(topicA, TOPIC_TYPE));
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisoryConsumer = session.createConsumer(advisoryTopic);
        // start throwing messages at the consumer one for an ongoing series of
        // matching topics for the subscription's filter.
        MessageProducer producer = session.createProducer(null);
        // Send one to the destination where we want a matching advisory
        producer.send(session.createTopic(topicA), session.createMessage());
        for (int i = 0; i < (MESSAGE_COUNT); i++) {
            BytesMessage m = session.createBytesMessage();
            m.writeBytes(new byte[1024]);
            Topic newTopic = session.createTopic(((topicPrefix) + (UUID.randomUUID().toString())));
            AMQ6264Test.LOG.debug("Sending message to next topic: {}", newTopic);
            producer.send(newTopic, m);
        }
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
    }
}
