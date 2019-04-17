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
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.junit.Test;


public class SplitterThrowExceptionInExpressionTwoTest extends ContextTestSupport {
    @Test
    public void testSplitterAndVerifyMock() throws Exception {
        getMockEndpoint("mock:cast1").expectedMessageCount(1);
        getMockEndpoint("mock:cast2").expectedMessageCount(0);
        getMockEndpoint("mock:cast3").expectedMessageCount(0);
        getMockEndpoint("mock:error").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("direct:start", "A,B,C");
        assertMockEndpointsSatisfied();
    }

    private static class MyExpression implements Expression {
        public <T> T evaluate(Exchange exchange, Class<T> type) {
            // force an exception early, to test that the onException error handlers
            // can kick in anyway
            throw new org.apache.camel.ExpressionEvaluationException(null, exchange, null);
        }
    }
}
