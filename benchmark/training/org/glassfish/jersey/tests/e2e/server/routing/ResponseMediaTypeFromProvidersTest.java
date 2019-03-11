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
package org.glassfish.jersey.tests.e2e.server.routing;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class ResponseMediaTypeFromProvidersTest extends JerseyTest {
    @Path("response")
    public static class ResponseResource {
        private List<String> getList() {
            // must be an ArrayList. Arrays.asList creates Arrays$ArrayList.
            return Arrays.asList("array", "list").stream().collect(Collectors.toCollection(ArrayList::new));
        }

        @GET
        @Path("list")
        public Response responseList() {
            return Response.ok(getList()).build();
        }
    }

    @Provider
    public static class CollectionMessageBodyWriter implements MessageBodyWriter<Collection<?>> {
        @Override
        public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return (type.equals(ArrayList.class)) && (genericType.equals(ArrayList.class));
        }

        @Override
        public long getSize(final Collection<?> objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(final Collection<?> objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write("OK".getBytes());
        }
    }

    @Provider
    public static class IncorrectCollectionMessageBodyWriter implements MessageBodyWriter<Collection<?>> {
        @Override
        public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return !(new ResponseMediaTypeFromProvidersTest.CollectionMessageBodyWriter().isWriteable(type, genericType, annotations, mediaType));
        }

        @Override
        public long getSize(final Collection<?> objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(final Collection<?> objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write("ERROR".getBytes());
        }
    }

    @Test
    public void testResponseList() throws Exception {
        final Response response = target("response").path("list").request().get();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo("OK"));
    }

    public static class StringBean {
        private String value;

        public StringBean() {
        }

        public StringBean(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    @Provider
    public static class StringBeanMessageBodyWriter implements MessageBodyWriter<ResponseMediaTypeFromProvidersTest.StringBean> {
        public static MediaType STRING_BEAN_MT = new MediaType("string", "bean");

        @Override
        public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return (type.equals(ResponseMediaTypeFromProvidersTest.StringBean.class)) && (ResponseMediaTypeFromProvidersTest.StringBeanMessageBodyWriter.STRING_BEAN_MT.equals(mediaType));
        }

        @Override
        public long getSize(final ResponseMediaTypeFromProvidersTest.StringBean objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(final ResponseMediaTypeFromProvidersTest.StringBean objects, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write(objects.getValue().getBytes());
        }
    }

    @Path("AcceptableNonWriteableMethodResource")
    public static class AcceptableNonWriteableMethodResource {
        @GET
        public ResponseMediaTypeFromProvidersTest.StringBean getStringBean() {
            return new ResponseMediaTypeFromProvidersTest.StringBean("getStringBean");
        }

        @GET
        @Produces("text/html")
        public ResponseMediaTypeFromProvidersTest.StringBean getTextHtml() {
            return new ResponseMediaTypeFromProvidersTest.StringBean("getTextHtml");
        }

        @GET
        @Produces("text/xhtml")
        public ResponseMediaTypeFromProvidersTest.StringBean getTextXHtml() {
            return new ResponseMediaTypeFromProvidersTest.StringBean("getTextXHtml");
        }

        @POST
        @Consumes("string/bean")
        @Produces("string/bean")
        public ResponseMediaTypeFromProvidersTest.StringBean postStringBean(final ResponseMediaTypeFromProvidersTest.StringBean stringBean) {
            return stringBean;
        }

        @POST
        @Produces("string/bean")
        public ResponseMediaTypeFromProvidersTest.StringBean postStringBean(final String string) {
            return new ResponseMediaTypeFromProvidersTest.StringBean(("postStringBean_" + string));
        }

        @POST
        @Consumes("string/bean")
        @Path("response")
        public Response postResponse(final ResponseMediaTypeFromProvidersTest.StringBean stringBean) {
            return Response.ok(stringBean).type("string/bean").build();
        }

        @POST
        @Path("response")
        public Response postResponse(final String string) {
            return Response.ok(new ResponseMediaTypeFromProvidersTest.StringBean(("postStringBean_" + string))).type("string/bean").build();
        }
    }

    @Test
    public void testGetMethodRouting() throws Exception {
        final Response response = target("AcceptableNonWriteableMethodResource").request("text/html", "text/xhtml", "string/bean;q=0.2").get();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.getMediaType(), CoreMatchers.equalTo(ResponseMediaTypeFromProvidersTest.StringBeanMessageBodyWriter.STRING_BEAN_MT));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo("getStringBean"));
    }

    @Test
    public void testPostMethodRouting() throws Exception {
        final Response response = target("AcceptableNonWriteableMethodResource").request("text/html", "text/xhtml", "string/bean;q=0.2").post(Entity.entity("value", "string/bean"));
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.getMediaType(), CoreMatchers.equalTo(ResponseMediaTypeFromProvidersTest.StringBeanMessageBodyWriter.STRING_BEAN_MT));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo("postStringBean_value"));
    }

    @Test
    public void testPostMethodRoutingWildcard() throws Exception {
        final Response response = target("AcceptableNonWriteableMethodResource").request("*/*").post(Entity.entity("value", "string/bean"));
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.getMediaType(), CoreMatchers.equalTo(ResponseMediaTypeFromProvidersTest.StringBeanMessageBodyWriter.STRING_BEAN_MT));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo("postStringBean_value"));
    }

    @Test
    public void testPostMethodRoutingResponse() throws Exception {
        final Response response = target("AcceptableNonWriteableMethodResource").path("response").request().post(Entity.entity("value", "string/bean"));
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.getMediaType(), CoreMatchers.equalTo(ResponseMediaTypeFromProvidersTest.StringBeanMessageBodyWriter.STRING_BEAN_MT));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo("postStringBean_value"));
    }
}

