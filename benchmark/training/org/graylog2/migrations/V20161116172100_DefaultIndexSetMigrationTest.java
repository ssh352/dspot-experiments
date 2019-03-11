/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.migrations;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.graylog2.configuration.ElasticsearchConfiguration;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.indexset.DefaultIndexSetConfig;
import org.graylog2.indexer.indexset.DefaultIndexSetCreated;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.indexer.management.IndexManagementConfig;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.indexer.retention.RetentionStrategy;
import org.graylog2.plugin.indexer.retention.RetentionStrategyConfig;
import org.graylog2.plugin.indexer.rotation.RotationStrategy;
import org.graylog2.plugin.indexer.rotation.RotationStrategyConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20161116172100_DefaultIndexSetMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private ClusterConfigService clusterConfigService;

    private final ElasticsearchConfiguration elasticsearchConfiguration = new ElasticsearchConfiguration();

    private RotationStrategy rotationStrategy = new V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategy();

    private RetentionStrategy retentionStrategy = new V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategy();

    private Migration migration;

    @Test
    @SuppressWarnings("deprecation")
    public void upgradeCreatesDefaultIndexSet() throws Exception {
        final V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig rotationStrategyConfig = new V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig();
        final V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig retentionStrategyConfig = new V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig();
        final IndexSetConfig savedIndexSetConfig = IndexSetConfig.builder().id("id").title("title").indexPrefix("prefix").shards(1).replicas(0).rotationStrategy(rotationStrategyConfig).retentionStrategy(retentionStrategyConfig).creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC)).indexAnalyzer("standard").indexTemplateName("prefix-template").indexOptimizationMaxNumSegments(1).indexOptimizationDisabled(false).build();
        Mockito.when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create("test", "test"));
        Mockito.when(clusterConfigService.get(V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig.class)).thenReturn(rotationStrategyConfig);
        Mockito.when(clusterConfigService.get(V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig.class)).thenReturn(retentionStrategyConfig);
        Mockito.when(indexSetService.save(ArgumentMatchers.any(IndexSetConfig.class))).thenReturn(savedIndexSetConfig);
        final ArgumentCaptor<IndexSetConfig> indexSetConfigCaptor = ArgumentCaptor.forClass(IndexSetConfig.class);
        migration.upgrade();
        Mockito.verify(indexSetService).save(indexSetConfigCaptor.capture());
        Mockito.verify(clusterConfigService).write(DefaultIndexSetConfig.create("id"));
        Mockito.verify(clusterConfigService).write(DefaultIndexSetCreated.create());
        final IndexSetConfig capturedIndexSetConfig = indexSetConfigCaptor.getValue();
        assertThat(capturedIndexSetConfig.id()).isNull();
        assertThat(capturedIndexSetConfig.title()).isEqualTo("Default index set");
        assertThat(capturedIndexSetConfig.description()).isEqualTo("The Graylog default index set");
        assertThat(capturedIndexSetConfig.indexPrefix()).isEqualTo(elasticsearchConfiguration.getIndexPrefix());
        assertThat(capturedIndexSetConfig.shards()).isEqualTo(elasticsearchConfiguration.getShards());
        assertThat(capturedIndexSetConfig.replicas()).isEqualTo(elasticsearchConfiguration.getReplicas());
        assertThat(capturedIndexSetConfig.rotationStrategy()).isInstanceOf(V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig.class);
        assertThat(capturedIndexSetConfig.retentionStrategy()).isInstanceOf(V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig.class);
        assertThat(capturedIndexSetConfig.indexAnalyzer()).isEqualTo(elasticsearchConfiguration.getAnalyzer());
        assertThat(capturedIndexSetConfig.indexTemplateName()).isEqualTo(elasticsearchConfiguration.getTemplateName());
        assertThat(capturedIndexSetConfig.indexOptimizationMaxNumSegments()).isEqualTo(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments());
        assertThat(capturedIndexSetConfig.indexOptimizationDisabled()).isEqualTo(elasticsearchConfiguration.isDisableIndexOptimization());
    }

    @Test
    public void upgradeThrowsIllegalStateExceptionIfIndexManagementConfigIsMissing() throws Exception {
        Mockito.when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(null);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Couldn't find index management configuration");
        migration.upgrade();
    }

    @Test
    public void upgradeThrowsIllegalStateExceptionIfRotationStrategyIsMissing() throws Exception {
        Mockito.when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create("foobar", "test"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Couldn't retrieve rotation strategy provider for <foobar>");
        migration.upgrade();
    }

    @Test
    public void upgradeThrowsIllegalStateExceptionIfRetentionStrategyIsMissing() throws Exception {
        Mockito.when(clusterConfigService.get(V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig.class)).thenReturn(new V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig());
        Mockito.when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create("test", "foobar"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Couldn't retrieve retention strategy provider for <foobar>");
        migration.upgrade();
    }

    @Test
    public void migrationDoesNotRunAgainIfMigrationWasSuccessfulBefore() throws Exception {
        Mockito.when(clusterConfigService.get(DefaultIndexSetCreated.class)).thenReturn(DefaultIndexSetCreated.create());
        migration.upgrade();
        Mockito.verify(clusterConfigService).get(DefaultIndexSetCreated.class);
        Mockito.verifyNoMoreInteractions(clusterConfigService);
        Mockito.verifyZeroInteractions(indexSetService);
    }

    private static class StubRotationStrategy implements RotationStrategy {
        @Override
        public void rotate(IndexSet indexSet) {
        }

        @Override
        public Class<? extends RotationStrategyConfig> configurationClass() {
            return V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig.class;
        }

        @Override
        public RotationStrategyConfig defaultConfiguration() {
            return new V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategyConfig();
        }
    }

    private static class StubRotationStrategyConfig implements RotationStrategyConfig {
        @Override
        public String type() {
            return V20161116172100_DefaultIndexSetMigrationTest.StubRotationStrategy.class.getCanonicalName();
        }
    }

    private static class StubRetentionStrategy implements RetentionStrategy {
        @Override
        public void retain(IndexSet indexSet) {
        }

        @Override
        public Class<? extends RetentionStrategyConfig> configurationClass() {
            return V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig.class;
        }

        @Override
        public RetentionStrategyConfig defaultConfiguration() {
            return new V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategyConfig();
        }
    }

    private static class StubRetentionStrategyConfig implements RetentionStrategyConfig {
        @Override
        public String type() {
            return V20161116172100_DefaultIndexSetMigrationTest.StubRetentionStrategy.class.getCanonicalName();
        }
    }
}

