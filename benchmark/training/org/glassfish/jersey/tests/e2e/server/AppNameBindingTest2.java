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
package org.glassfish.jersey.tests.e2e.server;


import MediaType.TEXT_PLAIN_TYPE;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.NameBinding;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link NameBinding name binding} annotations on the {@link Application} class.
 *
 * @author Miroslav Fuksa
 */
public class AppNameBindingTest2 extends JerseyTest {
    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface FirstGlobalNameBinding {}

    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface SecondGlobalNameBinding {}

    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ThirdGlobalNameBinding {}

    @Provider
    @AppNameBindingTest2.FirstGlobalNameBinding
    public static class AddOneInterceptor implements WriterInterceptor {
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            String entity = ((String) (context.getEntity()));
            Integer i = Integer.parseInt(entity);
            entity = String.valueOf((i + 1));
            context.setEntity(entity);
            context.proceed();
        }
    }

    @Provider
    @AppNameBindingTest2.FirstGlobalNameBinding
    @AppNameBindingTest2.SecondGlobalNameBinding
    public static class AddHundredInterceptor implements WriterInterceptor {
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            String entity = ((String) (context.getEntity()));
            Integer i = Integer.parseInt(entity);
            entity = String.valueOf((i + 100));
            context.setEntity(entity);
            context.proceed();
        }
    }

    @Provider
    @AppNameBindingTest2.FirstGlobalNameBinding
    @AppNameBindingTest2.SecondGlobalNameBinding
    @AppNameBindingTest2.ThirdGlobalNameBinding
    public static class AddThousandInterceptor implements WriterInterceptor {
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            String entity = ((String) (context.getEntity()));
            Integer i = Integer.parseInt(entity);
            entity = String.valueOf((i + 1000));
            context.setEntity(entity);
            context.proceed();
        }
    }

    @Provider
    @AppNameBindingTest2.FirstGlobalNameBinding
    public static class AddTenFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            String entity = ((String) (responseContext.getEntity()));
            Integer i = Integer.valueOf(entity);
            entity = String.valueOf((i + 10));
            responseContext.setEntity(entity, ((Annotation[]) (null)), TEXT_PLAIN_TYPE);
        }
    }

    @AppNameBindingTest2.FirstGlobalNameBinding
    @AppNameBindingTest2.SecondGlobalNameBinding
    public class MyApp extends Application {
        public Set<Class<?>> getClasses() {
            Set<Class<?>> resources = new HashSet<Class<?>>();
            resources.add(AppNameBindingTest2.Resource.class);
            resources.add(AppNameBindingTest2.AddOneInterceptor.class);
            resources.add(AppNameBindingTest2.AddTenFilter.class);
            resources.add(AppNameBindingTest2.AddHundredInterceptor.class);
            resources.add(AppNameBindingTest2.AddThousandInterceptor.class);
            return resources;
        }
    }

    @Path("resource")
    public static class Resource {
        @POST
        @Path("bind")
        @AppNameBindingTest2.FirstGlobalNameBinding
        @AppNameBindingTest2.ThirdGlobalNameBinding
        @Produces("text/plain")
        public String echoWithBind(String echo) {
            // note: AddThousandInterceptor will not be triggered even we have here @ThirdGlobalNameBinding. Annotations from
            // Application class and from resource methods are evaluated separately.
            return echo;
        }

        @POST
        @Path("nobind")
        @Produces("text/plain")
        public String echoNoBind(String echo) {
            return echo;
        }
    }

    @Test
    public void testBind() {
        final Response response = target().path("resource/bind").request(TEXT_PLAIN_TYPE).post(Entity.entity(Integer.valueOf(0), TEXT_PLAIN_TYPE));
        Assert.assertEquals(200, response.getStatus());
        final Integer integer = response.readEntity(Integer.class);
        Assert.assertEquals(111, integer.intValue());
    }

    @Test
    public void testNoBind() {
        final Response response = target().path("resource/nobind").request(TEXT_PLAIN_TYPE).post(Entity.entity(Integer.valueOf(0), TEXT_PLAIN_TYPE));
        Assert.assertEquals(200, response.getStatus());
        final Integer integer = response.readEntity(Integer.class);
        Assert.assertEquals(111, integer.intValue());
    }
}

