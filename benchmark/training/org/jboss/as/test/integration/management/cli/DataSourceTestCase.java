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
package org.jboss.as.test.integration.management.cli;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DataSourceTestCase extends AbstractCliTestBase {
    @ArquillianResource
    URL url;

    private static final String[][] DS_PROPS = new String[][]{ new String[]{ "idle-timeout-minutes", "5" } };

    @Test
    public void testDataSource() throws Exception {
        testAddDataSource();
        testModifyDataSource();
        testRemoveDataSource();
    }

    @Test
    public void testDataSourceConnectionProperties() throws Exception {
        testAddDataSourceConnectionProperties();
        testModifyDataSource();
        testRemoveDataSource();
    }

    @Test
    public void testXaDataSource() throws Exception {
        testAddXaDataSource();
        testModifyXaDataSource();
        testRemoveXaDataSource();
    }
}

