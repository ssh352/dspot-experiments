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
package org.assertj.core.internal;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Digests#fromHex(String)}</code>.
 *
 * @author Valeriy Vyrva
 */
public class Digests_fromHex_Test extends DigestsBaseTest {
    @Test
    public void should_fail_if_digest_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> Digests.fromHex(null)).withMessage("The digest should not be null");
    }

    @Test
    public void should_pass_if_digest_is_empty() {
        Assertions.assertThat(Digests.fromHex("")).isEmpty();
    }

    @Test
    public void should_pass_if_digest_converted_correctly() {
        Assertions.assertThat(Digests.fromHex(DigestsBaseTest.DIGEST_TEST_1_STR)).isEqualTo(DigestsBaseTest.DIGEST_TEST_1_BYTES);
    }

    @Test
    public void should_fail_if_digest_converted_incorrectly() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(Digests.fromHex(DigestsBaseTest.EXPECTED_MD5_DIGEST_STR)).isEqualTo(DigestsBaseTest.DIGEST_TEST_1_BYTES));
    }

    @Test
    public void should_pass_if_digest_length_is_not_even() {
        Assertions.assertThat(Digests.fromHex("A")).isEmpty();
        Assertions.assertThat(Digests.fromHex("AA")).containsExactly(170);
        Assertions.assertThat(Digests.fromHex("AAA")).containsExactly(170);
    }
}

