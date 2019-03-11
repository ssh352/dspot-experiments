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
package org.apache.beam.runners.dataflow.worker;


import Experiment.IntertransformIO;
import java.util.HashSet;
import java.util.Set;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.worker.ExperimentContext.Experiment;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ExperimentContextTest}.
 */
@RunWith(JUnit4.class)
public class ExperimentContextTest {
    @Test
    public void testAllExperiments() {
        Set<String> experimentNames = new HashSet<>();
        ExperimentContext ec = ExperimentContext.parseFrom(experimentNames);
        // So far nothing is enabled.
        for (Experiment experiment : Experiment.values()) {
            Assert.assertFalse(ec.isEnabled(experiment));
        }
        // Now set all experiments.
        for (Experiment experiment : Experiment.values()) {
            experimentNames.add(experiment.getName());
        }
        ec = ExperimentContext.parseFrom(experimentNames);
        // They should all be enabled now.
        for (Experiment experiment : Experiment.values()) {
            Assert.assertTrue(ec.isEnabled(experiment));
        }
    }

    @Test
    public void testInitializeFromPipelineOptions() {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.as(DataflowPipelineDebugOptions.class).setExperiments(Lists.newArrayList(IntertransformIO.getName()));
        ExperimentContext ec = ExperimentContext.parseFrom(options);
        Assert.assertTrue(ec.isEnabled(IntertransformIO));
    }

    @Test
    public void testUnsetExperimentsInPipelineOptions() {
        PipelineOptions options = PipelineOptionsFactory.create();
        ExperimentContext ec = ExperimentContext.parseFrom(options);
        Assert.assertFalse(ec.isEnabled(IntertransformIO));
    }
}

