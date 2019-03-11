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
package org.apache.activemq.bugs;


import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;


public class AMQ5136Test {
    BrokerService brokerService;

    @Test
    public void memoryUsageOnCommit() throws Exception {
        sendMessagesAndAssertMemoryUsage(new AMQ5136Test.TransactionHandler() {
            @Override
            public void finishTransaction(Session session) throws JMSException {
                session.commit();
            }
        });
    }

    @Test
    public void memoryUsageOnRollback() throws Exception {
        sendMessagesAndAssertMemoryUsage(new AMQ5136Test.TransactionHandler() {
            @Override
            public void finishTransaction(Session session) throws JMSException {
                session.rollback();
            }
        });
    }

    private static interface TransactionHandler {
        void finishTransaction(Session session) throws JMSException;
    }
}

