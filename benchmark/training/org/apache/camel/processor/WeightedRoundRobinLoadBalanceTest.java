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
package org.apache.camel.processor;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class WeightedRoundRobinLoadBalanceTest extends ContextTestSupport {
    protected MockEndpoint x;

    protected MockEndpoint y;

    protected MockEndpoint z;

    @Test
    public void testRoundRobin() throws Exception {
        x.expectedMessageCount(5);
        y.expectedMessageCount(2);
        z.expectedMessageCount(1);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:start").loadBalance().weighted(true, "4,2,1").to("mock:x", "mock:y", "mock:z");
                // END SNIPPET: example
            }
        });
        context.start();
        sendMessages(1, 2, 3, 4, 5, 6, 7, 8);
        assertMockEndpointsSatisfied();
        x.expectedBodiesReceived(1, 4, 6, 7, 8);
        y.expectedBodiesReceived(2, 5);
        z.expectedBodiesReceived(3);
    }

    @Test
    public void testRoundRobin2() throws Exception {
        x.expectedMessageCount(3);
        y.expectedMessageCount(1);
        z.expectedMessageCount(3);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:start").loadBalance().weighted(true, "2, 1, 3", ",").to("mock:x", "mock:y", "mock:z");
                // END SNIPPET: example
            }
        });
        context.start();
        sendMessages(1, 2, 3, 4, 5, 6, 7);
        assertMockEndpointsSatisfied();
        x.expectedBodiesReceived(1, 4, 7);
        y.expectedBodiesReceived(2);
        z.expectedBodiesReceived(3, 5, 6);
    }

    @Test
    public void testRoundRobinBulk() throws Exception {
        x.expectedMessageCount(10);
        y.expectedMessageCount(15);
        z.expectedMessageCount(25);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:start").loadBalance().weighted(true, "2-3-5", "-").to("mock:x", "mock:y", "mock:z");
                // END SNIPPET: example
            }
        });
        context.start();
        sendBulkMessages(50);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnmatchedRatiosToProcessors() throws Exception {
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    // START SNIPPET: example
                    from("direct:start").loadBalance().weighted(true, "2,3").to("mock:x", "mock:y", "mock:z");
                    // END SNIPPET: example
                }
            });
            context.start();
            Assert.fail("Should have thrown exception");
        } catch (FailedToCreateRouteException e) {
            IllegalArgumentException iae = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Loadbalacing with 3 should match number of distributions 2", iae.getMessage());
        }
    }
}
