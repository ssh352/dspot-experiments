package org.robolectric.shadows;


import android.content.Context;
import android.widget.ListPopupWindow;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowListPopupWindowTest {
    @Test
    public void show_setsLastListPopupWindow() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        ListPopupWindow popupWindow = new ListPopupWindow(context);
        assertThat(ShadowListPopupWindow.getLatestListPopupWindow()).isNull();
        popupWindow.setAnchorView(new android.view.View(context));
        popupWindow.show();
        assertThat(ShadowListPopupWindow.getLatestListPopupWindow()).isSameAs(popupWindow);
    }
}

