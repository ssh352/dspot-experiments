/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.extractor.ts;


import com.google.android.exoplayer2.testutil.ExtractorAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link AdtsExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AdtsExtractorTest {
    @Test
    public void testSample() throws Exception {
        ExtractorAsserts.assertBehavior(AdtsExtractor::new, "ts/sample.adts");
    }

    @Test
    public void testSample_withSeeking() throws Exception {
        ExtractorAsserts.assertBehavior(() -> /* firstStreamSampleTimestampUs= */
        /* flags= */
        new AdtsExtractor(0, AdtsExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING), "ts/sample_cbs.adts");
    }
}

