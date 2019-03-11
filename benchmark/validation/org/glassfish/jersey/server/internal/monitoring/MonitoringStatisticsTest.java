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
package org.glassfish.jersey.server.internal.monitoring;


import Resource.Builder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Miroslav Fuksa
 */
public class MonitoringStatisticsTest {
    @Path("/test-resource")
    public static class TestResource {
        @GET
        public String get() {
            return "get";
        }

        @Path("child")
        @GET
        public String childGet() {
            return "childGet";
        }

        @Path("child")
        @POST
        public void childPost() {
        }

        @Path("child")
        @PUT
        public void childPut() {
        }
    }

    @Path("hello")
    public static class HelloResource {
        @GET
        public String get() {
            return "hello";
        }

        @POST
        public void post() {
        }

        @Path("/world")
        @GET
        public String childGet() {
            return "hello-world";
        }
    }

    public static class MyInflector implements Inflector<ContainerRequestContext, Object> {
        @Override
        public Object apply(ContainerRequestContext containerRequestContext) {
            return Response.ok().build();
        }
    }

    @Test
    public void testSimpleUris() {
        final MonitoringStatisticsImpl stats = getSimpleStats();
        final Set<String> keys = stats.getUriStatistics().keySet();
        final Iterator<String> iterator = keys.iterator();
        Assert.assertEquals("/hello", iterator.next());
        Assert.assertEquals("/hello/world", iterator.next());
        Assert.assertEquals("/test-resource", iterator.next());
        Assert.assertEquals("/test-resource/child", iterator.next());
    }

    @Test
    public void testSimpleResourceClasses() {
        final MonitoringStatisticsImpl stats = getSimpleStats();
        final Set<Class<?>> keys = stats.getResourceClassStatistics().keySet();
        final Iterator<Class<?>> it = keys.iterator();
        Assert.assertEquals(MonitoringStatisticsTest.HelloResource.class, it.next());
        Assert.assertEquals(MonitoringStatisticsTest.TestResource.class, it.next());
    }

    @Test
    public void testResourceClassesWithProgrammaticResources() {
        final MonitoringStatisticsImpl stats = getProgStats().build();
        final Set<Class<?>> keys = stats.getResourceClassStatistics().keySet();
        final Iterator<Class<?>> it = keys.iterator();
        Assert.assertEquals(MonitoringStatisticsTest.HelloResource.class, it.next());
        Assert.assertEquals(MonitoringStatisticsTest.MyInflector.class, it.next());
        Assert.assertEquals(MonitoringStatisticsTest.TestResource.class, it.next());
    }

    @Test
    public void testUrisWithProgrammaticResources() {
        final MonitoringStatisticsImpl stats = getProgStats().build();
        final Iterator<Map.Entry<String, ResourceStatistics>> it = stats.getUriStatistics().entrySet().iterator();
        check(it, "/hello", 2);
        check(it, "/hello/world", 1);
        check(it, "/prog", 1);
        check(it, "/test-resource", 1);
        check(it, "/test-resource/child", 3);
        check(it, "/test-resource/prog-child", 1);
    }

    @Test
    public void testUrisWithProgrammaticResourcesAndExecution() {
        final MonitoringStatisticsImpl.Builder statBuilder = getProgStats();
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.addMethod("GET").handledBy(MonitoringStatisticsTest.MyInflector.class);
        resourceBuilder.addMethod("POST").handledBy(MonitoringStatisticsTest.MyInflector.class);
        final Resource res = resourceBuilder.build();
        ResourceMethod getMethod;
        ResourceMethod postMethod;
        if (res.getResourceMethods().get(0).getHttpMethod().equals("GET")) {
            getMethod = res.getResourceMethods().get(0);
            postMethod = res.getResourceMethods().get(1);
        } else {
            getMethod = res.getResourceMethods().get(1);
            postMethod = res.getResourceMethods().get(0);
        }
        statBuilder.addExecution("/new/elefant", getMethod, 10, 5, 8, 8);
        statBuilder.addExecution("/new/elefant", getMethod, 20, 12, 18, 10);
        statBuilder.addExecution("/new/elefant", postMethod, 30, 2, 28, 4);
        final MonitoringStatisticsImpl stat = statBuilder.build();
        final Iterator<Map.Entry<String, ResourceStatistics>> it = stat.getUriStatistics().entrySet().iterator();
        check(it, "/hello", 2);
        check(it, "/hello/world", 1);
        check(it, "/new/elefant", 2);
        check(it, "/prog", 1);
        check(it, "/test-resource", 1);
        check(it, "/test-resource/child", 3);
        check(it, "/test-resource/prog-child", 1);
        final Map<ResourceMethod, ResourceMethodStatistics> resourceMethodStatistics = stat.getUriStatistics().get("/new/elefant").getResourceMethodStatistics();
        for (ResourceMethodStatistics methodStatistics : resourceMethodStatistics.values()) {
            final ResourceMethod method = methodStatistics.getResourceMethod();
            final ExecutionStatistics st = methodStatistics.getMethodStatistics();
            if (method.getHttpMethod().equals("GET")) {
                Assert.assertEquals(20, st.getLastStartTime().getTime());
            } else
                if (method.getHttpMethod().equals("POST")) {
                    Assert.assertEquals(30, st.getLastStartTime().getTime());
                } else {
                    Assert.fail();
                }

        }
    }
}

