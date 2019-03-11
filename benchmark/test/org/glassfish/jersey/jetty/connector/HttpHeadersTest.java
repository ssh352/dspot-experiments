/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.jetty.connector;


import HttpHeaders.USER_AGENT;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the headers.
 *
 * @author Stepan Kopriva
 */
public class HttpHeadersTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(HttpHeadersTest.class.getName());

    @Path("/test")
    public static class HttpMethodResource {
        @POST
        public String post(@HeaderParam("Transfer-Encoding")
        String transferEncoding, @HeaderParam("X-CLIENT")
        String xClient, @HeaderParam("X-WRITER")
        String xWriter, String entity) {
            Assert.assertEquals("client", xClient);
            return "POST";
        }

        @GET
        public String testUserAgent(@Context
        HttpHeaders httpHeaders) {
            final List<String> requestHeader = httpHeaders.getRequestHeader(USER_AGENT);
            if ((requestHeader.size()) != 1) {
                return "FAIL";
            }
            return requestHeader.get(0);
        }
    }

    @Test
    public void testPost() {
        Response response = target().path("test").request().header("X-CLIENT", "client").post(null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(response.hasEntity());
    }

    /**
     * Test, that {@code User-agent} header is as set by Jersey, not by underlying Jetty client.
     */
    @Test
    public void testUserAgent() {
        String response = target().path("test").request().get(String.class);
        Assert.assertTrue(("User-agent header should start with 'Jersey', but was " + response), response.startsWith("Jersey"));
    }
}

