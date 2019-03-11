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
package org.apache.hadoop.mapreduce.v2;


import JobState.RUNNING;
import Service.STATE.STOPPED;
import TaskAttemptState.SUCCEEDED;
import com.google.common.base.Supplier;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptStatusUpdateEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptStatusUpdateEvent.TaskAttemptStatus;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestSpeculativeExecutionWithMRApp {
    private static final int NUM_MAPPERS = 5;

    private static final int NUM_REDUCERS = 0;

    @Test
    public void testSpeculateSuccessfulWithoutUpdateEvents() throws Exception {
        Clock actualClock = SystemClock.getInstance();
        final ControlledClock clock = new ControlledClock(actualClock);
        clock.setTime(System.currentTimeMillis());
        MRApp app = new MRApp(TestSpeculativeExecutionWithMRApp.NUM_MAPPERS, TestSpeculativeExecutionWithMRApp.NUM_REDUCERS, false, "test", true, clock);
        Job job = app.submit(new Configuration(), true, true);
        app.waitForState(job, RUNNING);
        Map<TaskId, Task> tasks = job.getTasks();
        Assert.assertEquals("Num tasks is not correct", ((TestSpeculativeExecutionWithMRApp.NUM_MAPPERS) + (TestSpeculativeExecutionWithMRApp.NUM_REDUCERS)), tasks.size());
        Iterator<Task> taskIter = tasks.values().iterator();
        while (taskIter.hasNext()) {
            app.waitForState(taskIter.next(), TaskState.RUNNING);
        } 
        // Process the update events
        clock.setTime(((System.currentTimeMillis()) + 2000));
        EventHandler appEventHandler = app.getContext().getEventHandler();
        for (Map.Entry<TaskId, Task> mapTask : tasks.entrySet()) {
            for (Map.Entry<TaskAttemptId, TaskAttempt> taskAttempt : mapTask.getValue().getAttempts().entrySet()) {
                TaskAttemptStatus status = createTaskAttemptStatus(taskAttempt.getKey(), ((float) (0.8)), TaskAttemptState.RUNNING);
                TaskAttemptStatusUpdateEvent event = new TaskAttemptStatusUpdateEvent(taskAttempt.getKey(), new java.util.concurrent.atomic.AtomicReference(status));
                appEventHandler.handle(event);
            }
        }
        Random generator = new Random();
        Object[] taskValues = tasks.values().toArray();
        final Task taskToBeSpeculated = ((Task) (taskValues[generator.nextInt(taskValues.length)]));
        // Other than one random task, finish every other task.
        for (Map.Entry<TaskId, Task> mapTask : tasks.entrySet()) {
            for (Map.Entry<TaskAttemptId, TaskAttempt> taskAttempt : mapTask.getValue().getAttempts().entrySet()) {
                if ((mapTask.getKey()) != (taskToBeSpeculated.getID())) {
                    appEventHandler.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(taskAttempt.getKey(), TaskAttemptEventType.TA_DONE));
                    appEventHandler.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(taskAttempt.getKey(), TaskAttemptEventType.TA_CONTAINER_COMPLETED));
                    app.waitForState(taskAttempt.getValue(), SUCCEEDED);
                }
            }
        }
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                if ((taskToBeSpeculated.getAttempts().size()) != 2) {
                    clock.setTime(((System.currentTimeMillis()) + 1000));
                    return false;
                } else {
                    return true;
                }
            }
        }, 1000, 60000);
        // finish 1st TA, 2nd will be killed
        TaskAttempt[] ta = TestSpeculativeExecutionWithMRApp.makeFirstAttemptWin(appEventHandler, taskToBeSpeculated);
        TestSpeculativeExecutionWithMRApp.verifySpeculationMessage(app, ta);
        app.waitForState(STOPPED);
    }

    @Test
    public void testSepculateSuccessfulWithUpdateEvents() throws Exception {
        Clock actualClock = SystemClock.getInstance();
        final ControlledClock clock = new ControlledClock(actualClock);
        clock.setTime(System.currentTimeMillis());
        MRApp app = new MRApp(TestSpeculativeExecutionWithMRApp.NUM_MAPPERS, TestSpeculativeExecutionWithMRApp.NUM_REDUCERS, false, "test", true, clock);
        Job job = app.submit(new Configuration(), true, true);
        app.waitForState(job, RUNNING);
        Map<TaskId, Task> tasks = job.getTasks();
        Assert.assertEquals("Num tasks is not correct", ((TestSpeculativeExecutionWithMRApp.NUM_MAPPERS) + (TestSpeculativeExecutionWithMRApp.NUM_REDUCERS)), tasks.size());
        Iterator<Task> taskIter = tasks.values().iterator();
        while (taskIter.hasNext()) {
            app.waitForState(taskIter.next(), TaskState.RUNNING);
        } 
        // Process the update events
        clock.setTime(((System.currentTimeMillis()) + 1000));
        EventHandler appEventHandler = app.getContext().getEventHandler();
        for (Map.Entry<TaskId, Task> mapTask : tasks.entrySet()) {
            for (Map.Entry<TaskAttemptId, TaskAttempt> taskAttempt : mapTask.getValue().getAttempts().entrySet()) {
                TaskAttemptStatus status = createTaskAttemptStatus(taskAttempt.getKey(), ((float) (0.5)), TaskAttemptState.RUNNING);
                TaskAttemptStatusUpdateEvent event = new TaskAttemptStatusUpdateEvent(taskAttempt.getKey(), new java.util.concurrent.atomic.AtomicReference(status));
                appEventHandler.handle(event);
            }
        }
        Task speculatedTask = null;
        int numTasksToFinish = ((TestSpeculativeExecutionWithMRApp.NUM_MAPPERS) + (TestSpeculativeExecutionWithMRApp.NUM_REDUCERS)) - 1;
        clock.setTime(((System.currentTimeMillis()) + 1000));
        for (Map.Entry<TaskId, Task> task : tasks.entrySet()) {
            for (Map.Entry<TaskAttemptId, TaskAttempt> taskAttempt : task.getValue().getAttempts().entrySet()) {
                if (numTasksToFinish > 0) {
                    appEventHandler.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(taskAttempt.getKey(), TaskAttemptEventType.TA_DONE));
                    appEventHandler.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(taskAttempt.getKey(), TaskAttemptEventType.TA_CONTAINER_COMPLETED));
                    numTasksToFinish--;
                    app.waitForState(taskAttempt.getValue(), SUCCEEDED);
                } else {
                    // The last task is chosen for speculation
                    TaskAttemptStatus status = createTaskAttemptStatus(taskAttempt.getKey(), ((float) (0.75)), TaskAttemptState.RUNNING);
                    speculatedTask = task.getValue();
                    TaskAttemptStatusUpdateEvent event = new TaskAttemptStatusUpdateEvent(taskAttempt.getKey(), new java.util.concurrent.atomic.AtomicReference(status));
                    appEventHandler.handle(event);
                }
            }
        }
        clock.setTime(((System.currentTimeMillis()) + 15000));
        for (Map.Entry<TaskId, Task> task : tasks.entrySet()) {
            for (Map.Entry<TaskAttemptId, TaskAttempt> taskAttempt : task.getValue().getAttempts().entrySet()) {
                if ((taskAttempt.getValue().getState()) != (TaskAttemptState.SUCCEEDED)) {
                    TaskAttemptStatus status = createTaskAttemptStatus(taskAttempt.getKey(), ((float) (0.75)), TaskAttemptState.RUNNING);
                    TaskAttemptStatusUpdateEvent event = new TaskAttemptStatusUpdateEvent(taskAttempt.getKey(), new java.util.concurrent.atomic.AtomicReference(status));
                    appEventHandler.handle(event);
                }
            }
        }
        final Task speculatedTaskConst = speculatedTask;
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                if ((speculatedTaskConst.getAttempts().size()) != 2) {
                    clock.setTime(((System.currentTimeMillis()) + 1000));
                    return false;
                } else {
                    return true;
                }
            }
        }, 1000, 60000);
        TaskAttempt[] ta = TestSpeculativeExecutionWithMRApp.makeFirstAttemptWin(appEventHandler, speculatedTask);
        TestSpeculativeExecutionWithMRApp.verifySpeculationMessage(app, ta);
        app.waitForState(STOPPED);
    }
}

