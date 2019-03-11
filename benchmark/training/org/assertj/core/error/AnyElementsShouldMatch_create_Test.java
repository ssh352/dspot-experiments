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


import PredicateDescription.GIVEN;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.PredicateDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class AnyElementsShouldMatch_create_Test {
    @Test
    public void should_create_error_message() {
        // GIVEN
        ErrorMessageFactory factory = AnyElementShouldMatch.anyElementShouldMatch(Lists.newArrayList("Luke", "Yoda"), new PredicateDescription("Yoda violates some restrictions"));
        // WHEN
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (("Expecting any elements of:%n" + "  <[\"Luke\", \"Yoda\"]>%n") + "to match 'Yoda violates some restrictions' predicate but none did."))));
    }

    @Test
    public void should_create_error_message_given() {
        // GIVEN
        ErrorMessageFactory factory = AnyElementShouldMatch.anyElementShouldMatch(Lists.newArrayList("Luke", "Yoda"), GIVEN);
        // WHEN
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (("Expecting any elements of:%n" + "  <[\"Luke\", \"Yoda\"]>%n") + "to match given predicate but none did."))));
    }
}

