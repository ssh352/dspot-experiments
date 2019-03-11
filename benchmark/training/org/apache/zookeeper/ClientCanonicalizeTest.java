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
package org.apache.zookeeper;


import SaslServerPrincipal.WrapperInetAddress;
import SaslServerPrincipal.WrapperInetSocketAddress;
import ZKClientConfig.ZK_SASL_CLIENT_CANONICALIZE_HOSTNAME;
import java.io.IOException;
import org.apache.zookeeper.client.ZKClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClientCanonicalizeTest extends ZKTestCase {
    @Test
    public void testClientCanonicalization() throws IOException, InterruptedException {
        SaslServerPrincipal.WrapperInetSocketAddress addr = Mockito.mock(WrapperInetSocketAddress.class);
        SaslServerPrincipal.WrapperInetAddress ia = Mockito.mock(WrapperInetAddress.class);
        Mockito.when(addr.getHostName()).thenReturn("zookeeper.apache.org");
        Mockito.when(addr.getAddress()).thenReturn(ia);
        Mockito.when(ia.getCanonicalHostName()).thenReturn("zk1.apache.org");
        Mockito.when(ia.getHostAddress()).thenReturn("127.0.0.1");
        ZKClientConfig conf = new ZKClientConfig();
        String principal = SaslServerPrincipal.getServerPrincipal(addr, conf);
        Assert.assertEquals("The computed principal does not appear to have been canonicalized", "zookeeper/zk1.apache.org", principal);
    }

    @Test
    public void testClientNoCanonicalization() throws IOException, InterruptedException {
        SaslServerPrincipal.WrapperInetSocketAddress addr = Mockito.mock(WrapperInetSocketAddress.class);
        SaslServerPrincipal.WrapperInetAddress ia = Mockito.mock(WrapperInetAddress.class);
        Mockito.when(addr.getHostName()).thenReturn("zookeeper.apache.org");
        Mockito.when(addr.getAddress()).thenReturn(ia);
        Mockito.when(ia.getCanonicalHostName()).thenReturn("zk1.apache.org");
        Mockito.when(ia.getHostAddress()).thenReturn("127.0.0.1");
        ZKClientConfig conf = new ZKClientConfig();
        conf.setProperty(ZK_SASL_CLIENT_CANONICALIZE_HOSTNAME, "false");
        String principal = SaslServerPrincipal.getServerPrincipal(addr, conf);
        Assert.assertEquals("The computed principal does appears to have been canonicalized incorrectly", "zookeeper/zookeeper.apache.org", principal);
    }

    @Test
    public void testClientCanonicalizationToIp() throws IOException, InterruptedException {
        SaslServerPrincipal.WrapperInetSocketAddress addr = Mockito.mock(WrapperInetSocketAddress.class);
        SaslServerPrincipal.WrapperInetAddress ia = Mockito.mock(WrapperInetAddress.class);
        Mockito.when(addr.getHostName()).thenReturn("zookeeper.apache.org");
        Mockito.when(addr.getAddress()).thenReturn(ia);
        Mockito.when(ia.getCanonicalHostName()).thenReturn("127.0.0.1");
        Mockito.when(ia.getHostAddress()).thenReturn("127.0.0.1");
        ZKClientConfig conf = new ZKClientConfig();
        String principal = SaslServerPrincipal.getServerPrincipal(addr, conf);
        Assert.assertEquals("The computed principal does appear to have falled back to the original host name", "zookeeper/zookeeper.apache.org", principal);
    }
}

