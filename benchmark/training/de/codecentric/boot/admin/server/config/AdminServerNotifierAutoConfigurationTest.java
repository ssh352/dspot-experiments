/**
 * Copyright 2014-2019 the original author or authors.
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
package de.codecentric.boot.admin.server.config;


import de.codecentric.boot.admin.server.notify.TestNotifier;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSenderImpl;


public class AdminServerNotifierAutoConfigurationTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class, ClientHttpConnectorAutoConfiguration.class, WebClientAutoConfiguration.class, HazelcastAutoConfiguration.class, WebMvcAutoConfiguration.class, AdminServerAutoConfiguration.class, AdminServerNotifierAutoConfiguration.class)).withUserConfiguration(AdminServerMarkerConfiguration.class);

    @Test
    public void test_notifierListener() {
        this.contextRunner.withUserConfiguration(AdminServerNotifierAutoConfigurationTest.TestSingleNotifierConfig.class).run(( context) -> {
            assertThat(context).getBean(.class).isInstanceOf(.class);
            assertThat(context).getBeans(.class).hasSize(1);
        });
    }

    @Test
    public void test_no_notifierListener() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void test_mail() {
        this.contextRunner.withUserConfiguration(AdminServerNotifierAutoConfigurationTest.MailSenderConfig.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void test_hipchat() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.hipchat.url:http://example.com").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_letschat() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.letschat.url:http://example.com").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_slack() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.slack.webhook-url:http://example.com").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_pagerduty() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.pagerduty.service-key:foo").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_opsgenie() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.opsgenie.api-key:foo").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_ms_teams() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.ms-teams.webhook-url:http://example.com").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_telegram() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.telegram.auth-token:123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_discord() {
        this.contextRunner.withPropertyValues("spring.boot.admin.notify.discord.webhook-url:http://example.com").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void test_multipleNotifiers() {
        this.contextRunner.withUserConfiguration(AdminServerNotifierAutoConfigurationTest.TestMultipleNotifierConfig.class).run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertThat(context).getBeans(.class).hasSize(3);
        });
    }

    @Test
    public void test_multipleNotifiersWithPrimary() {
        this.contextRunner.withUserConfiguration(AdminServerNotifierAutoConfigurationTest.TestMultipleWithPrimaryNotifierConfig.class).run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertThat(context).getBeans(.class).hasSize(2);
        });
    }

    public static class TestSingleNotifierConfig {
        @Bean
        @Qualifier("testNotifier")
        public TestNotifier testNotifier() {
            return new TestNotifier();
        }
    }

    private static class MailSenderConfig {
        @Bean
        public JavaMailSenderImpl mailSender() {
            return new JavaMailSenderImpl();
        }
    }

    private static class TestMultipleNotifierConfig {
        @Bean
        @Qualifier("testNotifier1")
        public TestNotifier testNotifier1() {
            return new TestNotifier();
        }

        @Bean
        @Qualifier("testNotifier2")
        public TestNotifier testNotifier2() {
            return new TestNotifier();
        }
    }

    private static class TestMultipleWithPrimaryNotifierConfig {
        @Bean
        @Primary
        @Qualifier("testNotifier")
        public TestNotifier testNotifierPrimary() {
            return new TestNotifier();
        }

        @Bean
        @Qualifier("testNotifier3")
        public TestNotifier testNotifier2() {
            return new TestNotifier();
        }
    }
}
