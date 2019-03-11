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
package org.apache.druid.query.materializedview;


import TestDerbyConnector.DerbyConnectorRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.druid.client.BatchServerInventoryView;
import org.apache.druid.client.BrokerServerView;
import org.apache.druid.client.DruidServer;
import org.apache.druid.curator.CuratorTestBase;
import org.apache.druid.indexing.materializedview.DerivativeDataSourceMetadata;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.metadata.IndexerSQLMetadataStorageCoordinator;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.server.initialization.ZkPathsConfig;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class DatasourceOptimizerTest extends CuratorTestBase {
    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private TestDerbyConnector derbyConnector;

    private DerivativeDataSourceManager derivativesManager;

    private DruidServer druidServer;

    private ObjectMapper jsonMapper;

    private ZkPathsConfig zkPathsConfig;

    private DataSourceOptimizer optimizer;

    private MaterializedViewConfig viewConfig;

    private IndexerSQLMetadataStorageCoordinator metadataStorageCoordinator;

    private BatchServerInventoryView baseView;

    private BrokerServerView brokerServerView;

    @Test(timeout = 60000L)
    public void testOptimize() throws InterruptedException {
        // insert datasource metadata
        String dataSource = "derivative";
        String baseDataSource = "base";
        Set<String> dims = Sets.newHashSet("dim1", "dim2", "dim3");
        Set<String> metrics = Sets.newHashSet("cost");
        DerivativeDataSourceMetadata metadata = new DerivativeDataSourceMetadata(baseDataSource, dims, metrics);
        metadataStorageCoordinator.insertDataSourceMetadata(dataSource, metadata);
        // insert base datasource segments
        List<Boolean> baseResult = Lists.transform(ImmutableList.of("2011-04-01/2011-04-02", "2011-04-02/2011-04-03", "2011-04-03/2011-04-04", "2011-04-04/2011-04-05", "2011-04-05/2011-04-06"), ( interval) -> {
            final DataSegment segment = createDataSegment("base", interval, "v1", Lists.newArrayList("dim1", "dim2", "dim3", "dim4"), (1024 * 1024));
            try {
                metadataStorageCoordinator.announceHistoricalSegments(Sets.newHashSet(segment));
                announceSegmentForServer(druidServer, segment, zkPathsConfig, jsonMapper);
            } catch (IOException e) {
                return false;
            }
            return true;
        });
        // insert derivative segments
        List<Boolean> derivativeResult = Lists.transform(ImmutableList.of("2011-04-01/2011-04-02", "2011-04-02/2011-04-03", "2011-04-03/2011-04-04"), ( interval) -> {
            final DataSegment segment = createDataSegment("derivative", interval, "v1", Lists.newArrayList("dim1", "dim2", "dim3"), 1024);
            try {
                metadataStorageCoordinator.announceHistoricalSegments(Sets.newHashSet(segment));
                announceSegmentForServer(druidServer, segment, zkPathsConfig, jsonMapper);
            } catch (IOException e) {
                return false;
            }
            return true;
        });
        Assert.assertFalse(baseResult.contains(false));
        Assert.assertFalse(derivativeResult.contains(false));
        derivativesManager.start();
        while (DerivativeDataSourceManager.getAllDerivatives().isEmpty()) {
            TimeUnit.SECONDS.sleep(1L);
        } 
        // build user query
        TopNQuery userQuery = new TopNQueryBuilder().dataSource("base").granularity(QueryRunnerTestHelper.allGran).dimension("dim1").metric("cost").threshold(4).intervals("2011-04-01/2011-04-06").aggregators(Collections.singletonList(new LongSumAggregatorFactory("cost", "cost"))).build();
        List<Query> expectedQueryAfterOptimizing = Lists.newArrayList(new TopNQueryBuilder().dataSource("derivative").granularity(QueryRunnerTestHelper.allGran).dimension("dim1").metric("cost").threshold(4).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.of("2011-04-01/2011-04-04")))).aggregators(Collections.singletonList(new LongSumAggregatorFactory("cost", "cost"))).build(), new TopNQueryBuilder().dataSource("base").granularity(QueryRunnerTestHelper.allGran).dimension("dim1").metric("cost").threshold(4).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.of("2011-04-04/2011-04-06")))).aggregators(Collections.singletonList(new LongSumAggregatorFactory("cost", "cost"))).build());
        Assert.assertEquals(expectedQueryAfterOptimizing, optimizer.optimize(userQuery));
        derivativesManager.stop();
    }
}

