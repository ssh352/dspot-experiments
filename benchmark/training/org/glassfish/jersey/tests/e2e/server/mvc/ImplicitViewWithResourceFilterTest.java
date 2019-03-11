/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.server.mvc;


import Invocation.Builder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.annotation.Priority;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class ImplicitViewWithResourceFilterTest extends JerseyTest {
    @Path("/")
    @Template
    public static class ImplicitTemplate {
        public String toString() {
            return "ImplicitTemplate";
        }
    }

    @Priority(10)
    public static class FilterOne implements ContainerRequestFilter , ContainerResponseFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            List<String> xTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertNull(xTest);
            requestContext.getHeaders().add("X-TEST", "one");
        }

        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
            List<String> rxTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertEquals(2, rxTest.size());
            Assert.assertEquals("one", rxTest.get(0));
            Assert.assertEquals("two", rxTest.get(1));
            List<Object> xTest = responseContext.getHeaders().get("X-TEST");
            Assert.assertEquals(1, xTest.size());
            Assert.assertEquals("two", xTest.get(0));
            Assert.assertNull(responseContext.getHeaders().get("Y-TEST"));
            responseContext.getHeaders().add("Y-TEST", "one");
        }
    }

    @Priority(20)
    public static class FilterTwo implements ContainerRequestFilter , ContainerResponseFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            List<String> xTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertEquals(1, xTest.size());
            Assert.assertEquals("one", xTest.get(0));
            requestContext.getHeaders().add("X-TEST", "two");
        }

        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
            List<String> rxTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertEquals(2, rxTest.size());
            Assert.assertEquals("one", rxTest.get(0));
            Assert.assertEquals("two", rxTest.get(1));
            Assert.assertNull(responseContext.getHeaders().get("X-TEST"));
            Assert.assertNull(responseContext.getHeaders().get("Y-TEST"));
            responseContext.getHeaders().add("X-TEST", "two");
        }
    }

    @Test
    public void testImplicitTemplate() throws IOException {
        final Invocation.Builder request = target("/").request();
        Response cr = request.get(Response.class);
        Assert.assertEquals(200, cr.getStatus());
        List<Object> xTest = cr.getMetadata().get("X-TEST");
        Assert.assertEquals(1, xTest.size());
        Assert.assertEquals("two", xTest.get(0));
        List<Object> yTest = cr.getMetadata().get("Y-TEST");
        Assert.assertEquals(1, yTest.size());
        Assert.assertEquals("one", yTest.get(0));
        Properties p = new Properties();
        p.load(cr.readEntity(InputStream.class));
        Assert.assertEquals("/org/glassfish/jersey/tests/e2e/server/mvc/ImplicitViewWithResourceFilterTest/ImplicitTemplate/index.testp", p.getProperty("path"));
        Assert.assertEquals("ImplicitTemplate", p.getProperty("model"));
    }
}

