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
package org.assertj.core.internal.booleans;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEqual;
import org.assertj.core.internal.BooleansBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Booleans#assertEqual(AssertionInfo, Boolean, boolean)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Booleans_assertEqual_Test extends BooleansBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> booleans.assertEqual(someInfo(), null, true)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_booleans_are_equal() {
        booleans.assertEqual(TestData.someInfo(), Boolean.TRUE, true);
    }

    @Test
    public void should_fail_if_booleans_are_not_equal() {
        AssertionInfo info = TestData.someInfo();
        boolean expected = false;
        try {
            booleans.assertEqual(info, Boolean.TRUE, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(Boolean.TRUE, expected, info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

