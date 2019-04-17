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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the FileRenameStrategy using preMove and move options
 */
public class FileConsumerBeginAndCommitRenameStrategyTest extends ContextTestSupport {
    @Test
    public void testRenameSuccess() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:report");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Hello Paris");
        mock.expectedFileExists("target/data/done/paris.txt", "Hello Paris");
        template.sendBodyAndHeader("file:target/data/reports", "Hello Paris", FILE_NAME, "paris.txt");
        mock.assertIsSatisfied();
    }

    @Test
    public void testIllegalOptions() throws Exception {
        try {
            context.getEndpoint("file://target/data?move=../done/${file:name}&delete=true").createConsumer(new Processor() {
                public void process(Exchange exchange) throws Exception {
                }
            });
            Assert.fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            context.getEndpoint("file://target/data?move=${file:name.noext}.bak&delete=true").createConsumer(new Processor() {
                public void process(Exchange exchange) throws Exception {
                }
            });
            Assert.fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }
}
