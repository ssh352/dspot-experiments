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
package org.assertj.core.internal.strings;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEqualNormalizingWhitespace;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.internal.Strings#assertEqualsNormalizingWhitespace(org.assertj.core.api.AssertionInfo, CharSequence, CharSequence)} </code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 * @author Alexander Bischof
 * @author Dan Corder
 */
public class Strings_assertEqualsNormalizingWhitespace_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_is_null_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertEqualsNormalizingWhitespace(someInfo(), null, "Luke")).withMessage(String.format(ShouldBeEqualNormalizingWhitespace.shouldBeEqualNormalizingWhitespace(null, "Luke").create()));
    }

    @Test
    public void should_fail_if_actual_is_not_null_and_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertEqualsNormalizingWhitespace(someInfo(), "Luke", null)).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_both_Strings_are_not_equal_after_whitespace_is_normalized() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertEqualsNormalizingWhitespace(someInfo(), "Yoda", "Luke")).withMessage(String.format(ShouldBeEqualNormalizingWhitespace.shouldBeEqualNormalizingWhitespace("Yoda", "Luke").create()));
    }
}

