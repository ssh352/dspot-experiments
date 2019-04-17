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
package org.apache.camel.processor.interceptor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.Breakpoint;
import org.apache.camel.spi.Condition;
import org.junit.Assert;
import org.junit.Test;


public class DebugExceptionEventBreakpointTest extends ContextTestSupport {
    private List<String> logs = new ArrayList<>();

    private Condition exceptionCondition;

    private Breakpoint breakpoint;

    @Test
    public void testDebug() throws Exception {
        context.getDebugger().addBreakpoint(breakpoint, exceptionCondition);
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        try {
            template.sendBody("direct:start", "Hello Camel");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("Breakpoint at ThrowException[java.lang.IllegalArgumentException] caused by: IllegalArgumentException[Damn]", logs.get(0));
    }
}
