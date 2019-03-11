/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.net;


import Buffers.BufferType.UNTRACKED;
import java.nio.ByteBuffer;
import org.apache.geode.distributed.internal.DMStats;
import org.junit.Test;
import org.mockito.Mockito;


public class BuffersTest {
    @Test
    public void expandBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.clear();
        for (int i = 0; i < 256; i++) {
            byte b = ((byte) (i & 255));
            buffer.put(b);
        }
        createAndVerifyNewWriteBuffer(buffer, false);
        createAndVerifyNewWriteBuffer(buffer, true);
        createAndVerifyNewReadBuffer(buffer, false);
        createAndVerifyNewReadBuffer(buffer, true);
    }

    // the fixed numbers in this test came from a distributed unit test failure
    @Test
    public void bufferPositionAndLimitForReadAreCorrectAfterExpansion() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(33842);
        buffer.position(7);
        buffer.limit(16384);
        ByteBuffer newBuffer = Buffers.expandReadBufferIfNeeded(UNTRACKED, buffer, 40899, Mockito.mock(DMStats.class));
        assertThat(newBuffer.capacity()).isGreaterThanOrEqualTo(40899);
        // buffer should be ready to read the same amount of data
        assertThat(newBuffer.position()).isEqualTo(0);
        assertThat(newBuffer.limit()).isEqualTo((16384 - 7));
    }

    @Test
    public void bufferPositionAndLimitForWriteAreCorrectAfterExpansion() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(33842);
        buffer.position(16384);
        buffer.limit(buffer.capacity());
        ByteBuffer newBuffer = Buffers.expandWriteBufferIfNeeded(UNTRACKED, buffer, 40899, Mockito.mock(DMStats.class));
        assertThat(newBuffer.capacity()).isGreaterThanOrEqualTo(40899);
        // buffer should have the same amount of data as the old one
        assertThat(newBuffer.position()).isEqualTo(16384);
        assertThat(newBuffer.limit()).isEqualTo(newBuffer.capacity());
    }
}

