/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdk7.rabbitmq.spring;


import AMQP.BasicProperties;
import ServiceType.ASYNC;
import ServiceType.INTERNAL_METHOD;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedTrace;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.plugin.jdk7.rabbitmq.util.RabbitMQTestConstants;
import com.navercorp.pinpoint.plugin.jdk7.rabbitmq.util.TestBroker;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import com.navercorp.test.pinpoint.plugin.rabbitmq.PropagationMarker;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.impl.AMQCommand;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;


/**
 *
 *
 * @author HyunGil Jeong
 */
// 1.4.5, 1.4.6, 1.6.4.RELEASE has dependency issues
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@PinpointConfig("rabbitmq/client/pinpoint-rabbitmq.config")
@Dependency({ "org.springframework.amqp:spring-rabbit:[1.4.2.RELEASE,1.4.5.RELEASE),[1.5.0.RELEASE,1.6.4.RELEASE),[1.6.5.RELEASE,1.7.0.RELEASE)", "com.fasterxml.jackson.core:jackson-core:2.8.11", "org.apache.qpid:qpid-broker:6.1.1" })
public class SpringAmqpRabbit_1_4_2_to_1_7_0_IT {
    private static final TestBroker BROKER = new TestBroker();

    private static final TestApplicationContext CONTEXT = new TestApplicationContext();

    private final SpringAmqpRabbitTestRunner testRunner = new SpringAmqpRabbitTestRunner(SpringAmqpRabbit_1_4_2_to_1_7_0_IT.CONTEXT);

    @Test
    public void testPush() throws Exception {
        final String remoteAddress = testRunner.getRemoteAddress();
        Class<?> rabbitTemplateClass = Class.forName("org.springframework.amqp.rabbit.core.RabbitTemplate");
        Method rabbitTemplateConvertAndSend = rabbitTemplateClass.getDeclaredMethod("convertAndSend", String.class, String.class, Object.class);
        ExpectedTrace rabbitTemplateConvertAndSendTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, rabbitTemplateConvertAndSend);// method

        // automatic recovery deliberately disabled as Spring has it's own recovery mechanism
        Class<?> channelNClass = Class.forName("com.rabbitmq.client.impl.ChannelN");
        Method channelNBasicPublish = channelNClass.getDeclaredMethod("basicPublish", String.class, String.class, boolean.class, boolean.class, BasicProperties.class, byte[].class);
        ExpectedTrace channelNBasicPublishTrace = // serviceType
        // method
        // rpc
        // endPoint
        // destinationId
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT, channelNBasicPublish, null, remoteAddress, ("exchange-" + (RabbitMQTestConstants.EXCHANGE)), Expectations.annotation("rabbitmq.exchange", RabbitMQTestConstants.EXCHANGE), Expectations.annotation("rabbitmq.routingkey", RabbitMQTestConstants.ROUTING_KEY_PUSH));
        ExpectedTrace rabbitMqConsumerInvocationTrace = // serviceType
        // method
        // rpc
        // endPoint (collected but API to retrieve local address is not available in all versions, so skip)
        // remoteAddress
        Expectations.root(RabbitMQTestConstants.RABBITMQ_CLIENT, "RabbitMQ Consumer Invocation", ("rabbitmq://exchange=" + (RabbitMQTestConstants.EXCHANGE)), null, remoteAddress, Expectations.annotation("rabbitmq.routingkey", RabbitMQTestConstants.ROUTING_KEY_PUSH));
        Class<?> consumerDispatcherClass = Class.forName("com.rabbitmq.client.impl.ConsumerDispatcher");
        Method consumerDispatcherHandleDelivery = consumerDispatcherClass.getDeclaredMethod("handleDelivery", Consumer.class, String.class, Envelope.class, BasicProperties.class, byte[].class);
        ExpectedTrace consumerDispatcherHandleDeliveryTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, consumerDispatcherHandleDelivery);// method

        ExpectedTrace asynchronousInvocationTrace = Expectations.event(ASYNC.getName(), "Asynchronous Invocation");
        Class<?> blockingQueueConsumerInternalConsumerClass = Class.forName("org.springframework.amqp.rabbit.listener.BlockingQueueConsumer$InternalConsumer");
        Method blockingQueueConsumerInternalConsumerHandleDelivery = blockingQueueConsumerInternalConsumerClass.getDeclaredMethod("handleDelivery", String.class, Envelope.class, BasicProperties.class, byte[].class);
        ExpectedTrace blockingQueueConsumerInternalConsumerHandleDeliveryTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, blockingQueueConsumerInternalConsumerHandleDelivery);
        Class<?> deliveryClass = Class.forName("org.springframework.amqp.rabbit.listener.BlockingQueueConsumer$Delivery");
        Constructor<?> deliveryConstructor = deliveryClass.getDeclaredConstructor(String.class, Envelope.class, BasicProperties.class, byte[].class);
        ExpectedTrace deliveryConstructorTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, deliveryConstructor);
        Class<?> abstractMessageListenerContainerClass = Class.forName("org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer");
        Method abstractMessageListenerContainerExecuteListener = abstractMessageListenerContainerClass.getDeclaredMethod("executeListener", Channel.class, Message.class);
        ExpectedTrace abstractMessageListenerContainerExecuteListenerTrace = Expectations.event(INTERNAL_METHOD.getName(), abstractMessageListenerContainerExecuteListener);
        Class<?> propagationMarkerClass = PropagationMarker.class;
        Method propagationMarkerMark = propagationMarkerClass.getDeclaredMethod("mark");
        ExpectedTrace markTrace = Expectations.event(INTERNAL_METHOD.getName(), propagationMarkerMark);
        ExpectedTrace[] producerTraces = new ExpectedTrace[]{ rabbitTemplateConvertAndSendTrace, channelNBasicPublishTrace };
        ExpectedTrace[] consumerTraces = new ExpectedTrace[]{ rabbitMqConsumerInvocationTrace, consumerDispatcherHandleDeliveryTrace, asynchronousInvocationTrace, blockingQueueConsumerInternalConsumerHandleDeliveryTrace, deliveryConstructorTrace, asynchronousInvocationTrace, abstractMessageListenerContainerExecuteListenerTrace, markTrace };
        final int expectedTraceCount = (producerTraces.length) + (consumerTraces.length);
        final PluginTestVerifier verifier = testRunner.runPush(expectedTraceCount);
        verifier.verifyDiscreteTrace(producerTraces);
        verifier.verifyDiscreteTrace(consumerTraces);
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testPull() throws Exception {
        final String remoteAddress = testRunner.getRemoteAddress();
        Class<?> rabbitTemplateClass = Class.forName("org.springframework.amqp.rabbit.core.RabbitTemplate");
        // verify queue-initiated traces
        Method rabbitTemplateConvertAndSend = rabbitTemplateClass.getDeclaredMethod("convertAndSend", String.class, String.class, Object.class);
        ExpectedTrace rabbitTemplateConvertAndSendTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, rabbitTemplateConvertAndSend);// method

        // automatic recovery deliberately disabled as Spring has it's own recovery mechanism
        Class<?> channelNClass = Class.forName("com.rabbitmq.client.impl.ChannelN");
        Method channelNBasicPublish = channelNClass.getDeclaredMethod("basicPublish", String.class, String.class, boolean.class, boolean.class, BasicProperties.class, byte[].class);
        ExpectedTrace channelNBasicPublishTrace = // serviceType
        // method
        // rpc
        // endPoint
        // destinationId
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT, channelNBasicPublish, null, remoteAddress, ("exchange-" + (RabbitMQTestConstants.EXCHANGE)), Expectations.annotation("rabbitmq.exchange", RabbitMQTestConstants.EXCHANGE), Expectations.annotation("rabbitmq.routingkey", RabbitMQTestConstants.ROUTING_KEY_PULL));
        ExpectedTrace rabbitMqConsumerInvocationTrace = // serviceType
        // method
        // rpc
        // endPoint (collected but API to retrieve local address is not available in all versions, so skip)
        // remoteAddress
        Expectations.root(RabbitMQTestConstants.RABBITMQ_CLIENT, "RabbitMQ Consumer Invocation", ("rabbitmq://exchange=" + (RabbitMQTestConstants.EXCHANGE)), null, remoteAddress, Expectations.annotation("rabbitmq.routingkey", RabbitMQTestConstants.ROUTING_KEY_PULL));
        Class<?> amqChannelClass = Class.forName("com.rabbitmq.client.impl.AMQChannel");
        Method amqChannelHandleCompleteInboundCommand = amqChannelClass.getDeclaredMethod("handleCompleteInboundCommand", AMQCommand.class);
        ExpectedTrace amqChannelHandleCompleteInboundCommandTrace = // method
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, amqChannelHandleCompleteInboundCommand);
        ExpectedTrace[] producerTraces = new ExpectedTrace[]{ rabbitTemplateConvertAndSendTrace, channelNBasicPublishTrace };
        ExpectedTrace[] consumerTraces = new ExpectedTrace[]{ rabbitMqConsumerInvocationTrace, amqChannelHandleCompleteInboundCommandTrace };
        // verify client-initiated traces
        Method rabbitTemplateReceive = rabbitTemplateClass.getDeclaredMethod("receive", String.class);
        ExpectedTrace rabbitTemplateReceiveTrace = // serviceType
        Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, rabbitTemplateReceive);// method

        Method channelNBasicGet = channelNClass.getDeclaredMethod("basicGet", String.class, boolean.class);
        ExpectedTrace channelNBasicGetTrace = Expectations.event(RabbitMQTestConstants.RABBITMQ_CLIENT_INTERNAL, channelNBasicGet);
        Class<?> propagationMarkerClass = PropagationMarker.class;
        Method propagationMarkerMark = propagationMarkerClass.getDeclaredMethod("mark");
        ExpectedTrace markTrace = Expectations.event(INTERNAL_METHOD.getName(), propagationMarkerMark);
        ExpectedTrace[] clientInitiatedTraces = new ExpectedTrace[]{ rabbitTemplateReceiveTrace, channelNBasicGetTrace, markTrace };
        final int expectedTraceCount = ((producerTraces.length) + (consumerTraces.length)) + (clientInitiatedTraces.length);
        final PluginTestVerifier verifier = testRunner.runPull(expectedTraceCount);
        verifier.verifyDiscreteTrace(producerTraces);
        verifier.verifyDiscreteTrace(consumerTraces);
        verifier.verifyDiscreteTrace(clientInitiatedTraces);
        verifier.verifyTraceCount(0);
    }
}
