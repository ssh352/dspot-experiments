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
package org.springframework.cloud.netflix.zuul;


import HttpMethod.GET;
import HttpStatus.OK;
import com.netflix.zuul.ZuulFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "zuul.routes[testclient]:/testing123/**")
@DirtiesContext
public class SimpleZuulServerApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RouteLocator routes;

    @Test
    public void bindRoute() {
        assertThat(getRoute("/testing123/**")).isNotNull();
    }

    @Test
    public void getOnSelf() {
        ResponseEntity<String> result = testRestTemplate.exchange("/", GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Hello world");
    }

    @Test
    public void getOnSelfViaFilter() {
        ResponseEntity<String> result = testRestTemplate.exchange("/testing123/1", GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    // Don't use @SpringBootApplication because we don't want to component scan
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulServer
    @Import(NoSecurityConfiguration.class)
    static class SimpleZuulServerApplication {
        @RequestMapping("/local")
        public String local() {
            return "Hello local";
        }

        @RequestMapping("/")
        public String home() {
            return "Hello world";
        }

        @Bean
        public ZuulFilter sampleFilter() {
            return new ZuulFilter() {
                @Override
                public String filterType() {
                    return PRE_TYPE;
                }

                @Override
                public boolean shouldFilter() {
                    return true;
                }

                @Override
                public Object run() {
                    return null;
                }

                @Override
                public int filterOrder() {
                    return 0;
                }
            };
        }
    }
}

