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
package org.apache.camel.support.jsse;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import org.apache.camel.CamelContext;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class KeyStoreParametersTest extends AbstractJsseParametersTest {
    @Test
    public void testPropertyPlaceholders() throws Exception {
        CamelContext context = this.createPropertiesPlaceholderAwareContext();
        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setCamelContext(context);
        ksp.setType("{{keyStoreParameters.type}}");
        ksp.setProvider("{{keyStoreParameters.provider}}");
        ksp.setResource("{{keyStoreParameters.resource}}");
        ksp.setPassword("{{keyStoreParamerers.password}}");
        KeyStore ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
    }

    @Test
    public void testValidParameters() throws IOException, URISyntaxException, GeneralSecurityException {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        KeyStore ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
        URL resourceUrl = this.getClass().getResource("/org/apache/camel/support/jsse/localhost.ks");
        ksp.setResource(resourceUrl.toExternalForm());
        ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
        resourceUrl = this.getClass().getResource("/org/apache/camel/support/jsse/localhost.ks");
        File file = new File(resourceUrl.toURI());
        ksp.setResource(file.getAbsolutePath());
        ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
    }

    @Test
    public void testExplicitType() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setType("jks");
        KeyStore ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
    }

    @Test
    public void testExplicitProvider() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setProvider(ksp.createKeyStore().getProvider().getName());
        KeyStore ks = ksp.createKeyStore();
        Assert.assertNotNull(ks.getCertificate("server"));
    }

    @Test
    public void testExplicitInvalidProvider() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setProvider("sdfdsfgfdsgdsfg");
        try {
            ksp.createKeyStore();
            Assert.fail();
        } catch (NoSuchProviderException e) {
            // expected
        }
    }

    @Test
    public void testExplicitInvalidType() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setType("1234");
        try {
            ksp.createKeyStore();
            Assert.fail();
        } catch (KeyStoreException e) {
            // expected
        }
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            // checkout http://openjdk.java.net/jeps/229
            return;
        }
        ksp = this.createMinimalKeyStoreParameters();
        ksp.setType("pkcs12");
        try {
            ksp.createKeyStore();
            Assert.fail();
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testIncorrectPassword() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setPassword("");
        try {
            ksp.createKeyStore();
            Assert.fail();
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testIncorrectResource() throws Exception {
        KeyStoreParameters ksp = this.createMinimalKeyStoreParameters();
        ksp.setResource("");
        try {
            ksp.createKeyStore();
            Assert.fail();
        } catch (IOException e) {
            // expected
        }
    }
}
