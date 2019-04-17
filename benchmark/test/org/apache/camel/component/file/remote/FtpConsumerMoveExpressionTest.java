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
package org.apache.camel.component.file.remote;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for FTP using expression (file language)
 */
public class FtpConsumerMoveExpressionTest extends FtpServerTestSupport {
    @Test
    public void testMoveUsingExpression() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Reports");
        sendFile(getFtpUrl(), "Reports", "report2.txt");
        assertMockEndpointsSatisfied();
        // give time for consumer to rename file
        Thread.sleep(1000);
        String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File(((((FtpServerTestSupport.FTP_ROOT_DIR) + "/filelanguage/backup/") + now) + "/123-report2.bak"));
        assertTrue("File should have been renamed", file.exists());
    }

    public class MyGuidGenerator {
        public String guid() {
            return "123";
        }
    }
}
