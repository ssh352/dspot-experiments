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
package org.assertj.core.data;


import java.math.BigInteger;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ErrorMessages;
import org.junit.jupiter.api.Test;


public class Offset_built_with_BigInteger_Test {
    @Test
    public void should_throw_error_if_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            BigInteger value = null;
            offset(value);
        });
    }

    @Test
    public void should_throw_error_if_value_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> offset(new BigInteger("-1"))).withMessage(ErrorMessages.offsetValueIsNotPositive());
    }

    @Test
    public void should_throw_error_if_value_is_zero_strict_offset() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> strictOffset(BigInteger.ZERO)).withMessage(ErrorMessages.strictOffsetValueIsNotStrictlyPositive());
    }

    @Test
    public void should_create_Offset() {
        BigInteger value = BigInteger.TEN;
        Offset<BigInteger> offset = Offset.offset(value);
        Assertions.assertThat(offset.value).isSameAs(value);
    }
}

