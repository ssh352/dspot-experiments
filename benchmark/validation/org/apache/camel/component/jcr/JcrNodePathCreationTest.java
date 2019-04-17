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
package org.apache.camel.component.jcr;


import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.camel.Exchange;
import org.junit.Test;


public class JcrNodePathCreationTest extends JcrRouteTestSupport {
    private Value[] multiValued;

    @Test
    public void testJcrNodePathCreation() throws Exception {
        Exchange exchange = createExchangeWithBody("<body/>");
        Exchange out = template.send("direct:a", exchange);
        assertNotNull(out);
        String uuid = out.getOut().getBody(String.class);
        assertNotNull("Out body was null; expected JCR node UUID", uuid);
        Session session = openSession();
        try {
            Node node = session.getNodeByIdentifier(uuid);
            assertNotNull(node);
            assertEquals("/home/test/node/with/path", node.getPath());
        } finally {
            if ((session != null) && (session.isLive())) {
                session.logout();
            }
        }
    }

    @Test
    public void testJcrNodePathCreationMultiValued() throws Exception {
        Exchange exchange = createExchangeWithBody(multiValued);
        Exchange out = template.send("direct:a", exchange);
        assertNotNull(out);
        String uuid = out.getOut().getBody(String.class);
        assertNotNull("Out body was null; expected JCR node UUID", uuid);
        Session session = openSession();
        try {
            Node node = session.getNodeByIdentifier(uuid);
            assertNotNull(node);
            assertEquals("/home/test/node/with/path", node.getPath());
            assertTrue(node.getProperty("my.contents.property").isMultiple());
            assertArrayEquals(multiValued, node.getProperty("my.contents.property").getValues());
        } finally {
            if ((session != null) && (session.isLive())) {
                session.logout();
            }
        }
    }
}
