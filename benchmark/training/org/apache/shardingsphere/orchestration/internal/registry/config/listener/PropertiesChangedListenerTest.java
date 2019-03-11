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
package org.apache.shardingsphere.orchestration.internal.registry.config.listener;


import org.apache.shardingsphere.orchestration.reg.api.RegistryCenter;
import org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent.ChangedType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class PropertiesChangedListenerTest {
    private static final String PROPERTIES_YAML = "executor.size: 16\nsql.show: true";

    private PropertiesChangedListener propertiesChangedListener;

    @Mock
    private RegistryCenter regCenter;

    @Test
    public void assertCreateShardingOrchestrationEvent() {
        Assert.assertThat(propertiesChangedListener.createShardingOrchestrationEvent(new org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent("test", PropertiesChangedListenerTest.PROPERTIES_YAML, ChangedType.UPDATED)).getProps().get("sql.show"), CoreMatchers.is(((Object) (true))));
    }
}

