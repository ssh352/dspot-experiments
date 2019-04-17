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


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveAtLeastOneElementOfType;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class ObjectArrays_assertHasAtLeastOneElementOfType_Test extends ObjectArraysBaseTest {
    private static final Object[] array = new Object[]{ 6, "Hello" };

    @Test
    public void should_pass_if_actual_has_one_element_of_the_expected_type() {
        arrays.assertHasAtLeastOneElementOfType(TestData.someInfo(), ObjectArrays_assertHasAtLeastOneElementOfType_Test.array, Integer.class);
        arrays.assertHasAtLeastOneElementOfType(TestData.someInfo(), ObjectArrays_assertHasAtLeastOneElementOfType_Test.array, String.class);
        arrays.assertHasAtLeastOneElementOfType(TestData.someInfo(), ObjectArrays_assertHasAtLeastOneElementOfType_Test.array, Object.class);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasAtLeastOneElementOfType(someInfo(), null, .class)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_exception_if_expected_type_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertHasAtLeastOneElementOfType(someInfo(), array, null));
    }

    @Test
    public void should_fail_if_no_elements_in_actual_belongs_to_the_expected_type() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasAtLeastOneElementOfType(someInfo(), array, .class)).withMessage(ShouldHaveAtLeastOneElementOfType.shouldHaveAtLeastOneElementOfType(ObjectArrays_assertHasAtLeastOneElementOfType_Test.array, Float.class).create());
    }
}
