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
package org.jboss.as.test.integration.ws.serviceref;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Serviceref through ejb3 deployment descriptor.
 *
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ServiceRefTestCase {
    private static StatelessRemote remote1;

    private static StatelessRemote remote2;

    @Test
    public void testEJBRelay1() throws Exception {
        // test StatelessBean
        final String result1 = ServiceRefTestCase.remote1.echo1("Relay1");
        Assert.assertEquals("First EJB:Relay1", result1);
        // test StatelessBean2
        final String result2 = ServiceRefTestCase.remote2.echo1("Relay1");
        Assert.assertEquals("Second EJB:Relay1", result2);
    }

    @Test
    public void testEJBRelay2() throws Exception {
        // test StatelessBean
        final String result1 = ServiceRefTestCase.remote1.echo2("Relay2");
        Assert.assertEquals("First EJB:Relay2", result1);
        // test StatelessBean2
        final String result2 = ServiceRefTestCase.remote2.echo2("Relay2");
        Assert.assertEquals("Second EJB:Relay2", result2);
    }

    @Test
    public void testEJBRelay3() throws Exception {
        // test StatelessBean
        final String result1 = ServiceRefTestCase.remote1.echo3("Relay3");
        Assert.assertEquals("First EJB:Relay3", result1);
        // test StatelessBean2
        final String result2 = ServiceRefTestCase.remote2.echo3("Relay3");
        Assert.assertEquals("Second EJB:Relay3", result2);
    }

    @Test
    public void testEJBRelay4() throws Exception {
        // test StatelessBean
        final String result1 = ServiceRefTestCase.remote1.echo4("Relay4");
        Assert.assertEquals("First EJB:Relay4", result1);
        // test StatelessBean2
        final String result2 = ServiceRefTestCase.remote2.echo4("Relay4");
        Assert.assertEquals("Second EJB:Relay4", result2);
    }

    @Test
    public void testCdiBeanRelay() throws Exception {
        // test StatelessBean
        final String result1 = ServiceRefTestCase.remote1.echoCDI("RelayCDI");
        Assert.assertEquals("First EJB:RelayCDI", result1);
        // test StatelessBean2
        final String result2 = ServiceRefTestCase.remote2.echoCDI("RelayCDI");
        Assert.assertEquals("Second EJB:RelayCDI", result2);
    }
}
