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
package org.assertj.core.error;


import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;


public class ShouldHaveSameHashCode_create_Test {
    @Test
    public void should_create_error_message() {
        // GIVEN
        Object actual = new ShouldHaveSameHashCode_create_Test.FixedHashCode(123);
        Object expected = new ShouldHaveSameHashCode_create_Test.FixedHashCode(456);
        // WHEN
        String message = ShouldHaveSameHashCode.shouldHaveSameHashCode(actual, expected).create(new TextDescription("Test"), StandardRepresentation.STANDARD_REPRESENTATION);
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((((("Expecting%n" + "  <FixedHashCode[code=123]>%n") + "to have the same hash code as:%n") + "  <FixedHashCode[code=456]>%n") + "but actual hash code is%n") + "  <123>%n") + "while expected hash code was:%n") + "  <456>"))));
    }

    private static class FixedHashCode {
        private int code;

        public FixedHashCode(int code) {
            this.code = code;
        }

        @Override
        public int hashCode() {
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            ShouldHaveSameHashCode_create_Test.FixedHashCode other = ((ShouldHaveSameHashCode_create_Test.FixedHashCode) (obj));
            if ((code) != (other.code))
                return false;

            return true;
        }

        @Override
        public String toString() {
            return String.format("FixedHashCode[code=%s]", code);
        }
    }
}

