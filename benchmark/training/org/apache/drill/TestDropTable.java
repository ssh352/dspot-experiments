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
package org.apache.drill;


import org.apache.drill.categories.SqlTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.test.BaseTestQuery;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestDropTable extends PlanTestBase {
    private static final String CREATE_SIMPLE_TABLE = "create table `%s` as select 1 from cp.`employee.json`";

    private static final String CREATE_SIMPLE_VIEW = "create view `%s` as select 1 from cp.`employee.json`";

    private static final String DROP_TABLE = "drop table `%s`";

    private static final String DROP_TABLE_IF_EXISTS = "drop table if exists `%s`";

    private static final String DROP_VIEW_IF_EXISTS = "drop view if exists `%s`";

    @Test
    public void testDropJsonTable() throws Exception {
        BaseTestQuery.test("alter session set `store.format` = 'json'");
        final String tableName = "simple_json";
        // create a json table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, tableName);
        // drop the table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, tableName).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", tableName)).go();
    }

    @Test
    public void testDropParquetTable() throws Exception {
        final String tableName = "simple_json";
        // create a parquet table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, tableName);
        // drop the table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, tableName).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", tableName)).go();
    }

    @Test
    public void testDropTextTable() throws Exception {
        BaseTestQuery.test("alter session set `store.format` = 'csv'");
        final String csvTable = "simple_csv";
        // create a csv table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, csvTable);
        // drop the table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, csvTable).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", csvTable)).go();
        BaseTestQuery.test("alter session set `store.format` = 'psv'");
        final String psvTable = "simple_psv";
        // create a psv table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, psvTable);
        // drop the table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, psvTable).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", psvTable)).go();
        BaseTestQuery.test("alter session set `store.format` = 'tsv'");
        final String tsvTable = "simple_tsv";
        // create a tsv table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, tsvTable);
        // drop the table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, tsvTable).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", tsvTable)).go();
    }

    @Test
    public void testNonHomogenousDrop() throws Exception {
        final String tableName = "homogenous_table";
        // create a parquet table
        BaseTestQuery.test(String.format(TestDropTable.CREATE_SIMPLE_TABLE, tableName));
        // create a json table within the same directory
        BaseTestQuery.test("alter session set `store.format` = 'json'");
        final String nestedJsonTable = (tableName + (Path.SEPARATOR)) + "json_table";
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, nestedJsonTable);
        boolean dropFailed = false;
        // this should fail, because the directory contains non-homogenous files
        try {
            BaseTestQuery.test(TestDropTable.DROP_TABLE, tableName);
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("VALIDATION ERROR"));
            dropFailed = true;
        }
        Assert.assertTrue("Dropping of non-homogeneous table should have failed", dropFailed);
        // drop the individual json table
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, nestedJsonTable).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", nestedJsonTable)).go();
        // Now drop should succeed
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, tableName).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", tableName)).go();
    }

    @Test
    public void testDropOnImmutableSchema() throws Exception {
        boolean dropFailed = false;
        try {
            BaseTestQuery.test("drop table dfs.`tmp`");
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("VALIDATION ERROR"));
            dropFailed = true;
        }
        Assert.assertTrue("Dropping table on immutable schema failed", dropFailed);
    }

    // DRILL-4673
    @Test
    @Category(UnlikelyTest.class)
    public void testDropTableIfExistsWhileTableExists() throws Exception {
        final String existentTableName = "test_table_exists";
        // successful dropping of existent table
        BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, existentTableName);
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE_IF_EXISTS, existentTableName).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", existentTableName)).go();
    }

    // DRILL-4673
    @Test
    @Category(UnlikelyTest.class)
    public void testDropTableIfExistsWhileTableDoesNotExist() throws Exception {
        final String nonExistentTableName = "test_table_not_exists";
        // dropping of non existent table without error
        BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE_IF_EXISTS, nonExistentTableName).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Table [%s] not found", nonExistentTableName)).go();
    }

    // DRILL-4673
    @Test
    @Category(UnlikelyTest.class)
    public void testDropTableIfExistsWhileItIsAView() throws Exception {
        final String viewName = "test_view";
        try {
            // dropping of non existent table without error if the view with such name is existed
            BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_VIEW, viewName);
            BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE_IF_EXISTS, viewName).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Table [%s] not found", viewName)).go();
        } finally {
            BaseTestQuery.test(TestDropTable.DROP_VIEW_IF_EXISTS, viewName);
        }
    }

    @Test
    public void testDropTableNameStartsWithSlash() throws Exception {
        String tableName = "test_table_starts_with_slash_drop";
        try {
            BaseTestQuery.test(TestDropTable.CREATE_SIMPLE_TABLE, tableName);
            BaseTestQuery.testBuilder().sqlQuery(TestDropTable.DROP_TABLE, ("/" + tableName)).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", tableName)).go();
        } finally {
            BaseTestQuery.test(TestDropTable.DROP_TABLE_IF_EXISTS, tableName);
        }
    }
}

