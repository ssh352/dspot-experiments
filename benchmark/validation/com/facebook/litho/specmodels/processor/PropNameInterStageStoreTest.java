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


import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.specmodels.MockSpecModel;
import com.squareup.javapoet.ClassName;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests {@link PropNameInterStageStore}
 */
public class PropNameInterStageStoreTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Filer mFiler;

    @Test
    public void testLoad() throws IOException {
        final PropNameInterStageStore store = new PropNameInterStageStore(mFiler);
        final FileObject fileObject = PropNameInterStageStoreTest.makeFileObjectForString("arg0\narg1\n");
        Mockito.when(mFiler.getResource(ArgumentMatchers.any(JavaFileManager.Location.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(fileObject);
        final Optional<ImmutableList<String>> strings = store.loadNames(new MockName("com.example.MyComponentSpec"));
        LithoAssertions.assertThat(strings.isPresent()).isTrue();
        LithoAssertions.assertThat(strings.get()).containsExactly("arg0", "arg1");
        Mockito.verify(mFiler).getResource(StandardLocation.CLASS_PATH, "", "META-INF/litho/com.example.MyComponentSpec.props");
    }

    @Test
    public void testSave() throws IOException {
        final PropNameInterStageStore store = new PropNameInterStageStore(mFiler);
        final MockSpecModel specModel = MockSpecModel.newBuilder().rawProps(ImmutableList.of(PropNameInterStageStoreTest.makePropModel("param0"), PropNameInterStageStoreTest.makePropModel("param1"))).rawInjectProps(ImmutableList.of(makeInjectPropModel("injectParam0"))).specTypeName(ClassName.get(PropNameInterStageStoreTest.MyTestSpec.class)).build();
        store.saveNames(specModel);
        Mockito.verify(mFiler).createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/litho/com.facebook.litho.specmodels.processor.PropNameInterStageStoreTest.MyTestSpec.props");
        // Not checking the actually written values here because Java IO is a horrible mess.
    }

    public static class MyTestSpec {}
}

