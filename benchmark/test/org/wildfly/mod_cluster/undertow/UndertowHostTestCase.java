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
package org.wildfly.mod_cluster.undertow;


import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.jboss.modcluster.container.Context;
import org.jboss.modcluster.container.Engine;
import org.jboss.modcluster.container.Host;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.extension.undertow.Server;
import org.wildfly.extension.undertow.UndertowService;


public class UndertowHostTestCase {
    private final String hostName = "host";

    private final String alias = "alias";

    private final Server server = new TestServer("serverName", "defaultHost");

    private final UndertowService service = new TestUndertowService("default-container", "default-server", "default-virtual-host", "instance-id", this.server);

    private final Host undertowHost = new TestHost(this.hostName, Collections.singletonList(this.alias), this.service, this.server);

    private final Engine engine = Mockito.mock(Engine.class);

    private final Host host = new UndertowHost(this.undertowHost, this.engine);

    @Test
    public void getName() {
        Assert.assertSame(this.hostName, this.host.getName());
    }

    @Test
    public void getAliases() {
        Set<String> result = this.host.getAliases();
        Assert.assertTrue(result.toString(), result.contains(this.hostName));
        Assert.assertTrue(result.toString(), result.contains(this.alias));
    }

    @Test
    public void getEngine() {
        Assert.assertSame(this.engine, this.host.getEngine());
    }

    @Test
    public void getContexts() {
        Deployment deployment = Mockito.mock(Deployment.class);
        DeploymentInfo info = new DeploymentInfo();
        String expectedPath = "";
        info.setContextPath(expectedPath);
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        Mockito.when(deployment.getDeploymentInfo()).thenReturn(info);
        this.undertowHost.registerDeployment(deployment, handler);
        Iterator<Context> result = this.host.getContexts().iterator();
        Assert.assertTrue(result.hasNext());
        Context context = result.next();
        Assert.assertSame(this.host, context.getHost());
        Assert.assertSame(expectedPath, context.getPath());
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void findContext() {
        Deployment deployment = Mockito.mock(Deployment.class);
        DeploymentInfo info = new DeploymentInfo();
        String expectedPath = "";
        info.setContextPath(expectedPath);
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        Mockito.when(deployment.getDeploymentInfo()).thenReturn(info);
        this.undertowHost.registerDeployment(deployment, handler);
        Context result = this.host.findContext(expectedPath);
        Assert.assertSame(this.host, result.getHost());
        Assert.assertSame(expectedPath, result.getPath());
        result = this.host.findContext("unknown");
        Assert.assertNull(result);
    }
}

