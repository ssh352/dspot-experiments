/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.double_;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.DoubleAssertBaseTest;
import org.junit.jupiter.api.Test;


public class DoubleAssert_isNotZero_Test extends DoubleAssertBaseTest {
    @Test
    public void should_pass_with_Double_negative_zero() {
        // GIVEN
        final Double negativeZero = -0.0;
        // THEN
        Assertions.assertThat(negativeZero).isNotZero();
    }

    @Test
    public void should_fail_with_primitive_negative_zero() {
        // GIVEN
        final double negativeZero = -0.0;
        try {
            // WHEN
            Assertions.assertThat(negativeZero).isNotZero();
        } catch (AssertionError e) {
            // THEN
            Assertions.assertThat(e).hasMessage(String.format("%nExpecting:%n <-0.0>%nnot to be equal to:%n <0.0>%n"));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_with_primitive_positive_zero() {
        // GIVEN
        final double positiveZero = 0.0;
        try {
            // WHEN
            Assertions.assertThat(positiveZero).isNotZero();
        } catch (AssertionError e) {
            // THEN
            Assertions.assertThat(e).hasMessage(String.format("%nExpecting:%n <0.0>%nnot to be equal to:%n <0.0>%n"));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_with_Double_positive_zero() {
        // GIVEN
        final Double positiveZero = 0.0;
        try {
            // WHEN
            Assertions.assertThat(positiveZero).isNotZero();
        } catch (AssertionError e) {
            // THEN
            Assertions.assertThat(e).hasMessage(String.format("%nExpecting:%n <0.0>%nnot to be equal to:%n <0.0>%n"));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

