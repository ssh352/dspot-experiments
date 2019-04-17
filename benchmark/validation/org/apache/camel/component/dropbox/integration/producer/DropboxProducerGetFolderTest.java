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
package org.apache.camel.component.dropbox.integration.producer;


import org.apache.camel.component.dropbox.integration.DropboxTestSupport;
import org.junit.Test;


public class DropboxProducerGetFolderTest extends DropboxTestSupport {
    public static final String FILE_NAME1 = "myFile.txt";

    public static final String FILE_NAME2 = "myFile2.txt";

    private static final String CONTENT1 = "content1";

    private static final String CONTENT2 = "content2";

    @Test
    public void testCamelDropbox() throws Exception {
        test("direct:start");
    }

    @Test
    public void testCamelDropboxWithOptionInHeader() throws Exception {
        test("direct:start2");
    }

    @Test
    public void testCamelDropboxHeaderHasPriorityOnParameter() throws Exception {
        test("direct:start3");
    }
}
