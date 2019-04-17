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
package org.apache.camel.component.jms;


import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class ConsumeJmsObjectMessageTest extends CamelTestSupport {
    protected JmsTemplate jmsTemplate;

    private MockEndpoint endpoint;

    @Test
    public void testConsumeObjectMessage() throws Exception {
        endpoint.expectedMessageCount(1);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.send("test.object", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage msg = session.createObjectMessage();
                ConsumeJmsObjectMessageTest.MyUser user = new ConsumeJmsObjectMessageTest.MyUser();
                user.setName("Claus");
                msg.setObject(user);
                return msg;
            }
        });
        endpoint.assertIsSatisfied();
        assertCorrectObjectReceived();
    }

    @Test
    public void testSendBytesMessage() throws Exception {
        endpoint.expectedMessageCount(1);
        ConsumeJmsObjectMessageTest.MyUser user = new ConsumeJmsObjectMessageTest.MyUser();
        user.setName("Claus");
        template.sendBody("direct:test", user);
        endpoint.assertIsSatisfied();
        assertCorrectObjectReceived();
    }

    public static class MyUser implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
