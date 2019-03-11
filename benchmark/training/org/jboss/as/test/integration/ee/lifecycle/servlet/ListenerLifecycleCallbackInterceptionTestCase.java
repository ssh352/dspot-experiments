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
package org.jboss.as.test.integration.ee.lifecycle.servlet;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Matus Abaffy
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ListenerLifecycleCallbackInterceptionTestCase extends LifecycleInterceptionTestCase {
    /**
     * This is not a real test method.
     */
    @Test
    @InSequence(1)
    public void deployRemoteArchive() {
        // In order to use @ArquillianResource URL from the unmanaged deployment we need to deploy the test archive first
        deployer.deploy(LifecycleInterceptionTestCase.REMOTE);
    }
}

