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
package org.assertj.core.internal.urls;


import java.net.URI;
import java.net.URISyntaxException;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.uri.ShouldHavePort;
import org.assertj.core.internal.UrisBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Uris_assertHasPort_Test extends UrisBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> uris.assertHasPort(info, null, 8080)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_uri_has_the_given_port() throws URISyntaxException {
        uris.assertHasPort(info, new URI("http://example.com:8080/pages/"), 8080);
    }

    @Test
    public void should_fail_if_actual_URI_port_is_not_the_given_port() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://example.com:8080/pages/");
        int expectedPort = 8888;
        try {
            uris.assertHasPort(info, uri, expectedPort);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHavePort.shouldHavePort(uri, expectedPort));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

