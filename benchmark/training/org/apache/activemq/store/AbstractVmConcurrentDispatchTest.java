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
package org.apache.activemq.store;


import ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE;
import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test can also be used to debug AMQ-6218 and AMQ-6221
 *
 * This test shows that messages are received with non-null data while
 * several consumers are used.
 */
public abstract class AbstractVmConcurrentDispatchTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractVmConcurrentDispatchTest.class);

    private final AbstractVmConcurrentDispatchTest.MessageType messageType;

    private final boolean reduceMemoryFootPrint;

    protected final boolean useTopic;

    protected static enum MessageType {

        TEXT,
        MAP,
        OBJECT;}

    protected static final boolean[] booleanVals = new boolean[]{ true, false };

    protected static boolean[] reduceMemoryFootPrintVals = AbstractVmConcurrentDispatchTest.booleanVals;

    protected static boolean[] useTopicVals = AbstractVmConcurrentDispatchTest.booleanVals;

    private String testTopicName = "mytopic";

    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    public AbstractVmConcurrentDispatchTest(AbstractVmConcurrentDispatchTest.MessageType messageType, boolean reduceMemoryFootPrint, boolean useTopic) {
        this.messageType = messageType;
        this.reduceMemoryFootPrint = reduceMemoryFootPrint;
        this.useTopic = useTopic;
    }

    private BrokerService broker;

    private final AtomicBoolean failure = new AtomicBoolean();

    private CountDownLatch ready;

    private URI connectionURI;

    private URI vmConnectionURI;

    private final boolean USE_VM_TRANSPORT = true;

    private final int NUM_CONSUMERS = 30;

    private final int NUM_PRODUCERS = 1;

    private final int NUM_TASKS = (NUM_CONSUMERS) + (NUM_PRODUCERS);

    private final AtomicInteger count = new AtomicInteger();

    private String MessageId = null;

    private int MessageCount = 0;

    @Test(timeout = 180000)
    public void testMessagesAreValid() throws Exception {
        if (this.useTopic) {
            Assume.assumeTrue(reduceMemoryFootPrint);
        }
        ExecutorService tasks = Executors.newFixedThreadPool(NUM_TASKS);
        for (int i = 0; i < (NUM_CONSUMERS); i++) {
            AbstractVmConcurrentDispatchTest.LOG.info("Created Consumer: {}", (i + 1));
            tasks.execute(new AbstractVmConcurrentDispatchTest.HelloWorldConsumer(useTopic));
        }
        for (int i = 0; i < (NUM_PRODUCERS); i++) {
            AbstractVmConcurrentDispatchTest.LOG.info("Created Producer: {}", (i + 1));
            tasks.execute(new AbstractVmConcurrentDispatchTest.HelloWorldProducer(useTopic));
        }
        Assert.assertTrue(ready.await(20, TimeUnit.SECONDS));
        try {
            tasks.shutdown();
            // run for 10 seconds as that seems to be enough time to cause an error
            // if there is going to be one
            tasks.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            // should get exception with no errors
        }
        Assert.assertFalse("Test Encountered a null bodied message", failure.get());
    }

    public class HelloWorldProducer implements Runnable {
        final boolean useTopic;

        public HelloWorldProducer(boolean useTopic) {
            this.useTopic = useTopic;
        }

        @Override
        public void run() {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getBrokerURI());
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                // If using topics, just test a generic topic name
                // If useTopic is false then we are testing virtual topics/queue consumes
                Destination destination = (useTopic) ? session.createTopic(testTopicName) : session.createTopic("VirtualTopic.AMQ6218Test");
                MessageProducer producer = session.createProducer(destination);
                AbstractVmConcurrentDispatchTest.LOG.info("Producer: {}", destination);
                producer.setDeliveryMode(PERSISTENT);
                producer.setPriority(4);
                producer.setTimeToLive(0);
                ready.countDown();
                int j = 0;
                while (!(failure.get())) {
                    j++;
                    String text = "AMQ Message Number :" + j;
                    Message message = null;
                    if (messageType.equals(AbstractVmConcurrentDispatchTest.MessageType.MAP)) {
                        MapMessage mapMessage = session.createMapMessage();
                        mapMessage.setString("text", text);
                        message = mapMessage;
                    } else
                        if (messageType.equals(AbstractVmConcurrentDispatchTest.MessageType.OBJECT)) {
                            ObjectMessage objectMessage = session.createObjectMessage();
                            objectMessage.setObject(text);
                            message = objectMessage;
                        } else {
                            message = session.createTextMessage(text);
                        }

                    producer.send(message);
                    AbstractVmConcurrentDispatchTest.LOG.info("Sent message: {}", message.getJMSMessageID());
                } 
                connection.close();
            } catch (Exception e) {
                AbstractVmConcurrentDispatchTest.LOG.error(("Caught: " + e));
                e.printStackTrace();
            }
        }
    }

    public class HelloWorldConsumer implements Runnable , ExceptionListener {
        final boolean useTopic;

        public HelloWorldConsumer(boolean useTopic) {
            this.useTopic = useTopic;
        }

        @Override
        public void run() {
            try {
                int i = count.incrementAndGet();
                String destName = (!(useTopic)) ? ("Consumer.Q" + i) + ".VirtualTopic.AMQ6218Test" : testTopicName;
                AbstractVmConcurrentDispatchTest.LOG.info(destName);
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getBrokerURI());
                Connection connection = connectionFactory.createConnection();
                connection.setClientID(("clientId" + i));
                connection.start();
                Session session = connection.createSession(false, INDIVIDUAL_ACKNOWLEDGE);
                Destination destination = (useTopic) ? session.createTopic(destName) : session.createQueue(destName);
                MessageConsumer consumer = (useTopic) ? session.createDurableSubscriber(((Topic) (destination)), ("sub" + i)) : session.createConsumer(destination);
                ready.countDown();
                while (!(failure.get())) {
                    Message message = consumer.receive(500);
                    if (message != null) {
                        synchronized(this) {
                            if ((MessageId) != null) {
                                if (message.getJMSMessageID().equalsIgnoreCase(MessageId)) {
                                    (MessageCount)++;
                                } else {
                                    AbstractVmConcurrentDispatchTest.LOG.info(((("Count of message " + (MessageId)) + " is ") + (MessageCount)));
                                    MessageCount = 1;
                                    MessageId = message.getJMSMessageID();
                                }
                            } else {
                                MessageId = message.getJMSMessageID();
                                MessageCount = 1;
                            }
                        }
                        String text = null;
                        if ((messageType.equals(AbstractVmConcurrentDispatchTest.MessageType.OBJECT)) && (message instanceof ObjectMessage)) {
                            ObjectMessage objectMessage = ((ObjectMessage) (message));
                            text = ((String) (objectMessage.getObject()));
                        } else
                            if ((messageType.equals(AbstractVmConcurrentDispatchTest.MessageType.TEXT)) && (message instanceof TextMessage)) {
                                TextMessage textMessage = ((TextMessage) (message));
                                text = textMessage.getText();
                            } else
                                if ((messageType.equals(AbstractVmConcurrentDispatchTest.MessageType.MAP)) && (message instanceof MapMessage)) {
                                    MapMessage mapMessage = ((MapMessage) (message));
                                    text = mapMessage.getString("text");
                                } else {
                                    AbstractVmConcurrentDispatchTest.LOG.info((((((destName + " Message is not a instanceof ") + (messageType)) + " message id: ") + (message.getJMSMessageID())) + message));
                                }


                        if (text == null) {
                            AbstractVmConcurrentDispatchTest.LOG.warn(((destName + " text received as a null ") + message));
                            failure.set(true);
                        } else {
                            AbstractVmConcurrentDispatchTest.LOG.info(((((destName + " text ") + text) + " message id: ") + (message.getJMSMessageID())));
                        }
                        message.acknowledge();
                    }
                } 
                connection.close();
            } catch (Exception e) {
                AbstractVmConcurrentDispatchTest.LOG.error("Caught: ", e);
            }
        }

        @Override
        public synchronized void onException(JMSException ex) {
            AbstractVmConcurrentDispatchTest.LOG.error("JMS Exception occurred.  Shutting down client.");
        }
    }
}

