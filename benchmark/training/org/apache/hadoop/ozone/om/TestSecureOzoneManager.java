/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.om;


import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.security.x509.SecurityConfig;
import org.apache.hadoop.hdds.security.x509.certificate.client.CertificateClient;
import org.apache.hadoop.hdds.security.x509.certificate.utils.CertificateCodec;
import org.apache.hadoop.hdds.security.x509.keys.KeyCodec;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.security.ssl.KeyStoreTestUtil;
import org.apache.hadoop.test.LambdaTestUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test secure Ozone Manager operation in distributed handler scenario.
 */
public class TestSecureOzoneManager {
    private MiniOzoneCluster cluster = null;

    private OzoneConfiguration conf;

    private String clusterId;

    private String scmId;

    private String omId;

    private Path metaDir;

    @Rule
    public Timeout timeout = new Timeout((1000 * 25));

    /**
     * Test failure cases for secure OM initialization.
     */
    @Test
    public void testSecureOmInitFailures() throws Exception {
        PrivateKey privateKey;
        PublicKey publicKey;
        LogCapturer omLogs = LogCapturer.captureLogs(OzoneManager.getLogger());
        OMStorage omStorage = new OMStorage(conf);
        omStorage.setClusterId(clusterId);
        omStorage.setScmId(scmId);
        omStorage.setOmId(omId);
        omLogs.clearOutput();
        // Case 1: When keypair as well as certificate is missing. Initial keypair
        // boot-up. Get certificate will fail no SCM is not running.
        LambdaTestUtils.intercept(ConnectException.class, ("Connection " + "refused; For more detail"), () -> OzoneManager.initializeSecurity(conf, omStorage));
        SecurityConfig securityConfig = new SecurityConfig(conf);
        CertificateClient client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        privateKey = client.getPrivateKey();
        publicKey = client.getPublicKey();
        Assert.assertNotNull(client.getPrivateKey());
        Assert.assertNotNull(client.getPublicKey());
        Assert.assertNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: GETCERT"));
        omLogs.clearOutput();
        // Case 2: If key pair already exist than response should be RECOVER.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        LambdaTestUtils.intercept(RuntimeException.class, (" OM security" + " initialization failed"), () -> OzoneManager.initializeSecurity(conf, omStorage));
        Assert.assertNotNull(client.getPrivateKey());
        Assert.assertNotNull(client.getPublicKey());
        Assert.assertNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: RECOVER"));
        Assert.assertTrue(omLogs.getOutput().contains((" OM certificate is " + "missing")));
        omLogs.clearOutput();
        // Case 3: When public key as well as certificate is missing.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        FileUtils.deleteQuietly(Paths.get(securityConfig.getKeyLocation().toString(), securityConfig.getPublicKeyFileName()).toFile());
        LambdaTestUtils.intercept(RuntimeException.class, (" OM security" + " initialization failed"), () -> OzoneManager.initializeSecurity(conf, omStorage));
        Assert.assertNotNull(client.getPrivateKey());
        Assert.assertNull(client.getPublicKey());
        Assert.assertNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: FAILURE"));
        omLogs.clearOutput();
        // Case 4: When private key and certificate is missing.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        FileUtils.deleteQuietly(Paths.get(securityConfig.getKeyLocation().toString(), securityConfig.getPrivateKeyFileName()).toFile());
        KeyCodec keyCodec = new KeyCodec(securityConfig);
        keyCodec.writePublicKey(publicKey);
        LambdaTestUtils.intercept(RuntimeException.class, (" OM security" + " initialization failed"), () -> OzoneManager.initializeSecurity(conf, omStorage));
        Assert.assertNull(client.getPrivateKey());
        Assert.assertNotNull(client.getPublicKey());
        Assert.assertNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: FAILURE"));
        omLogs.clearOutput();
        // Case 5: When only certificate is present.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        FileUtils.deleteQuietly(Paths.get(securityConfig.getKeyLocation().toString(), securityConfig.getPublicKeyFileName()).toFile());
        CertificateCodec certCodec = new CertificateCodec(securityConfig);
        X509Certificate x509Certificate = KeyStoreTestUtil.generateCertificate("CN=Test", new KeyPair(publicKey, privateKey), 10, securityConfig.getSignatureAlgo());
        certCodec.writeCertificate(new X509CertificateHolder(x509Certificate.getEncoded()));
        LambdaTestUtils.intercept(RuntimeException.class, (" OM security" + " initialization failed"), () -> OzoneManager.initializeSecurity(conf, omStorage));
        Assert.assertNull(client.getPrivateKey());
        Assert.assertNull(client.getPublicKey());
        Assert.assertNotNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: FAILURE"));
        omLogs.clearOutput();
        // Case 6: When private key and certificate is present.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        FileUtils.deleteQuietly(Paths.get(securityConfig.getKeyLocation().toString(), securityConfig.getPublicKeyFileName()).toFile());
        keyCodec.writePrivateKey(privateKey);
        OzoneManager.initializeSecurity(conf, omStorage);
        Assert.assertNotNull(client.getPrivateKey());
        Assert.assertNotNull(client.getPublicKey());
        Assert.assertNotNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: SUCCESS"));
        omLogs.clearOutput();
        // Case 7 When keypair and certificate is present.
        client = new org.apache.hadoop.hdds.security.x509.certificate.client.OMCertificateClient(securityConfig);
        OzoneManager.initializeSecurity(conf, omStorage);
        Assert.assertNotNull(client.getPrivateKey());
        Assert.assertNotNull(client.getPublicKey());
        Assert.assertNotNull(client.getCertificate());
        Assert.assertTrue(omLogs.getOutput().contains("Init response: SUCCESS"));
        omLogs.clearOutput();
    }
}

