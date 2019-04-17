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
package org.apache.camel.processor.aggregator;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.BodyInAggregatingStrategy;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class AggregateUnknownExecutorServiceRefTest extends ContextTestSupport {
    @Test
    public void testAggregateUnknownExecutorServiceRef() throws Exception {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    // use an unknown executor service ref should fail
                    from("direct:start").aggregate(TestSupport.header("id"), new BodyInAggregatingStrategy()).completionSize(3).executorServiceRef("myUnknownProfile").to("log:foo").to("mock:aggregated");
                }
            });
            context.start();
            Assert.fail("Should have thrown exception");
        } catch (FailedToCreateRouteException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().contains("myUnknownProfile"));
        }
    }
}
