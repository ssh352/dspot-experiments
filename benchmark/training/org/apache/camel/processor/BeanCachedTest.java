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
package org.apache.camel.processor;


import javax.naming.Context;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Assert;
import org.junit.Test;


public class BeanCachedTest extends ContextTestSupport {
    private Context context;

    private JndiRegistry registry;

    @Test
    public void testFreshBeanInContext() throws Exception {
        // Just make sure the bean processor doesn't work if the cached is false
        BeanCachedTest.MyBean originalInstance = registry.lookupByNameAndType("something", BeanCachedTest.MyBean.class);
        template.sendBody("direct:noCache", null);
        context.unbind("something");
        context.bind("something", new BeanCachedTest.MyBean());
        // Make sure we can get the object from the registry
        Assert.assertNotSame(registry.lookupByName("something"), originalInstance);
        template.sendBody("direct:noCache", null);
    }

    @Test
    public void testBeanWithCached() throws Exception {
        // Just make sure the bean processor doesn't work if the cached is false
        BeanCachedTest.MyBean originalInstance = registry.lookupByNameAndType("something", BeanCachedTest.MyBean.class);
        template.sendBody("direct:cached", null);
        context.unbind("something");
        context.bind("something", new BeanCachedTest.MyBean());
        // Make sure we can get the object from the registry
        Assert.assertNotSame(registry.lookupByName("something"), originalInstance);
        try {
            template.sendBody("direct:cached", null);
            Assert.fail("The IllegalStateException is expected");
        } catch (CamelExecutionException ex) {
            Assert.assertTrue("IllegalStateException is expected!", ((ex.getCause()) instanceof IllegalStateException));
            Assert.assertEquals("This bean is not supported to be invoked again!", ex.getCause().getMessage());
        }
    }

    public static class MyBean {
        private boolean invoked;

        public void doSomething(Exchange exchange) throws Exception {
            if (invoked) {
                throw new IllegalStateException("This bean is not supported to be invoked again!");
            } else {
                invoked = true;
            }
        }
    }
}
