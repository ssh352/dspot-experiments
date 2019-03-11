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
package org.apache.beam.runners.core.construction;


import ModelCoders.LENGTH_PREFIX_CODER_URN;
import java.io.IOException;
import org.apache.beam.model.pipeline.v1.RunnerApi.Coder;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.MessageWithComponents;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.runners.core.construction.ModelCoders.KvCoderComponents;
import org.apache.beam.runners.core.construction.ModelCoders.WindowedValueCoderComponents;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.LengthPrefixCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow.IntervalWindowCoder;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.KV;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ModelCoders}.
 */
@RunWith(JUnit4.class)
public class ModelCodersTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void windowedValueCoderComponentsToConstructor() throws IOException {
        FullWindowedValueCoder<Iterable<KV<String, Integer>>> javaCoder = FullWindowedValueCoder.of(IterableCoder.of(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of())), IntervalWindowCoder.of());
        MessageWithComponents coderAndComponents = CoderTranslation.toProto(javaCoder);
        WindowedValueCoderComponents windowedValueCoderComponents = ModelCoders.getWindowedValueCoderComponents(coderAndComponents.getCoder());
        Coder windowedCoder = ModelCoders.windowedValueCoder(windowedValueCoderComponents.elementCoderId(), windowedValueCoderComponents.windowCoderId());
        Assert.assertThat(windowedCoder, Matchers.equalTo(coderAndComponents.getCoder()));
    }

    @Test
    public void windowedValueCoderComponentsWrongUrn() {
        thrown.expect(IllegalArgumentException.class);
        ModelCoders.getWindowedValueCoderComponents(Coder.newBuilder().setSpec(SdkFunctionSpec.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(LENGTH_PREFIX_CODER_URN))).build());
    }

    @Test
    public void windowedValueCoderComponentsNoUrn() {
        thrown.expect(IllegalArgumentException.class);
        ModelCoders.getWindowedValueCoderComponents(Coder.newBuilder().setSpec(SdkFunctionSpec.getDefaultInstance()).build());
    }

    @Test
    public void kvCoderComponentsToConstructor() throws IOException {
        KvCoder<byte[], Iterable<Long>> javaCoder = KvCoder.of(ByteArrayCoder.of(), IterableCoder.of(LengthPrefixCoder.of(VarLongCoder.of())));
        MessageWithComponents coderAndComponents = CoderTranslation.toProto(javaCoder);
        KvCoderComponents kvCoderComponents = ModelCoders.getKvCoderComponents(coderAndComponents.getCoder());
        Coder kvCoder = ModelCoders.kvCoder(kvCoderComponents.keyCoderId(), kvCoderComponents.valueCoderId());
        Assert.assertThat(kvCoder, Matchers.equalTo(coderAndComponents.getCoder()));
    }

    @Test
    public void kvCoderComponentsWrongUrn() {
        thrown.expect(IllegalArgumentException.class);
        ModelCoders.getKvCoderComponents(Coder.newBuilder().setSpec(SdkFunctionSpec.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(LENGTH_PREFIX_CODER_URN))).build());
    }

    @Test
    public void kvCoderComponentsNoUrn() {
        thrown.expect(IllegalArgumentException.class);
        ModelCoders.getKvCoderComponents(Coder.newBuilder().setSpec(SdkFunctionSpec.getDefaultInstance()).build());
    }
}

