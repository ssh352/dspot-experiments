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
package org.apache.activemq.transport.stomp;


import Session.AUTO_ACKNOWLEDGE;
import Stomp.Headers.Subscribe.AckModeValues.AUTO;
import Stomp.Transformations.JMS_ADVISORY_JSON;
import Stomp.Transformations.JMS_ADVISORY_XML;
import Stomp.Transformations.JMS_JSON;
import Stomp.Transformations.JMS_XML;
import java.util.HashMap;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.management.ObjectName;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StompAdvisoryTest extends StompTestSupport {
    static final String STATS_DESTINATION_PREFIX = "ActiveMQ.Statistics.Destination";

    private static final Logger LOG = LoggerFactory.getLogger(StompAdvisoryTest.class);

    protected ActiveMQConnection connection;

    @Test(timeout = 60000)
    public void testConnectionAdvisory() throws Exception {
        stompConnect();
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Connection", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ConnectionInfo\":"));
        c.stop();
        c.close();
        f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertNotNull("Body is not null", f.getBody());
        Assert.assertTrue("Body should have content", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ConnectionInfo\":"));
    }

    @Test(timeout = 60000)
    public void testConnectionAdvisoryJSON() throws Exception {
        stompConnect();
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("transformation", JMS_JSON.toString());
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Connection", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ConnectionInfo\":"));
        c.stop();
        c.close();
        f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertNotNull("Body is not null", f.getBody());
        Assert.assertTrue("Body should have content", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ConnectionInfo\":"));
    }

    @Test(timeout = 60000)
    public void testConnectionAdvisoryXML() throws Exception {
        stompConnect();
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("transformation", JMS_XML.toString());
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Connection", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("<ConnectionInfo>"));
        c.stop();
        c.close();
        f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertNotNull("Body is not null", f.getBody());
        Assert.assertTrue("Body should have content", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("<ConnectionInfo>"));
    }

    @Test(timeout = 60000)
    public void testConsumerAdvisory() throws Exception {
        stompConnect();
        Destination dest = new ActiveMQQueue("testConsumerAdvisory");
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Consumer.>", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        Session session = c.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(dest);
        Assert.assertNotNull(consumer);
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ConsumerInfo\":"));
        c.stop();
        c.close();
    }

    @Test(timeout = 60000)
    public void testProducerAdvisory() throws Exception {
        stompConnect();
        Destination dest = new ActiveMQQueue("testProducerAdvisory");
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Producer.>", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        Session session = c.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(dest);
        Message mess = session.createTextMessage("test");
        producer.send(mess);
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ProducerInfo\":"));
        c.stop();
        c.close();
    }

    @Test(timeout = 60000)
    public void testProducerAdvisoryXML() throws Exception {
        stompConnect();
        Destination dest = new ActiveMQQueue("testProducerAdvisoryXML");
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("transformation", JMS_ADVISORY_XML.toString());
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Producer.>", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        Session session = c.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(dest);
        Message mess = session.createTextMessage("test");
        producer.send(mess);
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("<ProducerInfo>"));
        c.stop();
        c.close();
    }

    @Test(timeout = 60000)
    public void testProducerAdvisoryJSON() throws Exception {
        stompConnect();
        Destination dest = new ActiveMQQueue("testProducerAdvisoryJSON");
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("transformation", JMS_ADVISORY_JSON.toString());
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.Producer.>", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection c = cf.createConnection("system", "manager");
        c.start();
        Session session = c.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(dest);
        Message mess = session.createTextMessage("test");
        producer.send(mess);
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"ProducerInfo\":"));
        c.stop();
        c.close();
    }

    @Test(timeout = 60000)
    public void testStatisticsAdvisory() throws Exception {
        Connection c = cf.createConnection("system", "manager");
        c.start();
        final Session session = c.createSession(false, AUTO_ACKNOWLEDGE);
        final Topic replyTo = session.createTopic("stats");
        // Dummy Queue used to later gather statistics.
        final ActiveMQQueue testQueue = new ActiveMQQueue("queueToBeTestedForStats");
        final MessageProducer producer = session.createProducer(null);
        Message mess = session.createTextMessage("test");
        producer.send(testQueue, mess);
        // Create a request for Queue statistics
        Thread child = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Queue query = session.createQueue(((StompAdvisoryTest.STATS_DESTINATION_PREFIX) + (testQueue.getQueueName())));
                    Message msg = session.createMessage();
                    msg.setJMSReplyTo(replyTo);
                    producer.send(query, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        child.start();
        stompConnect();
        // Attempt to gather the statistics response from the previous request.
        stompConnection.connect("system", "manager");
        stompConnection.subscribe(("/topic/" + (replyTo.getTopicName())), AUTO);
        stompConnection.begin("TX");
        StompFrame f = stompConnection.receive(5000);
        stompConnection.commit("TX");
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue("Should contains memoryUsage stats", f.getBody().contains("memoryUsage"));
        c.stop();
        c.close();
    }

    @Test(timeout = 60000)
    public void testDestinationAdvisoryTempQueue() throws Exception {
        cf.setWatchTopicAdvisories(false);
        stompConnect();
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.TempQueue", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection connection = cf.createConnection("system", "manager");
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        session.createTemporaryQueue();
        connection.close();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"DestinationInfo\":"));
    }

    @Test(timeout = 60000)
    public void testDestinationAdvisoryTempTopic() throws Exception {
        cf.setWatchTopicAdvisories(false);
        stompConnect();
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.TempTopic", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection connection = cf.createConnection("system", "manager");
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        session.createTemporaryTopic();
        connection.close();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"DestinationInfo\":"));
    }

    @Test(timeout = 60000)
    public void testDestinationAdvisoryCompositeTempDestinations() throws Exception {
        cf.setWatchTopicAdvisories(true);
        HashMap<String, String> subheaders = new HashMap<String, String>(1);
        subheaders.put("receipt", "id-1");
        stompConnect();
        stompConnection.connect("system", "manager");
        stompConnection.subscribe("/topic/ActiveMQ.Advisory.TempTopic,/topic/ActiveMQ.Advisory.TempQueue", AUTO, subheaders);
        String frame = stompConnection.receiveFrame();
        StompAdvisoryTest.LOG.debug("Response to subscribe was: {}", frame);
        Assert.assertTrue(frame.trim().startsWith("RECEIPT"));
        // Now connect via openwire and check we get the advisory
        Connection connection = cf.createConnection("system", "manager");
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        session.createTemporaryTopic();
        session.createTemporaryQueue();
        ObjectName[] topicSubscribers = brokerService.getAdminView().getTopicSubscribers();
        for (ObjectName subscription : topicSubscribers) {
            StompAdvisoryTest.LOG.info("Topic Subscription: {}", subscription);
        }
        connection.close();
        StompFrame f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"DestinationInfo\":"));
        f = stompConnection.receive();
        StompAdvisoryTest.LOG.debug(f.toString());
        Assert.assertEquals(f.getAction(), "MESSAGE");
        Assert.assertTrue("Should have a body", ((f.getBody().length()) > 0));
        Assert.assertTrue(f.getBody().startsWith("{\"DestinationInfo\":"));
    }
}

