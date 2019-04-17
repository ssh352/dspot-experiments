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


public class OnFieldsComparator_compare_Test {
    private OnFieldsComparator onFieldsComparator;

    @Test
    public void should_return_true_if_both_Objects_are_null() {
        Assertions.assertThat(onFieldsComparator.compare(null, null)).isZero();
    }

    @Test
    public void should_return_are_not_equal_if_first_Object_is_null_and_second_is_not() {
        Assertions.assertThat(onFieldsComparator.compare(null, new OnFieldsComparator_compare_Test.DarthVader("I like you", "I'll kill you"))).isNotZero();
    }

    @Test
    public void should_return_are_not_equal_if_second_Object_is_null_and_first_is_not() {
        Assertions.assertThat(onFieldsComparator.compare(new OnFieldsComparator_compare_Test.DarthVader("I like you", "I'll kill you"), null)).isNotZero();
    }

    @Test
    public void should_return_true_if_given_fields_are_equal() {
        OnFieldsComparator_compare_Test.DarthVader actual = new OnFieldsComparator_compare_Test.DarthVader("I like you", "I'll kill you");
        OnFieldsComparator_compare_Test.DarthVader other = new OnFieldsComparator_compare_Test.DarthVader("I like you", "I like you");
        Assertions.assertThat(onFieldsComparator.compare(actual, other)).isZero();
    }

    @Test
    public void should_return_false_if_given_fields_are_not_equal() {
        OnFieldsComparator_compare_Test.DarthVader actual = new OnFieldsComparator_compare_Test.DarthVader("I like you", "I'll kill you");
        OnFieldsComparator_compare_Test.DarthVader other = new OnFieldsComparator_compare_Test.DarthVader("I'll kill you", "I'll kill you");
        Assertions.assertThat(onFieldsComparator.compare(actual, other)).isNotZero();
    }

    @Test
    public void should_return_false_if_Objects_do_not_have_the_same_properties() {
        Assertions.assertThat(onFieldsComparator.compare(new OnFieldsComparator_compare_Test.DarthVader("I like you", "I'll kill you"), 2)).isNotZero();
    }

    public static class DarthVader {
        public final String telling;

        public final String thinking;

        public DarthVader(String telling, String thinking) {
            this.telling = telling;
            this.thinking = thinking;
        }
    }
}
