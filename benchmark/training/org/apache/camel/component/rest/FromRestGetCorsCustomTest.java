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
package org.apache.camel.component.rest;


import RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_HEADERS;
import RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_METHODS;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class FromRestGetCorsCustomTest extends ContextTestSupport {
    @Test
    public void testCors() throws Exception {
        // the rest becomes routes and the input is a seda endpoint created by the DummyRestConsumerFactory
        getMockEndpoint("mock:update").expectedMessageCount(1);
        Exchange out = template.request("seda:post-say-bye", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("I was here");
            }
        });
        Assert.assertNotNull(out);
        Assert.assertEquals(out.getOut().getHeader("Access-Control-Allow-Origin"), "myserver");
        Assert.assertEquals(out.getOut().getHeader("Access-Control-Allow-Methods"), CORS_ACCESS_CONTROL_ALLOW_METHODS);
        Assert.assertEquals(out.getOut().getHeader("Access-Control-Allow-Headers"), CORS_ACCESS_CONTROL_ALLOW_HEADERS);
        Assert.assertEquals(out.getOut().getHeader("Access-Control-Max-Age"), "180");
        assertMockEndpointsSatisfied();
    }
}
