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
package org.graylog2.periodical;


import com.google.common.collect.ImmutableMap;
import javax.inject.Provider;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.IndexSetRegistry;
import org.graylog2.indexer.NoTargetIndexException;
import org.graylog2.indexer.cluster.Cluster;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.notifications.NotificationService;
import org.graylog2.plugin.indexer.rotation.RotationStrategy;
import org.graylog2.plugin.indexer.rotation.RotationStrategyConfig;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.shared.system.activities.NullActivityWriter;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class IndexRotationThreadTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Indices indices;

    @Mock
    private Cluster cluster;

    @Mock
    private NodeId nodeId;

    @Mock
    private IndexSetRegistry indexSetRegistry;

    @Test
    public void testPerformRotation() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = new IndexRotationThreadTest.RotationStrategyProvider() {
            @Override
            public void doRotate(IndexSet indexSet) {
                indexSet.cycle();
            }
        };
        final IndexRotationThread rotationThread = new IndexRotationThread(notificationService, indices, indexSetRegistry, cluster, new NullActivityWriter(), nodeId, ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build());
        Mockito.when(indexSetConfig.rotationStrategyClass()).thenReturn("strategy");
        rotationThread.checkForRotation(indexSet);
        Mockito.verify(indexSet, Mockito.times(1)).cycle();
    }

    @Test
    public void testDoNotPerformRotation() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = new IndexRotationThreadTest.RotationStrategyProvider();
        final IndexRotationThread rotationThread = new IndexRotationThread(notificationService, indices, indexSetRegistry, cluster, new NullActivityWriter(), nodeId, ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build());
        Mockito.when(indexSetConfig.rotationStrategyClass()).thenReturn("strategy");
        rotationThread.checkForRotation(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
    }

    @Test
    public void testDoNotPerformRotationIfClusterIsDown() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = Mockito.spy(new IndexRotationThreadTest.RotationStrategyProvider());
        Mockito.when(cluster.isConnected()).thenReturn(false);
        final IndexRotationThread rotationThread = new IndexRotationThread(notificationService, indices, indexSetRegistry, cluster, new NullActivityWriter(), nodeId, ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build());
        rotationThread.doRun();
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.verify(provider, Mockito.never()).get();
    }

    private static class RotationStrategyProvider implements Provider<RotationStrategy> {
        @Override
        public RotationStrategy get() {
            return new RotationStrategy() {
                @Override
                public void rotate(IndexSet indexSet) {
                    doRotate(indexSet);
                }

                @Override
                public RotationStrategyConfig defaultConfiguration() {
                    return null;
                }

                @Override
                public Class<? extends RotationStrategyConfig> configurationClass() {
                    return null;
                }
            };
        }

        public void doRotate(IndexSet indexSet) {
        }
    }
}

