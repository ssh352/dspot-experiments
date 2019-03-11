/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.service.cli.thrift;


import org.apache.hive.service.rpc.thrift.TOpenSessionReq;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test security in classes generated by Thrift.
 */
public class TestThriftCLIServiceSecurity {
    /**
     * Ensures password isn't printed to logs from TOpenSessionReq.toString().
     * See maven-replacer-plugin code in service-rpc/pom.xml.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPasswordNotInLogs() throws Exception {
        String PASSWORD = "testpassword";
        TOpenSessionReq tOpenSessionReq = new TOpenSessionReq();
        tOpenSessionReq.setPassword(PASSWORD);
        Assert.assertFalse(tOpenSessionReq.toString().contains(PASSWORD));
    }
}

