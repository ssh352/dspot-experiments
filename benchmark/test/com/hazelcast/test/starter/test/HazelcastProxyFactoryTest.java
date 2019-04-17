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
package com.hazelcast.test.starter.test;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.starter.HazelcastAPIDelegatingClassloader;
import com.hazelcast.test.starter.HazelcastProxyFactory;
import com.hazelcast.test.starter.HazelcastStarter;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class HazelcastProxyFactoryTest {
    @Test
    public void testReturnedProxyImplements_sameInterfaceByNameOnTargetClassLoader() throws Exception {
        HazelcastProxyFactoryTest.ProxiedInterface delegate = new HazelcastProxyFactoryTest.ProxiedInterface() {
            @Override
            public void get() {
            }
        };
        // HazelcastAPIDelegatingClassloader will reload the bytes of ProxiedInterface as a new class
        // as happens with every com.hazelcast class that contains "test"
        HazelcastAPIDelegatingClassloader targetClassLoader = new HazelcastAPIDelegatingClassloader(new URL[]{  }, HazelcastProxyFactoryTest.class.getClassLoader());
        Object proxy = HazelcastProxyFactory.proxyObjectForStarter(targetClassLoader, delegate);
        Assert.assertNotNull(proxy);
        Class<?>[] ifaces = proxy.getClass().getInterfaces();
        Assert.assertEquals(1, ifaces.length);
        Class<?> proxyInterface = ifaces[0];
        // it is not the same class but has the same name on a different classloader
        Assert.assertNotEquals(HazelcastProxyFactoryTest.ProxiedInterface.class, proxyInterface);
        Assert.assertEquals(HazelcastProxyFactoryTest.ProxiedInterface.class.getName(), proxyInterface.getName());
        Assert.assertEquals(targetClassLoader, proxyInterface.getClassLoader());
    }

    @Test
    public void testProxyHazelcastInstanceClasses_ofSameVersion_areSame() {
        HazelcastInstance hz1 = HazelcastStarter.newHazelcastInstance("3.8");
        HazelcastInstance hz2 = HazelcastStarter.newHazelcastInstance("3.8");
        try {
            Assert.assertEquals(hz1.getClass(), hz2.getClass());
        } finally {
            hz1.shutdown();
            hz2.shutdown();
        }
    }

    public interface ProxiedInterface {
        void get();
    }
}
