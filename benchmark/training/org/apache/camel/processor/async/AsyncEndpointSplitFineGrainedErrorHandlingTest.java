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
package org.apache.camel.processor.async;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointSplitFineGrainedErrorHandlingTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testAsyncEndpoint() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("A", "B", "C");
        getMockEndpoint("mock:after").expectedBodiesReceived("Bye Camel", "Bye Camel", "Bye Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C");
        template.sendBody("direct:start", "A,B,C");
        assertMockEndpointsSatisfied();
        // 3 split messages + 1 redelivery attempt
        Assert.assertEquals((3 + 1), AsyncEndpointSplitFineGrainedErrorHandlingTest.counter);
    }
}
