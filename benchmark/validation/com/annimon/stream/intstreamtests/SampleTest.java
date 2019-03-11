package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class SampleTest {
    @Test
    public void testSample() {
        IntStream.of(1, 2, 3, 1, 2, 3, 1, 2, 3).sample(3).custom(assertElements(Matchers.arrayContaining(1, 1, 1)));
    }

    @Test
    public void testSampleWithStep1() {
        IntStream.of(1, 2, 3, 1, 2, 3, 1, 2, 3).sample(1).custom(assertElements(Matchers.arrayContaining(1, 2, 3, 1, 2, 3, 1, 2, 3)));
    }

    @Test(expected = IllegalArgumentException.class, timeout = 1000)
    public void testSampleWithNegativeStep() {
        IntStream.of(1, 2, 3, 1, 2, 3, 1, 2, 3).sample((-1)).count();
    }
}

