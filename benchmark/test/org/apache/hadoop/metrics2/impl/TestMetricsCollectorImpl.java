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
package org.apache.hadoop.metrics2.impl;


import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.hadoop.metrics2.filter.TestPatternFilter;
import org.junit.Assert;
import org.junit.Test;


public class TestMetricsCollectorImpl {
    @Test
    public void recordBuilderShouldNoOpIfFiltered() {
        SubsetConfiguration fc = new ConfigBuilder().add("p.exclude", "foo").subset("p");
        MetricsCollectorImpl mb = new MetricsCollectorImpl();
        mb.setRecordFilter(TestPatternFilter.newGlobFilter(fc));
        MetricsRecordBuilderImpl rb = mb.addRecord("foo");
        rb.tag(info("foo", ""), "value").addGauge(info("g0", ""), 1);
        Assert.assertEquals("no tags", 0, rb.tags().size());
        Assert.assertEquals("no metrics", 0, rb.metrics().size());
        Assert.assertNull("null record", rb.getRecord());
        Assert.assertEquals("no records", 0, mb.getRecords().size());
    }

    @Test
    public void testPerMetricFiltering() {
        SubsetConfiguration fc = new ConfigBuilder().add("p.exclude", "foo").subset("p");
        MetricsCollectorImpl mb = new MetricsCollectorImpl();
        mb.setMetricFilter(TestPatternFilter.newGlobFilter(fc));
        MetricsRecordBuilderImpl rb = mb.addRecord("foo");
        rb.tag(info("foo", ""), "").addCounter(info("c0", ""), 0).addGauge(info("foo", ""), 1);
        Assert.assertEquals("1 tag", 1, rb.tags().size());
        Assert.assertEquals("1 metric", 1, rb.metrics().size());
        Assert.assertEquals("expect foo tag", "foo", rb.tags().get(0).name());
        Assert.assertEquals("expect c0", "c0", rb.metrics().get(0).name());
    }
}

