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
package com.linecorp.armeria.client;


import HttpMethod.GET;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.metric.NoopMeterRegistry;
import java.net.URI;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ClientFactory.DEFAULT;


public class DefaultHttpClientTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testConcatenateRequestPath() throws Exception {
        final String clientUriPath = "http://127.0.0.1/hello";
        final String requestPath = "world/test?q1=foo";
        final Client<HttpRequest, HttpResponse> mockClientDelegate = Mockito.mock(Client.class);
        final ClientBuilderParams clientBuilderParams = new DefaultClientBuilderParams(DEFAULT, new URI(clientUriPath), HttpClient.class, ClientOptions.DEFAULT);
        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient(clientBuilderParams, mockClientDelegate, NoopMeterRegistry.get(), com.linecorp.armeria.common.SessionProtocol.of("http"), Endpoint.of("127.0.0.1"));
        defaultHttpClient.execute(HttpRequest.of(HttpHeaders.of(GET, requestPath)));
        final ArgumentCaptor<HttpRequest> argCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(mockClientDelegate).execute(ArgumentMatchers.any(ClientRequestContext.class), argCaptor.capture());
        final String concatPath = argCaptor.getValue().path();
        assertThat(concatPath).isEqualTo("/hello/world/test?q1=foo");
    }
}

