/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.specmodels.generator;


import com.facebook.litho.specmodels.model.RenderDataDiffModel;
import com.facebook.litho.specmodels.model.SpecModel;
import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests {@link RenderDataGenerator}
 */
public class RenderDataGeneratorTest {
    @Rule
    public CompilationRule mCompilationRule = new CompilationRule();

    private SpecModel mSpecModelWithDiff;

    private SpecModel mSpecModelWithoutDiff;

    private RenderDataDiffModel mRenderDataDiffModel;

    @Test
    public void testGenerateWithDiff() {
        TypeSpecDataHolder dataHolder = RenderDataGenerator.generate(mSpecModelWithDiff);
        assertThat(dataHolder.getMethodSpecs()).isNotEmpty();
    }

    @Test
    public void testDoNotGenerateWithoutDiff() {
        TypeSpecDataHolder dataHolder = RenderDataGenerator.generate(mSpecModelWithoutDiff);
        assertThat(dataHolder.getMethodSpecs()).isEmpty();
    }
}

