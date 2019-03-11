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
package org.apache.beam.sdk.metrics;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link MetricFiltering}.
 */
@RunWith(JUnit4.class)
public class MetricFilteringTest {
    @Test
    public void testMatchCompositeStepNameFilters() {
        // MetricsFilter with a Class-namespace + name filter + step filter.
        // Successful match.
        Assert.assertTrue(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).addStep("myStep").build(), MetricKey.create("myBigStep/myStep", MetricName.named(MetricFilteringTest.class, "myMetricName"))));
        // Unsuccessful match.
        Assert.assertFalse(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).addStep("myOtherStep").build(), MetricKey.create("myOtherStepNoMatch/myStep", MetricName.named(MetricFilteringTest.class, "myMetricName"))));
    }

    @Test
    public void testMatchStepNameFilters() {
        // MetricsFilter with a Class-namespace + name filter + step filter.
        // Successful match.
        Assert.assertTrue(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).addStep("myStep").build(), MetricKey.create("myStep", MetricName.named(MetricFilteringTest.class, "myMetricName"))));
        // Unsuccessful match.
        Assert.assertFalse(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).addStep("myOtherStep").build(), MetricKey.create("myStep", MetricName.named(MetricFilteringTest.class, "myMetricName"))));
    }

    @Test
    public void testMatchClassNamespaceFilters() {
        // MetricsFilter with a Class-namespace + name filter. Without step filter.
        // Successful match.
        Assert.assertTrue(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).build(), MetricKey.create("anyStep", MetricName.named(MetricFilteringTest.class, "myMetricName"))));
        // Unsuccessful match.
        Assert.assertFalse(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named(MetricFilteringTest.class, "myMetricName")).build(), MetricKey.create("anyStep", MetricName.named(MetricFiltering.class, "myMetricName"))));
    }

    @Test
    public void testMatchStringNamespaceFilters() {
        // MetricsFilter with a String-namespace + name filter. Without step filter.
        // Successful match.
        Assert.assertTrue(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named("myNamespace", "myMetricName")).build(), MetricKey.create("anyStep", MetricName.named("myNamespace", "myMetricName"))));
        // Unsuccessful match.
        Assert.assertFalse(MetricFiltering.matches(MetricsFilter.builder().addNameFilter(MetricNameFilter.named("myOtherNamespace", "myMetricName")).build(), MetricKey.create("anyStep", MetricName.named("myNamespace", "myMetricname"))));
    }

    @Test
    public void testMatchesSubPath() {
        Assert.assertTrue("Match of the first element", matchesSubPath("Top1/Outer1/Inner1/Bottom1", "Top1"));
        Assert.assertTrue("Match of the first elements", matchesSubPath("Top1/Outer1/Inner1/Bottom1", "Top1/Outer1"));
        Assert.assertTrue("Match of the last elements", matchesSubPath("Top1/Outer1/Inner1/Bottom1", "Inner1/Bottom1"));
        Assert.assertFalse("Substring match but no subpath match", matchesSubPath("Top1/Outer1/Inner1/Bottom1", "op1/Outer1/Inner1"));
        Assert.assertFalse("Substring match from start - but no subpath match", matchesSubPath("Top1/Outer1/Inner1/Bottom1", "Top"));
    }

    @Test
    public void testMatchesScope() {
        Assert.assertTrue(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1"));
        Assert.assertTrue(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1/Outer1/Inner1/Bottom1"));
        Assert.assertTrue(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1/Outer1"));
        Assert.assertTrue(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1/Outer1/Inner1"));
        Assert.assertFalse(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1/Inner1"));
        Assert.assertFalse(matchesScopeWithSingleFilter("Top1/Outer1/Inner1/Bottom1", "Top1/Outer1/Inn"));
    }
}

