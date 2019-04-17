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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.support.SynchronizationAdapter;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointUoWFailedTest extends ContextTestSupport {
    private static String beforeThreadName;

    private static String afterThreadName;

    private AsyncEndpointUoWFailedTest.MySynchronization sync = new AsyncEndpointUoWFailedTest.MySynchronization();

    @Test
    public void testAsyncEndpoint() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:after").expectedBodiesReceived("Bye Camel");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.requestBody("direct:start", "Hello Camel", String.class);
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Damn", e.getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
        // wait a bit to ensure UoW has been run
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        Assert.assertFalse("Should use different threads", AsyncEndpointUoWFailedTest.beforeThreadName.equalsIgnoreCase(AsyncEndpointUoWFailedTest.afterThreadName));
        Assert.assertEquals(0, sync.isOnComplete());
        Assert.assertEquals(1, sync.isOnFailure());
    }

    private static class MySynchronization extends SynchronizationAdapter {
        private AtomicInteger onComplete = new AtomicInteger();

        private AtomicInteger onFailure = new AtomicInteger();

        public void onComplete(Exchange exchange) {
            onComplete.incrementAndGet();
        }

        @Override
        public void onFailure(Exchange exchange) {
            onFailure.incrementAndGet();
        }

        public int isOnComplete() {
            return onComplete.get();
        }

        public int isOnFailure() {
            return onFailure.get();
        }
    }
}
