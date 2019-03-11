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


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.error.ZippedElementsShouldSatisfy.ZipSatisfyError;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ElementsShouldZipSatisfy_create_Test {
    private AssertionInfo info;

    @Test
    public void should_create_error_message() {
        // GIVEN
        List<ZipSatisfyError> errors = Lists.list(new ZipSatisfyError("Luke", "LUKE", "error luke"), new ZipSatisfyError("Yo-da", "YODA", "error yoda"));
        ErrorMessageFactory factory = ZippedElementsShouldSatisfy.zippedElementsShouldSatisfy(info, Lists.list("Luke", "Yo-da"), Lists.list("LUKE", "YODA"), errors);
        // WHEN
        String message = factory.create(new TextDescription("Test"), info.representation());
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((((("Expecting zipped elements of:%n" + "  <[\"Luke\", \"Yo-da\"]>%n") + "and:%n") + "  <[\"LUKE\", \"YODA\"]>%n") + "to satisfy given requirements but these zipped elements did not:") + "%n%n- (\"Luke\", \"LUKE\") error: error luke") + "%n%n- (\"Yo-da\", \"YODA\") error: error yoda"))));
    }

    @Test
    public void should_create_error_message_and_escape_percent_correctly() {
        // GIVEN
        List<ZipSatisfyError> errors = Lists.list(new ZipSatisfyError("Luke", "LU%dKE", "error luke"), new ZipSatisfyError("Yo-da", "YODA", "error yoda"));
        ErrorMessageFactory factory = ZippedElementsShouldSatisfy.zippedElementsShouldSatisfy(info, Lists.list("Luke", "Yo-da"), Lists.list("LU%dKE", "YODA"), errors);
        // WHEN
        String message = factory.create(new TextDescription("Test"), info.representation());
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((((("Expecting zipped elements of:%n" + "  <[\"Luke\", \"Yo-da\"]>%n") + "and:%n") + "  <[\"LU%%dKE\", \"YODA\"]>%n") + "to satisfy given requirements but these zipped elements did not:") + "%n%n- (\"Luke\", \"LU%%dKE\") error: error luke") + "%n%n- (\"Yo-da\", \"YODA\") error: error yoda"))));
    }
}

