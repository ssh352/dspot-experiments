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
package org.apache.druid.java.util.common.parsers;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.parsers.AbstractFlatTextFormatParser.FlatTextFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FlatTextFormatParserTest {
    private static final FlatTextFormatParserTest.FlatTextFormatParserFactory parserFactory = new FlatTextFormatParserTest.FlatTextFormatParserFactory();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final FlatTextFormat format;

    public FlatTextFormatParserTest(FlatTextFormat format) {
        this.format = format;
    }

    @Test
    public void testValidHeader() {
        final String header = FlatTextFormatParserTest.concat(format, "time", "value1", "value2");
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, header);
        Assert.assertEquals(ImmutableList.of("time", "value1", "value2"), parser.getFieldNames());
    }

    @Test
    public void testDuplicatedColumnName() {
        final String header = FlatTextFormatParserTest.concat(format, "time", "value1", "value2", "value2");
        expectedException.expect(ParseException.class);
        expectedException.expectMessage(StringUtils.format("Unable to parse header [%s]", header));
        FlatTextFormatParserTest.parserFactory.get(format, header);
    }

    @Test
    public void testWithHeader() {
        final String header = FlatTextFormatParserTest.concat(format, "time", "value1", "value2");
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, header);
        final String body = FlatTextFormatParserTest.concat(format, "hello", "world", "foo");
        final Map<String, Object> jsonMap = parser.parseToMap(body);
        Assert.assertEquals("jsonMap", ImmutableMap.of("time", "hello", "value1", "world", "value2", "foo"), jsonMap);
    }

    @Test
    public void testWithoutHeader() {
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format);
        final String body = FlatTextFormatParserTest.concat(format, "hello", "world", "foo");
        final Map<String, Object> jsonMap = parser.parseToMap(body);
        Assert.assertEquals("jsonMap", ImmutableMap.of("column_1", "hello", "column_2", "world", "column_3", "foo"), jsonMap);
    }

    @Test
    public void testWithSkipHeaderRows() {
        final int skipHeaderRows = 2;
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, false, skipHeaderRows);
        parser.startFileFromBeginning();
        final String[] body = new String[]{ FlatTextFormatParserTest.concat(format, "header", "line", "1"), FlatTextFormatParserTest.concat(format, "header", "line", "2"), FlatTextFormatParserTest.concat(format, "hello", "world", "foo") };
        int index;
        for (index = 0; index < skipHeaderRows; index++) {
            Assert.assertNull(parser.parseToMap(body[index]));
        }
        final Map<String, Object> jsonMap = parser.parseToMap(body[index]);
        Assert.assertEquals("jsonMap", ImmutableMap.of("column_1", "hello", "column_2", "world", "column_3", "foo"), jsonMap);
    }

    @Test
    public void testWithHeaderRow() {
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, true, 0);
        parser.startFileFromBeginning();
        final String[] body = new String[]{ FlatTextFormatParserTest.concat(format, "time", "value1", "value2"), FlatTextFormatParserTest.concat(format, "hello", "world", "foo") };
        Assert.assertNull(parser.parseToMap(body[0]));
        final Map<String, Object> jsonMap = parser.parseToMap(body[1]);
        Assert.assertEquals("jsonMap", ImmutableMap.of("time", "hello", "value1", "world", "value2", "foo"), jsonMap);
    }

    @Test
    public void testWithHeaderRowOfEmptyColumns() {
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, true, 0);
        parser.startFileFromBeginning();
        final String[] body = new String[]{ FlatTextFormatParserTest.concat(format, "time", "", "value2", ""), FlatTextFormatParserTest.concat(format, "hello", "world", "foo", "bar") };
        Assert.assertNull(parser.parseToMap(body[0]));
        final Map<String, Object> jsonMap = parser.parseToMap(body[1]);
        Assert.assertEquals("jsonMap", ImmutableMap.of("time", "hello", "column_2", "world", "value2", "foo", "column_4", "bar"), jsonMap);
    }

    @Test
    public void testWithDifferentHeaderRows() {
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, true, 0);
        parser.startFileFromBeginning();
        final String[] body = new String[]{ FlatTextFormatParserTest.concat(format, "time", "value1", "value2"), FlatTextFormatParserTest.concat(format, "hello", "world", "foo") };
        Assert.assertNull(parser.parseToMap(body[0]));
        Map<String, Object> jsonMap = parser.parseToMap(body[1]);
        Assert.assertEquals("jsonMap", ImmutableMap.of("time", "hello", "value1", "world", "value2", "foo"), jsonMap);
        parser.startFileFromBeginning();
        final String[] body2 = new String[]{ FlatTextFormatParserTest.concat(format, "time", "value1", "value2", "value3"), FlatTextFormatParserTest.concat(format, "hello", "world", "foo", "bar") };
        Assert.assertNull(parser.parseToMap(body2[0]));
        jsonMap = parser.parseToMap(body2[1]);
        Assert.assertEquals("jsonMap", ImmutableMap.of("time", "hello", "value1", "world", "value2", "foo", "value3", "bar"), jsonMap);
    }

    @Test
    public void testWithoutStartFileFromBeginning() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("hasHeaderRow or maxSkipHeaderRows is not supported. Please check the indexTask supports these options.");
        final int skipHeaderRows = 2;
        final Parser<String, Object> parser = FlatTextFormatParserTest.parserFactory.get(format, false, skipHeaderRows);
        final String[] body = new String[]{ FlatTextFormatParserTest.concat(format, "header", "line", "1"), FlatTextFormatParserTest.concat(format, "header", "line", "2"), FlatTextFormatParserTest.concat(format, "hello", "world", "foo") };
        parser.parseToMap(body[0]);
    }

    private static class FlatTextFormatParserFactory {
        public Parser<String, Object> get(FlatTextFormat format) {
            return get(format, false, 0);
        }

        public Parser<String, Object> get(FlatTextFormat format, boolean hasHeaderRow, int maxSkipHeaderRows) {
            switch (format) {
                case CSV :
                    return new CSVParser(null, hasHeaderRow, maxSkipHeaderRows);
                case DELIMITED :
                    return new DelimitedParser("\t", null, hasHeaderRow, maxSkipHeaderRows);
                default :
                    throw new org.apache.druid.java.util.common.IAE("Unknown format[%s]", format);
            }
        }

        public Parser<String, Object> get(FlatTextFormat format, String header) {
            switch (format) {
                case CSV :
                    return new CSVParser(null, header);
                case DELIMITED :
                    return new DelimitedParser("\t", null, header);
                default :
                    throw new org.apache.druid.java.util.common.IAE("Unknown format[%s]", format);
            }
        }
    }
}
