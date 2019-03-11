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
package org.assertj.core.internal.inputstreams;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.Diff;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;


public class Diff_diff_InputStream_String_Test {
    private static Diff diff;

    private InputStream actual;

    private String expected;

    @Test
    public void should_return_empty_diff_list_if_inputstreams_have_equal_content() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("base", "line0", "line1");
        expected = Diff_diff_InputStream_String_Test.joinLines("base", "line0", "line1");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).isEmpty();
    }

    @Test
    public void should_return_diffs_if_inputstreams_do_not_have_equal_content() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("base", "line_0", "line_1");
        expected = Diff_diff_InputStream_String_Test.joinLines("base", "line0", "line1");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).hasSize(1).first().hasToString(String.format(("Changed content at line 2:%n" + ((((("expecting:%n" + "  [\"line0\",%n") + "   \"line1\"]%n") + "but was:%n") + "  [\"line_0\",%n") + "   \"line_1\"]%n"))));
    }

    @Test
    public void should_return_multiple_diffs_if_inputstreams_contain_multiple_differences() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("base", "line_0", "line1", "line_2");
        expected = Diff_diff_InputStream_String_Test.joinLines("base", "line0", "line1", "line2");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).hasSize(2);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Changed content at line 2:%n" + ((("expecting:%n" + "  [\"line0\"]%n") + "but was:%n") + "  [\"line_0\"]%n"))));
        Assertions.assertThat(diffs.get(1)).hasToString(String.format(("Changed content at line 4:%n" + ((("expecting:%n" + "  [\"line2\"]%n") + "but was:%n") + "  [\"line_2\"]%n"))));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_shorter_than_content_of_expected() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("base", "line_0");
        expected = Diff_diff_InputStream_String_Test.joinLines("base", "line_0", "line_1");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Missing content at line 3:%n" + "  [\"line_1\"]%n")));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_longer_than_content_of_expected() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("base", "line_0", "line_1");
        expected = Diff_diff_InputStream_String_Test.joinLines("base", "line_0");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Extra content at line 3:%n" + "  [\"line_1\"]%n")));
    }

    @Test
    public void should_return_single_diff_line_for_new_line_at_start() throws IOException {
        // GIVEN
        actual = Diff_diff_InputStream_Test.stream("", "line_0", "line_1", "line_2");
        expected = Diff_diff_InputStream_String_Test.joinLines("line_0", "line_1", "line_2");
        // WHEN
        List<Delta<String>> diffs = Diff_diff_InputStream_String_Test.diff.diff(actual, expected);
        // THEN
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Extra content at line 1:%n" + "  [\"\"]%n")));
    }
}

