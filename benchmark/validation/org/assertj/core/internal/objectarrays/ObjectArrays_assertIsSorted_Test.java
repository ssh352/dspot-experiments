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
package org.assertj.core.internal.objectarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertIsSorted(AssertionInfo, Object[])}</code>.
 *
 * @author Joel Costigliola
 * @author Mikhail Mazursky
 */
public class ObjectArrays_assertIsSorted_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order() {
        arrays.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_with_comparable_component_type() {
        arrays.assertIsSorted(TestData.someInfo(), new String[0]);
    }

    @Test
    public void should_pass_if_actual_is_empty_with_non_comparable_component_type() {
        arrays.assertIsSorted(TestData.someInfo(), Arrays.array());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_comparable_element() {
        actual = Arrays.array("Obiwan");
        arrays.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSorted(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Yoda", "Leia");
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldBeSorted(1, actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_only_one_element_with_non_comparable_component_type() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = Arrays.array(new Object());
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveMutuallyComparableElements(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_some_elements_with_non_comparable_component_type() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = Arrays.array("bar", new Object(), "foo");
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveMutuallyComparableElements(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_some_not_mutually_comparable_elements() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = new Object[]{ "bar", new Integer(5), "foo" };
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveMutuallyComparableElements(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        actual = Arrays.array("leia", "Luke", "luke", "Vador", "Yoda");
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_with_comparable_component_type_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), new String[0]);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), Arrays.array());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_comparable_element_according_to_custom_comparison_strategy() {
        actual = Arrays.array("Obiwan");
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertIsSorted(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("LUKE", "Yoda", "Leia");
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldBeSortedAccordingToGivenComparator(1, actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_only_one_element_with_non_comparable_component_type_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = Arrays.array(new Object());
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_some_elements_with_non_comparable_component_type_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = Arrays.array("bar", new Object(), "foo");
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_some_not_mutually_comparable_elements_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] actual = new Object[]{ "bar", new Integer(5), "foo" };
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

