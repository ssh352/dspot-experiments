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


import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.TestCondition;
import org.assertj.core.data.MapEntry;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.Maps;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link ShouldContainValue#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>
 * .
 *
 * @author Nicolas Fran?ois
 */
public class ShouldContainValue_create_Test {
    @Test
    public void should_create_error_message() {
        Map<?, ?> map = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        ErrorMessageFactory factory = ShouldContainValue.shouldContainValue(map, "VeryOld");
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting:%n" + "  <{\"color\"=\"green\", \"name\"=\"Yoda\"}>%n") + "to contain value:%n") + "  <\"VeryOld\">"))));
    }

    @Test
    public void should_create_error_message_with_value_condition() {
        Map<?, ?> map = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        ErrorMessageFactory factory = ShouldContainValue.shouldContainValue(map, new TestCondition("test condition"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((("Expecting:%n" + "  <{\"color\"=\"green\", \"name\"=\"Yoda\"}>%n") + "to contain a value satisfying:%n") + "  <test condition>"))));
    }
}

