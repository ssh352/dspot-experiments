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
package com.hazelcast.collection.impl.queue;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestStringUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class StoreLatencyPlugin_QueueIntegrationTest extends HazelcastTestSupport {
    private static final String QUEUE_NAME = "someQueue";

    private HazelcastInstance hz;

    private IQueue<Integer> queue;

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++) {
            queue.put(i);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                File file = HazelcastTestSupport.getNodeEngineImpl(hz).getDiagnostics().currentFile();
                String content = TestStringUtils.fileAsText(file);
                HazelcastTestSupport.assertContains(content, StoreLatencyPlugin_QueueIntegrationTest.QUEUE_NAME);
            }
        });
    }
}
