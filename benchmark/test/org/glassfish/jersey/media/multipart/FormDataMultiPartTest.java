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
package org.glassfish.jersey.media.multipart;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for {@link FormDataMultiPart}.
 *
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class FormDataMultiPartTest extends MultiPartTest {
    @Test
    @SuppressWarnings("empty-statement")
    public void testFieldsFDMP() throws Exception {
        FormDataMultiPart fdmp = ((FormDataMultiPart) (multiPart));
        Assert.assertEquals(0, fdmp.getFields().size());
        fdmp = fdmp.field("foo", "bar").field("baz", "bop");
        Assert.assertEquals(2, fdmp.getFields().size());
        Assert.assertNotNull(fdmp.getField("foo"));
        Assert.assertEquals("bar", fdmp.getField("foo").getValue());
        Assert.assertNotNull(fdmp.getField("baz"));
        Assert.assertEquals("bop", fdmp.getField("baz").getValue());
        Assert.assertEquals("bar", fdmp.getFields("foo").get(0).getValue());
        Assert.assertEquals("bop", fdmp.getFields("baz").get(0).getValue());
        Assert.assertNotNull(fdmp.getFields().get("foo"));
        Assert.assertEquals("bar", fdmp.getFields().get("foo").get(0).getValue());
        Assert.assertNotNull(fdmp.getFields().get("baz"));
        Assert.assertEquals("bop", fdmp.getFields().get("baz").get(0).getValue());
        fdmp = fdmp.field("foo", "bar").field("baz", "bop");
        Assert.assertEquals(2, fdmp.getFields().get("foo").size());
        Assert.assertEquals(2, fdmp.getFields().get("baz").size());
        Assert.assertEquals(2, fdmp.getFields("foo").size());
        Assert.assertEquals(2, fdmp.getFields("baz").size());
    }
}
