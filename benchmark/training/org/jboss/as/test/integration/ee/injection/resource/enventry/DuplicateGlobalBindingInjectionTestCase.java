/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ee.injection.resource.enventry;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test that two deployments can declare the same global en-entry
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class DuplicateGlobalBindingInjectionTestCase {
    @Test
    @OperateOnDeployment("dep1")
    public void testGlobalBound1() throws Exception {
        final String globalValue = ((String) (new InitialContext().lookup("java:global/foo")));
        Assert.assertEquals("injection!", globalValue);
    }

    @Test
    @OperateOnDeployment("dep2")
    public void testGlobalBound2() throws Exception {
        final String globalValue = ((String) (new InitialContext().lookup("java:global/foo")));
        Assert.assertEquals("injection!", globalValue);
    }
}
