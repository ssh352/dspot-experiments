package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


public final class AllMatchTest {
    @Test
    public void testAllMatch() {
        Assert.assertFalse(DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).allMatch(Functions.greaterThan(Math.PI)));
        Assert.assertTrue(DoubleStream.of(10.347, 19.84, 100.0).allMatch(Functions.greaterThan(Math.PI)));
        Assert.assertFalse(DoubleStream.of(0.012, 3.039).allMatch(Functions.greaterThan(Math.PI)));
        Assert.assertTrue(DoubleStream.empty().allMatch(Functions.greaterThan(Math.PI)));
    }
}

