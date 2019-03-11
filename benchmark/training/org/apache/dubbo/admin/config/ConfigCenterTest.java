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
package org.apache.dubbo.admin.config;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.dubbo.admin.common.exception.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;


@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigCenterTest {
    private String zkAddress;

    private TestingServer zkServer;

    private CuratorFramework zkClient;

    @InjectMocks
    private ConfigCenter configCenter;

    @Test
    public void testGetDynamicConfiguration() throws Exception {
        // mock @value inject
        ReflectionTestUtils.setField(configCenter, "configCenter", zkAddress);
        ReflectionTestUtils.setField(configCenter, "group", "dubbo");
        ReflectionTestUtils.setField(configCenter, "username", "username");
        ReflectionTestUtils.setField(configCenter, "password", "password");
        // config is null
        configCenter.getDynamicConfiguration();
        // config is registry address
        zkClient.createContainers("/dubbo/config/dubbo/dubbo.properties");
        zkClient.setData().forPath("/dubbo/config/dubbo/dubbo.properties", "dubbo.registry.address=zookeeper://test-registry.com:2181".getBytes());
        configCenter.getDynamicConfiguration();
        Object registryUrl = ReflectionTestUtils.getField(configCenter, "registryUrl");
        Assert.assertNotNull(registryUrl);
        Assert.assertEquals("test-registry.com", getHost());
        // config is meta date address
        zkClient.setData().forPath("/dubbo/config/dubbo/dubbo.properties", "dubbo.metadata-report.address=zookeeper://test-metadata.com:2181".getBytes());
        configCenter.getDynamicConfiguration();
        Object metadataUrl = ReflectionTestUtils.getField(configCenter, "metadataUrl");
        Assert.assertNotNull(metadataUrl);
        Assert.assertEquals("test-metadata.com", getHost());
        // config is empty
        zkClient.setData().forPath("/dubbo/config/dubbo/dubbo.properties", "".getBytes());
        ReflectionTestUtils.setField(configCenter, "registryUrl", null);
        ReflectionTestUtils.setField(configCenter, "metadataUrl", null);
        configCenter.getDynamicConfiguration();
        Assert.assertNull(ReflectionTestUtils.getField(configCenter, "registryUrl"));
        Assert.assertNull(ReflectionTestUtils.getField(configCenter, "metadataUrl"));
        // configCenter is null
        ReflectionTestUtils.setField(configCenter, "configCenter", null);
        // registryAddress is not null
        ReflectionTestUtils.setField(configCenter, "registryAddress", zkAddress);
        configCenter.getDynamicConfiguration();
        registryUrl = ReflectionTestUtils.getField(configCenter, "registryUrl");
        Assert.assertNotNull(registryUrl);
        Assert.assertEquals("127.0.0.1", getHost());
        // configCenter & registryAddress are null
        try {
            ReflectionTestUtils.setField(configCenter, "configCenter", null);
            ReflectionTestUtils.setField(configCenter, "registryAddress", null);
            configCenter.getDynamicConfiguration();
            Assert.fail("should throw exception when configCenter, registryAddress are all null");
        } catch (ConfigurationException e) {
        }
    }

    @Test
    public void testGetRegistry() throws Exception {
        try {
            configCenter.getRegistry();
            Assert.fail("should throw exception when registryAddress is blank");
        } catch (ConfigurationException e) {
        }
        Assert.assertNull(ReflectionTestUtils.getField(configCenter, "registryUrl"));
        // mock @value inject
        ReflectionTestUtils.setField(configCenter, "registryAddress", zkAddress);
        ReflectionTestUtils.setField(configCenter, "group", "dubbo");
        ReflectionTestUtils.setField(configCenter, "username", "username");
        ReflectionTestUtils.setField(configCenter, "password", "password");
        configCenter.getRegistry();
        Object registryUrl = ReflectionTestUtils.getField(configCenter, "registryUrl");
        Assert.assertNotNull(registryUrl);
        Assert.assertEquals("127.0.0.1", getHost());
    }

    @Test
    public void testGetMetadataCollector() throws Exception {
        // when metadataAddress is empty
        ReflectionTestUtils.setField(configCenter, "metadataAddress", "");
        configCenter.getMetadataCollector();
        Assert.assertNull(ReflectionTestUtils.getField(configCenter, "metadataUrl"));
        // mock @value inject
        ReflectionTestUtils.setField(configCenter, "metadataAddress", zkAddress);
        ReflectionTestUtils.setField(configCenter, "group", "dubbo");
        ReflectionTestUtils.setField(configCenter, "username", "username");
        ReflectionTestUtils.setField(configCenter, "password", "password");
        configCenter.getMetadataCollector();
        Object metadataUrl = ReflectionTestUtils.getField(configCenter, "metadataUrl");
        Assert.assertNotNull(metadataUrl);
        Assert.assertEquals("127.0.0.1", getHost());
    }
}

