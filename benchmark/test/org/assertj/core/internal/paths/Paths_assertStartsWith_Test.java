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
package org.assertj.core.internal.paths;


import java.io.IOException;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.exception.PathsException;
import org.assertj.core.error.ShouldStartWithPath;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertStartsWith_Test extends MockPathsBaseTest {
    private Path canonicalActual;

    private Path canonicalOther;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertStartsWith(info, null, other)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertStartsWith(info, actual, null)).withMessage("the expected start path should not be null");
    }

    @Test
    public void should_throw_PathsException_if_actual_cannot_be_resolved() throws IOException {
        final IOException exception = new IOException();
        Mockito.when(actual.toRealPath()).thenThrow(exception);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertStartsWith(info, actual, other)).withMessage("failed to resolve actual real path").withCause(exception);
    }

    @Test
    public void should_throw_PathsException_if_other_cannot_be_resolved() throws IOException {
        final IOException exception = new IOException();
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(other.toRealPath()).thenThrow(exception);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertStartsWith(info, actual, other)).withMessage("failed to resolve argument real path").withCause(exception);
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_other() throws IOException {
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(other.toRealPath()).thenReturn(canonicalOther);
        // This is the default, but let's make this explicit
        Mockito.when(canonicalActual.startsWith(canonicalOther)).thenReturn(false);
        try {
            paths.assertStartsWith(info, actual, other);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWithPath.shouldStartWith(actual, other));
        }
    }

    @Test
    public void should_succeed_if_actual_starts_with_other() throws IOException {
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(other.toRealPath()).thenReturn(canonicalOther);
        Mockito.when(canonicalActual.startsWith(canonicalOther)).thenReturn(true);
        paths.assertStartsWith(info, actual, other);
    }
}

