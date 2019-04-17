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
package org.apache.camel.component.kafka;


import KafkaConstants.KAFKA_RECORDMETA;
import KafkaConstants.PARTITION_KEY;
import KafkaConstants.TOPIC;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.serde.DefaultKafkaHeaderSerializer;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.junit.Test;


public class KafkaProducerFullTest extends BaseEmbeddedKafkaTest {
    private static final String TOPIC_STRINGS = "test";

    private static final String TOPIC_INTERCEPTED = "test";

    private static final String TOPIC_STRINGS_IN_HEADER = "testHeader";

    private static final String TOPIC_BYTES = "testBytes";

    private static final String TOPIC_BYTES_IN_HEADER = "testBytesHeader";

    private static final String GROUP_BYTES = "groupStrings";

    private static final String TOPIC_PROPAGATED_HEADERS = "testPropagatedHeaders";

    private static KafkaConsumer<String, String> stringsConsumerConn;

    private static KafkaConsumer<byte[], byte[]> bytesConsumerConn;

    @EndpointInject(uri = ("kafka:" + (KafkaProducerFullTest.TOPIC_STRINGS)) + "?requestRequiredAcks=-1")
    private Endpoint toStrings;

    @EndpointInject(uri = ("kafka:" + (KafkaProducerFullTest.TOPIC_STRINGS)) + "?requestRequiredAcks=-1&partitionKey=1")
    private Endpoint toStrings2;

    @EndpointInject(uri = (("kafka:" + (KafkaProducerFullTest.TOPIC_INTERCEPTED)) + "?requestRequiredAcks=-1") + "&interceptorClasses=org.apache.camel.component.kafka.MockProducerInterceptor")
    private Endpoint toStringsWithInterceptor;

    @EndpointInject(uri = "mock:kafkaAck")
    private MockEndpoint mockEndpoint;

    @EndpointInject(uri = ((("kafka:" + (KafkaProducerFullTest.TOPIC_BYTES)) + "?requestRequiredAcks=-1") + "&serializerClass=org.apache.kafka.common.serialization.ByteArraySerializer&") + "keySerializerClass=org.apache.kafka.common.serialization.ByteArraySerializer")
    private Endpoint toBytes;

    @EndpointInject(uri = ("kafka:" + (KafkaProducerFullTest.TOPIC_PROPAGATED_HEADERS)) + "?requestRequiredAcks=-1")
    private Endpoint toPropagatedHeaders;

    @Produce(uri = "direct:startStrings")
    private ProducerTemplate stringsTemplate;

    @Produce(uri = "direct:startStrings2")
    private ProducerTemplate stringsTemplate2;

    @Produce(uri = "direct:startBytes")
    private ProducerTemplate bytesTemplate;

    @Produce(uri = "direct:startTraced")
    private ProducerTemplate interceptedTemplate;

    @Produce(uri = "direct:propagatedHeaders")
    private ProducerTemplate propagatedHeadersTemplate;

    @Test
    public void producedStringMessageIsReceivedByKafka() throws IOException, InterruptedException {
        int messageInTopic = 10;
        int messageInOtherTopic = 5;
        CountDownLatch messagesLatch = new CountDownLatch((messageInTopic + messageInOtherTopic));
        sendMessagesInRoute(messageInTopic, stringsTemplate, "IT test message", PARTITION_KEY, "1");
        sendMessagesInRoute(messageInOtherTopic, stringsTemplate, "IT test message in other topic", PARTITION_KEY, "1", TOPIC, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER);
        createKafkaMessageConsumer(KafkaProducerFullTest.stringsConsumerConn, KafkaProducerFullTest.TOPIC_STRINGS, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(200, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        List<Exchange> exchangeList = mockEndpoint.getExchanges();
        assertEquals("Fifteen Exchanges are expected", exchangeList.size(), 15);
        for (Exchange exchange : exchangeList) {
            @SuppressWarnings("unchecked")
            List<RecordMetadata> recordMetaData1 = ((List<RecordMetadata>) (exchange.getIn().getHeader(KAFKA_RECORDMETA)));
            assertEquals("One RecordMetadata is expected.", recordMetaData1.size(), 1);
            assertTrue("Offset is positive", ((recordMetaData1.get(0).offset()) >= 0));
            assertTrue("Topic Name start with 'test'", recordMetaData1.get(0).topic().startsWith("test"));
        }
    }

    @Test
    public void producedString2MessageIsReceivedByKafka() throws IOException, InterruptedException {
        int messageInTopic = 10;
        int messageInOtherTopic = 5;
        CountDownLatch messagesLatch = new CountDownLatch((messageInTopic + messageInOtherTopic));
        sendMessagesInRoute(messageInTopic, stringsTemplate2, "IT test message", ((String[]) (null)));
        sendMessagesInRoute(messageInOtherTopic, stringsTemplate2, "IT test message in other topic", PARTITION_KEY, "1", TOPIC, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER);
        createKafkaMessageConsumer(KafkaProducerFullTest.stringsConsumerConn, KafkaProducerFullTest.TOPIC_STRINGS, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(200, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        List<Exchange> exchangeList = mockEndpoint.getExchanges();
        assertEquals("Fifteen Exchanges are expected", exchangeList.size(), 15);
        for (Exchange exchange : exchangeList) {
            @SuppressWarnings("unchecked")
            List<RecordMetadata> recordMetaData1 = ((List<RecordMetadata>) (exchange.getIn().getHeader(KAFKA_RECORDMETA)));
            assertEquals("One RecordMetadata is expected.", recordMetaData1.size(), 1);
            assertTrue("Offset is positive", ((recordMetaData1.get(0).offset()) >= 0));
            assertTrue("Topic Name start with 'test'", recordMetaData1.get(0).topic().startsWith("test"));
        }
    }

    @Test
    public void producedStringMessageIsIntercepted() throws IOException, InterruptedException {
        int messageInTopic = 10;
        int messageInOtherTopic = 5;
        CountDownLatch messagesLatch = new CountDownLatch((messageInTopic + messageInOtherTopic));
        sendMessagesInRoute(messageInTopic, interceptedTemplate, "IT test message", PARTITION_KEY, "1");
        sendMessagesInRoute(messageInOtherTopic, interceptedTemplate, "IT test message in other topic", PARTITION_KEY, "1", TOPIC, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER);
        createKafkaMessageConsumer(KafkaProducerFullTest.stringsConsumerConn, KafkaProducerFullTest.TOPIC_INTERCEPTED, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(200, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        assertEquals((messageInTopic + messageInOtherTopic), MockProducerInterceptor.recordsCaptured.size());
    }

    @Test
    public void producedStringCollectionMessageIsReceivedByKafka() throws IOException, InterruptedException {
        int messageInTopic = 10;
        int messageInOtherTopic = 5;
        CountDownLatch messagesLatch = new CountDownLatch((messageInTopic + messageInOtherTopic));
        List<String> msgs = new ArrayList<>();
        for (int x = 0; x < messageInTopic; x++) {
            msgs.add(("Message " + x));
        }
        sendMessagesInRoute(1, stringsTemplate, msgs, PARTITION_KEY, "1");
        msgs = new ArrayList<>();
        for (int x = 0; x < messageInOtherTopic; x++) {
            msgs.add(("Other Message " + x));
        }
        sendMessagesInRoute(1, stringsTemplate, msgs, PARTITION_KEY, "1", TOPIC, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER);
        createKafkaMessageConsumer(KafkaProducerFullTest.stringsConsumerConn, KafkaProducerFullTest.TOPIC_STRINGS, KafkaProducerFullTest.TOPIC_STRINGS_IN_HEADER, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(200, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        List<Exchange> exchangeList = mockEndpoint.getExchanges();
        assertEquals("Two Exchanges are expected", exchangeList.size(), 2);
        Exchange e1 = exchangeList.get(0);
        @SuppressWarnings("unchecked")
        List<RecordMetadata> recordMetaData1 = ((List<RecordMetadata>) (e1.getIn().getHeader(KAFKA_RECORDMETA)));
        assertEquals("Ten RecordMetadata is expected.", recordMetaData1.size(), 10);
        for (RecordMetadata recordMeta : recordMetaData1) {
            assertTrue("Offset is positive", ((recordMeta.offset()) >= 0));
            assertTrue("Topic Name start with 'test'", recordMeta.topic().startsWith("test"));
        }
        Exchange e2 = exchangeList.get(1);
        @SuppressWarnings("unchecked")
        List<RecordMetadata> recordMetaData2 = ((List<RecordMetadata>) (e2.getIn().getHeader(KAFKA_RECORDMETA)));
        assertEquals("Five RecordMetadata is expected.", recordMetaData2.size(), 5);
        for (RecordMetadata recordMeta : recordMetaData2) {
            assertTrue("Offset is positive", ((recordMeta.offset()) >= 0));
            assertTrue("Topic Name start with 'test'", recordMeta.topic().startsWith("test"));
        }
    }

    @Test
    public void producedBytesMessageIsReceivedByKafka() throws IOException, InterruptedException {
        int messageInTopic = 10;
        int messageInOtherTopic = 5;
        CountDownLatch messagesLatch = new CountDownLatch((messageInTopic + messageInOtherTopic));
        Map<String, Object> inTopicHeaders = new HashMap<>();
        inTopicHeaders.put(PARTITION_KEY, "1".getBytes());
        sendMessagesInRoute(messageInTopic, bytesTemplate, "IT test message".getBytes(), inTopicHeaders);
        Map<String, Object> otherTopicHeaders = new HashMap<>();
        otherTopicHeaders.put(PARTITION_KEY, "1".getBytes());
        otherTopicHeaders.put(TOPIC, KafkaProducerFullTest.TOPIC_BYTES_IN_HEADER);
        sendMessagesInRoute(messageInOtherTopic, bytesTemplate, "IT test message in other topic".getBytes(), otherTopicHeaders);
        createKafkaBytesMessageConsumer(KafkaProducerFullTest.bytesConsumerConn, KafkaProducerFullTest.TOPIC_BYTES, KafkaProducerFullTest.TOPIC_BYTES_IN_HEADER, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(200, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        List<Exchange> exchangeList = mockEndpoint.getExchanges();
        assertEquals("Fifteen Exchanges are expected", exchangeList.size(), 15);
        for (Exchange exchange : exchangeList) {
            @SuppressWarnings("unchecked")
            List<RecordMetadata> recordMetaData1 = ((List<RecordMetadata>) (exchange.getIn().getHeader(KAFKA_RECORDMETA)));
            assertEquals("One RecordMetadata is expected.", recordMetaData1.size(), 1);
            assertTrue("Offset is positive", ((recordMetaData1.get(0).offset()) >= 0));
            assertTrue("Topic Name start with 'test'", recordMetaData1.get(0).topic().startsWith("test"));
        }
    }

    @Test
    public void propagatedHeaderIsReceivedByKafka() throws Exception {
        String propagatedStringHeaderKey = "PROPAGATED_STRING_HEADER";
        String propagatedStringHeaderValue = "propagated string header value";
        String propagatedIntegerHeaderKey = "PROPAGATED_INTEGER_HEADER";
        Integer propagatedIntegerHeaderValue = 54545;
        String propagatedLongHeaderKey = "PROPAGATED_LONG_HEADER";
        Long propagatedLongHeaderValue = 5454545454545L;
        String propagatedDoubleHeaderKey = "PROPAGATED_DOUBLE_HEADER";
        Double propagatedDoubleHeaderValue = 43434.545;
        String propagatedBytesHeaderKey = "PROPAGATED_BYTES_HEADER";
        byte[] propagatedBytesHeaderValue = new byte[]{ 121, 34, 34, 54, 5, 3, 54, -34 };
        String propagatedBooleanHeaderKey = "PROPAGATED_BOOLEAN_HEADER";
        Boolean propagatedBooleanHeaderValue = Boolean.TRUE;
        Map<String, Object> camelHeaders = new HashMap<>();
        camelHeaders.put(propagatedStringHeaderKey, propagatedStringHeaderValue);
        camelHeaders.put(propagatedIntegerHeaderKey, propagatedIntegerHeaderValue);
        camelHeaders.put(propagatedLongHeaderKey, propagatedLongHeaderValue);
        camelHeaders.put(propagatedDoubleHeaderKey, propagatedDoubleHeaderValue);
        camelHeaders.put(propagatedBytesHeaderKey, propagatedBytesHeaderValue);
        camelHeaders.put(propagatedBooleanHeaderKey, propagatedBooleanHeaderValue);
        camelHeaders.put("CustomObjectHeader", new Object());
        camelHeaders.put("CustomNullObjectHeader", null);
        camelHeaders.put("CamelFilteredHeader", "CamelFilteredHeader value");
        CountDownLatch messagesLatch = new CountDownLatch(1);
        propagatedHeadersTemplate.sendBodyAndHeaders("Some test message", camelHeaders);
        List<ConsumerRecord<String, String>> records = pollForRecords(KafkaProducerFullTest.stringsConsumerConn, KafkaProducerFullTest.TOPIC_PROPAGATED_HEADERS, messagesLatch);
        boolean allMessagesReceived = messagesLatch.await(10000, TimeUnit.MILLISECONDS);
        assertTrue(("Not all messages were published to the kafka topics. Not received: " + (messagesLatch.getCount())), allMessagesReceived);
        ConsumerRecord<String, String> record = records.get(0);
        Headers headers = record.headers();
        assertNotNull("Kafka Headers should not be null.", headers);
        // we have 6 headers
        assertEquals("6 propagated header is expected.", 6, headers.toArray().length);
        assertEquals("Propagated string value received", propagatedStringHeaderValue, new String(getHeaderValue(propagatedStringHeaderKey, headers)));
        assertEquals("Propagated integer value received", propagatedIntegerHeaderValue, new Integer(ByteBuffer.wrap(getHeaderValue(propagatedIntegerHeaderKey, headers)).getInt()));
        assertEquals("Propagated long value received", propagatedLongHeaderValue, new Long(ByteBuffer.wrap(getHeaderValue(propagatedLongHeaderKey, headers)).getLong()));
        assertEquals("Propagated double value received", propagatedDoubleHeaderValue, new Double(ByteBuffer.wrap(getHeaderValue(propagatedDoubleHeaderKey, headers)).getDouble()));
        assertArrayEquals("Propagated byte array value received", propagatedBytesHeaderValue, getHeaderValue(propagatedBytesHeaderKey, headers));
        assertEquals("Propagated boolean value received", propagatedBooleanHeaderValue, Boolean.valueOf(new String(getHeaderValue(propagatedBooleanHeaderKey, headers))));
    }

    @Test
    public void headerFilterStrategyCouldBeOverridden() {
        KafkaEndpoint kafkaEndpoint = context.getEndpoint("kafka:TOPIC_PROPAGATED_HEADERS?headerFilterStrategy=#myStrategy", KafkaEndpoint.class);
        assertIsInstanceOf(KafkaProducerFullTest.MyHeaderFilterStrategy.class, kafkaEndpoint.getConfiguration().getHeaderFilterStrategy());
    }

    @Test
    public void headerSerializerCouldBeOverridden() {
        KafkaEndpoint kafkaEndpoint = context.getEndpoint("kafka:TOPIC_PROPAGATED_HEADERS?kafkaHeaderSerializer=#myHeaderSerializer", KafkaEndpoint.class);
        assertIsInstanceOf(KafkaProducerFullTest.MyKafkaHeadersSerializer.class, kafkaEndpoint.getConfiguration().getKafkaHeaderSerializer());
    }

    private static class MyHeaderFilterStrategy extends DefaultHeaderFilterStrategy {}

    private static class MyKafkaHeadersSerializer extends DefaultKafkaHeaderSerializer {}
}
