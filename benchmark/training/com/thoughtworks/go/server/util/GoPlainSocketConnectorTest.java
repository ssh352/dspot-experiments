/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.util;


import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.ServerConnector;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoPlainSocketConnectorTest {
    private ServerConnector connector;

    private HttpConfiguration configuration;

    @Test
    public void shouldCreateAServerConnectorWithConfiguredPortAndBuffersize() throws Exception {
        Assert.assertThat(connector.getPort(), Matchers.is(1234));
        Assert.assertThat(connector.getHost(), Matchers.is("foo"));
        Assert.assertThat(connector.getIdleTimeout(), Matchers.is(200L));
        Assert.assertThat(configuration.getOutputBufferSize(), Matchers.is(100));
    }

    @Test
    public void shouldNotSendAServerHeaderForSecurityReasons() throws Exception {
        Assert.assertThat(configuration.getSendServerVersion(), Matchers.is(false));
    }
}

