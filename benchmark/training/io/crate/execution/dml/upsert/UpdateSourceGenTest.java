/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.dml.upsert;


import io.crate.analyze.AnalyzedUpdateStatement;
import io.crate.expression.symbol.Assignments;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.SearchPath;
import io.crate.metadata.TransactionContext;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.Collections;
import java.util.Map;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UpdateSourceGenTest extends CrateDummyClusterServiceUnitTest {
    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testSetXBasedOnXAndPartitionedColumn() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addPartitionedTable("create table t (x int, p int) partitioned by (p)", asIndexName()).build();
        AnalyzedUpdateStatement update = e.analyze("update t set x = x + p");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        DocTableInfo table = ((DocTableInfo) (update.table().tableInfo()));
        UpdateSourceGen updateSourceGen = new UpdateSourceGen(e.functions(), txnCtx, table, assignments.targetNames());
        Map<String, Object> source = Collections.singletonMap("x", 1);
        BytesReference updatedSource = updateSourceGen.generateSource(new io.crate.expression.reference.Doc(table.concreteIndices()[0], "1", 1, source, () -> {
            try {
                return Strings.toString(XContentFactory.jsonBuilder().map(source));
            } catch ( e1) {
                throw new <e1>RuntimeException();
            }
        }), assignments.sources(), new Object[0]);
        assertThat(updatedSource.utf8ToString(), Matchers.is("{\"x\":2}"));
    }

    @Test
    public void testSourceGenerationWithAssignmentUsingDocumentPrimaryKey() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addTable("create table t (y int)").build();
        AnalyzedUpdateStatement update = e.analyze("update t set y = _id::integer * 2");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        DocTableInfo table = ((DocTableInfo) (update.table().tableInfo()));
        UpdateSourceGen updateSourceGen = new UpdateSourceGen(e.functions(), txnCtx, table, assignments.targetNames());
        BytesReference source = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().field("y", 100).endObject());
        BytesReference updatedSource = updateSourceGen.generateSource(new io.crate.expression.reference.Doc(table.concreteIndices()[0], "4", 1, Collections.emptyMap(), source::utf8ToString), assignments.sources(), new Object[0]);
        assertThat(updatedSource.utf8ToString(), Matchers.is("{\"y\":8}"));
    }

    @Test
    public void testNestedGeneratedColumnIsGenerated() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addTable("create table t (x int, obj object as (y as x + 1))").build();
        AnalyzedUpdateStatement update = e.analyze("update t set x = 4");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        DocTableInfo table = ((DocTableInfo) (update.table().tableInfo()));
        UpdateSourceGen updateSourceGen = new UpdateSourceGen(e.functions(), txnCtx, table, assignments.targetNames());
        BytesReference updatedSource = updateSourceGen.generateSource(new io.crate.expression.reference.Doc(table.concreteIndices()[0], "1", 1, Collections.emptyMap(), () -> "{}"), assignments.sources(), new Object[0]);
        assertThat(updatedSource.utf8ToString(), Matchers.is("{\"obj\":{\"y\":5},\"x\":4}"));
    }

    @Test
    public void testGeneratedColumnUsingFunctionDependingOnActiveTransaction() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addTable("create table t (x int, gen as current_schema)").build();
        AnalyzedUpdateStatement update = e.analyze("update t set x = 1");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        DocTableInfo table = ((DocTableInfo) (update.table().tableInfo()));
        UpdateSourceGen sourceGen = new UpdateSourceGen(e.functions(), TransactionContext.of("dummyUser", SearchPath.createSearchPathFrom("dummySchema")), table, assignments.targetNames());
        BytesReference source = sourceGen.generateSource(new io.crate.expression.reference.Doc(table.concreteIndices()[0], "1", 1, Collections.emptyMap(), () -> "{}"), assignments.sources(), new Object[0]);
        assertThat(source.utf8ToString(), Matchers.is("{\"gen\":\"dummySchema\",\"x\":1}"));
    }
}
