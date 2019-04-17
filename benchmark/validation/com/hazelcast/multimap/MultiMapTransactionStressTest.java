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
package com.hazelcast.multimap;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.map.impl.tx.MapTransactionStressTest;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import java.util.Collection;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.map.impl.tx.MapTransactionStressTest.ProducerThread.VALUE;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class MultiMapTransactionStressTest extends HazelcastTestSupport {
    private String name;

    private HazelcastInstance hz;

    private Thread producerThread;

    @Test
    public void testTransactionAtomicity_whenMultiMapGetIsUsed_withTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                TransactionContext tx = hz.newTransactionContext();
                try {
                    tx.beginTransaction();
                    TransactionalMultiMap<Object, Object> multiMap = tx.getMultiMap(name);
                    Collection<Object> values = multiMap.get(id);
                    Assert.assertFalse(values.isEmpty());
                    multiMap.remove(id);
                    tx.commitTransaction();
                } catch (TransactionException e) {
                    tx.rollbackTransaction();
                    e.printStackTrace();
                }
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }

    @Test
    public void testTransactionAtomicity_whenMultiMapGetIsUsed_withoutTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                MultiMap<Object, Object> multiMap = hz.getMultiMap(name);
                Collection<Object> values = multiMap.get(id);
                Assert.assertFalse(values.isEmpty());
                multiMap.remove(id);
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }

    @Test
    public void testTransactionAtomicity_whenMultiMapContainsKeyIsUsed_withoutTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                MultiMap<Object, Object> multiMap = hz.getMultiMap(name);
                Assert.assertTrue(multiMap.containsKey(id));
                multiMap.remove(id);
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }

    @Test
    public void testTransactionAtomicity_whenMultiMapContainsEntryIsUsed_withoutTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                MultiMap<Object, Object> multiMap = hz.getMultiMap(name);
                Assert.assertTrue(multiMap.containsEntry(id, VALUE));
                multiMap.remove(id);
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }

    @Test
    public void testTransactionAtomicity_whenMultiMapValueCountIsUsed_withoutTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                MultiMap<Object, Object> multiMap = hz.getMultiMap(name);
                Assert.assertEquals(1, multiMap.valueCount(id));
                multiMap.remove(id);
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }

    @Test
    public void testTransactionAtomicity_whenMultiMapValueCountIsUsed_withTransaction() {
        IQueue<String> q = hz.getQueue(name);
        for (int i = 0; i < 1000; i++) {
            String id = q.poll();
            if (id != null) {
                TransactionContext tx = hz.newTransactionContext();
                try {
                    tx.beginTransaction();
                    TransactionalMultiMap<Object, Object> multiMap = tx.getMultiMap(name);
                    Assert.assertEquals(1, multiMap.valueCount(id));
                    multiMap.remove(id);
                    tx.commitTransaction();
                } catch (TransactionException e) {
                    tx.rollbackTransaction();
                    e.printStackTrace();
                }
            } else {
                LockSupport.parkNanos(100);
            }
        }
    }
}
