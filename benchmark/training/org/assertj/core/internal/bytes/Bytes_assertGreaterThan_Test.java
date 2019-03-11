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
package org.assertj.core.internal.bytes;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeGreater;
import org.assertj.core.internal.BytesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Bytes#assertGreaterThan(AssertionInfo, Byte, byte)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Bytes_assertGreaterThan_Test extends BytesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertGreaterThan(someInfo(), null, ((byte) (8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_greater_than_other() {
        bytes.assertGreaterThan(TestData.someInfo(), ((byte) (8)), ((byte) (6)));
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other() {
        try {
            bytes.assertGreaterThan(TestData.someInfo(), ((byte) (6)), ((byte) (6)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(TestData.someInfo(), ShouldBeGreater.shouldBeGreater(((byte) (6)), ((byte) (6))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            bytes.assertGreaterThan(info, ((byte) (6)), ((byte) (8)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater(((byte) (6)), ((byte) (8))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        bytesWithAbsValueComparisonStrategy.assertGreaterThan(TestData.someInfo(), ((byte) (-8)), ((byte) (6)));
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        try {
            bytesWithAbsValueComparisonStrategy.assertGreaterThan(TestData.someInfo(), ((byte) (-6)), ((byte) (6)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(TestData.someInfo(), ShouldBeGreater.shouldBeGreater(((byte) (-6)), ((byte) (6)), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            bytesWithAbsValueComparisonStrategy.assertGreaterThan(info, ((byte) (-6)), ((byte) (8)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater(((byte) (-6)), ((byte) (8)), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

