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
package com.hazelcast.internal.partition.impl;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionReplicaManagerTest extends HazelcastTestSupport {
    private static final int PARTITION_ID = 23;

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance hazelcastInstance;

    private PartitionReplicaManager manager;

    @Test(expected = AssertionError.class)
    public void testTriggerPartitionReplicaSync_whenReplicaIndexNegative_thenThrowException() {
        Set<ServiceNamespace> namespaces = Collections.<ServiceNamespace>singleton(NonFragmentedServiceNamespace.INSTANCE);
        manager.triggerPartitionReplicaSync(PartitionReplicaManagerTest.PARTITION_ID, namespaces, (-1));
    }

    @Test(expected = AssertionError.class)
    public void testTriggerPartitionReplicaSync_whenReplicaIndexTooLarge_thenThrowException() {
        Set<ServiceNamespace> namespaces = Collections.<ServiceNamespace>singleton(NonFragmentedServiceNamespace.INSTANCE);
        manager.triggerPartitionReplicaSync(PartitionReplicaManagerTest.PARTITION_ID, namespaces, ((InternalPartition.MAX_REPLICA_COUNT) + 1));
    }

    @Test
    public void testCheckSyncPartitionTarget_whenPartitionOwnerIsNull_thenReturnFalse() {
        Assert.assertNull(manager.checkAndGetPrimaryReplicaOwner(PartitionReplicaManagerTest.PARTITION_ID, 0));
    }

    @Test
    public void testCheckSyncPartitionTarget_whenNodeIsPartitionOwner_thenReturnFalse() {
        HazelcastTestSupport.warmUpPartitions(hazelcastInstance);
        Assert.assertNull(manager.checkAndGetPrimaryReplicaOwner(PartitionReplicaManagerTest.PARTITION_ID, 0));
    }
}
