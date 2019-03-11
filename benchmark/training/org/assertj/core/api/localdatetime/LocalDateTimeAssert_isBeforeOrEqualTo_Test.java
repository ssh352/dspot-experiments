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
package org.assertj.core.api.localdatetime;


import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Pawe? Stawicki
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class LocalDateTimeAssert_isBeforeOrEqualTo_Test extends LocalDateTimeAssertBaseTest {
    @Test
    public void test_isBeforeOrEqual_assertion() {
        // WHEN
        Assertions.assertThat(LocalDateTimeAssertBaseTest.BEFORE).isBeforeOrEqualTo(LocalDateTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(LocalDateTimeAssertBaseTest.REFERENCE).isBeforeOrEqualTo(LocalDateTimeAssertBaseTest.REFERENCE);
        // THEN
        LocalDateTimeAssert_isBeforeOrEqualTo_Test.verify_that_isBeforeOrEqual_assertion_fails_and_throws_AssertionError(LocalDateTimeAssertBaseTest.AFTER, LocalDateTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isBeforeOrEqual_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(LocalDateTime.of(2000, 1, 5, 3, 0, 5)).isBeforeOrEqualTo(LocalDateTime.of(1998, 1, 1, 3, 3, 3))).withMessage(String.format("%nExpecting:%n  <2000-01-05T03:00:05>%nto be before or equals to:%n  <1998-01-01T03:03:03>"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            LocalDateTime actual = null;
            assertThat(actual).isBeforeOrEqualTo(LocalDateTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_dateTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDateTime.now()).isBeforeOrEqualTo(((LocalDateTime) (null)))).withMessage("The LocalDateTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDateTime.now()).isBeforeOrEqualTo(((String) (null)))).withMessage("The String representing the LocalDateTime to compare actual with should not be null");
    }
}

