/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.ws.schemalocations;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that schema locations are rewritten.
 * <p>
 * CXF-6469
 *
 * @author Tomas Hofman (thofman@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SchemaLocationsRewriteTestCase {
    @ArquillianResource
    URL baseUrl;

    private static final Logger log = Logger.getLogger(SchemaLocationsRewriteTestCase.class.getName());

    @Test
    public void testSchemaLocationsRewritten() throws Exception {
        // first path: SimpleService.wsdl -> imported/AnotherService.wsdl -> SimpleService.xsd -> importschema.xsd
        String importedWsdlLocation = getWsdlLocation(new URL(baseUrl, "SimpleService?wsdl"), "AnotherService.wsdl");
        verifyLocationRewritten(importedWsdlLocation);
        String xsdLocation = getSchemaLocation(new URL(importedWsdlLocation), "SimpleService.xsd");
        verifyLocationRewritten(xsdLocation);
        String importedXsdLocation = getSchemaLocation(new URL(xsdLocation), "importedschema.xsd");
        verifyLocationRewritten(importedXsdLocation);
        // second path: SimpleService.wsdl -> imported/SimpleService.xsd -> importedschema.xsd
        xsdLocation = getSchemaLocation(new URL(baseUrl, "SimpleService?wsdl"), "SimpleService.xsd");
        verifyLocationRewritten(xsdLocation);
        importedXsdLocation = getSchemaLocation(new URL(xsdLocation), "importedschema.xsd");
        verifyLocationRewritten(importedXsdLocation);
    }
}

