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
package org.apache.camel.component.restlet;


import Exchange.CONTENT_TYPE;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class RestRestletBindingModeOffWithContractTest extends RestletTestSupport {
    @Test
    public void testBindingModeOffWithContract() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:input");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(UserPojoEx.class);
        String body = "{\"id\": 123, \"name\": \"Donald Duck\"}";
        Object answer = template.requestBodyAndHeader((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/new"), body, CONTENT_TYPE, "application/json");
        assertNotNull(answer);
        BufferedReader reader = new BufferedReader(new InputStreamReader(((InputStream) (answer))));
        String line;
        String answerString = "";
        while ((line = reader.readLine()) != null) {
            answerString += line;
        } 
        assertTrue(("Unexpected response: " + answerString), answerString.contains("\"active\":true"));
        assertMockEndpointsSatisfied();
        Object obj = mock.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals(UserPojoEx.class, obj.getClass());
        UserPojoEx user = ((UserPojoEx) (obj));
        assertNotNull(user);
        assertEquals(123, user.getId());
        assertEquals("Donald Duck", user.getName());
        assertEquals(true, user.isActive());
    }
}
