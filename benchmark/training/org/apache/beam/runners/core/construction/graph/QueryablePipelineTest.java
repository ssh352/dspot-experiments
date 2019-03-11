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
package org.apache.beam.runners.core.construction.graph;


import Environments.JAVA_SDK_HARNESS_ENVIRONMENT;
import PTransformTranslation.FLATTEN_TRANSFORM_URN;
import PTransformTranslation.IMPULSE_TRANSFORM_URN;
import PTransformTranslation.PAR_DO_TRANSFORM_URN;
import PTransformTranslation.READ_TRANSFORM_URN;
import RunnerApi.Coder;
import RunnerApi.Environment;
import RunnerApi.PCollection;
import RunnerApi.WindowingStrategy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.model.pipeline.v1.RunnerApi.ParDoPayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.SideInput;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PCollectionNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link QueryablePipeline}.
 */
@RunWith(JUnit4.class)
public class QueryablePipelineTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Constructing a {@link QueryablePipeline} with components that reference absent {@link RunnerApi.PCollection PCollections} should fail.
     */
    @Test
    public void fromEmptyComponents() {
        // Not that it's hugely useful, but it shouldn't throw.
        QueryablePipeline p = QueryablePipeline.forPrimitivesIn(Components.getDefaultInstance());
        Assert.assertThat(p.getRootTransforms(), Matchers.emptyIterable());
    }

    @Test
    public void fromComponentsWithMalformedComponents() {
        Components components = Components.newBuilder().putTransforms("root", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN).build()).putOutputs("output", "output.out").build()).build();
        thrown.expect(IllegalArgumentException.class);
        QueryablePipeline.forPrimitivesIn(components).getComponents();
    }

    @Test
    public void forTransformsWithMalformedGraph() {
        Components components = Components.newBuilder().putTransforms("root", PTransform.newBuilder().putOutputs("output", "output.out").build()).putPcollections("output.out", PCollection.newBuilder().setUniqueName("output.out").build()).putTransforms("consumer", PTransform.newBuilder().putInputs("input", "output.out").build()).build();
        thrown.expect(IllegalArgumentException.class);
        // Consumer consumes a PCollection which isn't produced.
        QueryablePipeline.forTransforms(ImmutableSet.of("consumer"), components);
    }

    @Test
    public void forTransformsWithSubgraph() {
        Components components = Components.newBuilder().putTransforms("root", PTransform.newBuilder().putOutputs("output", "output.out").build()).putPcollections("output.out", PCollection.newBuilder().setUniqueName("output.out").build()).putTransforms("consumer", PTransform.newBuilder().putInputs("input", "output.out").build()).putTransforms("ignored", PTransform.newBuilder().putInputs("input", "output.out").build()).build();
        QueryablePipeline pipeline = QueryablePipeline.forTransforms(ImmutableSet.of("root", "consumer"), components);
        Assert.assertThat(pipeline.getRootTransforms(), Matchers.contains(PipelineNode.pTransform("root", components.getTransformsOrThrow("root"))));
        Set<PTransformNode> consumers = pipeline.getPerElementConsumers(PipelineNode.pCollection("output.out", components.getPcollectionsOrThrow("output.out")));
        Assert.assertThat(consumers, Matchers.contains(PipelineNode.pTransform("consumer", components.getTransformsOrThrow("consumer"))));
    }

    @Test
    public void rootTransforms() {
        Pipeline p = Pipeline.create();
        p.apply("UnboundedRead", Read.from(CountingSource.unbounded())).apply(Window.into(FixedWindows.of(Duration.millis(5L)))).apply(Count.perElement());
        p.apply("BoundedRead", Read.from(CountingSource.upTo(100L)));
        Components components = PipelineTranslation.toProto(p).getComponents();
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        Assert.assertThat(qp.getRootTransforms(), Matchers.hasSize(2));
        for (PTransformNode rootTransform : qp.getRootTransforms()) {
            Assert.assertThat("Root transforms should have no inputs", rootTransform.getTransform().getInputsCount(), Matchers.equalTo(0));
            Assert.assertThat("Only added source reads to the pipeline", rootTransform.getTransform().getSpec().getUrn(), Matchers.equalTo(READ_TRANSFORM_URN));
        }
    }

    /**
     * Tests that inputs that are only side inputs are not returned from {@link QueryablePipeline#getPerElementConsumers(PCollectionNode)} and are returned from {@link QueryablePipeline#getSideInputs(PTransformNode)}.
     */
    @Test
    public void transformWithSideAndMainInputs() {
        Pipeline p = Pipeline.create();
        org.apache.beam.sdk.values.PCollection<Long> longs = p.apply("BoundedRead", Read.from(CountingSource.upTo(100L)));
        PCollectionView<String> view = p.apply("Create", Create.of("foo")).apply("View", View.asSingleton());
        longs.apply("par_do", ParDo.of(new QueryablePipelineTest.TestFn()).withSideInputs(view).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Components components = PipelineTranslation.toProto(p).getComponents();
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        String mainInputName = getOnlyElement(PipelineNode.pTransform("BoundedRead", components.getTransformsOrThrow("BoundedRead")).getTransform().getOutputsMap().values());
        PCollectionNode mainInput = PipelineNode.pCollection(mainInputName, components.getPcollectionsOrThrow(mainInputName));
        PTransform parDoTransform = components.getTransformsOrThrow("par_do");
        String sideInputLocalName = getOnlyElement(parDoTransform.getInputsMap().entrySet().stream().filter(( entry) -> !(entry.getValue().equals(mainInputName))).map(Map.Entry::getKey).collect(Collectors.toSet()));
        String sideInputCollectionId = parDoTransform.getInputsOrThrow(sideInputLocalName);
        PCollectionNode sideInput = PipelineNode.pCollection(sideInputCollectionId, components.getPcollectionsOrThrow(sideInputCollectionId));
        PTransformNode parDoNode = PipelineNode.pTransform("par_do", components.getTransformsOrThrow("par_do"));
        SideInputReference sideInputRef = SideInputReference.of(parDoNode, sideInputLocalName, sideInput);
        Assert.assertThat(qp.getSideInputs(parDoNode), Matchers.contains(sideInputRef));
        Assert.assertThat(qp.getPerElementConsumers(mainInput), Matchers.contains(parDoNode));
        Assert.assertThat(qp.getPerElementConsumers(sideInput), Matchers.not(Matchers.contains(parDoNode)));
    }

    /**
     * Tests that inputs that are both side inputs and main inputs are returned from {@link QueryablePipeline#getPerElementConsumers(PCollectionNode)} and {@link QueryablePipeline#getSideInputs(PTransformNode)}.
     */
    @Test
    public void transformWithSameSideAndMainInput() {
        Components components = Components.newBuilder().putPcollections("read_pc", PCollection.getDefaultInstance()).putPcollections("pardo_out", PCollection.getDefaultInstance()).putTransforms("root", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN).build()).putOutputs("out", "read_pc").build()).putTransforms("multiConsumer", PTransform.newBuilder().putInputs("main_in", "read_pc").putInputs("side_in", "read_pc").putOutputs("out", "pardo_out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().putSideInputs("side_in", SideInput.getDefaultInstance()).build().toByteString()).build()).build()).build();
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        PCollectionNode multiInputPc = PipelineNode.pCollection("read_pc", components.getPcollectionsOrThrow("read_pc"));
        PTransformNode multiConsumerPT = PipelineNode.pTransform("multiConsumer", components.getTransformsOrThrow("multiConsumer"));
        SideInputReference sideInputRef = SideInputReference.of(multiConsumerPT, "side_in", multiInputPc);
        Assert.assertThat(qp.getPerElementConsumers(multiInputPc), Matchers.contains(multiConsumerPT));
        Assert.assertThat(qp.getSideInputs(multiConsumerPT), Matchers.contains(sideInputRef));
    }

    /**
     * Tests that {@link QueryablePipeline#getPerElementConsumers(PCollectionNode)} returns a
     * transform that consumes the node more than once.
     */
    @Test
    public void perElementConsumersWithConsumingMultipleTimes() {
        Pipeline p = Pipeline.create();
        org.apache.beam.sdk.values.PCollection<Long> longs = p.apply("BoundedRead", Read.from(CountingSource.upTo(100L)));
        PCollectionList.of(longs).and(longs).and(longs).apply("flatten", Flatten.pCollections());
        Components components = PipelineTranslation.toProto(p).getComponents();
        // This breaks if the way that IDs are assigned to PTransforms changes in PipelineTranslation
        String readOutput = getOnlyElement(components.getTransformsOrThrow("BoundedRead").getOutputsMap().values());
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        Set<PTransformNode> consumers = qp.getPerElementConsumers(PipelineNode.pCollection(readOutput, components.getPcollectionsOrThrow(readOutput)));
        Assert.assertThat(consumers.size(), Matchers.equalTo(1));
        Assert.assertThat(getOnlyElement(consumers).getTransform().getSpec().getUrn(), Matchers.equalTo(FLATTEN_TRANSFORM_URN));
    }

    @Test
    public void getProducer() {
        Pipeline p = Pipeline.create();
        org.apache.beam.sdk.values.PCollection<Long> longs = p.apply("BoundedRead", Read.from(CountingSource.upTo(100L)));
        PCollectionList.of(longs).and(longs).and(longs).apply("flatten", Flatten.pCollections());
        Components components = PipelineTranslation.toProto(p).getComponents();
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        String longsOutputName = getOnlyElement(PipelineNode.pTransform("BoundedRead", components.getTransformsOrThrow("BoundedRead")).getTransform().getOutputsMap().values());
        PTransformNode longsProducer = PipelineNode.pTransform("BoundedRead", components.getTransformsOrThrow("BoundedRead"));
        PCollectionNode longsOutput = PipelineNode.pCollection(longsOutputName, components.getPcollectionsOrThrow(longsOutputName));
        String flattenOutputName = getOnlyElement(PipelineNode.pTransform("flatten", components.getTransformsOrThrow("flatten")).getTransform().getOutputsMap().values());
        PTransformNode flattenProducer = PipelineNode.pTransform("flatten", components.getTransformsOrThrow("flatten"));
        PCollectionNode flattenOutput = PipelineNode.pCollection(flattenOutputName, components.getPcollectionsOrThrow(flattenOutputName));
        Assert.assertThat(qp.getProducer(longsOutput), Matchers.equalTo(longsProducer));
        Assert.assertThat(qp.getProducer(flattenOutput), Matchers.equalTo(flattenProducer));
    }

    @Test
    public void getEnvironmentWithEnvironment() {
        Pipeline p = Pipeline.create();
        org.apache.beam.sdk.values.PCollection<Long> longs = p.apply("BoundedRead", Read.from(CountingSource.upTo(100L)));
        PCollectionList.of(longs).and(longs).and(longs).apply("flatten", Flatten.pCollections());
        Components components = PipelineTranslation.toProto(p).getComponents();
        QueryablePipeline qp = QueryablePipeline.forPrimitivesIn(components);
        PTransformNode environmentalRead = PipelineNode.pTransform("BoundedRead", components.getTransformsOrThrow("BoundedRead"));
        PTransformNode nonEnvironmentalTransform = PipelineNode.pTransform("flatten", components.getTransformsOrThrow("flatten"));
        Assert.assertThat(qp.getEnvironment(environmentalRead).isPresent(), Matchers.is(true));
        Assert.assertThat(qp.getEnvironment(environmentalRead).get(), Matchers.equalTo(JAVA_SDK_HARNESS_ENVIRONMENT));
        Assert.assertThat(qp.getEnvironment(nonEnvironmentalTransform).isPresent(), Matchers.is(false));
    }

    private static class TestFn extends DoFn<Long, Long> {
        @ProcessElement
        public void process(ProcessContext ctxt) {
        }
    }

    @Test
    public void retainOnlyPrimitivesWithOnlyPrimitivesUnchanged() {
        Pipeline p = Pipeline.create();
        p.apply("Read", Read.from(CountingSource.unbounded())).apply("multi-do", ParDo.of(new QueryablePipelineTest.TestFn()).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Components originalComponents = PipelineTranslation.toProto(p).getComponents();
        Collection<String> primitiveComponents = QueryablePipeline.getPrimitiveTransformIds(originalComponents);
        Assert.assertThat(primitiveComponents, Matchers.equalTo(originalComponents.getTransformsMap().keySet()));
    }

    @Test
    public void retainOnlyPrimitivesComposites() {
        Pipeline p = Pipeline.create();
        p.apply(new org.apache.beam.sdk.transforms.PTransform<PBegin, org.apache.beam.sdk.values.PCollection<Long>>() {
            @Override
            public PCollection<Long> expand(PBegin input) {
                return input.apply(GenerateSequence.from(2L)).apply(Window.into(FixedWindows.of(Duration.standardMinutes(5L)))).apply(MapElements.into(TypeDescriptors.longs()).via(( l) -> l + 1));
            }
        });
        Components originalComponents = PipelineTranslation.toProto(p).getComponents();
        Collection<String> primitiveComponents = QueryablePipeline.getPrimitiveTransformIds(originalComponents);
        // Read, Window.Assign, ParDo. This will need to be updated if the expansions change.
        Assert.assertThat(primitiveComponents, Matchers.hasSize(3));
        for (String transformId : primitiveComponents) {
            Assert.assertThat(originalComponents.getTransformsMap(), Matchers.hasKey(transformId));
        }
    }

    /**
     * This method doesn't do any pruning for reachability, but this may not require a test.
     */
    @Test
    public void retainOnlyPrimitivesIgnoresUnreachableNodes() {
        Pipeline p = Pipeline.create();
        p.apply(new org.apache.beam.sdk.transforms.PTransform<PBegin, org.apache.beam.sdk.values.PCollection<Long>>() {
            @Override
            public PCollection<Long> expand(PBegin input) {
                return input.apply(GenerateSequence.from(2L)).apply(Window.into(FixedWindows.of(Duration.standardMinutes(5L)))).apply(MapElements.into(TypeDescriptors.longs()).via(( l) -> l + 1));
            }
        });
        Components augmentedComponents = PipelineTranslation.toProto(p).getComponents().toBuilder().putCoders("extra-coder", Coder.getDefaultInstance()).putWindowingStrategies("extra-windowing-strategy", WindowingStrategy.getDefaultInstance()).putEnvironments("extra-env", Environment.getDefaultInstance()).putPcollections("extra-pc", PCollection.getDefaultInstance()).build();
        Collection<String> primitiveComponents = QueryablePipeline.getPrimitiveTransformIds(augmentedComponents);
    }
}

