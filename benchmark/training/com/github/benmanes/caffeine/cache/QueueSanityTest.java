/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache;


import Ordering.FIFO;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.util.Pow2;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author nitsanw@yahoo.com (Nitsan Wakart)
 */
@SuppressWarnings("ThreadPriorityCheck")
public abstract class QueueSanityTest {
    public static final int SIZE = 8192 * 2;

    private final Queue<Integer> queue;

    private final ConcurrentQueueSpec spec;

    public QueueSanityTest(ConcurrentQueueSpec spec, Queue<Integer> queue) {
        this.queue = queue;
        this.spec = spec;
    }

    @Test
    public void sanity() {
        for (int i = 0; i < (QueueSanityTest.SIZE); i++) {
            Assert.assertNull(queue.poll());
            Assert.assertThat(queue, QueueSanityTest.emptyAndZeroSize());
        }
        int i = 0;
        while ((i < (QueueSanityTest.SIZE)) && (queue.offer(i))) {
            i++;
        } 
        int size = i;
        Assert.assertEquals(size, queue.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            i = 0;
            Integer p;
            Integer e;
            while ((p = queue.peek()) != null) {
                e = queue.poll();
                Assert.assertEquals(p, e);
                Assert.assertEquals((size - (i + 1)), queue.size());
                Assert.assertEquals((i++), e.intValue());
            } 
            Assert.assertEquals(size, i);
        } else {
            // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
            int sum = ((size - 1) * size) / 2;
            i = 0;
            Integer e;
            while ((e = queue.poll()) != null) {
                Assert.assertEquals((--size), queue.size());
                sum -= e;
            } 
            Assert.assertEquals(0, sum);
        }
        Assert.assertNull(queue.poll());
        Assert.assertThat(queue, QueueSanityTest.emptyAndZeroSize());
    }

    @Test
    public void testSizeIsTheNumberOfOffers() {
        int currentSize = 0;
        while ((currentSize < (QueueSanityTest.SIZE)) && (queue.offer(currentSize))) {
            currentSize++;
            Assert.assertThat(queue, Matchers.hasSize(currentSize));
        } 
    }

    @Test
    public void whenFirstInThenFirstOut() {
        Assume.assumeThat(spec.ordering, Matchers.is(FIFO));
        // Arrange
        int i = 0;
        while ((i < (QueueSanityTest.SIZE)) && (queue.offer(i))) {
            i++;
        } 
        final int size = queue.size();
        // Act
        i = 0;
        Integer prev;
        while ((prev = queue.peek()) != null) {
            final Integer item = queue.poll();
            Assert.assertThat(item, Matchers.is(prev));
            Assert.assertThat(queue, Matchers.hasSize((size - (i + 1))));
            Assert.assertThat(item, Matchers.is(i));
            i++;
        } 
        // Assert
        Assert.assertThat(i, Matchers.is(size));
    }

    @Test(expected = NullPointerException.class)
    public void offerNullResultsInNPE() {
        queue.offer(null);
    }

    @Test
    public void whenOfferItemAndPollItemThenSameInstanceReturnedAndQueueIsEmpty() {
        Assert.assertThat(queue, QueueSanityTest.emptyAndZeroSize());
        // Act
        final Integer e = 1876876;
        queue.offer(e);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertEquals(1, queue.size());
        final Integer oh = queue.poll();
        Assert.assertEquals(e, oh);
        // Assert
        Assert.assertThat(oh, Matchers.sameInstance(e));
        Assert.assertThat(queue, QueueSanityTest.emptyAndZeroSize());
    }

    @Test
    public void testPowerOf2Capacity() {
        Assume.assumeThat(spec.isBounded(), Matchers.is(true));
        int n = Pow2.roundToPowerOfTwo(spec.capacity);
        for (int i = 0; i < n; i++) {
            Assert.assertTrue(("Failed to insert:" + i), queue.offer(i));
        }
        Assert.assertFalse(queue.offer(n));
    }

    static final class Val {
        public int value;
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testHappensBefore() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 1; i <= 10; i++) {
                        QueueSanityTest.Val v = new QueueSanityTest.Val();
                        v.value = i;
                        q.offer(v);
                    }
                    // slow down the producer, this will make the queue mostly empty encouraging visibility
                    // issues.
                    Thread.yield();
                } 
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 0; i < 10; i++) {
                        QueueSanityTest.Val v = ((QueueSanityTest.Val) (q.peek()));
                        if ((v != null) && ((v.value) == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        }
                        q.poll();
                    }
                } 
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("reordering detected", 0, fail.value);
    }

    @Test
    public void testSize() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue<Integer> q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.offer(1);
                    q.poll();
                } 
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    int size = q.size();
                    if ((size != 0) && (size != 1)) {
                        (fail.value)++;
                    }
                } 
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("Unexpected size observed", 0, fail.value);
    }

    @Test
    public void testPollAfterIsEmpty() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue<Integer> q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.offer(1);
                    // slow down the producer, this will make the queue mostly empty encouraging visibility
                    // issues.
                    Thread.yield();
                } 
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    if ((!(q.isEmpty())) && ((q.poll()) == null)) {
                        (fail.value)++;
                    }
                } 
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("Observed no element in non-empty queue", 0, fail.value);
    }
}

