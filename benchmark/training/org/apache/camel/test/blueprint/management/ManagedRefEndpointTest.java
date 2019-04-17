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
package org.apache.camel.test.blueprint.management;


import ServiceStatus.Started;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;


public class ManagedRefEndpointTest extends CamelBlueprintTestSupport {
    @Test
    public void testRef() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        // fire a message to get it running
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("foo").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        MBeanServer mbeanServer = getMBeanServer();
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=producers,*"), null);
        assertEquals(2, set.size());
        for (ObjectName on : set) {
            boolean registered = mbeanServer.isRegistered(on);
            assertTrue("Should be registered", registered);
            String uri = ((String) (mbeanServer.getAttribute(on, "EndpointUri")));
            assertTrue(uri, ((uri.equals("mock://foo")) || (uri.equals("mock://result"))));
            // should be started
            String state = ((String) (mbeanServer.getAttribute(on, "State")));
            assertEquals("Should be started", Started.name(), state);
        }
        set = mbeanServer.queryNames(new ObjectName("*:type=endpoints,*"), null);
        assertEquals(4, set.size());
        Set<String> uris = new HashSet<>(Arrays.asList("direct://start", "mock://foo", "mock://result", "ref://foo"));
        for (ObjectName on : set) {
            boolean registered = mbeanServer.isRegistered(on);
            assertTrue("Should be registered", registered);
            String uri = ((String) (mbeanServer.getAttribute(on, "EndpointUri")));
            assertTrue(uri, uris.contains(uri));
        }
    }
}
