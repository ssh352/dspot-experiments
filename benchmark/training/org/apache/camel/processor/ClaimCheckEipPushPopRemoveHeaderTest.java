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


public class ClaimCheckEipPushPopRemoveHeaderTest extends ContextTestSupport {
    @Test
    public void testPushPopBodyRemoveHeader() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:a").expectedHeaderReceived("foo", 123);
        getMockEndpoint("mock:a").expectedHeaderReceived("bar", "Moes");
        getMockEndpoint("mock:b").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:b").expectedHeaderReceived("foo", 456);
        getMockEndpoint("mock:b").expectedHeaderReceived("bar", "Jacks");
        getMockEndpoint("mock:c").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:c").expectedHeaderReceived("foo", 123);
        getMockEndpoint("mock:c").message(0).header("bar").isNull();
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", 123);
        assertMockEndpointsSatisfied();
    }
}
