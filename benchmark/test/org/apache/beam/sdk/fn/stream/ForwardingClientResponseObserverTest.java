/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.fn.stream;


import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.ClientCallStreamObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.ClientResponseObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link ForwardingClientResponseObserver}.
 */
@RunWith(JUnit4.class)
public class ForwardingClientResponseObserverTest {
    @Test
    public void testCallsAreForwardedAndOnReadyHandlerBound() {
        @SuppressWarnings("unchecked")
        StreamObserver<Object> delegateObserver = Mockito.mock(StreamObserver.class);
        @SuppressWarnings("unchecked")
        ClientCallStreamObserver<Object> callStreamObserver = Mockito.mock(ClientCallStreamObserver.class);
        Runnable onReadyHandler = () -> {
        };
        ClientResponseObserver<Object, Object> observer = new ForwardingClientResponseObserver(delegateObserver, onReadyHandler);
        observer.onNext("A");
        Mockito.verify(delegateObserver).onNext("A");
        Throwable t = new RuntimeException();
        observer.onError(t);
        Mockito.verify(delegateObserver).onError(t);
        observer.onCompleted();
        Mockito.verify(delegateObserver).onCompleted();
        observer.beforeStart(callStreamObserver);
        Mockito.verify(callStreamObserver).setOnReadyHandler(onReadyHandler);
        Mockito.verifyNoMoreInteractions(delegateObserver, callStreamObserver);
    }
}

