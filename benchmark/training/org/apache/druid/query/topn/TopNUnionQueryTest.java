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
package org.apache.druid.query.topn;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.segment.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TopNUnionQueryTest {
    private static final Closer resourceCloser = Closer.create();

    private final QueryRunner runner;

    public TopNUnionQueryTest(QueryRunner runner) {
        this.runner = runner;
    }

    @Test
    public void testTopNUnionQuery() {
        TopNQuery query = new TopNQueryBuilder().dataSource(QueryRunnerTestHelper.unionDataSource).granularity(QueryRunnerTestHelper.allGran).dimension(QueryRunnerTestHelper.marketDimension).metric(QueryRunnerTestHelper.dependentPostAggMetric).threshold(4).intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Arrays.asList(QueryRunnerTestHelper.addRowsIndexConstant, QueryRunnerTestHelper.dependentPostAgg, QueryRunnerTestHelper.hyperUniqueFinalizingPostAgg)).build();
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put(QueryRunnerTestHelper.marketDimension, "total_market").put("rows", 744L).put("index", 862719.3151855469).put("addRowsIndexConstant", 863464.3151855469).put(QueryRunnerTestHelper.dependentPostAggMetric, 864209.3151855469).put("uniques", QueryRunnerTestHelper.UNIQUES_2).put("maxIndex", 1743.9217529296875).put("minIndex", 792.3260498046875).put(QueryRunnerTestHelper.hyperUniqueFinalizingPostAggMetric, ((QueryRunnerTestHelper.UNIQUES_2) + 1.0)).build(), ImmutableMap.<String, Object>builder().put(QueryRunnerTestHelper.marketDimension, "upfront").put("rows", 744L).put("index", 768184.4240722656).put("addRowsIndexConstant", 768929.4240722656).put(QueryRunnerTestHelper.dependentPostAggMetric, 769674.4240722656).put("uniques", QueryRunnerTestHelper.UNIQUES_2).put("maxIndex", 1870.06103515625).put("minIndex", 545.9906005859375).put(QueryRunnerTestHelper.hyperUniqueFinalizingPostAggMetric, ((QueryRunnerTestHelper.UNIQUES_2) + 1.0)).build(), ImmutableMap.<String, Object>builder().put(QueryRunnerTestHelper.marketDimension, "spot").put("rows", 3348L).put("index", 382426.28929138184).put("addRowsIndexConstant", 385775.28929138184).put(QueryRunnerTestHelper.dependentPostAggMetric, 389124.28929138184).put("uniques", QueryRunnerTestHelper.UNIQUES_9).put(QueryRunnerTestHelper.hyperUniqueFinalizingPostAggMetric, ((QueryRunnerTestHelper.UNIQUES_9) + 1.0)).put("maxIndex", 277.2735290527344).put("minIndex", 59.02102279663086).build()))));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }
}

