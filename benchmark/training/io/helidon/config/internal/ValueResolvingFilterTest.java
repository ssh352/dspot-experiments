/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.internal;


import ConfigFilters.ValueResolvingBuilder.FAIL_ON_MISSING_REFERENCE_KEY_NAME;
import ValueResolvingFilter.MISSING_REFERENCE_ERROR;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigException;
import io.helidon.config.ConfigFilters;
import io.helidon.config.ConfigSources;
import io.helidon.config.MissingValueException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link ValueResolvingFilter}.
 */
public class ValueResolvingFilterTest {
    @Test
    public void testValueResolving() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${greeting} ${name}!", "greeting", "Hallo", "name", "Joachim"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo Joachim!"));
    }

    @Test
    public void testValueResolvingDottedReference() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${greeting.german} ${name}!", "greeting.german", "Hallo", "name", "Joachim"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo Joachim!"));
    }

    @Test
    public void testValueResolvingTransitive() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${template}", "template", "${greeting} ${name}!", "name", "Joachim", "greeting", "Hallo"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo Joachim!"));
    }

    @Test
    public void testValueResolvingBackslashed() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${template}", "template", "${greeting} \\${name}!", "name", "Joachim", "greeting", "Hallo"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo ${name}!"));
    }

    @Test
    public void testValueResolvingBackslashIgnored() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${greeting} \\ ${name}!", "name", "Joachim", "greeting", "Hallo"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo \\ Joachim!"));
    }

    @Test
    public void testValueResolvingBackslashIgnored2() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "${template}", "template", "${greeting} \\ ${name}!", "name", "Joachim", "greeting", "Hallo"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("message").asString().get(), Is.is("Hallo \\ Joachim!"));
    }

    private static class LoopTestResult {
        private IllegalStateException ex = null;

        private String message = null;
    }

    @Test
    public void testValueResolvingInfiniteLoop() throws InterruptedException, ExecutionException, TimeoutException {
        // Run the config.get in a FutureTask so the test can time out the get if
        // the recursive lookup was not correctly detected. That way the test
        // explicitly fails rather than hanging the test run as the loop runs and runs.
        final FutureTask<ValueResolvingFilterTest.LoopTestResult> shouldNotRecurse = new FutureTask<ValueResolvingFilterTest.LoopTestResult>(() -> {
            ValueResolvingFilterTest.LoopTestResult result = new ValueResolvingFilterTest.LoopTestResult();
            Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("message", "There ${template}", "template", "and back again ${message}"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
            try {
                // The following should trigger the exception.
                result.message = config.get("message").asString().get();
            } catch (IllegalStateException ex) {
                // We expect this.
                result.ex = ex;
            }
            return result;
        });
        // Either JDK9 or later or the config implementation should prevent
        // the config.get from timing out and should throw an exception instead.
        shouldNotRecurse.run();
        ValueResolvingFilterTest.LoopTestResult result = shouldNotRecurse.get(2, TimeUnit.SECONDS);
        MatcherAssert.assertThat(result.message, CoreMatchers.nullValue());
        MatcherAssert.assertThat(result.ex, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(result.ex.getMessage(), CoreMatchers.startsWith("Recursive update"));
    }

    @Test
    public void testValueResolvingMissingReferenceIgnored() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).addFilter(ConfigFilters.valueResolving()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("wrong").asString().get(), Is.is("${missing}"));
    }

    @Test
    public void testValueResolvingMissingReferenceNoFilter() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("wrong").asString().get(), Is.is("${missing}"));
    }

    @Test
    public void testValueResolvingMissingReferenceFails() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).addFilter(ConfigFilters.valueResolving().failOnMissingReference(true).build()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            config.get("wrong").asString().get();
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith(String.format(MISSING_REFERENCE_ERROR, "wrong")));
        MatcherAssert.assertThat(ex.getCause(), IsInstanceOf.instanceOf(MissingValueException.class));
    }

    @Test
    public void testValueResolvingMissingReferenceOKViaNoArgsCtor() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).addFilter(new ValueResolvingFilter()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get("wrong").asString().get(), Is.is("${missing}"));
    }

    @Test
    public void testValueResolvingMissingReferenceFailsViaNoArgsCtor() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).addFilter(ConfigFilters.valueResolving().failOnMissingReference(true).build()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            config.get("wrong").asString().get();
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith(String.format(MISSING_REFERENCE_ERROR, "wrong")));
        MatcherAssert.assertThat(ex.getCause(), IsInstanceOf.instanceOf(MissingValueException.class));
    }

    @Test
    public void testValueResolvingMissingReferenceFailsViaServiceLoader() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}", FAIL_ON_MISSING_REFERENCE_KEY_NAME, "true"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            config.get("wrong").asString().get();
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith(String.format(MISSING_REFERENCE_ERROR, "wrong")));
        MatcherAssert.assertThat(ex.getCause(), IsInstanceOf.instanceOf(MissingValueException.class));
    }

    @Test
    public void testValueResolvingMissingReferenceOKViaServiceLoader() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("wrong", "${missing}"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("wrong").asString().get(), Is.is("${missing}"));
    }

    @Test
    public void testValueResolvingSatisfiedReferenceViaServiceLoader() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("correct", "${refc}", "refc", "answer"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("correct").asString().get(), Is.is("answer"));
    }
}

