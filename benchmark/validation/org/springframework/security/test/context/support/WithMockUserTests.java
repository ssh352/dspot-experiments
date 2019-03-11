/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.test.context.support;


import TestExecutionEvent.TEST_EXECUTION;
import TestExecutionEvent.TEST_METHOD;
import org.junit.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;

import static TestExecutionEvent.TEST_EXECUTION;
import static TestExecutionEvent.TEST_METHOD;


public class WithMockUserTests {
    @Test
    public void defaults() {
        WithMockUser mockUser = AnnotatedElementUtils.findMergedAnnotation(WithMockUserTests.Annotated.class, WithMockUser.class);
        assertThat(mockUser.value()).isEqualTo("user");
        assertThat(mockUser.username()).isEmpty();
        assertThat(mockUser.password()).isEqualTo("password");
        assertThat(mockUser.roles()).containsOnly("USER");
        assertThat(mockUser.setupBefore()).isEqualByComparingTo(TEST_METHOD);
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithMockUserTests.Annotated.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_METHOD);
    }

    @WithMockUser
    private class Annotated {}

    @Test
    public void findMergedAnnotationWhenSetupExplicitThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithMockUserTests.SetupExplicit.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_METHOD);
    }

    @WithMockUser(setupBefore = TEST_METHOD)
    private class SetupExplicit {}

    @Test
    public void findMergedAnnotationWhenSetupOverriddenThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithMockUserTests.SetupOverridden.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_EXECUTION);
    }

    @WithMockUser(setupBefore = TEST_EXECUTION)
    private class SetupOverridden {}
}

