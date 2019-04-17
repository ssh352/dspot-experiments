/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classloading;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ThreadLeakTest extends AbstractThreadLeakTest {
    @Test
    public void testThreadLeak() {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        hz.shutdown();
    }

    @Test
    public void testThreadLeakUtils() {
        final Set<Thread> threads = ThreadLeakTestUtils.getThreads();
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread("leaking-thread") {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        thread.start();
        Thread[] runningThreads = ThreadLeakTestUtils.getAndLogThreads("There should be one thread running!", threads);
        Assert.assertNotNull("Expected to get running threads, but was null", runningThreads);
        Assert.assertEquals("Expected exactly one running thread", 1, runningThreads.length);
        latch.countDown();
        HazelcastTestSupport.assertJoinable(thread);
        runningThreads = ThreadLeakTestUtils.getAndLogThreads("There should be no threads running!", threads);
        Assert.assertNull("Expected to get null, but found running threads", runningThreads);
    }
}
