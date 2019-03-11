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
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link ShouldBeInSameMinuteWindow#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>
 * .
 *
 * @author Joel Costigliola
 * @author Mikhail Mazursky
 */
public class ShouldBeInSameMinuteWindow_create_Test {
    @Test
    public void should_create_error_message() {
        ErrorMessageFactory factory = ShouldBeInSameMinuteWindow.shouldBeInSameMinuteWindow(DateUtil.parseDatetime("2011-01-01T05:00:00"), DateUtil.parseDatetime("2011-01-01T05:02:01"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((("Expecting:%n" + "  <2011-01-01T05:00:00.000>%n") + "to be close to:%n") + "  <2011-01-01T05:02:01.000>%n") + "by less than one minute (strictly) but difference was: 2m and 1s"))));
    }
}

