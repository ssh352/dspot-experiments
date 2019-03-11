/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.cluster.messaging.impl;


import Namespaces.BASIC;
import com.google.common.util.concurrent.MoreExecutors;
import io.atomix.cluster.BootstrapService;
import io.atomix.cluster.ClusterMembershipService;
import io.atomix.cluster.ManagedClusterMembershipService;
import io.atomix.cluster.Member;
import io.atomix.cluster.Node;
import io.atomix.cluster.TestBootstrapService;
import io.atomix.cluster.messaging.ClusterEventService;
import io.atomix.cluster.messaging.ManagedClusterEventService;
import io.atomix.cluster.messaging.MessagingService;
import io.atomix.cluster.protocol.HeartbeatMembershipProtocolConfig;
import io.atomix.utils.Version;
import io.atomix.utils.serializer.Serializer;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Cluster event service test.
 */
public class DefaultClusterEventServiceTest {
    private static final Serializer SERIALIZER = Serializer.using(BASIC);

    @Test
    public void testClusterEventService() throws Exception {
        TestMessagingServiceFactory messagingServiceFactory = new TestMessagingServiceFactory();
        TestUnicastServiceFactory unicastServiceFactory = new TestUnicastServiceFactory();
        TestBroadcastServiceFactory broadcastServiceFactory = new TestBroadcastServiceFactory();
        Collection<Node> bootstrapLocations = buildBootstrapNodes(3);
        Member localMember1 = buildNode(1);
        MessagingService messagingService1 = messagingServiceFactory.newMessagingService(localMember1.address()).start().join();
        BootstrapService bootstrapService1 = new TestBootstrapService(messagingService1, unicastServiceFactory.newUnicastService(localMember1.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService1 = new io.atomix.cluster.impl.DefaultClusterMembershipService(localMember1, Version.from("1.0.0"), new io.atomix.cluster.impl.DefaultNodeDiscoveryService(bootstrapService1, localMember1, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService1, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig()));
        ClusterMembershipService clusterMembershipService1 = clusterService1.start().join();
        ManagedClusterEventService clusterEventingService1 = new DefaultClusterEventService(clusterMembershipService1, messagingService1);
        ClusterEventService eventService1 = clusterEventingService1.start().join();
        Member localMember2 = buildNode(2);
        MessagingService messagingService2 = messagingServiceFactory.newMessagingService(localMember2.address()).start().join();
        BootstrapService bootstrapService2 = new TestBootstrapService(messagingService2, unicastServiceFactory.newUnicastService(localMember2.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService2 = new io.atomix.cluster.impl.DefaultClusterMembershipService(localMember2, Version.from("1.0.0"), new io.atomix.cluster.impl.DefaultNodeDiscoveryService(bootstrapService2, localMember2, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService2, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig()));
        ClusterMembershipService clusterMembershipService2 = clusterService2.start().join();
        ManagedClusterEventService clusterEventingService2 = new DefaultClusterEventService(clusterMembershipService2, messagingService2);
        ClusterEventService eventService2 = clusterEventingService2.start().join();
        Member localMember3 = buildNode(3);
        MessagingService messagingService3 = messagingServiceFactory.newMessagingService(localMember3.address()).start().join();
        BootstrapService bootstrapService3 = new TestBootstrapService(messagingService3, unicastServiceFactory.newUnicastService(localMember1.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService3 = new io.atomix.cluster.impl.DefaultClusterMembershipService(localMember3, Version.from("1.0.0"), new io.atomix.cluster.impl.DefaultNodeDiscoveryService(bootstrapService3, localMember3, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService3, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig()));
        ClusterMembershipService clusterMembershipService3 = clusterService3.start().join();
        ManagedClusterEventService clusterEventingService3 = new DefaultClusterEventService(clusterMembershipService3, messagingService3);
        ClusterEventService eventService3 = clusterEventingService3.start().join();
        Thread.sleep(100);
        Set<Integer> events = new CopyOnWriteArraySet<>();
        eventService1.<String>subscribe("test1", DefaultClusterEventServiceTest.SERIALIZER::decode, ( message) -> {
            assertEquals("Hello world!", message);
            events.add(1);
        }, MoreExecutors.directExecutor()).join();
        eventService2.<String>subscribe("test1", DefaultClusterEventServiceTest.SERIALIZER::decode, ( message) -> {
            assertEquals("Hello world!", message);
            events.add(2);
        }, MoreExecutors.directExecutor()).join();
        eventService2.<String>subscribe("test1", DefaultClusterEventServiceTest.SERIALIZER::decode, ( message) -> {
            assertEquals("Hello world!", message);
            events.add(3);
        }, MoreExecutors.directExecutor()).join();
        eventService3.broadcast("test1", "Hello world!", DefaultClusterEventServiceTest.SERIALIZER::encode);
        Thread.sleep(100);
        Assert.assertEquals(3, events.size());
        events.clear();
        eventService3.unicast("test1", "Hello world!");
        Thread.sleep(100);
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(3));
        events.clear();
        eventService3.unicast("test1", "Hello world!");
        Thread.sleep(100);
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(1));
        events.clear();
        eventService3.unicast("test1", "Hello world!");
        Thread.sleep(100);
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(2));
        events.clear();
        eventService3.unicast("test1", "Hello world!");
        Thread.sleep(100);
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(3));
        events.clear();
        eventService1.<String, String>subscribe("test2", DefaultClusterEventServiceTest.SERIALIZER::decode, ( message) -> {
            events.add(1);
            return message;
        }, DefaultClusterEventServiceTest.SERIALIZER::encode, MoreExecutors.directExecutor()).join();
        eventService2.<String, String>subscribe("test2", DefaultClusterEventServiceTest.SERIALIZER::decode, ( message) -> {
            events.add(2);
            return message;
        }, DefaultClusterEventServiceTest.SERIALIZER::encode, MoreExecutors.directExecutor()).join();
        Assert.assertEquals("Hello world!", eventService3.send("test2", "Hello world!").join());
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(1));
        events.clear();
        Assert.assertEquals("Hello world!", eventService3.send("test2", "Hello world!").join());
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(2));
        events.clear();
        Assert.assertEquals("Hello world!", eventService3.send("test2", "Hello world!").join());
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(events.contains(1));
        CompletableFuture.allOf(new CompletableFuture[]{ clusterEventingService1.stop(), clusterEventingService2.stop(), clusterEventingService3.stop() }).join();
        CompletableFuture.allOf(new CompletableFuture[]{ clusterService1.stop(), clusterService2.stop(), clusterService3.stop() }).join();
    }
}

