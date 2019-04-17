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
package com.hazelcast.map.impl.eviction;


import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapEvictionPolicyTest extends HazelcastTestSupport {
    private final String mapName = "default";

    @Test
    public void testMapEvictionPolicy() throws Exception {
        int sampleCount = Evictor.SAMPLE_COUNT;
        Config config = getConfig();
        config.setProperty(GroupProperty.PARTITION_COUNT.getName(), "1");
        config.getMapConfig(mapName).setMapEvictionPolicy(new MapEvictionPolicyTest.OddEvictor()).getMaxSizeConfig().setMaxSizePolicy(MaxSizePolicy.PER_PARTITION).setSize(sampleCount);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        final CountDownLatch eventLatch = new CountDownLatch(1);
        final Queue<Integer> evictedKeys = new ConcurrentLinkedQueue<Integer>();
        map.addEntryListener(new com.hazelcast.map.listener.EntryEvictedListener<Integer, Integer>() {
            @Override
            public void entryEvicted(EntryEvent<Integer, Integer> event) {
                evictedKeys.add(event.getKey());
                eventLatch.countDown();
            }
        }, false);
        for (int i = 0; i < (sampleCount + 1); i++) {
            map.put(i, i);
        }
        HazelcastTestSupport.assertOpenEventually("No eviction occurred", eventLatch);
        for (Integer key : evictedKeys) {
            TestCase.assertTrue(String.format("Evicted key should be an odd number, but found %d", key), ((key % 2) != 0));
        }
    }

    private static class OddEvictor extends MapEvictionPolicy {
        @Override
        public int compare(EntryView o1, EntryView o2) {
            Assert.assertNotNull(o1);
            Assert.assertNotNull(o2);
            assertFalse(o1.equals(o2));
            TestCase.assertTrue(((o1.hashCode()) != 0));
            TestCase.assertTrue(((o2.hashCode()) != 0));
            Assert.assertNotNull(o1.toString());
            Assert.assertNotNull(o2.toString());
            Integer key = ((Integer) (o1.getKey()));
            if ((key % 2) != 0) {
                return -1;
            }
            return 1;
        }
    }
}
