/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.util.AutoCloseableLock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for ReplicasMap class
 */
public class TestReplicaMap {
    private final ReplicaMap map = new ReplicaMap(new AutoCloseableLock());

    private final String bpid = "BP-TEST";

    private final Block block = new Block(1234, 1234, 1234);

    /**
     * Test for ReplicasMap.get(Block) and ReplicasMap.get(long) tests
     */
    @Test
    public void testGet() {
        // Test 1: null argument throws invalid argument exception
        try {
            map.get(bpid, null);
            Assert.fail("Expected exception not thrown");
        } catch (IllegalArgumentException expected) {
        }
        // Test 2: successful lookup based on block
        Assert.assertNotNull(map.get(bpid, block));
        // Test 3: Lookup failure - generation stamp mismatch
        Block b = new Block(block);
        b.setGenerationStamp(0);
        Assert.assertNull(map.get(bpid, b));
        // Test 4: Lookup failure - blockID mismatch
        b.setGenerationStamp(block.getGenerationStamp());
        b.setBlockId(0);
        Assert.assertNull(map.get(bpid, b));
        // Test 5: successful lookup based on block ID
        Assert.assertNotNull(map.get(bpid, block.getBlockId()));
        // Test 6: failed lookup for invalid block ID
        Assert.assertNull(map.get(bpid, 0));
    }

    @Test
    public void testAdd() {
        // Test 1: null argument throws invalid argument exception
        try {
            map.add(bpid, null);
            Assert.fail("Expected exception not thrown");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testRemove() {
        // Test 1: null argument throws invalid argument exception
        try {
            map.remove(bpid, null);
            Assert.fail("Expected exception not thrown");
        } catch (IllegalArgumentException expected) {
        }
        // Test 2: remove failure - generation stamp mismatch
        Block b = new Block(block);
        b.setGenerationStamp(0);
        Assert.assertNull(map.remove(bpid, b));
        // Test 3: remove failure - blockID mismatch
        b.setGenerationStamp(block.getGenerationStamp());
        b.setBlockId(0);
        Assert.assertNull(map.remove(bpid, b));
        // Test 4: remove success
        Assert.assertNotNull(map.remove(bpid, block));
        // Test 5: remove failure - invalid blockID
        Assert.assertNull(map.remove(bpid, 0));
        // Test 6: remove success
        map.add(bpid, new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(block, null, null));
        Assert.assertNotNull(map.remove(bpid, block.getBlockId()));
    }
}

