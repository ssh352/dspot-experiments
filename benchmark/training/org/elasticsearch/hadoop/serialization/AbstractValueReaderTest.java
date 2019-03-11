/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization;


import FieldType.BINARY;
import org.elasticsearch.hadoop.serialization.builder.ValueReader;
import org.junit.Test;


public abstract class AbstractValueReaderTest {
    public ValueReader vr;

    @Test
    public void testNull() {
        checkNull(typeFromJson("null"));
    }

    @Test
    public void testEmptyString() {
        checkEmptyString(typeFromJson(""));
    }

    @Test
    public void testString() {
        checkString(typeFromJson("\"someText\""));
    }

    @Test
    public void testInteger() {
        checkInteger(typeFromJson(("" + (Integer.MAX_VALUE))));
    }

    @Test
    public void testLong() {
        checkLong(typeFromJson(("" + (Long.MAX_VALUE))));
    }

    @Test
    public void testDouble() {
        checkDouble(typeFromJson(("" + (Double.MAX_VALUE))));
    }

    @Test
    public void testFloat() {
        checkFloat(typeFromJson(("" + (Float.MAX_VALUE))));
    }

    @Test
    public void testBoolean() {
        checkBoolean(typeFromJson("true"));
    }

    @Test
    public void testByteArray() {
        String encode = org.codehaus.jackson.Base64Variants.getDefaultVariant().encode("byte array".getBytes());
        checkByteArray(typeFromJson((("\"" + encode) + "\"")), encode);
    }

    @Test
    public void testBinary() {
        String encode = org.codehaus.jackson.Base64Variants.getDefaultVariant().encode("binary blob".getBytes());
        checkBinary(readFromJson((("\"" + encode) + "\""), BINARY), encode.getBytes());
    }
}

