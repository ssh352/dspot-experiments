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


import Session.DUPS_OK_ACKNOWLEDGE;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Appender;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ5426Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ5426Test.class);

    private BrokerService brokerService;

    private String connectionUri;

    private AtomicBoolean hasFailureInProducer = new AtomicBoolean(false);

    private Thread producerThread;

    private AtomicBoolean hasErrorInLogger;

    private Appender errorDetectorAppender;

    @Test(timeout = (2 * 60) * 1000)
    public void testConsumerProperlyClosedWithoutError() throws Exception {
        Random rn = new Random();
        final int NUMBER_OF_RUNS = 1000;
        for (int run = 0; run < NUMBER_OF_RUNS; run++) {
            final AtomicInteger numberOfMessagesReceived = new AtomicInteger(0);
            AMQ5426Test.LOG.info("Starting run {} of {}", run, NUMBER_OF_RUNS);
            // Starts a consumer
            Connection connection = createConnectionFactory().createConnection();
            connection.start();
            Session session = connection.createSession(false, DUPS_OK_ACKNOWLEDGE);
            Topic destination = session.createTopic("test.AMQ5426");
            AMQ5426Test.LOG.debug("Created topic: {}", destination);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    AMQ5426Test.LOG.debug("Received message");
                    numberOfMessagesReceived.getAndIncrement();
                }
            });
            AMQ5426Test.LOG.debug("Created consumer: {}", consumer);
            try {
                // Sleep for a random time
                Thread.sleep(((rn.nextInt(5)) + 1));
            } catch (InterruptedException e) {
                // Restore the interrupt
                Thread.currentThread().interrupt();
            }
            // Close the consumer
            AMQ5426Test.LOG.debug("Closing consumer");
            consumer.close();
            session.close();
            connection.close();
            Assert.assertFalse("Exception in Producer Thread", hasFailureInProducer.get());
            Assert.assertFalse("Error detected in Logger", hasErrorInLogger.get());
            AMQ5426Test.LOG.info("Run {} of {} completed, message received: {}", run, NUMBER_OF_RUNS, numberOfMessagesReceived.get());
        }
    }
}

