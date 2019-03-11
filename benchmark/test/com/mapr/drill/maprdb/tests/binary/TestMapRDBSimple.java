/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mapr.drill.maprdb.tests.binary;


import com.mapr.tests.annotations.ClusterTest;
import org.apache.drill.hbase.BaseHBaseTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(ClusterTest.class)
public class TestMapRDBSimple extends BaseHBaseTest {
    @Test
    public void testMe() throws Exception {
        setColumnWidths(new int[]{ 8, 38, 38 });
        final String sql = "SELECT\n" + (("  *\n" + "FROM\n") + "  hbase.`[TABLE_NAME]` tableName");
        runHBaseSQLVerifyCount(sql, 8);
    }
}

