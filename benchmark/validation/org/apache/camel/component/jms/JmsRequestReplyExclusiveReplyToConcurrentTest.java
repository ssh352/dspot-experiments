/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jms;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;


public class JmsRequestReplyExclusiveReplyToConcurrentTest extends CamelTestSupport {
    private final int size = 100;

    private final CountDownLatch latch = new CountDownLatch(size);

    @Test
    public void testJmsRequestReplyExclusiveFixedReplyTo() throws Exception {
        StopWatch watch = new StopWatch();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < (size); i++) {
            final Integer num = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    String reply = template.requestBody("direct:start", ("" + num), String.class);
                    log.info("Sent {} expecting reply 'Hello {}' got --> {}", num, num, reply);
                    assertNotNull(reply);
                    assertEquals(("Hello " + num), reply);
                    latch.countDown();
                }
            });
        }
        log.info("Waiting to process {} messages...", size);
        // if any of the assertions above fails then the latch will not get decremented
        assertTrue("All assertions outside the main thread above should have passed", latch.await(3, TimeUnit.SECONDS));
        long delta = watch.taken();
        log.info("Took {} millis", delta);
        // just sleep a bit before shutting down
        Thread.sleep(1000);
        executor.shutdownNow();
    }
}
