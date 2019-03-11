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


import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MajorType;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.BIT;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.INTERVALDAY;
import TypeProtos.MinorType.TIMESTAMP;
import TypeProtos.MinorType.VARCHAR;
import Types.MAX_VARCHAR_LENGTH;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlFunctionTest.class)
public class TestFunctionsWithTypeExpoQueries extends BaseTestQuery {
    @Test
    public void testConcatWithMoreThanTwoArgs() throws Exception {
        final String query = "select concat(r_name, r_name, r_name, \'f\') as col \n" + "from cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(MAX_VARCHAR_LENGTH).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testRow_NumberInView() throws Exception {
        try {
            BaseTestQuery.test("use dfs.tmp;");
            BaseTestQuery.test(("create view TestFunctionsWithTypeExpoQueries_testViewShield1 as \n" + (((("select rnum, position_id, " + "   ntile(4) over(order by position_id) ") + " from (select position_id, row_number() ") + "       over(order by position_id) as rnum ") + "       from cp.`employee.json`)")));
            BaseTestQuery.test(("create view TestFunctionsWithTypeExpoQueries_testViewShield2 as \n" + ((("select row_number() over(order by position_id) as rnum, " + "    position_id, ") + "    ntile(4) over(order by position_id) ") + " from cp.`employee.json`")));
            BaseTestQuery.testBuilder().sqlQuery("select * from TestFunctionsWithTypeExpoQueries_testViewShield1").ordered().sqlBaselineQuery("select * from TestFunctionsWithTypeExpoQueries_testViewShield2").build().run();
        } finally {
            BaseTestQuery.test("drop view TestFunctionsWithTypeExpoQueries_testViewShield1;");
            BaseTestQuery.test("drop view TestFunctionsWithTypeExpoQueries_testViewShield2;");
        }
    }

    @Test
    public void testLRBTrimOneArg() throws Exception {
        final String query1 = "SELECT ltrim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query2 = "SELECT rtrim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query3 = "SELECT btrim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(MAX_VARCHAR_LENGTH).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query1).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query2).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query3).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testTrim() throws Exception {
        final String query1 = "SELECT trim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query2 = "SELECT trim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query3 = "SELECT trim('drill') as col FROM cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(MAX_VARCHAR_LENGTH).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query1).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query2).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query3).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testTrimOneArg() throws Exception {
        final String query1 = "SELECT trim(leading 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query2 = "SELECT trim(trailing 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query3 = "SELECT trim(both 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(MAX_VARCHAR_LENGTH).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query1).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query2).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query3).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testTrimTwoArg() throws Exception {
        final String query1 = "SELECT trim(leading ' ' from 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query2 = "SELECT trim(trailing ' ' from 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        final String query3 = "SELECT trim(both ' ' from 'drill') as col FROM cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(MAX_VARCHAR_LENGTH).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query1).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query2).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query3).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testIsNull() throws Exception {
        final String query = "select r_name is null as col from cp.`tpch/region.parquet` limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    /**
     * In the following query, the extract function would be borrowed from Calcite,
     * which asserts the return type as be BIG-INT
     */
    @Test
    public void testExtractSecond() throws Exception {
        String query = "select extract(second from time \'02:30:45.100\') as col \n" + ("from cp.`tpch/region.parquet` \n" + "limit 0");
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(FLOAT8).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testMetaDataExposeType() throws Exception {
        final String query = "select count(*) as col " + ("from dfs.`typeExposure/metadata_caching` " + "where concat(a, 'asdf') = 'asdf'");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Scan.*a.parquet.*numFiles = 1" };
        final String[] excludedPlan = new String[]{ "Filter" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPlan);
        // Validate the result
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("col").baselineValues(1L).build().run();
    }

    @Test
    public void testDate_Part() throws Exception {
        final String query = "select date_part('year', date '2008-2-23') as col " + ("from cp.`tpch/region.parquet` " + "limit 0");
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testNegativeByInterpreter() throws Exception {
        final String query = "select * from cp.`tpch/region.parquet` " + "where r_regionkey = negative(-1)";
        // Validate the plan
        final String[] expectedPlan = new String[]{ "Filter.*condition=\\[=\\(.*, 1\\)\\]\\)" };
        final String[] excludedPlan = new String[]{  };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPlan);
    }

    @Test
    public void testSumRequiredType() throws Exception {
        final String query = "SELECT " + ((("SUM(CASE WHEN (CAST(n_regionkey AS INT) = 1) THEN 1 ELSE 0 END) AS col " + "FROM cp.`tpch/nation.parquet` ") + "GROUP BY CAST(n_regionkey AS INT) ") + "limit 0");
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testSQRTDecimalLiteral() throws Exception {
        final String query = "SELECT sqrt(5.1) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(FLOAT8).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testSQRTIntegerLiteral() throws Exception {
        final String query = "SELECT sqrt(4) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(FLOAT8).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testTimestampDiff() throws Exception {
        final String query = "select to_timestamp('2014-02-13 00:30:30','YYYY-MM-dd HH:mm:ss') - to_timestamp('2014-02-13 00:30:30','YYYY-MM-dd HH:mm:ss') as col " + ("from cp.`tpch/region.parquet` " + "limit 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(INTERVALDAY).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testEqualBetweenIntervalAndTimestampDiff() throws Exception {
        final String query = "select to_timestamp('2016-11-02 10:00:00','YYYY-MM-dd HH:mm:ss') + interval '10-11' year to month as col " + (("from cp.`tpch/region.parquet` " + "where (to_timestamp('2016-11-02 10:00:00','YYYY-MM-dd HH:mm:ss') - to_timestamp('2016-01-01 10:00:00','YYYY-MM-dd HH:mm:ss') < interval '5 10:00:00' day to second) ") + "limit 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(TIMESTAMP).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testAvgAndSUM() throws Exception {
        final String query = "SELECT AVG(cast(r_regionkey as float)) AS `col1`, " + (((("SUM(cast(r_regionkey as float)) AS `col2`, " + "SUM(1) AS `col3` ") + "FROM cp.`tpch/region.parquet` ") + "GROUP BY CAST(r_regionkey AS INTEGER) ") + "LIMIT 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType1 = MajorType.newBuilder().setMinorType(FLOAT8).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType2 = MajorType.newBuilder().setMinorType(FLOAT8).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType3 = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col1"), majorType1));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col2"), majorType2));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col3"), majorType3));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testAvgCountStar() throws Exception {
        final String query = "select avg(distinct cast(r_regionkey as bigint)) + avg(cast(r_regionkey as integer)) as col1, " + ((("sum(distinct cast(r_regionkey as bigint)) + 100 as col2, count(*) as col3 " + "from cp.`tpch/region.parquet` alltypes_v ") + "where cast(r_regionkey as bigint) = 100000000000000000 ") + "limit 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType1 = MajorType.newBuilder().setMinorType(FLOAT8).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType2 = MajorType.newBuilder().setMinorType(BIGINT).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType3 = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col1"), majorType1));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col2"), majorType2));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col3"), majorType3));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testUDFInGroupBy() throws Exception {
        final String query = "select count(*) as col1, substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2) as col2, " + (((((((("char_length(substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2)) as col3 " + "from cp.`tpch/region.parquet` t1 ") + "left outer join cp.`tpch/nation.parquet` t2 on cast(t1.r_regionkey as Integer) = cast(t2.n_nationkey as Integer) ") + "left outer join cp.`employee.json` t3 on cast(t1.r_regionkey as Integer) = cast(t3.employee_id as Integer) ") + "group by substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2), ") + "char_length(substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2)) ") + "order by substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2),") + "char_length(substr(lower(UPPER(cast(t3.full_name as varchar(100)))), 5, 2)) ") + "limit 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType1 = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        final TypeProtos.MajorType majorType2 = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).setPrecision(MAX_VARCHAR_LENGTH).build();
        final TypeProtos.MajorType majorType3 = MajorType.newBuilder().setMinorType(BIGINT).setMode(OPTIONAL).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col1"), majorType1));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col2"), majorType2));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col3"), majorType3));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testWindowSumAvg() throws Exception {
        final String query = "with query as ( " + ((((("select sum(cast(employee_id as integer)) over w as col1, cast(avg(cast(employee_id as bigint)) over w as double precision) as col2, count(*) over w as col3 " + "from cp.`tpch/region.parquet` ") + "window w as (partition by cast(full_name as varchar(10)) order by cast(full_name as varchar(10)) nulls first)) ") + "select * ") + "from query ") + "limit 0");
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        final TypeProtos.MajorType majorType1 = MajorType.newBuilder().setMinorType(BIGINT).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType2 = MajorType.newBuilder().setMinorType(FLOAT8).setMode(OPTIONAL).build();
        final TypeProtos.MajorType majorType3 = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col1"), majorType1));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col2"), majorType2));
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col3"), majorType3));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testWindowRanking() throws Exception {
        final String queryCUME_DIST = "select CUME_DIST() over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final String queryDENSE_RANK = "select DENSE_RANK() over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final String queryPERCENT_RANK = "select PERCENT_RANK() over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final String queryRANK = "select RANK() over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final String queryROW_NUMBER = "select ROW_NUMBER() over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final TypeProtos.MajorType majorTypeDouble = MajorType.newBuilder().setMinorType(FLOAT8).setMode(REQUIRED).build();
        final TypeProtos.MajorType majorTypeBigInt = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchemaCUME_DIST = Lists.newArrayList();
        expectedSchemaCUME_DIST.add(Pair.of(SchemaPath.getSimplePath("col"), majorTypeDouble));
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchemaDENSE_RANK = Lists.newArrayList();
        expectedSchemaDENSE_RANK.add(Pair.of(SchemaPath.getSimplePath("col"), majorTypeBigInt));
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchemaPERCENT_RANK = Lists.newArrayList();
        expectedSchemaPERCENT_RANK.add(Pair.of(SchemaPath.getSimplePath("col"), majorTypeDouble));
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchemaRANK = Lists.newArrayList();
        expectedSchemaRANK.add(Pair.of(SchemaPath.getSimplePath("col"), majorTypeBigInt));
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchemaROW_NUMBER = Lists.newArrayList();
        expectedSchemaROW_NUMBER.add(Pair.of(SchemaPath.getSimplePath("col"), majorTypeBigInt));
        BaseTestQuery.testBuilder().sqlQuery(queryCUME_DIST).schemaBaseLine(expectedSchemaCUME_DIST).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryDENSE_RANK).schemaBaseLine(expectedSchemaDENSE_RANK).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryPERCENT_RANK).schemaBaseLine(expectedSchemaPERCENT_RANK).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryRANK).schemaBaseLine(expectedSchemaRANK).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryROW_NUMBER).schemaBaseLine(expectedSchemaROW_NUMBER).build().run();
    }

    @Test
    public void testWindowNTILE() throws Exception {
        final String query = "select ntile(1) over(order by position_id) as col " + ("from cp.`employee.json` " + "limit 0");
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(INT).setMode(REQUIRED).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testLeadLag() throws Exception {
        final String queryLEAD = "select lead(cast(n_nationkey as BigInt)) over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final String queryLAG = "select lag(cast(n_nationkey as BigInt)) over(order by n_nationkey) as col " + ("from cp.`tpch/nation.parquet` " + "limit 0");
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIGINT).setMode(OPTIONAL).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(queryLEAD).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryLAG).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testFirst_Last_Value() throws Exception {
        final String queryFirst = "select first_value(cast(position_id as integer)) over(order by position_id) as col " + ("from cp.`employee.json` " + "limit 0");
        final String queryLast = "select first_value(cast(position_id as integer)) over(order by position_id) as col " + ("from cp.`employee.json` " + "limit 0");
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(INT).setMode(OPTIONAL).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(queryFirst).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(queryLast).schemaBaseLine(expectedSchema).build().run();
    }

    // DRILL-4529
    @Test
    public void testWindowSumConstant() throws Exception {
        final String query = "select sum(1) over w as col " + ("from cp.`tpch/region.parquet` " + "window w as (partition by r_regionkey)");
        final String[] expectedPlan = new String[]{ "\\$SUM0" };
        final String[] excludedPlan = new String[]{  };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPlan);
    }

    // DRILL-4525
    @Test
    public void testBetweenDateAndTimeStamp() throws Exception {
        final String query = "select count(*) as col \n" + (("from cp.`employee.json` \n" + "where cast(birth_date as DATE) BETWEEN cast(\'1970-01-01\' AS DATE) AND (cast(\'1999-01-01\' AS DATE) + INTERVAL \'60\' day) \n") + "limit 0");
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = new ArrayList<>();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }

    // DRILL-4525
    @Test
    public void testBetweenDecimalAndDouble() throws Exception {
        final String query = "select cast(r_regionkey as Integer) as col \n" + (("from cp.`tpch/region.parquet` \n" + "where cast(r_regionkey as double) BETWEEN 1.1 AND 4.5 \n") + "limit 0");
        final TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(INT).setMode(OPTIONAL).build();
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = new ArrayList<>();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
    }
}

