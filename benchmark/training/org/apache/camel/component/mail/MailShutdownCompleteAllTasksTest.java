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
package org.apache.camel.component.mail;


import Exchange.BATCH_SIZE;
import ShutdownRunningTask.CompleteAllTasks;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test for shutdown.
 */
public class MailShutdownCompleteAllTasksTest extends CamelTestSupport {
    @Test
    public void testShutdownCompleteAllTasks() throws Exception {
        // give it 20 seconds to shutdown
        context.getShutdownStrategy().setTimeout(20);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // let it complete all tasks during shutdown
                from("pop3://jones@localhost?password=secret&consumer.initialDelay=100&consumer.delay=100").routeId("route1").shutdownRunningTask(CompleteAllTasks).delay(500).to("mock:bar");
            }
        });
        context.start();
        MockEndpoint bar = getMockEndpoint("mock:bar");
        bar.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        int batch = bar.getReceivedExchanges().get(0).getProperty(BATCH_SIZE, int.class);
        // shutdown during processing
        context.stop();
        // should route all
        assertEquals("Should complete all messages", batch, bar.getReceivedCounter());
    }
}
