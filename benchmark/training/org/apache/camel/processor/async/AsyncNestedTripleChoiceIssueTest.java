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
package org.apache.camel.processor.async;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class AsyncNestedTripleChoiceIssueTest extends ContextTestSupport {
    @Test
    public void testNestedChoiceVeryBig() throws Exception {
        getMockEndpoint("mock:low").expectedMessageCount(0);
        getMockEndpoint("mock:med").expectedMessageCount(0);
        getMockEndpoint("mock:big").expectedMessageCount(0);
        getMockEndpoint("mock:verybig").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", 10);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNestedChoiceBig() throws Exception {
        getMockEndpoint("mock:low").expectedMessageCount(0);
        getMockEndpoint("mock:med").expectedMessageCount(0);
        getMockEndpoint("mock:big").expectedMessageCount(1);
        getMockEndpoint("mock:verybig").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", 7);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNestedChoiceMed() throws Exception {
        getMockEndpoint("mock:low").expectedMessageCount(0);
        getMockEndpoint("mock:med").expectedMessageCount(1);
        getMockEndpoint("mock:big").expectedMessageCount(0);
        getMockEndpoint("mock:verybig").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", 4);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNestedChoiceLow() throws Exception {
        getMockEndpoint("mock:low").expectedMessageCount(1);
        getMockEndpoint("mock:med").expectedMessageCount(0);
        getMockEndpoint("mock:big").expectedMessageCount(0);
        getMockEndpoint("mock:verybig").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", 1);
        assertMockEndpointsSatisfied();
    }
}
