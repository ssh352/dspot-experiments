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
package com.facebook.litho.specmodels.processor;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.specmodels.model.DependencyInjectionHelper;
import com.facebook.litho.specmodels.model.LayoutSpecModel;
import com.google.testing.compile.CompilationRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link LayoutSpecModelFactory}
 */
public class LayoutSpecModelFactoryTest {
    private static final String TEST_QUALIFIED_SPEC_NAME = "com.facebook.litho.specmodels.processor.LayoutSpecModelFactoryTest.TestLayoutSpec";

    private static final String TEST_QUALIFIED_COMPONENT_NAME = "com.facebook.litho.specmodels.processor.LayoutSpecModelFactoryTest.TestLayoutComponentName";

    @Rule
    public CompilationRule mCompilationRule = new CompilationRule();

    private final LayoutSpecModelFactory mFactory = new LayoutSpecModelFactory();

    private final DependencyInjectionHelper mDependencyInjectionHelper = Mockito.mock(DependencyInjectionHelper.class);

    private LayoutSpecModel mLayoutSpecModel;

    @Test
    public void testCreate() {
        assertThat(mLayoutSpecModel.getSpecName()).isEqualTo("TestLayoutSpec");
        assertThat(mLayoutSpecModel.getComponentName()).isEqualTo("TestLayoutComponentName");
        assertThat(mLayoutSpecModel.getSpecTypeName().toString()).isEqualTo(LayoutSpecModelFactoryTest.TEST_QUALIFIED_SPEC_NAME);
        assertThat(mLayoutSpecModel.getComponentTypeName().toString()).isEqualTo(LayoutSpecModelFactoryTest.TEST_QUALIFIED_COMPONENT_NAME);
        assertThat(mLayoutSpecModel.getDelegateMethods()).hasSize(2);
        assertThat(mLayoutSpecModel.getProps()).hasSize(2);
        assertThat(mLayoutSpecModel.hasInjectedDependencies()).isTrue();
        assertThat(mLayoutSpecModel.getDependencyInjectionHelper()).isSameAs(mDependencyInjectionHelper);
    }

    @LayoutSpec("TestLayoutComponentName")
    static class TestLayoutSpec {
        @OnCreateInitialState
        static void createInitialState(@Prop
        int prop1) {
        }

        @OnCreateLayout
        static Component onCreateLayout(ComponentContext c, @Prop
        int prop2) {
            return null;
        }
    }
}
