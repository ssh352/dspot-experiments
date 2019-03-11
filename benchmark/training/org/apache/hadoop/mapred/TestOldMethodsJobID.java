/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;


import Status.OBSOLETE;
import TaskType.MAP;
import TaskType.REDUCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.mapred.TaskCompletionEvent.Status;
import org.apache.hadoop.mapreduce.TaskType;
import org.junit.Assert;
import org.junit.Test;

import static Reporter.NULL;


/**
 * Test deprecated methods
 */
public class TestOldMethodsJobID {
    /**
     * test deprecated methods of TaskID
     *
     * @throws IOException
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testDepricatedMethods() throws IOException {
        JobID jid = new JobID();
        TaskID test = new TaskID(jid, true, 1);
        Assert.assertEquals(test.getTaskType(), MAP);
        test = new TaskID(jid, false, 1);
        Assert.assertEquals(test.getTaskType(), REDUCE);
        test = new TaskID("001", 1, false, 1);
        Assert.assertEquals(test.getTaskType(), REDUCE);
        test = new TaskID("001", 1, true, 1);
        Assert.assertEquals(test.getTaskType(), MAP);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        test.write(new DataOutputStream(out));
        TaskID ti = TaskID.read(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        Assert.assertEquals(ti.toString(), test.toString());
        Assert.assertEquals("task_001_0001_m_000002", TaskID.getTaskIDsPattern("001", 1, true, 2));
        Assert.assertEquals("task_003_0001_m_000004", TaskID.getTaskIDsPattern("003", 1, MAP, 4));
        Assert.assertEquals("003_0001_m_000004", TaskID.getTaskIDsPatternWOPrefix("003", 1, MAP, 4).toString());
    }

    /**
     * test JobID
     *
     * @throws IOException
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testJobID() throws IOException {
        JobID jid = new JobID("001", 2);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jid.write(new DataOutputStream(out));
        Assert.assertEquals(jid, JobID.read(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))));
        Assert.assertEquals("job_001_0001", JobID.getJobIDsPattern("001", 1));
    }

    /**
     * test deprecated methods of TaskCompletionEvent
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testTaskCompletionEvent() {
        TaskAttemptID taid = new TaskAttemptID("001", 1, TaskType.REDUCE, 2, 3);
        TaskCompletionEvent template = new TaskCompletionEvent(12, taid, 13, true, Status.SUCCEEDED, "httptracker");
        TaskCompletionEvent testEl = TaskCompletionEvent.downgrade(template);
        testEl.setTaskAttemptId(taid);
        testEl.setTaskTrackerHttp("httpTracker");
        testEl.setTaskId("attempt_001_0001_m_000002_04");
        Assert.assertEquals("attempt_001_0001_m_000002_4", testEl.getTaskId());
        testEl.setTaskStatus(OBSOLETE);
        Assert.assertEquals(OBSOLETE.toString(), testEl.getStatus().toString());
        testEl.setTaskRunTime(20);
        Assert.assertEquals(testEl.getTaskRunTime(), 20);
        testEl.setEventId(16);
        Assert.assertEquals(testEl.getEventId(), 16);
    }

    /**
     * test depricated methods of JobProfile
     *
     * @throws IOException
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testJobProfile() throws IOException {
        JobProfile profile = new JobProfile("user", "job_001_03", "jobFile", "uri", "name");
        Assert.assertEquals("job_001_0003", profile.getJobId());
        Assert.assertEquals("default", profile.getQueueName());
        // serialization test
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        profile.write(new DataOutputStream(out));
        JobProfile profile2 = new JobProfile();
        profile2.readFields(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        Assert.assertEquals(profile2.name, profile.name);
        Assert.assertEquals(profile2.jobFile, profile.jobFile);
        Assert.assertEquals(profile2.queueName, profile.queueName);
        Assert.assertEquals(profile2.url, profile.url);
        Assert.assertEquals(profile2.user, profile.user);
    }

    /**
     * test TaskAttemptID
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testTaskAttemptID() {
        TaskAttemptID task = new TaskAttemptID("001", 2, true, 3, 4);
        Assert.assertEquals("attempt_001_0002_m_000003_4", TaskAttemptID.getTaskAttemptIDsPattern("001", 2, true, 3, 4));
        Assert.assertEquals("task_001_0002_m_000003", task.getTaskID().toString());
        Assert.assertEquals("attempt_001_0001_r_000002_3", TaskAttemptID.getTaskAttemptIDsPattern("001", 1, REDUCE, 2, 3));
        Assert.assertEquals("001_0001_m_000001_2", TaskAttemptID.getTaskAttemptIDsPatternWOPrefix("001", 1, MAP, 1, 2).toString());
    }

    /**
     * test Reporter.NULL
     */
    @Test(timeout = 5000)
    public void testReporter() {
        Reporter nullReporter = NULL;
        Assert.assertNull(nullReporter.getCounter(null));
        Assert.assertNull(nullReporter.getCounter("group", "name"));
        // getInputSplit method removed
        try {
            Assert.assertNull(nullReporter.getInputSplit());
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("NULL reporter has no input", e.getMessage());
        }
        Assert.assertEquals(0, nullReporter.getProgress(), 0.01);
    }
}

