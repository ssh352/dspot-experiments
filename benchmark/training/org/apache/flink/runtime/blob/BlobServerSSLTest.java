/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.blob;


import SecurityOptions.SSL_ALGORITHMS;
import SecurityOptions.SSL_INTERNAL_ENABLED;
import SecurityOptions.SSL_KEYSTORE;
import SecurityOptions.SSL_KEYSTORE_PASSWORD;
import SecurityOptions.SSL_KEY_PASSWORD;
import SecurityOptions.SSL_TRUSTSTORE;
import SecurityOptions.SSL_TRUSTSTORE_PASSWORD;
import java.io.IOException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing a {@link BlobServer} would fail with improper SSL config.
 */
public class BlobServerSSLTest extends TestLogger {
    @Test
    public void testFailedToInitWithTwoProtocolsSet() {
        final Configuration config = new Configuration();
        config.setBoolean(SSL_INTERNAL_ENABLED, true);
        config.setString(SSL_KEYSTORE, getClass().getResource("/local127.keystore").getPath());
        config.setString(SSL_KEYSTORE_PASSWORD, "password");
        config.setString(SSL_KEY_PASSWORD, "password");
        config.setString(SSL_TRUSTSTORE, getClass().getResource("/local127.truststore").getPath());
        config.setString(SSL_TRUSTSTORE_PASSWORD, "password");
        config.setString(SSL_ALGORITHMS, "TLSv1,TLSv1.1");
        try (final BlobServer ignored = new BlobServer(config, new VoidBlobStore())) {
            Assert.fail();
        } catch (Exception e) {
            findThrowable(e, IOException.class);
            findThrowableWithMessage(e, "Unable to open BLOB Server in specified port range: 0");
        }
    }

    @Test
    public void testFailedToInitWithInvalidSslKeystoreConfigured() {
        final Configuration config = new Configuration();
        config.setBoolean(SSL_INTERNAL_ENABLED, true);
        config.setString(SSL_KEYSTORE, "invalid.keystore");
        config.setString(SSL_KEYSTORE_PASSWORD, "password");
        config.setString(SSL_KEY_PASSWORD, "password");
        config.setString(SSL_TRUSTSTORE, "invalid.keystore");
        config.setString(SSL_TRUSTSTORE_PASSWORD, "password");
        try (final BlobServer ignored = new BlobServer(config, new VoidBlobStore())) {
            Assert.fail();
        } catch (Exception e) {
            findThrowable(e, IOException.class);
            findThrowableWithMessage(e, "Failed to initialize SSL for the blob server");
        }
    }

    @Test
    public void testFailedToInitWithMissingMandatorySslConfiguration() {
        final Configuration config = new Configuration();
        config.setBoolean(SSL_INTERNAL_ENABLED, true);
        try (final BlobServer ignored = new BlobServer(config, new VoidBlobStore())) {
            Assert.fail();
        } catch (Exception e) {
            findThrowable(e, IOException.class);
            findThrowableWithMessage(e, "Failed to initialize SSL for the blob server");
        }
    }
}

