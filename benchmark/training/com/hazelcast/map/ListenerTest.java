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
package com.hazelcast.map;


import InMemoryFormat.OBJECT;
import MapService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.impl.event.MapPartitionEventData;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ListenerTest extends HazelcastTestSupport {
    private final AtomicInteger globalCount = new AtomicInteger();

    private final AtomicInteger localCount = new AtomicInteger();

    private final AtomicInteger valueCount = new AtomicInteger();

    @Test
    public void testConfigListenerRegistration() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        String name = HazelcastTestSupport.randomString();
        Config config = getConfig();
        MapConfig mapConfig = config.getMapConfig(name);
        EntryListenerConfig entryListenerConfig = new EntryListenerConfig();
        entryListenerConfig.setImplementation(new EntryAdapter() {
            public void entryAdded(EntryEvent event) {
                latch.countDown();
            }
        });
        mapConfig.addEntryListenerConfig(entryListenerConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(name);
        map.put(1, 1);
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void globalListenerTest() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        map1.addEntryListener(createEntryListener(false), false);
        map1.addEntryListener(createEntryListener(false), true);
        map2.addEntryListener(createEntryListener(false), true);
        int k = 3;
        ListenerTest.putDummyData(map1, k);
        checkCountWithExpected((k * 3), 0, (k * 2));
    }

    @Test
    public void testEntryEventGetMemberNotNull() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        final CountDownLatch latch = new CountDownLatch(1);
        map1.addEntryListener(new EntryAdapter<Object, Object>() {
            @Override
            public void entryAdded(EntryEvent<Object, Object> event) {
                Assert.assertNotNull(event.getMember());
                latch.countDown();
            }
        }, false);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        String value = HazelcastTestSupport.randomString();
        map2.put(key, value);
        instance2.getLifecycleService().shutdown();
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void globalListenerRemoveTest() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        String id1 = map1.addEntryListener(createEntryListener(false), false);
        String id2 = map1.addEntryListener(createEntryListener(false), true);
        String id3 = map2.addEntryListener(createEntryListener(false), true);
        int k = 3;
        map1.removeEntryListener(id1);
        map1.removeEntryListener(id2);
        map1.removeEntryListener(id3);
        ListenerTest.putDummyData(map2, k);
        checkCountWithExpected(0, 0, 0);
    }

    @Test
    public void localListenerTest() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        map1.addLocalEntryListener(createEntryListener(true));
        map2.addLocalEntryListener(createEntryListener(true));
        int k = 4;
        ListenerTest.putDummyData(map1, k);
        checkCountWithExpected(0, k, k);
    }

    /**
     * Test for issue 584 and 756
     */
    @Test
    public void globalAndLocalListenerTest() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        map1.addLocalEntryListener(createEntryListener(true));
        map2.addLocalEntryListener(createEntryListener(true));
        map1.addEntryListener(createEntryListener(false), false);
        map2.addEntryListener(createEntryListener(false), false);
        map2.addEntryListener(createEntryListener(false), true);
        int k = 1;
        ListenerTest.putDummyData(map2, k);
        checkCountWithExpected((k * 3), k, (k * 2));
    }

    /**
     * Test for issue 584 and 756
     */
    @Test
    public void globalAndLocalListenerTest2() {
        Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map1 = instance1.getMap(name);
        IMap<String, String> map2 = instance2.getMap(name);
        // changed listener order
        map1.addEntryListener(createEntryListener(false), false);
        map1.addLocalEntryListener(createEntryListener(true));
        map2.addEntryListener(createEntryListener(false), true);
        map2.addLocalEntryListener(createEntryListener(true));
        map2.addEntryListener(createEntryListener(false), false);
        int k = 3;
        ListenerTest.putDummyData(map1, k);
        checkCountWithExpected((k * 3), k, (k * 2));
    }

    /**
     * Test that replace(key, oldValue, newValue) generates entryUpdated events, not entryAdded.
     */
    @Test
    public void replaceFiresUpdatedEvent() {
        final AtomicInteger entryUpdatedEventCount = new AtomicInteger(0);
        HazelcastInstance node = createHazelcastInstance(getConfig());
        IMap<Integer, Integer> map = node.getMap(HazelcastTestSupport.randomMapName());
        map.put(1, 1);
        map.addEntryListener(new EntryAdapter<Integer, Integer>() {
            @Override
            public void entryUpdated(EntryEvent<Integer, Integer> event) {
                entryUpdatedEventCount.incrementAndGet();
            }
        }, true);
        map.replace(1, 1, 2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, entryUpdatedEventCount.get());
            }
        });
    }

    /**
     * test for issue 589
     */
    @Test
    public void setFiresAlwaysAddEvent() throws InterruptedException {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Object, Object> map = instance.getMap(HazelcastTestSupport.randomString());
        final CountDownLatch updateLatch = new CountDownLatch(1);
        final CountDownLatch addLatch = new CountDownLatch(1);
        map.addEntryListener(new EntryAdapter<Object, Object>() {
            @Override
            public void entryAdded(EntryEvent<Object, Object> event) {
                addLatch.countDown();
            }

            @Override
            public void entryUpdated(EntryEvent<Object, Object> event) {
                updateLatch.countDown();
            }
        }, false);
        map.set(1, 1);
        map.set(1, 2);
        Assert.assertTrue(addLatch.await(5, TimeUnit.SECONDS));
        Assert.assertTrue(updateLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testLocalEntryListener_singleInstance_with_MatchingPredicate() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        boolean includeValue = false;
        map.addLocalEntryListener(createEntryListener(false), matchingPredicate(), includeValue);
        int count = 1000;
        for (int i = 0; i < count; i++) {
            map.put(("key" + i), ("value" + i));
        }
        checkCountWithExpected(count, 0, 0);
    }

    @Test
    public void testLocalEntryListener_singleInstance_with_NonMatchingPredicate() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        boolean includeValue = false;
        map.addLocalEntryListener(createEntryListener(false), nonMatchingPredicate(), includeValue);
        int count = 1000;
        for (int i = 0; i < count; i++) {
            map.put(("key" + i), ("value" + i));
        }
        checkCountWithExpected(0, 0, 0);
    }

    @Test
    public void testLocalEntryListener_multipleInstance_with_MatchingPredicate() {
        int instanceCount = 3;
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(instanceCount);
        HazelcastInstance instance = instanceFactory.newInstances(getConfig())[0];
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        boolean includeValue = false;
        map.addLocalEntryListener(createEntryListener(false), matchingPredicate(), includeValue);
        int count = 1000;
        for (int i = 0; i < count; i++) {
            map.put(("key" + i), ("value" + i));
        }
        final int eventPerPartitionMin = (count / instanceCount) - (count / 10);
        final int eventPerPartitionMax = (count / instanceCount) + (count / 10);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue((((globalCount.get()) > eventPerPartitionMin) && ((globalCount.get()) < eventPerPartitionMax)));
            }
        });
    }

    @Test
    public void testLocalEntryListener_multipleInstance_with_MatchingPredicate_and_Key() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        boolean includeValue = false;
        map.addLocalEntryListener(createEntryListener(false), matchingPredicate(), "key500", includeValue);
        int count = 1000;
        for (int i = 0; i < count; i++) {
            map.put(("key" + i), ("value" + i));
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((globalCount.get()) == 1));
            }
        });
    }

    @Test
    public void testEntryListenerEvent_withMapReplaceFail() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        final IMap<Integer, Object> map = instance.getMap(HazelcastTestSupport.randomString());
        final ListenerTest.CounterEntryListener listener = new ListenerTest.CounterEntryListener();
        map.addEntryListener(listener, true);
        final int putTotal = 1000;
        final int oldVal = 1;
        for (int i = 0; i < putTotal; i++) {
            map.put(i, oldVal);
        }
        final int replaceTotal = 1000;
        final int newVal = 2;
        for (int i = 0; i < replaceTotal; i++) {
            map.replace(i, "WrongValue", newVal);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < replaceTotal; i++) {
                    Assert.assertEquals(oldVal, map.get(i));
                }
                Assert.assertEquals(putTotal, listener.addCount.get());
                Assert.assertEquals(0, listener.updateCount.get());
            }
        });
    }

    /**
     * test for issue 3198
     */
    @Test
    public void testEntryListenerEvent_getValueWhenEntryRemoved() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        final AtomicReference<String> valueRef = new AtomicReference<String>();
        final AtomicReference<String> oldValueRef = new AtomicReference<String>();
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new EntryAdapter<String, String>() {
            public void entryRemoved(EntryEvent<String, String> event) {
                valueRef.set(event.getValue());
                oldValueRef.set(event.getOldValue());
                latch.countDown();
            }
        }, true);
        map.put("key", "value");
        map.remove("key");
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertNull(valueRef.get());
        Assert.assertEquals("value", oldValueRef.get());
    }

    @Test
    public void testEntryListenerEvent_getValueWhenEntryEvicted() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        final Object[] value = new Object[1];
        final Object[] oldValue = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new EntryAdapter<String, String>() {
            public void entryEvicted(EntryEvent<String, String> event) {
                value[0] = event.getValue();
                oldValue[0] = event.getOldValue();
                latch.countDown();
            }
        }, true);
        map.put("key", "value", 1, TimeUnit.SECONDS);
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertNull(value[0]);
        Assert.assertEquals("value", oldValue[0]);
    }

    @Test
    public void testEntryListenerEvent_withMapReplaceSuccess() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        final IMap<Integer, Object> map = instance.getMap(HazelcastTestSupport.randomString());
        final ListenerTest.CounterEntryListener listener = new ListenerTest.CounterEntryListener();
        map.addEntryListener(listener, true);
        final int putTotal = 1000;
        final int oldVal = 1;
        for (int i = 0; i < putTotal; i++) {
            map.put(i, oldVal);
        }
        final int replaceTotal = 1000;
        final int newVal = 2;
        for (int i = 0; i < replaceTotal; i++) {
            map.replace(i, oldVal, newVal);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < replaceTotal; i++) {
                    Assert.assertEquals(newVal, map.get(i));
                }
                Assert.assertEquals(putTotal, listener.addCount.get());
                Assert.assertEquals(replaceTotal, listener.updateCount.get());
            }
        });
    }

    /**
     * test for issue 4037
     */
    @Test
    public void testEntryEvent_includesOldValue_afterRemoveIfSameOperation() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        final String key = "key";
        final String value = "value";
        final ConcurrentMap<String, String> resultHolder = new ConcurrentHashMap<String, String>(1);
        map.addEntryListener(new EntryAdapter<String, String>() {
            public void entryRemoved(EntryEvent<String, String> event) {
                final String oldValue = event.getOldValue();
                resultHolder.put(key, oldValue);
                latch.countDown();
            }
        }, true);
        map.put(key, value);
        map.remove(key, value);
        HazelcastTestSupport.assertOpenEventually(latch);
        final String oldValueFromEntryEvent = resultHolder.get(key);
        Assert.assertEquals(value, oldValueFromEntryEvent);
    }

    @Test
    public void testMapPartitionLostListener_registeredViaImplementationInConfigObject() {
        final String name = HazelcastTestSupport.randomString();
        Config config = getConfig();
        MapConfig mapConfig = config.getMapConfig(name);
        MapPartitionLostListener listener = Mockito.mock(MapPartitionLostListener.class);
        mapConfig.addMapPartitionLostListenerConfig(new com.hazelcast.config.MapPartitionLostListenerConfig(listener));
        mapConfig.setBackupCount(0);
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.getMap(name);
        final EventService eventService = HazelcastTestSupport.getNode(instance).getNodeEngine().getEventService();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                final Collection<EventRegistration> registrations = eventService.getRegistrations(SERVICE_NAME, name);
                Assert.assertFalse(registrations.isEmpty());
            }
        });
    }

    @Test
    public void testPutAll_whenExistsEntryListenerWithIncludeValueSetToTrue_thenFireEventWithValue() throws InterruptedException {
        int key = 1;
        String initialValue = "foo";
        String newValue = "bar";
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, String> map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.put(key, initialValue);
        ListenerTest.UpdateListenerRecordingOldValue<Integer, String> listener = new ListenerTest.UpdateListenerRecordingOldValue<Integer, String>();
        map.addEntryListener(listener, true);
        Map<Integer, String> newMap = createMapWithEntry(key, newValue);
        map.putAll(newMap);
        String oldValue = listener.waitForOldValue();
        Assert.assertEquals(initialValue, oldValue);
    }

    @Test
    public void hazelcastAwareEntryListener_whenConfiguredViaClassName_thenInjectHazelcastInstance() {
        EntryListenerConfig listenerConfig = new EntryListenerConfig("com.hazelcast.map.ListenerTest$PingPongListener", false, true);
        hazelcastAwareEntryListener_injectHazelcastInstance(listenerConfig);
    }

    @Test
    public void hazelcastAwareEntryListener_whenConfiguredByProvidingInstance_thenInjectHazelcastInstance() {
        EntryListenerConfig listenerConfig = new EntryListenerConfig(new ListenerTest.PingPongListener(), false, true);
        hazelcastAwareEntryListener_injectHazelcastInstance(listenerConfig);
    }

    @Test
    public void test_ListenerShouldNotCauseDeserialization_withIncludeValueFalse() {
        String name = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.randomString();
        Config config = getConfig();
        config.getMapConfig(name).setInMemoryFormat(OBJECT);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(name);
        ListenerTest.EntryAddedLatch latch = new ListenerTest.EntryAddedLatch(1);
        map.addEntryListener(latch, false);
        map.executeOnKey(key, new AbstractEntryProcessor<Object, Object>() {
            @Override
            public Object process(Map.Entry<Object, Object> entry) {
                entry.setValue(new ListenerTest.SerializeCheckerObject());
                return null;
            }
        });
        HazelcastTestSupport.assertOpenEventually(latch, 10);
        ListenerTest.SerializeCheckerObject.assertNotSerialized();
    }

    private static class EntryAddedLatch extends CountDownLatch implements EntryAddedListener {
        EntryAddedLatch(int count) {
            super(count);
        }

        @Override
        public void entryAdded(EntryEvent event) {
            countDown();
        }
    }

    private static class SerializeCheckerObject implements DataSerializable {
        static volatile boolean serialized = false;

        static volatile boolean deserialized = false;

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            ListenerTest.SerializeCheckerObject.serialized = true;
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            ListenerTest.SerializeCheckerObject.deserialized = true;
        }

        static void assertNotSerialized() {
            Assert.assertFalse(ListenerTest.SerializeCheckerObject.serialized);
            Assert.assertFalse(ListenerTest.SerializeCheckerObject.deserialized);
        }
    }

    @Test
    public void test_mapPartitionEventData_toString() {
        Assert.assertNotNull(new MapPartitionEventData().toString());
    }

    @Test
    public void updates_with_putTransient_triggers_entryUpdatedListener() {
        HazelcastInstance hz = createHazelcastInstance(getConfig());
        IMap<String, String> map = hz.getMap("updates_with_putTransient_triggers_entryUpdatedListener");
        final CountDownLatch updateEventCounterLatch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryUpdatedListener<String, String>() {
            @Override
            public void entryUpdated(EntryEvent<String, String> event) {
                updateEventCounterLatch.countDown();
            }
        }, true);
        map.putTransient("hello", "world", 0, TimeUnit.SECONDS);
        map.putTransient("hello", "another world", 0, TimeUnit.SECONDS);
        HazelcastTestSupport.assertOpenEventually(updateEventCounterLatch);
    }

    private class UpdateListenerRecordingOldValue<K, V> implements com.hazelcast.map.listener.EntryUpdatedListener<K, V> {
        private volatile V oldValue;

        private final CountDownLatch latch = new CountDownLatch(1);

        V waitForOldValue() throws InterruptedException {
            latch.await();
            return oldValue;
        }

        @Override
        public void entryUpdated(EntryEvent<K, V> event) {
            oldValue = event.getOldValue();
            latch.countDown();
        }
    }

    public static class PingPongListener implements EntryListener<Integer, String> , HazelcastInstanceAware {
        private HazelcastInstance instance;

        @Override
        public void setHazelcastInstance(HazelcastInstance instance) {
            this.instance = instance;
        }

        @Override
        public void entryAdded(EntryEvent<Integer, String> event) {
            String outputMapName = event.getValue();
            IMap<Integer, String> outputMap = instance.getMap(outputMapName);
            outputMap.putAsync(0, "pong");
        }

        @Override
        public void entryEvicted(EntryEvent<Integer, String> event) {
        }

        @Override
        public void entryRemoved(EntryEvent<Integer, String> event) {
        }

        @Override
        public void entryUpdated(EntryEvent<Integer, String> event) {
        }

        @Override
        public void mapCleared(MapEvent event) {
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }
    }

    public class CounterEntryListener implements EntryListener<Object, Object> {
        final AtomicLong addCount = new AtomicLong();

        final AtomicLong removeCount = new AtomicLong();

        final AtomicLong updateCount = new AtomicLong();

        final AtomicLong evictCount = new AtomicLong();

        public CounterEntryListener() {
        }

        @Override
        public void entryAdded(EntryEvent<Object, Object> objectObjectEntryEvent) {
            addCount.incrementAndGet();
        }

        @Override
        public void entryRemoved(EntryEvent<Object, Object> objectObjectEntryEvent) {
            removeCount.incrementAndGet();
        }

        @Override
        public void entryUpdated(EntryEvent<Object, Object> objectObjectEntryEvent) {
            updateCount.incrementAndGet();
        }

        @Override
        public void entryEvicted(EntryEvent<Object, Object> objectObjectEntryEvent) {
            evictCount.incrementAndGet();
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }

        @Override
        public void mapCleared(MapEvent event) {
        }

        @Override
        public String toString() {
            return (((((((("EntryCounter{" + "addCount=") + (addCount)) + ", removeCount=") + (removeCount)) + ", updateCount=") + (updateCount)) + ", evictCount=") + (evictCount)) + '}';
        }
    }
}
