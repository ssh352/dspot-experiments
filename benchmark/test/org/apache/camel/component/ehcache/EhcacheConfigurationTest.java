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
package org.apache.camel.component.ehcache;


import org.apache.camel.EndpointInject;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.ehcache.Cache;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.ResourceType;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.Ehcache;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


public class EhcacheConfigurationTest extends CamelTestSupport {
    @EndpointInject(uri = "ehcache:globalConfig")
    EhcacheEndpoint globalConfig;

    @EndpointInject(uri = "ehcache:customConfig")
    EhcacheEndpoint customConfig;

    // *****************************
    // Test
    // *****************************
    @Test
    public void testConfiguration() throws Exception {
        Cache<String, String> globalConfigCache = globalConfig.getManager().getCache("globalConfig", String.class, String.class);
        Cache<String, String> customConfigCache = customConfig.getManager().getCache("customConfig", String.class, String.class);
        assertTrue((globalConfigCache instanceof Ehcache));
        assertTrue((customConfigCache instanceof Ehcache));
        CacheRuntimeConfiguration<String, String> gc = globalConfigCache.getRuntimeConfiguration();
        assertEquals(100, gc.getResourcePools().getPoolForResource(HEAP).getSize());
        assertEquals(EntryUnit.ENTRIES, gc.getResourcePools().getPoolForResource(HEAP).getUnit());
        assertEquals(1, gc.getResourcePools().getPoolForResource(OFFHEAP).getSize());
        assertEquals(MemoryUnit.MB, gc.getResourcePools().getPoolForResource(OFFHEAP).getUnit());
        CacheRuntimeConfiguration<String, String> cc = customConfigCache.getRuntimeConfiguration();
        assertEquals(200, cc.getResourcePools().getPoolForResource(HEAP).getSize());
        assertEquals(EntryUnit.ENTRIES, cc.getResourcePools().getPoolForResource(HEAP).getUnit());
        assertEquals(2, cc.getResourcePools().getPoolForResource(OFFHEAP).getSize());
        assertEquals(MemoryUnit.MB, cc.getResourcePools().getPoolForResource(OFFHEAP).getUnit());
    }
}
