/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.messaging.jms.external;


import java.io.IOException;
import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that invoking a management operation that removes a JMS resource that is used by a deployed archive must fail:
 * the resource must not be removed and any depending services must be recovered.
 * The deployment must still be operating after the failing management operation.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2014 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(ExternalMessagingDeploymentTestCase.SetupTask.class)
public class ExternalMessagingDeploymentTestCase {
    public static final String QUEUE_LOOKUP = "java:/jms/DependentMessagingDeploymentTestCase/myQueue";

    public static final String TOPIC_LOOKUP = "java:/jms/DependentMessagingDeploymentTestCase/myTopic";

    public static final String REMOTE_PCF = "remote-artemis";

    private static final String QUEUE_NAME = "myQueue";

    private static final String TOPIC_NAME = "myTopic";

    @ArquillianResource
    private URL url;

    static class SetupTask extends SnapshotRestoreSetupTask {
        private static final Logger logger = Logger.getLogger(SendToExternalJMSQueueTestCase.SetupTask.class);

        @Override
        public void doSetup(ManagementClient managementClient, String s) throws Exception {
            JMSOperations ops = JMSOperationsProvider.getInstance(managementClient.getControllerClient());
            ops.createJmsQueue(ExternalMessagingDeploymentTestCase.QUEUE_NAME, ("/queue/" + (ExternalMessagingDeploymentTestCase.QUEUE_NAME)));
            ops.createJmsTopic(ExternalMessagingDeploymentTestCase.TOPIC_NAME, ("/topic/" + (ExternalMessagingDeploymentTestCase.TOPIC_NAME)));
            ops.addExternalHttpConnector("http-test-connector", "http", "http-acceptor");
            ModelNode op = Operations.createRemoveOperation(getInitialPooledConnectionFactoryAddress());
            execute(managementClient, op, true);
            op = Operations.createAddOperation(getPooledConnectionFactoryAddress());
            op.get("transaction").set("xa");
            op.get("entries").add("java:/JmsXA java:jboss/DefaultJMSConnectionFactory");
            op.get("connectors").add("http-test-connector");
            execute(managementClient, op, true);
            op = Operations.createAddOperation(getClientTopicAddress());
            op.get("entries").add(ExternalMessagingDeploymentTestCase.TOPIC_LOOKUP);
            op.get("entries").add("/topic/myAwesomeClientTopic");
            execute(managementClient, op, true);
            op = Operations.createAddOperation(getClientQueueAddress());
            op.get("entries").add(ExternalMessagingDeploymentTestCase.QUEUE_LOOKUP);
            op.get("entries").add("/queue/myAwesomeClientQueue");
            execute(managementClient, op, true);
            ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
        }

        private ModelNode execute(final ManagementClient managementClient, final ModelNode op, final boolean expectSuccess) throws IOException {
            ModelNode response = managementClient.getControllerClient().execute(op);
            final String outcome = response.get("outcome").asString();
            if (expectSuccess) {
                Assert.assertEquals(response.toString(), "success", outcome);
                return response.get("result");
            } else {
                Assert.assertEquals("failed", outcome);
                return response.get("failure-description");
            }
        }

        ModelNode getPooledConnectionFactoryAddress() {
            ModelNode address = new ModelNode();
            address.add("subsystem", "messaging-activemq");
            address.add("pooled-connection-factory", ExternalMessagingDeploymentTestCase.REMOTE_PCF);
            return address;
        }

        ModelNode getClientTopicAddress() {
            ModelNode address = new ModelNode();
            address.add("subsystem", "messaging-activemq");
            address.add("external-jms-topic", ExternalMessagingDeploymentTestCase.TOPIC_NAME);
            return address;
        }

        ModelNode getClientQueueAddress() {
            ModelNode address = new ModelNode();
            address.add("subsystem", "messaging-activemq");
            address.add("external-jms-queue", ExternalMessagingDeploymentTestCase.QUEUE_NAME);
            return address;
        }

        ModelNode getInitialPooledConnectionFactoryAddress() {
            ModelNode address = new ModelNode();
            address.add("subsystem", "messaging-activemq");
            address.add("server", "default");
            address.add("pooled-connection-factory", "activemq-ra");
            return address;
        }
    }

    @Test
    public void testSendMessageInClientQueue() throws Exception {
        sendAndReceiveMessage(true);
    }

    @Test
    public void testSendMessageInClientTopic() throws Exception {
        sendAndReceiveMessage(false);
    }
}
