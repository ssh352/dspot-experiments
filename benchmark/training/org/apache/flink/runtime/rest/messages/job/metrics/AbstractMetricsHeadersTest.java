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
package org.apache.flink.runtime.rest.messages.job.metrics;


import HttpMethodWrapper.GET;
import HttpResponseStatus.OK;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AbstractMetricsHeaders}.
 */
public class AbstractMetricsHeadersTest extends TestLogger {
    private AbstractMetricsHeaders<EmptyMessageParameters> metricsHandlerHeaders;

    @Test
    public void testHttpMethod() {
        Assert.assertThat(metricsHandlerHeaders.getHttpMethod(), Matchers.equalTo(GET));
    }

    @Test
    public void testResponseStatus() {
        Assert.assertThat(metricsHandlerHeaders.getResponseStatusCode(), Matchers.equalTo(OK));
    }

    @Test
    public void testRequestClass() {
        Assert.assertThat(metricsHandlerHeaders.getRequestClass(), Matchers.equalTo(EmptyRequestBody.class));
    }

    @Test
    public void testResponseClass() {
        Assert.assertThat(metricsHandlerHeaders.getResponseClass(), Matchers.equalTo(MetricCollectionResponseBody.class));
    }
}
