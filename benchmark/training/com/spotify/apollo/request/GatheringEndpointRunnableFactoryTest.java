/**
 * -\-\-
 * Spotify Apollo API Implementations
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
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
package com.spotify.apollo.request;


import com.spotify.apollo.RequestContext;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.dispatch.EndpointInfo;
import com.spotify.apollo.meta.IncomingCallsGatherer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GatheringEndpointRunnableFactoryTest {
    @Mock
    IncomingCallsGatherer incomingCallsGatherer;

    @Mock
    OngoingRequest ongoingRequest;

    @Mock
    RequestContext requestContext;

    @Mock
    Endpoint endpoint;

    @Mock
    EndpointInfo info;

    @Mock
    EndpointRunnableFactory delegate;

    @Mock
    Runnable delegateRunnable;

    GatheringEndpointRunnableFactory endpointRunnableFactory;

    @Test
    public void shouldRunDelegate() throws Exception {
        endpointRunnableFactory.create(ongoingRequest, requestContext, endpoint).run();
        Mockito.verify(delegateRunnable).run();
    }

    @Test
    public void shouldGatherCalls() throws Exception {
        endpointRunnableFactory.create(ongoingRequest, requestContext, endpoint).run();
        Mockito.verify(incomingCallsGatherer).gatherIncomingCall(ArgumentMatchers.eq(ongoingRequest), ArgumentMatchers.eq(endpoint));
    }
}

