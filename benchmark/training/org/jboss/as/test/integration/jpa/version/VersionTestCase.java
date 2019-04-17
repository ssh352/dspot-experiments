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
package org.jboss.as.test.integration.jpa.version;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Ensure that version handling works.
 * This tests that JPA 2.0 specification section 7.6.1 is followed.  Specifically:
 * "
 * If the entity manager is invoked outside the scope of a transaction, any entities loaded from the database
 * will immediately become detached at the end of the method call.
 * "
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class VersionTestCase {
    private static final String ARCHIVE_NAME = "jpa_sessionfactory";

    @ArquillianResource
    private static InitialContext iniCtx;

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVersion() throws Exception {
        SFSB1 sfsb1 = lookup("SFSB1", SFSB1.class);
        // tx1 will create the employee
        sfsb1.createEmployee("Sally", "1 home street", 1);
        // non-tx2 will load the entity
        Employee emp = sfsb1.getEmployeeNoTX(1);
        Integer firstVersion = emp.getVersion();
        // non-tx3 will load the entity
        // tx4 will update the employee
        // no-tx4 will load the entity (shouldn't see the stale entity read in non-tx3)
        Employee updatedEmp = sfsb1.mutateEmployee(emp);
        Assert.assertTrue(((((("entities read in non-tx should be detached from persistence context as they are read." + "  version at time of creation = ") + firstVersion) + ", version after update should be greater than creation version") + ", version after update is = ") + (updatedEmp.getVersion())), ((firstVersion.intValue()) < (updatedEmp.getVersion().intValue())));
    }
}
