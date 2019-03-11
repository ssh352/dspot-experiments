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
import androidx.core.view.ViewCompat;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import static MountItem.LAYOUT_FLAG_DISABLE_TOUCHABLE;
import static MountItem.LAYOUT_FLAG_DUPLICATE_PARENT_STATE;


/**
 * Tests {@link MountItem}
 */
@RunWith(ComponentsTestRunner.class)
public class MountItemTest {
    private MountItem mMountItem;

    private Component mComponent;

    private ComponentHost mComponentHost;

    private Object mContent;

    private CharSequence mContentDescription;

    private Object mViewTag;

    private SparseArray<Object> mViewTags;

    private EventHandler mClickHandler;

    private EventHandler mLongClickHandler;

    private EventHandler mFocusChangeHandler;

    private EventHandler mTouchHandler;

    private EventHandler mDispatchPopulateAccessibilityEventHandler;

    private int mFlags;

    private ComponentContext mContext;

    private NodeInfo mNodeInfo;

    @Test
    public void testIsBound() {
        mMountItem.setIsBound(true);
        assertThat(mMountItem.isBound()).isTrue();
        mMountItem.setIsBound(false);
        assertThat(mMountItem.isBound()).isFalse();
    }

    @Test
    public void testGetters() {
        assertThat(mMountItem.getComponent()).isSameAs(((Component) (mComponent)));
        assertThat(mMountItem.getHost()).isSameAs(mComponentHost);
        assertThat(mMountItem.getContent()).isSameAs(mContent);
        assertThat(mMountItem.getNodeInfo().getContentDescription()).isSameAs(mContentDescription);
        assertThat(mMountItem.getNodeInfo().getClickHandler()).isSameAs(mClickHandler);
        assertThat(mMountItem.getNodeInfo().getFocusChangeHandler()).isSameAs(mFocusChangeHandler);
        assertThat(mMountItem.getNodeInfo().getTouchHandler()).isSameAs(mTouchHandler);
        assertThat(mMountItem.getLayoutFlags()).isEqualTo(mFlags);
        assertThat(mMountItem.getImportantForAccessibility()).isEqualTo(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    @Test
    public void testFlags() {
        mFlags = (LAYOUT_FLAG_DUPLICATE_PARENT_STATE) | (LAYOUT_FLAG_DISABLE_TOUCHABLE);
        mMountItem = new MountItem(mComponent, mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES, ORIENTATION_PORTRAIT, null);
        assertThat(MountItem.isDuplicateParentState(mMountItem.getLayoutFlags())).isTrue();
        assertThat(MountItem.isTouchableDisabled(mMountItem.getLayoutFlags())).isTrue();
        mFlags = 0;
        mMountItem = new MountItem(mComponent, mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES, ORIENTATION_PORTRAIT, null);
        assertThat(MountItem.isDuplicateParentState(mMountItem.getLayoutFlags())).isFalse();
        assertThat(MountItem.isTouchableDisabled(mMountItem.getLayoutFlags())).isFalse();
    }

    @Test
    public void testViewFlags() {
        View view = new View(RuntimeEnvironment.application);
        view.setClickable(true);
        view.setEnabled(true);
        view.setLongClickable(true);
        view.setFocusable(false);
        view.setSelected(false);
        mMountItem = new MountItem(mComponent, mComponentHost, view, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES, ORIENTATION_PORTRAIT, null);
        assertThat(mMountItem.isViewClickable()).isTrue();
        assertThat(mMountItem.isViewEnabled()).isTrue();
        assertThat(mMountItem.isViewLongClickable()).isTrue();
        assertThat(mMountItem.isViewFocusable()).isFalse();
        assertThat(mMountItem.isViewSelected()).isFalse();
        view.setClickable(false);
        view.setEnabled(false);
        view.setLongClickable(false);
        view.setFocusable(true);
        view.setSelected(true);
        mMountItem = new MountItem(mComponent, mComponentHost, view, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES, ORIENTATION_PORTRAIT, null);
        assertThat(mMountItem.isViewClickable()).isFalse();
        assertThat(mMountItem.isViewEnabled()).isFalse();
        assertThat(mMountItem.isViewLongClickable()).isFalse();
        assertThat(mMountItem.isViewFocusable()).isTrue();
        assertThat(mMountItem.isViewSelected()).isTrue();
    }

    @Test
    public void testIsAccessibleWithNullComponent() {
        final MountItem mountItem = new MountItem(mComponent, mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO, ORIENTATION_PORTRAIT, null);
        assertThat(mountItem.isAccessible()).isFalse();
    }

    @Test
    public void testIsAccessibleWithAccessibleComponent() {
        final MountItem mountItem = new MountItem(/* implementsAccessibility */
        TestDrawableComponent.create(mContext, true, true, true, true, false).build(), mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO, ORIENTATION_PORTRAIT, null);
        assertThat(mountItem.isAccessible()).isTrue();
    }

    @Test
    public void testIsAccessibleWithDisabledAccessibleComponent() {
        final MountItem mountItem = new MountItem(/* implementsAccessibility */
        TestDrawableComponent.create(mContext, true, true, true, true, false).build(), mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO, ORIENTATION_PORTRAIT, null);
        assertThat(mountItem.isAccessible()).isFalse();
    }

    @Test
    public void testIsAccessibleWithAccessibilityEventHandler() {
        final MountItem mountItem = new MountItem(/* implementsAccessibility */
        TestDrawableComponent.create(mContext, true, true, true, true, false).build(), mComponentHost, mContent, mNodeInfo, null, mFlags, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO, ORIENTATION_PORTRAIT, null);
        assertThat(mountItem.isAccessible()).isTrue();
    }

    @Test
    public void testIsAccessibleWithNonAccessibleComponent() {
        assertThat(mMountItem.isAccessible()).isFalse();
    }

    @Test
    public void testUpdateDoesntChangeFlags() {
        LayoutOutput layoutOutput = new LayoutOutput();
        layoutOutput.setNodeInfo(mNodeInfo);
        layoutOutput.setComponent(mComponent);
        View view = new View(RuntimeEnvironment.application);
        final MountItem mountItem = new MountItem(mComponent, mComponentHost, view, layoutOutput);
        assertThat(mountItem.isViewClickable()).isFalse();
        view.setClickable(true);
        mountItem.update(layoutOutput);
        assertThat(mountItem.isViewClickable()).isFalse();
    }
}

