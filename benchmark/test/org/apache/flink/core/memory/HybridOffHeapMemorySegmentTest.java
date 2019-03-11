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
package org.apache.flink.core.memory;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for the {@link HybridMemorySegment} in off-heap mode.
 */
@RunWith(Parameterized.class)
public class HybridOffHeapMemorySegmentTest extends MemorySegmentTestBase {
    public HybridOffHeapMemorySegmentTest(int pageSize) {
        super(pageSize);
    }

    @Test
    public void testHybridHeapSegmentSpecifics() {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(411);
        HybridMemorySegment seg = new HybridMemorySegment(buffer);
        Assert.assertFalse(seg.isFreed());
        Assert.assertTrue(seg.isOffHeap());
        Assert.assertEquals(buffer.capacity(), seg.size());
        Assert.assertTrue((buffer == (seg.getOffHeapBuffer())));
        try {
            // noinspection ResultOfMethodCallIgnored
            seg.getArray();
            Assert.fail("should throw an exception");
        } catch (IllegalStateException e) {
            // expected
        }
        ByteBuffer buf1 = seg.wrap(1, 2);
        ByteBuffer buf2 = seg.wrap(3, 4);
        Assert.assertTrue((buf1 != buffer));
        Assert.assertTrue((buf2 != buffer));
        Assert.assertTrue((buf1 != buf2));
        Assert.assertEquals(1, buf1.position());
        Assert.assertEquals(3, buf1.limit());
        Assert.assertEquals(3, buf2.position());
        Assert.assertEquals(7, buf2.limit());
    }
}

