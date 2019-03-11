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
package org.apache.activemq.broker.jmx;


import Session.AUTO_ACKNOWLEDGE;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SelectiveMBeanRegistrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(SelectiveMBeanRegistrationTest.class);

    BrokerService brokerService;

    protected MBeanServer mbeanServer;

    protected String domain = "org.apache.activemq";

    protected ConnectionFactory connectionFactory;

    protected Connection connection;

    protected boolean transacted;

    @Test
    public void testSuppression() throws Exception {
        connection = connectionFactory.createConnection("admin", "admin");
        connection.setClientID("MBeanTest");
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue("AQueue");
        session.createConsumer(queue);
        // create a plain topic
        Destination topic = session.createTopic("ATopic");
        session.createConsumer(topic);
        final ManagedRegionBroker managedRegionBroker = ((ManagedRegionBroker) (brokerService.getBroker().getAdaptor(ManagedRegionBroker.class)));
        // mbean exists
        Assert.assertTrue("one sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (managedRegionBroker.getQueueSubscribers().length) == 1;
            }
        }));
        // but it is not registered
        Assert.assertFalse(mbeanServer.isRegistered(managedRegionBroker.getQueueSubscribers()[0]));
        // and is not tracked
        Assert.assertFalse("not tracked as registered", managedRegionBroker.getRegisteredMbeans().contains(managedRegionBroker.getQueueSubscribers()[0]));
        // verify dynamicProducer suppressed
        session.createProducer(null);
        // mbean exists
        Assert.assertTrue("one sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (managedRegionBroker.getDynamicDestinationProducers().length) == 1;
            }
        }));
        // but it is not registered
        ObjectName query = new ObjectName(((domain) + ":type=Broker,brokerName=localhost,endpoint=dynamicProducer,*"));
        Set<ObjectInstance> mbeans = mbeanServer.queryMBeans(query, null);
        Assert.assertEquals(0, mbeans.size());
        Assert.assertFalse("producer not tracked as registered", managedRegionBroker.getRegisteredMbeans().contains(managedRegionBroker.getDynamicDestinationProducers()[0]));
        query = new ObjectName(((domain) + ":type=Broker,brokerName=localhost,destinationName=ActiveMQ.Advisory.*,*"));
        mbeans = mbeanServer.queryMBeans(query, null);
        Assert.assertEquals(0, mbeans.size());
        ObjectName[] topicNames = managedRegionBroker.getTopics();
        Assert.assertTrue("Some topics registered", ((topicNames.length) > 0));
        for (ObjectName objectName : topicNames) {
            if (objectName.getKeyProperty("destinationName").contains("Advisory")) {
                Assert.assertFalse(("advisory topic not tracked as registered: " + objectName), managedRegionBroker.getRegisteredMbeans().contains(objectName));
            } else {
                Assert.assertTrue(("topic tracked as registered: " + objectName), managedRegionBroker.getRegisteredMbeans().contains(objectName));
            }
        }
    }
}

