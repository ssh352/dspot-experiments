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
package org.glassfish.jersey.osgi.test.basic;


import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.osgi.test.util.Helper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


/**
 *
 *
 * @author Michal Gajdos
 */
@RunWith(PaxExam.class)
public class WebResourceFactoryTest {
    private static final String CONTEXT = "/jersey";

    private static final URI baseUri = UriBuilder.fromUri("http://localhost").port(Helper.getPort()).path(WebResourceFactoryTest.CONTEXT).build();

    @XmlRootElement
    public static class MyBean {
        public String name;
    }

    @Path("myresource")
    public static interface MyResourceIfc {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        String getIt();

        @POST
        @Consumes({ MediaType.APPLICATION_XML })
        @Produces({ MediaType.APPLICATION_XML })
        List<WebResourceFactoryTest.MyBean> postIt(List<WebResourceFactoryTest.MyBean> entity);

        @Path("{id}")
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        String getId(@PathParam("id")
        String id);

        @Path("query")
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        String getByName(@QueryParam("name")
        String name);

        @Path("subresource")
        WebResourceFactoryTest.MySubResourceIfc getSubResource();
    }

    public static class MyResource implements WebResourceFactoryTest.MyResourceIfc {
        @Override
        public String getIt() {
            return "Got it!";
        }

        @Override
        public List<WebResourceFactoryTest.MyBean> postIt(List<WebResourceFactoryTest.MyBean> entity) {
            return entity;
        }

        @Override
        public String getId(String id) {
            return id;
        }

        @Override
        public String getByName(String name) {
            return name;
        }

        @Override
        public WebResourceFactoryTest.MySubResourceIfc getSubResource() {
            return new WebResourceFactoryTest.MySubResource();
        }
    }

    public static class MySubResource implements WebResourceFactoryTest.MySubResourceIfc {
        @Override
        public WebResourceFactoryTest.MyBean getMyBean() {
            WebResourceFactoryTest.MyBean bean = new WebResourceFactoryTest.MyBean();
            bean.name = "Got it!";
            return bean;
        }
    }

    public static interface MySubResourceIfc {
        @GET
        @Produces(MediaType.APPLICATION_XML)
        public WebResourceFactoryTest.MyBean getMyBean();
    }

    private HttpServer server;

    private WebResourceFactoryTest.MyResourceIfc resource;

    @Test
    public void testGetIt() {
        Assert.assertEquals("Got it!", resource.getIt());
    }

    @Test
    public void testPostIt() {
        WebResourceFactoryTest.MyBean bean = new WebResourceFactoryTest.MyBean();
        bean.name = "Foo";
        Assert.assertEquals("Foo", resource.postIt(Collections.singletonList(bean)).get(0).name);
    }

    @Test
    public void testPathParam() {
        Assert.assertEquals("Bar", resource.getId("Bar"));
    }

    @Test
    public void testQueryParam() {
        Assert.assertEquals("Jersey2", resource.getByName("Jersey2"));
    }

    @Test
    public void testSubResource() {
        Assert.assertEquals("Got it!", resource.getSubResource().getMyBean().name);
    }
}

