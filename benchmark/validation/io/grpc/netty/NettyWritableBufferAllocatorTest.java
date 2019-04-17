/**
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.netty;


import io.grpc.internal.WritableBuffer;
import io.grpc.internal.WritableBufferAllocatorTestBase;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link NettyWritableBufferAllocator}.
 */
@RunWith(JUnit4.class)
public class NettyWritableBufferAllocatorTest extends WritableBufferAllocatorTestBase {
    private final NettyWritableBufferAllocator allocator = new NettyWritableBufferAllocator(ByteBufAllocator.DEFAULT);

    @Test
    public void testCapacityHasMinimum() {
        WritableBuffer buffer = allocator().allocate(100);
        Assert.assertEquals(0, buffer.readableBytes());
        Assert.assertEquals(4096, buffer.writableBytes());
    }

    @Test
    public void testCapacityIsExactAboveMinimum() {
        WritableBuffer buffer = allocator().allocate(9000);
        Assert.assertEquals(0, buffer.readableBytes());
        Assert.assertEquals(9000, buffer.writableBytes());
    }

    @Test
    public void testCapacityIsCappedAtMaximum() {
        // Current max is 1MB
        WritableBuffer buffer = allocator().allocate((1024 * 1025));
        Assert.assertEquals(0, buffer.readableBytes());
        Assert.assertEquals((1024 * 1024), buffer.writableBytes());
    }
}
