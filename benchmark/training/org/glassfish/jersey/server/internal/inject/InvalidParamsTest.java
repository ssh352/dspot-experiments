/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.internal.inject;


import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class InvalidParamsTest {
    public static class ParamEntity {
        public static InvalidParamsTest.ParamEntity fromString(String arg) {
            return new InvalidParamsTest.ParamEntity();
        }
    }

    @Path("invalid/path/param")
    public static class ResourceInvalidParams {
        @PathParam("arg1")
        String f1;

        @QueryParam("arg2")
        String f2;

        @CookieParam("arg3")
        String f3;

        @QueryParam("arg4")
        InvalidParamsTest.ParamEntity f4;

        @GET
        public String doGet(@PathParam("arg1")
        String s1, @QueryParam("arg2")
        String s2, @CookieParam("arg3")
        String s3, @QueryParam("arg4")
        InvalidParamsTest.ParamEntity s4) {
            Assert.assertEquals(s1, null);
            Assert.assertEquals(s2, null);
            Assert.assertEquals(s3, null);
            Assert.assertEquals(s4, null);
            Assert.assertEquals(f1, null);
            Assert.assertEquals(f2, null);
            Assert.assertEquals(f3, null);
            Assert.assertEquals(f4, null);
            return s1;
        }
    }

    @Test
    public void testInvalidPathParam() throws Exception {
        ContainerResponse responseContext = createApplication(InvalidParamsTest.ResourceInvalidParams.class).apply(RequestContextBuilder.from("/invalid/path/param", "GET").build()).get();
        // returned param is null -> 204 NO CONTENT
        Assert.assertEquals(204, responseContext.getStatus());
    }

    public static class FaultyParamEntityWAE {
        public static InvalidParamsTest.FaultyParamEntityWAE fromString(String arg) {
            throw new WebApplicationException(500);
        }
    }

    public static class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
        @Override
        public Response toResponse(WebApplicationException exception) {
            return Response.status(500).entity("caught").build();
        }
    }

    @Path("invalid/path/param")
    public static class ResourceWithFaultyParamEntityParamWAE {
        @GET
        public String doGet(@QueryParam("arg4")
        InvalidParamsTest.FaultyParamEntityWAE s4) {
            Assert.assertEquals(s4, null);
            return "foo";
        }
    }

    @Test
    public void testInvalidQueryParamParamWAE() throws Exception {
        ContainerResponse responseContext = createApplication(InvalidParamsTest.ResourceWithFaultyParamEntityParamWAE.class, InvalidParamsTest.WebApplicationExceptionMapper.class).apply(RequestContextBuilder.from("/invalid/path/param?arg4=test", "GET").build()).get();
        Assert.assertEquals(500, responseContext.getStatus());
        Assert.assertEquals("caught", responseContext.getEntity());
    }

    @Path("invalid/path/param")
    public static class ResourceWithFaultyParamEntityFieldWAE {
        @QueryParam("arg4")
        InvalidParamsTest.FaultyParamEntityWAE f4;

        @GET
        public String doGet() {
            Assert.assertEquals(f4, null);
            return "foo";
        }
    }

    @Test
    public void testInvalidQueryParamFieldWAE() throws Exception {
        ContainerResponse responseContext = createApplication(InvalidParamsTest.ResourceWithFaultyParamEntityFieldWAE.class, InvalidParamsTest.WebApplicationExceptionMapper.class).apply(RequestContextBuilder.from("/invalid/path/param?arg4=test", "GET").build()).get();
        Assert.assertEquals(500, responseContext.getStatus());
        Assert.assertEquals("caught", responseContext.getEntity());
    }

    public static class FaultyParamEntityISE {
        public static InvalidParamsTest.FaultyParamEntityISE fromString(String arg) {
            throw new IllegalStateException("error");
        }
    }

    @Path("invalid/path/param")
    public static class ResourceWithFaultyParamEntityFieldISE {
        @QueryParam("arg4")
        InvalidParamsTest.FaultyParamEntityISE f4;

        @GET
        public String doGet() {
            Assert.assertEquals(f4, null);
            return "foo";
        }
    }

    @Test
    public void testInvalidQueryParamFieldISE() throws Exception {
        ContainerResponse responseContext = createApplication(InvalidParamsTest.ResourceWithFaultyParamEntityFieldISE.class).apply(RequestContextBuilder.from("/invalid/path/param?arg4=test", "GET").build()).get();
        Assert.assertEquals(404, responseContext.getStatus());
    }

    @Path("invalid/path/param")
    public static class ResourceWithFaultyParamEntityParamISE {
        @GET
        public String doGet(@QueryParam("arg4")
        InvalidParamsTest.FaultyParamEntityISE s4) {
            Assert.assertEquals(s4, null);
            return "foo";
        }
    }

    @Test
    public void testInvalidQueryParamParamISE() throws Exception {
        ContainerResponse responseContext = createApplication(InvalidParamsTest.ResourceWithFaultyParamEntityParamISE.class).apply(RequestContextBuilder.from("/invalid/path/param?arg4=test", "GET").build()).get();
        Assert.assertEquals(404, responseContext.getStatus());
    }
}
