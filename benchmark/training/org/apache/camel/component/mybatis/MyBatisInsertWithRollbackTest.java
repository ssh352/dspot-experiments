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
package org.apache.camel.component.mybatis;


import Exchange.EXCEPTION_CAUGHT;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Test;


public class MyBatisInsertWithRollbackTest extends MyBatisTestSupport {
    @Test
    public void testInsert() throws Exception {
        getMockEndpoint("mock:commit").expectedMessageCount(0);
        getMockEndpoint("mock:rollback").expectedMessageCount(1);
        getMockEndpoint("mock:rollback").message(0).body().isEqualTo(null);
        getMockEndpoint("mock:rollback").message(0).header(EXCEPTION_CAUGHT).isInstanceOf(PersistenceException.class);
        template.sendBody("direct:start", null);
        assertMockEndpointsSatisfied();
        // there should be still 2 rows
        Integer rows = template.requestBody("mybatis:count?statementType=SelectOne", null, Integer.class);
        assertEquals("There should be 2 rows", 2, rows.intValue());
    }
}
