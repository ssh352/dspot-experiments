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
package io.helidon.config.tests;


import io.helidon.config.Config;
import io.helidon.config.ConfigMappingException;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.ConfigValues;
import io.helidon.config.MissingValueException;
import io.helidon.config.hocon.HoconConfigParserBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * This test shows Client API use-cases.
 */
public class DemoTest {
    @Test
    public void testCreateConfig() {
        // looks for: application .yaml | .conf | .json | .properties on classpath
        Config config = Config.create();
        // STRING
        MatcherAssert.assertThat(config.get("app.greeting").asString(), Matchers.is(ConfigValues.simpleValue("Hello")));
    }

    @Test
    public void testTypedAccessors() {
        Config config = Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).build();
        // ACCESSORS:
        // String, int, boolean, double, long,
        // OptionalInt, OptionalDouble, OptionalLong, Optional<T>,
        // List<String>, List<T>, Optional<List<String>>
        // INT
        MatcherAssert.assertThat(config.get("app.page-size").asInt().get(), Matchers.is(20));
        // boolean + DEFAULT
        MatcherAssert.assertThat(config.get("app.storageEnabled").asBoolean().orElse(false), Matchers.is(false));
        // LIST <Integer>
        MatcherAssert.assertThat(config.get("app.basic-range").asList(Integer.class).get(), contains((-20), 20));
        // BUILT-IN mapper for PATH
        MatcherAssert.assertThat(config.get("logging.outputs.file.name").as(Path.class).get(), Matchers.is(Paths.get("target/root.log")));
        // BUILT-IN MAPPERS:
        // Class, BigDecimal, BigInteger, Duration, LocalDate, LocalDateTime,
        // LocalTime, ZonedDateTime, ZoneId, Instant, OffsetTime, OffsetDateTime, File,
        // Path, Charset, URI, URL, Pattern, UUID
    }

    @Test
    public void testListOfConfigs() {
        Config config = Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).build();
        // list of objects
        List<Config> securityProviders = config.get("security.providers").asList(Config.class).get();
        MatcherAssert.assertThat(securityProviders.size(), Matchers.is(2));// with 2 items

        MatcherAssert.assertThat(securityProviders.get(0).get("name").asString(), Matchers.is(ConfigValues.simpleValue("BMCS")));// name of 1st provider

        MatcherAssert.assertThat(securityProviders.get(1).get("name").asString(), Matchers.is(ConfigValues.simpleValue("ForEndUsers")));// name of 2nd provider

    }

    @Test
    public void testNodeChildrenAndTraverse() {
        Config config = Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).build();
        System.out.println("Handlers:");
        // find out all configured logging outputs
        // DIRECT CHILDREN NODES
        config.get("logging.outputs").asNodeList().get().forEach(( node) -> System.out.println(("\t\t" + (node.key()))));
        // i.e. setup Logging Handler ...
        System.out.println("Levels:");
        // find out all logging level configurations:
        // DEPTH-FIRST SEARCH
        config.get("logging").traverse().filter(Config::isLeaf).filter(( node) -> node.key().name().equals("level")).forEach(( node) -> System.out.println(((("\t\t" + (node.key())) + " = ") + (node.asString()))));
        // i.e. setup Logger ...
    }

    @Test
    public void testFallbackConfigSource() {
        Config config = Config.builder().sources(// PROPERTIES first
        // with fallback to HOCON
        ConfigSources.create(ConfigSources.classpath("application.properties"), ConfigSources.classpath("application.conf"))).addParser(ConfigParsers.properties()).addParser(HoconConfigParserBuilder.buildDefault()).build();
        // value from HOCON
        MatcherAssert.assertThat(config.get("app.greeting").asString(), Matchers.is(ConfigValues.simpleValue("Hello")));
        // value from PROPERTIES
        MatcherAssert.assertThat(config.get("app.page-size").asInt(), Matchers.is(ConfigValues.simpleValue(10)));
        // value from PROPERTIES
        MatcherAssert.assertThat(config.get("app.storageEnabled").asBoolean().orElse(false), Matchers.is(true));
    }

    // ADVANCED USE-CASES
    /**
     * My App Config bean.
     */
    public static class AppConfig {
        private String greeting;

        private String name;

        private int pageSize;

        private List<Integer> basicRange;

        private boolean storageEnabled;

        private String storagePassphrase;

        private AppConfig(String greeting, String name, int pageSize, List<Integer> basicRange, boolean storageEnabled, String storagePassphrase) {
            this.greeting = greeting;
            this.name = name;
            this.pageSize = pageSize;
            this.basicRange = basicRange;
            this.storageEnabled = storageEnabled;
            this.storagePassphrase = storagePassphrase;
        }

        public String getGreeting() {
            return greeting;
        }

        public String getName() {
            return name;
        }

        public int getPageSize() {
            return pageSize;
        }

        public List<Integer> getBasicRange() {
            return basicRange;
        }

        public boolean isStorageEnabled() {
            return storageEnabled;
        }

        public String getStoragePassphrase() {
            return storagePassphrase;
        }
    }

    /**
     * Custom config mapper for {@link AppConfig} type.
     */
    public static class AppConfigMapper implements Function<Config, DemoTest.AppConfig> {
        @Override
        public DemoTest.AppConfig apply(Config node) throws ConfigMappingException, MissingValueException {
            String greeting = node.get("greeting").asString().get();
            String name = node.get("name").asString().get();
            int pageSize = node.get("page-size").asInt().get();
            List<Integer> basicRange = node.get("basic-range").asList(Integer.class).get();
            boolean storageEnabled = node.get("storageEnabled").asBoolean().orElse(false);
            String storagePassphrase = node.get("storagePassphrase").asString().get();
            return new DemoTest.AppConfig(greeting, name, pageSize, basicRange, storageEnabled, storagePassphrase);
        }
    }

    @Test
    public void testUseAppConfigMapper() {
        Config config = Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).disableValueResolving().build();
        DemoTest.AppConfig appConfig = // MAP using provided Mapper
        config.get("app").as(new DemoTest.AppConfigMapper()).get();
        MatcherAssert.assertThat(appConfig.getGreeting(), Matchers.is("Hello"));
        MatcherAssert.assertThat(appConfig.getName(), Matchers.is("Demo"));
        MatcherAssert.assertThat(appConfig.getPageSize(), Matchers.is(20));
        MatcherAssert.assertThat(appConfig.getBasicRange(), contains((-20), 20));
        MatcherAssert.assertThat(appConfig.isStorageEnabled(), Matchers.is(false));
        MatcherAssert.assertThat(appConfig.getStoragePassphrase(), Matchers.is("${AES=thisIsEncriptedPassphrase}"));
    }

    @Test
    public void testRegisterAppConfigMapper() {
        Config config = Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).addMapper(DemoTest.AppConfig.class, new DemoTest.AppConfigMapper()).disableValueResolving().build();
        DemoTest.AppConfig appConfig = // get AS type
        config.get("app").as(DemoTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getGreeting(), Matchers.is("Hello"));
        MatcherAssert.assertThat(appConfig.getName(), Matchers.is("Demo"));
        MatcherAssert.assertThat(appConfig.getPageSize(), Matchers.is(20));
        MatcherAssert.assertThat(appConfig.getBasicRange(), contains((-20), 20));
        MatcherAssert.assertThat(appConfig.isStorageEnabled(), Matchers.is(false));
        MatcherAssert.assertThat(appConfig.getStoragePassphrase(), Matchers.is("${AES=thisIsEncriptedPassphrase}"));
    }

    @Test
    public void testSecurityFilter() {
        Config config = // custom config filter
        Config.builder().sources(ConfigSources.classpath("application.conf")).addParser(HoconConfigParserBuilder.buildDefault()).addFilter(new SecurityConfigFilter()).disableValueResolving().build();
        // decrypted passphrase
        MatcherAssert.assertThat(config.get("app.storagePassphrase").asString(), Matchers.is(ConfigValues.simpleValue("Password1.")));
    }
}
