package com.baeldung.concurrent.adder;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import junit.framework.TestCase;
import org.junit.Test;


public class LongAdderUnitTest {
    @Test
    public void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThem() throws InterruptedException {
        // given
        LongAdder counter = new LongAdder();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        int numberOfThreads = 4;
        int numberOfIncrements = 100;
        // when
        Runnable incrementAction = () -> IntStream.range(0, numberOfIncrements).forEach(( i) -> counter.increment());
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(incrementAction);
        }
        // then
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        executorService.shutdown();
        TestCase.assertEquals(counter.sum(), (numberOfIncrements * numberOfThreads));
        TestCase.assertEquals(counter.sum(), (numberOfIncrements * numberOfThreads));
    }

    @Test
    public void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThemAndResetAdderAfterward() throws InterruptedException {
        // given
        LongAdder counter = new LongAdder();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        int numberOfThreads = 4;
        int numberOfIncrements = 100;
        // when
        Runnable incrementAction = () -> IntStream.range(0, numberOfIncrements).forEach(( i) -> counter.increment());
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(incrementAction);
        }
        // then
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        executorService.shutdown();
        TestCase.assertEquals(counter.sumThenReset(), (numberOfIncrements * numberOfThreads));
        await().until(() -> assertEquals(counter.sum(), 0));
    }
}

