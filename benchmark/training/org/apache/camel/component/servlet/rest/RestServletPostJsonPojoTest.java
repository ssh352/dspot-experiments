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
package org.apache.camel.component.servlet.rest;


import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import java.io.ByteArrayInputStream;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.servlet.ServletCamelRouterTestSupport;
import org.junit.Test;


public class RestServletPostJsonPojoTest extends ServletCamelRouterTestSupport {
    @Test
    public void testPostJaxbPojo() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:input");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(UserPojo.class);
        String body = "{\"id\": 123, \"name\": \"Donald Duck\"}";
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/users/new"), new ByteArrayInputStream(body.getBytes()), "application/json");
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals(200, response.getResponseCode());
        assertMockEndpointsSatisfied();
        UserPojo user = mock.getReceivedExchanges().get(0).getIn().getBody(UserPojo.class);
        assertNotNull(user);
        assertEquals(123, user.getId());
        assertEquals("Donald Duck", user.getName());
    }
}
