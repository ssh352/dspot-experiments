/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.process;


import org.apache.geode.distributed.AbstractLauncher.ServiceState;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ControllableProcess}.
 */
public class ControllableProcessTest {
    @Test
    public void fetchStatusWithValidationThrowsIfJsonIsNull() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        Mockito.when(state.toJson()).thenReturn(null);
        Throwable thrown = catchThrowable(() -> fetchStatusWithValidation(handler));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Null JSON for status is invalid");
    }

    @Test
    public void fetchStatusWithValidationThrowsIfJsonIsEmpty() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        Mockito.when(state.toJson()).thenReturn("");
        Throwable thrown = catchThrowable(() -> fetchStatusWithValidation(handler));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Empty JSON for status is invalid");
    }

    @Test
    public void fetchStatusWithValidationThrowsIfJsonOnlyContainsSpaces() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        Mockito.when(state.toJson()).thenReturn("  ");
        Throwable thrown = catchThrowable(() -> fetchStatusWithValidation(handler));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Empty JSON for status is invalid");
    }

    @Test
    public void fetchStatusWithValidationThrowsIfJsonOnlyContainsTabs() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        Mockito.when(state.toJson()).thenReturn("\t\t");
        Throwable thrown = catchThrowable(() -> fetchStatusWithValidation(handler));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Empty JSON for status is invalid");
    }

    @Test
    public void fetchStatusWithValidationThrowsIfJsonOnlyContainsLineFeeds() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        Mockito.when(state.toJson()).thenReturn(((System.lineSeparator()) + (System.lineSeparator())));
        Throwable thrown = catchThrowable(() -> fetchStatusWithValidation(handler));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Empty JSON for status is invalid");
    }

    @Test
    public void fetchStatusWithValidationReturnsJsonIfItHasContent() {
        ControlNotificationHandler handler = Mockito.mock(ControlNotificationHandler.class);
        ServiceState state = Mockito.mock(ServiceState.class);
        Mockito.when(handler.handleStatus()).thenReturn(state);
        String jsonContent = "json content";
        Mockito.when(state.toJson()).thenReturn(jsonContent);
        String result = ControllableProcess.fetchStatusWithValidation(handler);
        assertThat(result).isEqualTo(jsonContent);
    }
}

