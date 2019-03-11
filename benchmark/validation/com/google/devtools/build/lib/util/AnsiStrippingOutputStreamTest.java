/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AnsiStrippingOutputStream}.
 */
@RunWith(JUnit4.class)
public class AnsiStrippingOutputStreamTest {
    ByteArrayOutputStream output;

    PrintStream input;

    private static final String ESCAPE = "\u001b[";

    @Test
    public void doesNotFailHorribly() throws Exception {
        assertThat(getOutput("Love")).isEqualTo("Love");
    }

    @Test
    public void canStripAnsiCode() throws Exception {
        assertThat(getOutput(((((AnsiStrippingOutputStreamTest.ESCAPE) + "32mLove") + (AnsiStrippingOutputStreamTest.ESCAPE)) + "m"))).isEqualTo("Love");
    }

    @Test
    public void recognizesAnsiCodeWhenBrokenUp() throws Exception {
        assertThat(getOutput("\u001b", "[", "mLove")).isEqualTo("Love");
    }

    @Test
    public void handlesOnlyEscCorrectly() throws Exception {
        assertThat(getOutput("\u001bLove")).isEqualTo("\u001bLove");
    }

    @Test
    public void handlesEscInPlaceOfControlCharCorrectly() throws Exception {
        assertThat(getOutput(((((AnsiStrippingOutputStreamTest.ESCAPE) + "31;42") + (AnsiStrippingOutputStreamTest.ESCAPE)) + "1mLove"))).isEqualTo(((AnsiStrippingOutputStreamTest.ESCAPE) + "31;42Love"));
    }

    @Test
    public void handlesTwoEscapeSequencesCorrectly() throws Exception {
        assertThat(getOutput((((((AnsiStrippingOutputStreamTest.ESCAPE) + "32m") + (AnsiStrippingOutputStreamTest.ESCAPE)) + "1m") + "Love"))).isEqualTo("Love");
    }
}

