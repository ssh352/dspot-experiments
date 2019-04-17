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


import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.Description;
import org.assertj.core.internal.ComparatorBasedComparisonStrategy;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;


/**
 * Tests for
 * <code>{@link ShouldBeEqual#newAssertionError(Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Joel Costigliola (based on Tomasz Nurkiewicz ideas)
 */
public class ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test {
    private String formattedDescription = "[my test]";

    private Description description;

    private ShouldBeEqual shouldBeEqual;

    @Test
    public void should_create_AssertionError_with_message_differentiating_expected_double_and_actual_float() {
        Float actual = 42.0F;
        Double expected = 42.0;
        shouldBeEqual = ((ShouldBeEqual) (ShouldBeEqual.shouldBeEqual(actual, expected, new StandardRepresentation())));
        shouldBeEqual.descriptionFormatter = Mockito.mock(DescriptionFormatter.class);
        Mockito.when(shouldBeEqual.descriptionFormatter.format(description)).thenReturn(formattedDescription);
        AssertionError error = shouldBeEqual.newAssertionError(description, new StandardRepresentation());
        Assertions.assertThat(error).isInstanceOf(AssertionFailedError.class).hasMessage(String.format(("[my test] %n" + (((("Expecting:%n" + " <42.0f>%n") + "to be equal to:%n") + " <42.0>%n") + "but was not."))));
    }

    @Test
    public void should_create_AssertionError_with_message_differentiating_expected_and_actual_persons() {
        ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person actual = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person("Jake", 43);
        ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person expected = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person("Jake", 47);
        shouldBeEqual = ((ShouldBeEqual) (ShouldBeEqual.shouldBeEqual(actual, expected, new StandardRepresentation())));
        shouldBeEqual.descriptionFormatter = Mockito.mock(DescriptionFormatter.class);
        Mockito.when(shouldBeEqual.descriptionFormatter.format(description)).thenReturn(formattedDescription);
        AssertionError error = shouldBeEqual.newAssertionError(description, new StandardRepresentation());
        Assertions.assertThat(error).isInstanceOf(AssertionFailedError.class).hasMessage(("[my test] %n" + (((("Expecting:%n" + " <\"Person[name=Jake] (Person@%s)\">%n") + "to be equal to:%n") + " <\"Person[name=Jake] (Person@%s)\">%n") + "but was not.")), Integer.toHexString(actual.hashCode()), Integer.toHexString(expected.hashCode()));
    }

    @Test
    public void should_create_AssertionError_with_message_differentiating_expected_and_actual_persons_even_if_a_comparator_based_comparison_strategy_is_used() {
        ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person actual = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person("Jake", 43);
        ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person expected = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person("Jake", 47);
        ComparisonStrategy ageComparisonStrategy = new ComparatorBasedComparisonStrategy(new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.PersonComparator());
        shouldBeEqual = ((ShouldBeEqual) (ShouldBeEqual.shouldBeEqual(actual, expected, ageComparisonStrategy, new StandardRepresentation())));
        shouldBeEqual.descriptionFormatter = Mockito.mock(DescriptionFormatter.class);
        Mockito.when(shouldBeEqual.descriptionFormatter.format(description)).thenReturn(formattedDescription);
        AssertionError error = shouldBeEqual.newAssertionError(description, new StandardRepresentation());
        Assertions.assertThat(error).isInstanceOf(AssertionFailedError.class).hasMessage(("[my test] %n" + ((((("Expecting:%n" + " <\"Person[name=Jake] (Person@%s)\">%n") + "to be equal to:%n") + " <\"Person[name=Jake] (Person@%s)\">%n") + "when comparing values using PersonComparator%n") + "but was not.")), Integer.toHexString(actual.hashCode()), Integer.toHexString(expected.hashCode()));
    }

    @Test
    public void should_create_AssertionError_with_message_differentiating_null_and_object_with_null_toString() {
        Object actual = null;
        Object expected = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.ToStringIsNull();
        shouldBeEqual = ((ShouldBeEqual) (ShouldBeEqual.shouldBeEqual(actual, expected, new StandardRepresentation())));
        shouldBeEqual.descriptionFormatter = Mockito.mock(DescriptionFormatter.class);
        Mockito.when(shouldBeEqual.descriptionFormatter.format(description)).thenReturn(formattedDescription);
        AssertionError error = shouldBeEqual.newAssertionError(description, new StandardRepresentation());
        Assertions.assertThat(error).isInstanceOf(AssertionFailedError.class).hasMessage(("[my test] %n" + (((("Expecting:%n" + " <null>%n") + "to be equal to:%n") + " <\"null (ToStringIsNull@%s)\">%n") + "but was not.")), Integer.toHexString(expected.hashCode()));
    }

    @Test
    public void should_create_AssertionError_with_message_differentiating_object_with_null_toString_and_null() {
        Object actual = new ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.ToStringIsNull();
        Object expected = null;
        shouldBeEqual = ((ShouldBeEqual) (ShouldBeEqual.shouldBeEqual(actual, expected, new StandardRepresentation())));
        shouldBeEqual.descriptionFormatter = Mockito.mock(DescriptionFormatter.class);
        Mockito.when(shouldBeEqual.descriptionFormatter.format(description)).thenReturn(formattedDescription);
        AssertionError error = shouldBeEqual.newAssertionError(description, new StandardRepresentation());
        Assertions.assertThat(error).isInstanceOf(AssertionFailedError.class).hasMessage(("[my test] %n" + (((("Expecting:%n" + " <\"null (ToStringIsNull@%s)\">%n") + "to be equal to:%n") + " <null>%n") + "but was not.")), Integer.toHexString(actual.hashCode()));
    }

    private static class Person {
        private final String name;

        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return Strings.concat("Person[name=", name, "]");
        }
    }

    private static class PersonComparator implements Comparator<ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person> {
        @Override
        public int compare(ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person p1, ShouldBeEqual_newAssertionError_differentiating_expected_and_actual_Test.Person p2) {
            return (p1.age) - (p2.age);
        }
    }

    public static class ToStringIsNull {
        @Override
        public String toString() {
            return null;
        }
    }
}
