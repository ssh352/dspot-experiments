/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.scheduler;


import Status.ABORT;
import Status.FINISHED;
import Status.PENDING;
import Status.RUNNING;
import junit.framework.TestCase;
import org.junit.Test;


public class FIFOSchedulerTest extends TestCase {
    private SchedulerFactory schedulerSvc;

    @Test
    public void testRun() throws InterruptedException {
        Scheduler s = schedulerSvc.createOrGetFIFOScheduler("test");
        Job job1 = new SleepingJob("job1", null, 500);
        Job job2 = new SleepingJob("job2", null, 500);
        s.submit(job1);
        s.submit(job2);
        Thread.sleep(200);
        TestCase.assertEquals(RUNNING, job1.getStatus());
        TestCase.assertEquals(PENDING, job2.getStatus());
        Thread.sleep(500);
        TestCase.assertEquals(FINISHED, job1.getStatus());
        TestCase.assertEquals(RUNNING, job2.getStatus());
        TestCase.assertTrue((500 < ((Long) (job1.getReturn()))));
        s.stop();
    }

    @Test
    public void testAbort() throws InterruptedException {
        Scheduler s = schedulerSvc.createOrGetFIFOScheduler("test");
        Job job1 = new SleepingJob("job1", null, 500);
        Job job2 = new SleepingJob("job2", null, 500);
        s.submit(job1);
        s.submit(job2);
        Thread.sleep(200);
        job1.abort();
        job2.abort();
        Thread.sleep(200);
        TestCase.assertEquals(ABORT, job1.getStatus());
        TestCase.assertEquals(ABORT, job2.getStatus());
        TestCase.assertTrue((500 > ((Long) (job1.getReturn()))));
        TestCase.assertEquals(null, job2.getReturn());
        s.stop();
    }
}
