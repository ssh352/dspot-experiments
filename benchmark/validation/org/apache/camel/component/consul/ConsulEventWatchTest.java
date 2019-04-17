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
package org.apache.camel.component.consul;


import ConsulConstants.CONSUL_RESULT;
import com.orbitz.consul.EventClient;
import java.util.List;
import java.util.function.Consumer;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ConsulEventWatchTest extends ConsulTestSupport {
    private String key;

    private EventClient client;

    @Test
    public void testWatchEvent() throws Exception {
        List<String> values = generateRandomListOfStrings(3);
        MockEndpoint mock = getMockEndpoint("mock:event-watch");
        mock.expectedBodiesReceived(values);
        mock.expectedHeaderReceived(CONSUL_RESULT, true);
        values.forEach(( v) -> client.fireEvent(key, v));
        mock.assertIsSatisfied();
    }
}
