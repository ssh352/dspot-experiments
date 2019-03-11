/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.redis.state;


import Config.TOPOLOGY_STATE_PROVIDER_CONFIG;
import RedisKeyValueStateProvider.StateConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link RedisKeyValueStateProvider}
 */
public class RedisKeyValueStateProviderTest {
    @Test
    public void testgetDefaultConfig() throws Exception {
        RedisKeyValueStateProvider provider = new RedisKeyValueStateProvider();
        Map<String, Object> topoConf = new HashMap<>();
        // topoConf.put(Config.TOPOLOGY_STATE_PROVIDER_CONFIG, "{\"keyClass\":\"String\"}");
        RedisKeyValueStateProvider.StateConfig config = provider.getStateConfig(topoConf);
        Assert.assertNotNull(config);
    }

    @Test
    public void testgetConfigWithProviderConfig() throws Exception {
        RedisKeyValueStateProvider provider = new RedisKeyValueStateProvider();
        Map<String, Object> topoConf = new HashMap<>();
        topoConf.put(TOPOLOGY_STATE_PROVIDER_CONFIG, ("{\"keyClass\":\"String\", \"valueClass\":\"String\"," + (" \"jedisPoolConfig\":" + "{\"host\":\"localhost\", \"port\":1000}}")));
        RedisKeyValueStateProvider.StateConfig config = provider.getStateConfig(topoConf);
        // System.out.println(config);
        Assert.assertEquals("String", config.keyClass);
        Assert.assertEquals("String", config.valueClass);
        Assert.assertEquals("localhost", config.jedisPoolConfig.getHost());
        Assert.assertEquals(1000, config.jedisPoolConfig.getPort());
    }
}

