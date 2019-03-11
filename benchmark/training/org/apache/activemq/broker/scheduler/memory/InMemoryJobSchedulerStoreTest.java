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
package org.apache.activemq.broker.scheduler.memory;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.scheduler.Job;
import org.apache.activemq.broker.scheduler.JobScheduler;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.IOHelper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class InMemoryJobSchedulerStoreTest {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryJobSchedulerStoreTest.class);

    @Test(timeout = 120 * 1000)
    public void testRestart() throws Exception {
        InMemoryJobSchedulerStore store = new InMemoryJobSchedulerStore();
        File directory = new File("target/test/ScheduledDB");
        IOHelper.mkdirs(directory);
        IOHelper.deleteChildren(directory);
        store.setDirectory(directory);
        final int NUMBER = 1000;
        store.start();
        List<ByteSequence> list = new ArrayList<ByteSequence>();
        for (int i = 0; i < NUMBER; i++) {
            ByteSequence buff = new ByteSequence(new String(("testjob" + i)).getBytes());
            list.add(buff);
        }
        JobScheduler js = store.getJobScheduler("test");
        js.startDispatching();
        int count = 0;
        long startTime = (10 * 60) * 1000;
        long period = startTime;
        for (ByteSequence job : list) {
            js.schedule(("id:" + (count++)), job, "", startTime, period, (-1));
        }
        List<Job> test = js.getAllJobs();
        InMemoryJobSchedulerStoreTest.LOG.debug("Found {} jobs in the store before restart", test.size());
        Assert.assertEquals(list.size(), test.size());
        store.stop();
        store.start();
        js = store.getJobScheduler("test");
        test = js.getAllJobs();
        InMemoryJobSchedulerStoreTest.LOG.debug("Found {} jobs in the store after restart", test.size());
        Assert.assertEquals(0, test.size());
    }
}

