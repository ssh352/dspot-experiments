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
package org.apache.camel.component.sql;


import java.util.List;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class SqlProducerWhereIssueTest extends CamelTestSupport {
    EmbeddedDatabase db;

    @Test
    public void testQueryWhereIssue() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:query");
        mock.expectedMessageCount(1);
        template.requestBodyAndHeader("direct:query", "Hi there!", "lowId", "1");
        assertMockEndpointsSatisfied();
        List list = mock.getReceivedExchanges().get(0).getIn().getBody(List.class);
        Map row = ((Map) (list.get(0)));
        assertEquals("ASF", row.get("LICENSE"));
        assertEquals(2, row.get("ROWCOUNT"));
        row = ((Map) (list.get(1)));
        assertEquals("XXX", row.get("LICENSE"));
        assertEquals(1, row.get("ROWCOUNT"));
    }
}
