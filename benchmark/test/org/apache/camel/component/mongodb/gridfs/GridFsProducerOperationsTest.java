/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mongodb.gridfs;


import Exchange.FILE_NAME;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class GridFsProducerOperationsTest extends AbstractMongoDbTest {
    @Test
    public void testOperations() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        String fn = "filename.for.db.txt";
        assertEquals(0, gridfs.find(fn).size());
        headers.put(FILE_NAME, fn);
        String data = "This is some stuff to go into the db";
        template.requestBodyAndHeaders("direct:create", data, headers);
        assertEquals(1, gridfs.find(fn).size());
        assertEquals(1, template.requestBodyAndHeaders("direct:count", null, headers));
        InputStream ins = template.requestBodyAndHeaders("direct:findOne", null, headers, InputStream.class);
        assertNotNull(ins);
        byte[] b = new byte[2048];
        int i = ins.read(b);
        assertEquals(data, new String(b, 0, i, "utf-8"));
        headers.put(FILE_NAME, ("2-" + fn));
        template.requestBodyAndHeaders("direct:create", (data + "data2"), headers);
        assertEquals(1, template.requestBodyAndHeaders("direct:count", null, headers));
        assertEquals(2, template.requestBody("direct:count", null, Integer.class).intValue());
        String s = template.requestBody("direct:listAll", null, String.class);
        assertTrue(s.contains(("2-" + fn)));
        template.requestBodyAndHeaders("direct:remove", null, headers);
        assertEquals(1, template.requestBody("direct:count", null, Integer.class).intValue());
        s = template.requestBody("direct:listAll", null, String.class);
        assertFalse(s.contains(("2-" + fn)));
    }
}
