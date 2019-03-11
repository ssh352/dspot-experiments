/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.aggregation.datasketches.theta;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.File;
import java.util.List;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.AggregationTestHelper;
import org.apache.druid.query.groupby.GroupByQueryConfig;
import org.apache.druid.query.select.SelectResultValue;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.query.topn.DimensionAndMetricValueExtractor;
import org.apache.druid.query.topn.TopNResultValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class SketchAggregationWithSimpleDataTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final GroupByQueryConfig config;

    private SketchModule sm;

    private File s1;

    private File s2;

    public SketchAggregationWithSimpleDataTest(GroupByQueryConfig config) {
        this.config = config;
    }

    @Test
    public void testSimpleDataIngestAndGpByQuery() throws Exception {
        try (final AggregationTestHelper gpByQueryAggregationTestHelper = AggregationTestHelper.createGroupByQueryAggregationTestHelper(sm.getJacksonModules(), config, tempFolder)) {
            Sequence seq = gpByQueryAggregationTestHelper.runQueryOnSegments(ImmutableList.of(s1, s2), SketchAggregationWithSimpleDataTest.readFileFromClasspathAsString("simple_test_data_group_by_query.json"));
            List<Row> results = seq.toList();
            Assert.assertEquals(5, results.size());
            Assert.assertEquals(ImmutableList.of(new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2014-10-19T00:00:00.000Z"), ImmutableMap.<String, Object>builder().put("product", "product_3").put("sketch_count", 38.0).put("sketchEstimatePostAgg", 38.0).put("sketchUnionPostAggEstimate", 38.0).put("sketchIntersectionPostAggEstimate", 38.0).put("sketchAnotBPostAggEstimate", 0.0).put("non_existing_col_validation", 0.0).build()), new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2014-10-19T00:00:00.000Z"), ImmutableMap.<String, Object>builder().put("product", "product_1").put("sketch_count", 42.0).put("sketchEstimatePostAgg", 42.0).put("sketchUnionPostAggEstimate", 42.0).put("sketchIntersectionPostAggEstimate", 42.0).put("sketchAnotBPostAggEstimate", 0.0).put("non_existing_col_validation", 0.0).build()), new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2014-10-19T00:00:00.000Z"), ImmutableMap.<String, Object>builder().put("product", "product_2").put("sketch_count", 42.0).put("sketchEstimatePostAgg", 42.0).put("sketchUnionPostAggEstimate", 42.0).put("sketchIntersectionPostAggEstimate", 42.0).put("sketchAnotBPostAggEstimate", 0.0).put("non_existing_col_validation", 0.0).build()), new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2014-10-19T00:00:00.000Z"), ImmutableMap.<String, Object>builder().put("product", "product_4").put("sketch_count", 42.0).put("sketchEstimatePostAgg", 42.0).put("sketchUnionPostAggEstimate", 42.0).put("sketchIntersectionPostAggEstimate", 42.0).put("sketchAnotBPostAggEstimate", 0.0).put("non_existing_col_validation", 0.0).build()), new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2014-10-19T00:00:00.000Z"), ImmutableMap.<String, Object>builder().put("product", "product_5").put("sketch_count", 42.0).put("sketchEstimatePostAgg", 42.0).put("sketchUnionPostAggEstimate", 42.0).put("sketchIntersectionPostAggEstimate", 42.0).put("sketchAnotBPostAggEstimate", 0.0).put("non_existing_col_validation", 0.0).build())), results);
        }
    }

    @Test
    public void testSimpleDataIngestAndTimeseriesQuery() throws Exception {
        AggregationTestHelper timeseriesQueryAggregationTestHelper = AggregationTestHelper.createTimeseriesQueryAggregationTestHelper(sm.getJacksonModules(), tempFolder);
        Sequence seq = timeseriesQueryAggregationTestHelper.runQueryOnSegments(ImmutableList.of(s1, s2), SketchAggregationWithSimpleDataTest.readFileFromClasspathAsString("timeseries_query.json"));
        Result<TimeseriesResultValue> result = ((Result<TimeseriesResultValue>) (Iterables.getOnlyElement(seq.toList())));
        Assert.assertEquals(DateTimes.of("2014-10-20T00:00:00.000Z"), result.getTimestamp());
        Assert.assertEquals(50.0, result.getValue().getDoubleMetric("sketch_count"), 0.01);
        Assert.assertEquals(50.0, result.getValue().getDoubleMetric("sketchEstimatePostAgg"), 0.01);
        Assert.assertEquals(50.0, result.getValue().getDoubleMetric("sketchUnionPostAggEstimate"), 0.01);
        Assert.assertEquals(50.0, result.getValue().getDoubleMetric("sketchIntersectionPostAggEstimate"), 0.01);
        Assert.assertEquals(0.0, result.getValue().getDoubleMetric("sketchAnotBPostAggEstimate"), 0.01);
        Assert.assertEquals(0.0, result.getValue().getDoubleMetric("non_existing_col_validation"), 0.01);
    }

    @Test
    public void testSimpleDataIngestAndTopNQuery() throws Exception {
        AggregationTestHelper topNQueryAggregationTestHelper = AggregationTestHelper.createTopNQueryAggregationTestHelper(sm.getJacksonModules(), tempFolder);
        Sequence seq = topNQueryAggregationTestHelper.runQueryOnSegments(ImmutableList.of(s1, s2), SketchAggregationWithSimpleDataTest.readFileFromClasspathAsString("topn_query.json"));
        Result<TopNResultValue> result = ((Result<TopNResultValue>) (Iterables.getOnlyElement(seq.toList())));
        Assert.assertEquals(DateTimes.of("2014-10-20T00:00:00.000Z"), result.getTimestamp());
        DimensionAndMetricValueExtractor value = Iterables.getOnlyElement(result.getValue().getValue());
        Assert.assertEquals(38.0, value.getDoubleMetric("sketch_count"), 0.01);
        Assert.assertEquals(38.0, value.getDoubleMetric("sketchEstimatePostAgg"), 0.01);
        Assert.assertEquals(38.0, value.getDoubleMetric("sketchUnionPostAggEstimate"), 0.01);
        Assert.assertEquals(38.0, value.getDoubleMetric("sketchIntersectionPostAggEstimate"), 0.01);
        Assert.assertEquals(0.0, value.getDoubleMetric("sketchAnotBPostAggEstimate"), 0.01);
        Assert.assertEquals(0.0, value.getDoubleMetric("non_existing_col_validation"), 0.01);
        Assert.assertEquals("product_3", value.getDimensionValue("product"));
    }

    @Test
    public void testSimpleDataIngestAndSelectQuery() throws Exception {
        SketchModule.registerSerde();
        SketchModule sm = new SketchModule();
        AggregationTestHelper selectQueryAggregationTestHelper = AggregationTestHelper.createSelectQueryAggregationTestHelper(sm.getJacksonModules(), tempFolder);
        Sequence seq = selectQueryAggregationTestHelper.runQueryOnSegments(ImmutableList.of(s1, s2), SketchAggregationWithSimpleDataTest.readFileFromClasspathAsString("select_query.json"));
        Result<SelectResultValue> result = ((Result<SelectResultValue>) (Iterables.getOnlyElement(seq.toList())));
        Assert.assertEquals(DateTimes.of("2014-10-20T00:00:00.000Z"), result.getTimestamp());
        Assert.assertEquals(100, result.getValue().getEvents().size());
        Assert.assertEquals("AgMDAAAazJMCAAAAAACAPzz9j7pWTMdROWGf15uY1nI=", result.getValue().getEvents().get(0).getEvent().get("pty_country"));
    }

    @Test
    public void testTopNQueryWithSketchConstant() throws Exception {
        AggregationTestHelper topNQueryAggregationTestHelper = AggregationTestHelper.createTopNQueryAggregationTestHelper(sm.getJacksonModules(), tempFolder);
        Sequence seq = topNQueryAggregationTestHelper.runQueryOnSegments(ImmutableList.of(s1, s2), SketchAggregationWithSimpleDataTest.readFileFromClasspathAsString("topn_query_sketch_const.json"));
        Result<TopNResultValue> result = ((Result<TopNResultValue>) (Iterables.getOnlyElement(seq.toList())));
        Assert.assertEquals(DateTimes.of("2014-10-20T00:00:00.000Z"), result.getTimestamp());
        DimensionAndMetricValueExtractor value1 = Iterables.get(result.getValue().getValue(), 0);
        Assert.assertEquals(38.0, value1.getDoubleMetric("sketch_count"), 0.01);
        Assert.assertEquals(38.0, value1.getDoubleMetric("sketchEstimatePostAgg"), 0.01);
        Assert.assertEquals(2.0, value1.getDoubleMetric("sketchEstimatePostAggForSketchConstant"), 0.01);
        Assert.assertEquals(39.0, value1.getDoubleMetric("sketchUnionPostAggEstimate"), 0.01);
        Assert.assertEquals(1.0, value1.getDoubleMetric("sketchIntersectionPostAggEstimate"), 0.01);
        Assert.assertEquals(37.0, value1.getDoubleMetric("sketchAnotBPostAggEstimate"), 0.01);
        Assert.assertEquals("product_3", value1.getDimensionValue("product"));
        DimensionAndMetricValueExtractor value2 = Iterables.get(result.getValue().getValue(), 1);
        Assert.assertEquals(42.0, value2.getDoubleMetric("sketch_count"), 0.01);
        Assert.assertEquals(42.0, value2.getDoubleMetric("sketchEstimatePostAgg"), 0.01);
        Assert.assertEquals(2.0, value2.getDoubleMetric("sketchEstimatePostAggForSketchConstant"), 0.01);
        Assert.assertEquals(42.0, value2.getDoubleMetric("sketchUnionPostAggEstimate"), 0.01);
        Assert.assertEquals(2.0, value2.getDoubleMetric("sketchIntersectionPostAggEstimate"), 0.01);
        Assert.assertEquals(40.0, value2.getDoubleMetric("sketchAnotBPostAggEstimate"), 0.01);
        Assert.assertEquals("product_1", value2.getDimensionValue("product"));
        DimensionAndMetricValueExtractor value3 = Iterables.get(result.getValue().getValue(), 2);
        Assert.assertEquals(42.0, value3.getDoubleMetric("sketch_count"), 0.01);
        Assert.assertEquals(42.0, value3.getDoubleMetric("sketchEstimatePostAgg"), 0.01);
        Assert.assertEquals(2.0, value3.getDoubleMetric("sketchEstimatePostAggForSketchConstant"), 0.01);
        Assert.assertEquals(42.0, value3.getDoubleMetric("sketchUnionPostAggEstimate"), 0.01);
        Assert.assertEquals(2.0, value3.getDoubleMetric("sketchIntersectionPostAggEstimate"), 0.01);
        Assert.assertEquals(40.0, value3.getDoubleMetric("sketchAnotBPostAggEstimate"), 0.01);
        Assert.assertEquals("product_2", value3.getDimensionValue("product"));
    }
}

