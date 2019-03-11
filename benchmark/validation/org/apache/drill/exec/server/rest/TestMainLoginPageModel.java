/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.rest;


import ExecConstants.HTTP_AUTHENTICATION_MECHANISMS;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import com.typesafe.config.ConfigValueFactory;
import junit.framework.TestCase;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.server.DrillbitContext;
import org.apache.drill.exec.server.rest.LogInLogOutResources.MainLoginPageModel;
import org.apache.drill.exec.work.WorkManager;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Test for {@link LogInLogOutResources.MainLoginPageModel} with various configurations done in DrillConfig
 */
public class TestMainLoginPageModel {
    @Mock
    WorkManager workManager;

    @Mock
    DrillbitContext context;

    @InjectMocks
    LogInLogOutResources logInLogOutResources = new LogInLogOutResources();

    /**
     * Test when auth is disabled then both Form and Spnego authentication is disabled.
     */
    @Test
    public void testAuthDisabled() {
        final DrillConfig config = DrillConfig.create();
        Mockito.when(context.getConfig()).thenReturn(config);
        final MainLoginPageModel model = logInLogOutResources.new MainLoginPageModel(null);
        TestCase.assertTrue((!(model.isFormEnabled())));
        TestCase.assertTrue((!(model.isSpnegoEnabled())));
    }

    /**
     * Test when auth is enabled with no http.auth.mechanisms configured then by default Form authentication is
     * enabled but Spnego is disabled.
     */
    @Test
    public void testAuthEnabledWithNoMech() {
        final DrillConfig config = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)));
        Mockito.when(context.getConfig()).thenReturn(config);
        final MainLoginPageModel model = logInLogOutResources.new MainLoginPageModel(null);
        TestCase.assertTrue(model.isFormEnabled());
        TestCase.assertTrue((!(model.isSpnegoEnabled())));
    }

    /**
     * Test when auth is enabled with http.auth.mechanisms configured as Form then only Form authentication is
     * enabled but Spnego is disabled.
     */
    @Test
    public void testAuthEnabledWithForm() {
        final DrillConfig config = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("form"))));
        Mockito.when(context.getConfig()).thenReturn(config);
        final MainLoginPageModel model = logInLogOutResources.new MainLoginPageModel(null);
        TestCase.assertTrue(model.isFormEnabled());
        TestCase.assertTrue((!(model.isSpnegoEnabled())));
    }

    /**
     * Test when auth is enabled with http.auth.mechanisms configured as Spnego then only Spnego authentication is
     * enabled but Form is disabled.
     */
    @Test
    public void testAuthEnabledWithSpnego() {
        final DrillConfig config = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("spnego"))));
        Mockito.when(context.getConfig()).thenReturn(config);
        final MainLoginPageModel model = logInLogOutResources.new MainLoginPageModel(null);
        TestCase.assertTrue((!(model.isFormEnabled())));
        TestCase.assertTrue(model.isSpnegoEnabled());
    }

    /**
     * Test when auth is enabled with http.auth.mechanisms configured as Form, Spnego then both Form and Spnego
     * authentication are enabled.
     */
    @Test
    public void testAuthEnabledWithFormSpnego() {
        final DrillConfig config = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(HTTP_AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("form", "spnego"))));
        Mockito.when(context.getConfig()).thenReturn(config);
        final MainLoginPageModel model = logInLogOutResources.new MainLoginPageModel(null);
        TestCase.assertTrue(model.isFormEnabled());
        TestCase.assertTrue(model.isSpnegoEnabled());
    }
}

