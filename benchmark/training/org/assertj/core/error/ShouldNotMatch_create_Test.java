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
import org.assertj.core.api.BaseTest;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.PredicateDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;

import static ShouldNotMatch.ADVICE;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class ShouldNotMatch_create_Test extends BaseTest {
    @Test
    public void should_create_error_message_with_default_predicate_description() {
        ErrorMessageFactory factory = ShouldNotMatch.shouldNotMatch("Yoda", ( color) -> color.equals("green"), GIVEN);
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %nExpecting:%n  <\"Yoda\">%nnot to match given predicate." + (ADVICE))));
    }

    @Test
    public void should_create_error_message_with_predicate_description() {
        ErrorMessageFactory factory = ShouldNotMatch.shouldNotMatch("Yoda", (String color) -> color.equals("green"), new PredicateDescription("green light saber"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format("[Test] %nExpecting:%n  <\"Yoda\">%nnot to match \'green light saber\' predicate."));
    }

    @Test
    public void should_fail_if_predicate_description_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> shouldNotMatch("Yoda", ( color) -> color.equals("green"), null)).withMessage("The predicate description must not be null");
    }
}

