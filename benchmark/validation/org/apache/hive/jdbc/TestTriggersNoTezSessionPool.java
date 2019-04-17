/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.jdbc;


import Action.Type;
import com.google.common.collect.Lists;
import org.apache.hadoop.hive.ql.wm.Expression;
import org.apache.hadoop.hive.ql.wm.ExpressionFactory;
import org.apache.hadoop.hive.ql.wm.Trigger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class TestTriggersNoTezSessionPool extends AbstractJdbcTriggersTest {
    @Rule
    public TestName testName = new TestName();

    @Test(timeout = 60000)
    public void testTriggerSlowQueryExecutionTime() throws Exception {
        Expression expression = ExpressionFactory.fromString("EXECUTION_TIME > 1000");
        Trigger trigger = new org.apache.hadoop.hive.ql.wm.ExecutionTrigger("slow_query", expression, new org.apache.hadoop.hive.ql.wm.Action(Type.KILL_QUERY));
        setupTriggers(Lists.newArrayList(trigger));
        String query = ((("select sleep(t1.under_col, 5), t1.value from " + (AbstractJdbcTriggersTest.tableName)) + " t1 join ") + (AbstractJdbcTriggersTest.tableName)) + " t2 on t1.under_col>=t2.under_col";
        runQueryWithTrigger(query, null, (trigger + " violated"), 50);
    }

    @Test(timeout = 60000)
    public void testTriggerVertexTotalTasks() throws Exception {
        Expression expression = ExpressionFactory.fromString("VERTEX_TOTAL_TASKS > 20");
        Trigger trigger = new org.apache.hadoop.hive.ql.wm.ExecutionTrigger("highly_parallel", expression, new org.apache.hadoop.hive.ql.wm.Action(Type.KILL_QUERY));
        setupTriggers(Lists.newArrayList(trigger));
        String query = ((("select sleep(t1.under_col, 5), t1.value from " + (AbstractJdbcTriggersTest.tableName)) + " t1 join ") + (AbstractJdbcTriggersTest.tableName)) + " t2 on t1.under_col>=t2.under_col";
        runQueryWithTrigger(query, getConfigs(), (trigger + " violated"), 50);
    }

    @Test(timeout = 60000)
    public void testTriggerDAGTotalTasks() throws Exception {
        Expression expression = ExpressionFactory.fromString("DAG_TOTAL_TASKS > 20");
        Trigger trigger = new org.apache.hadoop.hive.ql.wm.ExecutionTrigger("highly_parallel", expression, new org.apache.hadoop.hive.ql.wm.Action(Type.KILL_QUERY));
        setupTriggers(Lists.newArrayList(trigger));
        String query = ((("select sleep(t1.under_col, 5), t1.value from " + (AbstractJdbcTriggersTest.tableName)) + " t1 join ") + (AbstractJdbcTriggersTest.tableName)) + " t2 on t1.under_col>=t2.under_col";
        runQueryWithTrigger(query, getConfigs(), (trigger + " violated"), 50);
    }

    @Test(timeout = 60000)
    public void testTriggerTotalLaunchedTasks() throws Exception {
        Expression expression = ExpressionFactory.fromString("TOTAL_LAUNCHED_TASKS > 20");
        Trigger trigger = new org.apache.hadoop.hive.ql.wm.ExecutionTrigger("highly_parallel", expression, new org.apache.hadoop.hive.ql.wm.Action(Type.KILL_QUERY));
        setupTriggers(Lists.newArrayList(trigger));
        String query = ((("select sleep(t1.under_col, 5), t1.value from " + (AbstractJdbcTriggersTest.tableName)) + " t1 join ") + (AbstractJdbcTriggersTest.tableName)) + " t2 on t1.under_col>=t2.under_col";
        runQueryWithTrigger(query, getConfigs(), (trigger + " violated"), 50);
    }
}
