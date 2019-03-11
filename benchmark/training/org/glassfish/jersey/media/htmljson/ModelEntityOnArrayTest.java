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
package org.glassfish.jersey.media.htmljson;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


/**
 * Reading and writing class generated by {@link Model} as
 * arrays.
 *
 * @author Jaroslav Tulach
 */
public class ModelEntityOnArrayTest extends AbstractTypeTester {
    @Path("empty")
    public static class TestResource {
        @POST
        @Path("mybean")
        public String myBean(MyBean[] bean) {
            if ((bean.length) != 2) {
                return "ERROR, length: " + (bean.length);
            }
            if (!(bean[0].getValue().equals("Hello"))) {
                return "ERROR, [0].value = " + (bean[0].getValue());
            }
            if (!(bean[1].getValue().equals("Ahoy"))) {
                return "ERROR, [1].value = " + (bean[1].getValue());
            }
            return "PASSED";
        }

        @GET
        @Path("getbean")
        public Response getBean(@Context
        HttpHeaders headers) {
            MyBean teb = new MyBean();
            teb.setValue("hello");
            return Response.ok().type(APPLICATION_JSON_TYPE).entity(new MyBean[]{ teb }).build();
        }
    }

    public ModelEntityOnArrayTest() {
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Test
    public void myBeanAndPut() {
        WebTarget target = target("empty/mybean");
        MyBean mb = new MyBean();
        mb.setValue("Hello");
        MyBean ah = new MyBean();
        ah.setValue("Ahoy");
        MyBean[] arr = new MyBean[]{ mb, ah };
        final Response response = target.request().post(Entity.entity(arr, APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("PASSED", response.readEntity(String.class));
    }

    @Test
    public void doReadWrite() throws Exception {
        MyBean mb = new MyBean();
        mb.setValue("Hello");
        MyBean ah = new MyBean();
        ah.setValue("Ahoy");
        MyBean[] arr = new MyBean[]{ mb, ah };
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write('[');
        os.write(arr[0].toString().getBytes("UTF-8"));
        os.write(',');
        os.write(arr[1].toString().getBytes("UTF-8"));
        os.write(']');
        os.close();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        final Class c = arr.getClass();
        Object ret = new HtmlJsonProvider().readFrom(c, null, null, APPLICATION_JSON_TYPE, null, is);
        Assert.assertTrue(("It is array: " + ret), (ret instanceof MyBean[]));
        MyBean[] res = ((MyBean[]) (ret));
        Assert.assertEquals("Two items: ", 2, res.length);
        Assert.assertEquals(arr[0], res[0]);
        Assert.assertEquals(arr[1], res[1]);
    }

    @Test
    public void myBeanAndGet() {
        WebTarget target = target("empty/getbean");
        final Response response = target.request(APPLICATION_JSON).get();
        Assert.assertEquals(200, response.getStatus());
        final MyBean[] teb = response.readEntity(MyBean[].class);
        Assert.assertEquals("value", "hello", teb[0].getValue());
    }
}

