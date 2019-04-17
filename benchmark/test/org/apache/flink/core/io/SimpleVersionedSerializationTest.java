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
package org.apache.flink.core.io;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link SimpleVersionedSerialization} class.
 */
public class SimpleVersionedSerializationTest {
    @Test
    public void testSerializationRoundTrip() throws IOException {
        final SimpleVersionedSerializer<String> utfEncoder = new SimpleVersionedSerializer<String>() {
            private static final int VERSION = (Integer.MAX_VALUE) / 2;// version should occupy many bytes


            @Override
            public int getVersion() {
                return VERSION;
            }

            @Override
            public byte[] serialize(String str) throws IOException {
                return str.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String deserialize(int version, byte[] serialized) throws IOException {
                Assert.assertEquals(VERSION, version);
                return new String(serialized, StandardCharsets.UTF_8);
            }
        };
        final String testString = "dugfakgs";
        final DataOutputSerializer out = new DataOutputSerializer(32);
        SimpleVersionedSerialization.writeVersionAndSerialize(utfEncoder, testString, out);
        final byte[] outBytes = out.getCopyOfBuffer();
        final byte[] bytes = SimpleVersionedSerialization.writeVersionAndSerialize(utfEncoder, testString);
        Assert.assertArrayEquals(bytes, outBytes);
        final DataInputDeserializer in = new DataInputDeserializer(bytes);
        final String deserialized = SimpleVersionedSerialization.readVersionAndDeSerialize(utfEncoder, in);
        final String deserializedFromBytes = SimpleVersionedSerialization.readVersionAndDeSerialize(utfEncoder, outBytes);
        Assert.assertEquals(testString, deserialized);
        Assert.assertEquals(testString, deserializedFromBytes);
    }

    @Test
    public void testSerializeEmpty() throws IOException {
        final String testString = "beeeep!";
        SimpleVersionedSerializer<String> emptySerializer = new SimpleVersionedSerializer<String>() {
            @Override
            public int getVersion() {
                return 42;
            }

            @Override
            public byte[] serialize(String obj) throws IOException {
                return new byte[0];
            }

            @Override
            public String deserialize(int version, byte[] serialized) throws IOException {
                Assert.assertEquals(42, version);
                Assert.assertEquals(0, serialized.length);
                return testString;
            }
        };
        final DataOutputSerializer out = new DataOutputSerializer(32);
        SimpleVersionedSerialization.writeVersionAndSerialize(emptySerializer, "abc", out);
        final byte[] outBytes = out.getCopyOfBuffer();
        final byte[] bytes = SimpleVersionedSerialization.writeVersionAndSerialize(emptySerializer, "abc");
        Assert.assertArrayEquals(bytes, outBytes);
        final DataInputDeserializer in = new DataInputDeserializer(bytes);
        final String deserialized = SimpleVersionedSerialization.readVersionAndDeSerialize(emptySerializer, in);
        final String deserializedFromBytes = SimpleVersionedSerialization.readVersionAndDeSerialize(emptySerializer, outBytes);
        Assert.assertEquals(testString, deserialized);
        Assert.assertEquals(testString, deserializedFromBytes);
    }
}
