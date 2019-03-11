/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.reg.zookeeper;


import io.elasticjob.lite.fixture.EmbedTestingServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.KeeperException.NoAuthException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ZookeeperRegistryCenterForAuthTest {
    private static final String NAME_SPACE = ZookeeperRegistryCenterForAuthTest.class.getName();

    private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenterForAuthTest.NAME_SPACE);

    private static ZookeeperRegistryCenter zkRegCenter;

    @Test
    public void assertInitWithDigestSuccess() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(EmbedTestingServer.getConnectionString()).retryPolicy(new RetryOneTime(2000)).authorization("digest", "digest:password".getBytes()).build();
        client.start();
        client.blockUntilConnected();
        Assert.assertThat(client.getData().forPath((("/" + (ZookeeperRegistryCenterForAuthTest.class.getName())) + "/test/deep/nested")), CoreMatchers.is("deepNested".getBytes()));
    }

    @Test(expected = NoAuthException.class)
    public void assertInitWithDigestFailure() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(), new RetryOneTime(2000));
        client.start();
        client.blockUntilConnected();
        client.getData().forPath((("/" + (ZookeeperRegistryCenterForAuthTest.class.getName())) + "/test/deep/nested"));
    }
}

