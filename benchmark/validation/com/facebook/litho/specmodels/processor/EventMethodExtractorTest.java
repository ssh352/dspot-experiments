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


import TypeName.BOOLEAN;
import TypeName.INT;
import TypeName.VOID;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.specmodels.internal.RunMode;
import com.facebook.litho.specmodels.model.EventDeclarationModel;
import com.facebook.litho.specmodels.model.EventMethod;
import com.facebook.litho.specmodels.model.SpecMethodModel;
import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.ClassName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link EventMethodExtractor}
 */
public class EventMethodExtractorTest {
    @Rule
    public CompilationRule mCompilationRule = new CompilationRule();

    static class TestClass {
        @OnCreateLayout
        public void ignored() {
        }

        @OnEvent(Object.class)
        public void testMethod(@Prop
        boolean testProp, @State
        int testState, @Param
        Object testPermittedAnnotation) {
            // Don't do anything.
        }

        @OnUpdateState
        public void alsoIgnored() {
        }
    }

    @Test
    public void testMethodExtraction() {
        Elements elements = mCompilationRule.getElements();
        TypeElement typeElement = elements.getTypeElement(EventMethodExtractorTest.TestClass.class.getCanonicalName());
        List<Class<? extends Annotation>> permittedParamAnnotations = new ArrayList<>();
        ImmutableList<SpecMethodModel<EventMethod, EventDeclarationModel>> methods = EventMethodExtractor.getOnEventMethods(elements, typeElement, permittedParamAnnotations, Mockito.mock(Messager.class), RunMode.normal());
        assertThat(methods).hasSize(1);
        SpecMethodModel<EventMethod, EventDeclarationModel> eventMethod = methods.iterator().next();
        assertThat(eventMethod.typeModel.name).isEqualTo(ClassName.bestGuess("java.lang.Object"));
        assertThat(eventMethod.modifiers).hasSize(1);
        assertThat(eventMethod.modifiers).contains(Modifier.PUBLIC);
        assertThat(eventMethod.name.toString()).isEqualTo("testMethod");
        assertThat(eventMethod.returnType).isEqualTo(VOID);
        assertThat(eventMethod.methodParams).hasSize(3);
        assertThat(eventMethod.methodParams.get(0).getName()).isEqualTo("testProp");
        assertThat(eventMethod.methodParams.get(0).getTypeName()).isEqualTo(BOOLEAN);
        assertThat(eventMethod.methodParams.get(0).getAnnotations()).hasSize(1);
        assertThat(eventMethod.methodParams.get(1).getName()).isEqualTo("testState");
        assertThat(eventMethod.methodParams.get(1).getTypeName()).isEqualTo(INT);
        assertThat(eventMethod.methodParams.get(1).getAnnotations()).hasSize(1);
        assertThat(eventMethod.methodParams.get(2).getName()).isEqualTo("testPermittedAnnotation");
        assertThat(eventMethod.methodParams.get(2).getTypeName()).isEqualTo(ClassName.bestGuess("java.lang.Object"));
        assertThat(eventMethod.methodParams.get(2).getAnnotations()).hasSize(1);
    }
}

