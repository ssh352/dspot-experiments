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
package org.apache.camel.processor.aggregator;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify that aggregate by timeout only also works.
 */
public class AggregateTimeoutWithExecutorServiceTest extends ContextTestSupport {
    public static final int NUM_AGGREGATORS = 20;

    @Test
    public void testThreadNotUsedForEveryAggregatorWithCustomExecutorService() throws Exception {
        Assert.assertTrue("There should not be a thread for every aggregator when using a shared thread pool", ((AggregateTimeoutWithExecutorServiceTest.aggregateThreadsCount()) < (AggregateTimeoutWithExecutorServiceTest.NUM_AGGREGATORS)));
        // sanity check to make sure were testing routes that work
        for (int i = 0; i < (AggregateTimeoutWithExecutorServiceTest.NUM_AGGREGATORS); ++i) {
            MockEndpoint result = getMockEndpoint(("mock:result" + i));
            // by default the use latest aggregation strategy is used so we get message 4
            result.expectedBodiesReceived("Message 4");
        }
        for (int i = 0; i < (AggregateTimeoutWithExecutorServiceTest.NUM_AGGREGATORS); ++i) {
            for (int j = 0; j < 5; j++) {
                template.sendBodyAndHeader(("direct:start" + i), ("Message " + j), "id", "1");
            }
        }
        assertMockEndpointsSatisfied();
    }
}
