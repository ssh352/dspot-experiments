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
package org.apache.camel.component.xslt;


import javax.xml.transform.TransformerException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class XsltTransformingExceptionTest extends ContextTestSupport {
    private static final String GOOD_XML_STRING = "<name>Camel</name>";

    private static final String BAD_XML_STRING = "<staff><programmer></programmer></staff>";

    @Test
    public void testXsltException() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", XsltTransformingExceptionTest.BAD_XML_STRING);
            Assert.fail("Except a camel Execution exception here");
        } catch (CamelExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TransformerException));
        }
        // we should not get any message from the result endpoint
        assertMockEndpointsSatisfied();
    }

    // As the transformer is turned into security processing mode,
    // This test behavior is changed.
    @Test
    public void testXsltWithoutException() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", XsltTransformingExceptionTest.GOOD_XML_STRING);
            Assert.fail("Except a camel Execution exception here");
        } catch (CamelExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TransformerException));
        }
        assertMockEndpointsSatisfied();
    }
}
