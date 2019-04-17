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


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test that we can do file over JMS to file.
 */
public class FileRouteToJmsToFileTest extends CamelTestSupport {
    protected String componentName = "activemq";

    @Test
    public void testRouteFileToFile() throws Exception {
        deleteDirectory("target/file2file");
        NotifyBuilder notify = from("activemq:queue:hello").whenDone(1).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("file://target/file2file/in", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        File file = new File("target/file2file/out/hello.txt");
        assertTrue("The file should exists", file.exists());
    }
}
