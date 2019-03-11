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
package org.assertj.core.internal.bigdecimals;


import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link BigDecimals#assertIsZero(AssertionInfo, BigDecimal)}</code>.
 *
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class BigDecimals_assertIsZero_Test extends BigDecimalsBaseTest {
    @Test
    public void should_succeed_since_actual_is_zero() {
        numbers.assertIsZero(TestData.someInfo(), BigDecimal.ZERO);
    }

    @Test
    public void should_fail_since_actual_is_not_zero() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsZero(someInfo(), BigDecimal.ONE)).withMessage(String.format("%nExpecting:%n <1>%nto be equal to:%n <0>%nbut was not."));
    }

    @Test
    public void should_succeed_since_actual_is_zero_whatever_custom_comparison_strategy_is() {
        numbersWithComparatorComparisonStrategy.assertIsZero(TestData.someInfo(), BigDecimal.ZERO);
    }

    @Test
    public void should_fail_since_actual_is_not_zero_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbersWithComparatorComparisonStrategy.assertIsZero(someInfo(), BigDecimal.ONE)).withMessage(String.format("%nExpecting:%n <1>%nto be equal to:%n <0>%nbut was not."));
    }
}

