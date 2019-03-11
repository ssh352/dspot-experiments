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
package org.apache.beam.runners.spark.translation.streaming;


import ParDo.MultiOutput;
import Pipeline.PipelineVisitor.Defaults;
import SparkRunner.Evaluator;
import TransformHierarchy.Node;
import java.util.List;
import org.apache.beam.runners.spark.ReuseSparkContextRule;
import org.apache.beam.runners.spark.SparkPipelineOptions;
import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.runners.spark.io.CreateStream;
import org.apache.beam.runners.spark.translation.Dataset;
import org.apache.beam.runners.spark.translation.EvaluationContext;
import org.apache.beam.runners.spark.translation.SparkContextFactory;
import org.apache.beam.runners.spark.translation.TransformTranslator;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.runners.TransformHierarchy;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PValue;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * A test suite that tests tracking of the streaming sources created an {@link org.apache.beam.runners.spark.translation.streaming.UnboundedDataset}.
 */
public class TrackStreamingSourcesTest {
    @Rule
    public ReuseSparkContextRule reuseContext = ReuseSparkContextRule.yes();

    private static final transient SparkPipelineOptions options = PipelineOptionsFactory.create().as(SparkPipelineOptions.class);

    @Test
    public void testTrackSingle() {
        TrackStreamingSourcesTest.options.setRunner(SparkRunner.class);
        JavaSparkContext jsc = SparkContextFactory.getSparkContext(TrackStreamingSourcesTest.options);
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, new org.apache.spark.streaming.Duration(TrackStreamingSourcesTest.options.getBatchIntervalMillis()));
        Pipeline p = Pipeline.create(TrackStreamingSourcesTest.options);
        CreateStream<Integer> emptyStream = CreateStream.of(VarIntCoder.of(), Duration.millis(TrackStreamingSourcesTest.options.getBatchIntervalMillis())).emptyBatch();
        p.apply(emptyStream).apply(ParDo.of(new TrackStreamingSourcesTest.PassthroughFn()));
        p.traverseTopologically(new TrackStreamingSourcesTest.StreamingSourceTracker(jssc, p, MultiOutput.class, 0));
        Assert.assertThat(TrackStreamingSourcesTest.StreamingSourceTracker.numAssertions, IsEqual.equalTo(1));
    }

    @Test
    public void testTrackFlattened() {
        TrackStreamingSourcesTest.options.setRunner(SparkRunner.class);
        JavaSparkContext jsc = SparkContextFactory.getSparkContext(TrackStreamingSourcesTest.options);
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, new org.apache.spark.streaming.Duration(TrackStreamingSourcesTest.options.getBatchIntervalMillis()));
        Pipeline p = Pipeline.create(TrackStreamingSourcesTest.options);
        CreateStream<Integer> queueStream1 = CreateStream.of(VarIntCoder.of(), Duration.millis(TrackStreamingSourcesTest.options.getBatchIntervalMillis())).emptyBatch();
        CreateStream<Integer> queueStream2 = CreateStream.of(VarIntCoder.of(), Duration.millis(TrackStreamingSourcesTest.options.getBatchIntervalMillis())).emptyBatch();
        PCollection<Integer> pcol1 = p.apply(queueStream1);
        PCollection<Integer> pcol2 = p.apply(queueStream2);
        PCollection<Integer> flattened = PCollectionList.of(pcol1).and(pcol2).apply(Flatten.pCollections());
        flattened.apply(ParDo.of(new TrackStreamingSourcesTest.PassthroughFn()));
        p.traverseTopologically(new TrackStreamingSourcesTest.StreamingSourceTracker(jssc, p, MultiOutput.class, 0, 1));
        Assert.assertThat(TrackStreamingSourcesTest.StreamingSourceTracker.numAssertions, IsEqual.equalTo(1));
    }

    private static class PassthroughFn<T> extends DoFn<T, T> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element());
        }
    }

    private static class StreamingSourceTracker extends Pipeline.PipelineVisitor.Defaults {
        private final EvaluationContext ctxt;

        private final Evaluator evaluator;

        private final Class<? extends PTransform> transformClassToAssert;

        private final Integer[] expected;

        private static int numAssertions = 0;

        private StreamingSourceTracker(JavaStreamingContext jssc, Pipeline pipeline, Class<? extends PTransform> transformClassToAssert, Integer... expected) {
            this.ctxt = new EvaluationContext(jssc.sparkContext(), pipeline, TrackStreamingSourcesTest.options, jssc);
            this.evaluator = new SparkRunner.Evaluator(new StreamingTransformTranslator.Translator(new TransformTranslator.Translator()), ctxt);
            this.transformClassToAssert = transformClassToAssert;
            this.expected = expected;
        }

        private void assertSourceIds(List<Integer> streamingSources) {
            (TrackStreamingSourcesTest.StreamingSourceTracker.numAssertions)++;
            Assert.assertThat(streamingSources, Matchers.containsInAnyOrder(expected));
        }

        @Override
        public void enterPipeline(Pipeline p) {
            super.enterPipeline(p);
            evaluator.enterPipeline(p);
        }

        @Override
        public CompositeBehavior enterCompositeTransform(TransformHierarchy.Node node) {
            return evaluator.enterCompositeTransform(node);
        }

        @Override
        public void visitPrimitiveTransform(TransformHierarchy.Node node) {
            PTransform transform = node.getTransform();
            if ((transform.getClass()) == (transformClassToAssert)) {
                AppliedPTransform<?, ?, ?> appliedTransform = node.toAppliedPTransform(getPipeline());
                ctxt.setCurrentTransform(appliedTransform);
                // noinspection unchecked
                Dataset dataset = ctxt.borrowDataset(((PTransform<? extends PValue, ?>) (transform)));
                assertSourceIds(((UnboundedDataset<?>) (dataset)).getStreamSources());
                ctxt.setCurrentTransform(null);
            } else {
                evaluator.visitPrimitiveTransform(node);
            }
        }

        @Override
        public void leavePipeline(Pipeline p) {
            super.leavePipeline(p);
            evaluator.leavePipeline(p);
        }
    }
}

