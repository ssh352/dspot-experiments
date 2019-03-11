/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.netflix.zuul.filters;


import CookieSpecs.DEFAULT;
import HttpMethod.PATCH;
import HttpMethod.PUT;
import HttpStatus.OK;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomHostRoutingFilterTests.SampleCustomZuulProxyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "server.servlet.context-path: /app" })
@DirtiesContext
public class CustomHostRoutingFilterTests {
    @LocalServerPort
    private int port;

    @Autowired
    private DiscoveryClientRouteLocator routes;

    @Autowired
    private RoutesEndpoint endpoint;

    @Test
    public void getOnSelfViaCustomHostRoutingFilter() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/app/self/get/1"), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Get 1");
    }

    @Test
    public void postOnSelfViaCustomHostRoutingFilter() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        MultiValueMap<String, Object> params = new org.springframework.util.LinkedMultiValueMap();
        params.add("id", "2");
        ResponseEntity<String> result = new TestRestTemplate().postForEntity((("http://localhost:" + (this.port)) + "/app/self/post"), params, String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Post 2");
    }

    @Test
    public void putOnSelfViaCustomHostRoutingFilter() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/app/self/put/3"), PUT, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Put 3");
    }

    @Test
    public void patchOnSelfViaCustomHostRoutingFilter() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        MultiValueMap<String, Object> params = new org.springframework.util.LinkedMultiValueMap();
        params.add("patch", "5");
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/app/self/patch/4"), PATCH, new org.springframework.http.HttpEntity(params), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Patch 45");
    }

    @Test
    public void getOnSelfIgnoredHeaders() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/app/self/get/1"), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        TestCase.assertTrue(result.getHeaders().containsKey("X-NotIgnored"));
        TestCase.assertFalse(result.getHeaders().containsKey("X-Ignored"));
    }

    @Test
    public void getOnSelfWithSessionCookie() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/app"));
        this.endpoint.reset();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result1 = restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/app/self/cookie/1"), String.class);
        ResponseEntity<String> result2 = restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/app/self/cookie/2"), String.class);
        assertThat(result1.getBody()).isEqualTo("SetCookie 1");
        assertThat(result2.getBody()).isEqualTo("GetCookie 1");
    }

    @Configuration
    @EnableAutoConfiguration
    @RestController
    @Import(NoSecurityConfiguration.class)
    static class SampleCustomZuulProxyApplication {
        @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
        public String get(@PathVariable
        String id, HttpServletResponse response) {
            response.setHeader("X-Ignored", "foo");
            response.setHeader("X-NotIgnored", "bar");
            return "Get " + id;
        }

        @RequestMapping(value = "/cookie/{id}", method = RequestMethod.GET)
        public String getWithCookie(@PathVariable
        String id, HttpSession session) {
            Object testCookie = session.getAttribute("testCookie");
            if (testCookie != null) {
                return "GetCookie " + testCookie;
            }
            session.setAttribute("testCookie", id);
            return "SetCookie " + id;
        }

        @RequestMapping(value = "/post", method = RequestMethod.POST)
        public String post(@RequestParam("id")
        String id) {
            return "Post " + id;
        }

        @RequestMapping(value = "/put/{id}", method = RequestMethod.PUT)
        public String put(@PathVariable
        String id) {
            return "Put " + id;
        }

        @RequestMapping(value = "/patch/{id}", method = RequestMethod.PATCH)
        public String patch(@PathVariable
        String id, @RequestParam("patch")
        String patch) {
            return ("Patch " + id) + patch;
        }

        public static void main(String[] args) {
            SpringApplication.run(CustomHostRoutingFilterTests.SampleCustomZuulProxyApplication.class, args);
        }

        @Configuration
        @EnableZuulProxy
        protected static class CustomZuulProxyConfig {
            @Bean
            public ApacheHttpClientFactory customHttpClientFactory(HttpClientBuilder builder) {
                return new CustomHostRoutingFilterTests.SampleCustomZuulProxyApplication.CustomZuulProxyConfig.CustomApacheHttpClientFactory(builder);
            }

            @Bean
            public CloseableHttpClient closeableClient() {
                return HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(DEFAULT).build()).build();
            }

            @Bean
            public SimpleHostRoutingFilter simpleHostRoutingFilter(ProxyRequestHelper helper, ZuulProperties zuulProperties, CloseableHttpClient httpClient) {
                return new CustomHostRoutingFilterTests.SampleCustomZuulProxyApplication.CustomZuulProxyConfig.CustomHostRoutingFilter(helper, zuulProperties, httpClient);
            }

            private class CustomHostRoutingFilter extends SimpleHostRoutingFilter {
                CustomHostRoutingFilter(ProxyRequestHelper helper, ZuulProperties zuulProperties, CloseableHttpClient httpClient) {
                    super(helper, zuulProperties, httpClient);
                }

                @Override
                public Object run() {
                    addIgnoredHeaders("X-Ignored");
                    return super.run();
                }
            }

            private class CustomApacheHttpClientFactory extends DefaultApacheHttpClientFactory {
                CustomApacheHttpClientFactory(HttpClientBuilder builder) {
                    super(builder);
                }
            }
        }
    }
}

