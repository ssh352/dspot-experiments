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
package org.assertj.core.internal.longarrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSizeLessThanOrEqualTo;
import org.assertj.core.internal.LongArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class LongArrays_assertHasSizeLessThanOrEqualTo_Test extends LongArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSizeLessThanOrEqualTo(someInfo(), null, 6)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_less_than_or_equal_to_boundary() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSizeLessThanOrEqualTo(someInfo(), actual, 1)).withMessage(ShouldHaveSizeLessThanOrEqualTo.shouldHaveSizeLessThanOrEqualTo(actual, actual.length, 1).create());
    }

    @Test
    public void should_pass_if_size_of_actual_is_less_than_boundary() {
        arrays.assertHasSizeLessThanOrEqualTo(TestData.someInfo(), actual, 4);
    }

    @Test
    public void should_pass_if_size_of_actual_is_equal_to_boundary() {
        arrays.assertHasSizeLessThanOrEqualTo(TestData.someInfo(), actual, actual.length);
    }
}
