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
package org.assertj.core.internal.objects;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeIn;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static java.util.Arrays.asList;


/**
 * Tests for <code>{@link Objects#assertIsIn(AssertionInfo, Object, Object[])}</code>.
 *
 * @author Joel Costigliola
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class Objects_assertIsIn_with_array_Test extends ObjectsBaseTest {
    private static String[] values;

    @Test
    public void should_throw_error_if_array_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] array = null;
            objects.assertIsIn(someInfo(), "Yoda", array);
        }).withMessage(ErrorMessages.arrayIsNull());
    }

    @Test
    public void should_throw_error_if_array_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> objects.assertIsIn(someInfo(), "Yoda", emptyArray())).withMessage(ErrorMessages.arrayIsEmpty());
    }

    @Test
    public void should_pass_if_actual_is_in_array() {
        objects.assertIsIn(TestData.someInfo(), "Yoda", Objects_assertIsIn_with_array_Test.values);
    }

    @Test
    public void should_pass_if_actual_is_null_and_array_contains_null() {
        objects.assertIsIn(TestData.someInfo(), null, Arrays.array("Yoda", null));
    }

    @Test
    public void should_fail_if_actual_is_not_in_array() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertIsIn(info, "Luke", Objects_assertIsIn_with_array_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeIn.shouldBeIn("Luke", asList(Objects_assertIsIn_with_array_Test.values)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_in_array_according_to_custom_comparison_strategy() {
        objectsWithCustomComparisonStrategy.assertIsIn(TestData.someInfo(), "YODA", Objects_assertIsIn_with_array_Test.values);
    }

    @Test
    public void should_fail_if_actual_is_not_in_array_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            objectsWithCustomComparisonStrategy.assertIsIn(info, "Luke", Objects_assertIsIn_with_array_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeIn.shouldBeIn("Luke", asList(Objects_assertIsIn_with_array_Test.values), customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

