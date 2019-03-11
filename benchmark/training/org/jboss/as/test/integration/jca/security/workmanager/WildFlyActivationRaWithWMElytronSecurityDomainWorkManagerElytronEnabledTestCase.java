/**
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.jca.security.workmanager;


import ModelDescriptionConstants.SUBSYSTEM;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkContext;
import javax.resource.spi.work.WorkContextProvider;
import javax.resource.spi.work.WorkManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.jca.rar.MultipleAdminObject1;
import org.jboss.as.test.integration.jca.security.TestBean;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.core.security.SimplePrincipal;
import org.jboss.jca.core.spi.security.SecurityContext;
import org.jboss.jca.core.spi.security.SecurityIntegration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.PropertyFileBasedDomain;

import static javax.resource.spi.work.SecurityContext.<init>;


/**
 * Test security inflow with JCA work manager using Elytron security domain
 */
@RunWith(Arquillian.class)
@ServerSetup({ WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.ElytronSetup.class, WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.Ejb3Setup.class, WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.JcaSetup.class, WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.RaSetup.class })
public class WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase {
    private static final String ADMIN_OBJ_JNDI_NAME = "java:jboss/admObj";

    private static final String WM_ELYTRON_SECURITY_DOMAIN_NAME = "RaRealmElytron";

    private static final String WM_EJB3_SECURITY_DOMAIN_NAME = "RaRealm";

    private static final String BOOTSTRAP_CTX_NAME = "customContext";

    static class ElytronSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            final PropertyFileBasedDomain domain = PropertyFileBasedDomain.builder().withName(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.WM_ELYTRON_SECURITY_DOMAIN_NAME).withUser("rauser", "rauserpassword").build();
            return new ConfigurableElement[]{ domain };
        }
    }

    static class Ejb3Setup implements ServerSetupTask {
        private static final PathAddress EJB3_SUBSYSTEM_ADDRESS = PathAddress.pathAddress(SUBSYSTEM, "ejb3");

        private static final PathAddress EJB3_APP_SEC_DOMAIN_ADDRESS = WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.Ejb3Setup.EJB3_SUBSYSTEM_ADDRESS.append("application-security-domain", WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.WM_EJB3_SECURITY_DOMAIN_NAME);

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            ModelControllerClient mcc = managementClient.getControllerClient();
            addApplicationSecurityDomain(mcc);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            ModelControllerClient mcc = managementClient.getControllerClient();
            removeApplicationSecurityDomainSilently(mcc);
        }

        private void addApplicationSecurityDomain(ModelControllerClient client) throws Exception {
            ModelNode addAppSecDomainOperation = Operations.createAddOperation(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.Ejb3Setup.EJB3_APP_SEC_DOMAIN_ADDRESS.toModelNode());
            addAppSecDomainOperation.get("security-domain").set(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.WM_ELYTRON_SECURITY_DOMAIN_NAME);
            ModelNode response = execute(addAppSecDomainOperation, client);
            Assert.assertEquals(response.toString(), SUCCESS, response.get(OUTCOME).asString());
        }

        private void removeApplicationSecurityDomainSilently(ModelControllerClient client) throws IOException {
            ModelNode removeBootstrapCtxOperation = Operations.createRemoveOperation(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.Ejb3Setup.EJB3_APP_SEC_DOMAIN_ADDRESS.toModelNode());
            client.execute(removeBootstrapCtxOperation);
        }

        private ModelNode execute(ModelNode operation, ModelControllerClient client) throws IOException {
            return client.execute(operation);
        }
    }

    static class JcaSetup extends AbstractJcaSetup {
        private static final String WM_NAME = "customWM";

        @Override
        protected String getWorkManagerName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.JcaSetup.WM_NAME;
        }

        @Override
        protected String getBootstrapContextName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.BOOTSTRAP_CTX_NAME;
        }

        @Override
        protected Boolean getElytronEnabled() {
            return true;
        }
    }

    static class RaSetup extends AbstractRaSetup {
        private static final String RA_NAME = "wf-ra-wm-security-domain";

        @Override
        protected String getResourceAdapterName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.RaSetup.RA_NAME;
        }

        @Override
        protected String getBootstrapContextName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.BOOTSTRAP_CTX_NAME;
        }

        @Override
        protected String getAdminObjectJNDIName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.ADMIN_OBJ_JNDI_NAME;
        }

        @Override
        protected Consumer<ModelNode> getAddRAOperationConsumer() {
            return ( addRaOperation) -> {
                addRaOperation.get("wm-security").set(true);
                addRaOperation.get("wm-elytron-security-domain").set(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.WM_ELYTRON_SECURITY_DOMAIN_NAME);
                addRaOperation.get("wm-security-default-principal").set("wm-default-principal");
                addRaOperation.get("wm-security-default-groups").set(new ModelNode().setEmptyList().add("wm-default-group"));
            };
        }
    }

    @Resource(mappedName = WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.ADMIN_OBJ_JNDI_NAME)
    private MultipleAdminObject1 adminObject;

    @EJB
    private TestBean bean;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @OperateOnDeployment("ejb")
    public void testValidRole() throws Exception {
        WorkManager wm = getWorkManager();
        WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.MyWork myWork = new WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.MyWork(wm, bean, "eis", "eis-role");
        wm.doWork(myWork);
        // This is different from legacy security.
        // Legacy security merges default default principals and groups
        // (wm-security-default-principal and wm-security-default-groups)
        // with those defined by SecurityContext.
        // Elytron ignores configured defaults when there are already specific principal and groups set
        verifyUsers(myWork, "eis");
        verifyRoles(myWork, "eis-role");
    }

    @Test
    @OperateOnDeployment("ejb")
    public void testInvalidRole() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(EJBAccessException.class));
        expectedException.expectMessage(CoreMatchers.containsString("WFLYEJB0364"));
        WorkManager wm = getWorkManager();
        WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.MyWork myWork = new WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.MyWork(wm, bean, "eis", "invalid-role");
        wm.doWork(myWork);
    }

    public static class MyWork implements Work , WorkContextProvider {
        private static final long serialVersionUID = 1L;

        private final WorkManager wm;

        private Set<Principal> principals;

        private String[] roles;

        private final TestBean bean;

        private final String username;

        private final String role;

        public MyWork(WorkManager wm, TestBean bean, String username, String role) {
            this.wm = wm;
            this.principals = null;
            this.roles = null;
            this.bean = bean;
            this.username = username;
            this.role = role;
        }

        public List<WorkContext> getWorkContexts() {
            List<WorkContext> l = new ArrayList<>(1);
            l.add(new WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronEnabledTestCase.MySecurityContext(username, role));
            return l;
        }

        public void run() {
            bean.test();
            SecurityIntegration securityIntegration = getSecurityIntegration();
            SecurityContext securityContext = securityIntegration.getSecurityContext();
            if (securityContext != null) {
                Subject subject = securityContext.getAuthenticatedSubject();
                if (subject != null) {
                    if (((subject.getPrincipals()) != null) && ((subject.getPrincipals().size()) > 0)) {
                        principals = subject.getPrincipals();
                    }
                    roles = securityContext.getRoles();
                }
            }
        }

        public void release() {
        }

        public Set<Principal> getPrincipals() {
            return principals;
        }

        public String[] getRoles() {
            return roles;
        }
    }

    public static class MySecurityContext extends javax.resource.spi.work.SecurityContext {
        private static final long serialVersionUID = 1L;

        private final String username;

        private final String role;

        public MySecurityContext(String username, String role) {
            super();
            this.username = username;
            this.role = role;
        }

        public void setupSecurityContext(CallbackHandler handler, Subject executionSubject, Subject serviceSubject) {
            try {
                List<Callback> cbs = new ArrayList<>();
                cbs.add(new javax.security.auth.message.callback.CallerPrincipalCallback(executionSubject, new SimplePrincipal(username)));
                cbs.add(new GroupPrincipalCallback(executionSubject, new String[]{ role }));
                handler.handle(cbs.toArray(new Callback[cbs.size()]));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }
}

