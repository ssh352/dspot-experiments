/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.deployment.dependencies.ear;


import java.io.IOException;
import javax.ejb.NoSuchEJBException;
import javax.naming.NamingException;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ArchiveDeployer;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test for inter-deployment dependencies in EAR files. It also contains a module dependency simple test - EJB module depends on
 * WEB module in app1.ear.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InterDeploymentDependenciesEarTestCase {
    private static Logger LOGGER = Logger.getLogger(InterDeploymentDependenciesEarTestCase.class);

    private static final String DEP_APP1 = "app1";

    private static final String DEP_APP2 = "app2";

    private static final String MODULE_EJB = "hello";

    private static final String MODULE_WEB = "staller";

    @ArquillianResource
    public ManagementClient managementClient;

    // We don't inject this via @ArquillianResource because ARQ can't fully control
    // DEPA_APP1 and DEP_APP2 and things go haywire if we try. But we use ArchiveDeployer
    // because it's a convenient API for handling deploy/undeploy of Shrinkwrap archives
    private ArchiveDeployer deployer;

    private static EnterpriseArchive DEPENDEE = ShrinkWrap.create(EnterpriseArchive.class, ((InterDeploymentDependenciesEarTestCase.DEP_APP1) + ".ear")).addAsModule(InterDeploymentDependenciesEarTestCase.createWar()).addAsModule(InterDeploymentDependenciesEarTestCase.createBeanJar()).addAsLibrary(InterDeploymentDependenciesEarTestCase.createLogLibrary()).addAsManifestResource(InterDeploymentDependenciesEarTestCase.class.getPackage(), "application.xml", "application.xml");

    private static EnterpriseArchive DEPENDENT = ShrinkWrap.create(EnterpriseArchive.class, ((InterDeploymentDependenciesEarTestCase.DEP_APP2) + ".ear")).addAsLibrary(InterDeploymentDependenciesEarTestCase.createLogLibrary()).addAsModule(InterDeploymentDependenciesEarTestCase.createBeanJar()).addAsManifestResource(InterDeploymentDependenciesEarTestCase.class.getPackage(), "jboss-all.xml", "jboss-all.xml").addAsModule(InterDeploymentDependenciesEarTestCase.createBeanJar());

    /**
     * Tests enterprise application dependencies.
     */
    @Test
    public void test() throws IOException, NamingException, DeploymentException {
        try {
            deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDENT);
            Assert.fail("Application deployment must fail if the dependencies are not satisfied.");
        } catch (Exception e) {
            InterDeploymentDependenciesEarTestCase.LOGGER.debug("Expected fail", e);
        }
        deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDEE);
        deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDENT);
        final LogAccess helloApp1 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP1);
        final LogAccess helloApp2 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP2);
        Assert.assertEquals(((SleeperContextListener.class.getSimpleName()) + (LogAccessBean.class.getSimpleName())), helloApp1.getLog());
        Assert.assertEquals(LogAccessBean.class.getSimpleName(), helloApp2.getLog());
        forceDependeeUndeploy();
        try {
            helloApp2.getLog();
            Assert.fail("Calling EJB from dependent application should fail");
        } catch (IllegalStateException | NoSuchEJBException e) {
            // OK
        }
        // cleanUp will undeploy DEP_APP2
    }

    @Test
    public void testWithRestart() throws IOException, NamingException, DeploymentException, MgmtOperationException {
        try {
            deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDENT);
            Assert.fail("Application deployment must fail if the dependencies are not satisfied.");
        } catch (Exception e) {
            InterDeploymentDependenciesEarTestCase.LOGGER.debug("Expected fail", e);
        }
        deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDEE);
        deployer.deploy(InterDeploymentDependenciesEarTestCase.DEPENDENT);
        LogAccess helloApp1 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP1);
        LogAccess helloApp2 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP2);
        Assert.assertEquals(((SleeperContextListener.class.getSimpleName()) + (LogAccessBean.class.getSimpleName())), helloApp1.getLog());
        Assert.assertEquals(LogAccessBean.class.getSimpleName(), helloApp2.getLog());
        ModelNode redeploy = Util.createEmptyOperation("redeploy", PathAddress.pathAddress("deployment", InterDeploymentDependenciesEarTestCase.DEPENDEE.getName()));
        ManagementOperations.executeOperation(managementClient.getControllerClient(), redeploy);
        helloApp1 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP1);
        helloApp2 = lookupEJB(InterDeploymentDependenciesEarTestCase.DEP_APP2);
        Assert.assertEquals(((SleeperContextListener.class.getSimpleName()) + (LogAccessBean.class.getSimpleName())), helloApp1.getLog());
        Assert.assertEquals(LogAccessBean.class.getSimpleName(), helloApp2.getLog());
        forceDependeeUndeploy();
        try {
            helloApp2.getLog();
            Assert.fail("Calling EJB from dependent application should fail");
        } catch (IllegalStateException | NoSuchEJBException e) {
            // OK
        }
        // cleanUp will undeploy DEP_APP2
    }
}

