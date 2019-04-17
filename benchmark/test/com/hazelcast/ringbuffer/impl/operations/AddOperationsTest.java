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
package com.hazelcast.ringbuffer.impl.operations;


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AddOperationsTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine;

    private SerializationService serializationService;

    private Ringbuffer<Object> ringbuffer;

    private RingbufferService ringbufferService;

    @Test
    public void whenFailOverflowPolicy_andNoRemainingCapacity_thenNoBackup() throws Exception {
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add("item");
        }
        Data item = serializationService.toData("newItem");
        AddOperation addOperation = getAddOperation(item, OverflowPolicy.FAIL);
        addOperation.run();
        Assert.assertFalse(addOperation.shouldBackup());
        Assert.assertFalse(addOperation.shouldNotify());
        Assert.assertEquals(new Long((-1L)), addOperation.getResponse());
    }

    @Test
    public void whenFailOverflowPolicy_andRemainingCapacity_thenBackup() throws Exception {
        for (int k = 0; k < ((ringbuffer.capacity()) - 1); k++) {
            ringbuffer.add("item");
        }
        Data item = serializationService.toData("newItem");
        AddOperation addOperation = getAddOperation(item, OverflowPolicy.FAIL);
        addOperation.run();
        Assert.assertTrue(addOperation.shouldBackup());
        Assert.assertTrue(addOperation.shouldNotify());
        Assert.assertEquals(new Long(ringbuffer.tailSequence()), addOperation.getResponse());
    }

    @Test
    public void whenOverwritePolicy_andNoRemainingCapacity_thenBackup() throws Exception {
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add("item");
        }
        Data item = serializationService.toData("newItem");
        AddOperation addOperation = getAddOperation(item, OverflowPolicy.OVERWRITE);
        addOperation.run();
        Assert.assertTrue(addOperation.shouldBackup());
        Assert.assertTrue(addOperation.shouldNotify());
        Assert.assertEquals(new Long(ringbuffer.tailSequence()), addOperation.getResponse());
    }

    @Test
    public void whenOverwritePolicy_andRemainingCapacity_thenBackup() throws Exception {
        for (int k = 0; k < ((ringbuffer.capacity()) - 1); k++) {
            ringbuffer.add("item");
        }
        Data item = serializationService.toData("newItem");
        AddOperation addOperation = getAddOperation(item, OverflowPolicy.OVERWRITE);
        addOperation.run();
        Assert.assertTrue(addOperation.shouldNotify());
        Assert.assertTrue(addOperation.shouldBackup());
        Assert.assertEquals(new Long(ringbuffer.tailSequence()), addOperation.getResponse());
    }
}
