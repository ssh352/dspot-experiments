/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.testutil;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link com.google.devtools.build.lib.testutil.Suite#getSize(Class)}.
 */
@RunWith(JUnit4.class)
public class TestSizeAnnotationTest {
    private static class HasNoTestSpecAnnotation {}

    @TestSpec(flaky = true)
    private static class FlakyTestSpecAnnotation {}

    @TestSpec(suite = "foo")
    private static class HasNoSizeAnnotationElement {}

    @TestSpec(size = Suite.SMALL_TESTS)
    private static class IsAnnotatedWithSmallSize {}

    @TestSpec(size = Suite.MEDIUM_TESTS)
    private static class IsAnnotatedWithMediumSize {}

    @TestSpec(size = Suite.LARGE_TESTS)
    private static class IsAnnotatedWithLargeSize {}

    private static class SuperclassHasAnnotationButNoSizeElement extends TestSizeAnnotationTest.HasNoSizeAnnotationElement {}

    @TestSpec(size = Suite.LARGE_TESTS)
    private static class HasSizeElementAndSuperclassHasAnnotationButNoSizeElement extends TestSizeAnnotationTest.HasNoSizeAnnotationElement {}

    private static class SuperclassHasAnnotationWithSizeElement extends TestSizeAnnotationTest.IsAnnotatedWithSmallSize {}

    @TestSpec(size = Suite.LARGE_TESTS)
    private static class HasSizeElementAndSuperclassHasAnnotationWithSizeElement extends TestSizeAnnotationTest.IsAnnotatedWithSmallSize {}

    @Test
    public void testHasNoTestSpecAnnotationIsSmall() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.HasNoTestSpecAnnotation.class)).isEqualTo(Suite.SMALL_TESTS);
    }

    @Test
    public void testHasNoSizeAnnotationElementIsSmall() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.HasNoSizeAnnotationElement.class)).isEqualTo(Suite.SMALL_TESTS);
    }

    @Test
    public void testIsAnnotatedWithSmallSizeIsSmall() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.IsAnnotatedWithSmallSize.class)).isEqualTo(Suite.SMALL_TESTS);
    }

    @Test
    public void testIsAnnotatedWithMediumSizeIsMedium() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.IsAnnotatedWithMediumSize.class)).isEqualTo(Suite.MEDIUM_TESTS);
    }

    @Test
    public void testIsAnnotatedWithLargeSizeIsLarge() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.IsAnnotatedWithLargeSize.class)).isEqualTo(Suite.LARGE_TESTS);
    }

    @Test
    public void testSuperclassHasAnnotationButNoSizeElement() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.SuperclassHasAnnotationButNoSizeElement.class)).isEqualTo(Suite.SMALL_TESTS);
    }

    @Test
    public void testHasSizeElementAndSuperclassHasAnnotationButNoSizeElement() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.HasSizeElementAndSuperclassHasAnnotationButNoSizeElement.class)).isEqualTo(Suite.LARGE_TESTS);
    }

    @Test
    public void testSuperclassHasAnnotationWithSizeElement() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.SuperclassHasAnnotationWithSizeElement.class)).isEqualTo(Suite.SMALL_TESTS);
    }

    @Test
    public void testHasSizeElementAndSuperclassHasAnnotationWithSizeElement() {
        assertThat(Suite.getSize(TestSizeAnnotationTest.HasSizeElementAndSuperclassHasAnnotationWithSizeElement.class)).isEqualTo(Suite.LARGE_TESTS);
    }

    @Test
    public void testIsNotFlaky() {
        assertThat(Suite.isFlaky(TestSizeAnnotationTest.HasNoTestSpecAnnotation.class)).isFalse();
    }

    @Test
    public void testIsFlaky() {
        assertThat(Suite.isFlaky(TestSizeAnnotationTest.FlakyTestSpecAnnotation.class)).isTrue();
    }
}

