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
package org.assertj.core.internal.chararrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.CharArraysBaseTest;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link CharArrays#assertIsSorted(AssertionInfo, Object[])}</code>.
 *
 * @author Joel Costigliola
 */
public class CharArrays_assertIsSorted_Test extends CharArraysBaseTest {
    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order() {
        arrays.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertIsSorted(TestData.someInfo(), CharArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element() {
        arrays.assertIsSorted(TestData.someInfo(), CharArrays.arrayOf('d'));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSorted(someInfo(), ((char[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order() {
        AssertionInfo info = TestData.someInfo();
        actual = CharArrays.arrayOf('a', 'c', 'b');
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldBeSorted(1, actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), CharArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), CharArrays.arrayOf('d'));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertIsSorted(someInfo(), ((char[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = CharArrays.arrayOf('A', 'c', 'b');
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldBeSortedAccordingToGivenComparator(1, actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

