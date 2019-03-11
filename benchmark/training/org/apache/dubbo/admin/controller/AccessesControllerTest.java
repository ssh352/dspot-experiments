/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.admin.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.model.dto.AccessDTO;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.service.ProviderService;
import org.apache.dubbo.admin.service.RouteService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;


public class AccessesControllerTest extends AbstractSpringIntegrationTest {
    private final String env = "whatever";

    @MockBean
    private RouteService routeService;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void searchAccess() throws IOException {
        AccessDTO accessDTO = new AccessDTO();
        ResponseEntity<String> response;
        String exceptResponseBody;
        // application and service is all empty
        response = restTemplate.getForEntity(url("/api/{env}/rules/access"), String.class, env);
        Map map = objectMapper.readValue(response.getBody(), Map.class);
        Assert.assertFalse("should return a fail response", ((Boolean) (map.get("success"))));
        // when application is present
        String application = "applicationName";
        Mockito.when(routeService.findAccess(application)).thenReturn(accessDTO);
        response = restTemplate.getForEntity(url("/api/{env}/rules/access?application={application}"), String.class, env, application);
        exceptResponseBody = objectMapper.writeValueAsString(Collections.singletonList(accessDTO));
        Assert.assertEquals(exceptResponseBody, response.getBody());
        // when service is present
        String service = "serviceName";
        Mockito.when(routeService.findAccess(service)).thenReturn(accessDTO);
        response = restTemplate.getForEntity(url("/api/{env}/rules/access?service={service}"), String.class, env, service);
        exceptResponseBody = objectMapper.writeValueAsString(Collections.singletonList(accessDTO));
        Assert.assertEquals(exceptResponseBody, response.getBody());
    }

    @Test
    public void detailAccess() throws JsonProcessingException {
        String id = "1";
        AccessDTO accessDTO = new AccessDTO();
        Mockito.when(routeService.findAccess(id)).thenReturn(accessDTO);
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/{env}/rules/access/{id}"), String.class, env, id);
        String exceptResponseBody = objectMapper.writeValueAsString(accessDTO);
        Assert.assertEquals(exceptResponseBody, response.getBody());
    }

    @Test
    public void deleteAccess() {
        String id = "1";
        restTemplate.delete(url("/api/{env}/rules/access/{id}"), env, id);
        Mockito.verify(routeService).deleteAccess(id);
    }

    @Test
    public void createAccess() {
        AccessDTO accessDTO = new AccessDTO();
        String application = "applicationName";
        restTemplate.postForLocation(url("/api/{env}/rules/access"), accessDTO, env);
        // when application is present & dubbo's version is 2.6
        accessDTO.setApplication(application);
        Mockito.when(providerService.findVersionInApplication(application)).thenReturn("2.6");
        restTemplate.postForLocation(url("/api/{env}/rules/access"), accessDTO, env);
        // dubbo's version is 2.7
        Mockito.when(providerService.findVersionInApplication(application)).thenReturn("2.7");
        restTemplate.postForLocation(url("/api/{env}/rules/access"), accessDTO, env);
        // black white list is not null
        accessDTO.setBlacklist(new HashSet());
        accessDTO.setWhitelist(new HashSet());
        restTemplate.postForLocation(url("/api/{env}/rules/access"), accessDTO, env);
        Mockito.verify(routeService).createAccess(any(AccessDTO.class));
    }

    @Test
    public void updateAccess() throws IOException {
        AccessDTO accessDTO = new AccessDTO();
        String id = "1";
        // when id is 'Unknown ID'
        restTemplate.put(url("/api/{env}/rules/access/{id}"), accessDTO, env, id);
        Mockito.verify(routeService).findConditionRoute(id);
        // 
        ConditionRouteDTO conditionRouteDTO = Mockito.mock(ConditionRouteDTO.class);
        Mockito.when(routeService.findConditionRoute(id)).thenReturn(conditionRouteDTO);
        restTemplate.put(url("/api/{env}/rules/access/{id}"), accessDTO, env, id);
        Mockito.verify(routeService).updateAccess(any(AccessDTO.class));
    }
}

