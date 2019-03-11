/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common;


import HttpData.EMPTY_DATA;
import HttpHeaders.EMPTY_HEADERS;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpStatus.CONTINUE;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.RESET_CONTENT;
import com.google.common.collect.ImmutableList;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Test;


public class DefaultAggregatedHttpMessageTest {
    @Test
    public void toHttpRequest() throws Exception {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(POST, "/foo", MediaType.PLAIN_TEXT_UTF_8, "bar");
        final HttpRequest req = HttpRequest.of(aReq);
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(req.headers()).isEqualTo(HttpHeaders.of(POST, "/foo").contentType(MediaType.PLAIN_TEXT_UTF_8).setInt(HttpHeaderNames.CONTENT_LENGTH, 3));
        assertThat(drained).containsExactly(HttpData.of(StandardCharsets.UTF_8, "bar"));
    }

    @Test
    public void toHttpRequestWithoutContent() throws Exception {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(GET, "/bar");
        final HttpRequest req = HttpRequest.of(aReq);
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(req.headers()).isEqualTo(HttpHeaders.of(GET, "/bar"));
        assertThat(drained).isEmpty();
    }

    @Test
    public void toHttpRequestWithTrailingHeaders() throws Exception {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(PUT, "/baz", MediaType.PLAIN_TEXT_UTF_8, HttpData.ofUtf8("bar"), HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "37b51d194a7513e45b56f6524f2d51f2"));
        final HttpRequest req = HttpRequest.of(aReq);
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(req.headers()).isEqualTo(HttpHeaders.of(PUT, "/baz").contentType(MediaType.PLAIN_TEXT_UTF_8).setInt(HttpHeaderNames.CONTENT_LENGTH, 3));
        assertThat(drained).containsExactly(HttpData.of(StandardCharsets.UTF_8, "bar"), HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "37b51d194a7513e45b56f6524f2d51f2"));
    }

    @Test
    public void toHttpRequestAgainstResponse() {
        final AggregatedHttpMessage aRes = AggregatedHttpMessage.of(200);
        assertThatThrownBy(() -> HttpRequest.of(aRes)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void toHttpRequestWithoutPath() {
        // Method only
        assertThatThrownBy(() -> HttpRequest.of(AggregatedHttpMessage.of(HttpHeaders.of(HttpHeaderNames.METHOD, "GET")))).isInstanceOf(IllegalStateException.class);
        // Path only
        assertThatThrownBy(() -> HttpRequest.of(AggregatedHttpMessage.of(HttpHeaders.of(HttpHeaderNames.PATH, "/charlie")))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void toHttpResponse() throws Exception {
        final AggregatedHttpMessage aRes = AggregatedHttpMessage.of(OK, MediaType.PLAIN_TEXT_UTF_8, "alice");
        final HttpResponse res = HttpResponse.of(aRes);
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(OK).contentType(MediaType.PLAIN_TEXT_UTF_8).setInt(HttpHeaderNames.CONTENT_LENGTH, 5), HttpData.of(StandardCharsets.UTF_8, "alice"));
    }

    @Test
    public void toHttpResponseWithoutContent() throws Exception {
        final AggregatedHttpMessage aRes = AggregatedHttpMessage.of(OK, MediaType.PLAIN_TEXT_UTF_8, EMPTY_DATA);
        final HttpResponse res = HttpResponse.of(aRes);
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(OK).contentType(MediaType.PLAIN_TEXT_UTF_8).setInt(HttpHeaderNames.CONTENT_LENGTH, 0));
    }

    @Test
    public void toHttpResponseWithTrailingHeaders() throws Exception {
        final AggregatedHttpMessage aRes = AggregatedHttpMessage.of(OK, MediaType.PLAIN_TEXT_UTF_8, HttpData.ofUtf8("bob"), HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
        final HttpResponse res = HttpResponse.of(aRes);
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(OK).contentType(MediaType.PLAIN_TEXT_UTF_8), HttpData.of(StandardCharsets.UTF_8, "bob"), HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
    }

    @Test
    public void toHttpResponseWithInformationals() throws Exception {
        final AggregatedHttpMessage aRes = AggregatedHttpMessage.of(ImmutableList.of(HttpHeaders.of(CONTINUE)), HttpHeaders.of(OK), EMPTY_DATA, EMPTY_HEADERS);
        final HttpResponse res = HttpResponse.of(aRes);
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(CONTINUE), HttpHeaders.of(OK).setInt(HttpHeaderNames.CONTENT_LENGTH, 0));
    }

    @Test
    public void errorWhenContentOrTrailingHeadersShouldBeEmpty() throws Exception {
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(CONTINUE, HttpData.ofUtf8("bob"), EMPTY_HEADERS);
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(NO_CONTENT, HttpData.ofUtf8("bob"), EMPTY_HEADERS);
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(RESET_CONTENT, HttpData.ofUtf8("bob"), EMPTY_HEADERS);
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(NOT_MODIFIED, HttpData.ofUtf8("bob"), EMPTY_HEADERS);
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(CONTINUE, EMPTY_DATA, HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(NO_CONTENT, EMPTY_DATA, HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(RESET_CONTENT, EMPTY_DATA, HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
        DefaultAggregatedHttpMessageTest.contentAndTrailingHeadersShouldBeEmpty(NOT_MODIFIED, EMPTY_DATA, HttpHeaders.of(HttpHeaderNames.CONTENT_MD5, "9f9d51bc70ef21ca5c14f307980a29d8"));
    }

    @Test
    public void contentLengthIsNotSetWhen1xxOr204Or205() {
        HttpHeaders headers = HttpHeaders.of(CONTINUE).addInt(HttpHeaderNames.CONTENT_LENGTH, 100);
        assertThat(AggregatedHttpMessage.of(headers).headers().get(HttpHeaderNames.CONTENT_LENGTH)).isNull();
        headers = HttpHeaders.of(NO_CONTENT).addInt(HttpHeaderNames.CONTENT_LENGTH, 100);
        assertThat(AggregatedHttpMessage.of(headers).headers().get(HttpHeaderNames.CONTENT_LENGTH)).isNull();
        headers = HttpHeaders.of(RESET_CONTENT).addInt(HttpHeaderNames.CONTENT_LENGTH, 100);
        assertThat(AggregatedHttpMessage.of(headers).headers().get(HttpHeaderNames.CONTENT_LENGTH)).isNull();
        // 304 response can have the 'Content-length' header when it is a response to a conditional
        // GET request. See https://tools.ietf.org/html/rfc7230#section-3.3.2
        headers = HttpHeaders.of(NOT_MODIFIED).addInt(HttpHeaderNames.CONTENT_LENGTH, 100);
        assertThat(AggregatedHttpMessage.of(headers).headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(100);
    }

    @Test
    public void contentLengthIsSet() {
        AggregatedHttpMessage msg = AggregatedHttpMessage.of(OK);
        assertThat(msg.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(6);// the length of status.toHttpData()

        msg = AggregatedHttpMessage.of(OK, MediaType.PLAIN_TEXT_UTF_8, HttpData.ofUtf8("foo"));
        assertThat(msg.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(3);
        msg = AggregatedHttpMessage.of(OK, MediaType.PLAIN_TEXT_UTF_8, HttpData.ofUtf8(""));
        assertThat(msg.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(0);
        final HttpHeaders headers = HttpHeaders.of(OK).addInt(HttpHeaderNames.CONTENT_LENGTH, 1000000);
        // It can have 'Content-length' even though it does not have content, because it can be a response
        // to a HEAD request.
        assertThat(AggregatedHttpMessage.of(headers).headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(1000000);
        msg = AggregatedHttpMessage.of(headers, HttpData.ofUtf8("foo"));
        assertThat(msg.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)).isEqualTo(3);// The length is reset to 3 from 1000000.

    }

    @Test
    public void toHttpResponseAgainstRequest() {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(GET, "/qux");
        assertThatThrownBy(() -> HttpResponse.of(aReq)).isInstanceOf(IllegalStateException.class);
    }
}

