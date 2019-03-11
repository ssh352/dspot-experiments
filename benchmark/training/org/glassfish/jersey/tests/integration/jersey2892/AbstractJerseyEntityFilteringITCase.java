/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.integration.jersey2892;


import MediaType.APPLICATION_JSON_TYPE;
import TestResource.Persons;
import TestResource.Pointer;
import TestResource.Recursive;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests whether classes repeating in the object graph are filtered out correctly.
 *
 * @author Stepan Vavra (stepan.vavra at oracle.com)
 */
public abstract class AbstractJerseyEntityFilteringITCase extends JerseyTest {
    /**
     * Tests whether sub-sub-field, {@link TestResource.Street} in particular,
     * is not filtered out.
     * <p/>
     * This corresponds with the JERSEY-2892 reported case.
     */
    @Test
    public void testWhetherSubSubFiledIsNotFilteredOut() {
        Response response = target(((provider()) + "/test")).request(APPLICATION_JSON_TYPE).get();
        final TestResource.Persons persons = response.readEntity(Persons.class);
        Assert.assertEquals("Amphitheatre Pkwy", persons.first.address.street.name);
        Assert.assertEquals("Microsoft Way", persons.second.address.street.name);
    }

    /**
     * Tests whether a de-referenced case of the reported problem is still correctly not filtered out. In particular, a
     * sub-sub-sub-field of the same class is not filtered out.
     */
    @Test
    public void testWhetherSubSubSubFieldIsNotFilteredOut() {
        Response response = target(((provider()) + "/pointer")).request(APPLICATION_JSON_TYPE).get();
        final TestResource.Pointer pointer = response.readEntity(Pointer.class);
        Assert.assertEquals("Amphitheatre Pkwy", pointer.persons.first.address.street.name);
        Assert.assertEquals("Microsoft Way", pointer.persons.second.address.street.name);
    }

    /**
     * Tests whether a reference cycle is detected and infinite recursion is prevented.
     */
    @Test
    public void testWhetherReferenceCycleIsDetected() {
        Response response = target(((provider()) + "/recursive")).request(APPLICATION_JSON_TYPE).get();
        final TestResource.Recursive recursive = response.readEntity(Recursive.class);
        Assert.assertEquals("c", recursive.subField.subSubField.idSubSubField);
    }
}

