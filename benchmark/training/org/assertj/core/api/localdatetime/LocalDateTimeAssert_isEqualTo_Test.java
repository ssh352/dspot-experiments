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
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link java.time.LocalDateTime} are already defined in assertj-core)
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class LocalDateTimeAssert_isEqualTo_Test extends LocalDateTimeAssertBaseTest {
    @Test
    public void test_isEqualTo_assertion() {
        // WHEN
        Assertions.assertThat(LocalDateTimeAssertBaseTest.REFERENCE).isEqualTo(LocalDateTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        Assertions.assertThatThrownBy(() -> assertThat(LocalDateTimeAssertBaseTest.REFERENCE).isEqualTo(LocalDateTimeAssertBaseTest.REFERENCE.plusSeconds(1).toString())).isInstanceOf(AssertionError.class);
    }

    @Test
    public void test_isEqualTo_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(LocalDateTime.of(2000, 1, 5, 3, 0, 5)).isEqualTo(LocalDateTime.of(2012, 1, 1, 3, 3, 3).toString())).withMessage(String.format("%nExpecting:%n <2000-01-05T03:00:05>%nto be equal to:%n <2012-01-01T03:03:03>%nbut was not."));
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDateTime.now()).isEqualTo(((String) (null)))).withMessage("The String representing the LocalDateTime to compare actual with should not be null");
    }
}

