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
package org.apache.beam.runners.direct;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.util.IllegalMutationException;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ImmutabilityEnforcementFactory}.
 */
@RunWith(JUnit4.class)
public class ImmutabilityEnforcementFactoryTest implements Serializable {
    @Rule
    public transient TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    private transient ImmutabilityEnforcementFactory factory;

    private transient BundleFactory bundleFactory;

    private transient PCollection<byte[]> pcollection;

    private transient AppliedPTransform<?, ?, ?> consumer;

    @Test
    public void unchangedSucceeds() {
        WindowedValue<byte[]> element = WindowedValue.valueInGlobalWindow("bar".getBytes(StandardCharsets.UTF_8));
        CommittedBundle<byte[]> elements = bundleFactory.createBundle(pcollection).add(element).commit(Instant.now());
        ModelEnforcement<byte[]> enforcement = factory.forBundle(elements, consumer);
        enforcement.beforeElement(element);
        enforcement.afterElement(element);
        enforcement.afterFinish(elements, StepTransformResult.<byte[]>withoutHold(consumer).build(), Collections.emptyList());
    }

    @Test
    public void mutatedDuringProcessElementThrows() {
        WindowedValue<byte[]> element = WindowedValue.valueInGlobalWindow("bar".getBytes(StandardCharsets.UTF_8));
        CommittedBundle<byte[]> elements = bundleFactory.createBundle(pcollection).add(element).commit(Instant.now());
        ModelEnforcement<byte[]> enforcement = factory.forBundle(elements, consumer);
        enforcement.beforeElement(element);
        element.getValue()[0] = 'f';
        thrown.expect(IllegalMutationException.class);
        thrown.expectMessage(consumer.getFullName());
        thrown.expectMessage("illegaly mutated");
        thrown.expectMessage("Input values must not be mutated");
        enforcement.afterElement(element);
        enforcement.afterFinish(elements, StepTransformResult.<byte[]>withoutHold(consumer).build(), Collections.emptyList());
    }

    @Test
    public void mutatedAfterProcessElementFails() {
        WindowedValue<byte[]> element = WindowedValue.valueInGlobalWindow("bar".getBytes(StandardCharsets.UTF_8));
        CommittedBundle<byte[]> elements = bundleFactory.createBundle(pcollection).add(element).commit(Instant.now());
        ModelEnforcement<byte[]> enforcement = factory.forBundle(elements, consumer);
        enforcement.beforeElement(element);
        enforcement.afterElement(element);
        element.getValue()[0] = 'f';
        thrown.expect(IllegalMutationException.class);
        thrown.expectMessage(consumer.getFullName());
        thrown.expectMessage("illegaly mutated");
        thrown.expectMessage("Input values must not be mutated");
        enforcement.afterFinish(elements, StepTransformResult.<byte[]>withoutHold(consumer).build(), Collections.emptyList());
    }
}
