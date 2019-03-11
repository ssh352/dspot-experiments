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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.ServerSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The purpose of this test is to show that AMQ-5851 is fixed.  When running in an application
 * container, if multiple messages were consumed asynchronously and the messages had a short TTL,
 * it was possible to get an exception on the broker when a message acknowledgement was received.
 * This is because the original expiration strategy was excessive and when an expired Ack was received,
 * all dispatched messages were checked for expiration instead of only the messages tied to that Ack.
 * This caused an issue because sometimes a thread would finish and send back a standard ack,
 * but another expired ack would have already cleared the message from the dispach list.
 * Now only messages tied to the MessageAck are expired which fixes this problem.
 */
public class ExpiredAckAsyncConsumerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExpiredAckAsyncConsumerTest.class);

    private BrokerService broker;

    private Connection connection;

    private ConnectionConsumer connectionConsumer;

    private Queue queue;

    private AtomicBoolean finished = new AtomicBoolean();

    private AtomicBoolean failed = new AtomicBoolean();

    @Test(timeout = 60 * 1000)
    public void testAsyncMessageExpiration() throws Exception {
        ExecutorService executors = Executors.newFixedThreadPool(1);
        final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageProducer producer = session.createProducer(queue);
        producer.setTimeToLive(10L);
        // Send 30 messages and make sure we can consume with multiple threads without failing
        // even when messages are expired
        executors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    int count = 0;
                    while ((!(failed.get())) && (count < 30)) {
                        producer.send(session.createTextMessage(("Hello World: " + count)));
                        ExpiredAckAsyncConsumerTest.LOG.info(("sending: " + count));
                        count++;
                        Thread.sleep(100L);
                    } 
                    finished.set(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        connectionConsumer = connection.createConnectionConsumer(queue, null, new ExpiredAckAsyncConsumerTest.TestServerSessionPool(connection), 1000);
        Assert.assertTrue("received messages", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return finished.get();
            }
        }));
        Assert.assertFalse("An exception was received on receive", failed.get());
    }

    /**
     * Simulate a ServerSessionPool in an application server with 15 threads
     */
    private class TestServerSessionPool implements ServerSessionPool {
        Connection connection;

        LinkedBlockingQueue<ExpiredAckAsyncConsumerTest.TestServerSession> serverSessions = new LinkedBlockingQueue<>(10);

        public TestServerSessionPool(Connection connection) throws JMSException {
            this.connection = connection;
            for (int i = 0; i < 15; i++) {
                addSession();
            }
        }

        @Override
        public ServerSession getServerSession() throws JMSException {
            try {
                return serverSessions.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("could not get session");
            }
        }

        public void addSession() {
            try {
                serverSessions.add(new ExpiredAckAsyncConsumerTest.TestServerSession(this));
            } catch (Exception e) {
            }
        }
    }

    /**
     * Simulate a ServerSession
     */
    private class TestServerSession implements ServerSession {
        ExpiredAckAsyncConsumerTest.TestServerSessionPool pool;

        Session session;

        public TestServerSession(ExpiredAckAsyncConsumerTest.TestServerSessionPool pool) throws JMSException {
            this.pool = pool;
            session = pool.connection.createSession(false, AUTO_ACKNOWLEDGE);
            session.setMessageListener(new ExpiredAckAsyncConsumerTest.TestMessageListener());
        }

        @Override
        public Session getSession() throws JMSException {
            return session;
        }

        @Override
        public void start() throws JMSException {
            new Thread() {
                @Override
                public void run() {
                    // execute run on the session
                    if (!(finished.get())) {
                        try {
                            session.run();
                            pool.addSession();
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
        }
    }

    private class TestMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            try {
                Thread.sleep(1000L);
                String text = getText();
                ExpiredAckAsyncConsumerTest.LOG.info(("got message: " + text));
            } catch (Exception e) {
                ExpiredAckAsyncConsumerTest.LOG.error("in onMessage", e);
            }
        }
    }
}

