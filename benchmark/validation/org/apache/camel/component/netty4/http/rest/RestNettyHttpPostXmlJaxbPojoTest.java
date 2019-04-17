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
package org.apache.camel.component.netty4.http.rest;


import Exchange.CONTENT_TYPE;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.netty4.http.BaseNettyTest;
import org.junit.Test;


public class RestNettyHttpPostXmlJaxbPojoTest extends BaseNettyTest {
    @Test
    public void testPostJaxbPojo() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:input");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(UserJaxbPojo.class);
        String body = "<user name=\"Donald Duck\" id=\"123\"></user>";
        template.sendBodyAndHeader((("netty4-http:http://localhost:" + (getPort())) + "/users/new"), body, CONTENT_TYPE, "text/xml");
        assertMockEndpointsSatisfied();
        UserJaxbPojo user = mock.getReceivedExchanges().get(0).getIn().getBody(UserJaxbPojo.class);
        assertNotNull(user);
        assertEquals(123, user.getId());
        assertEquals("Donald Duck", user.getName());
    }

    @Test
    public void testPostJaxbPojoNoContentType() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:input");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(UserJaxbPojo.class);
        String body = "<user name=\"Donald Duck\" id=\"456\"></user>";
        template.sendBody((("netty4-http:http://localhost:" + (getPort())) + "/users/new"), body);
        assertMockEndpointsSatisfied();
        UserJaxbPojo user = mock.getReceivedExchanges().get(0).getIn().getBody(UserJaxbPojo.class);
        assertNotNull(user);
        assertEquals(456, user.getId());
        assertEquals("Donald Duck", user.getName());
    }
}
