/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.jetty;


import HttpHeaderNames.CONTENT_TYPE;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.internal.webapp.WebAppContainerTest;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.ClassRule;
import org.junit.Test;


public class JettyServiceTest extends WebAppContainerTest {
    private static final List<Object> jettyBeans = new ArrayList<>();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.http(0);
            sb.https(0);
            sb.tlsSelfSigned();
            sb.serviceUnder("/jsp/", new JettyServiceBuilder().handler(JettyServiceTest.newWebAppContext()).configurator(( s) -> JettyServiceTest.jettyBeans.addAll(s.getBeans())).build().decorate(LoggingService.newDecorator()));
            sb.serviceUnder("/default/", new JettyServiceBuilder().handler(new DefaultHandler()).build());
            final ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase(webAppRoot().getPath());
            sb.serviceUnder("/resources/", new JettyServiceBuilder().handler(resourceHandler).build());
        }
    };

    @Test
    public void configurator() throws Exception {
        assertThat(JettyServiceTest.jettyBeans).hasAtLeastOneElementOfType(ThreadPool.class).hasAtLeastOneElementOfType(WebAppContext.class);
    }

    @Test
    public void defaultHandlerFavicon() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(JettyServiceTest.server.uri("/default/favicon.ico")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(res.getFirstHeader(CONTENT_TYPE.toString()).getValue()).startsWith("image/x-icon");
                assertThat(EntityUtils.toByteArray(res.getEntity()).length).isGreaterThan(0);
            }
        }
    }

    @Test
    public void resourceHandlerWithLargeResource() throws Exception {
        testLarge("/resources/large.txt");
    }
}

