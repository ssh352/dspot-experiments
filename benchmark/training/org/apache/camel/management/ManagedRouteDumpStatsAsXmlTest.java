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
package org.apache.camel.management;


import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;
import org.w3c.dom.Document;


public class ManagedRouteDumpStatsAsXmlTest extends ManagementTestSupport {
    @Test
    public void testPerformanceCounterStats() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        // get the stats for the route
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ObjectName.getInstance("org.apache.camel:context=camel-1,type=routes,name=\"foo\"");
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.asyncSendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        String xml = ((String) (mbeanServer.invoke(on, "dumpRouteStatsAsXml", new Object[]{ false, true }, new String[]{ "boolean", "boolean" })));
        log.info(xml);
        // should be valid XML
        Document doc = context.getTypeConverter().convertTo(Document.class, xml);
        assertNotNull(doc);
        int processors = doc.getDocumentElement().getElementsByTagName("processorStat").getLength();
        assertEquals(3, processors);
    }
}
