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
package org.apache.activemq.command;


import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import junit.framework.TestCase;


/**
 *
 */
public class ActiveMQObjectMessageTest extends TestCase {
    /**
     * Constructor for ActiveMQObjectMessageTest.
     *
     * @param name
     * 		
     */
    public ActiveMQObjectMessageTest(String name) {
        super(name);
    }

    public void testBytes() throws IOException, JMSException {
        ActiveMQObjectMessage msg = new ActiveMQObjectMessage();
        String str = "testText";
        msg.setObject(str);
        msg = ((ActiveMQObjectMessage) (msg.copy()));
        TestCase.assertEquals(msg.getObject(), str);
    }

    public void testSetObject() throws JMSException {
        ActiveMQObjectMessage msg = new ActiveMQObjectMessage();
        String str = "testText";
        msg.setObject(str);
        TestCase.assertTrue(((msg.getObject()) == str));
    }

    public void testClearBody() throws JMSException {
        ActiveMQObjectMessage objectMessage = new ActiveMQObjectMessage();
        try {
            objectMessage.setObject("String");
            objectMessage.clearBody();
            TestCase.assertFalse(objectMessage.isReadOnlyBody());
            TestCase.assertNull(objectMessage.getObject());
            objectMessage.setObject("String");
            objectMessage.getObject();
        } catch (MessageNotWriteableException mnwe) {
            TestCase.fail("should be writeable");
        }
    }

    public void testReadOnlyBody() throws JMSException {
        ActiveMQObjectMessage msg = new ActiveMQObjectMessage();
        msg.setObject("test");
        msg.setReadOnlyBody(true);
        try {
            msg.getObject();
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        }
        try {
            msg.setObject("test");
            TestCase.fail("should throw exception");
        } catch (MessageNotWriteableException e) {
        }
    }

    public void testWriteOnlyBody() throws JMSException {
        // should always be readable
        ActiveMQObjectMessage msg = new ActiveMQObjectMessage();
        msg.setReadOnlyBody(false);
        try {
            msg.setObject("test");
            msg.getObject();
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        }
        msg.setReadOnlyBody(true);
        try {
            msg.getObject();
            msg.setObject("test");
            TestCase.fail("should throw exception");
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        } catch (MessageNotWriteableException mnwe) {
        }
    }
}
