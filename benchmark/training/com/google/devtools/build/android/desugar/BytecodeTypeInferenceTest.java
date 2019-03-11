/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.desugar;


import InferredType.BOOLEAN;
import InferredType.BYTE;
import InferredType.DOUBLE;
import InferredType.FLOAT;
import InferredType.INT;
import InferredType.LONG;
import InferredType.NULL;
import InferredType.TOP;
import InferredType.UNINITIALIZED_PREFIX;
import InferredType.UNINITIALIZED_THIS;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.devtools.build.android.desugar.BytecodeTypeInference.InferredType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.objectweb.asm.Label;


/**
 * Test for {@link BytecodeTypeInference}
 */
@RunWith(JUnit4.class)
public class BytecodeTypeInferenceTest {
    private static final Path JAR_PATH = Paths.get(System.getProperty("jar_path"));

    private static final Path GOLDEN_PATH = Paths.get(System.getProperty("golden_file"));

    @Test
    public void testTypeInference() throws IOException {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ByteCodeTypePrinter.printClassesWithTypes(BytecodeTypeInferenceTest.JAR_PATH, printWriter);
            printWriter.close();
        }
        String inferenceResult = stringWriter.toString().trim();
        String golden = Files.asCharSource(BytecodeTypeInferenceTest.GOLDEN_PATH.toFile(), StandardCharsets.UTF_8).read().trim();
        assertThat(inferenceResult).isEqualTo(golden);
    }

    @Test
    public void testUninitializedInferType() {
        Label label = new Label();
        InferredType type = InferredType.createUninitializedType(label);
        assertThat(type.descriptor()).isEqualTo(UNINITIALIZED_PREFIX);
        assertThat(type.uninitializationLabel()).isEqualTo(label);
    }

    @Test
    public void testNonUninitializedInferType() {
        ImmutableMap<String, InferredType> map = ImmutableMap.<String, InferredType>builder().put("Z", BOOLEAN).put("B", BYTE).put("I", INT).put("F", FLOAT).put("D", DOUBLE).put("J", LONG).put("TOP", TOP).put("NULL", NULL).put("UNINITIALIZED_THIS", UNINITIALIZED_THIS).build();
        map.forEach(( descriptor, expected) -> {
            InferredType type = InferredType.createNonUninitializedType(descriptor);
            assertThat(type.uninitializationLabel()).isNull();
            assertThat(type.descriptor()).isEqualTo(descriptor);
            assertThat(type).isSameAs(expected);
        });
    }
}

