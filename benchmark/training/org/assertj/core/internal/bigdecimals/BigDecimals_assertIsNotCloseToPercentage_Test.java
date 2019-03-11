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
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldNotBeEqualWithinPercentage;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class BigDecimals_assertIsNotCloseToPercentage_Test extends BigDecimalsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsNotCloseToPercentage(someInfo(), null, BigDecimal.ONE, withPercentage(1))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseToPercentage(someInfo(), BigDecimal.ONE, null, withPercentage(1)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseToPercentage(someInfo(), BigDecimal.ONE, BigDecimal.ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> numbers.assertIsNotCloseToPercentage(someInfo(), BigDecimal.ONE, BigDecimal.ZERO, withPercentage((-1))));
    }

    @Test
    public void should_fail_if_actual_is_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsNotCloseToPercentage(TestData.someInfo(), BigDecimal.ONE, BigDecimal.TEN, Percentage.withPercentage(100));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinPercentage.shouldNotBeEqualWithinPercentage(BigDecimal.ONE, BigDecimal.TEN, Assertions.withinPercentage(100), BigDecimal.TEN.subtract(BigDecimal.ONE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

