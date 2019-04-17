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
import org.junit.Test;


public class FileMoveAndMoveFailedIssueTest extends ContextTestSupport {
    @Test
    public void testMove() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedFileExists("target/data/input/target/data/input.bak/somedate/hello.txt");
        template.sendBodyAndHeader("file:target/data/input", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMoveFailed() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedFileExists("target/data/input/target/data/input.err/somedate/bomb.txt");
        template.sendBodyAndHeader("file:target/data/input", "Kaboom", FILE_NAME, "bomb.txt");
        assertMockEndpointsSatisfied();
    }
}
