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
package org.assertj.core.api.object;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssertBaseTest;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectAssert#hasFieldOrProperty(String)}</code>.
 *
 * @author Libor Ondrusek
 */
public class ObjectAssert_hasFieldOrProperty_Test extends ObjectAssertBaseTest {
    public static final String FIELD_NAME = "name";// field in org.assertj.core.test.Person


    @Test
    public void should_fail_if_field_or_property_does_not_exists() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Jedi jedi = new Jedi("Yoda", "Blue");
            assertThat(jedi).hasFieldOrProperty("not_exists_in_jedi_object");
        }).withMessage(String.format("%nExpecting%n  <Yoda the Jedi>%nto have a property or a field named <\"not_exists_in_jedi_object\">"));
    }

    @Test
    public void should_fail_if_given_field_or_property_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {
            Jedi jedi = new Jedi("Yoda", "Blue");
            assertThat(jedi).hasFieldOrProperty(null);
        }).withMessage("The name of the property/field to read should not be null");
    }
}

