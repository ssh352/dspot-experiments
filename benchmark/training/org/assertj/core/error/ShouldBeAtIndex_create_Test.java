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


import org.assertj.core.api.Assertions;
import org.assertj.core.api.TestCondition;
import org.assertj.core.data.Index;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldBeAtIndex#create(Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Bo Gotthardt
 */
public class ShouldBeAtIndex_create_Test {
    @Test
    public void should_create_error_message() {
        ErrorMessageFactory factory = ShouldBeAtIndex.shouldBeAtIndex(Lists.newArrayList("Yoda", "Luke"), new TestCondition<String>("red lightsaber"), Index.atIndex(1), "Luke");
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format("[Test] %nExpecting:%n <\"Luke\">%nat index <1> to be:%n <red lightsaber>%nin:%n <[\"Yoda\", \"Luke\"]>%n"));
    }
}

