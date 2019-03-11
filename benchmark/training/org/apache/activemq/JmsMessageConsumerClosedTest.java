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


import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test behavior of JMS MessageConsumer API implementation when closed.
 */
public class JmsMessageConsumerClosedTest {
    private Connection connection;

    private MessageConsumer consumer;

    private Destination destination;

    private BrokerService brokerService;

    @Test(timeout = 30000)
    public void testCloseWhileReceiveIsBlocked() throws Exception {
        final MessageConsumer consumer = createConsumer();
        Thread closer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    consumer.close();
                } catch (Exception ex) {
                }
            }
        });
        closer.start();
        Assert.assertNull(consumer.receive());
    }

    @Test(timeout = 30000)
    public void testCloseWhileTimedReceiveIsBlocked() throws Exception {
        final MessageConsumer consumer = createConsumer();
        Thread closer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    consumer.close();
                } catch (Exception ex) {
                }
            }
        });
        closer.start();
        Assert.assertNull(consumer.receive(5000));
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testGetMessageSelectorFails() throws Exception {
        consumer.getMessageSelector();
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testGetMessageListenerFails() throws Exception {
        consumer.getMessageListener();
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testSetMessageListenerFails() throws Exception {
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
            }
        });
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testRreceiveFails() throws Exception {
        consumer.receive();
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testRreceiveTimedFails() throws Exception {
        consumer.receive(11);
    }

    @Test(timeout = 30000, expected = IllegalStateException.class)
    public void testRreceiveNoWaitFails() throws Exception {
        consumer.receiveNoWait();
    }

    @Test(timeout = 30000)
    public void testClose() throws Exception {
        consumer.close();
    }
}

