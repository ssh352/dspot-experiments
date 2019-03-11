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


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeRegularFile;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertIsRegularFile_Test extends MockPathsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertIsRegularFile(info, null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_with_should_exist_error_if_actual_does_not_exist() {
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(false);
        try {
            paths.assertIsRegularFile(info, actual);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldExist.shouldExist(actual));
        }
    }

    @Test
    public void should_fail_if_target_exists_but_is_not_a_regular_file() {
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isRegularFile(actual)).thenReturn(false);
        try {
            paths.assertIsRegularFile(info, actual);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeRegularFile.shouldBeRegularFile(actual));
        }
    }

    @Test
    public void should_succeed_if_actual_is_an_existing_regular_file() {
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isRegularFile(actual)).thenReturn(true);
        paths.assertIsRegularFile(info, actual);
    }
}

