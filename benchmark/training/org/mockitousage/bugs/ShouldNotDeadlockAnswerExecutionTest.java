/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


// see bug 190
public class ShouldNotDeadlockAnswerExecutionTest {
    @Test
    public void failIfMockIsSharedBetweenThreads() throws Exception {
        ShouldNotDeadlockAnswerExecutionTest.Service service = Mockito.mock(ShouldNotDeadlockAnswerExecutionTest.Service.class);
        ExecutorService threads = Executors.newCachedThreadPool();
        AtomicInteger counter = new AtomicInteger(2);
        // registed answer on verySlowMethod
        Mockito.when(service.verySlowMethod()).thenAnswer(new ShouldNotDeadlockAnswerExecutionTest.LockingAnswer(counter));
        // execute verySlowMethod twice in separate threads
        threads.execute(new ShouldNotDeadlockAnswerExecutionTest.ServiceRunner(service));
        threads.execute(new ShouldNotDeadlockAnswerExecutionTest.ServiceRunner(service));
        // waiting for threads to finish
        threads.shutdown();
        if (!(threads.awaitTermination(1000, TimeUnit.MILLISECONDS))) {
            // threads were timed-out
            Assert.fail();
        }
    }

    @Test
    public void successIfEveryThreadHasItsOwnMock() throws Exception {
        ShouldNotDeadlockAnswerExecutionTest.Service service1 = Mockito.mock(ShouldNotDeadlockAnswerExecutionTest.Service.class);
        ShouldNotDeadlockAnswerExecutionTest.Service service2 = Mockito.mock(ShouldNotDeadlockAnswerExecutionTest.Service.class);
        ExecutorService threads = Executors.newCachedThreadPool();
        AtomicInteger counter = new AtomicInteger(2);
        // registed answer on verySlowMethod
        Mockito.when(service1.verySlowMethod()).thenAnswer(new ShouldNotDeadlockAnswerExecutionTest.LockingAnswer(counter));
        Mockito.when(service2.verySlowMethod()).thenAnswer(new ShouldNotDeadlockAnswerExecutionTest.LockingAnswer(counter));
        // execute verySlowMethod twice in separate threads
        threads.execute(new ShouldNotDeadlockAnswerExecutionTest.ServiceRunner(service1));
        threads.execute(new ShouldNotDeadlockAnswerExecutionTest.ServiceRunner(service2));
        // waiting for threads to finish
        threads.shutdown();
        if (!(threads.awaitTermination(500, TimeUnit.MILLISECONDS))) {
            // threads were timed-out
            Assert.fail();
        }
    }

    static class LockingAnswer implements Answer<String> {
        private AtomicInteger counter;

        public LockingAnswer(AtomicInteger counter) {
            this.counter = counter;
        }

        /**
         * Decrement counter and wait until counter has value 0
         */
        public String answer(InvocationOnMock invocation) throws Throwable {
            counter.decrementAndGet();
            while ((counter.get()) != 0) {
                Thread.sleep(10);
            } 
            return null;
        }
    }

    static class ServiceRunner implements Runnable {
        private ShouldNotDeadlockAnswerExecutionTest.Service service;

        public ServiceRunner(ShouldNotDeadlockAnswerExecutionTest.Service service) {
            this.service = service;
        }

        public void run() {
            service.verySlowMethod();
        }
    }

    interface Service {
        String verySlowMethod();
    }
}

