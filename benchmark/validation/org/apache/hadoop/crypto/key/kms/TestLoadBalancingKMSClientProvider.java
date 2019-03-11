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
package org.apache.hadoop.crypto.key.kms;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import CommonConfigurationKeysPublic.KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.crypto.key.KeyProvider.Options;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.net.ConnectTimeoutException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestLoadBalancingKMSClientProvider {
    @Rule
    public Timeout testTimeout = new Timeout((30 * 1000));

    @Test
    public void testCreation() throws Exception {
        Configuration conf = new Configuration();
        KeyProvider kp = new KMSClientProvider.Factory().createProvider(new URI("kms://http@host1:9600/kms/foo"), conf);
        Assert.assertTrue((kp instanceof LoadBalancingKMSClientProvider));
        KMSClientProvider[] providers = getProviders();
        Assert.assertEquals(1, providers.length);
        Assert.assertEquals(Sets.newHashSet("http://host1:9600/kms/foo/v1/"), Sets.newHashSet(providers[0].getKMSUrl()));
        kp = new KMSClientProvider.Factory().createProvider(new URI("kms://http@host1;host2;host3:9600/kms/foo"), conf);
        Assert.assertTrue((kp instanceof LoadBalancingKMSClientProvider));
        providers = ((LoadBalancingKMSClientProvider) (kp)).getProviders();
        Assert.assertEquals(3, providers.length);
        Assert.assertEquals(Sets.newHashSet("http://host1:9600/kms/foo/v1/", "http://host2:9600/kms/foo/v1/", "http://host3:9600/kms/foo/v1/"), Sets.newHashSet(providers[0].getKMSUrl(), providers[1].getKMSUrl(), providers[2].getKMSUrl()));
        kp = new KMSClientProvider.Factory().createProvider(new URI("kms://http@host1;host2;host3:9600/kms/foo"), conf);
        Assert.assertTrue((kp instanceof LoadBalancingKMSClientProvider));
        providers = ((LoadBalancingKMSClientProvider) (kp)).getProviders();
        Assert.assertEquals(3, providers.length);
        Assert.assertEquals(Sets.newHashSet("http://host1:9600/kms/foo/v1/", "http://host2:9600/kms/foo/v1/", "http://host3:9600/kms/foo/v1/"), Sets.newHashSet(providers[0].getKMSUrl(), providers[1].getKMSUrl(), providers[2].getKMSUrl()));
    }

    @Test
    public void testLoadBalancing() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("p1", "v1", new byte[0]));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("p2", "v2", new byte[0]));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("p3", "v3", new byte[0]));
        KeyProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3 }, 0, conf);
        Assert.assertEquals("p1", kp.createKey("test1", new Options(conf)).getName());
        Assert.assertEquals("p2", kp.createKey("test2", new Options(conf)).getName());
        Assert.assertEquals("p3", kp.createKey("test3", new Options(conf)).getName());
        Assert.assertEquals("p1", kp.createKey("test4", new Options(conf)).getName());
    }

    @Test
    public void testLoadBalancingWithFailure() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("p1", "v1", new byte[0]));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        // This should not be retried
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new NoSuchAlgorithmException("p2"));
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("p3", "v3", new byte[0]));
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        // This should be retried
        KMSClientProvider p4 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p4.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p4"));
        Mockito.when(p4.getKMSUrl()).thenReturn("p4");
        KeyProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3, p4 }, 0, conf);
        Assert.assertEquals("p1", kp.createKey("test4", new Options(conf)).getName());
        // Exceptions other than IOExceptions will not be retried
        try {
            kp.createKey("test1", new Options(conf)).getName();
            Assert.fail("Should fail since its not an IOException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NoSuchAlgorithmException));
        }
        Assert.assertEquals("p3", kp.createKey("test2", new Options(conf)).getName());
        // IOException will trigger retry in next provider
        Assert.assertEquals("p1", kp.createKey("test3", new Options(conf)).getName());
    }

    @Test
    public void testLoadBalancingWithAllBadNodes() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p3"));
        KMSClientProvider p4 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p4.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p4"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        Mockito.when(p4.getKMSUrl()).thenReturn("p4");
        KeyProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3, p4 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf)).getName();
            Assert.fail("Should fail since all providers threw an IOException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IOException));
        }
    }

    private class MyKMSClientProvider extends KMSClientProvider {
        public MyKMSClientProvider(URI uri, Configuration conf) throws IOException {
            super(uri, conf);
        }

        @Override
        public KeyProviderCryptoExtension.EncryptedKeyVersion generateEncryptedKey(final String encryptionKeyName) throws IOException, GeneralSecurityException {
            TestLoadBalancingKMSClientProvider.throwEx(new AuthenticationException("bar"));
            return null;
        }

        @Override
        public KeyVersion decryptEncryptedKey(final KeyProviderCryptoExtension.EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
            TestLoadBalancingKMSClientProvider.throwEx(new AuthenticationException("bar"));
            return null;
        }

        @Override
        public KeyVersion createKey(final String name, final Options options) throws IOException, NoSuchAlgorithmException {
            TestLoadBalancingKMSClientProvider.throwEx(new AuthenticationException("bar"));
            return null;
        }

        @Override
        public KeyVersion rollNewVersion(final String name) throws IOException, NoSuchAlgorithmException {
            TestLoadBalancingKMSClientProvider.throwEx(new AuthenticationException("bar"));
            return null;
        }
    }

    @Test
    public void testClassCastException() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = new TestLoadBalancingKMSClientProvider.MyKMSClientProvider(new URI("kms://http@host1:9600/kms/foo"), conf);
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1 }, 0, conf);
        try {
            kp.generateEncryptedKey("foo");
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getCause().getClass().getName().contains("AuthenticationException"));
        }
        try {
            final KeyProviderCryptoExtension.EncryptedKeyVersion encryptedKeyVersion = Mockito.mock(KeyProviderCryptoExtension.EncryptedKeyVersion.class);
            kp.decryptEncryptedKey(encryptedKeyVersion);
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getCause().getClass().getName().contains("AuthenticationException"));
        }
        try {
            final KeyProvider.Options options = KeyProvider.options(conf);
            kp.createKey("foo", options);
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getCause().getClass().getName().contains("AuthenticationException"));
        }
        try {
            kp.rollNewVersion("foo");
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getCause().getClass().getName().contains("AuthenticationException"));
        }
    }

    /**
     * tests {@link LoadBalancingKMSClientProvider#warmUpEncryptedKeys(String...)}
     * error handling in case when all the providers throws {@link IOException}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWarmUpEncryptedKeysWhenAllProvidersFail() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        String keyName = "key1";
        Mockito.doThrow(new IOException(new AuthorizationException("p1"))).when(p1).warmUpEncryptedKeys(Mockito.anyString());
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.doThrow(new IOException(new AuthorizationException("p2"))).when(p2).warmUpEncryptedKeys(Mockito.anyString());
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.warmUpEncryptedKeys(keyName);
            Assert.fail("Should fail since both providers threw IOException");
        } catch (Exception e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
        Mockito.verify(p1, Mockito.times(1)).warmUpEncryptedKeys(keyName);
        Mockito.verify(p2, Mockito.times(1)).warmUpEncryptedKeys(keyName);
    }

    /**
     * tests {@link LoadBalancingKMSClientProvider#warmUpEncryptedKeys(String...)}
     * error handling in case atleast one provider succeeds.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWarmUpEncryptedKeysWhenOneProviderSucceeds() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        String keyName = "key1";
        Mockito.doThrow(new IOException(new AuthorizationException("p1"))).when(p1).warmUpEncryptedKeys(Mockito.anyString());
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.doNothing().when(p2).warmUpEncryptedKeys(Mockito.anyString());
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.warmUpEncryptedKeys(keyName);
        } catch (Exception e) {
            Assert.fail("Should not throw Exception since p2 doesn't throw Exception");
        }
        Mockito.verify(p1, Mockito.times(1)).warmUpEncryptedKeys(keyName);
        Mockito.verify(p2, Mockito.times(1)).warmUpEncryptedKeys(keyName);
    }

    /**
     * Tests whether retryPolicy fails immediately on non-idempotent operations,
     * after trying each provider once,
     * on encountering IOException which is not SocketException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesNonIdempotentOpWithIOExceptionFailsImmediately() throws Exception {
        Configuration conf = new Configuration();
        final String keyName = "test";
        // Setting total failover attempts to .
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 10);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p3"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3 }, 0, conf);
        try {
            kp.createKey(keyName, new Options(conf));
            Assert.fail("Should fail since all providers threw an IOException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IOException));
        }
        Mockito.verify(kp.getProviders()[0], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(kp.getProviders()[1], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(kp.getProviders()[2], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
    }

    /**
     * Tests whether retryPolicy retries on idempotent operations
     * when encountering IOException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesIdempotentOpWithIOExceptionSucceedsSecondTime() throws Exception {
        Configuration conf = new Configuration();
        final String keyName = "test";
        final KeyProvider.KeyVersion keyVersion = new KMSClientProvider.KMSKeyVersion(keyName, "v1", new byte[0]);
        // Setting total failover attempts to .
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 10);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.getCurrentKey(Mockito.anyString())).thenThrow(new IOException("p1")).thenReturn(keyVersion);
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.getCurrentKey(Mockito.anyString())).thenThrow(new IOException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.getCurrentKey(Mockito.anyString())).thenThrow(new IOException("p3"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3 }, 0, conf);
        KeyProvider.KeyVersion result = kp.getCurrentKey(keyName);
        Assert.assertEquals(keyVersion, result);
        Mockito.verify(kp.getProviders()[0], Mockito.times(2)).getCurrentKey(Mockito.eq(keyName));
        Mockito.verify(kp.getProviders()[1], Mockito.times(1)).getCurrentKey(Mockito.eq(keyName));
        Mockito.verify(kp.getProviders()[2], Mockito.times(1)).getCurrentKey(Mockito.eq(keyName));
    }

    /**
     * Tests that client doesn't retry once it encounters AccessControlException
     * from first provider.
     * This assumes all the kms servers are configured with identical access to
     * keys.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithAccessControlException() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new AccessControlException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p3"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
            Assert.fail("Should fail because provider p1 threw an AccessControlException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof AccessControlException));
        }
        Mockito.verify(p1, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.never()).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p3, Mockito.never()).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests that client doesn't retry once it encounters RunTimeException
     * from first provider.
     * This assumes all the kms servers are configured with identical access to
     * keys.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithRuntimeException() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new RuntimeException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
            Assert.fail("Should fail since provider p1 threw RuntimeException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof RuntimeException));
        }
        Mockito.verify(p1, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.never()).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests the client retries until it finds a good provider.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithTimeoutsException() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 4);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new UnknownHostException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new NoRouteToHostException("p3"));
        KMSClientProvider p4 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p4.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenReturn(new KMSClientProvider.KMSKeyVersion("test3", "v1", new byte[0]));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        Mockito.when(p4.getKMSUrl()).thenReturn("p4");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3, p4 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
        } catch (Exception e) {
            Assert.fail("Provider p4 should have answered the request.");
        }
        Mockito.verify(p1, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p3, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p4, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests the operation succeeds second time after ConnectTimeoutException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesSucceedsSecondTime() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p1")).thenReturn(new KMSClientProvider.KMSKeyVersion("test3", "v1", new byte[0]));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
        } catch (Exception e) {
            Assert.fail("Provider p1 should have answered the request second time.");
        }
        Mockito.verify(p1, Mockito.times(2)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests whether retryPolicy retries specified number of times.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesSpecifiedNumberOfTimes() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 10);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
            Assert.fail("Should fail");
        } catch (Exception e) {
            assert e instanceof ConnectTimeoutException;
        }
        Mockito.verify(p1, Mockito.times(6)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(5)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests whether retryPolicy retries number of times equals to number of
     * providers if conf kms.client.failover.max.attempts is not set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesIfMaxAttemptsNotSet() throws Exception {
        Configuration conf = new Configuration();
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectTimeoutException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
            Assert.fail("Should fail");
        } catch (Exception e) {
            assert e instanceof ConnectTimeoutException;
        }
        Mockito.verify(p1, Mockito.times(2)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests that client reties each provider once, when it encounters
     * AuthenticationException wrapped in an IOException from first provider.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithAuthenticationExceptionWrappedinIOException() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException(new AuthenticationException("p1")));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new IOException(new AuthenticationException("p1")));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        try {
            kp.createKey("test3", new Options(conf));
            Assert.fail("Should fail since provider p1 threw AuthenticationException");
        } catch (Exception e) {
            Assert.assertTrue(((e.getCause()) instanceof AuthenticationException));
        }
        Mockito.verify(p1, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq("test3"), Mockito.any(Options.class));
    }

    /**
     * Tests the operation succeeds second time after SSLHandshakeException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithSSLHandshakeExceptionSucceedsSecondTime() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        final String keyName = "test";
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new SSLHandshakeException("p1")).thenReturn(new KMSClientProvider.KMSKeyVersion(keyName, "v1", new byte[0]));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        kp.createKey(keyName, new Options(conf));
        Mockito.verify(p1, Mockito.times(2)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
    }

    /**
     * Tests the operation fails at every attempt after SSLHandshakeException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesWithSSLHandshakeExceptionFailsAtEveryAttempt() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 2);
        final String keyName = "test";
        final String exceptionMessage = "p1 exception message";
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Exception originalSslEx = new SSLHandshakeException(exceptionMessage);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(originalSslEx);
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new ConnectException("p2 exception message"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        Exception interceptedEx = LambdaTestUtils.intercept(ConnectException.class, ("SSLHandshakeException: " + exceptionMessage), () -> kp.createKey(keyName, new Options(conf)));
        Assert.assertEquals(originalSslEx, interceptedEx.getCause());
        Mockito.verify(p1, Mockito.times(2)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(p2, Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
    }

    /**
     * Tests that if an idempotent operation succeeds second time after
     * SocketTimeoutException, then the operation is successful.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesIdempotentOpWithSocketTimeoutExceptionSucceeds() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 3);
        final List<String> keys = Arrays.asList("testKey");
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.getKeys()).thenThrow(new SocketTimeoutException("p1")).thenReturn(keys);
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.getKeys()).thenThrow(new SocketTimeoutException("p2"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        List<String> result = kp.getKeys();
        Assert.assertEquals(keys, result);
        Mockito.verify(p1, Mockito.times(2)).getKeys();
        Mockito.verify(p2, Mockito.times(1)).getKeys();
    }

    /**
     * Tests that if a non idempotent operation fails at every attempt
     * after SocketTimeoutException, then SocketTimeoutException is thrown.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesIdempotentOpWithSocketTimeoutExceptionFails() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 2);
        final String keyName = "test";
        final String exceptionMessage = "p1 exception message";
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Exception originalEx = new SocketTimeoutException(exceptionMessage);
        Mockito.when(p1.getKeyVersions(Mockito.anyString())).thenThrow(originalEx);
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.getKeyVersions(Mockito.anyString())).thenThrow(new SocketTimeoutException("p2 exception message"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2 }, 0, conf);
        Exception interceptedEx = LambdaTestUtils.intercept(SocketTimeoutException.class, ("SocketTimeoutException: " + exceptionMessage), () -> kp.getKeyVersions(keyName));
        Assert.assertEquals(originalEx, interceptedEx);
        Mockito.verify(p1, Mockito.times(2)).getKeyVersions(Mockito.eq(keyName));
        Mockito.verify(p2, Mockito.times(1)).getKeyVersions(Mockito.eq(keyName));
    }

    /**
     * Tests whether retryPolicy fails immediately on non-idempotent operations,
     * after trying each provider once, on encountering SocketTimeoutException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRetriesNonIdempotentOpWithSocketTimeoutExceptionFails() throws Exception {
        Configuration conf = new Configuration();
        final String keyName = "test";
        // Setting total failover attempts to .
        conf.setInt(KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY, 10);
        KMSClientProvider p1 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p1.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new SocketTimeoutException("p1"));
        KMSClientProvider p2 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p2.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new SocketTimeoutException("p2"));
        KMSClientProvider p3 = Mockito.mock(KMSClientProvider.class);
        Mockito.when(p3.createKey(Mockito.anyString(), Mockito.any(Options.class))).thenThrow(new SocketTimeoutException("p3"));
        Mockito.when(p1.getKMSUrl()).thenReturn("p1");
        Mockito.when(p2.getKMSUrl()).thenReturn("p2");
        Mockito.when(p3.getKMSUrl()).thenReturn("p3");
        LoadBalancingKMSClientProvider kp = new LoadBalancingKMSClientProvider(new KMSClientProvider[]{ p1, p2, p3 }, 0, conf);
        try {
            kp.createKey(keyName, new Options(conf));
            Assert.fail("Should fail since all providers threw a SocketTimeoutException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SocketTimeoutException));
        }
        Mockito.verify(kp.getProviders()[0], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(kp.getProviders()[1], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
        Mockito.verify(kp.getProviders()[2], Mockito.times(1)).createKey(Mockito.eq(keyName), Mockito.any(Options.class));
    }

    @Test
    public void testTokenServiceCreationWithLegacyFormat() throws Exception {
        Configuration conf = new Configuration();
        // Create keyprovider with old token format (ip:port)
        conf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, "kms:/something");
        String authority = "host1:9600";
        URI kmsUri = URI.create((("kms://http@" + authority) + "/kms/foo"));
        KeyProvider kp = new KMSClientProvider.Factory().createProvider(kmsUri, conf);
        Assert.assertTrue((kp instanceof LoadBalancingKMSClientProvider));
        LoadBalancingKMSClientProvider lbkp = ((LoadBalancingKMSClientProvider) (kp));
        Assert.assertEquals(1, lbkp.getProviders().length);
        Assert.assertEquals(authority, lbkp.getCanonicalServiceName());
        for (KMSClientProvider provider : lbkp.getProviders()) {
            Assert.assertEquals(authority, provider.getCanonicalServiceName());
        }
    }

    @Test
    public void testTokenServiceCreationWithUriFormat() throws Exception {
        final Configuration conf = new Configuration();
        final URI kmsUri = URI.create("kms://http@host1;host2;host3:9600/kms/foo");
        final KeyProvider kp = new KMSClientProvider.Factory().createProvider(kmsUri, conf);
        Assert.assertTrue((kp instanceof LoadBalancingKMSClientProvider));
        final LoadBalancingKMSClientProvider lbkp = ((LoadBalancingKMSClientProvider) (kp));
        Assert.assertEquals(kmsUri.toString(), lbkp.getCanonicalServiceName());
        KMSClientProvider[] providers = lbkp.getProviders();
        Assert.assertEquals(3, providers.length);
        for (int i = 0; i < (providers.length); i++) {
            Assert.assertEquals(URI.create(providers[i].getKMSUrl()).getAuthority(), providers[i].getCanonicalServiceName());
            Assert.assertNotEquals(kmsUri, providers[i].getCanonicalServiceName());
        }
    }

    @Test
    public void testGetActualUGI() throws Exception {
        // enable security
        final Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("foo", new String[]{ "hadoop" });
        String providerUriString = "kms://http@host1;host2;host3:9600/kms/foo";
        final URI kmsUri = URI.create(providerUriString);
        // create a fake kms dt
        final Token token = new Token();
        token.setKind(KMSDelegationToken.TOKEN_KIND);
        token.setService(new Text(providerUriString));
        // call getActualUgi() with the current user.
        UserGroupInformation actualUgi = ugi.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
            @Override
            public UserGroupInformation run() throws Exception {
                final KeyProvider kp = new KMSClientProvider.Factory().createProvider(kmsUri, conf);
                final LoadBalancingKMSClientProvider lbkp = ((LoadBalancingKMSClientProvider) (kp));
                final Credentials creds = new Credentials();
                creds.addToken(token.getService(), token);
                UserGroupInformation.getCurrentUser().addCredentials(creds);
                KMSClientProvider[] providers = lbkp.getProviders();
                return providers[0].getActualUgi();
            }
        });
        // make sure getActualUgi() returns the current user, not login user.
        Assert.assertEquals("getActualUgi() should return the current user, not login user", ugi, actualUgi);
    }
}

