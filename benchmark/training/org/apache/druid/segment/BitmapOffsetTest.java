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
package org.apache.druid.segment;


import org.apache.druid.collections.bitmap.BitmapFactory;
import org.apache.druid.collections.bitmap.ImmutableBitmap;
import org.apache.druid.collections.bitmap.MutableBitmap;
import org.apache.druid.segment.data.Offset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class BitmapOffsetTest {
    private static final int[] TEST_VALS = new int[]{ 1, 2, 4, 291, 27412, 49120, 212312, 2412101 };

    private static final int[] TEST_VALS_FLIP = new int[]{ 2412101, 212312, 49120, 27412, 291, 4, 2, 1 };

    private final BitmapFactory factory;

    private final boolean descending;

    public BitmapOffsetTest(BitmapFactory factory, boolean descending) {
        this.factory = factory;
        this.descending = descending;
    }

    @Test
    public void testSanity() {
        MutableBitmap mutable = factory.makeEmptyMutableBitmap();
        for (int val : BitmapOffsetTest.TEST_VALS) {
            mutable.add(val);
        }
        ImmutableBitmap bitmap = factory.makeImmutableBitmap(mutable);
        final BitmapOffset offset = BitmapOffset.of(bitmap, descending, bitmap.size());
        final int[] expected = (descending) ? BitmapOffsetTest.TEST_VALS_FLIP : BitmapOffsetTest.TEST_VALS;
        int count = 0;
        while (offset.withinBounds()) {
            Assert.assertEquals(expected[count], offset.getOffset());
            int cloneCount = count;
            Offset clonedOffset = offset.clone();
            while (clonedOffset.withinBounds()) {
                Assert.assertEquals(expected[cloneCount], clonedOffset.getOffset());
                ++cloneCount;
                clonedOffset.increment();
            } 
            ++count;
            offset.increment();
        } 
        Assert.assertEquals(count, expected.length);
    }
}

