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
package org.apache.camel.processor.onexception;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test inspired by user forum.
 */
public class OnExceptionRouteWithDefaultErrorHandlerTest extends ContextTestSupport {
    private MyOwnHandlerBean myOwnHandlerBean;

    private MyServiceBean myServiceBean;

    @Test
    public void testNoError() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "<order><type>myType</type><user>James</user></order>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFunctionalError() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        template.sendBody("direct:start", "<order><type>myType</type><user>Func</user></order>");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("<order><type>myType</type><user>Func</user></order>", myOwnHandlerBean.getPayload());
    }

    @Test
    public void testTechnicalError() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        template.sendBody("direct:start", "<order><type>myType</type><user>Tech</user></order>");
        assertMockEndpointsSatisfied();
        // should not handle it
        Assert.assertNull(myOwnHandlerBean.getPayload());
    }

    @Test
    public void testErrorWhileHandlingException() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "<order><type>myType</type><user>FuncError</user></order>");
            Assert.fail("Should throw a RuntimeCamelException");
        } catch (RuntimeCamelException e) {
            Assert.assertEquals("Damn something did not work", e.getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
        // should not handle it
        Assert.assertNull(myOwnHandlerBean.getPayload());
    }
}
