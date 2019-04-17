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
package org.apache.camel.component.bean;


import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.processor.BeanRouteTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BodyAnnotationToDisambiguateMethodsTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BeanRouteTest.class);

    protected BodyAnnotationToDisambiguateMethodsTest.MyBean myBean = new BodyAnnotationToDisambiguateMethodsTest.MyBean();

    @Test
    public void testSendMessage() throws Exception {
        String expectedBody = "Wobble";
        template.sendBodyAndHeader("direct:in", expectedBody, "foo", "bar");
        Assert.assertEquals(("bean body: " + (myBean)), expectedBody, myBean.body);
    }

    public static class MyBean {
        public String body;

        public void bar(String body) {
            Assert.fail(("bar() called with: " + body));
        }

        public void foo(@Body
        String body) {
            this.body = body;
            BodyAnnotationToDisambiguateMethodsTest.LOG.info(("foo() method called on " + (this)));
        }

        public void wrongMethod(String body) {
            Assert.fail(("wrongMethod() called with: " + body));
        }
    }
}
