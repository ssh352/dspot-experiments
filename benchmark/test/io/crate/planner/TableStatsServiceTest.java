/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.planner;


import TableStats.Stats;
import TableStatsService.STATS_SERVICE_REFRESH_INTERVAL_SETTING;
import TableStatsService.STMT;
import TableStatsService.TableStatsResultReceiver;
import com.carrotsearch.hppc.ObjectObjectMap;
import io.crate.action.sql.SQLOperations;
import io.crate.action.sql.Session;
import io.crate.data.RowN;
import io.crate.metadata.RelationName;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TableStatsServiceTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testSettingsChanges() {
        // Initially disabled
        TableStatsService statsService = new TableStatsService(Settings.builder().put(STATS_SERVICE_REFRESH_INTERVAL_SETTING.getKey(), 0).build(), THREAD_POOL, clusterService, new TableStats(), Mockito.mock(SQLOperations.class, Answers.RETURNS_MOCKS.get()));
        assertThat(statsService.refreshInterval, Matchers.is(TimeValue.timeValueMinutes(0)));
        assertThat(statsService.refreshScheduledTask, Matchers.is(Matchers.nullValue()));
        // Default setting
        statsService = new TableStatsService(Settings.EMPTY, THREAD_POOL, clusterService, new TableStats(), Mockito.mock(SQLOperations.class, Answers.RETURNS_MOCKS.get()));
        assertThat(statsService.refreshInterval, Matchers.is(STATS_SERVICE_REFRESH_INTERVAL_SETTING.getDefault()));
        assertThat(statsService.refreshScheduledTask, Matchers.is(IsNull.notNullValue()));
        ClusterSettings clusterSettings = clusterService.getClusterSettings();
        // Update setting
        clusterSettings.applySettings(Settings.builder().put(STATS_SERVICE_REFRESH_INTERVAL_SETTING.getKey(), "10m").build());
        assertThat(statsService.refreshInterval, Matchers.is(TimeValue.timeValueMinutes(10)));
        assertThat(statsService.refreshScheduledTask, Matchers.is(IsNull.notNullValue()));
        // Disable
        clusterSettings.applySettings(Settings.builder().put(STATS_SERVICE_REFRESH_INTERVAL_SETTING.getKey(), 0).build());
        assertThat(statsService.refreshInterval, Matchers.is(TimeValue.timeValueMillis(0)));
        assertThat(statsService.refreshScheduledTask, Matchers.is(Matchers.nullValue()));
        // Reset setting
        clusterSettings.applySettings(Settings.builder().build());
        assertThat(statsService.refreshInterval, Matchers.is(STATS_SERVICE_REFRESH_INTERVAL_SETTING.getDefault()));
        assertThat(statsService.refreshScheduledTask, Matchers.is(IsNull.notNullValue()));
    }

    @Test
    public void testRowsToTableStatConversion() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<ObjectObjectMap<RelationName, TableStats.Stats>> statsFuture = new CompletableFuture<>();
        TableStatsService.TableStatsResultReceiver receiver = new TableStatsService.TableStatsResultReceiver(statsFuture::complete);
        receiver.setNextRow(new RowN(new Object[]{ 0L, 10L, "empty", "foo" }));
        receiver.setNextRow(new RowN(new Object[]{ 1L, 10L, "custom", "foo" }));
        receiver.setNextRow(new RowN(new Object[]{ 2L, 20L, "doc", "foo" }));
        receiver.setNextRow(new RowN(new Object[]{ 3L, 30L, "bar", "foo" }));
        receiver.allFinished(false);
        ObjectObjectMap<RelationName, TableStats.Stats> stats = statsFuture.get(10, TimeUnit.SECONDS);
        assertThat(stats.size(), Matchers.is(4));
        TableStats.Stats statValues = stats.get(new RelationName("bar", "foo"));
        assertThat(statValues.numDocs, Matchers.is(3L));
        assertThat(statValues.sizeInBytes, Matchers.is(30L));
        TableStats tableStats = new TableStats();
        tableStats.updateTableStats(stats);
        assertThat(tableStats.estimatedSizePerRow(new RelationName("bar", "foo")), Matchers.is(10L));
        assertThat(tableStats.estimatedSizePerRow(new RelationName("empty", "foo")), Matchers.is(0L));
        assertThat(tableStats.estimatedSizePerRow(new RelationName("notInCache", "foo")), Matchers.is((-1L)));
    }

    @Test
    public void testStatsQueriesCorrectly() {
        SQLOperations sqlOperations = Mockito.mock(SQLOperations.class);
        Session session = Mockito.mock(Session.class);
        Mockito.when(sqlOperations.newSystemSession()).thenReturn(session);
        TableStatsService statsService = new TableStatsService(Settings.EMPTY, THREAD_POOL, clusterService, new TableStats(), sqlOperations);
        statsService.run();
        Mockito.verify(session, Mockito.times(1)).quickExec(ArgumentMatchers.eq(STMT), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testNoUpdateIfLocalNodeNotAvailable() {
        final ClusterService clusterService = Mockito.mock(ClusterService.class);
        Mockito.when(clusterService.localNode()).thenReturn(null);
        Mockito.when(clusterService.getClusterSettings()).thenReturn(this.clusterService.getClusterSettings());
        SQLOperations sqlOperations = Mockito.mock(SQLOperations.class);
        Session session = Mockito.mock(Session.class);
        Mockito.when(sqlOperations.createSession(ArgumentMatchers.anyString(), ArgumentMatchers.anyObject(), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(session);
        TableStatsService statsService = new TableStatsService(Settings.EMPTY, THREAD_POOL, clusterService, new TableStats(), sqlOperations);
        statsService.run();
        Mockito.verify(session, Mockito.times(0)).sync();
    }
}
