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
package org.apache.camel.component.cxf;


import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.wsdl_first.Person;
import org.apache.camel.wsdl_first.PersonService;
import org.apache.camel.wsdl_first.UnknownPersonFault;
import org.junit.Test;


public class CXFWsdlOnlyTest extends CamelSpringTestSupport {
    private static Endpoint endpoint1;

    private static Endpoint endpoint2;

    private static int port1 = CXFTestSupport.getPort1();

    private static int port2 = CXFTestSupport.getPort2();

    private static int port3 = CXFTestSupport.getPort3();

    private static int port4 = CXFTestSupport.getPort4();

    @Test
    public void testRoutesWithFault() throws Exception {
        URL wsdlURL = getClass().getClassLoader().getResource("person.wsdl");
        PersonService ss = new PersonService(wsdlURL, new QName("http://camel.apache.org/wsdl-first", "PersonService"));
        Person client = ss.getSoap();
        ((BindingProvider) (client)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CXFWsdlOnlyTest.port3)) + "/CXFWsdlOnlyTest/PersonService/"));
        Holder<String> personId = new Holder<>();
        personId.value = "hello";
        Holder<String> ssn = new Holder<>();
        Holder<String> name = new Holder<>();
        client.getPerson(personId, ssn, name);
        assertEquals("Bonjour", name.value);
        personId.value = "";
        ssn = new Holder<>();
        name = new Holder<>();
        Throwable t = null;
        try {
            client.getPerson(personId, ssn, name);
            fail("Expect exception");
        } catch (UnknownPersonFault e) {
            t = e;
        }
        assertTrue((t instanceof UnknownPersonFault));
        Person client2 = ss.getSoap2();
        ((BindingProvider) (client2)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CXFWsdlOnlyTest.port4)) + "/CXFWsdlOnlyTest/PersonService/"));
        Holder<String> personId2 = new Holder<>();
        personId2.value = "hello";
        Holder<String> ssn2 = new Holder<>();
        Holder<String> name2 = new Holder<>();
        client2.getPerson(personId2, ssn2, name2);
        assertEquals("Bonjour", name2.value);
        personId2.value = "";
        ssn2 = new Holder<>();
        name2 = new Holder<>();
        try {
            client2.getPerson(personId2, ssn2, name2);
            fail("Expect exception");
        } catch (UnknownPersonFault e) {
            t = e;
        }
        assertTrue((t instanceof UnknownPersonFault));
    }
}
