/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.error.uri;


import java.net.URI;
import java.net.URL;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.junit.jupiter.api.Test;


public class ShouldHaveAuthority_create_Test {
    @Test
    public void should_create_error_message_for_uri() throws Exception {
        URI uri = new URI("http://assertj.org:8080/news");
        String error = ShouldHaveAuthority.shouldHaveAuthority(uri, "foo.org").create(new TestDescription("TEST"));
        Assertions.assertThat(error).isEqualTo(String.format(("[TEST] %n" + ((((("Expecting authority of%n" + "  <http://assertj.org:8080/news>%n") + "to be:%n") + "  <\"foo.org\">%n") + "but was:%n") + "  <\"assertj.org:8080\">"))));
    }

    @Test
    public void should_create_error_message_for_url() throws Exception {
        URL url = new URL("http://assertj.org:8080/news");
        String error = ShouldHaveAuthority.shouldHaveAuthority(url, "foo.org").create(new TestDescription("TEST"));
        Assertions.assertThat(error).isEqualTo(String.format(("[TEST] %n" + ((((("Expecting authority of%n" + "  <http://assertj.org:8080/news>%n") + "to be:%n") + "  <\"foo.org\">%n") + "but was:%n") + "  <\"assertj.org:8080\">"))));
    }
}

