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
package org.apache.flume.node;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import junit.framework.Assert;
import org.apache.flume.conf.FlumeConfiguration;
import org.apache.flume.conf.FlumeConfiguration.AgentConfiguration;
import org.junit.Test;


public class TestPollingZooKeeperConfigurationProvider extends TestAbstractZooKeeperConfigurationProvider {
    private EventBus eb;

    private TestPollingZooKeeperConfigurationProvider.EventSync es;

    private PollingZooKeeperConfigurationProvider cp;

    private class EventSync {
        private boolean notified;

        @Subscribe
        public synchronized void notifyEvent(MaterializedConfiguration mConfig) {
            notified = true;
            notifyAll();
        }

        public synchronized void awaitEvent() throws InterruptedException {
            while (!(notified)) {
                wait();
            } 
        }

        public synchronized void reset() {
            notified = false;
        }
    }

    @Test
    public void testPolling() throws Exception {
        es.awaitEvent();
        es.reset();
        FlumeConfiguration fc = cp.getFlumeConfiguration();
        Assert.assertTrue(fc.getConfigurationErrors().isEmpty());
        AgentConfiguration ac = fc.getConfigurationFor(TestAbstractZooKeeperConfigurationProvider.AGENT_NAME);
        Assert.assertNull(ac);
        addData();
        es.awaitEvent();
        es.reset();
        verifyProperties(cp);
    }
}

