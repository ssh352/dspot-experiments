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
package org.assertj.core.api;


import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Assertions#assertThat(Comparable)}</code>.
 */
public class Assertions_assertThat_with_Comparable_Test {
    @Test
    public void should_create_Assert() {
        Assertions_assertThat_with_Comparable_Test.SomeComparable comparable = new Assertions_assertThat_with_Comparable_Test.SomeComparable();
        AbstractComparableAssert<?, Assertions_assertThat_with_Comparable_Test.SomeComparable> assertions = Assertions.assertThat(comparable);
        Assertions.assertThat(assertions).isNotNull();
    }

    @Test
    public void should_pass_actual() {
        Assertions_assertThat_with_Comparable_Test.SomeComparable comparable = new Assertions_assertThat_with_Comparable_Test.SomeComparable();
        AbstractComparableAssert<?, Assertions_assertThat_with_Comparable_Test.SomeComparable> assertions = Assertions.assertThat(comparable);
        Assertions.assertThat(assertions.actual).isSameAs(comparable);
    }

    private static class SomeComparable implements Comparable<Assertions_assertThat_with_Comparable_Test.SomeComparable> {
        @Override
        public int compareTo(Assertions_assertThat_with_Comparable_Test.SomeComparable o) {
            return 0;
        }
    }
}
