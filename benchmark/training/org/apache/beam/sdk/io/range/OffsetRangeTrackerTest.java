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
package org.apache.beam.sdk.io.range;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link OffsetRangeTracker}.
 */
@RunWith(JUnit4.class)
public class OffsetRangeTrackerTest {
    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @Test
    public void testUpdateStartOffset() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertEquals(100, tracker.getStartPosition().longValue());
        // Update start offset to first record returned
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 150));
        Assert.assertEquals(150, tracker.getStartPosition().longValue());
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 180));
        Assert.assertEquals(150, tracker.getStartPosition().longValue());
    }

    @Test
    public void testTryReturnRecordSimpleSparse() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 110));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 140));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 183));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 210));
    }

    @Test
    public void testTryReturnRecordSimpleDense() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(3, 6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 3));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 4));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 5));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 6));
    }

    @Test
    public void testTryReturnRecordContinuesUntilSplitPoint() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(9, 18);
        // Return records with gaps of 2; every 3rd record is a split point.
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 10));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 12));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 14));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 16));
        // Out of range, but not a split point...
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 18));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 20));
        // Out of range AND a split point.
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 22));
    }

    @Test
    public void testSplitAtOffsetFailsIfUnstarted() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertFalse(tracker.trySplitAtPosition(150));
    }

    @Test
    public void testSplitAtOffset() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 110));
        // Example positions we shouldn't split at, when last record is [110, 130]:
        Assert.assertFalse(tracker.trySplitAtPosition(109));
        Assert.assertFalse(tracker.trySplitAtPosition(110));
        Assert.assertFalse(tracker.trySplitAtPosition(200));
        Assert.assertFalse(tracker.trySplitAtPosition(210));
        // Example positions we *should* split at:
        Assert.assertTrue(tracker.copy().trySplitAtPosition(111));
        Assert.assertTrue(tracker.copy().trySplitAtPosition(129));
        Assert.assertTrue(tracker.copy().trySplitAtPosition(130));
        Assert.assertTrue(tracker.copy().trySplitAtPosition(131));
        Assert.assertTrue(tracker.copy().trySplitAtPosition(150));
        Assert.assertTrue(tracker.copy().trySplitAtPosition(199));
        // If we split at 170 and then at 150:
        Assert.assertTrue(tracker.trySplitAtPosition(170));
        Assert.assertTrue(tracker.trySplitAtPosition(150));
        // Should be able to return a record starting before the new stop offset.
        // Returning records starting at the same offset is ok.
        Assert.assertTrue(tracker.copy().tryReturnRecordAt(true, 135));
        Assert.assertTrue(tracker.copy().tryReturnRecordAt(true, 135));
        // Should be able to return a record starting right before the new stop offset.
        Assert.assertTrue(tracker.copy().tryReturnRecordAt(true, 149));
        // Should not be able to return a record starting at or after the new stop offset
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 150));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 151));
        // Should accept non-splitpoint records starting after stop offset.
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 152));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, 160));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 171));
    }

    @Test
    public void testGetPositionForFractionDense() throws Exception {
        // Represents positions 3, 4, 5.
        OffsetRangeTracker tracker = new OffsetRangeTracker(3, 6);
        // [3, 3) represents from [0, 1/3) fraction of [3, 6)
        Assert.assertEquals(3, tracker.getPositionForFractionConsumed(0.0));
        Assert.assertEquals(3, tracker.getPositionForFractionConsumed((1.0 / 6)));
        Assert.assertEquals(3, tracker.getPositionForFractionConsumed(0.333));
        // [3, 4) represents from [0, 2/3) fraction of [3, 6)
        Assert.assertEquals(4, tracker.getPositionForFractionConsumed(0.334));
        Assert.assertEquals(4, tracker.getPositionForFractionConsumed(0.666));
        // [3, 5) represents from [0, 1) fraction of [3, 6)
        Assert.assertEquals(5, tracker.getPositionForFractionConsumed(0.667));
        Assert.assertEquals(5, tracker.getPositionForFractionConsumed(0.999));
        // The whole [3, 6) is consumed for fraction 1
        Assert.assertEquals(6, tracker.getPositionForFractionConsumed(1.0));
    }

    @Test
    public void testGetPositionForFractionDenseUpdateStartOffset() throws Exception {
        // Represents positions 3, 4, 5.
        OffsetRangeTracker tracker = new OffsetRangeTracker(3, 6);
        // [3, 3) represents from [0, 1/3) fraction of [3, 6)
        Assert.assertEquals(3, tracker.getPositionForFractionConsumed(0.333));
        // Update start offset to 4
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 4));
        // [4, 4) represents from [0, 1/2) fraction of [4, 6)
        Assert.assertEquals(4, tracker.getPositionForFractionConsumed(0.0));
        Assert.assertEquals(4, tracker.getPositionForFractionConsumed(0.499));
        // [4, 5) represents from [0, 1) fraction of [4, 6)
        Assert.assertEquals(5, tracker.getPositionForFractionConsumed(0.5));
        Assert.assertEquals(5, tracker.getPositionForFractionConsumed(0.999));
        // The whole [4, 6) is consumed for fraction 1
        Assert.assertEquals(6, tracker.getPositionForFractionConsumed(1.0));
    }

    @Test
    public void testGetFractionConsumedDense() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(3, 6);
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 3));
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 4));
        Assert.assertEquals((1.0 / 3), tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 5));
        Assert.assertEquals((2.0 / 3), tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(/* non-split-point */
        tracker.tryReturnRecordAt(false, 6));
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(/* non-split-point */
        tracker.tryReturnRecordAt(false, 7));
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 7));
    }

    @Test
    public void testGetFractionConsumedSparse() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 100));
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 110));
        // Consumed positions through 109 = total 10 positions of 100.
        Assert.assertEquals(0.1, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 150));
        Assert.assertEquals(0.5, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 195));
        Assert.assertEquals(0.95, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 200));
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), 1.0E-6);
    }

    @Test
    public void testGetFractionConsumedUpdateStartOffset() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 150));
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 160));
        Assert.assertEquals(0.2, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 180));
        Assert.assertEquals(0.6, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 195));
        Assert.assertEquals(0.9, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertFalse(tracker.tryReturnRecordAt(true, 200));
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), 1.0E-6);
    }

    @Test
    public void testEverythingWithUnboundedRange() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, Long.MAX_VALUE);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 150));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, 250));
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), 1.0E-6);
        Assert.assertFalse(tracker.trySplitAtPosition(1000));
        try {
            tracker.getPositionForFractionConsumed(0.5);
            Assert.fail("getPositionForFractionConsumed should fail for an unbounded range");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testTryReturnFirstRecordNotSplitPoint() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(false, 120);
    }

    @Test
    public void testTryReturnRecordNonMonotonic() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(100, 200);
        tracker.tryReturnRecordAt(true, 120);
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, 110);
    }
}

