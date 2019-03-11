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
package org.assertj.core.internal.bytearrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBeEmpty;
import org.assertj.core.internal.ByteArraysBaseTest;
import org.assertj.core.test.ByteArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ByteArrays#assertNotEmpty(AssertionInfo, byte[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ByteArrays_assertNotEmpty_Test extends ByteArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertNotEmpty(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        AssertionInfo info = TestData.someInfo();
        try {
            arrays.assertNotEmpty(info, ByteArrays.emptyArray());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEmpty.shouldNotBeEmpty());
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_not_empty() {
        arrays.assertNotEmpty(TestData.someInfo(), ByteArrays.arrayOf(8));
    }
}

