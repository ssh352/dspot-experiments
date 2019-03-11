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
package org.apache.flink.streaming.graph;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.checkpoint.MasterTriggerRestoreHook;
import org.apache.flink.runtime.checkpoint.MasterTriggerRestoreHook.Factory;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.checkpoint.WithMasterCheckpointHook;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that when sources implement {@link WithMasterCheckpointHook} the hooks are properly
 * configured in the job's checkpoint settings.
 */
@SuppressWarnings("serial")
public class WithMasterCheckpointHookConfigTest extends TestLogger {
    /**
     * This test creates a program with 4 sources (2 with master hooks, 2 without).
     * The resulting job graph must have 2 configured master hooks.
     */
    @Test
    public void testHookConfiguration() throws Exception {
        // create some sources some of which configure master hooks
        final WithMasterCheckpointHookConfigTest.TestSource source1 = new WithMasterCheckpointHookConfigTest.TestSource();
        final WithMasterCheckpointHookConfigTest.TestSourceWithHook source2 = new WithMasterCheckpointHookConfigTest.TestSourceWithHook("foo");
        final WithMasterCheckpointHookConfigTest.TestSource source3 = new WithMasterCheckpointHookConfigTest.TestSource();
        final WithMasterCheckpointHookConfigTest.TestSourceWithHook source4 = new WithMasterCheckpointHookConfigTest.TestSourceWithHook("bar");
        final MapFunction<String, String> identity = new WithMasterCheckpointHookConfigTest.Identity();
        final WithMasterCheckpointHookConfigTest.IdentityWithHook<String> identityWithHook1 = new WithMasterCheckpointHookConfigTest.IdentityWithHook<>("apple");
        final WithMasterCheckpointHookConfigTest.IdentityWithHook<String> identityWithHook2 = new WithMasterCheckpointHookConfigTest.IdentityWithHook<>("orange");
        final Set<MasterTriggerRestoreHook<?>> hooks = new HashSet<MasterTriggerRestoreHook<?>>(Arrays.asList(source2.createMasterTriggerRestoreHook(), source4.createMasterTriggerRestoreHook(), identityWithHook1.createMasterTriggerRestoreHook(), identityWithHook2.createMasterTriggerRestoreHook()));
        // we can instantiate a local environment here, because we never actually execute something
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.enableCheckpointing(500);
        env.addSource(source1).map(identity).union(env.addSource(source2).map(identity)).union(env.addSource(source3).map(identityWithHook1)).union(env.addSource(source4).map(identityWithHook2)).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<String>());
        final JobGraph jg = env.getStreamGraph().getJobGraph();
        SerializedValue<Factory[]> serializedConfiguredHooks = jg.getCheckpointingSettings().getMasterHooks();
        Assert.assertNotNull(serializedConfiguredHooks);
        Factory[] configuredHooks = serializedConfiguredHooks.deserializeValue(getClass().getClassLoader());
        Assert.assertEquals(hooks.size(), configuredHooks.length);
        // check that all hooks are contained and exist exactly once
        for (Factory f : configuredHooks) {
            MasterTriggerRestoreHook<?> hook = f.create();
            Assert.assertTrue(hooks.remove(hook));
        }
        Assert.assertTrue(hooks.isEmpty());
    }

    // -----------------------------------------------------------------------
    private static class TestHook implements MasterTriggerRestoreHook<String> {
        private final String id;

        TestHook(String id) {
            this.id = id;
        }

        @Override
        public String getIdentifier() {
            return id;
        }

        @Override
        public void reset() throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<String> triggerCheckpoint(long checkpointId, long timestamp, Executor executor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void restoreCheckpoint(long checkpointId, @Nullable
        String checkpointData) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public SimpleVersionedSerializer<String> createCheckpointDataSerializer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == (this)) || (((obj != null) && ((obj.getClass()) == (getClass()))) && (((WithMasterCheckpointHookConfigTest.TestHook) (obj)).id.equals(id)));
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    // -----------------------------------------------------------------------
    private static class TestSource implements SourceFunction<String> {
        @Override
        public void run(SourceContext<String> ctx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancel() {
        }
    }

    // -----------------------------------------------------------------------
    private static class TestSourceWithHook extends WithMasterCheckpointHookConfigTest.TestSource implements WithMasterCheckpointHook<String> {
        private final String id;

        TestSourceWithHook(String id) {
            this.id = id;
        }

        @Override
        public WithMasterCheckpointHookConfigTest.TestHook createMasterTriggerRestoreHook() {
            return new WithMasterCheckpointHookConfigTest.TestHook(id);
        }
    }

    // -----------------------------------------------------------------------
    private static class Identity<T> implements MapFunction<T, T> {
        @Override
        public T map(T value) {
            return value;
        }
    }

    // -----------------------------------------------------------------------
    private static class IdentityWithHook<T> extends WithMasterCheckpointHookConfigTest.Identity<T> implements WithMasterCheckpointHook<String> {
        private final String id;

        IdentityWithHook(String id) {
            this.id = id;
        }

        @Override
        public WithMasterCheckpointHookConfigTest.TestHook createMasterTriggerRestoreHook() {
            return new WithMasterCheckpointHookConfigTest.TestHook(id);
        }
    }
}

