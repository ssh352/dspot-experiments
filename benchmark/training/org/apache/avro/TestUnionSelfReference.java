/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro;


import JsonProperties.NULL_VALUE;
import Type.UNION;
import org.apache.avro.Schema.Field;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestUnionSelfReference {
    /**
     * The logger for TestUnionSelfReference
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUnionSelfReference.class);

    private static final String SIMPLE_BINARY_TREE = "{" + ((((((((((((((((((((((((("    \"namespace\": \"tree\"," + "    \"type\": \"record\",") + "    \"name\": \"Node\",") + "    \"fields\": [") + "      {") + "        \"name\": \"left\",") + "        \"type\": [") + "          \"null\",") + "          {") + "            \"type\": \"Node\"") + "          }") + "        ],") + "        \"default\": null") + "      },") + "      {") + "        \"name\": \"right\",") + "        \"type\": [") + "          \"null\",") + "          {") + "            \"type\": \"Node\"") + "          }") + "        ],") + "        \"default\": null") + "      }") + "    ]") + "  }");

    private static final String THREE_TYPE_UNION = "{" + ((((((((((((((((((((((((((("    \"namespace\": \"tree\"," + "    \"type\": \"record\",") + "    \"name\": \"Node\",") + "    \"fields\": [") + "      {") + "        \"name\": \"left\",") + "        \"type\": [") + "          \"null\",") + "          \"string\",") + "          {") + "            \"type\": \"Node\"") + "          }") + "        ],") + "        \"default\": null") + "      },") + "      {") + "        \"name\": \"right\",") + "        \"type\": [") + "          \"null\",") + "          \"string\",") + "          {") + "            \"type\": \"Node\"") + "          }") + "        ],") + "        \"default\": null") + "      }") + "    ]") + "  }");

    @Test
    public void testSelfReferenceInUnion() {
        Schema schema = new Schema.Parser().parse(TestUnionSelfReference.SIMPLE_BINARY_TREE);
        Field leftField = schema.getField("left");
        Assert.assertEquals(NULL_VALUE, leftField.defaultVal());
        final Schema leftFieldSchema = leftField.schema();
        Assert.assertEquals(UNION, leftFieldSchema.getType());
        Assert.assertEquals("null", leftFieldSchema.getTypes().get(0).getName());
        Assert.assertEquals("Node", leftFieldSchema.getTypes().get(1).getName());
        Field rightField = schema.getField("right");
        Assert.assertEquals(NULL_VALUE, rightField.defaultVal());
        final Schema rightFieldSchema = rightField.schema();
        Assert.assertEquals(UNION, rightFieldSchema.getType());
        Assert.assertEquals("null", rightFieldSchema.getTypes().get(0).getName());
        Assert.assertEquals("Node", rightFieldSchema.getTypes().get(1).getName());
    }

    @Test
    public void testSelfReferenceInThreeUnion() {
        Schema schema = new Schema.Parser().parse(TestUnionSelfReference.THREE_TYPE_UNION);
        Field leftField = schema.getField("left");
        Assert.assertEquals(NULL_VALUE, leftField.defaultVal());
        final Schema leftFieldSchema = leftField.schema();
        Assert.assertEquals(UNION, leftFieldSchema.getType());
        Assert.assertEquals("null", leftFieldSchema.getTypes().get(0).getName());
        Assert.assertEquals("string", leftFieldSchema.getTypes().get(1).getName());
        Assert.assertEquals("Node", leftFieldSchema.getTypes().get(2).getName());
        Field rightField = schema.getField("right");
        Assert.assertEquals(NULL_VALUE, rightField.defaultVal());
        final Schema rightFieldSchema = rightField.schema();
        Assert.assertEquals(UNION, rightFieldSchema.getType());
        Assert.assertEquals("null", rightFieldSchema.getTypes().get(0).getName());
        Assert.assertEquals("string", rightFieldSchema.getTypes().get(1).getName());
        Assert.assertEquals("Node", rightFieldSchema.getTypes().get(2).getName());
    }
}

