/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.master.metrics;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HealthCheckGaugeTest {
    @Mock
    HealthCheckRegistry registry;

    @Test
    public void testHealthy() throws Exception {
        Mockito.when(registry.runHealthCheck(ArgumentMatchers.anyString())).thenReturn(Result.healthy());
        final HealthCheckGauge gauge = new HealthCheckGauge(registry, "foo");
        Assert.assertThat(gauge.getValue(), CoreMatchers.equalTo(1));
    }

    @Test
    public void testUnhealthy() throws Exception {
        Mockito.when(registry.runHealthCheck(ArgumentMatchers.anyString())).thenReturn(Result.unhealthy("meh"));
        final HealthCheckGauge gauge = new HealthCheckGauge(registry, "foo");
        Assert.assertThat(gauge.getValue(), CoreMatchers.equalTo(0));
    }
}
