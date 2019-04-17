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
package org.apache.camel.component.directvm;


import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class DirectVmConsumerExpressionTest extends ContextTestSupport {
    private CamelContext context2;

    private CamelContext context3;

    private CamelContext context4;

    @Test
    public void testSelectEndpoint() throws Exception {
        MockEndpoint result2 = context2.getEndpoint("mock:result2", MockEndpoint.class);
        result2.expectedBodiesReceived("Hello World");
        MockEndpoint result3 = context3.getEndpoint("mock:result3", MockEndpoint.class);
        result3.expectedBodiesReceived("Hello World");
        MockEndpoint result4 = context4.getEndpoint("mock:result4", MockEndpoint.class);
        result4.expectedMessageCount(0);
        template.sendBody("direct:start", "Hello World");
        MockEndpoint.assertIsSatisfied(context2);
        MockEndpoint.assertIsSatisfied(context3);
        MockEndpoint.assertIsSatisfied(context4);
    }
}
