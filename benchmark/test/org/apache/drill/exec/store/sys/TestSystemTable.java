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
package org.apache.drill.exec.store.sys;


import ExecConstants.JSON_ALL_TEXT_MODE;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class TestSystemTable extends PlanTestBase {
    @Test
    public void alterSessionOption() throws Exception {
        // 
        BaseTestQuery.newTest().sqlQuery("select val as bool from sys.options where name = '%s' order by accessibleScopes desc", JSON_ALL_TEXT_MODE).baselineColumns("bool").ordered().baselineValues(String.valueOf(false)).go();
        BaseTestQuery.test("alter session set `%s` = true", JSON_ALL_TEXT_MODE);
        // Using old table to detect both optionScopes: BOOT & SESSION
        BaseTestQuery.newTest().sqlQuery("select bool_val as bool from sys.options_old where name = '%s' order by accessibleScopes desc ", JSON_ALL_TEXT_MODE).baselineColumns("bool").ordered().baselineValues(false).baselineValues(true).go();
    }

    // DRILL-2670
    @Test
    @Category(UnlikelyTest.class)
    public void optionsOrderBy() throws Exception {
        BaseTestQuery.test("select * from sys.options order by name");
    }

    @Test
    public void threadsTable() throws Exception {
        BaseTestQuery.test("select * from sys.threads");
    }

    @Test
    public void memoryTable() throws Exception {
        BaseTestQuery.test("select * from sys.memory");
    }

    @Test
    public void connectionsTable() throws Exception {
        BaseTestQuery.test("select * from sys.connections");
    }

    @Test
    public void functionsTable() throws Exception {
        BaseTestQuery.test("select * from sys.functions");
    }

    @Test
    public void testInternalFunctionsTable() throws Exception {
        String query = "select internal, count(*) from sys.functions group by internal";
        // Testing a mix of public and internal functions defined in FunctionTemplate
        Assert.assertEquals(2, BaseTestQuery.testSql(query));
    }

    @Test
    public void profilesTable() throws Exception {
        BaseTestQuery.test("select * from sys.profiles");
    }

    @Test
    public void profilesJsonTable() throws Exception {
        BaseTestQuery.test("select * from sys.profiles_json");
    }

    @Test
    public void testProfilesLimitPushDown() throws Exception {
        String query = "select * from sys.profiles limit 10";
        String numFilesPattern = "maxRecordsToRead=10";
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern }, new String[]{  });
    }

    @Test
    public void testColumnNullability() throws Exception {
        String query = "select distinct is_nullable, count(*) from INFORMATION_SCHEMA.`COLUMNS` where table_schema = 'sys' group by is_nullable";
        // Asserting a mix of nullable and non-nullable columns (pre-DRILL-6588, all columns were Not Nullable)
        Assert.assertEquals(2, BaseTestQuery.testSql(query));
    }
}

