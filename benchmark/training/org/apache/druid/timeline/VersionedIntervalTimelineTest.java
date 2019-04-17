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
package org.apache.druid.timeline;


import Days.ONE;
import Hours.FIVE;
import Hours.FOUR;
import Hours.THREE;
import Hours.TWO;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.partition.IntegerPartitionChunk;
import org.apache.druid.timeline.partition.PartitionChunk;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class VersionedIntervalTimelineTest {
    VersionedIntervalTimeline<String, Integer> timeline;

    @Test
    public void testApril() {
        assertValues(Arrays.asList(createExpected("2011-04-01/2011-04-02", "3", 5), createExpected("2011-04-02/2011-04-06", "2", 1), createExpected("2011-04-06/2011-04-09", "3", 4)), timeline.lookup(Intervals.of("2011-04-01/2011-04-09")));
    }

    @Test
    public void testApril2() {
        Assert.assertEquals(makeSingle(1), timeline.remove(Intervals.of("2011-04-01/2011-04-09"), "2", makeSingle(1)));
        assertValues(Arrays.asList(createExpected("2011-04-01/2011-04-02", "3", 5), createExpected("2011-04-02/2011-04-03", "1", 2), createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-06/2011-04-09", "3", 4)), timeline.lookup(Intervals.of("2011-04-01/2011-04-09")));
    }

    @Test
    public void testApril3() {
        Assert.assertEquals(makeSingle(1), timeline.remove(Intervals.of("2011-04-01/2011-04-09"), "2", makeSingle(1)));
        Assert.assertEquals(makeSingle(2), timeline.remove(Intervals.of("2011-04-01/2011-04-03"), "1", makeSingle(2)));
        assertValues(Arrays.asList(createExpected("2011-04-01/2011-04-02", "3", 5), createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-06/2011-04-09", "3", 4)), timeline.lookup(Intervals.of("2011-04-01/2011-04-09")));
    }

    @Test
    public void testApril4() {
        Assert.assertEquals(makeSingle(1), timeline.remove(Intervals.of("2011-04-01/2011-04-09"), "2", makeSingle(1)));
        assertValues(Arrays.asList(createExpected("2011-04-01/2011-04-02", "3", 5), createExpected("2011-04-02/2011-04-03", "1", 2), createExpected("2011-04-03/2011-04-05", "1", 3)), timeline.lookup(Intervals.of("2011-04-01/2011-04-05")));
        assertValues(Arrays.asList(createExpected("2011-04-02T18/2011-04-03", "1", 2), createExpected("2011-04-03/2011-04-04T01", "1", 3)), timeline.lookup(Intervals.of("2011-04-02T18/2011-04-04T01")));
    }

    @Test
    public void testMay() {
        assertValues(Collections.singletonList(createExpected("2011-05-01/2011-05-09", "4", 9)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
    }

    @Test
    public void testMay2() {
        Assert.assertNotNull(timeline.remove(Intervals.of("2011-05-01/2011-05-10"), "4", makeSingle(1)));
        assertValues(Arrays.asList(createExpected("2011-05-01/2011-05-03", "2", 7), createExpected("2011-05-03/2011-05-04", "3", 8), createExpected("2011-05-04/2011-05-05", "2", 7)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
    }

    @Test
    public void testMay3() {
        Assert.assertEquals(makeSingle(9), timeline.remove(Intervals.of("2011-05-01/2011-05-10"), "4", makeSingle(9)));
        Assert.assertEquals(makeSingle(7), timeline.remove(Intervals.of("2011-05-01/2011-05-05"), "2", makeSingle(7)));
        assertValues(Arrays.asList(createExpected("2011-05-01/2011-05-02", "1", 6), createExpected("2011-05-03/2011-05-04", "3", 8)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
    }

    @Test
    public void testInsertInWrongOrder() {
        DateTime overallStart = DateTimes.nowUtc().minus(TWO);
        Assert.assertTrue("These timestamps have to be at the end AND include now for this test to work.", overallStart.isAfter(timeline.incompletePartitionsTimeline.lastEntry().getKey().getEnd()));
        final Interval oneHourInterval1 = new Interval(overallStart.plus(THREE), overallStart.plus(FOUR));
        final Interval oneHourInterval2 = new Interval(overallStart.plus(FOUR), overallStart.plus(FIVE));
        add(oneHourInterval1, "1", 1);
        add(oneHourInterval2, "1", 1);
        add(new Interval(overallStart, overallStart.plus(ONE)), "2", 2);
        assertValues(Collections.singletonList(createExpected(oneHourInterval1.toString(), "2", 2)), timeline.lookup(oneHourInterval1));
    }

    @Test
    public void testRemove() {
        for (TimelineObjectHolder<String, Integer> holder : timeline.findOvershadowed()) {
            for (PartitionChunk<Integer> chunk : holder.getObject()) {
                timeline.remove(holder.getInterval(), holder.getVersion(), chunk);
            }
        }
        Assert.assertTrue(timeline.findOvershadowed().isEmpty());
    }

    @Test
    public void testFindEntry() {
        Assert.assertEquals(new org.apache.druid.timeline.partition.ImmutablePartitionHolder<Integer>(new org.apache.druid.timeline.partition.PartitionHolder<Integer>(makeSingle(1))), timeline.findEntry(Intervals.of("2011-10-01/2011-10-02"), "1"));
        Assert.assertEquals(new org.apache.druid.timeline.partition.ImmutablePartitionHolder<Integer>(new org.apache.druid.timeline.partition.PartitionHolder<Integer>(makeSingle(1))), timeline.findEntry(Intervals.of("2011-10-01/2011-10-01T10"), "1"));
        Assert.assertEquals(new org.apache.druid.timeline.partition.ImmutablePartitionHolder<Integer>(new org.apache.druid.timeline.partition.PartitionHolder<Integer>(makeSingle(1))), timeline.findEntry(Intervals.of("2011-10-01T02/2011-10-02"), "1"));
        Assert.assertEquals(new org.apache.druid.timeline.partition.ImmutablePartitionHolder<Integer>(new org.apache.druid.timeline.partition.PartitionHolder<Integer>(makeSingle(1))), timeline.findEntry(Intervals.of("2011-10-01T04/2011-10-01T17"), "1"));
        Assert.assertEquals(null, timeline.findEntry(Intervals.of("2011-10-01T04/2011-10-01T17"), "2"));
        Assert.assertEquals(null, timeline.findEntry(Intervals.of("2011-10-01T04/2011-10-02T17"), "1"));
    }

    @Test
    public void testFindEntryWithOverlap() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-02/2011-01-05", "2", 1);
        Assert.assertEquals(new org.apache.druid.timeline.partition.ImmutablePartitionHolder<Integer>(new org.apache.druid.timeline.partition.PartitionHolder<Integer>(makeSingle(1))), timeline.findEntry(Intervals.of("2011-01-02T02/2011-01-04"), "1"));
    }

    @Test
    public void testPartitioning() {
        assertValues(ImmutableList.of(createExpected("2011-10-01/2011-10-02", "1", 1), createExpected("2011-10-02/2011-10-03", "3", Arrays.asList(IntegerPartitionChunk.make(null, 10, 0, 20), IntegerPartitionChunk.make(10, null, 1, 21))), createExpected("2011-10-03/2011-10-04", "3", 3), createExpected("2011-10-04/2011-10-05", "4", 4), createExpected("2011-10-05/2011-10-06", "5", 5)), timeline.lookup(Intervals.of("2011-10-01/2011-10-06")));
    }

    @Test
    public void testPartialPartitionNotReturned() {
        testRemove();
        add("2011-10-06/2011-10-07", "6", IntegerPartitionChunk.make(null, 10, 0, 60));
        assertValues(ImmutableList.of(createExpected("2011-10-05/2011-10-06", "5", 5)), timeline.lookup(Intervals.of("2011-10-05/2011-10-07")));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
        add("2011-10-06/2011-10-07", "6", IntegerPartitionChunk.make(10, 20, 1, 61));
        assertValues(ImmutableList.of(createExpected("2011-10-05/2011-10-06", "5", 5)), timeline.lookup(Intervals.of("2011-10-05/2011-10-07")));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
        add("2011-10-06/2011-10-07", "6", IntegerPartitionChunk.make(20, null, 2, 62));
        assertValues(ImmutableList.of(createExpected("2011-10-05/2011-10-06", "5", 5), createExpected("2011-10-06/2011-10-07", "6", Arrays.asList(IntegerPartitionChunk.make(null, 10, 0, 60), IntegerPartitionChunk.make(10, 20, 1, 61), IntegerPartitionChunk.make(20, null, 2, 62)))), timeline.lookup(Intervals.of("2011-10-05/2011-10-07")));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
    }

    @Test
    public void testIncompletePartitionDoesNotOvershadow() {
        testRemove();
        add("2011-10-05/2011-10-07", "6", IntegerPartitionChunk.make(null, 10, 0, 60));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
        add("2011-10-05/2011-10-07", "6", IntegerPartitionChunk.make(10, 20, 1, 61));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
        add("2011-10-05/2011-10-07", "6", IntegerPartitionChunk.make(20, null, 2, 62));
        assertValues(ImmutableSet.of(createExpected("2011-10-05/2011-10-06", "5", 5)), timeline.findOvershadowed());
    }

    @Test
    public void testRemovePartitionMakesIncomplete() {
        testIncompletePartitionDoesNotOvershadow();
        final IntegerPartitionChunk<Integer> chunk = IntegerPartitionChunk.make(null, 10, 0, 60);
        Assert.assertEquals(chunk, timeline.remove(Intervals.of("2011-10-05/2011-10-07"), "6", chunk));
        assertValues(ImmutableList.of(createExpected("2011-10-05/2011-10-06", "5", 5)), timeline.lookup(Intervals.of("2011-10-05/2011-10-07")));
        Assert.assertTrue("Expected no overshadowed entries", timeline.findOvershadowed().isEmpty());
    }

    @Test
    public void testInsertAndRemoveSameThingsion() {
        add("2011-05-01/2011-05-10", "5", 10);
        assertValues(Collections.singletonList(createExpected("2011-05-01/2011-05-09", "5", 10)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
        Assert.assertEquals(makeSingle(10), timeline.remove(Intervals.of("2011-05-01/2011-05-10"), "5", makeSingle(10)));
        assertValues(Collections.singletonList(createExpected("2011-05-01/2011-05-09", "4", 9)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
        add("2011-05-01/2011-05-10", "5", 10);
        assertValues(Collections.singletonList(createExpected("2011-05-01/2011-05-09", "5", 10)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
        Assert.assertEquals(makeSingle(9), timeline.remove(Intervals.of("2011-05-01/2011-05-10"), "4", makeSingle(9)));
        assertValues(Collections.singletonList(createExpected("2011-05-01/2011-05-09", "5", 10)), timeline.lookup(Intervals.of("2011-05-01/2011-05-09")));
    }

    // 1|----|
    // 1|----|
    @Test(expected = UnsupportedOperationException.class)
    public void testOverlapSameVersionThrowException() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-05/2011-01-15", "1", 3);
    }

    // 1|----|
    // 1|----|
    @Test
    public void testOverlapSameVersionIsOkay() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-01/2011-01-10", "2", 2);
        add("2011-01-01/2011-01-10", "2", 3);
        add("2011-01-01/2011-01-10", "1", 4);
        assertValues(Collections.singletonList(createExpected("2011-01-01/2011-01-10", "2", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-10")));
    }

    // 1|----|----|
    // 2|----|
    @Test
    public void testOverlapSecondBetween() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-10/2011-01-20", "1", 2);
        add("2011-01-05/2011-01-15", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|----|
    // 1|----|----|
    @Test
    public void testOverlapFirstBetween() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-05/2011-01-15", "2", 3);
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-10/2011-01-20", "1", 2);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|----|
    // 2|----|
    @Test
    public void testOverlapFirstBefore() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-05/2011-01-15", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-15")));
    }

    // 2|----|
    // 1|----|
    @Test
    public void testOverlapFirstAfter() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-05/2011-01-15", "2", 3);
        add("2011-01-01/2011-01-10", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-15")));
    }

    // 1|----|
    // 2|----|
    @Test
    public void testOverlapSecondBefore() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-05/2011-01-15", "1", 3);
        add("2011-01-01/2011-01-10", "2", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-10", "2", 1), createExpected("2011-01-10/2011-01-15", "1", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-15")));
    }

    // 2|----|
    // 1|----|
    @Test
    public void testOverlapSecondAfter() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "2", 3);
        add("2011-01-05/2011-01-15", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-10", "2", 1), createExpected("2011-01-10/2011-01-15", "1", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-15")));
    }

    // 1|----------|
    // 2|----|
    @Test
    public void testOverlapFirstLarger() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 2);
        add("2011-01-05/2011-01-15", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 2), createExpected("2011-01-05/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|----|
    // 1|----------|
    @Test
    public void testOverlapSecondLarger() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-05/2011-01-15", "2", 3);
        add("2011-01-01/2011-01-20", "1", 2);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 2), createExpected("2011-01-05/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|----|-----|
    // 2|-------|
    @Test
    public void testOverlapSecondPartialAlign() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-10/2011-01-20", "1", 2);
        add("2011-01-01/2011-01-15", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|-------|
    // 1|----|-----|
    @Test
    public void testOverlapFirstPartialAlign() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-15", "2", 3);
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-10/2011-01-20", "1", 2);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-15", "2", 3), createExpected("2011-01-15/2011-01-20", "1", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|-------|
    // 2|------------|
    // 3|---|
    @Test
    public void testOverlapAscending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-05/2011-01-20", "2", 2);
        add("2011-01-03/2011-01-06", "3", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-03", "1", 1), createExpected("2011-01-03/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-20", "2", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 3|---|
    // 2|------------|
    // 1|-------|
    @Test
    public void testOverlapDescending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-03/2011-01-06", "3", 3);
        add("2011-01-05/2011-01-20", "2", 2);
        add("2011-01-01/2011-01-10", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-03", "1", 1), createExpected("2011-01-03/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-20", "2", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|------------|
    // 3|---|
    // 1|-------|
    @Test
    public void testOverlapMixed() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-05/2011-01-20", "2", 2);
        add("2011-01-03/2011-01-06", "3", 3);
        add("2011-01-01/2011-01-10", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-03", "1", 1), createExpected("2011-01-03/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-20", "2", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|-------------|
    // 2|--------|
    // 3|-----|
    @Test
    public void testOverlapContainedAscending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-02/2011-01-10", "2", 2);
        add("2011-01-02/2011-01-06", "3", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-02", "1", 1), createExpected("2011-01-02/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 3|-----|
    // 2|--------|
    // 1|-------------|
    @Test
    public void testOverlapContainedDescending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-02/2011-01-06", "3", 3);
        add("2011-01-02/2011-01-10", "2", 2);
        add("2011-01-01/2011-01-20", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-02", "1", 1), createExpected("2011-01-02/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|--------|
    // 3|-----|
    // 1|-------------|
    @Test
    public void testOverlapContainedmixed() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-02/2011-01-10", "2", 2);
        add("2011-01-02/2011-01-06", "3", 3);
        add("2011-01-01/2011-01-20", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-02", "1", 1), createExpected("2011-01-02/2011-01-06", "3", 3), createExpected("2011-01-06/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|------|------|----|
    // 2|-----|
    @Test
    public void testOverlapSecondContained() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-07", "1", 1);
        add("2011-01-07/2011-01-15", "1", 2);
        add("2011-01-15/2011-01-20", "1", 3);
        add("2011-01-10/2011-01-13", "2", 4);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-07", "1", 1), createExpected("2011-01-07/2011-01-10", "1", 2), createExpected("2011-01-10/2011-01-13", "2", 4), createExpected("2011-01-13/2011-01-15", "1", 2), createExpected("2011-01-15/2011-01-20", "1", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|-----|
    // 1|------|------|----|
    @Test
    public void testOverlapFirstContained() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-10/2011-01-13", "2", 4);
        add("2011-01-01/2011-01-07", "1", 1);
        add("2011-01-07/2011-01-15", "1", 2);
        add("2011-01-15/2011-01-20", "1", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-07", "1", 1), createExpected("2011-01-07/2011-01-10", "1", 2), createExpected("2011-01-10/2011-01-13", "2", 4), createExpected("2011-01-13/2011-01-15", "1", 2), createExpected("2011-01-15/2011-01-20", "1", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|----|----|
    // 2|---------|
    @Test
    public void testOverlapSecondContainsFirst() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-01/2011-01-10", "2", 2);
        add("2011-01-10/2011-01-20", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-20", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|---------|
    // 1|----|----|
    @Test
    public void testOverlapFirstContainsSecond() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-10/2011-01-20", "2", 3);
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-01/2011-01-10", "2", 2);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-20", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|----|
    // 2|----|
    // 3|----|
    @Test
    public void testOverlapLayeredAscending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-10", "1", 1);
        add("2011-01-05/2011-01-15", "2", 2);
        add("2011-01-15/2011-01-25", "3", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 2), createExpected("2011-01-15/2011-01-25", "3", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-25")));
    }

    // 3|----|
    // 2|----|
    // 1|----|
    @Test
    public void testOverlapLayeredDescending() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-15/2011-01-25", "3", 3);
        add("2011-01-05/2011-01-15", "2", 2);
        add("2011-01-01/2011-01-10", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-15", "2", 2), createExpected("2011-01-15/2011-01-25", "3", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-25")));
    }

    // 2|----|     |----|
    // 1|-------------|
    @Test
    public void testOverlapV1Large() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-15", "1", 1);
        add("2011-01-03/2011-01-05", "2", 2);
        add("2011-01-13/2011-01-20", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-03", "1", 1), createExpected("2011-01-03/2011-01-05", "2", 2), createExpected("2011-01-05/2011-01-13", "1", 1), createExpected("2011-01-13/2011-01-20", "2", 2)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|-------------|
    // 1|----|     |----|
    @Test
    public void testOverlapV2Large() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-15", "2", 1);
        add("2011-01-03/2011-01-05", "1", 2);
        add("2011-01-13/2011-01-20", "1", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-15", "2", 2), createExpected("2011-01-15/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|-------------|
    // 2|----|   |----|
    @Test
    public void testOverlapV1LargeIsAfter() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-03/2011-01-20", "1", 1);
        add("2011-01-01/2011-01-05", "2", 2);
        add("2011-01-13/2011-01-17", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "2", 2), createExpected("2011-01-05/2011-01-13", "1", 1), createExpected("2011-01-13/2011-01-17", "2", 3), createExpected("2011-01-17/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|----|   |----|
    // 1|-------------|
    @Test
    public void testOverlapV1SecondLargeIsAfter() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-13/2011-01-17", "2", 3);
        add("2011-01-01/2011-01-05", "2", 2);
        add("2011-01-03/2011-01-20", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "2", 2), createExpected("2011-01-05/2011-01-13", "1", 1), createExpected("2011-01-13/2011-01-17", "2", 3), createExpected("2011-01-17/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 1|-----------|
    // 2|----|     |----|
    @Test
    public void testOverlapV1FirstBetween() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-03/2011-01-17", "1", 1);
        add("2011-01-01/2011-01-05", "2", 2);
        add("2011-01-15/2011-01-20", "2", 3);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "2", 2), createExpected("2011-01-05/2011-01-15", "1", 1), createExpected("2011-01-15/2011-01-20", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 2|----|     |----|
    // 1|-----------|
    @Test
    public void testOverlapV1SecondBetween() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-05", "2", 2);
        add("2011-01-15/2011-01-20", "2", 3);
        add("2011-01-03/2011-01-17", "1", 1);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "2", 2), createExpected("2011-01-05/2011-01-15", "1", 1), createExpected("2011-01-15/2011-01-20", "2", 3)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    // 4|---|
    // 3|---|
    // 2|---|
    // 1|-------------|
    @Test
    public void testOverlapLargeUnderlyingWithSmallDayAlignedOverlays() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-05", "1", 1);
        add("2011-01-03/2011-01-04", "2", 2);
        add("2011-01-04/2011-01-05", "3", 3);
        add("2011-01-05/2011-01-06", "4", 4);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-03", "1", 1), createExpected("2011-01-03/2011-01-04", "2", 2), createExpected("2011-01-04/2011-01-05", "3", 3), createExpected("2011-01-05/2011-01-06", "4", 4)), timeline.lookup(Intervals.of("0000-01-01/3000-01-01")));
    }

    // |----3---||---1---|
    // |---2---|
    @Test
    public void testOverlapCausesNullEntries() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01T12/2011-01-02", "3", 3);
        add("2011-01-02/3011-01-03", "1", 1);
        add("2011-01-01/2011-01-02", "2", 2);
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-01T12", "2", 2), createExpected("2011-01-01T12/2011-01-02", "3", 3), createExpected("2011-01-02/3011-01-03", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/3011-01-03")));
    }

    // 1|----|    |----|
    // 2|------|  |------|
    // 3|------------------|
    @Test
    public void testOverlapOvershadowedThirdContains() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-03/2011-01-06", "1", 1);
        add("2011-01-09/2011-01-12", "1", 2);
        add("2011-01-02/2011-01-08", "2", 3);
        add("2011-01-10/2011-01-16", "2", 4);
        add("2011-01-01/2011-01-20", "3", 5);
        assertValues(Sets.newHashSet(createExpected("2011-01-02/2011-01-08", "2", 3), createExpected("2011-01-10/2011-01-16", "2", 4), createExpected("2011-01-03/2011-01-06", "1", 1), createExpected("2011-01-09/2011-01-12", "1", 2)), timeline.findOvershadowed());
    }

    // 2|------|------|
    // 1|-------------|
    // 3|-------------|
    @Test
    public void testOverlapOvershadowedAligned() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-05", "2", 1);
        add("2011-01-05/2011-01-10", "2", 2);
        add("2011-01-01/2011-01-10", "1", 3);
        add("2011-01-01/2011-01-10", "3", 4);
        assertValues(Sets.newHashSet(createExpected("2011-01-01/2011-01-05", "2", 1), createExpected("2011-01-05/2011-01-10", "2", 2), createExpected("2011-01-01/2011-01-10", "1", 3)), timeline.findOvershadowed());
    }

    // 2|----|      |-----|
    // 1|---------|
    // 3|-----------|
    @Test
    public void testOverlapOvershadowedSomeComplexOverlapsCantThinkOfBetterName() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-05", "2", 1);
        add("2011-01-10/2011-01-15", "2", 2);
        add("2011-01-03/2011-01-12", "1", 3);
        add("2011-01-01/2011-01-10", "3", 4);
        assertValues(Sets.newHashSet(createExpected("2011-01-03/2011-01-12", "1", 3), createExpected("2011-01-01/2011-01-05", "2", 1)), timeline.findOvershadowed());
    }

    @Test
    public void testOverlapAndRemove() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-10/2011-01-15", "2", 2);
        timeline.remove(Intervals.of("2011-01-10/2011-01-15"), "2", makeSingle(2));
        assertValues(Collections.singletonList(createExpected("2011-01-01/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    @Test
    public void testOverlapAndRemove2() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-10/2011-01-20", "2", 2);
        add("2011-01-20/2011-01-30", "3", 4);
        timeline.remove(Intervals.of("2011-01-10/2011-01-20"), "2", makeSingle(2));
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-20", "1", 1), createExpected("2011-01-20/2011-01-30", "3", 4)), timeline.lookup(Intervals.of("2011-01-01/2011-01-30")));
    }

    @Test
    public void testOverlapAndRemove3() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-02/2011-01-03", "2", 2);
        add("2011-01-10/2011-01-14", "2", 3);
        timeline.remove(Intervals.of("2011-01-02/2011-01-03"), "2", makeSingle(2));
        timeline.remove(Intervals.of("2011-01-10/2011-01-14"), "2", makeSingle(3));
        assertValues(Collections.singletonList(createExpected("2011-01-01/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    @Test
    public void testOverlapAndRemove4() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-10/2011-01-15", "2", 2);
        add("2011-01-15/2011-01-20", "2", 3);
        timeline.remove(Intervals.of("2011-01-15/2011-01-20"), "2", makeSingle(3));
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-10", "1", 1), createExpected("2011-01-10/2011-01-15", "2", 2), createExpected("2011-01-15/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    @Test
    public void testOverlapAndRemove5() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-20", "1", 1);
        add("2011-01-10/2011-01-15", "2", 2);
        timeline.remove(Intervals.of("2011-01-10/2011-01-15"), "2", makeSingle(2));
        add("2011-01-01/2011-01-20", "1", 1);
        assertValues(Collections.singletonList(createExpected("2011-01-01/2011-01-20", "1", 1)), timeline.lookup(Intervals.of("2011-01-01/2011-01-20")));
    }

    @Test
    public void testRemoveSomethingDontHave() {
        Assert.assertNull("Don't have it, should be null", timeline.remove(Intervals.of("1970-01-01/2025-04-20"), "1", makeSingle(1)));
        Assert.assertNull("Don't have it, should be null", timeline.remove(Intervals.of("2011-04-01/2011-04-09"), "version does not exist", makeSingle(1)));
    }

    @Test
    public void testRemoveNothingBacking() {
        timeline = makeStringIntegerTimeline();
        add("2011-01-01/2011-01-05", "1", 1);
        add("2011-01-05/2011-01-10", "2", 2);
        add("2011-01-10/2011-01-15", "3", 3);
        add("2011-01-15/2011-01-20", "4", 4);
        timeline.remove(Intervals.of("2011-01-15/2011-01-20"), "4", makeSingle(4));
        assertValues(Arrays.asList(createExpected("2011-01-01/2011-01-05", "1", 1), createExpected("2011-01-05/2011-01-10", "2", 2), createExpected("2011-01-10/2011-01-15", "3", 3)), timeline.lookup(new Interval(DateTimes.EPOCH, DateTimes.MAX)));
    }

    @Test
    public void testOvershadowingHigherVersionWins1() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-03", "1", 2);
        add("2011-04-03/2011-04-06", "1", 3);
        add("2011-04-06/2011-04-09", "1", 4);
        add("2011-04-01/2011-04-09", "2", 1);
        assertValues(ImmutableSet.of(createExpected("2011-04-01/2011-04-03", "1", 2), createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-06/2011-04-09", "1", 4)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionWins2() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-01/2011-04-03", "2", 2);
        add("2011-04-03/2011-04-06", "2", 3);
        add("2011-04-06/2011-04-09", "2", 4);
        assertValues(ImmutableSet.of(createExpected("2011-04-01/2011-04-09", "1", 1)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionWins3() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-03", "1", 2);
        add("2011-04-03/2011-04-06", "1", 3);
        add("2011-04-09/2011-04-12", "1", 4);
        add("2011-04-01/2011-04-12", "2", 1);
        assertValues(Sets.newHashSet(createExpected("2011-04-01/2011-04-03", "1", 2), createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-09/2011-04-12", "1", 4)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionWins4() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-03/2011-04-06", "1", 3);
        add("2011-04-06/2011-04-09", "1", 4);
        add("2011-04-01/2011-04-09", "2", 1);
        assertValues(ImmutableSet.of(createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-06/2011-04-09", "1", 4)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionNeverOvershadowedByLower1() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-03/2011-04-06", "2", 3);
        add("2011-04-06/2011-04-09", "2", 4);
        assertValues(ImmutableSet.of(), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionNeverOvershadowedByLower2() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-01/2011-04-03", "2", 2);
        add("2011-04-06/2011-04-09", "2", 4);
        assertValues(ImmutableSet.of(), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionNeverOvershadowedByLower3() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-01/2011-04-03", "2", 2);
        add("2011-04-03/2011-04-06", "2", 3);
        assertValues(ImmutableSet.of(), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionNeverOvershadowedByLower4() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "2", 1);
        add("2011-04-01/2011-04-03", "3", 2);
        add("2011-04-03/2011-04-06", "4", 3);
        add("2011-04-03/2011-04-06", "1", 3);
        assertValues(ImmutableSet.of(createExpected("2011-04-03/2011-04-06", "1", 3)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingHigherVersionNeverOvershadowedByLower5() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-12", "2", 1);
        add("2011-04-01/2011-04-03", "3", 2);
        add("2011-04-06/2011-04-09", "4", 3);
        add("2011-04-09/2011-04-12", "1", 3);
        add("2011-04-03/2011-04-06", "1", 3);
        assertValues(Sets.newHashSet(createExpected("2011-04-03/2011-04-06", "1", 3), createExpected("2011-04-09/2011-04-12", "1", 3)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingSameIntervalHighVersionWins() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-01/2011-04-09", "9", 2);
        add("2011-04-01/2011-04-09", "2", 3);
        assertValues(Sets.newHashSet(createExpected("2011-04-01/2011-04-09", "2", 3), createExpected("2011-04-01/2011-04-09", "1", 1)), timeline.findOvershadowed());
    }

    @Test
    public void testOvershadowingSameIntervalSameVersionAllKept() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        add("2011-04-01/2011-04-09", "9", 2);
        add("2011-04-01/2011-04-09", "2", 3);
        add("2011-04-01/2011-04-09", "9", 4);
        assertValues(Sets.newHashSet(createExpected("2011-04-01/2011-04-09", "2", 3), createExpected("2011-04-01/2011-04-09", "1", 1)), timeline.findOvershadowed());
    }

    @Test
    public void testNotFoundReturnsEmpty() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-09", "1", 1);
        Assert.assertTrue(timeline.lookup(Intervals.of("1970/1980")).isEmpty());
    }

    // https://github.com/apache/incubator-druid/issues/3010
    @Test
    public void testRemoveIncompleteKeepsComplete() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-01/2011-04-02", "1", IntegerPartitionChunk.make(null, 1, 0, 77));
        add("2011-04-01/2011-04-02", "1", IntegerPartitionChunk.make(1, null, 1, 88));
        add("2011-04-01/2011-04-02", "2", IntegerPartitionChunk.make(null, 1, 0, 99));
        assertValues(ImmutableList.of(createExpected("2011-04-01/2011-04-02", "1", Arrays.asList(IntegerPartitionChunk.make(null, 1, 0, 77), IntegerPartitionChunk.make(1, null, 1, 88)))), timeline.lookup(Intervals.of("2011-04-01/2011-04-02")));
        add("2011-04-01/2011-04-02", "3", IntegerPartitionChunk.make(null, 1, 0, 110));
        assertValues(ImmutableList.of(createExpected("2011-04-01/2011-04-02", "1", Arrays.asList(IntegerPartitionChunk.make(null, 1, 0, 77), IntegerPartitionChunk.make(1, null, 1, 88)))), timeline.lookup(Intervals.of("2011-04-01/2011-04-02")));
        assertValues(Sets.newHashSet(createExpected("2011-04-01/2011-04-02", "2", Collections.singletonList(IntegerPartitionChunk.make(null, 1, 0, 99)))), timeline.findOvershadowed());
        testRemove();
        assertValues(ImmutableList.of(createExpected("2011-04-01/2011-04-02", "1", Arrays.asList(IntegerPartitionChunk.make(null, 1, 0, 77), IntegerPartitionChunk.make(1, null, 1, 88)))), timeline.lookup(Intervals.of("2011-04-01/2011-04-02")));
    }

    @Test
    public void testIsOvershadowedWithNonOverlappingSegmentsInTimeline() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-05/2011-04-07", "1", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-07/2011-04-09", "1", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-15/2011-04-17", "1", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-17/2011-04-19", "1", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-03"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-05"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-06"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-07"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-08"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-30"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "1"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "1"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "1"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "1"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "2"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "2"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "2"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "2"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-07"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-08"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-30"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-07/2011-04-08"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-07/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-07/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-07/2011-04-30"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-08/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-08/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-08/2011-04-30"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-09/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-09/2011-04-15"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-09/2011-04-17"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-09/2011-04-19"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-09/2011-04-30"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-16"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-17"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-18"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-19"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-20"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-30"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-19/2011-04-20"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-21/2011-04-22"), "0"));
    }

    @Test
    public void testIsOvershadowedWithOverlappingSegmentsInTimeline() {
        timeline = makeStringIntegerTimeline();
        add("2011-04-05/2011-04-09", "11", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-07/2011-04-11", "12", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-15/2011-04-19", "12", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        add("2011-04-17/2011-04-21", "11", new org.apache.druid.timeline.partition.SingleElementPartitionChunk<Integer>(1));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-03"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-05"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-06"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-07"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-08"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-09"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-10"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-11"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-01/2011-04-30"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-10"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-11"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-10"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-11"), "12"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-06"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-07"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-08"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-09"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-10"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-11"), "13"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-12"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-15"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-16"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-17"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-18"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-19"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-20"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-21"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-05/2011-04-22"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-07"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-08"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-09"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-10"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-11"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-12"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-15"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-16"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-17"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-18"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-19"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-20"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-21"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-06/2011-04-22"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-15"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-16"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-17"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-18"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-19"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-20"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-21"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-12/2011-04-22"), "0"));
        Assert.assertTrue(timeline.isOvershadowed(Intervals.of("2011-04-15/2011-04-21"), "0"));
        Assert.assertFalse(timeline.isOvershadowed(Intervals.of("2011-04-21/2011-04-22"), "0"));
    }
}
