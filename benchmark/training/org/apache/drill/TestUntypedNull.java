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


import ExecConstants.OUTPUT_FORMAT_OPTION;
import TypeProtos.MajorType;
import TypeProtos.MinorType.NULL;
import java.util.Arrays;
import java.util.List;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlFunctionTest.class)
public class TestUntypedNull extends ClusterTest {
    private static final MajorType UNTYPED_NULL_TYPE = Types.optional(NULL);

    @Test
    public void testSplitFunction() throws Exception {
        String query = "select split(n_name, \' \') [1] from cp.`tpch/nation.parquet`\n" + "where n_nationkey = -1 group by n_name order by n_name limit 10";
        QueryBuilder.QuerySummary summary = queryBuilder().sql(query).run();
        Assert.assertTrue(summary.succeeded());
        Assert.assertEquals(0, summary.recordCount());
    }

    @Test
    public void testWindowFunction() throws Exception {
        String query = "select row_number() over (partition by split(n_name, \' \') [1])\n" + "from cp.`tpch/nation.parquet` where n_nationkey = -1";
        QueryBuilder.QuerySummary summary = queryBuilder().sql(query).run();
        Assert.assertTrue(summary.succeeded());
        Assert.assertEquals(0, summary.recordCount());
    }

    @Test
    public void testUnion() throws Exception {
        String query = "select split(n_name, \' \') [1] from cp.`tpch/nation.parquet` where n_nationkey = -1 group by n_name\n" + ("union\n" + "select split(n_name, ' ') [1] from cp.`tpch/nation.parquet` where n_nationkey = -1 group by n_name");
        QueryBuilder.QuerySummary summary = queryBuilder().sql(query).run();
        Assert.assertTrue(summary.succeeded());
        Assert.assertEquals(0, summary.recordCount());
    }

    @Test
    public void testTableCreation() throws Exception {
        String tablePrefix = "table_";
        List<String> formats = Arrays.asList("parquet", "json", "csv");
        try {
            for (String format : formats) {
                ClusterTest.client.alterSession(OUTPUT_FORMAT_OPTION, format);
                String query = String.format(("create table dfs.tmp.%s%s as\n" + "select split(n_name, ' ') [1] from cp.`tpch/nation.parquet` where n_nationkey = -1 group by n_name"), tablePrefix, format);
                QueryBuilder.QuerySummary summary = queryBuilder().sql(query).run();
                Assert.assertTrue(summary.succeeded());
                Assert.assertEquals(1, summary.recordCount());
            }
        } finally {
            ClusterTest.client.resetSession(OUTPUT_FORMAT_OPTION);
            for (String format : formats) {
                queryBuilder().sql(String.format("drop table if exists dfs.tmp.%s%s", tablePrefix, format)).run();
            }
        }
    }

    @Test
    public void testTypeAndMode() throws Exception {
        String query = "select\n" + ((((("typeof(split(n_name, \' \') [1]),\n" + "drilltypeof(split(n_name, \' \') [1]),\n") + "sqltypeof(split(n_name, \' \') [1]),\n") + "modeof(split(n_name, \' \') [1])\n") + "from cp.`tpch/nation.parquet`\n") + "where n_nationkey = -1");
        QueryBuilder.QuerySummary summary = queryBuilder().sql(query).run();
        Assert.assertTrue(summary.succeeded());
        Assert.assertEquals(0, summary.recordCount());
    }

    @Test
    public void testCoalesceOnNotExistentColumns() throws Exception {
        String query = "select coalesce(unk1, unk2) as coal from cp.`tpch/nation.parquet` limit 5";
        BatchSchema expectedSchema = new SchemaBuilder().add("coal", TestUntypedNull.UNTYPED_NULL_TYPE).build();
        testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).go();
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("coal").baselineValuesForSingleColumn(null, null, null, null, null).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsWithGroupBy() throws Exception {
        String query = "select coalesce(unk1, unk2) as coal from cp.`tpch/nation.parquet` group by 1";
        BatchSchema expectedSchema = new SchemaBuilder().add("coal", TestUntypedNull.UNTYPED_NULL_TYPE).build();
        testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).go();
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("coal").baselineValuesForSingleColumn(new Object[]{ null }).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsWithOrderBy() throws Exception {
        String query = "select coalesce(unk1, unk2) as coal from cp.`tpch/nation.parquet` order by 1 limit 5";
        BatchSchema expectedSchema = new SchemaBuilder().add("coal", TestUntypedNull.UNTYPED_NULL_TYPE).build();
        testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).go();
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("coal").baselineValuesForSingleColumn(null, null, null, null, null).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsWithCoalesceInWhereClause() throws Exception {
        String query = "select coalesce(unk1, unk2) as coal from cp.`tpch/nation.parquet` where coalesce(unk1, unk2) > 10";
        testBuilder().sqlQuery(query).unOrdered().expectsNumRecords(0).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsWithCoalesceInHavingClause() throws Exception {
        String query = "select 1 from cp.`tpch/nation.parquet` group by n_name having count(coalesce(unk1, unk2)) > 10";
        testBuilder().sqlQuery(query).unOrdered().expectsNumRecords(0).go();
    }

    @Test
    public void testPartitionByCoalesceOnNotExistentColumns() throws Exception {
        String query = "select row_number() over (partition by coalesce(unk1, unk2)) as row_num from cp.`tpch/nation.parquet` limit 5";
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("row_num").baselineValuesForSingleColumn(1L, 2L, 3L, 4L, 5L).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsInUDF() throws Exception {
        String query = "select substr(coalesce(unk1, unk2), 1, 2) as coal from cp.`tpch/nation.parquet` limit 5";
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("coal").baselineValuesForSingleColumn(null, null, null, null, null).go();
    }

    @Test
    public void testCoalesceOnNotExistentColumnsInUDF2() throws Exception {
        String query = "select abs(coalesce(unk1, unk2)) as coal from cp.`tpch/nation.parquet` limit 5";
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("coal").baselineValuesForSingleColumn(null, null, null, null, null).go();
    }
}

