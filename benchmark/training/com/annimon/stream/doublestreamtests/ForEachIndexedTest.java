package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.IntPair;
import com.annimon.stream.function.IndexedDoubleConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ForEachIndexedTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testForEachIndexed() {
        final List<IntPair<Double>> result = new ArrayList<IntPair<Double>>();
        DoubleStream.of(1000, 2000, 3000).forEachIndexed(new IndexedDoubleConsumer() {
            @Override
            public void accept(int index, double value) {
                result.add(new IntPair<Double>(index, value));
            }
        });
        Assert.assertThat(result, Matchers.is(Arrays.asList(new IntPair<Double>(0, 1000.0), new IntPair<Double>(1, 2000.0), new IntPair<Double>(2, 3000.0))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForEachIndexedWithStartAndStep() {
        final List<IntPair<Double>> result = new ArrayList<IntPair<Double>>();
        DoubleStream.of(1000, 2000, 3000).forEachIndexed(50, (-10), new IndexedDoubleConsumer() {
            @Override
            public void accept(int index, double value) {
                result.add(new IntPair<Double>(index, value));
            }
        });
        Assert.assertThat(result, Matchers.is(Arrays.asList(new IntPair<Double>(50, 1000.0), new IntPair<Double>(40, 2000.0), new IntPair<Double>(30, 3000.0))));
    }
}
