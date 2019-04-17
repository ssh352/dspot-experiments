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
package org.springframework.web.reactive.function.server;


import MediaType.TEXT_HTML;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class LocaleContextResolverIntegrationTests extends AbstractRouterFunctionIntegrationTests {
    private final WebClient webClient = WebClient.create();

    @Test
    public void fixedLocale() {
        Mono<ClientResponse> result = webClient.get().uri((("http://localhost:" + (this.port)) + "/")).exchange();
        StepVerifier.create(result).consumeNextWith(( response) -> {
            assertEquals(HttpStatus.OK, response.statusCode());
            assertEquals(Locale.GERMANY, response.headers().asHttpHeaders().getContentLanguage());
        }).verifyComplete();
    }

    private static class DummyViewResolver implements ViewResolver {
        @Override
        public Mono<View> resolveViewName(String viewName, Locale locale) {
            return Mono.just(new LocaleContextResolverIntegrationTests.DummyView(locale));
        }
    }

    private static class DummyView implements View {
        private final Locale locale;

        public DummyView(Locale locale) {
            this.locale = locale;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return Collections.singletonList(TEXT_HTML);
        }

        @Override
        public Mono<Void> render(@Nullable
        Map<String, ?> model, @Nullable
        MediaType contentType, ServerWebExchange exchange) {
            exchange.getResponse().getHeaders().setContentLanguage(locale);
            return Mono.empty();
        }
    }
}
