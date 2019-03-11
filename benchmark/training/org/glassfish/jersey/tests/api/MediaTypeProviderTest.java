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


import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.message.internal.MediaTypeProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * Media type provider tests.
 *
 * @author Mark Hadley
 * @author Adam Lindenthal (adam.lindenthal at oracle.com)
 */
public class MediaTypeProviderTest {
    @Test
    public void testToString() {
        final MediaType header = new MediaType("application", "xml");
        final MediaTypeProvider instance = new MediaTypeProvider();
        final String expResult = "application/xml";
        final String result = instance.toString(header);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testToStringWithParams() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("charset", "utf8");
        final MediaType header = new MediaType("application", "xml", params);
        final MediaTypeProvider instance = new MediaTypeProvider();
        final String expResult = "application/xml;charset=utf8";
        final String result = instance.toString(header);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFromString() throws Exception {
        final MediaTypeProvider instance = new MediaTypeProvider();
        final String header = "application/xml";
        final MediaType result = instance.fromString(header);
        Assert.assertEquals(result.getType(), "application");
        Assert.assertEquals(result.getSubtype(), "xml");
        Assert.assertEquals(result.getParameters().size(), 0);
    }

    @Test
    public void testFromStringWithParams() throws Exception {
        final String header = "application/xml;charset=utf8";
        final MediaTypeProvider instance = new MediaTypeProvider();
        final MediaType result = instance.fromString(header);
        Assert.assertEquals(result.getType(), "application");
        Assert.assertEquals(result.getSubtype(), "xml");
        Assert.assertEquals(result.getParameters().size(), 1);
        Assert.assertTrue(result.getParameters().containsKey("charset"));
        Assert.assertEquals(result.getParameters().get("charset"), "utf8");
    }

    @Test
    public void testWithQuotedParam() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("foo", "\"bar\"");
        final MediaType header = new MediaType("application", "xml", params);
        final MediaTypeProvider instance = new MediaTypeProvider();
        final String result = instance.toString(header);
        final String expResult = "application/xml;foo=\"\\\"bar\\\"\"";
        Assert.assertEquals(expResult, result);
        final MediaType m = instance.fromString(result);
        Assert.assertEquals("\"bar\"", m.getParameters().get("foo"));
    }
}

