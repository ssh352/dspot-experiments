/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.common;


import BytesRange.TO_END_OF_CONTENT;
import org.junit.Test;

import static BytesRange.TO_END_OF_CONTENT;


public class BytesRangeTest {
    @Test
    public void testHeaderValueForRangeFrom() {
        assertThat(BytesRange.from(2000).toHttpRangeHeaderValue()).isEqualTo("bytes=2000-");
    }

    @Test
    public void testHeaderValueForRangeTo() {
        assertThat(BytesRange.toMax(1000).toHttpRangeHeaderValue()).isEqualTo("bytes=0-1000");
    }

    @Test
    public void testContains() {
        assertThat(BytesRange.toMax(1000).contains(BytesRange.toMax(999))).isTrue();
        assertThat(BytesRange.toMax(1000).contains(BytesRange.toMax(1000))).isTrue();
        assertThat(BytesRange.toMax(1000).contains(BytesRange.toMax(1001))).isFalse();
        assertThat(BytesRange.from(1000).contains(BytesRange.from(999))).isFalse();
        assertThat(BytesRange.from(1000).contains(BytesRange.from(1000))).isTrue();
        assertThat(BytesRange.from(1000).contains(BytesRange.from(1001))).isTrue();
        assertThat(BytesRange.from(1000).contains(BytesRange.toMax(999))).isFalse();
        assertThat(new BytesRange(0, TO_END_OF_CONTENT).contains(new BytesRange(0, 1000))).isTrue();
        assertThat(new BytesRange(0, 1000).contains(new BytesRange(0, TO_END_OF_CONTENT))).isFalse();
    }

    @Test
    public void testFromContentRangeHeaderWithValidHeader() {
        BytesRangeTest.assertValidFromContentRangeHeader("bytes 0-499/1234", 0, 499);
        BytesRangeTest.assertValidFromContentRangeHeader("bytes 500-999/1234", 500, 999);
        BytesRangeTest.assertValidFromContentRangeHeader("bytes 500-1233/1234", 500, TO_END_OF_CONTENT);
        BytesRangeTest.assertValidFromContentRangeHeader("bytes 734-1233/1234", 734, TO_END_OF_CONTENT);
    }

    @Test
    public void testFromContentRangeHeaderWithInvalidHeader() {
        assertThat(BytesRange.fromContentRangeHeader(null)).isNull();
        BytesRangeTest.assertInvalidFromContentRangeHeader("not bytes 0-499/1234");
        BytesRangeTest.assertInvalidFromContentRangeHeader("bytes -499/1234");
        BytesRangeTest.assertInvalidFromContentRangeHeader("bytes 0-/1234");
        BytesRangeTest.assertInvalidFromContentRangeHeader("bytes 499/1234");
        BytesRangeTest.assertInvalidFromContentRangeHeader("bytes 0-499");
        BytesRangeTest.assertInvalidFromContentRangeHeader("bytes 0-/");
    }
}

