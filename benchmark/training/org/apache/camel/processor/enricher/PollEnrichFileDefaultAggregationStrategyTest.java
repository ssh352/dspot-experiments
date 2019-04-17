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
package org.apache.camel.processor.enricher;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class PollEnrichFileDefaultAggregationStrategyTest extends ContextTestSupport {
    @Test
    public void testPollEnrichDefaultAggregationStrategyBody() throws Exception {
        getMockEndpoint("mock:start").expectedBodiesReceived("Start");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Big file");
        mock.expectedFileExists("target/data/enrich/.done/AAA.fin");
        mock.expectedFileExists("target/data/enrichdata/.done/AAA.dat");
        template.sendBodyAndHeader("file://target/data/enrich", "Start", FILE_NAME, "AAA.fin");
        log.info("Sleeping for 0.25 sec before writing enrichdata file");
        Thread.sleep(250);
        template.sendBodyAndHeader("file://target/data/enrichdata", "Big file", FILE_NAME, "AAA.dat");
        log.info("... write done");
        assertMockEndpointsSatisfied();
        PollEnrichFileDefaultAggregationStrategyTest.assertFileDoesNotExists("target/data/enrichdata/AAA.dat.camelLock");
    }
}
