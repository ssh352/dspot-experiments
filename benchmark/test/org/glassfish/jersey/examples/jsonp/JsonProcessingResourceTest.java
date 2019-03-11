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
package org.glassfish.jersey.examples.jsonp;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_TYPE;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class JsonProcessingResourceTest extends JerseyTest {
    private static final List<JsonObject> documents = new ArrayList<>();

    static {
        JsonProcessingResourceTest.documents.add(Json.createObjectBuilder().add("name", "Jersey").add("site", "http://jersey.github.io").build());
        JsonProcessingResourceTest.documents.add(Json.createObjectBuilder().add("age", 33).add("phone", "158158158").add("name", "Foo").build());
        JsonProcessingResourceTest.documents.add(Json.createObjectBuilder().add("name", "JSON-P").add("site", "https://javaee.github.io/jsonp/").build());
    }

    @Test
    public void testStoreGetRemoveDocument() throws Exception {
        final JsonObject document = JsonProcessingResourceTest.documents.get(0);
        // Store.
        final Response response = target("document").request(APPLICATION_JSON).post(Entity.json(document));
        Assert.assertEquals(200, response.getStatus());
        final List<JsonNumber> ids = response.readEntity(JsonArray.class).getValuesAs(JsonNumber.class);
        Assert.assertEquals(1, ids.size());
        // Get.
        final String id = ids.get(0).toString();
        final WebTarget documentTarget = target("document").path(id);
        final JsonObject storedDocument = documentTarget.request(APPLICATION_JSON).get(JsonObject.class);
        Assert.assertEquals(document, storedDocument);
        // Remove.
        final JsonObject removedDocument = documentTarget.request(APPLICATION_JSON).delete(JsonObject.class);
        Assert.assertEquals(document, removedDocument);
        // Get.
        final Response errorResponse = documentTarget.request(APPLICATION_JSON).get();
        Assert.assertEquals(204, errorResponse.getStatus());
    }

    @Test
    public void testStoreDocuments() throws Exception {
        final Response response = target("document/multiple").request(APPLICATION_JSON).post(Entity.json(getDocumentJsonArray()));
        Assert.assertEquals(200, response.getStatus());
        final List<JsonNumber> ids = response.readEntity(JsonArray.class).getValuesAs(JsonNumber.class);
        Assert.assertEquals(JsonProcessingResourceTest.documents.size(), ids.size());
        // Remove All.
        target("document").request().delete();
    }

    @Test
    public void testFilterDocuments() throws Exception {
        // Store documents.
        target("document/multiple").request(APPLICATION_JSON_TYPE).post(Entity.json(getDocumentJsonArray()));
        // Filter.
        JsonArray filter = Json.createArrayBuilder().add("site").build();
        JsonArray filtered = target("document/filter").request(APPLICATION_JSON).post(Entity.json(filter), JsonArray.class);
        checkFilteredDocuments(filtered, 2, "site");
        filter = Json.createArrayBuilder().add("site").add("age").build();
        filtered = target("document/filter").request(APPLICATION_JSON).post(Entity.json(filter), JsonArray.class);
        checkFilteredDocuments(filtered, 3, "site", "age");
        // Remove All.
        target("document").request().delete();
    }
}

