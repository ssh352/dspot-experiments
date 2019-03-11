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


import java.net.URI;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4595Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ4595Test.class);

    private BrokerService broker;

    private URI connectUri;

    private ActiveMQConnectionFactory factory;

    @Test(timeout = 120000)
    public void testBrowsingSmallBatch() throws JMSException {
        doTestBrowsing(100);
    }

    @Test(timeout = 160000)
    public void testBrowsingMediumBatch() throws JMSException {
        doTestBrowsing(1000);
    }

    @Test(timeout = 300000)
    public void testBrowsingLargeBatch() throws JMSException {
        doTestBrowsing(10000);
    }
}

