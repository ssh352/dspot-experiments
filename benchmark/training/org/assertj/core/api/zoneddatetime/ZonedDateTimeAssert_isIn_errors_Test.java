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
package org.assertj.core.api.zoneddatetime;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link ZonedDateTime} are already defined in assertj-core)
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class ZonedDateTimeAssert_isIn_errors_Test extends ZonedDateTimeAssertBaseTest {
    @Test
    public void test_isIn_assertion() {
        // WHEN
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.REFERENCE).isIn(ZonedDateTimeAssertBaseTest.REFERENCE.toString(), ZonedDateTimeAssertBaseTest.REFERENCE.plusNanos(1).toString());
        // THEN
        ZonedDateTimeAssert_isIn_errors_Test.verify_that_isIn_assertion_fails_and_throws_AssertionError(ZonedDateTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isIn_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            assertThat(ZonedDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC)).isIn(ZonedDateTime.of(2012, 1, 1, 3, 3, 3, 0, ZoneOffset.UTC).toString());
        }).withMessage(String.format("%nExpecting:%n <2000-01-05T03:00:05Z>%nto be in:%n <[2012-01-01T03:03:03Z]>%n"));
    }

    @Test
    public void should_fail_if_dateTimes_as_string_array_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isIn(((String[]) (null)))).withMessage("The given ZonedDateTime array should not be null");
    }

    @Test
    public void should_fail_if_dateTimes_as_string_array_parameter_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isIn(new String[0])).withMessage("The given ZonedDateTime array should not be empty");
    }
}

