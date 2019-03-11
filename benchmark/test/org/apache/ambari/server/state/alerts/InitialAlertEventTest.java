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
package org.apache.ambari.server.state.alerts;


import AlertFirmness.HARD;
import AlertState.CRITICAL;
import AlertState.WARNING;
import category.AlertTest;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.events.AlertReceivedEvent;
import org.apache.ambari.server.events.InitialAlertEvent;
import org.apache.ambari.server.events.MockEventListener;
import org.apache.ambari.server.events.publishers.AlertEventPublisher;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.server.orm.entities.AlertCurrentEntity;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Alert;
import org.apache.ambari.server.state.AlertState;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.utils.EventBusSynchronizer;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests that {@link InitialAlertEventTest} instances are fired correctly.
 */
@Category({ AlertTest.class })
public class InitialAlertEventTest {
    private AlertsDAO m_alertsDao;

    private AlertEventPublisher m_eventPublisher;

    private Injector m_injector;

    private MockEventListener m_listener;

    private AlertDefinitionDAO m_definitionDao;

    private Clusters m_clusters;

    private Cluster m_cluster;

    private String m_clusterName;

    private ServiceFactory m_serviceFactory;

    private OrmTestHelper m_helper;

    private final String STACK_VERSION = "2.0.6";

    private final String REPO_VERSION = "2.0.6-1234";

    private final StackId STACK_ID = new StackId("HDP", STACK_VERSION);

    private RepositoryVersionEntity m_repositoryVersion;

    /**
     * Tests that when a new alert is received that an {@link InitialAlertEvent}
     * is fired.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInitialAlertEvent() throws Exception {
        // ensure there are no historical items
        Assert.assertEquals(0, m_alertsDao.findAll().size());
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(InitialAlertEvent.class));
        // get a definition to use for the incoming alert
        AlertDefinitionEntity definition = m_definitionDao.findAll(m_cluster.getClusterId()).get(0);
        // create the "first" alert
        Alert alert = new Alert(definition.getDefinitionName(), null, definition.getServiceName(), definition.getComponentName(), null, AlertState.CRITICAL);
        alert.setClusterId(m_cluster.getClusterId());
        AlertReceivedEvent event = new AlertReceivedEvent(m_cluster.getClusterId(), alert);
        // public the received event
        m_eventPublisher.publish(event);
        // ensure we now have a history item and a current
        Assert.assertEquals(1, m_alertsDao.findAll().size());
        List<AlertCurrentEntity> currentAlerts = m_alertsDao.findCurrent();
        Assert.assertEquals(1, currentAlerts.size());
        // verify that that initial alert is HARD
        Assert.assertEquals(HARD, currentAlerts.get(0).getFirmness());
        Assert.assertEquals(CRITICAL, currentAlerts.get(0).getAlertHistory().getAlertState());
        // verify that the initial alert event was triggered
        Assert.assertEquals(1, m_listener.getAlertEventReceivedCount(InitialAlertEvent.class));
        // clear the initial alert event that was recorded
        m_listener.reset();
        // alert the alert and re-fire
        alert.setState(WARNING);
        m_eventPublisher.publish(event);
        // ensure that the initial alert event was NOT received again
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(InitialAlertEvent.class));
    }

    /**
     *
     */
    private static class MockModule implements Module {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(Binder binder) {
            // sychronize on the ambari event bus for this test to work properly
            EventBusSynchronizer.synchronizeAmbariEventPublisher(binder);
        }
    }
}

