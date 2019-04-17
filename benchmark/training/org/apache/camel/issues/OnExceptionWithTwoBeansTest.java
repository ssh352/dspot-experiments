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
package org.apache.camel.issues;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class OnExceptionWithTwoBeansTest extends ContextTestSupport {
    @Test
    public void testOnExceptionFirstBean() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(1);
        getMockEndpoint("mock:bean").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("direct:start", "illegal");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOnExceptionSecondBean() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(1);
        getMockEndpoint("mock:bean").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("direct:start", "handle");
        assertMockEndpointsSatisfied();
    }

    public class MyBean1 {
        public String checkin(String message) {
            if ("illegal".equals(message)) {
                throw new IllegalArgumentException();
            }
            return message;
        }
    }

    public class MyBean2 {
        public String handle(String message) {
            if ("handle".equals(message)) {
                throw new IllegalArgumentException();
            }
            return message;
        }
    }
}
