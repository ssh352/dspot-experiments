/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hdfs.avro;


import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;


public class TestFixedAvroSerializer {
    // These should match FixedAvroSerializer.config in the test resources
    private static final String schemaString1 = "{\"type\":\"record\"," + (("\"name\":\"stormtest1\"," + "\"fields\":[{\"name\":\"foo1\",\"type\":\"string\"},") + "{ \"name\":\"int1\", \"type\":\"int\" }]}");

    private static final String schemaString2 = "{\"type\":\"record\"," + (("\"name\":\"stormtest2\"," + "\"fields\":[{\"name\":\"foobar1\",\"type\":\"string\"},") + "{ \"name\":\"intint1\", \"type\":\"int\" }]}");

    private static Schema schema1;

    private static Schema schema2;

    final AvroSchemaRegistry reg;

    public TestFixedAvroSerializer() throws Exception {
        reg = new FixedAvroSerializer();
    }

    @Test
    public void testSchemas() {
        testTheSchema(TestFixedAvroSerializer.schema1);
        testTheSchema(TestFixedAvroSerializer.schema2);
    }

    @Test
    public void testDifferentFPs() {
        String fp1 = reg.getFingerprint(TestFixedAvroSerializer.schema1);
        String fp2 = reg.getFingerprint(TestFixedAvroSerializer.schema2);
        Assert.assertNotEquals(fp1, fp2);
    }
}

