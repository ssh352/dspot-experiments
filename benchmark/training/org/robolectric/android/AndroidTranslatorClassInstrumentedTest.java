package org.robolectric.android;


import android.graphics.Bitmap;
import android.graphics.Paint;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.internal.Instrument;


@RunWith(AndroidJUnit4.class)
@Config(sdk = Config.NEWEST_SDK)
public class AndroidTranslatorClassInstrumentedTest {
    @Test
    @Config(shadows = AndroidTranslatorClassInstrumentedTest.ShadowPaintForTests.class)
    public void testNativeMethodsAreDelegated() throws Exception {
        Paint paint = new Paint();
        paint.setColor(1234);
        assertThat(paint.getColor()).isEqualTo(1234);
    }

    @Test
    @Config(shadows = AndroidTranslatorClassInstrumentedTest.ShadowClassWithPrivateConstructor.class)
    public void testClassesWithPrivateDefaultConstructorsCanBeShadowed() {
        AndroidTranslatorClassInstrumentedTest.ClassWithPrivateConstructor inst = new AndroidTranslatorClassInstrumentedTest.ClassWithPrivateConstructor();
        assertThat(inst.getInt()).isEqualTo(42);
    }

    @Test
    public void testEnumConstructorsAreNotRewritten() {
        // just referencing this enum value would blow up if we rewrite its constructor
        Bitmap.Config alpha8 = Config.ALPHA_8;
        assertThat(alpha8.toString()).isEqualTo("ALPHA_8");
    }

    /* Test "foreign class" getting its methods shadowed whe it's
    in the SandboxClassLoader CustomClassNames arrayList
     */
    @Test
    @Config(shadows = { AndroidTranslatorClassInstrumentedTest.ShadowCustomPaint.class, AndroidTranslatorClassInstrumentedTest.ShadowPaintForTests.class })
    public void testCustomMethodShadowed() throws Exception {
        AndroidTranslatorClassInstrumentedTest.CustomPaint customPaint = new AndroidTranslatorClassInstrumentedTest.CustomPaint();
        assertThat(customPaint.getColor()).isEqualTo(10);
        assertThat(customPaint.getColorName()).isEqualTo("rainbow");
    }

    @Instrument
    public static class ClassWithPrivateConstructor {
        private ClassWithPrivateConstructor() {
        }

        public int getInt() {
            return 99;
        }
    }

    @Implements(AndroidTranslatorClassInstrumentedTest.ClassWithPrivateConstructor.class)
    public static class ShadowClassWithPrivateConstructor {
        @Implementation
        protected int getInt() {
            return 42;
        }
    }

    @Implements(Paint.class)
    public static class ShadowPaintForTests {
        private int color;

        @Implementation
        protected void setColor(int color) {
            this.color = color;
        }

        @Implementation
        protected int getColor() {
            return color;
        }
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    @Instrument
    public static class CustomPaint extends Paint {
        private int customColor;

        @Override
        public int getColor() {
            return customColor;
        }

        public String getColorName() {
            return Integer.toString(customColor);
        }
    }

    @Implements(AndroidTranslatorClassInstrumentedTest.CustomPaint.class)
    public static class ShadowCustomPaint extends AndroidTranslatorClassInstrumentedTest.ShadowPaintForTests {
        @Override
        @Implementation
        protected int getColor() {
            return 10;
        }

        @Implementation
        protected String getColorName() {
            return "rainbow";
        }
    }
}

