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


import Exchange.EXCEPTION_CAUGHT;
import Exchange.FAILURE_ENDPOINT;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TryFinallyCaughtExceptionTest extends ContextTestSupport {
    @Test
    public void testTryFinallyCaughtException() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        MockEndpoint error = getMockEndpoint("mock:b");
        error.expectedMessageCount(1);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
        Exception e = error.getReceivedExchanges().get(0).getProperty(EXCEPTION_CAUGHT, Exception.class);
        Assert.assertNotNull(e);
        Assert.assertEquals("Forced", e.getMessage());
        String to = error.getReceivedExchanges().get(0).getProperty(FAILURE_ENDPOINT, String.class);
        Assert.assertEquals("bean://myBean?method=doSomething", to);
    }
}
