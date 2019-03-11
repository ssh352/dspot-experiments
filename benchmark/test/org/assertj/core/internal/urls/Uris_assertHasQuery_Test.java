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
import org.assertj.core.error.uri.ShouldHaveQuery;
import org.assertj.core.internal.UrisBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Uris#assertHasQuery(org.assertj.core.api.AssertionInfo, java.net.URI, String)}  </code>
 * .
 *
 * @author Alexander Bischof
 */
public class Uris_assertHasQuery_Test extends UrisBaseTest {
    @Test
    public void should_pass_if_actual_uri_has_the_expected_query() throws URISyntaxException {
        uris.assertHasQuery(info, new URI("http://www.helloworld.org/index.html?type=test"), "type=test");
    }

    @Test
    public void should_pass_if_actual_uri_has_no_query_and_given_is_null() throws URISyntaxException {
        uris.assertHasQuery(info, new URI("http://www.helloworld.org/index.html"), null);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> uris.assertHasQuery(info, null, "http://www.helloworld.org/index.html?type=test")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_URI_query_is_not_the_given_query() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://assertj.org/news?type=beta");
        String expectedQuery = "type=final";
        try {
            uris.assertHasQuery(info, uri, expectedQuery);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveQuery.shouldHaveQuery(uri, expectedQuery));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_no_query_and_expected_query_is_not_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://assertj.org/news");
        String expectedQuery = "type=final";
        try {
            uris.assertHasQuery(info, uri, expectedQuery);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveQuery.shouldHaveQuery(uri, expectedQuery));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_a_query_and_expected_query_is_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://assertj.org/news?type=beta");
        String expectedQuery = null;
        try {
            uris.assertHasQuery(info, uri, expectedQuery);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveQuery.shouldHaveQuery(uri, expectedQuery));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

