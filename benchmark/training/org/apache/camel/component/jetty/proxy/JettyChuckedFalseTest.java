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
package org.apache.camel.component.jetty.proxy;


import Exchange.CONTENT_LENGTH;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.junit.Test;


public class JettyChuckedFalseTest extends BaseJettyTest {
    @Test
    public void runningTest() throws Exception {
        Exchange exchange = template.request("http://localhost:{{port}}/test", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // we do nothing here, so http producer will send a GET method request
            }
        });
        Message out = exchange.getOut();
        // make sure we have the content-length header
        String len = out.getHeader(CONTENT_LENGTH, String.class);
        assertEquals("We should have the content-length header here.", "20", len);
        String response = out.getBody(String.class);
        assertEquals("Get a wrong response", "This is hello world.", response);
    }
}
