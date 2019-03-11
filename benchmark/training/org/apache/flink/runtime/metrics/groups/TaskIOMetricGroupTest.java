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
package org.apache.flink.runtime.metrics.groups;


import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.runtime.executiongraph.IOMetrics;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TaskIOMetricGroup}.
 */
public class TaskIOMetricGroupTest {
    @Test
    public void testTaskIOMetricGroup() {
        TaskMetricGroup task = UnregisteredMetricGroups.createUnregisteredTaskMetricGroup();
        TaskIOMetricGroup taskIO = task.getIOMetricGroup();
        // test counter forwarding
        Assert.assertNotNull(taskIO.getNumRecordsInCounter());
        Assert.assertNotNull(taskIO.getNumRecordsOutCounter());
        Counter c1 = new SimpleCounter();
        c1.inc(32L);
        Counter c2 = new SimpleCounter();
        c2.inc(64L);
        taskIO.reuseRecordsInputCounter(c1);
        taskIO.reuseRecordsOutputCounter(c2);
        Assert.assertEquals(32L, taskIO.getNumRecordsInCounter().getCount());
        Assert.assertEquals(64L, taskIO.getNumRecordsOutCounter().getCount());
        // test IOMetrics instantiation
        taskIO.getNumBytesInLocalCounter().inc(100L);
        taskIO.getNumBytesInRemoteCounter().inc(150L);
        taskIO.getNumBytesOutCounter().inc(250L);
        taskIO.getNumBuffersInLocalCounter().inc(1L);
        taskIO.getNumBuffersInRemoteCounter().inc(2L);
        taskIO.getNumBuffersOutCounter().inc(3L);
        IOMetrics io = taskIO.createSnapshot();
        Assert.assertEquals(32L, io.getNumRecordsIn());
        Assert.assertEquals(64L, io.getNumRecordsOut());
        Assert.assertEquals(100L, io.getNumBytesInLocal());
        Assert.assertEquals(150L, io.getNumBytesInRemote());
        Assert.assertEquals(250L, io.getNumBytesOut());
        Assert.assertEquals(1L, taskIO.getNumBuffersInLocalCounter().getCount());
        Assert.assertEquals(2L, taskIO.getNumBuffersInRemoteCounter().getCount());
        Assert.assertEquals(3L, taskIO.getNumBuffersOutCounter().getCount());
    }
}

