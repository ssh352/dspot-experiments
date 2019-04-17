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
package org.apache.camel.component.stringtemplate;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class StringTemplateCustomDelimiterTest extends CamelTestSupport {
    private static final String DIRECT_BRACE = "direct:brace";

    private static final String DIRECT_DOLLAR = "direct:dollar";

    @Test
    public void testWithBraceDelimiter() {
        Exchange response = template.request(StringTemplateCustomDelimiterTest.DIRECT_BRACE, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Yay !");
            }
        });
        assertEquals("With brace delimiter Yay !", response.getOut().getBody().toString().trim());
    }

    @Test
    public void testWithDollarDelimiter() {
        Exchange response = template.request(StringTemplateCustomDelimiterTest.DIRECT_DOLLAR, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Yay !");
            }
        });
        assertEquals("With identical dollar delimiter Yay !", response.getOut().getBody().toString().trim());
    }
}
