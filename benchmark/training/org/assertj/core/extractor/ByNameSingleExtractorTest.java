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
package org.assertj.core.extractor;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


public class ByNameSingleExtractorTest {
    private static final Employee yoda = new Employee(1L, new Name("Yoda"), 800);

    @Test
    public void should_extract_field_values_even_if_property_does_not_exist() {
        Object extractedValues = idExtractor().apply(ByNameSingleExtractorTest.yoda);
        Assertions.assertThat(extractedValues).isEqualTo(1L);
    }

    @Test
    public void should_extract_property_values_when_no_public_field_match_given_name() {
        Object extractedValues = ageExtractor().apply(ByNameSingleExtractorTest.yoda);
        Assertions.assertThat(extractedValues).isEqualTo(800);
    }

    @Test
    public void should_extract_pure_property_values() {
        Object extractedValues = adultExtractor().apply(ByNameSingleExtractorTest.yoda);
        Assertions.assertThat(extractedValues).isEqualTo(true);
    }

    @Test
    public void should_throw_error_when_no_property_nor_public_field_match_given_name() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> new ByNameSingleExtractor<Employee>("unknown").apply(yoda));
    }

    @Test
    public void should_throw_exception_when_given_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> new ByNameSingleExtractor<Employee>(null).apply(yoda)).withMessage("The name of the field/property to read should not be null");
    }

    @Test
    public void should_throw_exception_when_given_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> new ByNameSingleExtractor<Employee>("").apply(yoda)).withMessage("The name of the field/property to read should not be empty");
    }

    @Test
    public void should_fallback_to_field_if_exception_has_been_thrown_on_property_access() {
        Object extractedValue = nameExtractor().apply(new ByNameSingleExtractorTest.EmployeeWithBrokenName("Name"));
        Assertions.assertThat(extractedValue).isEqualTo(new Name("Name"));
    }

    @Test
    public void should_prefer_properties_over_fields() {
        Object extractedValue = nameExtractor().apply(new ByNameSingleExtractorTest.EmployeeWithOverriddenName("Overridden Name"));
        Assertions.assertThat(extractedValue).isEqualTo(new Name("Overridden Name"));
    }

    @Test
    public void should_throw_exception_if_property_cannot_be_extracted_due_to_runtime_exception_during_property_access() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            Employee employee = new org.assertj.core.extractor.BrokenEmployee();
            adultExtractor().apply(employee);
        });
    }

    @Test
    public void should_throw_exception_if_no_object_is_given() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> idExtractor().apply(null));
    }

    @Test
    public void should_extract_single_value_from_maps_by_key() {
        String key1 = "key1";
        String key2 = "key2";
        Map<String, Employee> map1 = new HashMap<>();
        map1.put(key1, ByNameSingleExtractorTest.yoda);
        Employee luke = new Employee(2L, new Name("Luke"), 22);
        map1.put(key2, luke);
        Map<String, Employee> map2 = new HashMap<>();
        map2.put(key1, ByNameSingleExtractorTest.yoda);
        Employee han = new Employee(3L, new Name("Han"), 31);
        map2.put(key2, han);
        List<Map<String, Employee>> maps = Arrays.asList(map1, map2);
        Assertions.assertThat(maps).extracting(key2).containsExactly(luke, han);
        Assertions.assertThat(maps).extracting(key2, Employee.class).containsExactly(luke, han);
        Assertions.assertThat(maps).extracting(key1).containsExactly(ByNameSingleExtractorTest.yoda, ByNameSingleExtractorTest.yoda);
        Assertions.assertThat(maps).extracting("bad key").containsExactly(null, null);
    }

    @Test
    public void should_extract_property_field_combinations() {
        Employee darth = new Employee(1L, new Name("Darth", "Vader"), 100);
        Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
        darth.field = luke;
        luke.field = darth;
        luke.surname = new Name("Young", "Padawan");
        Object extracted = Extractors.byName("me.field.me.field.me.field.surname.name").apply(darth);
        Assertions.assertThat(extracted).isEqualTo("Young Padawan");
    }

    @Test
    public void should_extract_property_with_barename_method() {
        ByNameSingleExtractorTest.BareOptionalIntHolder holder = new ByNameSingleExtractorTest.BareOptionalIntHolder(42);
        Assertions.assertThat(holder).extracting("value").containsExactly(OptionalInt.of(42));
    }

    @Test
    public void should_ignore_property_with_barename_method() {
        ByNameSingleExtractorTest.BareOptionalIntHolder holder = new ByNameSingleExtractorTest.BareOptionalIntHolder(42);
        Assertions.setExtractBareNamePropertyMethods(false);
        Assertions.assertThat(holder).extracting("value").containsExactly(42);
        Assertions.setExtractBareNamePropertyMethods(true);
    }

    public static class EmployeeWithBrokenName extends Employee {
        public EmployeeWithBrokenName(String name) {
            super(1L, new Name(name), 0);
        }

        @Override
        public Name getName() {
            throw new IllegalStateException();
        }
    }

    public static class EmployeeWithOverriddenName extends Employee {
        private String overriddenName;

        public EmployeeWithOverriddenName(final String overriddenName) {
            super(1L, new Name("Name"), 0);
            this.overriddenName = overriddenName;
        }

        @Override
        public Name getName() {
            return new Name(overriddenName);
        }
    }

    public static class BrokenEmployee extends Employee {
        @Override
        public boolean isAdult() {
            throw new IllegalStateException();
        }
    }

    /**
     * This style of Optional handling is emitted by Immutables code gen library.
     */
    static class BareOptionalIntHolder {
        private final Integer value;

        BareOptionalIntHolder() {
            value = null;
        }

        BareOptionalIntHolder(int value) {
            this.value = value;
        }

        public OptionalInt value() {
            return OptionalInt.of(value);
        }

        // ensure setter-like methods don't distract us
        public ByNameSingleExtractorTest.BareOptionalIntHolder value(int value) {
            throw new AssertionError("unreached");
        }
    }
}
