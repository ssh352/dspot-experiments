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
package org.jboss.as.test.integration.ejb.mdb.vaultedproperties;


import java.io.IOException;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.integration.security.common.VaultHandler;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verify that MDB activation config properties can be vaulted.
 *
 * The test case will send a message to the destination and expects a reply.
 * The reply will be received only if the MDB was able to lookup the destination from its vaulted property in destinationLookup.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2014 Red Hat inc.
 */
@RunWith(Arquillian.class)
@ServerSetup({ MDBWithVaultedPropertiesTestCase.StoreVaultedPropertyTask.class })
public class MDBWithVaultedPropertiesTestCase {
    private static final String QUEUE_NAME = "vaultedproperties_queue";

    static final String CLEAR_TEXT_DESTINATION_LOOKUP = "java:jboss/messaging/vaultedproperties/queue";

    static final String VAULT_LOCATION = (MDBWithVaultedPropertiesTestCase.class.getResource("/").getPath()) + "security/jms-vault/";

    static class StoreVaultedPropertyTask implements ServerSetupTask {
        private VaultHandler vaultHandler;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            VaultHandler.cleanFilesystem(MDBWithVaultedPropertiesTestCase.VAULT_LOCATION, true);
            // create new vault
            vaultHandler = new VaultHandler(MDBWithVaultedPropertiesTestCase.VAULT_LOCATION);
            // store the destination lookup into the vault
            String vaultedProperty = vaultHandler.addSecuredAttribute("messaging", "destination", MDBWithVaultedPropertiesTestCase.CLEAR_TEXT_DESTINATION_LOOKUP.toCharArray());
            addVaultConfiguration(managementClient);
            createJMSQueue(managementClient, MDBWithVaultedPropertiesTestCase.QUEUE_NAME, MDBWithVaultedPropertiesTestCase.CLEAR_TEXT_DESTINATION_LOOKUP);
            updateAnnotationPropertyReplacement(managementClient, true);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            removeVaultConfiguration(managementClient);
            // remove temporary files
            vaultHandler.cleanUp();
            removeJMSQueue(managementClient, MDBWithVaultedPropertiesTestCase.QUEUE_NAME);
            updateAnnotationPropertyReplacement(managementClient, false);
        }

        private void addVaultConfiguration(ManagementClient managementClient) throws IOException {
            ModelNode op;
            op = new ModelNode();
            op.get(OP_ADDR).add(CORE_SERVICE, VAULT);
            op.get(OP).set(ADD);
            ModelNode vaultOption = op.get(VAULT_OPTIONS);
            vaultOption.get("KEYSTORE_URL").set(vaultHandler.getKeyStore());
            vaultOption.get("KEYSTORE_PASSWORD").set(vaultHandler.getMaskedKeyStorePassword());
            vaultOption.get("KEYSTORE_ALIAS").set(vaultHandler.getAlias());
            vaultOption.get("SALT").set(vaultHandler.getSalt());
            vaultOption.get("ITERATION_COUNT").set(vaultHandler.getIterationCountAsString());
            vaultOption.get("ENC_FILE_DIR").set(vaultHandler.getEncodedVaultFileDirectory());
            managementClient.getControllerClient().execute(build());
        }

        private void removeVaultConfiguration(ManagementClient managementClient) throws IOException {
            ModelNode op = new ModelNode();
            op.get(OP_ADDR).add(CORE_SERVICE, VAULT);
            op.get(OP).set(REMOVE);
            managementClient.getControllerClient().execute(build());
        }

        void createJMSQueue(ManagementClient managementClient, String name, String lookup) {
            JMSOperations jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.createJmsQueue(name, lookup);
            jmsAdminOperations.close();
        }

        void removeJMSQueue(ManagementClient managementClient, String name) {
            JMSOperations jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.removeJmsQueue(name);
            jmsAdminOperations.close();
        }

        private void updateAnnotationPropertyReplacement(ManagementClient managementClient, boolean value) throws IOException {
            ModelNode op;
            op = new ModelNode();
            op.get(OP_ADDR).add("subsystem", "ee");
            op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            op.get(NAME).set("annotation-property-replacement");
            op.get(VALUE).set(value);
            managementClient.getControllerClient().execute(build());
        }
    }

    @Resource(mappedName = MDBWithVaultedPropertiesTestCase.CLEAR_TEXT_DESTINATION_LOOKUP)
    private Queue queue;

    @Resource(mappedName = "/JmsXA")
    private ConnectionFactory factory;

    @Test
    public void sendAndReceiveMessage() {
        try (JMSContext context = factory.createContext()) {
            TemporaryQueue replyTo = context.createTemporaryQueue();
            String text = UUID.randomUUID().toString();
            context.createProducer().setJMSReplyTo(replyTo).send(queue, text);
            JMSConsumer consumer = context.createConsumer(replyTo);
            String reply = consumer.receiveBody(String.class, 5000);
            Assert.assertEquals(text, reply);
        }
    }
}

