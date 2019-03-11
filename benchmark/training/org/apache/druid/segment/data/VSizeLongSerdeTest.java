/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.data;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static VSizeLongSerde.SUPPORTED_SIZES;


public class VSizeLongSerdeTest {
    private ByteBuffer buffer;

    private ByteArrayOutputStream outStream;

    private ByteBuffer outBuffer;

    private long[] values0 = new long[]{ 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1 };

    private long[] values1 = new long[]{ 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1 };

    private long[] values2 = new long[]{ 12, 5, 2, 9, 3, 2, 5, 1, 0, 6, 13, 10, 15 };

    private long[] values3 = new long[]{ 1, 1, 1, 1, 1, 11, 11, 11, 11 };

    private long[] values4 = new long[]{ 200, 200, 200, 401, 200, 301, 200, 200, 200, 404, 200, 200, 200, 200 };

    private long[] values5 = new long[]{ 123, 632, 12, 39, 536, 0, 1023, 52, 777, 526, 214, 562, 823, 346 };

    private long[] values6 = new long[]{ 1000000, 1000001, 1000002, 1000003, 1000004, 1000005, 1000006, 1000007, 1000008 };

    @Test
    public void testGetBitsForMax() {
        Assert.assertEquals(1, VSizeLongSerde.getBitsForMax(1));
        Assert.assertEquals(1, VSizeLongSerde.getBitsForMax(2));
        Assert.assertEquals(2, VSizeLongSerde.getBitsForMax(3));
        Assert.assertEquals(4, VSizeLongSerde.getBitsForMax(16));
        Assert.assertEquals(8, VSizeLongSerde.getBitsForMax(200));
        Assert.assertEquals(12, VSizeLongSerde.getBitsForMax(999));
        Assert.assertEquals(24, VSizeLongSerde.getBitsForMax(12345678));
        Assert.assertEquals(32, VSizeLongSerde.getBitsForMax(Integer.MAX_VALUE));
        Assert.assertEquals(64, VSizeLongSerde.getBitsForMax(Long.MAX_VALUE));
    }

    @Test
    public void testSerdeValues() throws IOException {
        for (int i : SUPPORTED_SIZES) {
            testSerde(i, values0);
            if (i >= 1) {
                testSerde(i, values1);
            }
            if (i >= 4) {
                testSerde(i, values2);
                testSerde(i, values3);
            }
            if (i >= 9) {
                testSerde(i, values4);
            }
            if (i >= 10) {
                testSerde(i, values5);
            }
            if (i >= 20) {
                testSerde(i, values6);
            }
        }
    }

    @Test
    public void testSerdeLoop() throws IOException {
        for (int i : SUPPORTED_SIZES) {
            if (i >= 8) {
                testSerdeIncLoop(i, 0, 256);
            }
            if (i >= 16) {
                testSerdeIncLoop(i, 0, 50000);
            }
        }
    }
}

