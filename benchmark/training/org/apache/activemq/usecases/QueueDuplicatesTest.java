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
package org.apache.activemq.usecases;


import DeliveryMode.NON_PERSISTENT;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueueDuplicatesTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(QueueDuplicatesTest.class);

    private static DateFormat formatter = new SimpleDateFormat("HH:mm:ss SSS");

    private String brokerUrl;

    private String subject;

    private Connection brokerConnection;

    public QueueDuplicatesTest(String name) {
        super(name);
    }

    public void testDuplicates() {
        try {
            // Get Session
            Session session = createSession(brokerConnection);
            // create consumer
            Destination dest = session.createQueue(subject);
            MessageConsumer consumer = session.createConsumer(dest);
            // subscribe to queue
            consumer.setMessageListener(new QueueDuplicatesTest.SimpleConsumer());
            // create producer
            Thread sendingThread = new QueueDuplicatesTest.SendingThread(brokerUrl, subject);
            // start producer
            sendingThread.start();
            // wait about 5 seconds
            Thread.sleep(5000);
            // unsubscribe consumer
            consumer.close();
            // wait another 5 seconds
            Thread.sleep(5000);
            // create new consumer
            consumer = session.createConsumer(dest);
            // subscribe to queue
            consumer.setMessageListener(new QueueDuplicatesTest.SimpleConsumer());
            // sleep a little while longer
            Thread.sleep(15000);
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendingThread extends Thread {
        private String subject;

        SendingThread(String brokerUrl, String subject) {
            this.subject = subject;
            setDaemon(false);
        }

        public void run() {
            try {
                Session session = createSession(brokerConnection);
                Destination dest = session.createQueue(subject);
                MessageProducer producer = session.createProducer(dest);
                producer.setDeliveryMode(NON_PERSISTENT);
                for (int i = 0; i < 20; i++) {
                    String txt = "Text Message: " + i;
                    TextMessage msg = session.createTextMessage(txt);
                    producer.send(msg);
                    QueueDuplicatesTest.LOG.info((((((QueueDuplicatesTest.formatter.format(new Date())) + " Sent ==> ") + msg) + " to ") + (subject)));
                    Thread.sleep(1000);
                }
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class SimpleConsumer implements MessageListener {
        private Map<String, Message> msgs = new HashMap<String, Message>();

        public void onMessage(Message message) {
            QueueDuplicatesTest.LOG.info((((QueueDuplicatesTest.formatter.format(new Date())) + " SimpleConsumer Message Received: ") + message));
            try {
                String id = message.getJMSMessageID();
                TestCase.assertNull(("Message is duplicate: " + id), msgs.get(id));
                msgs.put(id, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

