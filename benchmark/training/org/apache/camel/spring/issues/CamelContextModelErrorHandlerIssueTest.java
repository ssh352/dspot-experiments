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
package org.apache.camel.spring.issues;


import java.io.File;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.apache.camel.spring.SpringModelJAXBContextFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CamelContextModelErrorHandlerIssueTest extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(CamelContextModelErrorHandlerIssueTest.class);

    @Test
    public void testCamelContextModel() throws Exception {
        JAXBContext jaxbContext = new SpringModelJAXBContextFactory().newJAXBContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new File("src/test/resources/org/apache/camel/spring/issues/CamelContextModelErrorHandlerIssueTest.xml"));
        Assert.assertNotNull(obj);
        CamelContextFactoryBean context = ((CamelContextFactoryBean) (obj));
        Assert.assertEquals("myCamel", context.getId());
        Assert.assertEquals("dlc", context.getErrorHandlerRef());
        Assert.assertEquals(1, context.getRoutes().size());
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(context, writer);
        String s = writer.getBuffer().toString();
        CamelContextModelErrorHandlerIssueTest.LOG.info(s);
        Assert.assertTrue("Should have error handler", s.contains("<errorHandler"));
        Assert.assertTrue("Should have redelivery policy", s.contains("<redeliveryPolicy"));
    }
}
