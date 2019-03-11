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
package com.pinterest.secor.parser;


import TimestampedMessages.UnitTestTimestamp1;
import TimestampedMessages.UnitTestTimestamp2;
import TimestampedMessages.UnitTestTimestamp2.Internal;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.protobuf.TimestampedMessages;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by pgautam on 10/9/16.
 */
@RunWith(PowerMockRunner.class)
public class ProtobufTimestampParserTest extends TestCase {
    private SecorConfig mConfig;

    private long timestamp;

    @Test
    public void testExtractTimestampMillisFromKafkaTimestamp() throws Exception {
        Mockito.when(mConfig.getBoolean("kafka.useTimestamp", false)).thenReturn(true);
        ProtobufMessageParser parser = new ProtobufMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1405970352L)));
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(buildMessage(1405970352123L)));
    }

    @Test
    public void testExtractTimestampMillis() throws Exception {
        ProtobufMessageParser parser = new ProtobufMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1405970352L)));
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(buildMessage(1405970352123L)));
    }

    @Test
    public void testExtractPathTimestampMillis() throws Exception {
        Map<String, String> classPerTopic = new HashMap<String, String>();
        System.out.println(UnitTestTimestamp1.class.getName());
        classPerTopic.put("test", UnitTestTimestamp1.class.getName());
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestamp");
        Mockito.when(mConfig.getProtobufMessageClassPerTopic()).thenReturn(classPerTopic);
        ProtobufMessageParser parser = new ProtobufMessageParser(mConfig);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(1405970352L).setNanos(0).build();
        TimestampedMessages.UnitTestTimestamp1 message = UnitTestTimestamp1.newBuilder().setTimestamp(timestamp).build();
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(new com.pinterest.secor.message.Message("test", 0, 0, null, message.toByteArray(), timestamp.getSeconds())));
        Timestamp timestampWithNano = Timestamp.newBuilder().setSeconds(1405970352L).setNanos(123000000).build();
        message = UnitTestTimestamp1.newBuilder().setTimestamp(timestampWithNano).build();
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(new com.pinterest.secor.message.Message("test", 0, 0, null, message.toByteArray(), timestamp.getSeconds())));
    }

    @Test
    public void testExtractNestedTimestampMillis() throws Exception {
        Map<String, String> classPerTopic = new HashMap<String, String>();
        classPerTopic.put("*", UnitTestTimestamp2.class.getName());
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("internal.timestamp");
        Mockito.when(mConfig.getProtobufMessageClassPerTopic()).thenReturn(classPerTopic);
        ProtobufMessageParser parser = new ProtobufMessageParser(mConfig);
        Timestamp timestamp = Timestamps.fromMillis(1405970352000L);
        TimestampedMessages.UnitTestTimestamp2 message = UnitTestTimestamp2.newBuilder().setInternal(Internal.newBuilder().setTimestamp(timestamp).build()).build();
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(new com.pinterest.secor.message.Message("test", 0, 0, null, message.toByteArray(), timestamp.getSeconds())));
        timestamp = Timestamps.fromMillis(1405970352123L);
        message = UnitTestTimestamp2.newBuilder().setInternal(Internal.newBuilder().setTimestamp(timestamp).build()).build();
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(new com.pinterest.secor.message.Message("test", 0, 0, null, message.toByteArray(), timestamp.getSeconds())));
    }
}

