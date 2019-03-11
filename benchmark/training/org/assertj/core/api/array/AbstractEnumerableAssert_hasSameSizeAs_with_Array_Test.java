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
package org.assertj.core.api.array;


import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.api.AbstractEnumerableAssert#hasSameSizeAs(Object)}</code>.
 *
 * @author Joel Costigliola
 */
public class AbstractEnumerableAssert_hasSameSizeAs_with_Array_Test {
    @Test
    public void should_pass_if_actual_primitive_array_has_same_size_as_other_object_array() {
        assertThat(new byte[]{ 1, 2 }).hasSameSizeAs(new Byte[]{ 2, 3 });
        assertThat(new int[]{ 1, 2 }).hasSameSizeAs(new String[]{ "1", "2" });
    }

    @Test
    public void should_pass_if_actual_primitive_array_has_same_size_as_other_primitive_array() {
        assertThat(new byte[]{ 1, 2 }).hasSameSizeAs(new byte[]{ 2, 3 });
        assertThat(new byte[]{ 1, 2 }).hasSameSizeAs(new int[]{ 2, 3 });
    }

    @Test
    public void should_pass_if_actual_object_array_has_same_size_as_other_object_array() {
        assertThat(new String[]{ "1", "2" }).hasSameSizeAs(new Byte[]{ 2, 3 });
        assertThat(new String[]{ "1", "2" }).hasSameSizeAs(new String[]{ "1", "2" });
    }

    @Test
    public void should_pass_if_actual_object_array_has_same_size_as_other_primitive_array() {
        assertThat(new String[]{ "1", "2" }).hasSameSizeAs(new byte[]{ 2, 3 });
        assertThat(new String[]{ "1", "2" }).hasSameSizeAs(new int[]{ 2, 3 });
    }

    @Test
    public void should_fail_if_actual_is_null() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            final byte[] actual = null;
            assertThat(actual).hasSameSizeAs(new byte[]{ 2, 3 });
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_not_an_array() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(new byte[]{ 1, 2 }).hasSameSizeAs("a string")).withMessage(String.format("%nExpecting an array but was:<\"a string\">"));
    }

    @Test
    public void should_fail_if_size_of_actual_has_same_as_other_array() {
        final byte[] actual = new byte[]{ 1, 2 };
        final byte[] other = new byte[]{ 1, 2, 3 };
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasSameSizeAs(other)).withMessage(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, actual.length, other.length).create());
    }
}

