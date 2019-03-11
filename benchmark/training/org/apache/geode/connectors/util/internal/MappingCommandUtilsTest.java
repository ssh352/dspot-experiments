/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.connectors.util.internal;


import java.util.ArrayList;
import java.util.List;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheConfig.AsyncEventQueue;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.internal.cli.PreconditionException;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.junit.Test;
import org.mockito.Mockito;


public class MappingCommandUtilsTest {
    private final String TESTREGION = "testRegion";

    private final String GROUPNAME = "cluster";

    ConfigurationPersistenceService configurationPersistenceService;

    CacheConfig cacheConfig;

    RegionConfig regionConfig;

    @Test
    public void getCacheConfigReturnsCorrectCacheConfig() throws PreconditionException {
        Mockito.when(configurationPersistenceService.getCacheConfig(GROUPNAME)).thenReturn(cacheConfig);
        CacheConfig result = MappingCommandUtils.getCacheConfig(configurationPersistenceService, GROUPNAME);
        assertThat(result).isEqualTo(cacheConfig);
    }

    @Test
    public void checkForRegionReturnsCorrectRegionConfig() throws PreconditionException {
        List<RegionConfig> regionsList = new ArrayList<>();
        regionsList.add(regionConfig);
        Mockito.when(regionConfig.getName()).thenReturn(TESTREGION);
        Mockito.when(cacheConfig.getRegions()).thenReturn(regionsList);
        RegionConfig result = MappingCommandUtils.checkForRegion(TESTREGION, cacheConfig, GROUPNAME);
        assertThat(result).isEqualTo(regionConfig);
    }

    @Test
    public void getMappingsFromRegionConfigReturnsCorrectMappings() {
        RegionMapping validRegionMapping = Mockito.mock(RegionMapping.class);
        CacheElement invalidRegionMapping = Mockito.mock(CacheElement.class);
        List<CacheElement> cacheElements = new ArrayList<>();
        cacheElements.add(((CacheElement) (validRegionMapping)));
        cacheElements.add(invalidRegionMapping);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(cacheElements);
        List<RegionMapping> results = MappingCommandUtils.getMappingsFromRegionConfig(cacheConfig, regionConfig, GROUPNAME);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(validRegionMapping);
    }

    @Test
    public void createAsyncEventQueueNameProducesCorrectName() {
        String result = MappingCommandUtils.createAsyncEventQueueName(TESTREGION);
        assertThat(result).isEqualTo("JDBC#testRegion");
    }

    @Test
    public void testFindAsyncEventQueueReturnsCorrectObject() {
        AsyncEventQueue asyncEventQueue = Mockito.mock(AsyncEventQueue.class);
        AsyncEventQueue wrongAsyncEventQueue = Mockito.mock(AsyncEventQueue.class);
        Mockito.when(asyncEventQueue.getId()).thenReturn(MappingCommandUtils.createAsyncEventQueueName(TESTREGION));
        Mockito.when(wrongAsyncEventQueue.getId()).thenReturn("Wrong Id");
        List<AsyncEventQueue> asyncEventQueues = new ArrayList<>();
        asyncEventQueues.add(asyncEventQueue);
        asyncEventQueues.add(wrongAsyncEventQueue);
        Mockito.when(regionConfig.getName()).thenReturn(TESTREGION);
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(asyncEventQueues);
        AsyncEventQueue result = MappingCommandUtils.findAsyncEventQueue(cacheConfig, regionConfig);
        assertThat(result).isEqualTo(asyncEventQueue);
    }

    @Test
    public void testIsMappingAsyncReturnsCorrectValue() {
        AsyncEventQueue asyncEventQueue = Mockito.mock(AsyncEventQueue.class);
        AsyncEventQueue wrongAsyncEventQueue = Mockito.mock(AsyncEventQueue.class);
        Mockito.when(asyncEventQueue.getId()).thenReturn(MappingCommandUtils.createAsyncEventQueueName(TESTREGION));
        Mockito.when(wrongAsyncEventQueue.getId()).thenReturn("Wrong Id");
        List<AsyncEventQueue> asyncEventQueues = new ArrayList<>();
        asyncEventQueues.add(asyncEventQueue);
        asyncEventQueues.add(wrongAsyncEventQueue);
        Mockito.when(regionConfig.getName()).thenReturn(TESTREGION);
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(asyncEventQueues);
        boolean result = MappingCommandUtils.isMappingSynchronous(cacheConfig, regionConfig);
        assertThat(result).isEqualTo(false);
    }
}

