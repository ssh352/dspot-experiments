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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.internal.Strings#assertEqualsIgnoringWhitespace(org.assertj.core.api.AssertionInfo, CharSequence, CharSequence)} </code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 * @author Alexander Bischof
 * @author Dan Corder
 */
public class Strings_assertEqualsIgnoringWhitespace_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_is_null_and_expected_is_not() {
        AssertionInfo info = TestData.someInfo();
        try {
            strings.assertEqualsIgnoringWhitespace(info, null, "Luke");
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqualIgnoringWhitespace(info, null, "Luke");
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_null_and_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertEqualsIgnoringWhitespace(someInfo(), "Luke", null)).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_both_Strings_are_not_equal_ignoring_whitespace() {
        AssertionInfo info = TestData.someInfo();
        try {
            strings.assertEqualsIgnoringWhitespace(info, "Yoda", "Luke");
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqualIgnoringWhitespace(info, "Yoda", "Luke");
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}
