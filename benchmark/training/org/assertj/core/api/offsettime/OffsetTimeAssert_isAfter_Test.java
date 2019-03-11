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
package org.assertj.core.api.offsettime;


import java.time.OffsetTime;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class OffsetTimeAssert_isAfter_Test extends OffsetTimeAssertBaseTest {
    @Test
    public void test_isAfter_assertion() {
        // WHEN
        Assertions.assertThat(OffsetTimeAssertBaseTest.AFTER).isAfter(OffsetTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(OffsetTimeAssertBaseTest.AFTER).isAfter(OffsetTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        OffsetTimeAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(OffsetTimeAssertBaseTest.REFERENCE, OffsetTimeAssertBaseTest.REFERENCE);
        OffsetTimeAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(OffsetTimeAssertBaseTest.BEFORE, OffsetTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isAfter_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(parse("03:00:05.123Z")).isAfter(parse("03:00:05.123456789Z"))).withMessage(String.format(("%n" + ((("Expecting:%n" + "  <03:00:05.123Z>%n") + "to be strictly after:%n") + "  <03:00:05.123456789Z>"))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            OffsetTime actual = null;
            assertThat(actual).isAfter(OffsetTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_offsetTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(OffsetTime.now()).isAfter(((OffsetTime) (null)))).withMessage("The OffsetTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_offsetTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(OffsetTime.now()).isAfter(((String) (null)))).withMessage("The String representing the OffsetTime to compare actual with should not be null");
    }
}

