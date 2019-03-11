/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.profiler.callcounts;


import com.google.common.collect.ImmutableList;
import com.google.perftools.profiles.ProfileProto.Profile;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Callcounts}.
 */
@RunWith(JUnit4.class)
public class CallcountsTest {
    @Test
    public void testCallCounts() {
        int samplePeriod = 1;
        int sampleVariance = 0;
        int maxCallstackDepth = 2;
        Callcounts.init(samplePeriod, sampleVariance, maxCallstackDepth);
        callMethod1();
        callMethod2();
        Profile profile = Callcounts.createProfile();
        assertThat(profile.getSampleList()).hasSize(2);
        List<String> callstack0 = sampleToCallstack(profile, profile.getSample(0));
        List<String> callstack1 = sampleToCallstack(profile, profile.getSample(1));
        String cls = getClass().getName();
        assertThat(ImmutableList.of(callstack0, callstack1)).containsExactly(ImmutableList.of((cls + ".callMethod1(35)"), (cls + ".testCallCounts(48)")), ImmutableList.of((cls + ".callMethod2(39)"), (cls + ".testCallCounts(49)")));
    }
}

