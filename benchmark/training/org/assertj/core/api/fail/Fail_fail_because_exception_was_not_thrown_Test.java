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
package org.assertj.core.api.fail;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Fail#shouldHaveThrown(Class)} (Class)}</code>.
 *
 * @author Joel Costigliola
 */
public class Fail_fail_because_exception_was_not_thrown_Test {
    @Test
    public void should_include_message_built_with_given_exception_name() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> failBecauseExceptionWasNotThrown(.class)).withMessage("NullPointerException should have been thrown");
    }

    @Test
    public void should_include_message_built_with_given_throwable_name() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> failBecauseExceptionWasNotThrown(.class)).withMessage("OutOfMemoryError should have been thrown");
    }
}

