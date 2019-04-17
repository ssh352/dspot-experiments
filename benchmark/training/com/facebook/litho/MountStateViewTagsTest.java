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
package com.facebook.litho;


import android.util.SparseArray;
import android.view.View;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class MountStateViewTagsTest {
    private static final int DUMMY_ID = 268435456;

    private ComponentContext mContext;

    @Test
    public void testInnerComponentHostViewTags() {
        final Object tag1 = new Object();
        final SparseArray<Object> tags1 = new SparseArray(1);
        tags1.put(MountStateViewTagsTest.DUMMY_ID, tag1);
        final Object tag2 = new Object();
        final SparseArray<Object> tags2 = new SparseArray(1);
        tags2.put(MountStateViewTagsTest.DUMMY_ID, tag2);
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).viewTags(tags1).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c))).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c).viewTags(tags2)).build();
            }
        });
        final View innerHost1 = lithoView.getChildAt(0);
        final View innerHost2 = lithoView.getChildAt(1);
        assertThat(innerHost1.getTag(MountStateViewTagsTest.DUMMY_ID)).isEqualTo(tag1);
        assertThat(innerHost2.getTag(MountStateViewTagsTest.DUMMY_ID)).isEqualTo(tag2);
    }

    @Test
    public void testRootHostViewTags() {
        final Object tag = new Object();
        final SparseArray<Object> tags = new SparseArray(1);
        tags.put(MountStateViewTagsTest.DUMMY_ID, tag);
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).viewTags(tags).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(lithoView.getTag(MountStateViewTagsTest.DUMMY_ID)).isEqualTo(tag);
    }
}
