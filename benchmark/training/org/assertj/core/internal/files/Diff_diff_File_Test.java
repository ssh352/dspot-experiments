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
package org.assertj.core.internal.files;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.Diff;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.TextFileWriter;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Diff#diff(File, File)}</code>.
 *
 * @author Yvonne Wang
 */
public class Diff_diff_File_Test {
    private static Diff diff;

    private static TextFileWriter writer;

    private File actual;

    private File expected;

    @Test
    public void should_return_empty_diff_list_if_files_have_equal_content() throws IOException {
        String[] content = Arrays.array("line0", "line1");
        Diff_diff_File_Test.writer.write(actual, content);
        Diff_diff_File_Test.writer.write(expected, content);
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).isEmpty();
    }

    @Test
    public void should_return_diffs_if_files_do_not_have_equal_content() throws IOException {
        Diff_diff_File_Test.writer.write(actual, "line_0", "line_1");
        Diff_diff_File_Test.writer.write(expected, "line0", "line1");
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Changed content at line 1:%n" + ((((("expecting:%n" + "  [\"line0\",%n") + "   \"line1\"]%n") + "but was:%n") + "  [\"line_0\",%n") + "   \"line_1\"]%n"))));
    }

    @Test
    public void should_return_multiple_diffs_if_files_contain_multiple_differences() throws IOException {
        Diff_diff_File_Test.writer.write(actual, "line_0", "line1", "line_2");
        Diff_diff_File_Test.writer.write(expected, "line0", "line1", "line2");
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(2);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Changed content at line 1:%n" + ((("expecting:%n" + "  [\"line0\"]%n") + "but was:%n") + "  [\"line_0\"]%n"))));
        Assertions.assertThat(diffs.get(1)).hasToString(String.format(("Changed content at line 3:%n" + ((("expecting:%n" + "  [\"line2\"]%n") + "but was:%n") + "  [\"line_2\"]%n"))));
    }

    @Test
    public void should_be_able_to_detect_mixed_differences() throws IOException {
        // @format:off
        Diff_diff_File_Test.writer.write(actual, "line1", "line2", "line3", "line4", "line5", "line 9", "line 10", "line 11");
        Diff_diff_File_Test.writer.write(expected, "line1", "line1a", "line1b", "line2", "line3", "line7", "line5");
        // @format:on
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(3);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Missing content at line 2:%n" + ("  [\"line1a\",%n" + "   \"line1b\"]%n"))));
        Assertions.assertThat(diffs.get(1)).hasToString(String.format(("Changed content at line 6:%n" + ((("expecting:%n" + "  [\"line7\"]%n") + "but was:%n") + "  [\"line4\"]%n"))));
        Assertions.assertThat(diffs.get(2)).hasToString(String.format(("Extra content at line 8:%n" + (("  [\"line 9\",%n" + "   \"line 10\",%n") + "   \"line 11\"]%n"))));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_shorter_than_content_of_expected() throws IOException {
        Diff_diff_File_Test.writer.write(actual, "line_0");
        Diff_diff_File_Test.writer.write(expected, "line_0", "line_1");
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Missing content at line 2:%n" + "  [\"line_1\"]%n")));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_longer_than_content_of_expected() throws IOException {
        Diff_diff_File_Test.writer.write(actual, "line_0", "line_1");
        Diff_diff_File_Test.writer.write(expected, "line_0");
        List<Delta<String>> diffs = Diff_diff_File_Test.diff.diff(actual, Charset.defaultCharset(), expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Extra content at line 2:%n" + "  [\"line_1\"]%n")));
    }
}

