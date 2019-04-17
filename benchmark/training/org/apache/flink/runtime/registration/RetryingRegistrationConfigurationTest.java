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
package org.apache.flink.runtime.registration;


import ClusterOptions.ERROR_REGISTRATION_DELAY;
import ClusterOptions.INITIAL_REGISTRATION_TIMEOUT;
import ClusterOptions.MAX_REGISTRATION_TIMEOUT;
import ClusterOptions.REFUSED_REGISTRATION_DELAY;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link RetryingRegistrationConfiguration}.
 */
public class RetryingRegistrationConfigurationTest extends TestLogger {
    @Test
    public void testConfigurationParsing() {
        final Configuration configuration = new Configuration();
        final long initialRegistrationTimeout = 1L;
        final long maxRegistrationTimeout = 2L;
        final long refusedRegistrationDelay = 3L;
        final long errorRegistrationDelay = 4L;
        configuration.setLong(INITIAL_REGISTRATION_TIMEOUT, initialRegistrationTimeout);
        configuration.setLong(MAX_REGISTRATION_TIMEOUT, maxRegistrationTimeout);
        configuration.setLong(REFUSED_REGISTRATION_DELAY, refusedRegistrationDelay);
        configuration.setLong(ERROR_REGISTRATION_DELAY, errorRegistrationDelay);
        final RetryingRegistrationConfiguration retryingRegistrationConfiguration = RetryingRegistrationConfiguration.fromConfiguration(configuration);
        Assert.assertThat(retryingRegistrationConfiguration.getInitialRegistrationTimeoutMillis(), Matchers.is(initialRegistrationTimeout));
        Assert.assertThat(retryingRegistrationConfiguration.getMaxRegistrationTimeoutMillis(), Matchers.is(maxRegistrationTimeout));
        Assert.assertThat(retryingRegistrationConfiguration.getRefusedDelayMillis(), Matchers.is(refusedRegistrationDelay));
        Assert.assertThat(retryingRegistrationConfiguration.getErrorDelayMillis(), Matchers.is(errorRegistrationDelay));
    }
}
