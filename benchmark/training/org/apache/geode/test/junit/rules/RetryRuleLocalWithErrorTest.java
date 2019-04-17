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
import org.apache.geode.test.junit.Retry;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Unit tests for {@link RetryRule} involving local scope (ie rule affects only the test methods
 * annotated with {@code @Retry}) with failures due to an {@code Error}.
 */
public class RetryRuleLocalWithErrorTest {
    @Test
    public void failsUnused() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.FailsUnused.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleLocalWithErrorTest.FailsUnused.message);
        assertThat(RetryRuleLocalWithErrorTest.FailsUnused.count).isEqualTo(1);
    }

    @Test
    public void passesUnused() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.PassesUnused.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithErrorTest.PassesUnused.count).isEqualTo(1);
    }

    @Test
    public void failsOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.message);
        assertThat(RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void passesOnSecondAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.count).isEqualTo(2);
    }

    @Test
    public void failsOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isFalse();
        List<Failure> failures = result.getFailures();
        assertThat(failures.size()).as(("Failures: " + failures)).isEqualTo(1);
        Failure failure = failures.get(0);
        assertThat(failure.getException()).isExactlyInstanceOf(AssertionError.class).hasMessage(RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.message);
        assertThat(RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.count).isEqualTo(3);
    }

    @Test
    public void passesOnThirdAttempt() {
        Result result = TestRunner.runTest(RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.count).isEqualTo(3);
    }

    /**
     * Used by test {@link #failsUnused()}
     */
    public static class FailsUnused {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.FailsUnused.count = 0;
            RetryRuleLocalWithErrorTest.FailsUnused.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.FailsUnused.count)++;
            RetryRuleLocalWithErrorTest.FailsUnused.message = "Failing " + (RetryRuleLocalWithErrorTest.FailsUnused.count);
            Assert.fail(RetryRuleLocalWithErrorTest.FailsUnused.message);
        }
    }

    /**
     * Used by test {@link #passesUnused()}
     */
    public static class PassesUnused {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.PassesUnused.count = 0;
            RetryRuleLocalWithErrorTest.PassesUnused.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.PassesUnused.count)++;
        }
    }

    /**
     * Used by test {@link #failsOnSecondAttempt()}
     */
    public static class FailsOnSecondAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.count = 0;
            RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.count)++;
            RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.message = "Failing " + (RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.count);
            Assert.fail(RetryRuleLocalWithErrorTest.FailsOnSecondAttempt.message);
        }
    }

    /**
     * Used by test {@link #passesOnSecondAttempt()}
     */
    public static class PassesOnSecondAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.count = 0;
            RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(2)
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.count)++;
            if ((RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.count) < 2) {
                RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.message = "Failing " + (RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.count);
                Assert.fail(RetryRuleLocalWithErrorTest.PassesOnSecondAttempt.message);
            }
        }
    }

    /**
     * Used by test {@link #failsOnThirdAttempt()}
     */
    public static class FailsOnThirdAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.count = 0;
            RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.count)++;
            RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.message = "Failing " + (RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.count);
            Assert.fail(RetryRuleLocalWithErrorTest.FailsOnThirdAttempt.message);
        }
    }

    /**
     * Used by test {@link #passesOnThirdAttempt()}
     */
    public static class PassesOnThirdAttempt {
        static int count = 0;

        static String message = null;

        @BeforeClass
        public static void beforeClass() {
            RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.count = 0;
            RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.message = null;
        }

        @Rule
        public RetryRule retryRule = new RetryRule();

        @Test
        @Retry(3)
        public void doTest() throws Exception {
            (RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.count)++;
            if ((RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.count) < 3) {
                RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.message = "Failing " + (RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.count);
                Assert.fail(RetryRuleLocalWithErrorTest.PassesOnThirdAttempt.message);
            }
        }
    }
}
