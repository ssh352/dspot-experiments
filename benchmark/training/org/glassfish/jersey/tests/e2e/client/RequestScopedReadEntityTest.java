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
package org.glassfish.jersey.tests.e2e.client;


import Invocation.Builder;
import MediaType.TEXT_PLAIN_TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO: javadoc.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class RequestScopedReadEntityTest extends JerseyTest {
    public static class Message {
        private final String text;

        public Message(String text) {
            this.text = text;
        }
    }

    @Path("simple")
    public static class SimpleResource {
        @GET
        @Produces("text/plain")
        public String getIt() {
            return "passed";
        }
    }

    @Produces("text/plain")
    public static class ScopedMessageEntityProvider extends AbstractMessageReaderWriterProvider<RequestScopedReadEntityTest.Message> {
        @Inject
        private Provider<ClientRequest> clientRequestProvider;

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return (type == (RequestScopedReadEntityTest.Message.class)) && (mediaType.equals(TEXT_PLAIN_TYPE));
        }

        @Override
        public RequestScopedReadEntityTest.Message readFrom(Class<RequestScopedReadEntityTest.Message> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return (clientRequestProvider.get()) != null ? new RequestScopedReadEntityTest.Message(readFromAsString(entityStream, mediaType)) : new RequestScopedReadEntityTest.Message("failed");
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return (type == (RequestScopedReadEntityTest.Message.class)) && (mediaType.equals(TEXT_PLAIN_TYPE));
        }

        @Override
        public void writeTo(RequestScopedReadEntityTest.Message message, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            writeToAsString(((clientRequestProvider.get()) != null ? message.text : "failed"), entityStream, mediaType);
        }
    }

    @Test
    public void testReadAfterClose() {
        final Invocation.Builder request = target().path("simple").register(RequestScopedReadEntityTest.ScopedMessageEntityProvider.class).request();
        final Response response = request.get(Response.class);
        // reading entity "out-of-scope"
        final RequestScopedReadEntityTest.Message msg = response.readEntity(RequestScopedReadEntityTest.Message.class);
        Assert.assertEquals("passed", msg.text);
    }
}

