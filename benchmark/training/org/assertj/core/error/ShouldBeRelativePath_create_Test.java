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


import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link ShouldBeRelativePath#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>
 * .
 */
public class ShouldBeRelativePath_create_Test {
    private static final TestDescription TEST_DESCRIPTION = new TestDescription("Test");

    private static final StandardRepresentation STANDARD_REPRESENTATION = new StandardRepresentation();

    @Test
    public void should_create_error_message_for_File() {
        FakeFile file = new FakeFile("xyz");
        ErrorMessageFactory factory = ShouldBeRelativePath.shouldBeRelativePath(file);
        String message = factory.create(ShouldBeRelativePath_create_Test.TEST_DESCRIPTION, ShouldBeRelativePath_create_Test.STANDARD_REPRESENTATION);
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] " + (ShouldBeRelativePath.SHOULD_BE_RELATIVE_PATH)), file));
    }

    @Test
    public void should_create_error_message_for_Path() {
        final Path path = Mockito.mock(Path.class);
        ErrorMessageFactory factory = ShouldBeRelativePath.shouldBeRelativePath(path);
        String message = factory.create(ShouldBeRelativePath_create_Test.TEST_DESCRIPTION, ShouldBeRelativePath_create_Test.STANDARD_REPRESENTATION);
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] " + (ShouldBeRelativePath.SHOULD_BE_RELATIVE_PATH)), path));
    }
}
