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
package org.apache.camel.component.sjms.tx;


import org.apache.camel.CamelContext;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.component.sjms.CamelJmsTestHelper;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A unit test to ensure the error is raised against incompatible configuration, InOut + transacted.
 */
public class TransactedProducerInOutErrorTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransactedProducerInOutErrorTest.class);

    @Test(expected = FailedToCreateRouteException.class)
    public void test() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createRouteBuilder());
        SjmsComponent component = context.getComponent("sjms", SjmsComponent.class);
        component.setConnectionFactory(CamelJmsTestHelper.createConnectionFactory());
        try {
            context.start();
        } catch (Throwable t) {
            Assert.assertEquals(FailedToCreateRouteException.class, t.getClass());
            Assert.assertEquals(FailedToCreateProducerException.class, t.getCause().getClass());
            Assert.assertEquals(IllegalArgumentException.class, t.getCause().getCause().getClass());
            TransactedProducerInOutErrorTest.LOG.info("Exception was thrown as expected", t);
            throw t;
        }
    }
}
