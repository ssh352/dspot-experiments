/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.filter;


import Response.Status.BAD_REQUEST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.glassfish.jersey.server.RequestContextBuilder.from;


/**
 *
 *
 * @author Martin Matula
 */
public class CsrfProtectionFilterTest {
    private ApplicationHandler handler;

    @Path("/resource")
    public static class Resource {
        @GET
        public String get() {
            return "GET";
        }

        @PUT
        public String put() {
            return "PUT";
        }
    }

    @Test
    public void testGetNoHeader() throws Exception {
        ContainerResponse response = handler.apply(from("", "/resource", "GET").build()).get();
        Assert.assertEquals("GET", response.getEntity());
    }

    @Test
    public void testGetWithHeader() throws Exception {
        ContainerResponse response = handler.apply(from("", "/resource", "GET").header(CsrfProtectionFilter.HEADER_NAME, "").build()).get();
        Assert.assertEquals("GET", response.getEntity());
    }

    @Test
    public void testPutNoHeader() throws Exception {
        ContainerResponse response = handler.apply(from("", "/resource", "PUT").build()).get();
        Assert.assertEquals(BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testPutWithHeader() throws Exception {
        ContainerResponse response = handler.apply(from("", "/resource", "PUT").header(CsrfProtectionFilter.HEADER_NAME, "").build()).get();
        Assert.assertEquals("PUT", response.getEntity());
    }
}

