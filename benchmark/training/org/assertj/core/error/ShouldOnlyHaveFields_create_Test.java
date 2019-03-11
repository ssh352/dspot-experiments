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


import java.util.LinkedHashSet;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.Player;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link ShouldOnlyHaveFields#create(Description, Representation)}</code>
 *
 * @author Filip Hrisafov
 */
public class ShouldOnlyHaveFields_create_Test {
    private static final LinkedHashSet<String> EMPTY_STRING_SET = Sets.<String>newLinkedHashSet();

    @Test
    public void should_create_error_message_for_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveFields(Player.class, Sets.newLinkedHashSet("name", "team"), Sets.newLinkedHashSet("nickname"), Sets.newLinkedHashSet("address"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following public accessible fields:%n") + "  <[\"name\", \"team\"]>%n") + "fields not found:%n") + "  <[\"nickname\"]>%n") + "and fields not expected:%n") + "  <[\"address\"]>"))));
    }

    @Test
    public void should_not_display_unexpected_fields_when_there_are_none_for_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveFields(Player.class, Sets.newLinkedHashSet("name", "team"), Sets.newLinkedHashSet("nickname"), ShouldOnlyHaveFields_create_Test.EMPTY_STRING_SET);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following public accessible fields:%n") + "  <[\"name\", \"team\"]>%n") + "but could not find the following fields:%n") + "  <[\"nickname\"]>"))));
    }

    @Test
    public void should_not_display_fields_not_found_when_there_are_none_for_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveFields(Player.class, Sets.newLinkedHashSet("name", "team"), ShouldOnlyHaveFields_create_Test.EMPTY_STRING_SET, Sets.newLinkedHashSet("address"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following public accessible fields:%n") + "  <[\"name\", \"team\"]>%n") + "but the following fields were unexpected:%n") + "  <[\"address\"]>"))));
    }

    @Test
    public void should_create_error_message_for_declared_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(Player.class, Sets.newLinkedHashSet("name", "team"), Sets.newLinkedHashSet("nickname"), Sets.newLinkedHashSet("address"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following declared fields:%n") + "  <[\"name\", \"team\"]>%n") + "fields not found:%n") + "  <[\"nickname\"]>%n") + "and fields not expected:%n") + "  <[\"address\"]>"))));
    }

    @Test
    public void should_not_display_unexpected_fields_when_there_are_none_for_declared_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(Player.class, Sets.newLinkedHashSet("name", "team"), Sets.newLinkedHashSet("nickname"), ShouldOnlyHaveFields_create_Test.EMPTY_STRING_SET);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following declared fields:%n") + "  <[\"name\", \"team\"]>%n") + "but could not find the following fields:%n") + "  <[\"nickname\"]>"))));
    }

    @Test
    public void should_not_display_fields_not_found_when_there_are_none_for_declared_fields() {
        ErrorMessageFactory factory = ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(Player.class, Sets.newLinkedHashSet("name", "team"), ShouldOnlyHaveFields_create_Test.EMPTY_STRING_SET, Sets.newLinkedHashSet("address"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting%n" + "  <org.assertj.core.test.Player>%n") + "to only have the following declared fields:%n") + "  <[\"name\", \"team\"]>%n") + "but the following fields were unexpected:%n") + "  <[\"address\"]>"))));
    }
}

