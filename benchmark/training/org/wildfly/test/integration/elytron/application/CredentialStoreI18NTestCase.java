/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.wildfly.test.integration.elytron.application;


import ElytronSubsystemMessages.ROOT_LOGGER;
import ModelDescriptionConstants.FAILURE_DESCRIPTION;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.CredentialReference;
import org.wildfly.test.security.common.elytron.Path;
import org.wildfly.test.security.common.elytron.SimpleCredentialStore;


/**
 * Tests credential store (CS) implementation in Elytron. This testcase uses several scenarios:
 * <ul>
 * <li>CS created on the top of existing keystore</li>
 * <li>CS with keystore created from scratch</li>
 * <li>keystore password as credential-reference (entry in another CS)</li>
 * <li>keystore file survives removing CS from domain model</li>
 * <li>several keystore types used for backing the credential store</li>
 * </ul>
 * The configuration for this test case is partly in module configuration (check keytool maven plugin used in {@code pom.xml}
 * and also credential store CLI commands in {@code modify-elytron.config.cli} file
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(CredentialStoreI18NTestCase.ElytronSetup.class)
public class CredentialStoreI18NTestCase extends AbstractCredentialStoreTestCase {
    private static final String NAME = CredentialStoreI18NTestCase.class.getSimpleName();

    private static final String LOWER = "lower";

    private static final String STR_SYMBOLS = "@!#?$%^&*()%+-{}";

    private static final String STR_CHINESE = "???";

    private static final String STR_ARABIC = "???????????";

    private static final String STR_EURO_LOWER = "???????????????????";

    private static final String STR_EURO_UPPER = "???????????????????";

    private static final String STR_ALL = ((((CredentialStoreI18NTestCase.STR_SYMBOLS) + (CredentialStoreI18NTestCase.STR_CHINESE)) + (CredentialStoreI18NTestCase.STR_ARABIC)) + (CredentialStoreI18NTestCase.STR_EURO_LOWER)) + (CredentialStoreI18NTestCase.STR_EURO_UPPER);

    /**
     * Tests using localized strings as secrets.
     */
    @Test
    public void testI18NSecret() throws Exception {
        assertAliasAndSecretSupported(CredentialStoreI18NTestCase.NAME, "i18nsecret", CredentialStoreI18NTestCase.STR_ALL);
    }

    /**
     * Tests using localized strings as alias.
     */
    @Test
    public void testI18NAlias() throws Exception {
        assertAliasAndSecretSupported(CredentialStoreI18NTestCase.NAME, CredentialStoreI18NTestCase.STR_CHINESE, "test");
        assertAliasAndSecretSupported(CredentialStoreI18NTestCase.NAME, CredentialStoreI18NTestCase.STR_ARABIC, "test");
        assertAliasAndSecretSupported(CredentialStoreI18NTestCase.NAME, CredentialStoreI18NTestCase.STR_EURO_LOWER, "test");
    }

    /**
     * Tests for CS aliases case-sensitiveness
     */
    @Test
    public void testAliasesCaseSensitive() throws Exception {
        assertCredentialValue(CredentialStoreI18NTestCase.NAME, CredentialStoreI18NTestCase.LOWER, CredentialStoreI18NTestCase.LOWER);
        try (CLIWrapper cli = new CLIWrapper(true)) {
            Assert.assertFalse(cli.sendLine("/subsystem=elytron/credential-store=CredentialStoreI18NTestCase:add-alias(alias=LOWER, secret-value=password)", true));
            ModelNode result = ModelNode.fromString(cli.readOutput());
            Assert.assertEquals(("result " + result), result.get(FAILURE_DESCRIPTION).asString(), ROOT_LOGGER.credentialAlreadyExists("LOWER", PasswordCredential.class.getName()).getMessage());
        }
    }

    /**
     * Configures 2 unmodifiable credential stores (CS) on the top of one existing JCEKS keystore - One CS uses plain text
     * keystore password, the second uses credential reference (pointing to the first CS). Then configures one modifiable CS.
     */
    static class ElytronSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            final Path jceksPath = Path.builder().withPath("cred-store.jceks").withRelativeTo("jboss.server.config.dir").build();
            final CredentialReference credRefPwd = CredentialReference.builder().withClearText("password").build();
            return new ConfigurableElement[]{ SimpleCredentialStore.builder().withName(CredentialStoreI18NTestCase.NAME).withKeyStorePath(jceksPath).withKeyStoreType("JCEKS").withCreate(false).withModifiable(true).withCredential(credRefPwd).withAlias(CredentialStoreI18NTestCase.LOWER, CredentialStoreI18NTestCase.LOWER).build() };
        }
    }
}

