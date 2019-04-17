package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class RangeTest {
    @Test
    public void testStreamRange() {
        Stream.range(0, 5).custom(assertElements(Matchers.contains(0, 1, 2, 3, 4)));
    }

    @Test
    public void testStreamRangeOnMaxValues() {
        long count = Stream.range(((Integer.MAX_VALUE) - 10), Integer.MAX_VALUE).count();
        Assert.assertEquals(10L, count);
    }

    @Test
    public void testStreamRangeOnMaxLongValues() {
        long count = Stream.range(((Long.MAX_VALUE) - 10), Long.MAX_VALUE).count();
        Assert.assertEquals(10L, count);
    }

    @Test
    public void testStreamRangeClosed() {
        Stream.rangeClosed(0, 5).custom(assertElements(Matchers.contains(0, 1, 2, 3, 4, 5)));
    }

    @Test
    public void testStreamRangeClosedOnMaxValues() {
        long count = Stream.rangeClosed(((Integer.MAX_VALUE) - 10), Integer.MAX_VALUE).count();
        Assert.assertEquals(11L, count);
    }

    @Test
    public void testStreamRangeClosedOnMaxLongValues() {
        long count = Stream.rangeClosed(((Long.MAX_VALUE) - 10), Long.MAX_VALUE).count();
        Assert.assertEquals(11L, count);
    }
}
