/**
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/classgraph/classgraph
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.issues.issue46;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class Issue46Test.
 */
public class Issue46Test {
    /**
     * Issue 46 test.
     */
    @Test
    public void issue46Test() {
        final String jarPath = (Issue46Test.class.getClassLoader().getResource("nested-jars-level1.zip").getPath()) + "!level2.jar!level3.jar!classpath1/classpath2";
        try (ScanResult scanResult = new ClassGraph().overrideClasspath(jarPath).enableClassInfo().scan()) {
            assertThat(scanResult.getAllClasses().getNames()).containsExactlyInAnyOrder("com.test.Test");
        }
    }
}
