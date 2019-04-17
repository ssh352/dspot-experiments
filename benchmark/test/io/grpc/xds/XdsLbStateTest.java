/**
 * Copyright 2019 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.xds;


import Status.Code.CANCELLED;
import io.envoyproxy.envoy.api.v2.DiscoveryRequest;
import io.grpc.LoadBalancer.Helper;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.internal.FakeClock;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.xds.XdsComms.AdsStreamCallback;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link XdsLbState}.
 */
@RunWith(JUnit4.class)
public class XdsLbStateTest {
    @Rule
    public final GrpcCleanupRule cleanupRule = new GrpcCleanupRule();

    @Mock
    private Helper helper;

    @Mock
    private AdsStreamCallback adsStreamCallback;

    private final FakeClock fakeClock = new FakeClock();

    private final StreamRecorder<DiscoveryRequest> streamRecorder = StreamRecorder.create();

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private XdsComms xdsComms;

    private ManagedChannel channel;

    @Test
    public void shutdownLbComm() throws Exception {
        xdsComms.shutdownChannel();
        Assert.assertTrue(channel.isShutdown());
        Assert.assertTrue(streamRecorder.awaitCompletion(1, TimeUnit.SECONDS));
        Assert.assertEquals(CANCELLED, Status.fromThrowable(streamRecorder.getError()).getCode());
    }

    @Test
    public void shutdownLbRpc_verifyChannelNotShutdown() throws Exception {
        xdsComms.shutdownLbRpc("shutdown msg1");
        Assert.assertTrue(streamRecorder.awaitCompletion(1, TimeUnit.SECONDS));
        Assert.assertEquals(CANCELLED, Status.fromThrowable(streamRecorder.getError()).getCode());
        Assert.assertFalse(channel.isShutdown());
    }

    @Test
    public void shutdownAndReleaseXdsCommsDoesShutdown() {
        XdsLbState xdsLbState = Mockito.mock(XdsLbState.class);
        xdsLbState.shutdownAndReleaseXdsComms();
        Mockito.verify(xdsLbState).shutdown();
    }
}
