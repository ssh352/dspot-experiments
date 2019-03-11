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
package org.glassfish.jersey.tests.e2e.json;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_OCTET_STREAM;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.JSONP;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
// @RunWith(ConcurrentRunner.class)
public class JsonProcessingTest extends JerseyTest {
    private static final String JSON_OBJECT_STR1 = "{\"foo\":\"bar\"}";

    private static final String JSON_OBJECT_STR2 = "{\"foo\": 12345}";

    private static final String JSON_ARRAY_STR1 = ((("[" + (JsonProcessingTest.JSON_OBJECT_STR1)) + ",") + (JsonProcessingTest.JSON_OBJECT_STR1)) + "]";

    private static final String JSON_ARRAY_STR2 = ((("[" + (JsonProcessingTest.JSON_OBJECT_STR2)) + ",") + (JsonProcessingTest.JSON_OBJECT_STR2)) + "]";

    private static final String JSON_ARRAY_VALUE_STR = "[null]";

    private static final JsonObject JSON_OBJECT = Json.createReader(new StringReader(JsonProcessingTest.JSON_OBJECT_STR1)).readObject();

    private static final JsonArray JSON_ARRAY = Json.createReader(new StringReader(JsonProcessingTest.JSON_ARRAY_STR1)).readArray();

    private static final JsonArray JSON_ARRAY_VALUE = Json.createReader(new StringReader(JsonProcessingTest.JSON_ARRAY_VALUE_STR)).readArray();

    private static final JsonValue JSON_VALUE_BOOL = JsonValue.TRUE;

    private static final JsonString JSON_VALUE_STRING = Json.createReader(new StringReader(JsonProcessingTest.JSON_ARRAY_STR1)).readArray().getJsonObject(0).getJsonString("foo");

    private static final JsonNumber JSON_VALUE_NUMBER = Json.createReader(new StringReader(JsonProcessingTest.JSON_ARRAY_STR2)).readArray().getJsonObject(0).getJsonNumber("foo");

    @Path("/")
    public static class Resource {
        @POST
        @Path("jsonObject")
        public JsonObject postJsonObject(final JsonObject jsonObject) {
            return jsonObject;
        }

        @POST
        @Path("jsonStructure")
        public JsonStructure postJsonStructure(final JsonStructure jsonStructure) {
            return jsonStructure;
        }

        @POST
        @Path("jsonArray")
        public JsonArray postJsonArray(final JsonArray jsonArray) {
            return jsonArray;
        }

        @POST
        @Path("jsonValue")
        public JsonValue postJsonValue(final JsonValue jsonValue) {
            return jsonValue;
        }

        @POST
        @Path("jsonString")
        public JsonString postJsonString(final JsonString jsonString) {
            return jsonString;
        }

        @POST
        @Path("jsonNumber")
        public JsonValue postJsonNumber(final JsonNumber jsonNumber) {
            return jsonNumber;
        }

        @GET
        @JSONP
        @Path("jsonObjectWithPadding")
        @Produces("application/javascript")
        public JsonObject getJsonObjectWithPadding() {
            return JsonProcessingTest.JSON_OBJECT;
        }
    }

    @Test
    public void testJsonObject() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(JsonProcessingTest.JSON_OBJECT, response.readEntity(JsonObject.class));
    }

    @Test
    public void testJsonObjectAsString() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT_STR1));
        Assert.assertEquals(JsonProcessingTest.JSON_OBJECT, response.readEntity(JsonObject.class));
    }

    @Test
    public void testJsonObjectPlus() throws Exception {
        final Response response = target("jsonObject").request("application/foo+json").post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(JsonProcessingTest.JSON_OBJECT, response.readEntity(JsonObject.class));
    }

    @Test
    public void testJsonObjectAsStringPlus() throws Exception {
        final Response response = target("jsonObject").request("application/foo+json").post(Entity.json(JsonProcessingTest.JSON_OBJECT_STR1));
        Assert.assertEquals(JsonProcessingTest.JSON_OBJECT, response.readEntity(JsonObject.class));
    }

    @Test
    public void testJsonObjectWrongTarget() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonObjectAsStringWrongTarget() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonObjectWrongEntity() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonObjectAsStringWrongEntity() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonObjectWrongMediaType() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_OCTET_STREAM).post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonObjectAsStringWrongMediaType() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_OCTET_STREAM).post(Entity.json(JsonProcessingTest.JSON_OBJECT_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArray() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY, response.readEntity(JsonArray.class));
    }

    @Test
    public void testJsonArrayAsString() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY_STR1));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY, response.readEntity(JsonArray.class));
    }

    @Test
    public void testJsonArrayPlus() throws Exception {
        final Response response = target("jsonArray").request("application/foo+json").post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY, response.readEntity(JsonArray.class));
    }

    @Test
    public void testJsonArrayAsStringPlus() throws Exception {
        final Response response = target("jsonArray").request("application/foo+json").post(Entity.json(JsonProcessingTest.JSON_ARRAY_STR1));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY, response.readEntity(JsonArray.class));
    }

    @Test
    public void testJsonArrayWrongTarget() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArrayAsStringWrongTarget() throws Exception {
        final Response response = target("jsonObject").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArrayWrongEntity() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArrayAsStringWrongEntity() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArrayWrongMediaType() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_OCTET_STREAM).post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArraytAsStringWrongMediaType() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_OCTET_STREAM).post(Entity.json(JsonProcessingTest.JSON_ARRAY_STR1));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testJsonArrayValueEntity() throws Exception {
        final Response response = target("jsonArray").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY_VALUE));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY_VALUE, response.readEntity(JsonArray.class));
    }

    @Test
    public void testJsonStructureArray() throws Exception {
        final Response response = target("jsonStructure").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_ARRAY));
        Assert.assertEquals(JsonProcessingTest.JSON_ARRAY, response.readEntity(JsonStructure.class));
    }

    @Test
    public void testJsonStructureObject() throws Exception {
        final Response response = target("jsonStructure").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_OBJECT));
        Assert.assertEquals(JsonProcessingTest.JSON_OBJECT, response.readEntity(JsonStructure.class));
    }

    @Test
    public void testJsonValueBool() throws Exception {
        final Response response = target("jsonValue").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_VALUE_BOOL));
        Assert.assertEquals(JsonProcessingTest.JSON_VALUE_BOOL, response.readEntity(JsonValue.class));
    }

    @Test
    public void testJsonValueString() throws Exception {
        final Response response = target("jsonString").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_VALUE_STRING));
        Assert.assertEquals(JsonProcessingTest.JSON_VALUE_STRING, response.readEntity(JsonString.class));
    }

    @Test
    public void testJsonValueStringAsValue() throws Exception {
        final Response response = target("jsonValue").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_VALUE_STRING));
        Assert.assertEquals(JsonProcessingTest.JSON_VALUE_STRING, response.readEntity(JsonString.class));
    }

    @Test
    public void testJsonValueStringAsString() throws Exception {
        final Response response = target("jsonValue").request(APPLICATION_JSON).post(Entity.json("\"Red 5\""));
        Assert.assertEquals("Red 5", response.readEntity(JsonString.class).getString());
    }

    @Test
    public void testJsonValueNumber() throws Exception {
        final Response response = target("jsonNumber").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_VALUE_NUMBER));
        Assert.assertEquals(JsonProcessingTest.JSON_VALUE_NUMBER, response.readEntity(JsonNumber.class));
    }

    @Test
    public void testJsonValueNumberAsValue() throws Exception {
        final Response response = target("jsonValue").request(APPLICATION_JSON).post(Entity.json(JsonProcessingTest.JSON_VALUE_NUMBER));
        Assert.assertEquals(JsonProcessingTest.JSON_VALUE_NUMBER, response.readEntity(JsonNumber.class));
    }

    @Test
    public void testJsonObjectWithPadding() throws Exception {
        final Response response = target("jsonObjectWithPadding").request("application/javascript").get();
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.is(((((JSONP.DEFAULT_CALLBACK) + "(") + (JsonProcessingTest.JSON_OBJECT_STR1)) + ")")));
    }
}

