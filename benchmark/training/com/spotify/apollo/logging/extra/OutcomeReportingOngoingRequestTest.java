/**
 * -\-\-
 * Spotify Apollo Extra
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.logging.extra;


import com.spotify.apollo.Request;
import com.spotify.apollo.RequestMetadata;
import com.spotify.apollo.Response;
import com.spotify.apollo.request.OngoingRequest;
import java.util.Optional;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OutcomeReportingOngoingRequestTest {
    private OutcomeReportingOngoingRequest ongoingRequest;

    private Request request;

    private OutcomeReportingOngoingRequestTest.Logger logger;

    @Test
    public void shouldInvokeLoggerWhenReplyIsMade() throws Exception {
        Response<ByteString> response = Response.forPayload(ByteString.encodeUtf8("good stuff"));
        ongoingRequest.reply(response);
        Assert.assertThat(logger.request, CoreMatchers.is(ongoingRequest));
        Assert.assertThat(logger.response, CoreMatchers.is(Optional.of(response)));
    }

    @Test
    public void shouldInvokeLoggerWhenRequestIsDropped() throws Exception {
        ongoingRequest.drop();
        Assert.assertThat(logger.request, CoreMatchers.is(ongoingRequest));
        Assert.assertThat(logger.response, CoreMatchers.is(Optional.empty()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class Logger implements RequestOutcomeConsumer {
        private OngoingRequest request;

        private Optional<Response<ByteString>> response;

        @Override
        public void accept(OngoingRequest request, Optional<Response<ByteString>> response) {
            this.request = request;
            this.response = response;
        }
    }

    private class FakeOngoingRequest implements OngoingRequest {
        @Override
        public Request request() {
            return request;
        }

        @Override
        public void reply(Response<ByteString> response) {
        }

        @Override
        public void drop() {
        }

        @Override
        public boolean isExpired() {
            return false;
        }

        @Override
        public RequestMetadata metadata() {
            return null;
        }
    }
}

