/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.microprofile.opentracing;


import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.ServerReload;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test verifying the assumption that different services provided by multiple WARs have different tracers.
 *
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a> (c) 2018 Red Hat, Inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiWarOpenTracingTestCase {
    @ContainerResource
    ManagementClient managementClient;

    @ArquillianResource
    @OperateOnDeployment("ServiceOne.war")
    private URL serviceOneUrl;

    @ArquillianResource
    @OperateOnDeployment("ServiceTwo.war")
    private URL serviceTwoUrl;

    @Test
    public void testMultipleWarServicesUseDifferentTracers() throws Exception {
        testHttpInvokation();
    }

    @Test
    public void testMultipleWarServicesUseDifferentTracersAfterReload() throws Exception {
        testHttpInvokation();
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
        testHttpInvokation();
    }
}

