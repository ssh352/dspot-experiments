/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.servlet.view.script;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;


/**
 * Unit tests for Kotlin script templates running on Kotlin JSR-223 support.
 *
 * @author Sebastien Deleuze
 */
public class KotlinScriptTemplateTests {
    private WebApplicationContext webAppContext;

    private ServletContext servletContext;

    @Test
    public void renderTemplateWithFrenchLocale() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("foo", "Foo");
        String url = "org/springframework/web/servlet/view/script/kotlin/template.kts";
        MockHttpServletResponse response = render(url, model, Locale.FRENCH, KotlinScriptTemplateTests.ScriptTemplatingConfiguration.class);
        Assert.assertEquals("<html><body>\n<p>Bonjour Foo</p>\n</body></html>", response.getContentAsString());
    }

    @Test
    public void renderTemplateWithEnglishLocale() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("foo", "Foo");
        String url = "org/springframework/web/servlet/view/script/kotlin/template.kts";
        MockHttpServletResponse response = render(url, model, Locale.ENGLISH, KotlinScriptTemplateTests.ScriptTemplatingConfiguration.class);
        Assert.assertEquals("<html><body>\n<p>Hello Foo</p>\n</body></html>", response.getContentAsString());
    }

    @Test
    public void renderTemplateWithoutRenderFunction() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("header", "<html><body>");
        model.put("hello", "Hello");
        model.put("foo", "Foo");
        model.put("footer", "</body></html>");
        MockHttpServletResponse response = render("org/springframework/web/servlet/view/script/kotlin/eval.kts", model, Locale.ENGLISH, KotlinScriptTemplateTests.ScriptTemplatingConfigurationWithoutRenderFunction.class);
        Assert.assertEquals("<html><body>\n<p>Hello Foo</p>\n</body></html>", response.getContentAsString());
    }

    @Configuration
    static class ScriptTemplatingConfiguration {
        @Bean
        public ScriptTemplateConfigurer kotlinScriptConfigurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setEngineName("kotlin");
            configurer.setScripts("org/springframework/web/servlet/view/script/kotlin/render.kts");
            configurer.setRenderFunction("render");
            return configurer;
        }

        @Bean
        public ResourceBundleMessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("org/springframework/web/servlet/view/script/messages");
            return messageSource;
        }
    }

    @Configuration
    static class ScriptTemplatingConfigurationWithoutRenderFunction {
        @Bean
        public ScriptTemplateConfigurer kotlinScriptConfigurer() {
            return new ScriptTemplateConfigurer("kotlin");
        }
    }
}
