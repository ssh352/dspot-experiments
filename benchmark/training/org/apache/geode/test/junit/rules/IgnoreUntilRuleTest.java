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
package org.apache.geode.test.junit.rules;


import java.util.List;
import org.apache.geode.test.junit.IgnoreUntil;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Unit tests for {@link IgnoreUntilRule}.
 */
public class IgnoreUntilRuleTest {
    private static final String ASSERTION_ERROR_MESSAGE = "failing test";

    @Test
    public void shouldIgnoreWhenUntilIsInFuture() {
        Result result = TestRunner.runTest(IgnoreUntilRuleTest.ShouldIgnoreWhenUntilIsInFuture.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(IgnoreUntilRuleTest.ShouldIgnoreWhenUntilIsInFuture.count).isEqualTo(0);
    }

    @Test
    public void shouldExecuteWhenUntilIsInPast() {
        Result result = TestRunner.runTest(IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsInPast.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(IgnoreUntilRuleTest.ASSERTION_ERROR_MESSAGE);
        assertThat(IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsInPast.count).isEqualTo(1);
    }

    @Test
    public void shouldExecuteWhenUntilIsDefault() {
        Result result = TestRunner.runTest(IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsDefault.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(IgnoreUntilRuleTest.ASSERTION_ERROR_MESSAGE);
        assertThat(IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsDefault.count).isEqualTo(1);
    }

    /**
     * Used by test {@link #shouldIgnoreWhenUntilIsInFuture()}
     */
    public static class ShouldIgnoreWhenUntilIsInFuture {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            IgnoreUntilRuleTest.ShouldIgnoreWhenUntilIsInFuture.count = 0;
        }

        @Rule
        public final IgnoreUntilRule ignoreUntilRule = new IgnoreUntilRule();

        @Test
        @IgnoreUntil(value = "description", until = "3000-01-01")
        public void doTest() throws Exception {
            (IgnoreUntilRuleTest.ShouldIgnoreWhenUntilIsInFuture.count)++;
            fail(IgnoreUntilRuleTest.ASSERTION_ERROR_MESSAGE);
        }
    }

    /**
     * Used by test {@link #shouldExecuteWhenUntilIsInPast()}
     */
    public static class ShouldExecuteWhenUntilIsInPast {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsInPast.count = 0;
        }

        @Rule
        public final IgnoreUntilRule ignoreUntilRule = new IgnoreUntilRule();

        @Test
        @IgnoreUntil(value = "description", until = "1980-01-01")
        public void doTest() throws Exception {
            (IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsInPast.count)++;
            fail(IgnoreUntilRuleTest.ASSERTION_ERROR_MESSAGE);
        }
    }

    /**
     * Used by test {@link #shouldExecuteWhenUntilIsDefault()}
     */
    public static class ShouldExecuteWhenUntilIsDefault {
        static int count = 0;

        @BeforeClass
        public static void beforeClass() {
            IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsDefault.count = 0;
        }

        @Rule
        public final IgnoreUntilRule ignoreUntilRule = new IgnoreUntilRule();

        @Test
        @IgnoreUntil("description")
        public void doTest() throws Exception {
            (IgnoreUntilRuleTest.ShouldExecuteWhenUntilIsDefault.count)++;
            fail(IgnoreUntilRuleTest.ASSERTION_ERROR_MESSAGE);
        }
    }
}

