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
package io.helidon.config.tests.parsers1;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.ConfigException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Parser tests with {@link Config.Builder#disableParserServices}.
 */
public class ParserServicesDisabledTest extends AbstractParserServicesTest {
    @Test
    public void testNoSuitableParser() {
        final ConfigException ce = Assertions.assertThrows(ConfigException.class, () -> {
            configBuilder().build();
        });
        MatcherAssert.assertThat(ce.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("Cannot load data from mandatory source", "InMemoryConfig[String]", "Cannot find suitable parser for 'text/x-java-properties' media type.")));
        // Cannot find suitable parser for 'text/x-java-properties' media type.
        MatcherAssert.assertThat(ce.getCause(), Matchers.instanceOf(ConfigException.class));
    }
}
