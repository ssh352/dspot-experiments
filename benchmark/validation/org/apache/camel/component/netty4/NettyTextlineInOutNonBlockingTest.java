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
package org.apache.camel.component.netty4;


import org.junit.Test;


public class NettyTextlineInOutNonBlockingTest extends BaseNettyTest {
    private static String beforeThreadName;

    private static String afterThreadName;

    private static String beforeThreadName2;

    private static String afterThreadName2;

    @Test
    public void testNonBlocking() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        String reply = template.requestBody("direct:start", "Hello World", String.class);
        assertEquals("Bye World", reply);
        assertMockEndpointsSatisfied();
        assertFalse("Should not same threads", NettyTextlineInOutNonBlockingTest.beforeThreadName.equalsIgnoreCase(NettyTextlineInOutNonBlockingTest.afterThreadName));
        assertFalse("Should not same threads", NettyTextlineInOutNonBlockingTest.beforeThreadName2.equalsIgnoreCase(NettyTextlineInOutNonBlockingTest.afterThreadName2));
    }
}
