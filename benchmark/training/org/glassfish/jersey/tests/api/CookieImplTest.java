/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.api;


import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import org.glassfish.jersey.message.internal.HttpHeaderReader;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marc Hadley
 */
public class CookieImplTest {
    @Test
    public void testCookieToString() {
        Cookie cookie = new Cookie("fred", "flintstone");
        String expResult = "$Version=1;fred=flintstone";
        Assert.assertEquals(expResult, cookie.toString());
        cookie = new Cookie("fred", "flintstone", "/path", null);
        expResult = "$Version=1;fred=flintstone;$Path=/path";
        Assert.assertEquals(expResult, cookie.toString());
        cookie = new Cookie("fred", "flintstone", "/path", ".sun.com");
        expResult = "$Version=1;fred=flintstone;$Domain=.sun.com;$Path=/path";
        Assert.assertEquals(expResult, cookie.toString());
        cookie = new Cookie("fred", "flintstone", "/path", ".sun.com", 2);
        expResult = "$Version=2;fred=flintstone;$Domain=.sun.com;$Path=/path";
        Assert.assertEquals(expResult, cookie.toString());
    }

    @Test
    public void testCookieValueOf() {
        Cookie cookie = Cookie.valueOf("$Version=2;fred=flintstone");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals(2, cookie.getVersion());
        cookie = Cookie.valueOf("$Version=1;fred=flintstone;$Path=/path");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals(1, cookie.getVersion());
        Assert.assertEquals("/path", cookie.getPath());
        cookie = Cookie.valueOf("$Version=1;fred=flintstone;$Domain=.sun.com;$Path=/path");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals(1, cookie.getVersion());
        Assert.assertEquals(".sun.com", cookie.getDomain());
        Assert.assertEquals("/path", cookie.getPath());
    }

    @Test
    public void testCreateCookies() {
        String cookieHeader = "fred=flintstone";
        Map<String, Cookie> cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 1);
        Cookie c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 0);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        cookieHeader = "fred=flintstone,barney=rubble";
        cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 2);
        c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 0);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        c = cookies.get("barney");
        Assert.assertEquals(c.getVersion(), 0);
        Assert.assertTrue("barney".equals(c.getName()));
        Assert.assertTrue("rubble".equals(c.getValue()));
        cookieHeader = "fred=flintstone;barney=rubble";
        cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 2);
        c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 0);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        c = cookies.get("barney");
        Assert.assertEquals(c.getVersion(), 0);
        Assert.assertTrue("barney".equals(c.getName()));
        Assert.assertTrue("rubble".equals(c.getValue()));
        cookieHeader = "$Version=1;fred=flintstone;$Path=/path;barney=rubble";
        cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 2);
        c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        Assert.assertTrue("/path".equals(c.getPath()));
        c = cookies.get("barney");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("barney".equals(c.getName()));
        Assert.assertTrue("rubble".equals(c.getValue()));
        cookieHeader = "$Version=1;fred=flintstone;$Path=/path,barney=rubble;$Domain=.sun.com";
        cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 2);
        c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        Assert.assertTrue("/path".equals(c.getPath()));
        c = cookies.get("barney");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("barney".equals(c.getName()));
        Assert.assertTrue("rubble".equals(c.getValue()));
        Assert.assertTrue(".sun.com".equals(c.getDomain()));
        cookieHeader = "$Version=1; fred = flintstone ; $Path=/path, barney=rubble ;$Domain=.sun.com";
        cookies = HttpHeaderReader.readCookies(cookieHeader);
        Assert.assertEquals(cookies.size(), 2);
        c = cookies.get("fred");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("fred".equals(c.getName()));
        Assert.assertTrue("flintstone".equals(c.getValue()));
        Assert.assertTrue("/path".equals(c.getPath()));
        c = cookies.get("barney");
        Assert.assertEquals(c.getVersion(), 1);
        Assert.assertTrue("barney".equals(c.getName()));
        Assert.assertTrue("rubble".equals(c.getValue()));
        Assert.assertTrue(".sun.com".equals(c.getDomain()));
    }

    @Test
    public void testNewCookieToString() {
        NewCookie cookie = new NewCookie("fred", "flintstone");
        String expResult = "fred=flintstone;Version=1";
        Assert.assertEquals(expResult, cookie.toString());
        cookie = new NewCookie("fred", "flintstone", null, null, null, 60, false);
        expResult = "fred=flintstone;Version=1;Max-Age=60";
        Assert.assertEquals(expResult, cookie.toString());
        cookie = new NewCookie("fred", "flintstone", null, null, "a modern stonage family", 60, false);
        expResult = "fred=flintstone;Version=1;Comment=\"a modern stonage family\";Max-Age=60";
        Assert.assertEquals(expResult, cookie.toString());
    }

    @Test
    public void testNewCookieValueOf() {
        NewCookie cookie = NewCookie.valueOf("fred=flintstone;Version=2");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals(2, cookie.getVersion());
        cookie = NewCookie.valueOf("fred=flintstone;Version=1;Max-Age=60");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals(1, cookie.getVersion());
        Assert.assertEquals(60, cookie.getMaxAge());
        cookie = NewCookie.valueOf("fred=flintstone;Version=1;Comment=\"a modern stonage family\";Max-Age=60;Secure");
        Assert.assertEquals("fred", cookie.getName());
        Assert.assertEquals("flintstone", cookie.getValue());
        Assert.assertEquals("a modern stonage family", cookie.getComment());
        Assert.assertEquals(1, cookie.getVersion());
        Assert.assertEquals(60, cookie.getMaxAge());
        Assert.assertTrue(cookie.isSecure());
    }
}

