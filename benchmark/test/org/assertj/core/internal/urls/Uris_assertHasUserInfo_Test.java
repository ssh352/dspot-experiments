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
import org.assertj.core.error.uri.ShouldHaveUserInfo;
import org.assertj.core.internal.UrisBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Uris_assertHasUserInfo_Test extends UrisBaseTest {
    @Test
    public void should_pass_if_actual_uri_has_no_user_info_and_given_user_info_is_null() throws URISyntaxException {
        uris.assertHasUserInfo(info, new URI("http://www.helloworld.org/index.html"), null);
    }

    @Test
    public void should_pass_if_actual_uri_has_the_expected_user_info() throws URISyntaxException {
        uris.assertHasUserInfo(info, new URI("http://test:pass@www.helloworld.org/index.html"), "test:pass");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> uris.assertHasUserInfo(info, null, "http://test:pass@www.helloworld.org/index.html")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_URI_user_info_is_not_the_expected_user_info() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://test:pass@assertj.org/news");
        String expectedUserInfo = "test:ok";
        try {
            uris.assertHasUserInfo(info, uri, expectedUserInfo);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveUserInfo.shouldHaveUserInfo(uri, expectedUserInfo));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_no_user_info_and_expected_user_info_is_not_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://assertj.org/news");
        String expectedUserInfo = "test:pass";
        try {
            uris.assertHasUserInfo(info, uri, expectedUserInfo);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveUserInfo.shouldHaveUserInfo(uri, expectedUserInfo));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_a_user_info_and_expected_user_info_is_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://test:pass@assertj.org");
        String expectedUserInfo = null;
        try {
            uris.assertHasUserInfo(info, uri, expectedUserInfo);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveUserInfo.shouldHaveUserInfo(uri, expectedUserInfo));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

