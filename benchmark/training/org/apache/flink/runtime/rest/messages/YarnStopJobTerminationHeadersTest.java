/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest.messages;


import HttpMethodWrapper.GET;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link YarnStopJobTerminationHeaders}.
 */
public class YarnStopJobTerminationHeadersTest extends TestLogger {
    // instance under test
    private static final YarnStopJobTerminationHeaders instance = YarnStopJobTerminationHeaders.getInstance();

    @Test
    public void testMethod() {
        Assert.assertThat(YarnStopJobTerminationHeadersTest.instance.getHttpMethod(), Matchers.is(GET));
    }

    @Test
    public void testURL() {
        Assert.assertThat(YarnStopJobTerminationHeadersTest.instance.getTargetRestEndpointURL(), Matchers.endsWith("yarn-stop"));
    }
}

