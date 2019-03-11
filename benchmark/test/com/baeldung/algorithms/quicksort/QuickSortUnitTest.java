package com.baeldung.algorithms.quicksort;


import org.junit.Assert;
import org.junit.Test;


public class QuickSortUnitTest {
    @Test
    public void givenIntegerArray_whenSortedWithQuickSort_thenGetSortedArray() {
        int[] actual = new int[]{ 9, 5, 1, 0, 6, 2, 3, 4, 7, 8 };
        int[] expected = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        QuickSort.quickSort(actual, 0, ((actual.length) - 1));
        Assert.assertArrayEquals(expected, actual);
    }
}

