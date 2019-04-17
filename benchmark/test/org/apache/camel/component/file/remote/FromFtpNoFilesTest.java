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


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test to verify polling a server with no files to poll.
 */
public class FromFtpNoFilesTest extends FtpServerTestSupport {
    @Test
    public void testPoolIn3SecondsButNoFiles() throws Exception {
        deleteDirectory(FtpServerTestSupport.FTP_ROOT_DIR);
        createDirectory(((FtpServerTestSupport.FTP_ROOT_DIR) + "slowfile"));
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        Thread.sleep((3 * 1000L));
        mock.assertIsSatisfied();
    }
}
