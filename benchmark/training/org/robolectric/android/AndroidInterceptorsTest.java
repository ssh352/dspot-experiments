package org.robolectric.android;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.internal.bytecode.MethodRef;


@RunWith(JUnit4.class)
public class AndroidInterceptorsTest {
    @Test
    public void allMethodRefs() throws Exception {
        assertThat(getAllMethodRefs()).containsAllOf(new MethodRef("java.util.LinkedHashMap", "eldest"), new MethodRef("java.lang.System", "loadLibrary"), new MethodRef("android.os.StrictMode", "trackActivity"), new MethodRef("android.os.StrictMode", "incrementExpectedActivityCount"), new MethodRef("android.util.LocaleUtil", "getLayoutDirectionFromLocale"), new MethodRef("com.android.internal.policy.PolicyManager", "makeNewWindow"), new MethodRef("android.view.FallbackEventHandler", "*"), new MethodRef("android.view.IWindowSession", "*"), new MethodRef("java.lang.System", "nanoTime"), new MethodRef("java.lang.System", "currentTimeMillis"), new MethodRef("java.lang.System", "arraycopy"), new MethodRef("java.lang.System", "logE"), new MethodRef("java.util.Locale", "adjustLanguageCode"));
    }

    @Test
    public void localeAdjustCodeInterceptor() throws Exception {
        assertThat(adjust("EN")).isEqualTo("en");
        assertThat(adjust("he")).isEqualTo("iw");
        assertThat(adjust("yi")).isEqualTo("ji");
        assertThat(adjust("ja")).isEqualTo("ja");
    }
}

