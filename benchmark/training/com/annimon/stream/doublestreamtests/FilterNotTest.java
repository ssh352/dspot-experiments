package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import com.annimon.stream.function.DoublePredicate;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterNotTest {
    @Test
    public void testFilterNot() {
        final DoublePredicate predicate = Functions.greaterThan(Math.PI);
        DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).filterNot(predicate).custom(assertElements(Matchers.arrayContaining(0.012, 3.039)));
        DoubleStream.of(4.096, 12).filterNot(predicate).custom(assertIsEmpty());
    }
}
