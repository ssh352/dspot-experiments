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
package org.apache.camel.component.spring.ws;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.apache.camel.component.spring.ws.jaxb.QuoteRequest;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;


@Ignore("TODO: investigate for Camel 3.0")
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerBreadcrumbIdTest extends CamelTestSupport {
    @Autowired
    private CamelEndpointMapping endpointMapping;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Test
    public void consumeWebServiceWithPojoRequestWhichIsWithBreadcrumb() throws Exception {
        QuoteRequest request = new QuoteRequest();
        request.setSymbol("GOOG");
        template.request("direct:webservice-marshall-asin", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                assertNotNull(exchange.getIn().getHeader("breadcrumbId"));
                assertEquals(exchange.getIn().getHeader("breadcrumbId"), "ID-Ralfs-MacBook-Pro-local-50523-1423553069254-0-5");
            }
        });
    }
}
