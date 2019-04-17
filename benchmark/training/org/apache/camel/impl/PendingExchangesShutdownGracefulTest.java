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
package org.apache.camel.impl;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class PendingExchangesShutdownGracefulTest extends ContextTestSupport {
    private static String foo = "";

    private static CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testShutdownGraceful() throws Exception {
        getMockEndpoint("mock:foo").expectedMinimumMessageCount(1);
        template.sendBody("seda:foo", "A");
        template.sendBody("seda:foo", "B");
        template.sendBody("seda:foo", "C");
        template.sendBody("seda:foo", "D");
        template.sendBody("seda:foo", "E");
        assertMockEndpointsSatisfied();
        // now stop the route before its complete
        Assert.assertTrue(PendingExchangesShutdownGracefulTest.latch.await(10, TimeUnit.SECONDS));
        context.stop();
        // it should wait as there was 1 inflight exchange and 4 pending messages left
        Assert.assertEquals("Should graceful shutdown", "ABCDE", PendingExchangesShutdownGracefulTest.foo);
    }
}
