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
package org.apache.hadoop.mapreduce.v2.api.records;


import MRProtos.TaskReportProto;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.TaskReportPBImpl;
import org.apache.hadoop.mapreduce.v2.app.MockJobs;
import org.apache.hadoop.mapreduce.v2.proto.MRProtos;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;


public class TestTaskReport {
    @Test
    public void testSetRawCounters() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        org.apache.hadoop.mapreduce.Counters rCounters = MockJobs.newCounters();
        // Set raw counters
        report.setRawCounters(rCounters);
        // Verify getCounters converts properly from raw to real
        Counters counters = report.getCounters();
        Assert.assertNotEquals(null, counters);
    }

    @Test
    public void testBuildImplicitRawCounters() {
        // Create basic class
        TaskReportPBImpl report = new TaskReportPBImpl();
        org.apache.hadoop.mapreduce.Counters rCounters = MockJobs.newCounters();
        // Set raw counters
        report.setRawCounters(rCounters);
        // Verify getProto method implicitly converts/sets real counters
        MRProtos.TaskReportProto protoVal = report.getProto();
        Assert.assertTrue(protoVal.hasCounters());
    }

    @Test
    public void testCountersOverRawCounters() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        org.apache.hadoop.mapreduce.Counters rCounters = MockJobs.newCounters();
        Counters altCounters = TypeConverter.toYarn(rCounters);
        // Set raw counters
        report.setRawCounters(rCounters);
        // Set real counters
        report.setCounters(altCounters);
        // Verify real counters has priority over raw
        Counters counters = report.getCounters();
        Assert.assertNotEquals(null, counters);
        Assert.assertNotEquals(rCounters, altCounters);
        Assert.assertEquals(counters, altCounters);
    }

    @Test
    public void testUninitializedCounters() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        // Verify properties initialized to null
        Assert.assertEquals(null, report.getCounters());
        Assert.assertEquals(null, report.getRawCounters());
    }

    @Test
    public void testSetRawCountersToNull() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        // Set raw counters to null
        report.setRawCounters(null);
        // Verify properties still null
        Assert.assertEquals(null, report.getCounters());
        Assert.assertEquals(null, report.getRawCounters());
    }

    @Test
    public void testSetCountersToNull() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        // Set raw counters to null
        report.setCounters(null);
        // Verify properties still null
        Assert.assertEquals(null, report.getCounters());
        Assert.assertEquals(null, report.getRawCounters());
    }

    @Test
    public void testSetNonNullCountersToNull() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        // Set raw counters
        org.apache.hadoop.mapreduce.Counters rCounters = MockJobs.newCounters();
        report.setRawCounters(rCounters);
        // Verify getCounters converts properly from raw to real
        Counters counters = report.getCounters();
        Assert.assertNotEquals(null, counters);
        // Clear counters to null and then verify
        report.setCounters(null);
        Assert.assertEquals(null, report.getCounters());
        Assert.assertEquals(null, report.getRawCounters());
    }

    @Test
    public void testSetNonNullRawCountersToNull() {
        // Create basic class
        TaskReport report = Records.newRecord(TaskReport.class);
        // Set raw counters
        org.apache.hadoop.mapreduce.Counters rCounters = MockJobs.newCounters();
        report.setRawCounters(rCounters);
        // Verify getCounters converts properly from raw to real
        Counters counters = report.getCounters();
        Assert.assertNotEquals(null, counters);
        // Clear counters to null and then verify
        report.setRawCounters(null);
        Assert.assertEquals(null, report.getCounters());
        Assert.assertEquals(null, report.getRawCounters());
    }
}

