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
package org.assertj.core.internal.objects;


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEqualByComparingOnlyGivenFields;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.internal.TypeComparators;
import org.assertj.core.test.CartoonCharacter;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Jedi;
import org.assertj.core.test.Name;
import org.assertj.core.test.Person;
import org.assertj.core.test.Player;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Objects_assertIsEqualToComparingOnlyGivenFields_Test extends ObjectsBaseTest {
    @Test
    public void should_pass_when_selected_fields_are_equal() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Green");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name", "lightSaberColor");
    }

    @Test
    public void should_pass_when_selected_fields_and_nested_fields_accessed_with_getters_are_equal() {
        Player rose = new Player(new Name("Derrick", "Rose"), "Chicago Bulls");
        Player jalen = new Player(new Name("Derrick", "Coleman"), "Chicago Bulls");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), rose, jalen, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "team", "name.first");
    }

    @Test
    public void should_pass_when_selected_fields_and_nested_public_fields_are_equal() {
        Player rose = new Player(new Name("Derrick", "Rose"), "Chicago Bulls");
        rose.nickname = new Name("Crazy", "Dunks");
        Player jalen = new Player(new Name("Derrick", "Coleman"), "Chicago Bulls");
        jalen.nickname = new Name("Crazy", "Defense");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), rose, jalen, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "team", "nickname.first");
    }

    @Test
    public void should_pass_when_mixed_nested_field_properties_compared_values_are_equal() {
        Player rose = new Player(new Name("Derrick", "Rose"), "Chicago Bulls");
        rose.nickname = new Name("Crazy", "Dunks");
        Player jalen = new Player(new Name("Jalen", "Rose"), "Chicago Bulls");
        jalen.nickname = new Name("Crazy", "Defense");
        // nickname is a field and Name#first is a property
        // name is a property and Name#first is a property
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), rose, jalen, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name.last", "nickname.first");
    }

    @Test
    public void should_pass_even_if_non_accepted_fields_differ() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
    }

    @Test
    public void should_pass_when_field_value_is_null() {
        Jedi actual = new Jedi("Yoda", null);
        Jedi other = new Jedi("Yoda", null);
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name", "lightSaberColor");
    }

    @Test
    public void should_pass_when_fields_are_equal_even_if_objects_types_differ() {
        CartoonCharacter actual = new CartoonCharacter("Homer Simpson");
        Person other = new Person("Homer Simpson");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Jedi other = new Jedi("Yoda", "Green");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertIsEqualToComparingOnlyGivenFields(someInfo(), null, other, noFieldComparators(), defaultTypeComparators(), "name", "lightSaberColor")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_some_selected_field_values_differ() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        try {
            objects.assertIsEqualToComparingOnlyGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name", "lightSaberColor");
        } catch (AssertionError err) {
            List<Object> expected = Lists.newArrayList(((Object) ("Blue")));
            List<Object> rejected = Lists.newArrayList(((Object) ("Green")));
            Mockito.verify(failures).failure(info, ShouldBeEqualByComparingOnlyGivenFields.shouldBeEqualComparingOnlyGivenFields(actual, Lists.newArrayList("lightSaberColor"), rejected, expected, Lists.newArrayList("name", "lightSaberColor")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_some_inherited_field_values_differ() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Luke", "Green");
        try {
            objects.assertIsEqualToComparingOnlyGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name", "lightSaberColor");
        } catch (AssertionError err) {
            List<Object> expected = Lists.newArrayList(((Object) ("Luke")));
            List<Object> rejected = Lists.newArrayList(((Object) ("Yoda")));
            Mockito.verify(failures).failure(info, ShouldBeEqualByComparingOnlyGivenFields.shouldBeEqualComparingOnlyGivenFields(actual, Lists.newArrayList("name"), rejected, expected, Lists.newArrayList("name", "lightSaberColor")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_one_of_actual_field_to_compare_can_not_be_found_in_the_other_object() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            Jedi actual = new Jedi("Yoda", "Green");
            Employee other = new Employee();
            objects.assertIsEqualToComparingOnlyGivenFields(someInfo(), actual, other, noFieldComparators(), defaultTypeComparators(), "lightSaberColor");
        }).withMessageContaining("Can't find any field or property with name 'lightSaberColor'");
    }

    @Test
    public void should_fail_when_selected_field_does_not_exist() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            Jedi actual = new Jedi("Yoda", "Green");
            Jedi other = new Jedi("Yoda", "Blue");
            objects.assertIsEqualToComparingOnlyGivenFields(someInfo(), actual, other, noFieldComparators(), defaultTypeComparators(), "age");
        }).withMessage(String.format(("%nCan't find any field or property with name 'age'.%n" + ((("Error when introspecting properties was :%n" + "- No getter for property 'age' in org.assertj.core.test.Jedi %n") + "Error when introspecting fields was :%n") + "- Unable to obtain the value of the field <'age'> from <Yoda the Jedi>"))));
    }

    @Test
    public void should_fail_when_selected_field_is_not_accessible_and_private_field_use_is_forbidden() {
        Assertions.setAllowComparingPrivateFields(false);
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            Jedi actual = new Jedi("Yoda", "Green");
            Jedi other = new Jedi("Yoda", "Blue");
            objects.assertIsEqualToComparingOnlyGivenFields(someInfo(), actual, other, noFieldComparators(), defaultTypeComparators(), "strangeNotReadablePrivateField");
        }).withMessageContaining("Can't find any field or property with name 'strangeNotReadablePrivateField'.");
        Assertions.setAllowComparingPrivateFields(true);
    }

    @Test
    public void should_pass_when_selected_field_is_private_and_private_field_use_is_allowed() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        objects.assertIsEqualToComparingOnlyGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "strangeNotReadablePrivateField");
    }
}

