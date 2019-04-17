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
package org.apache.camel.component.jms.issues;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test to verify that we can route a JMS message and do header lookup by name
 * without mutating it and that it can handle the default keyFormatStrategy with _HYPHEN_
 * in the key name
 */
public class JmsGetHeaderKeyFormatIssueWithContentTypeHeaderTest extends CamelTestSupport {
    private String uri = "activemq:queue:hello?jmsKeyFormatStrategy=default";

    @Test
    public void testSendWithHeaders() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body().isEqualTo("Hello World");
        mock.message(0).header("Content-Type").isEqualTo("text/plain");
        MockEndpoint copy = getMockEndpoint("mock:copy");
        copy.expectedMessageCount(1);
        copy.message(0).body().isEqualTo("Hello World");
        copy.message(0).header("Content-Type").isEqualTo("text/plain");
        template.sendBodyAndHeader(uri, "Hello World", "Content-Type", "text/plain");
        assertMockEndpointsSatisfied();
    }
}
