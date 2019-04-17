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
package com.hazelcast.core;


import com.hazelcast.config.Config;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyServiceImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistributedObjectTest extends HazelcastTestSupport {
    @Test
    public void testMap() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getMap("test");
        test(instance, object);
    }

    @Test
    public void testQueue() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getQueue("test");
        test(instance, object);
    }

    @Test
    public void testTopic() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getTopic("test");
        test(instance, object);
    }

    @Test
    public void testMultiMap() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getMultiMap("test");
        test(instance, object);
    }

    @Test
    public void testSet() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getSet("test");
        test(instance, object);
    }

    @Test
    public void testList() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getList("test");
        test(instance, object);
    }

    @Test
    public void testExecutorService() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getExecutorService("test");
        test(instance, object);
    }

    @Test
    public void testLock() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getLock("test");
        test(instance, object);
    }

    @Test
    public void testAtomicLong() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getAtomicLong("test");
        test(instance, object);
    }

    @Test
    public void testSemaphore() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getSemaphore("test");
        test(instance, object);
    }

    @Test
    public void testCountdownLatch() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getCountDownLatch("test");
        test(instance, object);
    }

    @Test
    public void testIdGenerator() {
        HazelcastInstance instance = createHazelcastInstance();
        DistributedObject object = instance.getIdGenerator("test");
        test(instance, object);
    }

    @Test
    public void testCustomObject() {
        Config config = new Config();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setImplementation(new DistributedObjectTest.TestInitializingObjectService()).setEnabled(true).setName(DistributedObjectTest.TestInitializingObjectService.NAME));
        HazelcastInstance instance = createHazelcastInstance(config);
        DistributedObjectTest.TestInitializingObject object = instance.getDistributedObject(DistributedObjectTest.TestInitializingObjectService.NAME, "test-object");
        test(instance, object);
    }

    @Test
    public void testInitialization() {
        int nodeCount = 4;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        Config config = new Config();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setImplementation(new DistributedObjectTest.TestInitializingObjectService()).setEnabled(true).setName(DistributedObjectTest.TestInitializingObjectService.NAME));
        String serviceName = DistributedObjectTest.TestInitializingObjectService.NAME;
        String objectName = "test-object";
        HazelcastInstance[] instances = new HazelcastInstance[nodeCount];
        for (int i = 0; i < (instances.length); i++) {
            instances[i] = factory.newHazelcastInstance(config);
            DistributedObjectTest.TestInitializingObject obj2 = instances[i].getDistributedObject(serviceName, objectName);
            Assert.assertTrue(obj2.init.get());
            Assert.assertFalse(obj2.error);
        }
    }

    @Test
    public void testInitialization_whenEachNodeExecutesPostJoinOperations() {
        int nodeCount = 4;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        Config config = new Config();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setImplementation(new DistributedObjectTest.TestInitializingObjectService()).setEnabled(true).setName(DistributedObjectTest.TestInitializingObjectService.NAME));
        String serviceName = DistributedObjectTest.TestInitializingObjectService.NAME;
        String objectName = "test-object";
        HazelcastInstance[] instances = new HazelcastInstance[nodeCount];
        for (int i = 0; i < (instances.length); i++) {
            instances[i] = factory.newHazelcastInstance(config);
            instances[i].getDistributedObject(serviceName, objectName);
        }
        for (int i = 0; i < nodeCount; i++) {
            NodeEngine nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instances[i]);
            OperationService operationService = nodeEngine.getOperationService();
            ProxyServiceImpl proxyService = ((ProxyServiceImpl) (nodeEngine.getProxyService()));
            Operation postJoinOperation = proxyService.getPostJoinOperation();
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    continue;
                }
                Node node2 = HazelcastTestSupport.getNode(instances[j]);
                operationService.send(postJoinOperation, node2.address);
            }
        }
        for (HazelcastInstance instance : instances) {
            DistributedObjectTest.TestInitializingObject obj = instance.getDistributedObject(serviceName, objectName);
            Assert.assertTrue(obj.init.get());
            Assert.assertFalse(obj.error);
        }
    }

    private static class TestInitializingObjectService implements RemoteService {
        static final String NAME = "TestInitializingObjectService";

        @Override
        public DistributedObject createDistributedObject(final String objectName) {
            return new DistributedObjectTest.TestInitializingObject(objectName);
        }

        @Override
        public void destroyDistributedObject(final String objectName) {
        }
    }

    private static class TestInitializingObject implements DistributedObject , InitializingObject {
        private final AtomicBoolean init = new AtomicBoolean(false);

        private final String name;

        private volatile boolean error;

        TestInitializingObject(final String name) {
            this.name = name;
        }

        @Override
        public void initialize() {
            if (!(init.compareAndSet(false, true))) {
                error = true;
                throw new IllegalStateException("InitializingObject must be initialized only once!");
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getServiceName() {
            return DistributedObjectTest.TestInitializingObjectService.NAME;
        }

        @Override
        public String getPartitionKey() {
            return getName();
        }

        @Override
        public void destroy() {
        }
    }

    @Test(expected = HazelcastException.class)
    public void testFailingInitialization() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        Config config = new Config();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setImplementation(new DistributedObjectTest.FailingInitializingObjectService()).setEnabled(true).setName(DistributedObjectTest.FailingInitializingObjectService.NAME));
        String serviceName = DistributedObjectTest.FailingInitializingObjectService.NAME;
        String objectName = "test-object";
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        hz.getDistributedObject(serviceName, objectName);
    }

    @Test
    public void testFailingInitialization_whenGetProxyCalledByMultipleThreads() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        Config config = new Config();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setImplementation(new DistributedObjectTest.FailingInitializingObjectService()).setEnabled(true).setName(DistributedObjectTest.FailingInitializingObjectService.NAME));
        final String serviceName = DistributedObjectTest.FailingInitializingObjectService.NAME;
        final String objectName = "test-object";
        final HazelcastInstance hz = factory.newHazelcastInstance(config);
        int threads = 3;
        final CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread() {
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        try {
                            hz.getDistributedObject(serviceName, objectName);
                            Assert.fail("Proxy creation should fail!");
                        } catch (HazelcastException expected) {
                            HazelcastTestSupport.ignore(expected);
                        }
                        LockSupport.parkNanos(1);
                    }
                    latch.countDown();
                }
            }.start();
        }
        HazelcastTestSupport.assertOpenEventually(latch, 30);
    }

    private static class FailingInitializingObjectService implements RemoteService {
        static final String NAME = "FailingInitializingObjectService";

        @Override
        public DistributedObject createDistributedObject(String objectName) {
            throw new HazelcastException("Object creation is not allowed!");
        }

        @Override
        public void destroyDistributedObject(final String objectName) {
        }
    }
}
