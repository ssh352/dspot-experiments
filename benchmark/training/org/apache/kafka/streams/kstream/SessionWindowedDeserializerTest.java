/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.Test;


public class SessionWindowedDeserializerTest {
    private final SessionWindowedDeserializer<?> sessionWindowedDeserializer = new SessionWindowedDeserializer();

    private final Map<String, String> props = new HashMap<>();

    @Test
    public void testWindowedKeyDeserializerNoArgConstructors() {
        sessionWindowedDeserializer.configure(props, true);
        final Deserializer<?> inner = sessionWindowedDeserializer.innerDeserializer();
        Assert.assertNotNull("Inner deserializer should be not null", inner);
        Assert.assertTrue("Inner deserializer type should be StringDeserializer", (inner instanceof StringDeserializer));
    }

    @Test
    public void testWindowedValueDeserializerNoArgConstructors() {
        sessionWindowedDeserializer.configure(props, false);
        final Deserializer<?> inner = sessionWindowedDeserializer.innerDeserializer();
        Assert.assertNotNull("Inner deserializer should be not null", inner);
        Assert.assertTrue("Inner deserializer type should be ByteArrayDeserializer", (inner instanceof ByteArrayDeserializer));
    }
}

