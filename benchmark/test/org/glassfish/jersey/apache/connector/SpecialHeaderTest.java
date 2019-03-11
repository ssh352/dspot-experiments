/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.apache.connector;


import HttpHeaders.CONTENT_ENCODING;
import HttpHeaders.CONTENT_LENGTH;
import HttpHeaders.CONTENT_TYPE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Miroslav Fuksa
 */
public class SpecialHeaderTest extends JerseyTest {
    @Path("resource")
    public static class MyResource {
        @GET
        @Produces("text/plain")
        @Path("encoded")
        public Response getEncoded() {
            return Response.ok("get").header(CONTENT_ENCODING, "gzip").build();
        }

        @GET
        @Produces("text/plain")
        @Path("non-encoded")
        public Response getNormal() {
            return Response.ok("get").build();
        }
    }

    @Test
    public void testNonEncoded() {
        final Response response = target().path("resource/non-encoded").request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get", response.readEntity(String.class));
        Assert.assertNull(response.getHeaderString(CONTENT_ENCODING));
        Assert.assertEquals("text/plain", response.getHeaderString(CONTENT_TYPE));
        Assert.assertEquals("3", response.getHeaderString(CONTENT_LENGTH));
    }
}

