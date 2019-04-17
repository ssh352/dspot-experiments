/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.examples.bookmark;


import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Michal Gajdos
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookmarkTest extends JerseyTest {
    @Test
    public void step1_getUsers() {
        final JSONArray users = target().path("resources/users/").request("application/json").get(JSONArray.class);
        Assert.assertTrue((users != null));
    }

    @Test
    public void step2_createUser() {
        boolean thrown = false;
        JSONObject user = new JSONObject();
        try {
            user.put("userid", "testuid").put("password", "test").put("email", "test@test.net").put("username", "Test User");
            target().path("resources/users/testuid").request().put(Entity.entity(user, "application/json"));
        } catch (Exception e) {
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertFalse(thrown);
    }

    @Test
    public void step3_getUsers2() {
        final JSONArray users = target().path("resources/users/").request("application/json").get(JSONArray.class);
        Assert.assertTrue((users != null));
        Assert.assertTrue(((users.length()) == 1));
    }

    @Test
    public void step4_updateUser() {
        boolean thrown = false;
        try {
            JSONObject user = target().path("resources/users/testuid").request("application/json").get(JSONObject.class);
            user.put("password", "NEW PASSWORD").put("email", "NEW@EMAIL.NET").put("username", "UPDATED TEST USER");
            target().path("resources/users/testuid").request().put(Entity.entity(user, "application/json"));
            user = target().path("resources/users/testuid").request("application/json").get(JSONObject.class);
            Assert.assertEquals(user.get("username"), "UPDATED TEST USER");
            Assert.assertEquals(user.get("email"), "NEW@EMAIL.NET");
            Assert.assertEquals(user.get("password"), "NEW PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertFalse(thrown);
    }

    // ugly.. but separation into separate test cases would be probably uglier
    @Test
    public void step5_getUserBookmarkList() {
        boolean thrown = false;
        try {
            JSONObject user = target().path("resources/users/testuid").request("application/json").get(JSONObject.class);
            Assert.assertTrue((user != null));
            final WebTarget webTarget = client().target(user.getString("bookmarks"));
            JSONObject bookmark = new JSONObject();
            bookmark.put("uri", "http://java.sun.com").put("sdesc", "test desc").put("ldesc", "long test description");
            webTarget.request().post(Entity.entity(bookmark, "application/json"));
            JSONArray bookmarks = webTarget.request("application/json").get(JSONArray.class);
            Assert.assertTrue((bookmarks != null));
            int bookmarksSize = bookmarks.length();
            String testBookmarkUrl = bookmarks.getString(0);
            final WebTarget bookmarkResource = client().target(testBookmarkUrl);
            bookmark = bookmarkResource.request("application/json").get(JSONObject.class);
            Assert.assertTrue((bookmark != null));
            bookmarkResource.request().delete();
            bookmarks = target().path("resources/users/testuid/bookmarks").request("application/json").get(JSONArray.class);
            Assert.assertTrue((bookmarks != null));
            Assert.assertTrue(((bookmarks.length()) == (bookmarksSize - 1)));
        } catch (Exception e) {
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertFalse(thrown);
    }

    @Test
    public void step6_deleteUser() {
        boolean thrown = false;
        try {
            target().path("resources/users/testuid").request().delete();
        } catch (Exception e) {
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertFalse(thrown);
    }
}
