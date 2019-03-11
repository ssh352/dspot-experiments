/**
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs.env;


import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class EnvironmentTest {
    @Test
    public void testRunEnvironment() {
        // tag::env[]
        ApplicationContext applicationContext = ApplicationContext.run("test", "android");
        Environment environment = applicationContext.getEnvironment();
        Assert.assertTrue(environment.getActiveNames().contains("test"));
        Assert.assertTrue(environment.getActiveNames().contains("android"));
        // end::env[]
    }

    @Test
    public void testRunEnvironmentWithProperties() {
        // tag::envProps[]
        ApplicationContext applicationContext = ApplicationContext.run(PropertySource.of("test", CollectionUtils.mapOf("micronaut.server.host", "foo", "micronaut.server.port", 8080)), "test", "android");
        Environment environment = applicationContext.getEnvironment();
        Assert.assertEquals("foo", environment.getProperty("micronaut.server.host", String.class).orElse("localhost"));
        // end::envProps[]
    }
}

