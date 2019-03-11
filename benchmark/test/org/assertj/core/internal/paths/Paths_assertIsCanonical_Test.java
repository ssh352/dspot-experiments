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
import org.assertj.core.error.ShouldBeCanonicalPath;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertIsCanonical_Test extends MockPathsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertIsCanonical(info, null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_PathsException_on_io_error() throws IOException {
        final IOException exception = new IOException();
        Mockito.when(actual.toRealPath()).thenThrow(exception);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertIsCanonical(info, actual)).withMessage("failed to resolve actual real path").withCause(exception);
    }

    @Test
    public void should_fail_if_actual_real_path_differs_from_actual() throws IOException {
        final Path other = Mockito.mock(Path.class);
        Mockito.when(actual.toRealPath()).thenReturn(other);
        try {
            paths.assertIsCanonical(info, actual);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeCanonicalPath.shouldBeCanonicalPath(actual));
        }
    }

    @Test
    public void should_succeed_if_actual_real_path_is_same_as_actual() throws IOException {
        Mockito.when(actual.toRealPath()).thenReturn(actual);
        paths.assertIsCanonical(info, actual);
    }
}

