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
package org.glassfish.jersey.tests.e2e.server;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 *
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class InterceptorNameAndDynamicBindingTest extends JerseyTest {
    static final String ENTITY = "ENTITY";

    abstract static class PrefixAddingReaderInterceptor implements ReaderInterceptor {
        public PrefixAddingReaderInterceptor() {
        }

        @Override
        public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
            context.setInputStream(new SequenceInputStream(new ByteArrayInputStream(getPrefix().getBytes()), context.getInputStream()));
            return context.proceed();
        }

        abstract String getPrefix();
    }

    abstract static class PrefixAddingWriterInterceptor implements WriterInterceptor {
        @Override
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            context.getOutputStream().write(getPrefix().getBytes());
            context.proceed();
        }

        abstract String getPrefix();
    }

    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    static @interface NameBoundReader {}

    @InterceptorNameAndDynamicBindingTest.NameBoundReader
    @Priority(40)
    static class NameBoundReaderInterceptor extends InterceptorNameAndDynamicBindingTest.PrefixAddingReaderInterceptor {
        @Override
        String getPrefix() {
            return "nameBoundReader";
        }
    }

    @Priority(60)
    static class DynamicallyBoundReaderInterceptor extends InterceptorNameAndDynamicBindingTest.PrefixAddingReaderInterceptor {
        @Override
        String getPrefix() {
            return "dynamicallyBoundReader";
        }
    }

    @NameBinding
    @Priority(40)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface NameBoundWriter {}

    @InterceptorNameAndDynamicBindingTest.NameBoundWriter
    public static class NameBoundWriterInterceptor extends InterceptorNameAndDynamicBindingTest.PrefixAddingWriterInterceptor {
        @Override
        String getPrefix() {
            return "nameBoundWriter";
        }
    }

    @Priority(20)
    public static class DynamicallyBoundWriterInterceptor extends InterceptorNameAndDynamicBindingTest.PrefixAddingWriterInterceptor {
        @Override
        String getPrefix() {
            return "dynamicallyBoundWriter";
        }
    }

    @Path("method")
    public static class MethodBindingResource {
        @Path("dynamicallyBoundWriter")
        @GET
        public String getDynamicallyBoundWriter() {
            return InterceptorNameAndDynamicBindingTest.ENTITY;
        }

        @Path("nameBoundWriter")
        @GET
        @InterceptorNameAndDynamicBindingTest.NameBoundWriter
        public String getNameBoundWriter() {
            return InterceptorNameAndDynamicBindingTest.ENTITY;
        }

        @Path("dynamicallyBoundReader")
        @POST
        public String postDynamicallyBoundReader(String input) {
            return input;
        }

        @Path("nameBoundReader")
        @POST
        @InterceptorNameAndDynamicBindingTest.NameBoundReader
        public String postNameBoundReader(String input) {
            return input;
        }
    }

    @Path("class")
    @InterceptorNameAndDynamicBindingTest.NameBoundWriter
    public static class ClassBindingResource {
        @Path("nameBoundWriter")
        @GET
        public String getNameBoundWriter() {
            return InterceptorNameAndDynamicBindingTest.ENTITY;
        }

        @Path("nameBoundReader")
        @POST
        public String postNameBoundReader(String input) {
            return input;
        }
    }

    @Path("mixed")
    @InterceptorNameAndDynamicBindingTest.NameBoundWriter
    public static class MixedBindingResource {
        @Path("nameBoundWriterDynamicReader")
        @POST
        public String postNameBoundWrDynamicallyBoundReader(String input) {
            return input;
        }

        @Path("nameBoundWriterDynamicWriterNameBoundReader")
        @POST
        @InterceptorNameAndDynamicBindingTest.NameBoundReader
        public String postNameBoundReWrDynamicallyBoundWriter(String input) {
            return input;
        }
    }

    static final Pattern ReaderMETHOD = Pattern.compile(".*Dynamically.*Reader");

    static final Pattern WriterMETHOD = Pattern.compile(".*Dynamically.*Writer");

    @Test
    public void testNameBoundReaderOnMethod() {
        _testReader("method", "nameBoundReader");
    }

    @Test
    public void testNameBoundWriterOnMethod() {
        _testWriter("method", "nameBoundWriter");
    }

    @Test
    public void testNameBoundReaderOnClass() {
        _testReader("class", "nameBoundReader", "nameBoundWriterENTITY");
    }

    @Test
    public void testNameBoundWriterOnClass() {
        _testWriter("class", "nameBoundWriter");
    }

    @Test
    public void testDynamicallyBoundReaderOnMethod() {
        _testReader("method", "dynamicallyBoundReader");
    }

    @Test
    public void testDynamicallyBoundWriterOnMethod() {
        _testWriter("method", "dynamicallyBoundWriter");
    }

    @Test
    public void testDynamicReaderOnMethodNamedWriterOnClass() {
        _testReader("mixed", "nameBoundWriterDynamicReader", "nameBoundWriterdynamicallyBoundReaderENTITY");
    }

    @Test
    public void testNameBoundWriterDynamicWriterNameBoundReader() {
        _testReader("mixed", "nameBoundWriterDynamicWriterNameBoundReader", "dynamicallyBoundWriternameBoundWriternameBoundReaderENTITY");
    }
}

