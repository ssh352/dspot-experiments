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
import org.junit.Test;


public class FailoverRoundRobinStickyTest extends ContextTestSupport {
    @Test
    public void testFailoverRoundRobinSticky() throws Exception {
        getMockEndpoint("mock:bad").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:bad2").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:good").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:good2").expectedMessageCount(0);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // as its round robin and sticky based it remembers that last good endpoint
        // and will invoke the last good
        resetMocks();
        getMockEndpoint("mock:bad").expectedMessageCount(0);
        getMockEndpoint("mock:bad2").expectedMessageCount(0);
        getMockEndpoint("mock:good").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:good2").expectedMessageCount(0);
        template.sendBody("direct:start", "Bye World");
        assertMockEndpointsSatisfied();
    }
}
