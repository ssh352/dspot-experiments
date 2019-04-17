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
package org.apache.camel.component.jetty.rest;


import Exchange.CONTENT_TYPE;
import Exchange.HTTP_METHOD;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.junit.Test;


public class RestJettyAcceptTest extends BaseJettyTest {
    @Test
    public void testJettyProducerNoAccept() throws Exception {
        String out = fluentTemplate.withHeader(HTTP_METHOD, "post").withBody("{ \"name\": \"Donald Duck\" }").to((("http://localhost:" + (BaseJettyTest.getPort())) + "/users/123/update")).request(String.class);
        assertEquals("{ \"status\": \"ok\" }", out);
    }

    @Test
    public void testJettyProducerAcceptValid() throws Exception {
        String out = fluentTemplate.withHeader(CONTENT_TYPE, "application/json").withHeader("Accept", "application/json").withHeader(HTTP_METHOD, "post").withBody("{ \"name\": \"Donald Duck\" }").to((("http://localhost:" + (BaseJettyTest.getPort())) + "/users/123/update")).request(String.class);
        assertEquals("{ \"status\": \"ok\" }", out);
    }

    @Test
    public void testJettyProducerAcceptInvalid() throws Exception {
        try {
            fluentTemplate.withHeader(CONTENT_TYPE, "application/json").withHeader("Accept", "application/xml").withHeader(HTTP_METHOD, "post").withBody("{ \"name\": \"Donald Duck\" }").to((("http://localhost:" + (BaseJettyTest.getPort())) + "/users/123/update")).request(String.class);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            HttpOperationFailedException cause = assertIsInstanceOf(HttpOperationFailedException.class, e.getCause());
            assertEquals(406, cause.getStatusCode());
            assertEquals("", cause.getResponseBody());
        }
    }
}
