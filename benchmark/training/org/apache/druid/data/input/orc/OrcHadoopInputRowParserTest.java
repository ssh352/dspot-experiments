/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.data.input.orc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.data.input.impl.StringDimensionSchema;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.io.orc.OrcStruct;
import org.apache.hadoop.hive.serde2.objectinspector.SettableStructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;


public class OrcHadoopInputRowParserTest {
    Injector injector;

    ObjectMapper mapper = new DefaultObjectMapper();

    @Test
    public void testSerde() throws IOException {
        String parserString = "{\n" + ((((((((((((((((("        \"type\": \"orc\",\n" + "        \"parseSpec\": {\n") + "          \"format\": \"timeAndDims\",\n") + "          \"timestampSpec\": {\n") + "            \"column\": \"timestamp\",\n") + "            \"format\": \"auto\"\n") + "          },\n") + "          \"dimensionsSpec\": {\n") + "            \"dimensions\": [\n") + "              \"col1\",\n") + "              \"col2\"\n") + "            ],\n") + "            \"dimensionExclusions\": [],\n") + "            \"spatialDimensions\": []\n") + "          }\n") + "        },\n") + "        \"typeString\": \"struct<timestamp:string,col1:string,col2:array<string>,val1:float>\"\n") + "      }");
        InputRowParser parser = mapper.readValue(parserString, InputRowParser.class);
        InputRowParser expected = new OrcHadoopInputRowParser(new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(ImmutableList.of(new StringDimensionSchema("col1"), new StringDimensionSchema("col2")), null, null)), "struct<timestamp:string,col1:string,col2:array<string>,val1:float>", null);
        Assert.assertEquals(expected, parser);
    }

    @Test
    public void testTypeFromParseSpec() {
        ParseSpec parseSpec = new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(ImmutableList.of(new StringDimensionSchema("col1"), new StringDimensionSchema("col2")), null, null));
        String typeString = OrcHadoopInputRowParser.typeStringFromParseSpec(parseSpec);
        String expected = "struct<timestamp:string,col1:string,col2:string>";
        Assert.assertEquals(expected, typeString);
    }

    @Test
    public void testParse() {
        final String typeString = "struct<timestamp:string,col1:string,col2:array<string>,col3:float,col4:bigint,col5:decimal,col6:array<string>,col7:map<string,string>>";
        final OrcHadoopInputRowParser parser = new OrcHadoopInputRowParser(new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(null, null, null)), typeString, "<PARENT>-<CHILD>");
        final SettableStructObjectInspector oi = ((SettableStructObjectInspector) (OrcStruct.createObjectInspector(TypeInfoUtils.getTypeInfoFromTypeString(typeString))));
        final OrcStruct struct = ((OrcStruct) (oi.create()));
        struct.setNumFields(8);
        oi.setStructFieldData(struct, oi.getStructFieldRef("timestamp"), new Text("2000-01-01"));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col1"), new Text("foo"));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col2"), ImmutableList.of(new Text("foo"), new Text("bar")));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col3"), new FloatWritable(1.5F));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col4"), new LongWritable(2));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col5"), new org.apache.hadoop.hive.serde2.io.HiveDecimalWritable(HiveDecimal.create(BigDecimal.valueOf(3.5))));
        oi.setStructFieldData(struct, oi.getStructFieldRef("col6"), null);
        oi.setStructFieldData(struct, oi.getStructFieldRef("col7"), ImmutableMap.of(new Text("subcol7"), new Text("subval7")));
        final InputRow row = parser.parseBatch(struct).get(0);
        Assert.assertEquals("timestamp", DateTimes.of("2000-01-01"), row.getTimestamp());
        Assert.assertEquals("col1", "foo", row.getRaw("col1"));
        Assert.assertEquals("col2", ImmutableList.of("foo", "bar"), row.getRaw("col2"));
        Assert.assertEquals("col3", 1.5F, row.getRaw("col3"));
        Assert.assertEquals("col4", 2L, row.getRaw("col4"));
        Assert.assertEquals("col5", 3.5, row.getRaw("col5"));
        Assert.assertNull("col6", row.getRaw("col6"));
        Assert.assertEquals("col7-subcol7", "subval7", row.getRaw("col7-subcol7"));
    }
}

