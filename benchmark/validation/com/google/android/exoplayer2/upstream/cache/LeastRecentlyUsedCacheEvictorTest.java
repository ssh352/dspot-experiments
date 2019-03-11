/**
 * Copyright (C) 2017 The Android Open Source Project
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
package com.google.android.exoplayer2.upstream.cache;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link LeastRecentlyUsedCacheEvictor}.
 */
@RunWith(RobolectricTestRunner.class)
public class LeastRecentlyUsedCacheEvictorTest {
    @Test
    public void testContentBiggerThanMaxSizeDoesNotThrowException() throws Exception {
        int maxBytes = 100;
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxBytes);
        evictor.onCacheInitialized();
        evictor.onStartFile(Mockito.mock(Cache.class), "key", 0, (maxBytes + 1));
    }
}

