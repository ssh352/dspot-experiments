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
package org.assertj.core.internal.shortarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainSequence;
import org.assertj.core.internal.ShortArraysBaseTest;
import org.assertj.core.test.ShortArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ShortArrays#assertContainsSequence(AssertionInfo, short[], short[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ShortArrays_assertContainsSequence_Test extends ShortArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsSequence(someInfo(), null, arrayOf(8))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsSequence(someInfo(), actual, null)).withMessage(valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = ShortArrays.emptyArray();
        arrays.assertContainsSequence(TestData.someInfo(), actual, ShortArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsSequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 8, 10, 12, 20, 22 };
        try {
            arrays.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_whole_sequence() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20 };
        try {
            arrays.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_sequence() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20, 22 };
        try {
            arrays.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_sequence() {
        arrays.assertContainsSequence(TestData.someInfo(), actual, ShortArrays.arrayOf(6, 8));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        arrays.assertContainsSequence(TestData.someInfo(), actual, ShortArrays.arrayOf(6, 8, 10, 12));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSequence(someInfo(), null, arrayOf((-8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSequence(someInfo(), actual, null)).withMessage(valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, -8, 10, 12, 20, 22 };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSequence.shouldContainSequence(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_whole_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20 };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSequence.shouldContainSequence(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20, 22 };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSequence.shouldContainSequence(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsSequence(TestData.someInfo(), actual, ShortArrays.arrayOf(6, (-8)));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsSequence(TestData.someInfo(), actual, ShortArrays.arrayOf(6, (-8), 10, 12));
    }
}
