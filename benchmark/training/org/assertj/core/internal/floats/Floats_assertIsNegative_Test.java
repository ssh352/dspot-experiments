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
package org.assertj.core.internal.floats;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Floats#assertIsNegative(AssertionInfo, Float)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Floats_assertIsNegative_Test extends FloatsBaseTest {
    @Test
    public void should_succeed_since_actual_is_negative() {
        floats.assertIsNegative(TestData.someInfo(), ((float) (-6)));
    }

    @Test
    public void should_fail_since_actual_is_not_negative() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNegative(someInfo(), 6.0F)).withMessage(String.format("%nExpecting:%n <6.0f>%nto be less than:%n <0.0f> "));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), ((float) (-6)))).withMessage(String.format("%nExpecting:%n <-6.0f>%nto be less than:%n <0.0f> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy2() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), 6.0F)).withMessage(String.format("%nExpecting:%n <6.0f>%nto be less than:%n <0.0f> when comparing values using AbsValueComparator"));
    }
}
