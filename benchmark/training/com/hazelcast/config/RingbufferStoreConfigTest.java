/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import Warning.NONFINAL_FIELDS;
import com.hazelcast.config.RingbufferStoreConfig.RingbufferStoreConfigReadOnly;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.ringbuffer.impl.RingbufferStoreWrapper;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Properties;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferStoreConfigTest {
    private RingbufferStoreConfig config = new RingbufferStoreConfig();

    @Test
    public void testDefaultSetting() {
        Assert.assertTrue(config.isEnabled());
        Assert.assertNull(config.getClassName());
        Assert.assertNull(config.getFactoryClassName());
        Assert.assertNull(config.getFactoryImplementation());
        Assert.assertNull(config.getStoreImplementation());
        Assert.assertNotNull(config.getProperties());
        Assert.assertTrue(config.getProperties().isEmpty());
    }

    @Test
    public void setStoreImplementation() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        RingbufferStore<Data> store = RingbufferStoreWrapper.create(RingbufferService.getRingbufferNamespace("name"), config, InMemoryFormat.OBJECT, serializationService, null);
        config.setStoreImplementation(store);
        Assert.assertEquals(store, config.getStoreImplementation());
    }

    @Test
    public void setProperties() {
        Properties properties = new Properties();
        properties.put("key", "value");
        config.setProperties(properties);
        Assert.assertEquals(properties, config.getProperties());
    }

    @Test
    public void setProperty() {
        config.setProperty("key", "value");
        Assert.assertEquals("value", config.getProperty("key"));
    }

    @Test
    public void setFactoryClassName() {
        config.setFactoryClassName("myFactoryClassName");
        Assert.assertEquals("myFactoryClassName", config.getFactoryClassName());
    }

    @Test
    public void setFactoryImplementation() {
        RingbufferStoreFactory factory = new RingbufferStoreFactory() {
            @Override
            public RingbufferStore newRingbufferStore(String name, Properties properties) {
                return null;
            }
        };
        config.setFactoryImplementation(factory);
        Assert.assertEquals(factory, config.getFactoryImplementation());
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(RingbufferStoreConfig.class).allFieldsShouldBeUsedExcept("readOnly").suppress(NONFINAL_FIELDS).withPrefabValues(RingbufferStoreConfigReadOnly.class, new RingbufferStoreConfigReadOnly(new RingbufferStoreConfig().setClassName("red")), new RingbufferStoreConfigReadOnly(new RingbufferStoreConfig().setClassName("black"))).verify();
    }
}
