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


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


// END SNIPPET: e2
public class DynamicRouterTest extends ContextTestSupport {
    private static int invoked;

    private static List<String> bodies = new ArrayList<>();

    @Test
    public void testDynamicRouter() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:b").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:c").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(5, DynamicRouterTest.invoked);
        Assert.assertEquals(5, DynamicRouterTest.bodies.size());
        Assert.assertEquals("Hello World", DynamicRouterTest.bodies.get(0));
        Assert.assertEquals("Hello World", DynamicRouterTest.bodies.get(1));
        Assert.assertEquals("Hello World", DynamicRouterTest.bodies.get(2));
        Assert.assertEquals("Bye World", DynamicRouterTest.bodies.get(3));
        Assert.assertEquals("Bye World", DynamicRouterTest.bodies.get(4));
    }
}
