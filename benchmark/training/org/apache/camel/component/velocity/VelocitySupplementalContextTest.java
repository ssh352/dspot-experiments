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
package org.apache.camel.component.velocity;


import VelocityConstants.VELOCITY_TEMPLATE;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class VelocitySupplementalContextTest extends CamelTestSupport {
    @Produce(uri = "direct:input")
    protected ProducerTemplate inputEndpoint;

    @EndpointInject(uri = "mock:results")
    protected MockEndpoint outputEndpoint;

    @Test
    public void testCamelRoute() throws Exception {
        outputEndpoint.expectedMessageCount(1);
        outputEndpoint.expectedHeaderReceived("body", "new_body");
        outputEndpoint.expectedHeaderReceived("in.body", "old_body");
        outputEndpoint.expectedBodiesReceived("bar");
        Map<String, Object> headers = new HashMap<>();
        headers.put(VELOCITY_TEMPLATE, ("#set( $headers.body = ${body} )\n#set( $headers[\'in.body\'] = $in.body )\n" + "bar"));
        inputEndpoint.sendBodyAndHeaders("old_body", headers);
        assertMockEndpointsSatisfied();
    }
}
