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
package org.apache.drill.exec;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.HiveStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.expr.fn.impl.DateUtility;
import org.apache.drill.exec.hive.HiveTestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category({ SlowTest.class, HiveStorageTest.class })
public class TestHiveDrillNativeParquetReader extends HiveTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFilterPushDownForManagedTable() throws Exception {
        String query = "select * from hive.kv_native where key > 1";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, null);
    }

    @Test
    public void testFilterPushDownForExternalTable() throws Exception {
        String query = "select * from hive.kv_native_ext where key = 1";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 1, actualRowCount);
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, null);
    }

    @Test
    public void testManagedPartitionPruning() throws Exception {
        String query = "select * from hive.readtest_parquet where tinyint_part = 64";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        // Hive partition pruning is applied during logical stage
        // while convert to Drill native parquet reader during physical
        // thus plan should not contain filter
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, new String[]{ "Filter" });
    }

    @Test
    public void testExternalPartitionPruning() throws Exception {
        String query = "select `key` from hive.kv_native_ext where part_key = 2";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        // Hive partition pruning is applied during logical stage
        // while convert to Drill native parquet reader during physical
        // thus plan should not contain filter
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, new String[]{ "Filter" });
    }

    @Test
    public void testSimpleStarSubQueryFilterPushDown() throws Exception {
        String query = "select * from (select * from (select * from hive.kv_native)) where key > 1";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, null);
    }

    @Test
    public void testPartitionedExternalTable() throws Exception {
        String query = "select * from hive.kv_native_ext";
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=2" }, null);
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("key", "part_key").baselineValues(1, 1).baselineValues(2, 1).baselineValues(3, 2).baselineValues(4, 2).go();
    }

    @Test
    public void testEmptyTable() throws Exception {
        String query = "select * from hive.empty_table";
        // Hive reader should be chosen to output the schema
        testPlanMatchingPatterns(query, new String[]{ "HiveScan" }, new String[]{ "HiveDrillNativeParquetScan" });
    }

    @Test
    public void testEmptyPartition() throws Exception {
        String query = "select * from hive.kv_native_ext where part_key = 3";
        // Hive reader should be chosen to output the schema
        testPlanMatchingPatterns(query, new String[]{ "HiveScan" }, new String[]{ "HiveDrillNativeParquetScan" });
    }

    @Test
    public void testPhysicalPlanSubmission() throws Exception {
        // checks only group scan
        PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.kv_native");
        PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.kv_native_ext");
        try {
            alterSession(ExecConstants.HIVE_CONF_PROPERTIES, "hive.mapred.supports.subdirectories=true\nmapred.input.dir.recursive=true");
            PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.sub_dir_table");
        } finally {
            resetSessionOption(ExecConstants.HIVE_CONF_PROPERTIES);
        }
    }

    @Test
    public void testProjectPushDownOptimization() throws Exception {
        String query = "select boolean_field, int_part from hive.readtest_parquet";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        // partition column is named during scan as Drill partition columns
        // it will be renamed to actual value in subsequent project
        testPlanMatchingPatterns(query, new String[]{ "Project\\(boolean_field=\\[\\$0\\], int_part=\\[CAST\\(\\$1\\):INTEGER\\]\\)", "HiveDrillNativeParquetScan", "columns=\\[`boolean_field`, `dir9`\\]" }, new String[]{  });
    }

    @Test
    public void testLimitPushDownOptimization() throws Exception {
        String query = "select * from hive.kv_native limit 2";
        int actualRowCount = testSql(query);
        Assert.assertEquals("Expected and actual row count should match", 2, actualRowCount);
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numFiles=1" }, null);
    }

    @Test
    public void testConvertCountToDirectScanOptimization() throws Exception {
        String query = "select count(1) as cnt from hive.kv_native";
        testPlanMatchingPatterns(query, new String[]{ "DynamicPojoRecordReader" }, null);
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(8L).go();
    }

    @Test
    public void testImplicitColumns() throws Exception {
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.allOf(CoreMatchers.containsString("VALIDATION ERROR"), CoreMatchers.containsString("not found in any table")));
        test("select *, filename, fqn, filepath, suffix from hive.kv_native");
    }

    // DRILL-3739
    @Test
    public void testReadingFromStorageHandleBasedTable() throws Exception {
        testBuilder().sqlQuery("select * from hive.kv_sh order by key limit 2").ordered().baselineColumns("key", "value").expectsEmptyResultSet().go();
    }

    @Test
    public void testReadAllSupportedHiveDataTypesNativeParquet() throws Exception {
        String query = "select * from hive.readtest_parquet";
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan" }, null);
        // All fields are null, but partition fields have non-null values
        // There is a regression in Hive 1.2.1 in binary and boolean partition columns. Disable for now.
        // "binary",
        // There is a regression in Hive 1.2.1 in binary and boolean partition columns. Disable for now.
        // "binary",
        // There is a regression in Hive 1.2.1 in binary and boolean partition columns. Disable for now.
        // "binary_part",
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("binary_field", "boolean_field", "tinyint_field", "decimal0_field", "decimal9_field", "decimal18_field", "decimal28_field", "decimal38_field", "double_field", "float_field", "int_field", "bigint_field", "smallint_field", "string_field", "varchar_field", "timestamp_field", "char_field", "boolean_part", "tinyint_part", "decimal0_part", "decimal9_part", "decimal18_part", "decimal28_part", "decimal38_part", "double_part", "float_part", "int_part", "bigint_part", "smallint_part", "string_part", "varchar_part", "timestamp_part", "date_part", "char_part").baselineValues("binaryfield".getBytes(), false, 34, new BigDecimal("66"), new BigDecimal("2347.92"), new BigDecimal("2758725827.99990"), new BigDecimal("29375892739852.8"), new BigDecimal("89853749534593985.783"), 8.345, 4.67F, 123456, 234235L, 3455, "stringfield", "varcharfield", DateUtility.parseBest("2013-07-05 17:01:00"), "charfield", true, 64, new BigDecimal("37"), new BigDecimal("36.90"), new BigDecimal("3289379872.94565"), new BigDecimal("39579334534534.4"), new BigDecimal("363945093845093890.900"), 8.345, 4.67F, 123456, 234235L, 3455, "string", "varchar", DateUtility.parseBest("2013-07-05 17:01:00"), DateUtility.parseLocalDate("2013-07-05"), "char").baselineValues(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, true, 64, new BigDecimal("37"), new BigDecimal("36.90"), new BigDecimal("3289379872.94565"), new BigDecimal("39579334534534.4"), new BigDecimal("363945093845093890.900"), 8.345, 4.67F, 123456, 234235L, 3455, "string", "varchar", DateUtility.parseBest("2013-07-05 17:01:00"), DateUtility.parseLocalDate("2013-07-05"), "char").go();
    }

    // DRILL-3938
    @Test
    public void testNativeReaderIsDisabledForAlteredPartitionedTable() throws Exception {
        String query = "select key, `value`, newcol from hive.kv_parquet order by key limit 1";
        // Make sure the HiveScan in plan has no native parquet reader
        testPlanMatchingPatterns(query, new String[]{ "HiveScan" }, new String[]{ "HiveDrillNativeParquetScan" });
    }

    @Test
    public void testHiveConfPropertiesAtSessionLevel() throws Exception {
        String query = "select * from hive.sub_dir_table";
        try {
            alterSession(ExecConstants.HIVE_CONF_PROPERTIES, "hive.mapred.supports.subdirectories=true\nmapred.input.dir.recursive=true");
            test(query);
        } finally {
            resetSessionOption(ExecConstants.HIVE_CONF_PROPERTIES);
        }
    }

    @Test
    public void testHiveVarcharPushDown() throws Exception {
        String query = "select int_key from hive.kv_native where var_key = 'var_1'";
        Map<String, String> properties = new HashMap<>();
        properties.put("true", "numRowGroups=1");
        properties.put("false", "numRowGroups=4");// Hive creates parquet files using Parquet lib older than 1.10.0

        try {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                alterSession(ExecConstants.PARQUET_READER_STRINGS_SIGNED_MIN_MAX, property.getKey());
                testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", property.getValue() });
                testBuilder().sqlQuery(query).unOrdered().baselineColumns("int_key").baselineValues(1).go();
            }
        } finally {
            resetSessionOption(ExecConstants.PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
        }
    }

    @Test
    public void testHiveDecimalPushDown() throws Exception {
        String query = "select int_key from hive.kv_native where dec_key = cast(1.11 as decimal(5, 2))";
        // Hive generates parquet files using parquet lib older than 1.10.0
        // thus statistics for decimal is not available
        testPlanMatchingPatterns(query, new String[]{ "HiveDrillNativeParquetScan", "numRowGroups=4" });
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("int_key").baselineValues(1).go();
    }

    @Test
    public void testInt96TimestampConversionWithNativeReader() throws Exception {
        String query = "select timestamp_field from hive.readtest_parquet";
        try {
            setSessionOption(ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP, true);
            testBuilder().sqlQuery(query).unOrdered().baselineColumns("timestamp_field").baselineValues(DateUtility.parseBest("2013-07-05 17:01:00")).baselineValues(new Object[]{ null }).go();
        } finally {
            resetSessionOption(ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP);
        }
    }
}

