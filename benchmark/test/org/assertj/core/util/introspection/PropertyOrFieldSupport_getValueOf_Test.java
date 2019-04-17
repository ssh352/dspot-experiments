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
package org.assertj.core.util.introspection;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.junit.jupiter.api.Test;


public class PropertyOrFieldSupport_getValueOf_Test {
    private static final Employee yoda = new Employee(1L, new Name("Yoda"), 800);

    private PropertyOrFieldSupport propertyOrFieldSupport;

    @Test
    public void should_extract_property_value() {
        Object value = propertyOrFieldSupport.getValueOf("age", PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(value).isEqualTo(800);
    }

    @Test
    public void should_extract_property_with_no_corresponding_field() {
        Object value = propertyOrFieldSupport.getValueOf("adult", PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(value).isEqualTo(true);
    }

    @Test
    public void should_prefer_properties_over_fields() {
        Object extractedValue = propertyOrFieldSupport.getValueOf("name", employeeWithOverriddenName("Overridden Name"));
        Assertions.assertThat(extractedValue).isEqualTo(new Name("Overridden Name"));
    }

    @Test
    public void should_extract_public_field_values_as_no_property_matches_given_name() {
        Object value = propertyOrFieldSupport.getValueOf("id", PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(value).isEqualTo(1L);
    }

    @Test
    public void should_extract_private_field_values_as_no_property_matches_given_name() {
        Object value = propertyOrFieldSupport.getValueOf("city", PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(value).isEqualTo("New York");
    }

    @Test
    public void should_fallback_to_field_if_exception_has_been_thrown_on_property_access() {
        Object extractedValue = propertyOrFieldSupport.getValueOf("name", employeeWithBrokenName("Name"));
        Assertions.assertThat(extractedValue).isEqualTo(new Name("Name"));
    }

    @Test
    public void should_return_null_if_one_of_nested_property_or_field_value_is_null() {
        Object value = propertyOrFieldSupport.getValueOf("surname.first", PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(value).isNull();
    }

    @Test
    public void should_extract_nested_property_field_combinations() {
        Employee darth = new Employee(1L, new Name("Darth", "Vader"), 100);
        Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
        darth.field = luke;
        luke.field = darth;
        luke.surname = new Name("Young", "Padawan");
        Object value = propertyOrFieldSupport.getValueOf("me.field.me.field.me.field.surname.name", darth);
        Assertions.assertThat(value).isEqualTo("Young Padawan");
    }

    @Test
    public void should_throw_error_when_no_property_nor_field_match_given_name() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> propertyOrFieldSupport.getValueOf("unknown", yoda));
    }

    @Test
    public void should_throw_error_when_no_property_nor_public_field_match_given_name_if_extraction_is_limited_to_public_fields() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            propertyOrFieldSupport = new PropertyOrFieldSupport(new PropertySupport(), FieldSupport.EXTRACTION_OF_PUBLIC_FIELD_ONLY);
            propertyOrFieldSupport.getValueOf("city", yoda);
        });
    }

    @Test
    public void should_throw_exception_when_given_property_or_field_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> propertyOrFieldSupport.getValueOf(null, yoda)).withMessage("The name of the property/field to read should not be null");
    }

    @Test
    public void should_throw_exception_when_given_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> propertyOrFieldSupport.getValueOf("", yoda)).withMessage("The name of the property/field to read should not be empty");
    }

    @Test
    public void should_throw_exception_if_property_cannot_be_extracted_due_to_runtime_exception_during_property_access() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> propertyOrFieldSupport.getValueOf("adult", brokenEmployee()));
    }

    @Test
    public void should_throw_exception_if_no_object_is_given() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> propertyOrFieldSupport.getValueOf("name", null));
    }

    @Test
    public void should_extract_single_value_from_maps_by_key() {
        String key1 = "key1";
        String key2 = "key2";
        Map<String, Employee> map1 = new HashMap<>();
        map1.put(key1, PropertyOrFieldSupport_getValueOf_Test.yoda);
        Employee luke = new Employee(2L, new Name("Luke"), 22);
        map1.put(key2, luke);
        Map<String, Employee> map2 = new HashMap<>();
        map2.put(key1, PropertyOrFieldSupport_getValueOf_Test.yoda);
        Employee han = new Employee(3L, new Name("Han"), 31);
        map2.put(key2, han);
        List<Map<String, Employee>> maps = Arrays.asList(map1, map2);
        Assertions.assertThat(maps).extracting(key2).containsExactly(luke, han);
        Assertions.assertThat(maps).extracting(key2, Employee.class).containsExactly(luke, han);
        Assertions.assertThat(maps).extracting(key1).containsExactly(PropertyOrFieldSupport_getValueOf_Test.yoda, PropertyOrFieldSupport_getValueOf_Test.yoda);
        Assertions.assertThat(maps).extracting("bad key").containsExactly(null, null);
    }
}
