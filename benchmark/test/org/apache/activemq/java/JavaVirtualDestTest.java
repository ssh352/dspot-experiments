/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.java;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.AbstractVirtualDestTest;
import org.apache.activemq.RuntimeConfigTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.virtual.CompositeQueue;
import org.apache.activemq.broker.region.virtual.FilteredDestination;
import org.apache.activemq.broker.region.virtual.VirtualDestination;
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class JavaVirtualDestTest extends AbstractVirtualDestTest {
    public static final int SLEEP = 2;// seconds


    private JavaRuntimeConfigurationBroker javaConfigBroker;

    @Test
    public void testNew() throws Exception {
        startBroker(new BrokerService());
        Assert.assertTrue("broker alive", brokerService.isStarted());
        // default config has support for VirtualTopic.>
        DestinationInterceptor[] interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("one interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
        VirtualDestinationInterceptor defaultValue = ((VirtualDestinationInterceptor) (interceptors[0]));
        Assert.assertEquals("default names in place", "VirtualTopic.>", defaultValue.getVirtualDestinations()[0].getVirtualDestination().getPhysicalName());
        exerciseVirtualTopic("VirtualTopic.Default");
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        Assert.assertEquals("one interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
        // update will happen on addDestination
        exerciseVirtualTopic("A.Default");
        VirtualDestinationInterceptor newValue = ((VirtualDestinationInterceptor) (interceptors[0]));
        Assert.assertEquals("new names in place", "A.>", defaultValue.getVirtualDestinations()[0].getVirtualDestination().getPhysicalName());
        // apply again - ensure no change
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        Assert.assertSame("same instance", newValue, brokerService.getDestinationInterceptors()[0]);
    }

    @Test
    public void testNewComposite() throws Exception {
        startBroker(new BrokerService());
        Assert.assertTrue("broker alive", brokerService.isStarted());
        CompositeQueue queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.CompositeQueue", Arrays.asList(new ActiveMQQueue("VirtualDestination.QueueConsumer"), new ActiveMQTopic("VirtualDestination.TopicConsumer")));
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ queue });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseCompositeQueue("VirtualDestination.CompositeQueue", "VirtualDestination.QueueConsumer");
    }

    @Test
    public void testNewCompositeApplyImmediately() throws Exception {
        startBroker(new BrokerService());
        Assert.assertTrue("broker alive", brokerService.isStarted());
        CompositeQueue queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.CompositeQueue", Arrays.asList(new ActiveMQQueue("VirtualDestination.QueueConsumer"), new ActiveMQTopic("VirtualDestination.TopicConsumer")));
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ queue }, true);
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseCompositeQueue("VirtualDestination.CompositeQueue", "VirtualDestination.QueueConsumer");
    }

    @Test
    public void testModComposite() throws Exception {
        BrokerService brokerService = new BrokerService();
        CompositeQueue queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.CompositeQueue", Arrays.asList(new ActiveMQQueue("VirtualDestination.QueueConsumer"), new ActiveMQTopic("VirtualDestination.TopicConsumer")));
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ queue }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        exerciseCompositeQueue("VirtualDestination.CompositeQueue", "VirtualDestination.QueueConsumer");
        // Apply updated config
        CompositeQueue newConfig = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.CompositeQueue", false, Arrays.asList(new ActiveMQQueue("VirtualDestination.QueueConsumer"), new ActiveMQTopic("VirtualDestination.TopicConsumer")));
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ newConfig });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseCompositeQueue("VirtualDestination.CompositeQueue", "VirtualDestination.QueueConsumer");
        exerciseCompositeQueue("VirtualDestination.CompositeQueue", "VirtualDestination.CompositeQueue");
    }

    @Test
    public void testNewNoDefaultVirtualTopicSupport() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setUseVirtualTopics(false);
        startBroker(brokerService);
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        DestinationInterceptor[] interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("one interceptor", 0, interceptors.length);
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        // update will happen on addDestination
        exerciseVirtualTopic("A.Default");
        interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("one interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
        // apply new config again, make sure still just 1 interceptor
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        // update will happen on addDestination
        exerciseVirtualTopic("A.Default");
        interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("one interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
    }

    @Test
    public void testNewWithMirrorQueueSupport() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setUseMirroredQueues(true);
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        DestinationInterceptor[] interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("expected interceptor", 2, interceptors.length);
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        // update will happen on addDestination
        exerciseVirtualTopic("A.Default");
        interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("expected interceptor", 2, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
        VirtualDestinationInterceptor newValue = ((VirtualDestinationInterceptor) (interceptors[0]));
        // apply again - ensure no change
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        Assert.assertSame("same instance", newValue, brokerService.getDestinationInterceptors()[0]);
    }

    @Test
    public void testRemove() throws Exception {
        final BrokerService brokerService = new BrokerService();
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        DestinationInterceptor[] interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("one interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
        exerciseVirtualTopic("A.Default");
        VirtualDestinationInterceptor defaultValue = ((VirtualDestinationInterceptor) (interceptors[0]));
        Assert.assertEquals("configured names in place", "A.>", defaultValue.getVirtualDestinations()[0].getVirtualDestination().getPhysicalName());
        exerciseVirtualTopic("A.Default");
        // apply empty config - this removes all virtual destinations from the interceptor
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{  });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        // update will happen on addDestination
        forceAddDestination("AnyDest");
        Assert.assertTrue("getVirtualDestinations empty on time", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() {
                return 0 == (getVirtualDestinations().length);
            }
        }));
        // reverse the remove, add again
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        // update will happen on addDestination
        exerciseVirtualTopic("A.NewOne");
        interceptors = brokerService.getDestinationInterceptors();
        Assert.assertEquals("expected interceptor", 1, interceptors.length);
        Assert.assertTrue("it is virtual topic interceptor", ((interceptors[0]) instanceof VirtualDestinationInterceptor));
    }

    @Test
    public void testMod() throws Exception {
        final BrokerService brokerService = new BrokerService();
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("one interceptor", 1, brokerService.getDestinationInterceptors().length);
        exerciseVirtualTopic("A.Default");
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("B.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseVirtualTopic("B.Default");
        Assert.assertEquals("still one interceptor", 1, brokerService.getDestinationInterceptors().length);
    }

    @Test
    public void testModApplyImmediately() throws Exception {
        final BrokerService brokerService = new BrokerService();
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("one interceptor", 1, brokerService.getDestinationInterceptors().length);
        exerciseVirtualTopic("A.Default");
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("B.>", false) }, true);
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseVirtualTopic("B.Default");
        Assert.assertEquals("still one interceptor", 1, brokerService.getDestinationInterceptors().length);
    }

    @Test
    public void testModWithMirroredQueue() throws Exception {
        final BrokerService brokerService = new BrokerService();
        brokerService.setUseMirroredQueues(true);
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("A.>", false) }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        Assert.assertEquals("one interceptor", 1, brokerService.getDestinationInterceptors().length);
        exerciseVirtualTopic("A.Default");
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ JavaVirtualDestTest.buildVirtualTopic("B.>", false) });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseVirtualTopic("B.Default");
        Assert.assertEquals("still one interceptor", 1, brokerService.getDestinationInterceptors().length);
    }

    @Test
    public void testNewFilteredComposite() throws Exception {
        final BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        FilteredDestination filteredDestination = new FilteredDestination();
        filteredDestination.setSelector("odd = 'yes'");
        filteredDestination.setQueue("VirtualDestination.QueueConsumer");
        CompositeQueue queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.FilteredCompositeQueue", Arrays.asList(filteredDestination));
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ queue });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseFilteredCompositeQueue("VirtualDestination.FilteredCompositeQueue", "VirtualDestination.QueueConsumer", "yes");
    }

    @Test
    public void testModFilteredComposite() throws Exception {
        final BrokerService brokerService = new BrokerService();
        FilteredDestination filteredDestination = new FilteredDestination();
        filteredDestination.setSelector("odd = 'yes'");
        filteredDestination.setQueue("VirtualDestination.QueueConsumer");
        CompositeQueue queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.FilteredCompositeQueue", Arrays.asList(filteredDestination));
        brokerService.setDestinationInterceptors(new DestinationInterceptor[]{ JavaVirtualDestTest.buildInterceptor(new VirtualDestination[]{ queue }) });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        exerciseFilteredCompositeQueue("VirtualDestination.FilteredCompositeQueue", "VirtualDestination.QueueConsumer", "yes");
        filteredDestination = new FilteredDestination();
        filteredDestination.setSelector("odd = 'no'");
        filteredDestination.setQueue("VirtualDestination.QueueConsumer");
        queue = JavaVirtualDestTest.buildCompositeQueue("VirtualDestination.FilteredCompositeQueue", Arrays.asList(filteredDestination));
        // apply new config
        javaConfigBroker.setVirtualDestinations(new VirtualDestination[]{ queue });
        TimeUnit.SECONDS.sleep(JavaVirtualDestTest.SLEEP);
        exerciseFilteredCompositeQueue("VirtualDestination.FilteredCompositeQueue", "VirtualDestination.QueueConsumer", "no");
        exerciseFilteredCompositeQueue("VirtualDestination.FilteredCompositeQueue", "VirtualDestination.QueueConsumer", "no");
    }
}

