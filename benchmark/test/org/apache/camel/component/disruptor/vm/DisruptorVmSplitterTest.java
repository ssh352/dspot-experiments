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
package org.apache.camel.component.disruptor.vm;


import java.util.Arrays;
import java.util.List;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.vm.AbstractVmTestSupport;
import org.junit.Test;


public class DisruptorVmSplitterTest extends AbstractVmTestSupport {
    @Test
    public void testSplitUsingMethodCall() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedBodiesReceived("Claus", "James", "Willem");
        template2.sendBody("direct:start", "Claus@James@Willem");
        assertMockEndpointsSatisfied();
    }

    public static final class SplitWordsBean {
        private SplitWordsBean() {
            // Helper Class
        }

        public static List<String> splitWords(String body) {
            // here we split the payload using java code
            // we have the true power of Java to do the splitting
            // as we like. As this is based on a unit test we just do it easy
            return Arrays.asList(body.split("@"));
        }
    }
}
