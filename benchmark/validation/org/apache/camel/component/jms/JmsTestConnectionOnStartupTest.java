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
package org.apache.camel.component.jms;


import org.apache.camel.FailedToCreateConsumerException;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsTestConnectionOnStartupTest extends CamelTestSupport {
    @Test
    public void testConnectionOnStartupConsumerTest() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:foo?testConnectionOnStartup=true").to("mock:foo");
            }
        });
        try {
            context.start();
            fail("Should have thrown an exception");
        } catch (FailedToCreateConsumerException e) {
            assertEquals(("Failed to create Consumer for endpoint: activemq://queue:foo?testConnectionOnStartup=true. " + "Reason: Cannot get JMS Connection on startup for destination foo"), e.getMessage());
        }
    }

    @Test
    public void testConnectionOnStartupProducerTest() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("activemq:queue:foo?testConnectionOnStartup=true");
            }
        });
        try {
            context.start();
            fail("Should have thrown an exception");
        } catch (FailedToCreateRouteException ex) {
            FailedToCreateProducerException e = ((FailedToCreateProducerException) (ex.getCause()));
            assertTrue(e.getMessage().startsWith("Failed to create Producer for endpoint: activemq://queue:foo?testConnectionOnStartup=true."));
            assertTrue(e.getMessage().contains("java.net.ConnectException"));
        }
    }
}
