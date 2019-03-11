/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.repeat.support;


import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Syer
 */
public class ThrottleLimitResultQueueTests {
    private ThrottleLimitResultQueue<String> queue = new ThrottleLimitResultQueue(1);

    @Test
    public void testPutTake() throws Exception {
        queue.expect();
        Assert.assertTrue(queue.isExpecting());
        Assert.assertTrue(queue.isEmpty());
        queue.put("foo");
        Assert.assertFalse(queue.isEmpty());
        Assert.assertEquals("foo", queue.take());
        Assert.assertFalse(queue.isExpecting());
    }

    @Test
    public void testPutWithoutExpecting() throws Exception {
        Assert.assertFalse(queue.isExpecting());
        try {
            queue.put("foo");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testTakeWithoutExpecting() throws Exception {
        Assert.assertFalse(queue.isExpecting());
        try {
            queue.take();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testThrottleLimit() throws Exception {
        queue.expect();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                queue.put("foo");
            }
        }).start();
        long t0 = System.currentTimeMillis();
        queue.expect();
        long t1 = System.currentTimeMillis();
        Assert.assertEquals("foo", queue.take());
        Assert.assertTrue(queue.isExpecting());
        Assert.assertTrue(("Did not block on expect (throttle limit should have been hit): time taken=" + (t1 - t0)), ((t1 - t0) > 50));
    }
}

