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
package org.apache.camel.component.netty4.http;


import java.util.Collections;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class NettyHttpHeaderFilterStrategyRemovalTest extends BaseNettyTest {
    NettyHttpHeaderFilterStrategy headerFilterStrategy = new NettyHttpHeaderFilterStrategy();

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Test
    public void shouldRemoveStrategyOption() throws Exception {
        String options = "headerFilterStrategy=#headerFilterStrategy";
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.message(0).header(Exchange.HTTP_QUERY).isNull();
        template.sendBody(((("netty4-http:http://localhost:" + (getPort())) + "/?") + options), "message");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void shouldResolveStrategyFromParameter() throws Exception {
        String headerToFilter = "foo";
        headerFilterStrategy.setOutFilter(Collections.singleton(headerToFilter));
        String options = "headerFilterStrategy=#headerFilterStrategy";
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.message(0).header(headerToFilter).isNull();
        template.sendBodyAndHeader(((("netty4-http:http://localhost:" + (getPort())) + "/?") + options), "message", headerToFilter, "headerValue");
        assertMockEndpointsSatisfied();
    }
}
