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


import ImmutableList.Builder;
import PlannerSettings.ENABLE_DECIMAL_DATA_TYPE_KEY;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(UnlikelyTest.class)
public class TestBugFixes extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestBugFixes.class);

    @Test
    public void leak1() throws Exception {
        String select = "select count(*) \n" + (("    from cp.`tpch/part.parquet` p1, cp.`tpch/part.parquet` p2 \n" + "    where p1.p_name = p2.p_name \n") + "  and p1.p_mfgr = p2.p_mfgr");
        BaseTestQuery.test(select);
    }

    @Test
    public void testSysDrillbits() throws Exception {
        BaseTestQuery.test("select * from sys.drillbits");
    }

    @Test
    public void testVersionTable() throws Exception {
        BaseTestQuery.test("select * from sys.version");
    }

    @Test
    public void DRILL883() throws Exception {
        BaseTestQuery.test("select n1.n_regionkey from cp.`tpch/nation.parquet` n1, (select n_nationkey from cp.`tpch/nation.parquet`) as n2 where n1.n_nationkey = n2.n_nationkey");
    }

    @Test
    public void DRILL1061() throws Exception {
        String query = "select foo.mycol.x as COMPLEX_COL from (select convert_from('{ x : [1,2], y : 100 }', 'JSON') as mycol from cp.`tpch/nation.parquet`) as foo(mycol) limit 1";
        BaseTestQuery.test(query);
    }

    @Test
    public void DRILL1126() throws Exception {
        try {
            BaseTestQuery.test(String.format("alter session set `%s` = true", ENABLE_DECIMAL_DATA_TYPE_KEY));
            String query = "select sum(cast(employee_id as decimal(38, 18))), avg(cast(employee_id as decimal(38, 18))) from cp.`employee.json` group by (department_id)";
            BaseTestQuery.test(query);
        } finally {
            BaseTestQuery.test(String.format("alter session set `%s` = false", ENABLE_DECIMAL_DATA_TYPE_KEY));
        }
    }

    /**
     * This test is not checking results because the bug fixed only appears with functions taking no arguments.
     * I could alternatively use something like the now() function, but this still would be hard to write
     * result verification for. The important aspect of the test is that it verifies that the previous IOOB
     * does not occur. The various no-argument functions should be verified in other ways.
     */
    @Test
    public void Drill3484() throws Exception {
        try {
            BaseTestQuery.test("alter SYSTEM set `drill.exec.functions.cast_empty_string_to_null` = true;");
            BaseTestQuery.test("select random() from sys.drillbits");
        } finally {
            BaseTestQuery.test("alter SYSTEM set `drill.exec.functions.cast_empty_string_to_null` = false;");
        }
    }

    // Should be "Failure while parsing sql. Node [rel#26:Subset#6.LOGICAL.ANY([]).[]] could not be implemented;".
    // Drill will hit CanNotPlan, until we add code fix to transform the local LHS filter in left outer join properly.
    @Test(expected = UserException.class)
    public void testDRILL1337_LocalLeftFilterLeftOutJoin() throws Exception {
        try {
            BaseTestQuery.test("select count(*) from cp.`tpch/nation.parquet` n left outer join cp.`tpch/region.parquet` r on n.n_regionkey = r.r_regionkey and n.n_nationkey > 10;");
        } catch (UserException e) {
            TestBugFixes.logger.info(("***** Test resulted in expected failure: " + (e.getMessage())));
            throw e;
        }
    }

    @Test
    public void testDRILL1337_LocalRightFilterLeftOutJoin() throws Exception {
        BaseTestQuery.test("select * from cp.`tpch/nation.parquet` n left outer join cp.`tpch/region.parquet` r on n.n_regionkey = r.r_regionkey and r.r_name not like '%ASIA' order by r.r_name;");
    }

    @Test
    public void testDRILL2361_AggColumnAliasWithDots() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as `test.alias` from cp.`employee.json`").unOrdered().baselineColumns("`test.alias`").baselineValues(1155L).build().run();
    }

    @Test
    public void testDRILL2361_SortColumnAliasWithDots() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select o_custkey as `x.y.z` from cp.`tpch/orders.parquet` where o_orderkey < 5 order by `x.y.z`").unOrdered().baselineColumns("`x.y.z`").baselineValues(370).baselineValues(781).baselineValues(1234).baselineValues(1369).build().run();
    }

    @Test
    public void testDRILL2361_JoinColumnAliasWithDots() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as cnt from (select o_custkey as `x.y` from cp.`tpch/orders.parquet`) o inner join cp.`tpch/customer.parquet` c on o.`x.y` = c.c_custkey").unOrdered().baselineColumns("cnt").baselineValues(15000L).build().run();
    }

    @Test
    public void testDRILL4192() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select dir0, dir1 from dfs.`bugs/DRILL-4192` order by dir1").unOrdered().baselineColumns("dir0", "dir1").baselineValues("single_top_partition", "nested_partition_1").baselineValues("single_top_partition", "nested_partition_2").go();
        BaseTestQuery.testBuilder().sqlQuery("select dir0, dir1 from dfs.`bugs/DRILL-4192/*/nested_partition_1` order by dir1").unOrdered().baselineColumns("dir0", "dir1").baselineValues("single_top_partition", "nested_partition_1").go();
    }

    @Test
    public void testDRILL4771() throws Exception {
        final String query = "select count(*) cnt, avg(distinct emp.department_id) avd\n" + " from cp.`employee.json` emp";
        final String[] expectedPlans = new String[]{ ".*Agg\\(group=\\[\\{\\}\\], cnt=\\[\\$SUM0\\(\\$1\\)\\], agg#1=\\[\\$SUM0\\(\\$0\\)\\], agg#2=\\[COUNT\\(\\$0\\)\\]\\)", ".*Agg\\(group=\\[\\{0\\}\\], cnt=\\[COUNT\\(\\)\\]\\)" };
        final String[] excludedPlans = new String[]{ ".*Join\\(condition=\\[true\\], joinType=\\[inner\\]\\).*" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlans, excludedPlans);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt", "avd").baselineValues(1155L, 10.416666666666666).build().run();
        final String query1 = "select emp.gender, count(*) cnt, avg(distinct emp.department_id) avd\n" + (" from cp.`employee.json` emp\n" + " group by gender");
        final String[] expectedPlans1 = new String[]{ ".*Agg\\(group=\\[\\{0\\}\\], cnt=\\[\\$SUM0\\(\\$2\\)\\], agg#1=\\[\\$SUM0\\(\\$1\\)\\], agg#2=\\[COUNT\\(\\$1\\)\\]\\)", ".*Agg\\(group=\\[\\{0, 1\\}\\], cnt=\\[COUNT\\(\\)\\]\\)" };
        final String[] excludedPlans1 = new String[]{ ".*Join\\(condition=\\[true\\], joinType=\\[inner\\]\\).*" };
        PlanTestBase.testPlanMatchingPatterns(query1, expectedPlans1, excludedPlans1);
        BaseTestQuery.testBuilder().sqlQuery(query1).unOrdered().baselineColumns("gender", "cnt", "avd").baselineValues("F", 601L, 10.416666666666666).baselineValues("M", 554L, 11.9).build().run();
    }

    @Test
    public void testDRILL4884() throws Exception {
        int limit = 65536;
        Builder<Map<String, Object>> baselineBuilder = ImmutableList.builder();
        for (int i = 0; i < limit; i++) {
            baselineBuilder.add(/* String.valueOf */
            Collections.<String, Object>singletonMap("`id`", (i + 1)));
        }
        List<Map<String, Object>> baseline = baselineBuilder.build();
        BaseTestQuery.testBuilder().sqlQuery("select cast(id as int) as id from cp.`bugs/DRILL-4884/limit_test_parquet/test0_0_0.parquet` group by id order by 1 limit %s", limit).unOrdered().baselineRecords(baseline).go();
    }

    @Test
    public void testDRILL5051() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(1) as cnt from (select l_orderkey from (select l_orderkey from cp.`tpch/lineitem.parquet` limit 2) limit 1 offset 1)").unOrdered().baselineColumns("cnt").baselineValues(1L).go();
    }

    // DRILL-4678
    @Test
    public void testManyDateCasts() throws Exception {
        StringBuilder query = new StringBuilder("SELECT DISTINCT dt FROM (VALUES");
        for (int i = 0; i < 50; i++) {
            query.append("(CAST('1964-03-07' AS DATE)),");
        }
        query.append("(CAST('1951-05-16' AS DATE))) tbl(dt)");
        BaseTestQuery.test(query.toString());
    }

    // DRILL-4971
    @Test
    public void testVisitBooleanOrWithoutFunctionsEvaluation() throws Exception {
        String query = "SELECT\n" + ((("CASE WHEN employee_id IN (1) THEN 1 ELSE 0 END `first`\n" + ", CASE WHEN employee_id IN (2) THEN 1 ELSE 0 END `second`\n") + ", CASE WHEN employee_id IN (1, 2) THEN 1 ELSE 0 END `any`\n") + "FROM cp.`employee.json` ORDER BY employee_id limit 2");
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("first", "second", "any").baselineValues(1, 0, 1).baselineValues(0, 1, 1).go();
    }

    // DRILL-4971
    @Test
    public void testVisitBooleanAndWithoutFunctionsEvaluation() throws Exception {
        String query = "SELECT employee_id FROM cp.`employee.json` WHERE\n" + ("((employee_id > 1 AND employee_id < 3) OR (employee_id > 9 AND employee_id < 11))\n" + "AND (employee_id > 1 AND employee_id < 3)");
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("employee_id").baselineValues(((long) (2))).go();
    }

    @Test
    public void testDRILL5269() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `planner.enable_nljoin_for_scalar_only` = false");
            BaseTestQuery.test("ALTER SESSION SET `planner.slice_target` = 500");
            BaseTestQuery.test(("\nSELECT `one` FROM (\n" + (((((((((("  SELECT 1 `one` FROM cp.`tpch/nation.parquet`\n" + "  INNER JOIN (\n") + "    SELECT 2 `two` FROM cp.`tpch/nation.parquet`\n") + "  ) `t0` ON (\n") + "    `tpch/nation.parquet`.n_regionkey IS NOT DISTINCT FROM `t0`.`two`\n") + "  )\n") + "  GROUP BY `one`\n") + ") `t1`\n") + "  INNER JOIN (\n") + "    SELECT count(1) `a_count` FROM cp.`tpch/nation.parquet`\n") + ") `t5` ON TRUE\n")));
        } finally {
            BaseTestQuery.test("ALTER SESSION RESET `planner.enable_nljoin_for_scalar_only`");
            BaseTestQuery.test("ALTER SESSION RESET `planner.slice_target`");
        }
    }

    @Test
    public void testDRILL6318() throws Exception {
        int rows = BaseTestQuery.testSql("SELECT FLATTEN(data) AS d FROM cp.`jsoninput/bug6318.json`");
        Assert.assertEquals(11, rows);
        rows = BaseTestQuery.testSql("SELECT FLATTEN(data) AS d FROM cp.`jsoninput/bug6318.json` LIMIT 3");
        Assert.assertEquals(3, rows);
        rows = BaseTestQuery.testSql("SELECT FLATTEN(data) AS d FROM cp.`jsoninput/bug6318.json` LIMIT 3 OFFSET 5");
        Assert.assertEquals(3, rows);
    }
}

