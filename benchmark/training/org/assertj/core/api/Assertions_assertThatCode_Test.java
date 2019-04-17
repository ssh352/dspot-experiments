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
package org.assertj.core.api;


import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.error.ShouldNotHaveThrown;
import org.junit.jupiter.api.Test;


public class Assertions_assertThatCode_Test {
    @Test
    public void can_invoke_late_assertion_on_assertThatCode() {
        // Given
        ThrowingCallable boom = raisingException("boom");
        // Then
        Assertions.assertThatCode(boom).isInstanceOf(Exception.class).hasMessageContaining("boom");
    }

    @Test
    public void should_fail_when_asserting_no_exception_raised_but_exception_occurs() {
        // Given
        Exception exception = new Exception("boom");
        ThrowingCallable boom = raisingException(exception);
        // Expect
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            // When;
            assertThatCode(boom).doesNotThrowAnyException();
        }).withMessage(ShouldNotHaveThrown.shouldNotHaveThrown(exception).create());
    }

    @Test
    public void can_use_description_in_error_message() {
        // Given
        ThrowingCallable boom = raisingException("boom");
        // Expect
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThatCode(boom).as("Test").doesNotThrowAnyException()).withMessageStartingWith("[Test]");
    }

    @Test
    public void error_message_contains_stacktrace() {
        // Given
        Exception exception = new Exception("boom");
        ThrowingCallable boom = raisingException(exception);
        // Then
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThatCode(boom).doesNotThrowAnyException()).withMessageContaining("java.lang.Exception: boom").withMessageContaining("at org.assertj.core.api.Assertions_assertThatCode_Test.error_message_contains_stacktrace");
    }

    @Test
    public void should_succeed_when_asserting_no_exception_raised_and_no_exception_occurs() {
        // Given
        ThrowingCallable silent = () -> {
        };
        // Then
        Assertions.assertThatCode(silent).doesNotThrowAnyException();
    }
}
