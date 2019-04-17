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
package org.apache.flink.runtime.rpc;


import AkkaOptions.ASK_TIMEOUT;
import AkkaOptions.TCP_TIMEOUT;
import SecurityOptions.SSL_ALGORITHMS;
import SecurityOptions.SSL_INTERNAL_ENABLED;
import SecurityOptions.SSL_INTERNAL_KEYSTORE;
import SecurityOptions.SSL_INTERNAL_KEYSTORE_PASSWORD;
import SecurityOptions.SSL_INTERNAL_KEY_PASSWORD;
import SecurityOptions.SSL_INTERNAL_TRUSTSTORE;
import SecurityOptions.SSL_INTERNAL_TRUSTSTORE_PASSWORD;
import akka.actor.ActorSystem;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceConfiguration;
import org.apache.flink.runtime.rpc.exceptions.RpcConnectionException;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test validates that the RPC service gives a good message when it cannot
 * connect to an RpcEndpoint.
 */
public class RpcSSLAuthITCase extends TestLogger {
    private static final String KEY_STORE_FILE = RpcSSLAuthITCase.class.getResource("/local127.keystore").getFile();

    private static final String TRUST_STORE_FILE = RpcSSLAuthITCase.class.getResource("/local127.truststore").getFile();

    private static final String UNTRUSTED_KEY_STORE_FILE = RpcSSLAuthITCase.class.getResource("/untrusted.keystore").getFile();

    @Test
    public void testConnectFailure() throws Exception {
        final Configuration baseConfig = new Configuration();
        baseConfig.setString(TCP_TIMEOUT, "1 s");
        // !!! This config has KEY_STORE_FILE / TRUST_STORE_FILE !!!
        Configuration sslConfig1 = new Configuration(baseConfig);
        sslConfig1.setBoolean(SSL_INTERNAL_ENABLED, true);
        sslConfig1.setString(SSL_INTERNAL_KEYSTORE, RpcSSLAuthITCase.KEY_STORE_FILE);
        sslConfig1.setString(SSL_INTERNAL_TRUSTSTORE, RpcSSLAuthITCase.TRUST_STORE_FILE);
        sslConfig1.setString(SSL_INTERNAL_KEYSTORE_PASSWORD, "password");
        sslConfig1.setString(SSL_INTERNAL_KEY_PASSWORD, "password");
        sslConfig1.setString(SSL_INTERNAL_TRUSTSTORE_PASSWORD, "password");
        sslConfig1.setString(SSL_ALGORITHMS, "TLS_RSA_WITH_AES_128_CBC_SHA");
        // !!! This config has KEY_STORE_FILE / UNTRUSTED_KEY_STORE_FILE !!!
        // If this is presented by a client, it will trust the server, but the server will
        // not trust this client in case client auth is enabled.
        Configuration sslConfig2 = new Configuration(baseConfig);
        sslConfig2.setBoolean(SSL_INTERNAL_ENABLED, true);
        sslConfig2.setString(SSL_INTERNAL_KEYSTORE, RpcSSLAuthITCase.UNTRUSTED_KEY_STORE_FILE);
        sslConfig2.setString(SSL_INTERNAL_TRUSTSTORE, RpcSSLAuthITCase.TRUST_STORE_FILE);
        sslConfig2.setString(SSL_INTERNAL_KEYSTORE_PASSWORD, "password");
        sslConfig2.setString(SSL_INTERNAL_KEY_PASSWORD, "password");
        sslConfig2.setString(SSL_INTERNAL_TRUSTSTORE_PASSWORD, "password");
        sslConfig2.setString(SSL_ALGORITHMS, "TLS_RSA_WITH_AES_128_CBC_SHA");
        ActorSystem actorSystem1 = null;
        ActorSystem actorSystem2 = null;
        RpcService rpcService1 = null;
        RpcService rpcService2 = null;
        try {
            actorSystem1 = AkkaUtils.createActorSystem(sslConfig1, "localhost", 0);
            actorSystem2 = AkkaUtils.createActorSystem(sslConfig2, "localhost", 0);
            // to test whether the test is still good:
            // - create actorSystem2 with sslConfig1 (same as actorSystem1) and see that both can connect
            // - set 'require-mutual-authentication = off' in the AkkaUtils ssl config section
            // we start the RPC service with a very long timeout to ensure that the test
            // can only pass if the connection problem is not recognized merely via a timeout
            Configuration configuration = new Configuration();
            configuration.setString(ASK_TIMEOUT, "10000000 s");
            AkkaRpcServiceConfiguration akkaRpcServiceConfig = AkkaRpcServiceConfiguration.fromConfiguration(configuration);
            rpcService1 = new org.apache.flink.runtime.rpc.akka.AkkaRpcService(actorSystem1, akkaRpcServiceConfig);
            rpcService2 = new org.apache.flink.runtime.rpc.akka.AkkaRpcService(actorSystem2, akkaRpcServiceConfig);
            RpcSSLAuthITCase.TestEndpoint endpoint = new RpcSSLAuthITCase.TestEndpoint(rpcService1);
            start();
            CompletableFuture<RpcSSLAuthITCase.TestGateway> future = rpcService2.connect(getAddress(), RpcSSLAuthITCase.TestGateway.class);
            RpcSSLAuthITCase.TestGateway gateway = future.get(10000000, TimeUnit.SECONDS);
            CompletableFuture<String> fooFuture = gateway.foo();
            fooFuture.get();
            Assert.fail("should never complete normally");
        } catch (ExecutionException e) {
            // that is what we want
            Assert.assertTrue(((e.getCause()) instanceof RpcConnectionException));
        } finally {
            final CompletableFuture<Void> rpcTerminationFuture1 = (rpcService1 != null) ? rpcService1.stopService() : CompletableFuture.completedFuture(null);
            final CompletableFuture<Void> rpcTerminationFuture2 = (rpcService2 != null) ? rpcService2.stopService() : CompletableFuture.completedFuture(null);
            FutureUtils.waitForAll(Arrays.asList(rpcTerminationFuture1, rpcTerminationFuture2)).get();
        }
    }

    // ------------------------------------------------------------------------
    // Test RPC endpoint
    // ------------------------------------------------------------------------
    /**
     * doc.
     */
    public interface TestGateway extends RpcGateway {
        CompletableFuture<String> foo();
    }

    /**
     * doc.
     */
    public static class TestEndpoint extends RpcEndpoint implements RpcSSLAuthITCase.TestGateway {
        public TestEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        @Override
        public CompletableFuture<String> foo() {
            return CompletableFuture.completedFuture("bar");
        }
    }
}
