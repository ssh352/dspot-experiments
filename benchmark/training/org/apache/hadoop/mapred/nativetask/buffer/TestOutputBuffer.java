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
package org.apache.hadoop.mapred.nativetask.buffer;


import BufferType.DIRECT_BUFFER;
import BufferType.HEAP_BUFFER;
import org.junit.Assert;
import org.junit.Test;

import static BufferType.DIRECT_BUFFER;
import static BufferType.HEAP_BUFFER;


public class TestOutputBuffer {
    @Test
    public void testOutputBuffer() {
        final int size = 100;
        final OutputBuffer output1 = new OutputBuffer(DIRECT_BUFFER, size);
        Assert.assertEquals(output1.getType(), DIRECT_BUFFER);
        Assert.assertTrue(((output1.length()) == 0));
        Assert.assertEquals(output1.limit(), size);
        final OutputBuffer output2 = new OutputBuffer(HEAP_BUFFER, size);
        Assert.assertEquals(output2.getType(), HEAP_BUFFER);
        Assert.assertTrue(((output2.length()) == 0));
        Assert.assertEquals(output2.limit(), size);
        final OutputBuffer output3 = new OutputBuffer(new byte[size]);
        Assert.assertEquals(output3.getType(), HEAP_BUFFER);
        Assert.assertTrue(((output3.length()) == 0));
        Assert.assertEquals(output3.limit(), size);
    }
}

