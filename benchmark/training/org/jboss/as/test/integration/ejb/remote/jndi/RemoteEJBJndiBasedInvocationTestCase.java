/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.remote.jndi;


import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jaikiran Pai
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RemoteEJBJndiBasedInvocationTestCase {
    private static final String APP_NAME = "";

    private static final String DISTINCT_NAME = "";

    private static final String MODULE_NAME = "remote-ejb-jndi-test-case";

    private static Context context;

    @Test
    public void testRemoteSLSBInvocation() throws Exception {
        final RemoteEcho remoteEcho = ((RemoteEcho) (RemoteEJBJndiBasedInvocationTestCase.context.lookup(((((((((("ejb:" + (RemoteEJBJndiBasedInvocationTestCase.APP_NAME)) + "/") + (RemoteEJBJndiBasedInvocationTestCase.MODULE_NAME)) + "/") + (RemoteEJBJndiBasedInvocationTestCase.DISTINCT_NAME)) + "/") + (EchoBean.class.getSimpleName())) + "!") + (RemoteEcho.class.getName())))));
        Assert.assertNotNull("Lookup returned a null bean proxy", remoteEcho);
        final String msg = "Hello world from a really remote client!!!";
        final String echo = remoteEcho.echo(msg);
        Assert.assertEquals("Unexpected echo returned from remote bean", msg, echo);
    }

    @Test
    public void testRemoteSFSBInvocation() throws Exception {
        final RemoteCounter remoteCounter = ((RemoteCounter) (RemoteEJBJndiBasedInvocationTestCase.context.lookup((((((((((("ejb:" + (RemoteEJBJndiBasedInvocationTestCase.APP_NAME)) + "/") + (RemoteEJBJndiBasedInvocationTestCase.MODULE_NAME)) + "/") + (RemoteEJBJndiBasedInvocationTestCase.DISTINCT_NAME)) + "/") + (CounterBean.class.getSimpleName())) + "!") + (RemoteCounter.class.getName())) + "?stateful"))));
        Assert.assertNotNull("Lookup returned a null bean proxy", remoteCounter);
        Assert.assertEquals("Unexpected initial count returned by bean", 0, remoteCounter.getCount());
        final int NUM_TIMES = 25;
        // test increment
        for (int i = 0; i < NUM_TIMES; i++) {
            remoteCounter.incrementCount();
            final int currentCount = remoteCounter.getCount();
            Assert.assertEquals("Unexpected count after increment", (i + 1), currentCount);
        }
        Assert.assertEquals("Unexpected total count after increment", NUM_TIMES, remoteCounter.getCount());
        // test decrement
        for (int i = NUM_TIMES; i > 0; i--) {
            remoteCounter.decrementCount();
            final int currentCount = remoteCounter.getCount();
            Assert.assertEquals("Unexpected count after decrement", (i - 1), currentCount);
        }
        Assert.assertEquals("Unexpected total count after decrement", 0, remoteCounter.getCount());
    }
}

