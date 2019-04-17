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
package org.apache.camel.component.aws.sqs;


import SqsConstants.DELAY_HEADER;
import SqsConstants.MD5_OF_BODY;
import SqsConstants.MESSAGE_GROUP_ID_PROPERTY;
import SqsConstants.MESSAGE_ID;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SqsProducerTest {
    private static final String SAMPLE_MESSAGE_BODY = "this is a body";

    private static final String MESSAGE_MD5 = "00000000000000000000000000000000";

    private static final String MESSAGE_ID = "11111111111111111111111111111111";

    private static final String QUEUE_URL = "some://queue/url";

    private static final String SAMPLE_MESSAGE_HEADER_NAME_1 = "header_name_1";

    private static final String SAMPLE_MESSAGE_HEADER_VALUE_1 = "heder_value_1";

    private static final String SAMPLE_MESSAGE_HEADER_NAME_2 = "header_name_2";

    private static final ByteBuffer SAMPLE_MESSAGE_HEADER_VALUE_2 = ByteBuffer.wrap(new byte[10]);

    private static final String SAMPLE_MESSAGE_HEADER_NAME_3 = "header_name_3";

    private static final String SAMPLE_MESSAGE_HEADER_VALUE_3 = "heder_value_3";

    private static final String SAMPLE_MESSAGE_HEADER_NAME_4 = "CamelHeader_1";

    private static final String SAMPLE_MESSAGE_HEADER_VALUE_4 = "testValue";

    private static final String SAMPLE_EXCHANGE_ID = "ID:whatever-the-hostname-is-32818-1506943497897-1:1:8:1:75939";

    @Mock
    Exchange exchange;

    @Mock
    private SqsEndpoint sqsEndpoint;

    @Mock
    private AmazonSQSClient amazonSQSClient;

    @Mock
    private Message outMessage;

    @Mock
    private Message inMessage;

    private SqsConfiguration sqsConfiguration;

    private SqsProducer underTest;

    @Test
    public void translateAttributes() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("key1", null);
        headers.put("key2", "");
        headers.put("key3", "value3");
        Map<String, MessageAttributeValue> translateAttributes = underTest.translateAttributes(headers, exchange);
        Assert.assertThat(translateAttributes.size(), CoreMatchers.is(1));
        Assert.assertThat(translateAttributes.get("key3").getDataType(), CoreMatchers.is("String"));
        Assert.assertThat(translateAttributes.get("key3").getStringValue(), CoreMatchers.is("value3"));
    }

    @Test
    public void itSendsTheBodyFromAnExchange() throws Exception {
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_BODY, capture.getValue().getMessageBody());
    }

    @Test
    public void itSendsTheCorrectQueueUrl() throws Exception {
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.QUEUE_URL, capture.getValue().getQueueUrl());
    }

    @Test
    public void itSetsTheDelayFromTheConfigurationOnTheRequest() throws Exception {
        sqsConfiguration.setDelaySeconds(Integer.valueOf(9001));
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(9001, capture.getValue().getDelaySeconds().intValue());
    }

    @Test
    public void itSetsTheDelayFromMessageHeaderOnTheRequest() throws Exception {
        Mockito.when(inMessage.getHeader(DELAY_HEADER, Integer.class)).thenReturn(Integer.valueOf(2000));
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(2000, capture.getValue().getDelaySeconds().intValue());
    }

    @Test
    public void itSetsTheMessageIdOnTheExchangeMessage() throws Exception {
        underTest.process(exchange);
        Mockito.verify(inMessage).setHeader(SqsConstants.MESSAGE_ID, SqsProducerTest.MESSAGE_ID);
    }

    @Test
    public void itSetsTheMd5SumOnTheExchangeMessage() throws Exception {
        underTest.process(exchange);
        Mockito.verify(inMessage).setHeader(MD5_OF_BODY, SqsProducerTest.MESSAGE_MD5);
    }

    @Test
    public void isAttributeMessageStringHeaderOnTheRequest() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_1, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_1);
        Mockito.when(inMessage.getHeaders()).thenReturn(headers);
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_1, capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_1).getStringValue());
        Assert.assertNull(capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_1).getBinaryValue());
    }

    @Test
    public void isAttributeMessageByteBufferHeaderOnTheRequest() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_2, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_2);
        Mockito.when(inMessage.getHeaders()).thenReturn(headers);
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_2, capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_2).getBinaryValue());
        Assert.assertNull(capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_2).getStringValue());
    }

    @Test
    public void isAllAttributeMessagesOnTheRequest() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_1, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_1);
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_2, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_2);
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_3, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_3);
        headers.put(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_4, SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_4);
        Mockito.when(inMessage.getHeaders()).thenReturn(headers);
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_1, capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_1).getStringValue());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_2, capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_2).getBinaryValue());
        Assert.assertEquals(SqsProducerTest.SAMPLE_MESSAGE_HEADER_VALUE_3, capture.getValue().getMessageAttributes().get(SqsProducerTest.SAMPLE_MESSAGE_HEADER_NAME_3).getStringValue());
        Assert.assertEquals(3, capture.getValue().getMessageAttributes().size());
    }

    @Test
    public void itSetsMessageGroupIdUsingConstantStrategy() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("useConstant");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals("CamelSingleMessageGroup", capture.getValue().getMessageGroupId());
    }

    @Test
    public void itFailsWhenFifoQueueAndNoMessageGroupIdStrategySet() {
        try {
            sqsConfiguration.setQueueName("queueName.fifo");
            SqsProducer invalidProducer = new SqsProducer(sqsEndpoint);
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertTrue(("Bad error message: " + (e.getMessage())), e.getMessage().startsWith("messageGroupIdStrategy must be set for FIFO queues"));
        }
    }

    @Test
    public void itSetsMessageGroupIdUsingExchangeIdStrategy() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("useExchangeId");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_EXCHANGE_ID, capture.getValue().getMessageGroupId());
    }

    @Test
    public void itSetsMessageGroupIdUsingHeaderValueStrategy() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("usePropertyValue");
        Mockito.when(exchange.getProperty(MESSAGE_GROUP_ID_PROPERTY, String.class)).thenReturn("my-group-id");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals("my-group-id", capture.getValue().getMessageGroupId());
    }

    @Test
    public void itSetsMessageDedpulicationIdUsingExchangeIdStrategy() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("useConstant");
        sqsConfiguration.setMessageDeduplicationIdStrategy("useExchangeId");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_EXCHANGE_ID, capture.getValue().getMessageDeduplicationId());
    }

    @Test
    public void itSetsMessageDedpulicationIdUsingExchangeIdStrategyAsDefault() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("useConstant");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertEquals(SqsProducerTest.SAMPLE_EXCHANGE_ID, capture.getValue().getMessageDeduplicationId());
    }

    @Test
    public void itDoesNotSetMessageDedpulicationIdUsingContentBasedDeduplicationStrategy() throws Exception {
        sqsConfiguration.setQueueName("queueName.fifo");
        sqsConfiguration.setMessageGroupIdStrategy("useConstant");
        sqsConfiguration.setMessageDeduplicationIdStrategy("useContentBasedDeduplication");
        underTest.process(exchange);
        ArgumentCaptor<SendMessageRequest> capture = ArgumentCaptor.forClass(SendMessageRequest.class);
        Mockito.verify(amazonSQSClient).sendMessage(capture.capture());
        Assert.assertNull(capture.getValue().getMessageDeduplicationId());
    }
}
