/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.microprofile.faulttolerance;


import CircuitBreakerHelper.CommandData;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Class CommandDataTest.
 */
public class CommandDataTest {
    @Test
    public void testSuccessRatio() {
        CircuitBreakerHelper.CommandData data = new CircuitBreakerHelper.CommandData(6);
        Arrays.asList(true, true, true, false, false, false).forEach(data::pushResult);
        MatcherAssert.assertThat(data.getSuccessRatio(), CoreMatchers.is((3.0 / 6)));
    }

    @Test
    public void testFailureRatio() {
        CircuitBreakerHelper.CommandData data = new CircuitBreakerHelper.CommandData(4);
        Arrays.asList(true, false, false, false).forEach(data::pushResult);
        MatcherAssert.assertThat(data.getFailureRatio(), CoreMatchers.is((3.0 / 4)));
    }

    @Test
    public void testPushResult() {
        CircuitBreakerHelper.CommandData data = new CircuitBreakerHelper.CommandData(2);
        Arrays.asList(true, false, false, false, true, true).forEach(data::pushResult);// last two count

        MatcherAssert.assertThat(data.getFailureRatio(), CoreMatchers.is(0.0));
    }

    @Test
    public void testSizeLessCapacity() {
        CircuitBreakerHelper.CommandData data = new CircuitBreakerHelper.CommandData(6);
        Arrays.asList(true, false, false).forEach(data::pushResult);
        MatcherAssert.assertThat(data.getFailureRatio(), CoreMatchers.is((-1.0)));// not enough data

    }
}

