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
package org.apache.hadoop.mapreduce.v2.hs.webapp.dao;


import JobInfo.NA;
import TaskAttemptState.SUCCEEDED;
import TaskType.REDUCE;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobACLsManager;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.hs.CompletedJob;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.hs.TestJobHistoryEntities;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJobInfo {
    @Test(timeout = 10000)
    public void testAverageMergeTime() throws IOException {
        String historyFileName = "job_1329348432655_0001-1329348443227-user-Sleep+job-1329348468601-10-1-SUCCEEDED-default.jhist";
        String confFileName = "job_1329348432655_0001_conf.xml";
        Configuration conf = new Configuration();
        JobACLsManager jobAclsMgr = new JobACLsManager(conf);
        Path fulleHistoryPath = new Path(TestJobHistoryEntities.class.getClassLoader().getResource(historyFileName).getFile());
        Path fullConfPath = new Path(TestJobHistoryEntities.class.getClassLoader().getResource(confFileName).getFile());
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        Mockito.when(info.getHistoryFile()).thenReturn(fulleHistoryPath);
        JobId jobId = MRBuilderUtils.newJobId(1329348432655L, 1, 1);
        CompletedJob completedJob = new CompletedJob(conf, jobId, fulleHistoryPath, true, "user", info, jobAclsMgr);
        JobInfo jobInfo = new JobInfo(completedJob);
        // There are 2 tasks with merge time of 45 and 55 respectively. So average
        // merge time should be 50.
        Assert.assertEquals(50L, jobInfo.getAvgMergeTime().longValue());
    }

    @Test
    public void testAverageReduceTime() {
        Job job = Mockito.mock(CompletedJob.class);
        final Task task1 = Mockito.mock(Task.class);
        final Task task2 = Mockito.mock(Task.class);
        JobId jobId = MRBuilderUtils.newJobId(1L, 1, 1);
        final TaskId taskId1 = MRBuilderUtils.newTaskId(jobId, 1, REDUCE);
        final TaskId taskId2 = MRBuilderUtils.newTaskId(jobId, 2, REDUCE);
        final TaskAttemptId taskAttemptId1 = MRBuilderUtils.newTaskAttemptId(taskId1, 1);
        final TaskAttemptId taskAttemptId2 = MRBuilderUtils.newTaskAttemptId(taskId2, 2);
        final TaskAttempt taskAttempt1 = Mockito.mock(TaskAttempt.class);
        final TaskAttempt taskAttempt2 = Mockito.mock(TaskAttempt.class);
        JobReport jobReport = Mockito.mock(JobReport.class);
        Mockito.when(taskAttempt1.getState()).thenReturn(SUCCEEDED);
        Mockito.when(taskAttempt1.getLaunchTime()).thenReturn(0L);
        Mockito.when(taskAttempt1.getShuffleFinishTime()).thenReturn(4L);
        Mockito.when(taskAttempt1.getSortFinishTime()).thenReturn(6L);
        Mockito.when(taskAttempt1.getFinishTime()).thenReturn(8L);
        Mockito.when(taskAttempt2.getState()).thenReturn(SUCCEEDED);
        Mockito.when(taskAttempt2.getLaunchTime()).thenReturn(5L);
        Mockito.when(taskAttempt2.getShuffleFinishTime()).thenReturn(10L);
        Mockito.when(taskAttempt2.getSortFinishTime()).thenReturn(22L);
        Mockito.when(taskAttempt2.getFinishTime()).thenReturn(42L);
        Mockito.when(task1.getType()).thenReturn(REDUCE);
        Mockito.when(task2.getType()).thenReturn(REDUCE);
        Mockito.when(task1.getAttempts()).thenReturn(new HashMap<TaskAttemptId, TaskAttempt>() {
            {
                put(taskAttemptId1, taskAttempt1);
            }
        });
        Mockito.when(task2.getAttempts()).thenReturn(new HashMap<TaskAttemptId, TaskAttempt>() {
            {
                put(taskAttemptId2, taskAttempt2);
            }
        });
        Mockito.when(job.getTasks()).thenReturn(new HashMap<TaskId, Task>() {
            {
                put(taskId1, task1);
                put(taskId2, task2);
            }
        });
        Mockito.when(job.getID()).thenReturn(jobId);
        Mockito.when(job.getReport()).thenReturn(jobReport);
        Mockito.when(job.getName()).thenReturn("TestJobInfo");
        Mockito.when(job.getState()).thenReturn(JobState.SUCCEEDED);
        JobInfo jobInfo = new JobInfo(job);
        Assert.assertEquals(11L, jobInfo.getAvgReduceTime().longValue());
    }

    @Test
    public void testGetStartTimeStr() {
        JobReport jobReport = Mockito.mock(JobReport.class);
        Mockito.when(jobReport.getStartTime()).thenReturn((-1L));
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getReport()).thenReturn(jobReport);
        Mockito.when(job.getName()).thenReturn("TestJobInfo");
        Mockito.when(job.getState()).thenReturn(JobState.SUCCEEDED);
        JobId jobId = MRBuilderUtils.newJobId(1L, 1, 1);
        Mockito.when(job.getID()).thenReturn(jobId);
        JobInfo jobInfo = new JobInfo(job);
        Assert.assertEquals(NA, jobInfo.getStartTimeStr());
        Date date = new Date();
        Mockito.when(jobReport.getStartTime()).thenReturn(date.getTime());
        jobInfo = new JobInfo(job);
        Assert.assertEquals(date.toString(), jobInfo.getStartTimeStr());
    }

    @Test
    public void testGetFormattedStartTimeStr() {
        JobReport jobReport = Mockito.mock(JobReport.class);
        Mockito.when(jobReport.getStartTime()).thenReturn((-1L));
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getReport()).thenReturn(jobReport);
        Mockito.when(job.getName()).thenReturn("TestJobInfo");
        Mockito.when(job.getState()).thenReturn(JobState.SUCCEEDED);
        JobId jobId = MRBuilderUtils.newJobId(1L, 1, 1);
        Mockito.when(job.getID()).thenReturn(jobId);
        DateFormat dateFormat = new SimpleDateFormat();
        JobInfo jobInfo = new JobInfo(job);
        Assert.assertEquals(NA, jobInfo.getFormattedStartTimeStr(dateFormat));
        Date date = new Date();
        Mockito.when(jobReport.getStartTime()).thenReturn(date.getTime());
        jobInfo = new JobInfo(job);
        Assert.assertEquals(dateFormat.format(date), jobInfo.getFormattedStartTimeStr(dateFormat));
    }
}

