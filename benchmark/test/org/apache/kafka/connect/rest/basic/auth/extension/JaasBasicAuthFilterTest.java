/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.rest.basic.auth.extension;


import JaasUtils.JAVA_LOGIN_CONFIG_PARAM;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.Configuration;
import javax.ws.rs.container.ContainerRequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.*")
public class JaasBasicAuthFilterTest {
    @MockStrict
    private ContainerRequestContext requestContext;

    private JaasBasicAuthFilter jaasBasicAuthFilter = new JaasBasicAuthFilter();

    private String previousJaasConfig;

    private Configuration previousConfiguration;

    @Test
    public void testSuccess() throws IOException {
        File credentialFile = File.createTempFile("credential", ".properties");
        credentialFile.deleteOnExit();
        List<String> lines = new ArrayList<>();
        lines.add("user=password");
        lines.add("user1=password1");
        Files.write(credentialFile.toPath(), lines, StandardCharsets.UTF_8);
        setupJaasConfig("KafkaConnect", credentialFile.getPath(), true);
        setMock("Basic", "user", "password", false);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testBadCredential() throws IOException {
        setMock("Basic", "user1", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testBadPassword() throws IOException {
        setMock("Basic", "user", "password1", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testUnknownBearer() throws IOException {
        setMock("Unknown", "user", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testUnknownLoginModule() throws IOException {
        setupJaasConfig("KafkaConnect1", "/tmp/testcrednetial", true);
        Configuration.setConfiguration(null);
        setMock("Basic", "user", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testUnknownCredentialsFile() throws IOException {
        setupJaasConfig("KafkaConnect", "/tmp/testcrednetial", true);
        Configuration.setConfiguration(null);
        setMock("Basic", "user", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testEmptyCredentialsFile() throws IOException {
        File jaasConfigFile = File.createTempFile("ks-jaas-", ".conf");
        jaasConfigFile.deleteOnExit();
        System.setProperty(JAVA_LOGIN_CONFIG_PARAM, jaasConfigFile.getPath());
        setupJaasConfig("KafkaConnect", "", true);
        Configuration.setConfiguration(null);
        setMock("Basic", "user", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }

    @Test
    public void testNoFileOption() throws IOException {
        File jaasConfigFile = File.createTempFile("ks-jaas-", ".conf");
        jaasConfigFile.deleteOnExit();
        System.setProperty(JAVA_LOGIN_CONFIG_PARAM, jaasConfigFile.getPath());
        setupJaasConfig("KafkaConnect", "", false);
        Configuration.setConfiguration(null);
        setMock("Basic", "user", "password", true);
        jaasBasicAuthFilter.filter(requestContext);
    }
}

