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
package org.apache.camel.component.cxf.util;


import DataFormat.CXF_MESSAGE;
import DataFormat.RAW;
import javax.xml.namespace.QName;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class CxfEndpointUtilsTest extends Assert {
    // set up the port name and service name
    protected static final QName SERVICE_NAME = new QName("http://www.example.com/test", "ServiceName");

    protected static final QName PORT_NAME = new QName("http://www.example.com/test", "PortName");

    private static final String CXF_BASE_URI = "cxf://http://www.example.com/testaddress" + ((("?serviceClass=org.apache.camel.component.cxf.HelloService" + "&portName={http://www.example.com/test}PortName") + "&serviceName={http://www.example.com/test}ServiceName") + "&defaultBus=true");

    private static final String NO_SERVICE_CLASS_URI = "cxf://http://www.example.com/testaddress" + ("?portName={http://www.example.com/test}PortName" + "&serviceName={http://www.example.com/test}ServiceName");

    @Test
    public void testGetProperties() throws Exception {
        CxfEndpoint endpoint = createEndpoint(getEndpointURI());
        QName service = endpoint.getServiceName();
        Assert.assertEquals("We should get the right service name", service, CxfEndpointUtilsTest.SERVICE_NAME);
    }

    @Test
    public void testGetDataFormatCXF() throws Exception {
        CxfEndpoint endpoint = createEndpoint((((getEndpointURI()) + (sepChar())) + "dataFormat=CXF_MESSAGE"));
        Assert.assertEquals("We should get the Message DataFormat", CXF_MESSAGE, endpoint.getDataFormat());
    }

    @Test
    public void testGetDataFormatRAW() throws Exception {
        CxfEndpoint endpoint = createEndpoint((((getEndpointURI()) + (sepChar())) + "dataFormat=RAW"));
        Assert.assertEquals("We should get the Message DataFormat", RAW, endpoint.getDataFormat());
    }

    @Test
    public void testCheckServiceClassWithTheEndpoint() throws Exception {
        CxfEndpoint endpoint = createEndpoint(getNoServiceClassURI());
        Assert.assertNull(endpoint.getServiceClass());
    }

    @Test
    public void testCheckServiceClassProcedure() throws Exception {
        CxfEndpoint endpoint = createEndpoint(getNoServiceClassURI());
        Assert.assertNotNull(endpoint.createProducer());
    }

    @Test
    public void testCheckServiceClassConsumer() throws Exception {
        CxfEndpoint endpoint = createEndpoint(getNoServiceClassURI());
        try {
            endpoint.createConsumer(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    // noop
                }
            });
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException exception) {
            Assert.assertNotNull("Should get a CamelException here", exception);
            Assert.assertTrue(exception.getMessage().startsWith("serviceClass must be specified"));
        }
    }
}
