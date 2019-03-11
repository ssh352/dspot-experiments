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
package org.apache.flink.runtime.instance;


import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.jobmanager.slots.ActorTaskManagerGateway;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testutils.CommonTestUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link org.apache.flink.runtime.instance.InstanceManager}.
 */
public class InstanceManagerTest extends TestLogger {
    static ActorSystem system;

    static UUID leaderSessionID = UUID.randomUUID();

    @Test
    public void testInstanceRegistering() {
        try {
            InstanceManager cm = new InstanceManager();
            final int dataPort = 20000;
            HardwareDescription hardwareDescription = HardwareDescription.extractFromSystem(4096);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // register three instances
            ResourceID resID1 = ResourceID.generate();
            ResourceID resID2 = ResourceID.generate();
            ResourceID resID3 = ResourceID.generate();
            TaskManagerLocation ici1 = new TaskManagerLocation(resID1, address, dataPort);
            TaskManagerLocation ici2 = new TaskManagerLocation(resID2, address, (dataPort + 15));
            TaskManagerLocation ici3 = new TaskManagerLocation(resID3, address, (dataPort + 30));
            final JavaTestKit probe1 = new JavaTestKit(InstanceManagerTest.system);
            final JavaTestKit probe2 = new JavaTestKit(InstanceManagerTest.system);
            final JavaTestKit probe3 = new JavaTestKit(InstanceManagerTest.system);
            cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe1.getRef(), InstanceManagerTest.leaderSessionID)), ici1, hardwareDescription, 1);
            cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe2.getRef(), InstanceManagerTest.leaderSessionID)), ici2, hardwareDescription, 2);
            cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe3.getRef(), InstanceManagerTest.leaderSessionID)), ici3, hardwareDescription, 5);
            Assert.assertEquals(3, cm.getNumberOfRegisteredTaskManagers());
            Assert.assertEquals(8, cm.getTotalNumberOfSlots());
            Collection<Instance> instances = cm.getAllRegisteredInstances();
            Set<TaskManagerLocation> taskManagerLocations = new HashSet<TaskManagerLocation>();
            for (Instance instance : instances) {
                taskManagerLocations.add(instance.getTaskManagerLocation());
            }
            Assert.assertTrue(taskManagerLocations.contains(ici1));
            Assert.assertTrue(taskManagerLocations.contains(ici2));
            Assert.assertTrue(taskManagerLocations.contains(ici3));
            cm.shutdown();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test erroneous: " + (e.getMessage())));
        }
    }

    @Test
    public void testRegisteringAlreadyRegistered() {
        try {
            InstanceManager cm = new InstanceManager();
            final int dataPort = 20000;
            ResourceID resID1 = ResourceID.generate();
            ResourceID resID2 = ResourceID.generate();
            HardwareDescription resources = HardwareDescription.extractFromSystem(4096);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            TaskManagerLocation ici = new TaskManagerLocation(resID1, address, dataPort);
            JavaTestKit probe = new JavaTestKit(InstanceManagerTest.system);
            cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe.getRef(), InstanceManagerTest.leaderSessionID)), ici, resources, 1);
            Assert.assertEquals(1, cm.getNumberOfRegisteredTaskManagers());
            Assert.assertEquals(1, cm.getTotalNumberOfSlots());
            try {
                cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe.getRef(), InstanceManagerTest.leaderSessionID)), ici, resources, 1);
            } catch (Exception e) {
                // good
            }
            // check for correct number of registered instances
            Assert.assertEquals(1, cm.getNumberOfRegisteredTaskManagers());
            Assert.assertEquals(1, cm.getTotalNumberOfSlots());
            cm.shutdown();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test erroneous: " + (e.getMessage())));
        }
    }

    @Test
    public void testReportHeartbeat() {
        try {
            InstanceManager cm = new InstanceManager();
            final int dataPort = 20000;
            ResourceID resID1 = ResourceID.generate();
            ResourceID resID2 = ResourceID.generate();
            ResourceID resID3 = ResourceID.generate();
            HardwareDescription hardwareDescription = HardwareDescription.extractFromSystem(4096);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // register three instances
            TaskManagerLocation ici1 = new TaskManagerLocation(resID1, address, dataPort);
            TaskManagerLocation ici2 = new TaskManagerLocation(resID2, address, (dataPort + 1));
            TaskManagerLocation ici3 = new TaskManagerLocation(resID3, address, (dataPort + 2));
            JavaTestKit probe1 = new JavaTestKit(InstanceManagerTest.system);
            JavaTestKit probe2 = new JavaTestKit(InstanceManagerTest.system);
            JavaTestKit probe3 = new JavaTestKit(InstanceManagerTest.system);
            InstanceID instanceID1 = cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe1.getRef(), InstanceManagerTest.leaderSessionID)), ici1, hardwareDescription, 1);
            InstanceID instanceID2 = cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe2.getRef(), InstanceManagerTest.leaderSessionID)), ici2, hardwareDescription, 1);
            InstanceID instanceID3 = cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe3.getRef(), InstanceManagerTest.leaderSessionID)), ici3, hardwareDescription, 1);
            // report some immediate heart beats
            Assert.assertTrue(cm.reportHeartBeat(instanceID1));
            Assert.assertTrue(cm.reportHeartBeat(instanceID2));
            Assert.assertTrue(cm.reportHeartBeat(instanceID3));
            // report heart beat for non-existing instance
            Assert.assertFalse(cm.reportHeartBeat(new InstanceID()));
            final long WAIT = 200;
            CommonTestUtils.sleepUninterruptibly(WAIT);
            Iterator<Instance> it = cm.getAllRegisteredInstances().iterator();
            Instance instance1 = it.next();
            long h1 = instance1.getLastHeartBeat();
            long h2 = it.next().getLastHeartBeat();
            long h3 = it.next().getLastHeartBeat();
            // send one heart beat again and verify that the
            Assert.assertTrue(cm.reportHeartBeat(instance1.getId()));
            long newH1 = instance1.getLastHeartBeat();
            long now = System.currentTimeMillis();
            Assert.assertTrue(((now - h1) >= WAIT));
            Assert.assertTrue(((now - h2) >= WAIT));
            Assert.assertTrue(((now - h3) >= WAIT));
            Assert.assertTrue(((now - newH1) <= WAIT));
            cm.shutdown();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test erroneous: " + (e.getMessage())));
        }
    }

    @Test
    public void testShutdown() {
        try {
            InstanceManager cm = new InstanceManager();
            cm.shutdown();
            try {
                ResourceID resID = ResourceID.generate();
                HardwareDescription resources = HardwareDescription.extractFromSystem(4096);
                InetAddress address = InetAddress.getByName("127.0.0.1");
                TaskManagerLocation ici = new TaskManagerLocation(resID, address, 20000);
                JavaTestKit probe = new JavaTestKit(InstanceManagerTest.system);
                cm.registerTaskManager(new ActorTaskManagerGateway(new AkkaActorGateway(probe.getRef(), InstanceManagerTest.leaderSessionID)), ici, resources, 1);
                Assert.fail("Should raise exception in shutdown state");
            } catch (IllegalStateException e) {
                // expected
            }
            Assert.assertFalse(cm.reportHeartBeat(new InstanceID()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test erroneous: " + (e.getMessage())));
        }
    }
}

