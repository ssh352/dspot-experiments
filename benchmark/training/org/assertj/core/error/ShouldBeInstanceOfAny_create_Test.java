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
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Throwables;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldBeInstanceOfAny#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Alex Ruiz
 */
public class ShouldBeInstanceOfAny_create_Test {
    private ErrorMessageFactory factory;

    @Test
    public void should_create_error_message() {
        String message = factory.create(new TestDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting:%n" + " <\"Yoda\">%n") + "to be an instance of any of:%n") + " <[java.io.File, java.util.regex.Pattern]>%n") + "but was instance of:%n") + " <java.lang.String>"))));
    }

    @Test
    public void should_create_error_message_with_stack_trace_for_throwable() {
        IllegalArgumentException throwable = new IllegalArgumentException("Not in a list");
        Class<?>[] types = new Class<?>[]{ NullPointerException.class, IllegalStateException.class };
        String message = ShouldBeInstanceOfAny.shouldBeInstanceOfAny(throwable, types).create();
        Assertions.assertThat(message).isEqualTo(String.format(("%nExpecting:%n" + (((" <java.lang.IllegalArgumentException: Not in a list>%n" + "to be an instance of any of:%n") + " <[java.lang.NullPointerException, java.lang.IllegalStateException]>%n") + "but was:%n <\"%s\">")), Throwables.getStackTrace(throwable)));
    }
}

