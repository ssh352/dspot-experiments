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
package org.springframework.cloud.netflix.eureka.server;


import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApplicationContextTests.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "spring.application.name=eureka", "eureka.dashboard.enabled=false" })
public class ApplicationDashboardDisabledTests {
    @Value("${local.server.port}")
    private int port = 0;

    @Test
    public void catalogLoads() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/eureka/apps"), Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void dashboardLoads() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}
