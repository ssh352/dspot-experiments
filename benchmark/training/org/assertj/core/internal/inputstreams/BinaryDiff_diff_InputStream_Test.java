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
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.BinaryDiff;
import org.assertj.core.internal.BinaryDiffResult;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link BinaryDiff#diff(java.io.InputStream, java.io.InputStream)}</code>.
 *
 * @author Olivier Michallat
 */
public class BinaryDiff_diff_InputStream_Test {
    private static BinaryDiff binaryDiff;

    private InputStream actual;

    private InputStream expected;

    @Test
    public void should_return_no_diff_if_inputstreams_have_equal_content() throws IOException {
        actual = stream(202, 254, 186, 190);
        expected = stream(202, 254, 186, 190);
        BinaryDiffResult result = BinaryDiff_diff_InputStream_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.hasNoDiff()).isTrue();
    }

    @Test
    public void should_return_diff_if_inputstreams_differ_on_one_byte() throws IOException {
        actual = stream(202, 254, 186, 190);
        expected = stream(202, 254, 190, 190);
        BinaryDiffResult result = BinaryDiff_diff_InputStream_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo(2);
        Assertions.assertThat(result.actual).isEqualTo("0xBA");
        Assertions.assertThat(result.expected).isEqualTo("0xBE");
    }

    @Test
    public void should_return_diff_if_actual_is_shorter() throws IOException {
        actual = stream(202, 254, 186);
        expected = stream(202, 254, 186, 190);
        BinaryDiffResult result = BinaryDiff_diff_InputStream_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo(3);
        Assertions.assertThat(result.actual).isEqualTo("EOF");
        Assertions.assertThat(result.expected).isEqualTo("0xBE");
    }

    @Test
    public void should_return_diff_if_expected_is_shorter() throws IOException {
        actual = stream(202, 254, 186, 190);
        expected = stream(202, 254, 186);
        BinaryDiffResult result = BinaryDiff_diff_InputStream_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo(3);
        Assertions.assertThat(result.actual).isEqualTo("0xBE");
        Assertions.assertThat(result.expected).isEqualTo("EOF");
    }
}

