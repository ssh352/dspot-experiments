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
package org.apache.activemq.transport.amqp;


import Session.AUTO_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.qpid.proton.amqp.Binary;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests interoperability between OpenWire and AMQP
 */
@RunWith(Parameterized.class)
public class JMSInteroperabilityTest extends JMSClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSInteroperabilityTest.class);

    private final String transformer;

    public JMSInteroperabilityTest(String transformer) {
        this.transformer = transformer;
    }

    // ----- Tests for property handling between protocols --------------------//
    @SuppressWarnings("unchecked")
    @Test(timeout = 60000)
    public void testMessagePropertiesArePreservedOpenWireToAMQP() throws Exception {
        boolean bool = true;
        byte bValue = 127;
        short nShort = 10;
        int nInt = 5;
        long nLong = 333;
        float nFloat = 1;
        double nDouble = 100;
        Enumeration<String> propertyNames = null;
        String testMessageBody = "Testing msgPropertyExistTest";
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        TextMessage outbound = openwireSession.createTextMessage();
        outbound.setText(testMessageBody);
        outbound.setBooleanProperty("Boolean", bool);
        outbound.setByteProperty("Byte", bValue);
        outbound.setShortProperty("Short", nShort);
        outbound.setIntProperty("Integer", nInt);
        outbound.setFloatProperty("Float", nFloat);
        outbound.setDoubleProperty("Double", nDouble);
        outbound.setStringProperty("String", "test");
        outbound.setLongProperty("Long", nLong);
        outbound.setObjectProperty("BooleanObject", Boolean.valueOf(bool));
        openwireProducer.send(outbound);
        Message inbound = amqpConsumer.receive(2500);
        propertyNames = inbound.getPropertyNames();
        int propertyCount = 0;
        do {
            String propertyName = propertyNames.nextElement();
            if ((propertyName.indexOf("JMS")) != 0) {
                propertyCount++;
                if (((((((((propertyName.equals("Boolean")) || (propertyName.equals("Byte"))) || (propertyName.equals("Integer"))) || (propertyName.equals("Short"))) || (propertyName.equals("Float"))) || (propertyName.equals("Double"))) || (propertyName.equals("String"))) || (propertyName.equals("Long"))) || (propertyName.equals("BooleanObject"))) {
                    JMSInteroperabilityTest.LOG.debug("Appclication Property set by client is: {}", propertyName);
                    if (!(inbound.propertyExists(propertyName))) {
                        Assert.assertTrue(inbound.propertyExists(propertyName));
                        JMSInteroperabilityTest.LOG.debug("Positive propertyExists test failed for {}", propertyName);
                    } else
                        if (inbound.propertyExists((propertyName + "1"))) {
                            JMSInteroperabilityTest.LOG.debug("Negative propertyExists test failed for {} 1", propertyName);
                            Assert.fail((("Negative propertyExists test failed for " + propertyName) + "1"));
                        }

                } else {
                    JMSInteroperabilityTest.LOG.debug("Appclication Property not set by client: {}", propertyName);
                    Assert.fail(("Appclication Property not set by client: " + propertyName));
                }
            } else {
                JMSInteroperabilityTest.LOG.debug("JMSProperty Name is: {}", propertyName);
            }
        } while (propertyNames.hasMoreElements() );
        amqp.close();
        openwire.close();
        Assert.assertEquals("Unexpected number of properties in received message.", 9, propertyCount);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 60000)
    public void testMessagePropertiesArePreservedAMQPToOpenWire() throws Exception {
        // Raw Transformer doesn't expand message properties.
        Assume.assumeFalse(transformer.equals("raw"));
        boolean bool = true;
        byte bValue = 127;
        short nShort = 10;
        int nInt = 5;
        long nLong = 333;
        float nFloat = 1;
        double nDouble = 100;
        Enumeration<String> propertyNames = null;
        String testMessageBody = "Testing msgPropertyExistTest";
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer amqpProducer = amqpSession.createProducer(queue);
        MessageConsumer openwireConsumer = openwireSession.createConsumer(queue);
        TextMessage outbound = amqpSession.createTextMessage();
        outbound.setText(testMessageBody);
        outbound.setBooleanProperty("Boolean", bool);
        outbound.setByteProperty("Byte", bValue);
        outbound.setShortProperty("Short", nShort);
        outbound.setIntProperty("Integer", nInt);
        outbound.setFloatProperty("Float", nFloat);
        outbound.setDoubleProperty("Double", nDouble);
        outbound.setStringProperty("String", "test");
        outbound.setLongProperty("Long", nLong);
        outbound.setObjectProperty("BooleanObject", Boolean.valueOf(bool));
        amqpProducer.send(outbound);
        Message inbound = openwireConsumer.receive(2500);
        propertyNames = inbound.getPropertyNames();
        int propertyCount = 0;
        do {
            String propertyName = propertyNames.nextElement();
            if ((propertyName.indexOf("JMS")) != 0) {
                propertyCount++;
                if (((((((((propertyName.equals("Boolean")) || (propertyName.equals("Byte"))) || (propertyName.equals("Integer"))) || (propertyName.equals("Short"))) || (propertyName.equals("Float"))) || (propertyName.equals("Double"))) || (propertyName.equals("String"))) || (propertyName.equals("Long"))) || (propertyName.equals("BooleanObject"))) {
                    JMSInteroperabilityTest.LOG.debug("Appclication Property set by client is: {}", propertyName);
                    if (!(inbound.propertyExists(propertyName))) {
                        Assert.assertTrue(inbound.propertyExists(propertyName));
                        JMSInteroperabilityTest.LOG.debug("Positive propertyExists test failed for {}", propertyName);
                    } else
                        if (inbound.propertyExists((propertyName + "1"))) {
                            JMSInteroperabilityTest.LOG.debug("Negative propertyExists test failed for {} 1", propertyName);
                            Assert.fail((("Negative propertyExists test failed for " + propertyName) + "1"));
                        }

                } else {
                    JMSInteroperabilityTest.LOG.debug("Appclication Property not set by client: {}", propertyName);
                    Assert.fail(("Appclication Property not set by client: " + propertyName));
                }
            } else {
                JMSInteroperabilityTest.LOG.debug("JMSProperty Name is: {}", propertyName);
            }
        } while (propertyNames.hasMoreElements() );
        amqp.close();
        openwire.close();
        Assert.assertEquals("Unexpected number of properties in received message.", 9, propertyCount);
    }

    // ----- Tests for OpenWire to Qpid JMS using MapMessage ------------------//
    @SuppressWarnings("unchecked")
    @Test
    public void testMapMessageUsingPrimitiveSettersSendReceive() throws Exception {
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        byte[] bytesValue = new byte[]{ 1, 2, 3, 4, 5 };
        // Create the Message
        MapMessage outgoing = openwireSession.createMapMessage();
        outgoing.setBoolean("boolean", true);
        outgoing.setByte("byte", ((byte) (10)));
        outgoing.setBytes("bytes", bytesValue);
        outgoing.setChar("char", 'B');
        outgoing.setDouble("double", 24.42);
        outgoing.setFloat("float", 3.14159F);
        outgoing.setInt("integer", 1024);
        outgoing.setLong("long", 8096L);
        outgoing.setShort("short", ((short) (255)));
        openwireProducer.send(outgoing);
        // Now consume the MapMessage
        Message received = amqpConsumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertTrue(("Expected MapMessage but got " + received), (received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Map<String, Object> incomingMap = ((Map<String, Object>) (incoming.getObject()));
        Assert.assertEquals(true, incomingMap.get("boolean"));
        Assert.assertEquals(10, ((byte) (incomingMap.get("byte"))));
        Assert.assertEquals('B', incomingMap.get("char"));
        Assert.assertEquals(24.42, ((double) (incomingMap.get("double"))), 0.5);
        Assert.assertEquals(3.14159F, ((float) (incomingMap.get("float"))), 0.5F);
        Assert.assertEquals(1024, incomingMap.get("integer"));
        Assert.assertEquals(8096L, incomingMap.get("long"));
        Assert.assertEquals(255, ((short) (incomingMap.get("short"))));
        // Test for the byte array which will be in an AMQP Binary as this message
        // is received as an ObjectMessage by Qpid JMS
        Object incomingValue = incomingMap.get("bytes");
        Assert.assertNotNull(incomingValue);
        Assert.assertTrue((incomingValue instanceof Binary));
        Binary incomingBinary = ((Binary) (incomingValue));
        byte[] incomingBytes = Arrays.copyOfRange(incomingBinary.getArray(), incomingBinary.getArrayOffset(), incomingBinary.getLength());
        Assert.assertTrue(Arrays.equals(bytesValue, incomingBytes));
        amqp.close();
        openwire.close();
    }

    // ----- Tests for OpenWire <-> Qpid JMS using ObjectMessage --------------//
    @SuppressWarnings("unchecked")
    @Test
    public void testMapInObjectMessageSendReceive() throws Exception {
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        // Create the Message
        ObjectMessage outgoing = openwireSession.createObjectMessage();
        HashMap<String, Object> outgoingMap = new HashMap<String, Object>();
        outgoingMap.put("none", null);
        outgoingMap.put("string", "test");
        outgoingMap.put("long", 255L);
        outgoingMap.put("empty-string", "");
        outgoingMap.put("negative-int", (-1));
        outgoingMap.put("float", 0.12F);
        outgoing.setObject(outgoingMap);
        openwireProducer.send(outgoing);
        // Now consume the ObjectMessage
        Message received = amqpConsumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertTrue(("Expected ObjectMessage but got " + received), (received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Object incomingObject = incoming.getObject();
        Assert.assertNotNull(incomingObject);
        Assert.assertTrue((incomingObject instanceof Map));
        Map<String, Object> incomingMap = ((Map<String, Object>) (incomingObject));
        Assert.assertEquals(outgoingMap.size(), incomingMap.size());
        amqp.close();
        openwire.close();
    }

    @Test
    public void testQpidToOpenWireObjectMessage() throws Exception {
        // Raw Transformer doesn't expand message properties.
        Assume.assumeFalse((!(transformer.equals("jms"))));
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer amqpProducer = amqpSession.createProducer(queue);
        MessageConsumer openwireConsumer = openwireSession.createConsumer(queue);
        // Create and send the Message
        ObjectMessage outgoing = amqpSession.createObjectMessage();
        outgoing.setObject(UUID.randomUUID());
        amqpProducer.send(outgoing);
        // Now consume the ObjectMessage
        Message received = openwireConsumer.receive(2000);
        Assert.assertNotNull(received);
        JMSInteroperabilityTest.LOG.info("Read new message: {}", received);
        Assert.assertTrue((received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Object payload = incoming.getObject();
        Assert.assertNotNull(payload);
        Assert.assertTrue((payload instanceof UUID));
        amqp.close();
        openwire.close();
    }

    @Test
    public void testOpenWireToQpidObjectMessage() throws Exception {
        // Raw Transformer doesn't expand message properties.
        Assume.assumeFalse((!(transformer.equals("jms"))));
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        // Create and send the Message
        ObjectMessage outgoing = amqpSession.createObjectMessage();
        outgoing.setObject(UUID.randomUUID());
        openwireProducer.send(outgoing);
        // Now consume the ObjectMessage
        Message received = amqpConsumer.receive(2000);
        Assert.assertNotNull(received);
        JMSInteroperabilityTest.LOG.info("Read new message: {}", received);
        Assert.assertTrue((received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Object payload = incoming.getObject();
        Assert.assertNotNull(payload);
        Assert.assertTrue((payload instanceof UUID));
        amqp.close();
        openwire.close();
    }

    @Test
    public void testOpenWireToQpidObjectMessageWithOpenWireCompression() throws Exception {
        // Raw Transformer doesn't expand message properties.
        Assume.assumeFalse((!(transformer.equals("jms"))));
        Connection openwire = createJMSConnection();
        setUseCompression(true);
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        // Create and send the Message
        ObjectMessage outgoing = amqpSession.createObjectMessage();
        outgoing.setObject(UUID.randomUUID());
        openwireProducer.send(outgoing);
        // Now consume the ObjectMessage
        Message received = amqpConsumer.receive(2000);
        Assert.assertNotNull(received);
        JMSInteroperabilityTest.LOG.info("Read new message: {}", received);
        Assert.assertTrue((received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Object payload = incoming.getObject();
        Assert.assertNotNull(payload);
        Assert.assertTrue((payload instanceof UUID));
        amqp.close();
        openwire.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObjectMessageContainingList() throws Exception {
        Connection openwire = createJMSConnection();
        Connection amqp = createConnection();
        openwire.start();
        amqp.start();
        Session openwireSession = openwire.createSession(false, AUTO_ACKNOWLEDGE);
        Session amqpSession = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = openwireSession.createQueue(getDestinationName());
        MessageProducer openwireProducer = openwireSession.createProducer(queue);
        MessageConsumer amqpConsumer = amqpSession.createConsumer(queue);
        // Create the Message
        ObjectMessage outgoing = openwireSession.createObjectMessage();
        ArrayList<Object> outgoingList = new ArrayList<Object>();
        outgoingList.add(null);
        outgoingList.add("test");
        outgoingList.add(255L);
        outgoingList.add("");
        outgoingList.add((-1));
        outgoingList.add(0.12F);
        outgoing.setObject(outgoingList);
        openwireProducer.send(outgoing);
        // Now consume the ObjectMessage
        Message received = amqpConsumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertTrue((received instanceof ObjectMessage));
        ObjectMessage incoming = ((ObjectMessage) (received));
        Object incomingObject = incoming.getObject();
        Assert.assertNotNull(incomingObject);
        Assert.assertTrue((incomingObject instanceof List));
        List<Object> incomingList = ((List<Object>) (incomingObject));
        Assert.assertEquals(outgoingList.size(), incomingList.size());
        amqp.close();
        openwire.close();
    }

    // ----- Test Qpid JMS to Qpid JMS interop with transformers --------------//
    @Test
    public void testQpidJMSToQpidJMSMessageSendReceive() throws Exception {
        final int SIZE = 1024;
        final int NUM_MESSAGES = 100;
        Connection amqpSend = createConnection("client-1");
        Connection amqpReceive = createConnection("client-2");
        amqpReceive.start();
        Session senderSession = amqpSend.createSession(false, AUTO_ACKNOWLEDGE);
        Session receiverSession = amqpReceive.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = senderSession.createQueue(getDestinationName());
        MessageProducer amqpProducer = senderSession.createProducer(queue);
        MessageConsumer amqpConsumer = receiverSession.createConsumer(queue);
        byte[] payload = new byte[SIZE];
        for (int i = 0; i < NUM_MESSAGES; ++i) {
            BytesMessage outgoing = senderSession.createBytesMessage();
            outgoing.setLongProperty("SendTime", System.currentTimeMillis());
            outgoing.writeBytes(payload);
            amqpProducer.send(outgoing);
        }
        // Now consume the message
        for (int i = 0; i < NUM_MESSAGES; ++i) {
            Message received = amqpConsumer.receive(2000);
            Assert.assertNotNull(received);
            Assert.assertTrue(("Expected BytesMessage but got " + received), (received instanceof BytesMessage));
            BytesMessage incoming = ((BytesMessage) (received));
            Assert.assertEquals(SIZE, incoming.getBodyLength());
        }
        amqpReceive.close();
        amqpSend.close();
    }
}

