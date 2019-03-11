package com.baeldung.pairsaddupnumber;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DifferentPairsUnitTest {
    /* All different pairs */
    @Test
    public void whenTraditionalLoop_thenReturnAllDifferentPairs() {
        /* Data */
        final int[] input = new int[]{ 2, 4, 3, 3, 8 };
        final int sum = 6;
        /* Call service */
        final List<Integer> pairs = DifferentPairs.findPairsWithForLoop(input, sum);
        /* Check results */
        assertThat(pairs).hasSize(2).contains(4, 3).doesNotContain(8);
    }

    @Test
    public void whenStreamApi_thenReturnAllDifferentPairs() {
        /* Data */
        final int[] input = new int[]{ 2, 4, 3, 3, 8 };
        final int sum = 6;
        /* Call service */
        final List<Integer> pairs = DifferentPairs.findPairsWithStreamApi(input, sum);
        /* Check results */
        Assert.assertNotNull(pairs);
        Assert.assertEquals(pairs.size(), 2);
        Assert.assertEquals(pairs.get(0), new Integer(4));
        assertThat(pairs).hasSize(2).contains(4, 3).doesNotContain(8);
    }
}

