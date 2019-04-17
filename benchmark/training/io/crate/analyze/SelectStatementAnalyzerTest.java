/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import AnyOperators.Names.EQ;
import ArithmeticFunctions.Names.ADD;
import ArrayType.ID;
import CastFunctionResolver.FunctionNames.TO_STRING;
import CastFunctionResolver.FunctionNames.TO_STRING_ARRAY;
import DataTypes.DOUBLE;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import FunctionInfo.Type;
import FunctionInfo.Type.AGGREGATE;
import IntegerType.INSTANCE;
import NotPredicate.NAME;
import SelectSymbol.ResultType.SINGLE_COLUMN_MULTIPLE_VALUES;
import SymbolType.LITERAL;
import WhereClause.MATCH_ALL;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.relations.AnalyzedRelation;
import io.crate.analyze.relations.QueriedRelation;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.exceptions.ConversionException;
import io.crate.exceptions.RelationUnknown;
import io.crate.exceptions.UnsupportedFeatureException;
import io.crate.expression.scalar.cast.CastFunctionResolver;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.SelectSymbol;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.udf.UserDefinedFunctionService;
import io.crate.metadata.FunctionInfo;
import io.crate.metadata.Functions;
import io.crate.metadata.RelationName;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.metadata.doc.DocTableInfoFactory;
import io.crate.metadata.doc.TestingDocTableInfoFactory;
import io.crate.metadata.table.TestingTableInfo;
import io.crate.sql.parser.ParsingException;
import io.crate.sql.tree.QualifiedName;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.TestingHelpers;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;


@SuppressWarnings("ConstantConditions")
public class SelectStatementAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor sqlExecutor;

    @Test
    public void testIsNullQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where id is not null");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is(NAME));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Function.class));
        Function isNull = ((Function) (query.arguments().get(0)));
        assertThat(isNull.info().ident().name(), Is.is(IsNullPredicate.NAME));
    }

    @Test
    public void testQueryUsesSearchPath() throws IOException {
        SQLExecutor executor = SQLExecutor.builder(clusterService).setSearchPath("first", "second", "third").addDocTable(TestingTableInfo.builder(new RelationName("first", "t"), TableDefinitions.SHARD_ROUTING).add("id", INSTANCE).build()).addDocTable(TestingTableInfo.builder(new RelationName("third", "t1"), TableDefinitions.SHARD_ROUTING).add("id", INSTANCE).build()).build();
        QueriedTable queriedTable = executor.analyze("select * from t");
        assertThat(queriedTable.getQualifiedName().getParts().get(0), Is.is("first"));
        queriedTable = executor.analyze("select * from t1");
        assertThat(queriedTable.getQualifiedName().getParts().get(0), Is.is("third"));
    }

    @Test
    public void testOrderedSelect() throws Exception {
        QueriedTable table = ((QueriedTable) (analyze("select load['1'] from sys.nodes order by load['5'] desc")));
        assertThat(table.querySpec().limit(), Matchers.nullValue());
        assertThat(table.querySpec().groupBy().isEmpty(), Is.is(true));
        assertThat(table.querySpec().orderBy(), Matchers.notNullValue());
        assertThat(table.querySpec().outputs().size(), Is.is(1));
        assertThat(table.querySpec().orderBy().orderBySymbols().size(), Is.is(1));
        assertThat(table.querySpec().orderBy().reverseFlags().length, Is.is(1));
        assertThat(table.querySpec().orderBy().orderBySymbols().get(0), SymbolMatchers.isReference("load['5']"));
    }

    @Test
    public void testNegativeLiteral() throws Exception {
        QueriedRelation relation = analyze("select * from sys.nodes where port['http'] = -400");
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        Symbol symbol = whereClause.arguments().get(1);
        assertThat(value(), Is.is((-400)));
    }

    @Test
    public void testSimpleSelect() throws Exception {
        QueriedRelation relation = analyze("select load['5'] from sys.nodes limit 2");
        assertThat(relation.querySpec().limit(), Is.is(Literal.of(2L)));
        assertThat(relation.querySpec().groupBy().isEmpty(), Is.is(true));
        assertThat(relation.querySpec().outputs().size(), Is.is(1));
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isReference("load['5']"));
    }

    @Test
    public void testAggregationSelect() throws Exception {
        QueriedRelation relation = analyze("select avg(load['5']) from sys.nodes");
        assertThat(relation.querySpec().groupBy().isEmpty(), Is.is(true));
        assertThat(relation.querySpec().outputs().size(), Is.is(1));
        Function col1 = ((Function) (relation.querySpec().outputs().get(0)));
        assertThat(col1.info().type(), Is.is(AGGREGATE));
        assertThat(col1.info().ident().name(), Is.is(AverageAggregation.NAME));
    }

    @Test
    public void testAllColumnCluster() throws Exception {
        QueriedRelation relation = analyze("select * from sys.cluster");
        assertThat(relation.fields().size(), Is.is(5));
        assertThat(outputNames(relation), Matchers.containsInAnyOrder("id", "license", "master_node", "name", "settings"));
        assertThat(relation.querySpec().outputs().size(), Is.is(5));
    }

    @Test
    public void testAllColumnNodes() throws Exception {
        QueriedRelation relation = analyze("select id, * from sys.nodes");
        List<String> outputNames = outputNames(relation);
        assertThat(outputNames, Matchers.contains("id", "cluster_state_version", "connections", "fs", "heap", "hostname", "id", "load", "mem", "name", "network", "os", "os_info", "port", "process", "rest_url", "thread_pools", "version"));
        assertThat(relation.querySpec().outputs().size(), Is.is(outputNames.size()));
    }

    @Test
    public void testWhereSelect() throws Exception {
        QueriedRelation relation = analyze(("select load from sys.nodes " + "where load['1'] = 1.2 or 1 >= load['5']"));
        assertThat(relation.querySpec().groupBy().isEmpty(), Is.is(true));
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertThat(whereClause.info().ident().name(), Is.is(OrOperator.NAME));
        assertThat(((whereClause.info().type()) == (Type.AGGREGATE)), Is.is(false));
        Function left = ((Function) (whereClause.arguments().get(0)));
        assertThat(left.info().ident().name(), Is.is(EqOperator.NAME));
        assertThat(left.arguments().get(0), SymbolMatchers.isReference("load['1']"));
        assertThat(left.arguments().get(1), IsInstanceOf.instanceOf(Literal.class));
        assertThat(left.arguments().get(1).valueType(), Is.is(DOUBLE));
        Function right = ((Function) (whereClause.arguments().get(1)));
        assertThat(right.info().ident().name(), Is.is(LteOperator.NAME));
        assertThat(right.arguments().get(0), SymbolMatchers.isReference("load['5']"));
        assertThat(right.arguments().get(1), IsInstanceOf.instanceOf(Literal.class));
        assertThat(left.arguments().get(1).valueType(), Is.is(DOUBLE));
    }

    @Test
    public void testSelectWithParameters() throws Exception {
        QueriedRelation relation = analyze(("select load from sys.nodes " + ("where load['1'] = ? or load['5'] <= ? or load['15'] >= ? or load['1'] = ? " + "or load['1'] = ? or name = ?")), new Object[]{ 1.2, 2.4F, 2L, 3, new Short("1"), "node 1" });
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertThat(whereClause.info().ident().name(), Is.is(OrOperator.NAME));
        assertThat(((whereClause.info().type()) == (Type.AGGREGATE)), Is.is(false));
        Function function = ((Function) (whereClause.arguments().get(0)));
        assertThat(function.info().ident().name(), Is.is(OrOperator.NAME));
        function = ((Function) (function.arguments().get(1)));
        assertThat(function.info().ident().name(), Is.is(EqOperator.NAME));
        assertThat(function.arguments().get(1), IsInstanceOf.instanceOf(Literal.class));
        assertThat(function.arguments().get(1).valueType(), Is.is(DOUBLE));
        function = ((Function) (whereClause.arguments().get(1)));
        assertThat(function.info().ident().name(), Is.is(EqOperator.NAME));
        assertThat(function.arguments().get(1), IsInstanceOf.instanceOf(Literal.class));
        assertThat(function.arguments().get(1).valueType(), Is.is(STRING));
    }

    @Test
    public void testOutputNames() throws Exception {
        QueriedRelation relation = analyze("select load as l, id, load['1'] from sys.nodes");
        List<String> outputNames = outputNames(relation);
        assertThat(outputNames.size(), Is.is(3));
        assertThat(outputNames.get(0), Is.is("l"));
        assertThat(outputNames.get(1), Is.is("id"));
        assertThat(outputNames.get(2), Is.is("load['1']"));
    }

    @Test
    public void testDuplicateOutputNames() throws Exception {
        QueriedRelation relation = analyze("select load as l, load['1'] as l from sys.nodes");
        List<String> outputNames = outputNames(relation);
        assertThat(outputNames.size(), Is.is(2));
        assertThat(outputNames.get(0), Is.is("l"));
        assertThat(outputNames.get(1), Is.is("l"));
    }

    @Test
    public void testOrderByOnAlias() throws Exception {
        QueriedRelation relation = analyze("select name as cluster_name from sys.cluster order by cluster_name");
        List<String> outputNames = outputNames(relation);
        assertThat(outputNames.size(), Is.is(1));
        assertThat(outputNames.get(0), Is.is("cluster_name"));
        assertThat(relation.querySpec().orderBy(), Matchers.notNullValue());
        assertThat(relation.querySpec().orderBy().orderBySymbols().size(), Is.is(1));
        assertThat(relation.querySpec().orderBy().orderBySymbols().get(0), Is.is(relation.querySpec().outputs().get(0)));
    }

    @Test
    public void testSelectGlobalAggregationOrderByWithColumnMissingFromSelect() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage(("ORDER BY expression 'id' must appear in the select clause " + "when grouping or global aggregation is used"));
        analyze("select count(id) from users order by id");
    }

    @Test
    public void testValidCombinationsOrderByWithAggregation() throws Exception {
        analyze("select name, count(id) from users group by name order by 1");
        analyze("select name, count(id) from users group by name order by 2");
        analyze("select name, count(id) from users group by name order by name");
        analyze("select name, count(id) from users group by name order by count(id)");
        analyze("select name, count(id) from users group by name order by lower(name)");
        analyze("select name, count(id) from users group by name order by lower(upper(name))");
        analyze("select name, count(id) from users group by name order by sin(count(id))");
        analyze("select name, count(id) from users group by name order by sin(sqrt(count(id)))");
    }

    @Test
    public void testOffsetSupportInAnalyzer() throws Exception {
        QueriedRelation relation = analyze("select * from sys.nodes limit 1 offset 3");
        assertThat(relation.querySpec().offset(), Is.is(Literal.of(3L)));
    }

    @Test
    public void testNoMatchStatement() throws Exception {
        for (String stmt : ImmutableList.of("select id from sys.nodes where false", "select id from sys.nodes where 1=0")) {
            QueriedRelation relation = analyze(stmt);
            assertThat(stmt, relation.querySpec().where().noMatch(), Is.is(true));
            assertThat(stmt, relation.querySpec().where().hasQuery(), Is.is(false));
        }
    }

    @Test
    public void testEvaluatingMatchAllStatement() throws Exception {
        QueriedRelation relation = analyze("select id from sys.nodes where 1 = 1");
        assertThat(relation.querySpec().where().noMatch(), Is.is(false));
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
    }

    @Test
    public void testAllMatchStatement() throws Exception {
        for (String stmt : ImmutableList.of("select id from sys.nodes where true", "select id from sys.nodes where 1=1", "select id from sys.nodes")) {
            QueriedRelation relation = analyze(stmt);
            assertThat(stmt, relation.querySpec().where().noMatch(), Is.is(false));
            assertThat(stmt, relation.querySpec().where().hasQuery(), Is.is(false));
        }
    }

    @Test
    public void testRewriteNotEquals() {
        // should rewrite to:
        // not(eq(sys.noes.name, 'something'))
        ImmutableList<String> statements = ImmutableList.of("select * from sys.nodes where sys.nodes.name <> 'something'", "select * from sys.nodes where sys.nodes.name != 'something'");
        for (String statement : statements) {
            QueriedRelation relation = analyze(statement);
            WhereClause whereClause = relation.querySpec().where();
            Function notFunction = ((Function) (whereClause.query()));
            assertThat(notFunction.info().ident().name(), Is.is(NAME));
            assertThat(notFunction.arguments().size(), Is.is(1));
            Function eqFunction = ((Function) (notFunction.arguments().get(0)));
            assertThat(eqFunction.info().ident().name(), Is.is(EqOperator.NAME));
            assertThat(eqFunction.arguments().size(), Is.is(2));
            List<Symbol> eqArguments = eqFunction.arguments();
            assertThat(eqArguments.get(1), SymbolMatchers.isLiteral("something"));
        }
    }

    @Test
    public void testRewriteRegexpNoMatch() throws Exception {
        String statement = "select * from sys.nodes where sys.nodes.name !~ '[sS]omething'";
        QueriedRelation relation = analyze(statement);
        WhereClause whereClause = relation.querySpec().where();
        Function notFunction = ((Function) (whereClause.query()));
        assertThat(notFunction.info().ident().name(), Is.is(NAME));
        assertThat(notFunction.arguments().size(), Is.is(1));
        Function eqFunction = ((Function) (notFunction.arguments().get(0)));
        assertThat(eqFunction.info().ident().name(), Is.is(RegexpMatchOperator.NAME));
        assertThat(eqFunction.arguments().size(), Is.is(2));
        List<Symbol> eqArguments = eqFunction.arguments();
        assertThat(eqArguments.get(0), SymbolMatchers.isReference("name"));
        assertThat(eqArguments.get(1), SymbolMatchers.isLiteral("[sS]omething"));
    }

    @Test
    public void testGranularityWithSingleAggregation() throws Exception {
        QueriedTable table = ((QueriedTable) (analyze("select count(*) from sys.nodes")));
        assertEquals(table.tableRelation().tableInfo().ident(), SysNodesTableInfo.IDENT);
    }

    @Test
    public void testRewriteCountStringLiteral() {
        QueriedRelation relation = analyze("select count('id') from sys.nodes");
        List<Symbol> outputSymbols = relation.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(Function.class));
        assertThat(arguments().size(), Is.is(0));
    }

    @Test
    public void testRewriteCountNull() {
        QueriedRelation relation = analyze("select count(null) from sys.nodes");
        List<Symbol> outputSymbols = relation.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(Literal.class));
        assertThat(value(), Is.is(0L));
    }

    @Test
    public void testWhereInSelect() throws Exception {
        QueriedRelation relation = analyze("select load from sys.nodes where load['1'] in (1.0, 2.0, 4.0, 8.0, 16.0)");
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertThat(whereClause.info().ident().name(), Is.is(EQ));
    }

    @Test
    public void testWhereInSelectListWithNull() throws Exception {
        QueriedRelation relation = analyze("select 'found' from users where 1 in (3, 2, null)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testWhereInSelectValueIsNull() throws Exception {
        QueriedRelation relation = analyze("select 'found' from users where null in (1, 2)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testWhereInSelectDifferentDataTypeValue() throws Exception {
        QueriedRelation relation;
        relation = analyze("select 'found' from users where 1.2 in (1, 2)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));// already normalized to 1.2 in (1.0, 2.0) --> false

        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
        relation = analyze("select 'found' from users where 1 in (1.2, 2)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testWhereInSelectDifferentDataTypeValueIncompatibleDataTypes() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast 'foo' to type long");
        analyze("select 'found' from users where 1 in (1, 'foo', 2)");
    }

    @Test
    public void testAggregationDistinct() {
        QueriedRelation relation = analyze("select count(distinct load['1']) from sys.nodes");
        assertThat(relation.querySpec().hasAggregates(), Is.is(true));
        Symbol output = relation.querySpec().outputs().get(0);
        assertThat(output, SymbolMatchers.isFunction("collection_count"));
        Function collectionCount = ((Function) (output));
        assertThat(collectionCount.arguments().size(), Is.is(1));
        Symbol symbol = collectionCount.arguments().get(0);
        assertThat(symbol, SymbolMatchers.isFunction("collect_set"));
        Function collectSet = ((Function) (symbol));
        assertThat(collectSet.info().type(), Matchers.equalTo(AGGREGATE));
        assertThat(collectSet.arguments().size(), Is.is(1));
        assertThat(collectSet.arguments().get(0), SymbolMatchers.isReference("load['1']"));
    }

    @Test
    public void testSelectDistinctWithFunction() {
        QueriedRelation relation = analyze("select distinct id + 1 from users");
        assertThat(relation.isDistinct(), Is.is(true));
        assertThat(relation.querySpec().outputs(), TestingHelpers.isSQL("add(doc.users.id, 1)"));
    }

    @Test
    public void testSelectDistinctWithGroupBySameFieldsSameOrder() {
        QueriedRelation distinctRelation = analyze("select distinct id, name from users group by id, name");
        QueriedRelation groupByRelation = analyze("select id, name from users group by id, name");
        assertThat(distinctRelation.querySpec().groupBy(), Matchers.equalTo(groupByRelation.querySpec().groupBy()));
        assertThat(distinctRelation.querySpec().outputs(), Matchers.equalTo(groupByRelation.querySpec().outputs()));
    }

    @Test
    public void testSelectDistinctWithGroupBySameFieldsDifferentOrder() {
        QueriedRelation relation = analyze("select distinct name, id from users group by id, name");
        assertThat(relation.querySpec(), TestingHelpers.isSQL("SELECT doc.users.name, doc.users.id GROUP BY doc.users.id, doc.users.name"));
    }

    @Test
    public void testDistinctOnLiteral() {
        QueriedRelation relation = analyze("select distinct [1,2,3] from users");
        assertThat(relation.isDistinct(), Is.is(true));
        assertThat(relation.querySpec().outputs(), TestingHelpers.isSQL("[1, 2, 3]"));
    }

    @Test
    public void testDistinctOnNullLiteral() {
        QueriedRelation relation = analyze("select distinct null from users");
        assertThat(relation.isDistinct(), Is.is(true));
        assertThat(relation.querySpec().outputs(), TestingHelpers.isSQL("NULL"));
    }

    @Test
    public void testSelectGlobalDistinctAggregate() {
        QueriedRelation relation = analyze("select distinct count(*) from users");
        assertThat(relation.querySpec().groupBy().isEmpty(), Is.is(true));
    }

    @Test
    public void testSelectGlobalDistinctRewriteAggregationGroupBy() {
        QueriedRelation distinctRelation = analyze("select distinct name, count(id) from users group by name");
        QueriedRelation groupByRelation = analyze("select name, count(id) from users group by name");
        assertEquals(groupByRelation.querySpec().groupBy(), distinctRelation.querySpec().groupBy());
    }

    @Test
    public void testSelectWithObjectLiteral() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1.0);
        map.put("5", 2.5);
        map.put("15", 8.0);
        QueriedRelation relation = analyze("select id from sys.nodes where load=?", new Object[]{ map });
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertThat(whereClause.arguments().get(1), Matchers.instanceOf(Literal.class));
        assertThat(value().equals(map), Is.is(true));
    }

    @Test
    public void testLikeInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where name like 'foo'");
        assertNotNull(relation.querySpec().where());
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertThat(whereClause.info().ident().name(), Is.is(LikeOperator.NAME));
        ImmutableList<DataType> argumentTypes = ImmutableList.of(STRING, STRING);
        assertEquals(argumentTypes, whereClause.info().ident().argumentTypes());
        assertThat(whereClause.arguments().get(0), SymbolMatchers.isReference("name"));
        assertThat(whereClause.arguments().get(1), SymbolMatchers.isLiteral("foo"));
    }

    @Test
    public void testLikeEscapeInWhereQuery() {
        // ESCAPE is not supported yet
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("ESCAPE is not supported.");
        analyze("select * from sys.nodes where name like 'foo' escape 'o'");
    }

    @Test
    public void testLikeNoStringDataTypeInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where name like 1");
        // check if the implicit cast of the pattern worked
        ImmutableList<DataType> argumentTypes = ImmutableList.of(STRING, STRING);
        Function whereClause = ((Function) (relation.querySpec().where().query()));
        assertEquals(argumentTypes, whereClause.info().ident().argumentTypes());
        assertThat(whereClause.arguments().get(1), IsInstanceOf.instanceOf(Literal.class));
        Literal stringLiteral = ((Literal) (whereClause.arguments().get(1)));
        assertThat(stringLiteral.value(), Is.is("1"));
    }

    @Test
    public void testLikeLongDataTypeInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where 1 like 2");
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testIsNullInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where name is null");
        Function isNullFunction = ((Function) (relation.querySpec().where().query()));
        assertThat(isNullFunction.info().ident().name(), Is.is(IsNullPredicate.NAME));
        assertThat(isNullFunction.arguments().size(), Is.is(1));
        assertThat(isNullFunction.arguments().get(0), SymbolMatchers.isReference("name"));
        assertNotNull(relation.querySpec().where());
    }

    @Test
    public void testNullIsNullInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where null is null");
        assertThat(relation.querySpec().where(), Is.is(MATCH_ALL));
    }

    @Test
    public void testLongIsNullInWhereQuery() {
        QueriedRelation relation = analyze("select * from sys.nodes where 1 is null");
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testNotPredicate() {
        QueriedRelation relation = analyze("select * from users where name not like 'foo%'");
        assertThat(info().ident().name(), Is.is(NAME));
    }

    @Test
    public void testFilterByLiteralBoolean() throws Exception {
        QueriedRelation relation = analyze("select * from users where awesome=TRUE");
        assertThat(arguments().get(1).symbolType(), Is.is(LITERAL));
    }

    @Test
    public void testSelectColumnWitoutFromResultsInColumnUnknownException() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column name unknown");
        analyze("select 'bar', name");
    }

    @Test
    public void test2From() throws Exception {
        QueriedRelation relation = analyze("select a.name from users a, users b");
        assertThat(relation, Matchers.instanceOf(MultiSourceSelect.class));
    }

    @Test
    public void testLimitWithWrongArgument() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast 'invalid' to type long");
        analyze("select * from sys.shards limit ?", new Object[]{ "invalid" });
    }

    @Test
    public void testOrderByQualifiedName() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'doc.friends' unknown");
        analyze("select * from users order by friends.id");
    }

    @Test
    public void testNotTimestamp() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast date to type boolean");
        analyze("select id, name from parted where not date");
    }

    @Test
    public void testJoin() throws Exception {
        QueriedRelation relation = analyze("select * from users, users_multi_pk where users.id = users_multi_pk.id");
        assertThat(relation, Matchers.instanceOf(MultiSourceSelect.class));
    }

    @Test
    public void testInnerJoinSyntaxDoesNotExtendsWhereClause() throws Exception {
        MultiSourceSelect mss = ((MultiSourceSelect) (analyze("select * from users inner join users_multi_pk on users.id = users_multi_pk.id")));
        assertThat(mss.querySpec().where().query(), TestingHelpers.isSQL("null"));
        assertThat(mss.joinPairs().get(0).condition(), TestingHelpers.isSQL("(doc.users.id = doc.users_multi_pk.id)"));
    }

    @Test
    public void testJoinSyntaxWithMoreThan2Tables() throws Exception {
        MultiSourceSelect relation = ((MultiSourceSelect) (analyze(("select * from users u1 " + ("join users_multi_pk u2 on u1.id = u2.id " + "join users_clustered_by_only u3 on u2.id = u3.id ")))));
        assertThat(relation.querySpec().where().query(), TestingHelpers.isSQL("null"));
        assertThat(relation.joinPairs().get(0).condition(), TestingHelpers.isSQL("(u1.id = u2.id)"));
        assertThat(relation.joinPairs().get(1).condition(), TestingHelpers.isSQL("(u2.id = u3.id)"));
    }

    @Test
    public void testCrossJoinWithJoinCondition() throws Exception {
        expectedException.expect(ParsingException.class);
        analyze("select * from users cross join users_multi_pk on users.id = users_multi_pk.id");
    }

    @Test
    public void testJoinUsingSyntax() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        analyze("select * from users join users_multi_pk using (id)");
    }

    @Test
    public void testNaturalJoinSyntax() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        analyze("select * from users natural join users_multi_pk");
    }

    @Test
    public void testInnerJoinSyntaxWithWhereClause() throws Exception {
        MultiSourceSelect relation = ((MultiSourceSelect) (analyze(("select * from users join users_multi_pk on users.id = users_multi_pk.id " + "where users.name = 'Arthur'"))));
        assertThat(relation.joinPairs().get(0).condition(), TestingHelpers.isSQL("(doc.users.id = doc.users_multi_pk.id)"));
        // make sure that where clause was pushed down and didn't disappear somehow
        assertThat(relation.querySpec().where().query(), TestingHelpers.isSQL("null"));
        QueriedRelation users = ((QueriedRelation) (relation.sources().get(QualifiedName.of("doc.users"))));
        assertThat(users.querySpec().where().query(), TestingHelpers.isSQL("(doc.users.name = 'Arthur')"));
    }

    @Test
    public void testJoinWithOrderBy() throws Exception {
        QueriedRelation relation = analyze("select users.id from users, users_multi_pk order by users.id");
        assertThat(relation, Matchers.instanceOf(MultiSourceSelect.class));
        MultiSourceSelect mss = ((MultiSourceSelect) (relation));
        assertThat(mss.querySpec().orderBy(), TestingHelpers.isSQL("doc.users.id"));
        Iterator<Map.Entry<QualifiedName, AnalyzedRelation>> it = mss.sources().entrySet().iterator();
        QueriedRelation usersRel = ((QueriedRelation) (it.next().getValue()));
        assertThat(usersRel.querySpec().orderBy(), Matchers.nullValue());
    }

    @Test
    public void testJoinWithOrderByOnCount() throws Exception {
        QueriedRelation relation = analyze(("select count(*) from users u1, users_multi_pk u2 " + "order by 1"));
        MultiSourceSelect mss = ((MultiSourceSelect) (relation));
        assertThat(mss.querySpec().orderBy(), TestingHelpers.isSQL("count()"));
    }

    @Test
    public void testJoinWithMultiRelationOrderBy() throws Exception {
        QueriedRelation relation = analyze("select u1.id from users u1, users_multi_pk u2 order by u2.id, u1.name || u2.name");
        assertThat(relation, Matchers.instanceOf(MultiSourceSelect.class));
        MultiSourceSelect mss = ((MultiSourceSelect) (relation));
        QueriedRelation u1 = ((QueriedRelation) (mss.sources().values().iterator().next()));
        assertThat(u1.querySpec(), TestingHelpers.isSQL("SELECT doc.users.id, doc.users.name"));
    }

    @Test
    public void testJoinConditionIsNotPartOfOutputs() throws Exception {
        QueriedRelation rel = analyze("select u1.name from users u1 inner join users u2 on u1.id = u2.id order by u2.date");
        MultiSourceSelect mss = ((MultiSourceSelect) (rel));
        assertThat(rel.querySpec().outputs(), Matchers.contains(SymbolMatchers.isField("name")));
    }

    @Test
    public void testUnionDistinct() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("UNION [DISTINCT] is not supported");
        analyze("select * from users union select * from users_multi_pk");
    }

    @Test
    public void testIntersect() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("INTERSECT is not supported");
        analyze("select * from users intersect select * from users_multi_pk");
    }

    @Test
    public void testExcept() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("EXCEPT is not supported");
        analyze("select * from users except select * from users_multi_pk");
    }

    @Test
    public void testArrayCompareInvalidArray() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast name to type [undefined_set, undefined_array]");
        analyze("select * from users where 'George' = ANY (name)");
    }

    @Test
    public void testArrayCompareObjectArray() throws Exception {
        QueriedRelation relation = analyze("select * from users where ? = ANY (friends)", new Object[]{ new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("id", 1L).map() });
        assertThat(relation.where().queryOrFallback(), Is.is(SymbolMatchers.isFunction("any_=")));
    }

    @Test
    public void testArrayCompareAny() throws Exception {
        QueriedRelation relation = analyze("select * from users where 0 = ANY (counters)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        FunctionInfo anyInfo = ((Function) (relation.querySpec().where().query())).info();
        assertThat(anyInfo.ident().name(), Is.is("any_="));
        relation = analyze("select * from users where 0 = ANY (counters)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        anyInfo = info();
        assertThat(anyInfo.ident().name(), Is.is("any_="));
    }

    @Test
    public void testArrayCompareAnyNeq() throws Exception {
        QueriedRelation relation = analyze("select * from users where ? != ANY (counters)", new Object[]{ 4.3F });
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        FunctionInfo anyInfo = ((Function) (relation.querySpec().where().query())).info();
        assertThat(anyInfo.ident().name(), Is.is("any_<>"));
    }

    @Test
    public void testArrayCompareAll() throws Exception {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("ALL is not supported");
        analyze("select * from users where 0 = ALL (counters)");
    }

    @Test
    public void testImplicitContainmentOnObjectArrayFields() throws Exception {
        // users.friends is an object array,
        // so its fields are selected as arrays,
        // ergo simple comparison does not work here
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast 5 to type long_array");
        analyze("select * from users where 5 = friends['id']");
    }

    @Test
    public void testAnyOnObjectArrayField() throws Exception {
        QueriedRelation relation = analyze("select * from users where 5 = ANY (friends['id'])");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function anyFunction = ((Function) (relation.querySpec().where().query()));
        assertThat(anyFunction.info().ident().name(), Is.is(EQ));
        assertThat(anyFunction.arguments().get(1), SymbolMatchers.isReference("friends['id']", new ArrayType(DataTypes.LONG)));
        assertThat(anyFunction.arguments().get(0), SymbolMatchers.isLiteral(5L));
    }

    @Test
    public void testAnyOnArrayInObjectArray() throws Exception {
        QueriedRelation relation = analyze("select * from users where ['vogon lyric lovers'] = ANY (friends['groups'])");
        assertThat(relation.where().queryOrFallback(), SymbolMatchers.isFunction("any_=", SymbolMatchers.isLiteral(new Object[]{ "vogon lyric lovers" }, new ArrayType(DataTypes.STRING)), SymbolMatchers.isReference("friends['groups']", new ArrayType(new ArrayType(DataTypes.STRING)))));
    }

    @Test
    public void testTableAliasWrongUse() throws Exception {
        expectedException.expect(RelationUnknown.class);
        // caused by where users.awesome, would have to use where u.awesome = true instead
        expectedException.expectMessage("Relation 'doc.users' unknown");
        analyze("select * from users as u where users.awesome = true");
    }

    @Test
    public void testTableAliasFullQualifiedName() throws Exception {
        expectedException.expect(RelationUnknown.class);
        // caused by where users.awesome, would have to use where u.awesome = true instead
        expectedException.expectMessage("Relation 'doc.users' unknown");
        analyze("select * from users as u where doc.users.awesome = true");
    }

    @Test
    public void testAliasSubscript() throws Exception {
        QueriedRelation relation = analyze("select u.friends['id'] from users as u");
        assertThat(relation.querySpec().outputs().size(), Is.is(1));
        Symbol s = relation.querySpec().outputs().get(0);
        assertThat(s, Matchers.notNullValue());
        assertThat(s, SymbolMatchers.isReference("friends['id']"));
    }

    @Test
    public void testOrderByWithOrdinal() throws Exception {
        QueriedRelation relation = analyze("select name from users u order by 1");
        assertEquals(relation.querySpec().outputs().get(0), relation.querySpec().orderBy().orderBySymbols().get(0));
    }

    @Test
    public void testOrderByOnArray() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot ORDER BY 'friends': invalid data type 'object_array'.");
        analyze("select * from users order by friends");
    }

    @Test
    public void testOrderByOnObject() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot ORDER BY 'load': invalid data type 'object'.");
        analyze("select * from sys.nodes order by load");
    }

    @Test
    public void testArithmeticPlus() throws Exception {
        QueriedRelation relation = analyze("select load['1'] + load['5'] from sys.nodes");
        assertThat(info().ident().name(), Is.is(ADD));
    }

    @Test
    public void testPrefixedNumericLiterals() throws Exception {
        QueriedRelation relation = analyze("select - - - 10");
        List<Symbol> outputs = relation.querySpec().outputs();
        assertThat(outputs.get(0), Is.is(Literal.of((-10L))));
        relation = analyze("select - + - 10");
        outputs = relation.querySpec().outputs();
        assertThat(outputs.get(0), Is.is(Literal.of(10L)));
        relation = analyze("select - (- 10 - + 10) * - (+ 10 + - 10)");
        outputs = relation.querySpec().outputs();
        assertThat(outputs.get(0), Is.is(Literal.of(0L)));
    }

    @Test
    public void testAnyLike() throws Exception {
        QueriedRelation relation = analyze("select * from users where 'awesome' LIKE ANY (tags)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is("any_like"));
        assertThat(query.arguments().size(), Is.is(2));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        assertThat(query.arguments().get(0), SymbolMatchers.isLiteral("awesome", STRING));
        assertThat(query.arguments().get(1), SymbolMatchers.isReference("tags"));
    }

    @Test
    public void testAnyLikeLiteralMatchAll() throws Exception {
        QueriedRelation relation = analyze("select * from users where 'awesome' LIKE ANY (['a', 'b', 'awesome'])");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(false));
    }

    @Test
    public void testAnyLikeLiteralNoMatch() throws Exception {
        QueriedRelation relation = analyze("select * from users where 'awesome' LIKE ANY (['a', 'b'])");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testAnyNotLike() throws Exception {
        QueriedRelation relation = analyze("select * from users where 'awesome' NOT LIKE ANY (tags)");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is("any_not_like"));
        assertThat(query.arguments().size(), Is.is(2));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        assertThat(query.arguments().get(0), SymbolMatchers.isLiteral("awesome", STRING));
        assertThat(query.arguments().get(1), SymbolMatchers.isReference("tags"));
    }

    @Test
    public void testAnyLikeInvalidArray() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast name to type [undefined_set, undefined_array]");
        analyze("select * from users where 'awesome' LIKE ANY (name)");
    }

    @Test
    public void testPositionalArgumentOrderByArrayType() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot ORDER BY 'friends': invalid data type 'object_array'.");
        analyze("SELECT id, friends FROM users ORDER BY 2");
    }

    @Test
    public void testOrderByDistanceAlias() throws Exception {
        String stmt = "SELECT distance(loc, 'POINT(-0.1275 51.507222)') AS distance_to_london " + ("FROM locations " + "ORDER BY distance_to_london");
        testDistanceOrderBy(stmt);
    }

    @Test
    public void testOrderByDistancePositionalArgument() throws Exception {
        String stmt = "SELECT distance(loc, 'POINT(-0.1275 51.507222)') " + ("FROM locations " + "ORDER BY 1");
        testDistanceOrderBy(stmt);
    }

    @Test
    public void testOrderByDistanceExplicitly() throws Exception {
        String stmt = "SELECT distance(loc, 'POINT(-0.1275 51.507222)') " + ("FROM locations " + "ORDER BY distance(loc, 'POINT(-0.1275 51.507222)')");
        testDistanceOrderBy(stmt);
    }

    @Test
    public void testOrderByDistancePermutatedExplicitly() throws Exception {
        String stmt = "SELECT distance('POINT(-0.1275 51.507222)', loc) " + ("FROM locations " + "ORDER BY distance('POINT(-0.1275 51.507222)', loc)");
        testDistanceOrderBy(stmt);
    }

    @Test
    public void testWhereMatchOnColumn() throws Exception {
        QueriedRelation relation = analyze("select * from users where match(name, 'Arthur Dent')");
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is("match"));
        assertThat(query.arguments().size(), Is.is(4));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        // noinspection unchecked
        Literal<Map<String, Object>> idents = ((Literal<Map<String, Object>>) (query.arguments().get(0)));
        assertThat(idents.value().size(), Is.is(1));
        assertThat(idents.value().get("name"), Is.is(Matchers.nullValue()));
        assertThat(query.arguments().get(1), Matchers.instanceOf(Literal.class));
        assertThat(query.arguments().get(1), SymbolMatchers.isLiteral("Arthur Dent", STRING));
        assertThat(query.arguments().get(2), SymbolMatchers.isLiteral("best_fields", STRING));
        // noinspection unchecked
        Literal<Map<String, Object>> options = ((Literal<Map<String, Object>>) (query.arguments().get(3)));
        assertThat(options.value(), Matchers.instanceOf(Map.class));
        assertThat(options.value().size(), Is.is(0));
    }

    @Test
    public void testForbidJoinWhereMatchOnBothTables() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Cannot use MATCH predicates on columns of 2 different relations " + "if it cannot be logically applied on each of them separately"));
        analyze(("select * from users u1, users_multi_pk u2 " + "where match(u1.name, 'Lanistas experimentum!') or match(u2.name, 'Rationes ridetis!')"));
    }

    @Test
    public void testMatchOnIndex() throws Exception {
        QueriedRelation relation = analyze("select * from users where match(name_text_ft, 'Arthur Dent')");
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is("match"));
        assertThat(query.arguments().size(), Is.is(4));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        // noinspection unchecked
        Literal<Map<String, Object>> idents = ((Literal<Map<String, Object>>) (query.arguments().get(0)));
        assertThat(idents.value().size(), Is.is(1));
        assertThat(idents.value().get("name_text_ft"), Is.is(Matchers.nullValue()));
        assertThat(query.arguments().get(1), Matchers.instanceOf(Literal.class));
        assertThat(query.arguments().get(1), SymbolMatchers.isLiteral("Arthur Dent", STRING));
        assertThat(query.arguments().get(2), SymbolMatchers.isLiteral("best_fields", STRING));
        // noinspection unchecked
        Literal<Map<String, Object>> options = ((Literal<Map<String, Object>>) (query.arguments().get(3)));
        assertThat(options.value(), Matchers.instanceOf(Map.class));
        assertThat(options.value().size(), Is.is(0));
    }

    @Test
    public void testMatchOnDynamicColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column details['me_not_exizzt'] unknown");
        analyze("select * from users where match(details['me_not_exizzt'], 'Arthur Dent')");
    }

    @Test
    public void testMatchPredicateInResultColumnList() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("match predicate cannot be selected");
        analyze("select match(name, 'bar') from users");
    }

    @Test
    public void testMatchPredicateInGroupByClause() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("match predicate cannot be used in a GROUP BY clause");
        analyze("select count(*) from users group by MATCH(name, 'bar')");
    }

    @Test
    public void testMatchPredicateInOrderByClause() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("match predicate cannot be used in an ORDER BY clause");
        analyze("select name from users order by match(name, 'bar')");
    }

    @Test
    public void testMatchPredicateWithWrongQueryTerm() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast {} to type string");
        analyze("select name from users order by match(name, {})");
    }

    @Test
    public void testSelectWhereSimpleMatchPredicate() throws Exception {
        QueriedRelation relation = analyze("select * from users where match (text, 'awesome')");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is(MatchPredicate.NAME));
        assertThat(query.arguments().size(), Is.is(4));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        // noinspection unchecked
        Literal<Map<String, Object>> idents = ((Literal<Map<String, Object>>) (query.arguments().get(0)));
        assertThat(idents.value().keySet(), Matchers.hasItem("text"));
        assertThat(idents.value().get("text"), Is.is(Matchers.nullValue()));
        assertThat(query.arguments().get(1), Matchers.instanceOf(Literal.class));
        assertThat(query.arguments().get(1), SymbolMatchers.isLiteral("awesome", STRING));
    }

    @Test
    public void testSelectWhereFullMatchPredicate() throws Exception {
        QueriedRelation relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using best_fields with (analyzer='german')"));
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        Function query = ((Function) (relation.querySpec().where().query()));
        assertThat(query.info().ident().name(), Is.is(MatchPredicate.NAME));
        assertThat(query.arguments().size(), Is.is(4));
        assertThat(query.arguments().get(0), Matchers.instanceOf(Literal.class));
        // noinspection unchecked
        Literal<Map<String, Object>> idents = ((Literal<Map<String, Object>>) (query.arguments().get(0)));
        assertThat(idents.value().size(), Is.is(2));
        assertThat(idents.value().get("name"), Is.is(1.2));
        assertThat(idents.value().get("text"), Is.is(Matchers.nullValue()));
        assertThat(query.arguments().get(1), SymbolMatchers.isLiteral("awesome", STRING));
        assertThat(query.arguments().get(2), SymbolMatchers.isLiteral("best_fields", STRING));
        // noinspection unchecked
        Literal<Map<String, Object>> options = ((Literal<Map<String, Object>>) (query.arguments().get(3)));
        Map<String, Object> map = options.value();
        replaceBytesRefWithString(map);
        assertThat(map.size(), Is.is(1));
        assertThat(map.get("analyzer"), Is.is("german"));
    }

    @Test
    public void testWhereMatchUnknownType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid MATCH type 'some_fields'");
        analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using some_fields"));
    }

    @Test
    public void testUnknownSubscriptInSelectList() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column o['no_such_column'] unknown");
        analyze("select o['no_such_column'] from users");
    }

    @Test
    public void testUnknownSubscriptInQuery() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column o['no_such_column'] unknown");
        analyze("select * from users where o['no_such_column'] is not null");
    }

    @Test
    public void testWhereMatchAllowedTypes() throws Exception {
        QueriedRelation best_fields_relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using best_fields"));
        QueriedRelation most_fields_relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using most_fields"));
        QueriedRelation cross_fields_relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using cross_fields"));
        QueriedRelation phrase_relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using phrase"));
        QueriedRelation phrase_prefix_relation = analyze(("select * from users " + "where match ((name 1.2, text), 'awesome') using phrase_prefix"));
        assertThat(getMatchType(((Function) (best_fields_relation.querySpec().where().query()))), Is.is("best_fields"));
        assertThat(getMatchType(((Function) (most_fields_relation.querySpec().where().query()))), Is.is("most_fields"));
        assertThat(getMatchType(((Function) (cross_fields_relation.querySpec().where().query()))), Is.is("cross_fields"));
        assertThat(getMatchType(((Function) (phrase_relation.querySpec().where().query()))), Is.is("phrase"));
        assertThat(getMatchType(((Function) (phrase_prefix_relation.querySpec().where().query()))), Is.is("phrase_prefix"));
    }

    @Test
    public void testWhereMatchAllOptions() throws Exception {
        QueriedRelation relation = analyze(("select * from users " + ((((((((((((((("where match ((name 1.2, text), 'awesome') using best_fields with " + "(") + "  analyzer='german',") + "  boost=4.6,") + "  tie_breaker=0.75,") + "  operator='or',") + "  minimum_should_match=4,") + "  fuzziness=12,") + "  max_expansions=3,") + "  prefix_length=4,") + "  rewrite='constant_score_boolean',") + "  fuzzy_rewrite='top_terms_20',") + "  zero_terms_query='all',") + "  cutoff_frequency=5,") + "  slop=3") + ")")));
        Function match = ((Function) (relation.querySpec().where().query()));
        // noinspection unchecked
        Map<String, Object> options = ((Literal<Map<String, Object>>) (match.arguments().get(3))).value();
        replaceBytesRefWithString(options);
        assertThat(TestingHelpers.mapToSortedString(options), Is.is(("analyzer=german, boost=4.6, cutoff_frequency=5, " + (("fuzziness=12, fuzzy_rewrite=top_terms_20, max_expansions=3, minimum_should_match=4, " + "operator=or, prefix_length=4, rewrite=constant_score_boolean, slop=3, tie_breaker=0.75, ") + "zero_terms_query=all"))));
    }

    @Test
    public void testHavingWithoutGroupBy() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("HAVING clause can only be used in GROUP BY or global aggregate queries");
        analyze("select * from users having max(bytes) > 100");
    }

    @Test
    public void testGlobalAggregateHaving() throws Exception {
        QueriedRelation relation = analyze("select sum(floats) from users having sum(bytes) in (42, 43, 44)");
        Function havingFunction = ((Function) (relation.querySpec().having().query()));
        // assert that the in was converted to or
        assertThat(havingFunction.info().ident().name(), Is.is(EQ));
    }

    @Test
    public void testGlobalAggregateReference() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use column bytes outside of an Aggregation in HAVING clause. Only GROUP BY keys allowed here.");
        analyze("select sum(floats) from users having bytes in (42, 43, 44)");
    }

    @Test
    public void testScoreReferenceInvalidComparison() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where \"_score\" = 0.9");
    }

    @Test
    public void testScoreReferenceComparisonWithColumn() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where \"_score\" >= id::float");
    }

    @Test
    public void testScoreReferenceInvalidNotPredicate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where not \"_score\" >= 0.9");
    }

    @Test
    public void testScoreReferenceInvalidLikePredicate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where \"_score\" in (0.9)");
    }

    @Test
    public void testScoreReferenceInvalidNullPredicate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where \"_score\" is null");
    }

    @Test
    public void testScoreReferenceInvalidNotNullPredicate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("System column '_score' can only be used within a '>=' comparison without any surrounded predicate");
        analyze("select * from users where \"_score\" is not null");
    }

    @Test
    public void testRegexpMatchInvalidArg() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast floats to type string");
        analyze("select * from users where floats ~ 'foo'");
    }

    @Test
    public void testRegexpMatchNull() throws Exception {
        QueriedRelation relation = analyze("select * from users where name ~ null");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(false));
        assertThat(relation.querySpec().where().noMatch(), Is.is(true));
    }

    @Test
    public void testRegexpMatch() throws Exception {
        QueriedRelation relation = analyze("select * from users where name ~ '.*foo(bar)?'");
        assertThat(relation.querySpec().where().hasQuery(), Is.is(true));
        assertThat(info().ident().name(), Is.is("op_~"));
    }

    @Test
    public void testSubscriptArray() throws Exception {
        QueriedRelation relation = analyze("select tags[1] from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(SubscriptFunction.NAME));
        List<Symbol> arguments = ((Function) (relation.querySpec().outputs().get(0))).arguments();
        assertThat(arguments.size(), Is.is(2));
        assertThat(arguments.get(0), SymbolMatchers.isReference("tags"));
        assertThat(arguments.get(1), SymbolMatchers.isLiteral(1));
    }

    @Test
    public void testSubscriptArrayInvalidIndexMin() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Array index must be in range 1 to 2147483648");
        analyze("select tags[0] from users");
    }

    @Test
    public void testSubscriptArrayInvalidIndexMax() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Array index must be in range 1 to 2147483648");
        analyze("select tags[2147483649] from users");
    }

    @Test
    public void testSubscriptArrayNested() throws Exception {
        QueriedRelation relation = analyze("select tags[1]['name'] from deeply_nested");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(SubscriptFunction.NAME));
        List<Symbol> arguments = ((Function) (relation.querySpec().outputs().get(0))).arguments();
        assertThat(arguments.size(), Is.is(2));
        assertThat(arguments.get(0), SymbolMatchers.isReference("tags['name']"));
        assertThat(arguments.get(1), SymbolMatchers.isLiteral(1));
    }

    @Test
    public void testSubscriptArrayInvalidNesting() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Nested array access is not supported");
        analyze("select tags[1]['metadata'][2] from deeply_nested");
    }

    @Test
    public void testSubscriptArrayAsAlias() throws Exception {
        QueriedRelation relation = analyze("select tags[1] as t_alias from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(SubscriptFunction.NAME));
        List<Symbol> arguments = ((Function) (relation.querySpec().outputs().get(0))).arguments();
        assertThat(arguments.size(), Is.is(2));
        assertThat(arguments.get(0), SymbolMatchers.isReference("tags"));
        assertThat(arguments.get(1), SymbolMatchers.isLiteral(1));
    }

    @Test
    public void testSubscriptArrayOnScalarResult() throws Exception {
        QueriedRelation relation = analyze("select regexp_matches(name, '.*')[1] as t_alias from users order by t_alias");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(SubscriptFunction.NAME));
        assertThat(relation.querySpec().orderBy().orderBySymbols().get(0), Is.is(relation.querySpec().outputs().get(0)));
        List<Symbol> arguments = ((Function) (relation.querySpec().outputs().get(0))).arguments();
        assertThat(arguments.size(), Is.is(2));
        assertThat(arguments.get(0), SymbolMatchers.isFunction(MatchesFunction.NAME));
        assertThat(arguments.get(1), SymbolMatchers.isLiteral(1));
        List<Symbol> scalarArguments = ((Function) (arguments.get(0))).arguments();
        assertThat(scalarArguments.size(), Is.is(2));
        assertThat(scalarArguments.get(0), SymbolMatchers.isReference("name"));
        assertThat(scalarArguments.get(1), SymbolMatchers.isLiteral(".*", STRING));
    }

    @Test
    public void testParameterSubcriptColumn() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Parameter substitution is not supported in subscript");
        analyze("select friends[?] from users", new Object[]{ "id" });
    }

    @Test
    public void testParameterSubscriptLiteral() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Parameter substitution is not supported in subscript");
        analyze("select ['a','b','c'][?] from users", new Object[2]);
    }

    @Test
    public void testArraySubqueryExpression() throws Exception {
        QueriedRelation relation = analyze("select array(select id from sys.shards) as shards_id_array from sys.shards");
        SelectSymbol arrayProjection = ((SelectSymbol) (relation.querySpec().outputs().get(0)));
        assertThat(arrayProjection.getResultType(), Is.is(SINGLE_COLUMN_MULTIPLE_VALUES));
        assertThat(arrayProjection.valueType().id(), Is.is(ID));
    }

    @Test
    public void testArraySubqueryWithMultipleColsThrowsUnsupportedSubExpression() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Subqueries with more than 1 column are not supported");
        analyze("select array(select id, num_docs from sys.shards) as tmp from sys.shards");
    }

    @Test
    public void testCastExpression() throws Exception {
        QueriedRelation relation = analyze("select cast(other_id as string) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(TO_STRING, Collections.singletonList(LONG)));
        relation = analyze("select cast(1+1 as string) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral("2", STRING));
        relation = analyze("select cast(friends['id'] as array(string)) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(TO_STRING_ARRAY, Collections.singletonList(new ArrayType(DataTypes.LONG))));
    }

    @Test
    public void testTryCastExpression() throws Exception {
        QueriedRelation relation = analyze("select try_cast(other_id as string) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(CastFunctionResolver.tryFunctionsMap().get(STRING), Collections.singletonList(LONG)));
        relation = analyze("select try_cast(1+1 as string) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral("2", STRING));
        relation = analyze("select try_cast(null as string) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral(null, STRING));
        relation = analyze("select try_cast(counters as array(boolean)) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isFunction(CastFunctionResolver.tryFunctionsMap().get(new ArrayType(DataTypes.BOOLEAN)), Collections.singletonList(new ArrayType(DataTypes.LONG))));
    }

    @Test
    public void testTryCastReturnNullWhenCastFailsOnLiterals() {
        QueriedRelation relation = analyze("select try_cast('124123asdf' as integer) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral(null));
        relation = analyze("select try_cast(['fd', '3', '5'] as array(integer)) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral(null));
        relation = analyze("select try_cast('1' as boolean) from users");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testInvalidTryCastExpression() {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("No cast function found for return type object");
        analyze("select try_cast(name as array(object)) from users");
    }

    @Test
    public void testInvalidCastExpression() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No cast function found for return type object");
        analyze("select cast(name as array(object)) from users");
    }

    @Test
    public void testSelectWithAliasRenaming() throws Exception {
        QueriedRelation relation = analyze("select text as name, name as n from users");
        Symbol text = relation.querySpec().outputs().get(0);
        Symbol name = relation.querySpec().outputs().get(1);
        assertThat(text, SymbolMatchers.isReference("text"));
        assertThat(name, SymbolMatchers.isReference("name"));
    }

    @Test
    public void testFunctionArgumentsCantBeAliases() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column n unknown");
        analyze("select name as n, substr(n, 1, 1) from users");
    }

    @Test
    public void testSubscriptOnAliasShouldNotWork() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column n unknown");
        analyze("select name as n, n[1] from users");
    }

    @Test
    public void testCanSelectColumnWithAndWithoutSubscript() throws Exception {
        QueriedRelation relation = analyze("select counters, counters[1] from users");
        Symbol counters = relation.querySpec().outputs().get(0);
        Symbol countersSubscript = relation.querySpec().outputs().get(1);
        assertThat(counters, SymbolMatchers.isReference("counters"));
        assertThat(countersSubscript, SymbolMatchers.isFunction("subscript"));
    }

    @Test
    public void testOrderByOnAliasWithSameColumnNameInSchema() throws Exception {
        // name exists in the table but isn't selected so not ambiguous
        QueriedRelation relation = analyze("select other_id as name from users order by name");
        assertThat(relation.querySpec().outputs().get(0), SymbolMatchers.isReference("other_id"));
        List<Symbol> sortSymbols = relation.querySpec().orderBy().orderBySymbols();
        assert sortSymbols != null;
        assertThat(sortSymbols.get(0), SymbolMatchers.isReference("other_id"));
    }

    @Test
    public void testSelectPartitionedTableOrderBy() throws Exception {
        QueriedRelation relation = analyze("select id from multi_parted order by id, abs(num)");
        List<Symbol> symbols = relation.querySpec().orderBy().orderBySymbols();
        assert symbols != null;
        assertThat(symbols.size(), Is.is(2));
        assertThat(symbols.get(0), SymbolMatchers.isReference("id"));
        assertThat(symbols.get(1), SymbolMatchers.isFunction("abs"));
    }

    @Test
    public void testExtractFunctionWithLiteral() {
        QueriedRelation relation = analyze("select extract('day' from '2012-03-24') from users");
        Symbol symbol = relation.querySpec().outputs().get(0);
        assertThat(symbol, SymbolMatchers.isLiteral(24));
    }

    @Test
    public void testExtractFunctionWithWrongType() {
        QueriedRelation relation = analyze("select extract(day from name::timestamp) from users");
        Symbol symbol = relation.querySpec().outputs().get(0);
        assertThat(symbol, SymbolMatchers.isFunction("extract_DAY_OF_MONTH"));
        Symbol argument = arguments().get(0);
        assertThat(argument, SymbolMatchers.isFunction("to_timestamp"));
    }

    @Test
    public void testExtractFunctionWithCorrectType() {
        QueriedRelation relation = analyze("select extract(day from timestamp) from transactions");
        Symbol symbol = relation.querySpec().outputs().get(0);
        assertThat(symbol, SymbolMatchers.isFunction("extract_DAY_OF_MONTH"));
        Symbol argument = arguments().get(0);
        assertThat(argument, SymbolMatchers.isReference("timestamp"));
    }

    @Test
    public void selectCurrentTimeStamp() throws Exception {
        QueriedRelation relation = analyze("select CURRENT_TIMESTAMP from sys.cluster");
        Symbol currentTime = relation.querySpec().outputs().get(0);
        assertThat(currentTime, Matchers.instanceOf(Literal.class));
        assertThat(currentTime.valueType(), Is.is(TIMESTAMP));
    }

    @Test
    public void testAnyRightLiteral() throws Exception {
        QueriedRelation relation = analyze("select id from sys.shards where id = any ([1,2])");
        WhereClause whereClause = relation.querySpec().where();
        assertThat(whereClause.hasQuery(), Is.is(true));
        assertThat(whereClause.query(), SymbolMatchers.isFunction("any_=", ImmutableList.of(INTEGER, new ArrayType(DataTypes.INTEGER))));
    }

    @Test
    public void testNonDeterministicFunctionsAreNotAllocated() throws Exception {
        QueriedRelation relation = analyze(("select random(), random(), random() " + (("from transactions " + "where random() = 13.2 ") + "order by 1, random(), random()")));
        List<Symbol> outputs = relation.querySpec().outputs();
        List<Symbol> orderBySymbols = relation.querySpec().orderBy().orderBySymbols();
        // non deterministic, all equal
        assertThat(outputs.get(0), Matchers.allOf(Matchers.equalTo(outputs.get(2)), Matchers.equalTo(orderBySymbols.get(1))));
        // different instances
        assertThat(outputs.get(0), Matchers.allOf(Matchers.not(Matchers.sameInstance(outputs.get(2))), Matchers.not(Matchers.sameInstance(orderBySymbols.get(1)))));
        assertThat(outputs.get(1), Matchers.equalTo(orderBySymbols.get(2)));
        // "order by 1" references output 1, its the same
        assertThat(outputs.get(0), Is.is(Matchers.equalTo(orderBySymbols.get(0))));
        assertThat(outputs.get(0), Is.is(Matchers.sameInstance(orderBySymbols.get(0))));
        assertThat(orderBySymbols.get(0), Is.is(Matchers.equalTo(orderBySymbols.get(1))));
        // check where clause
        WhereClause whereClause = relation.querySpec().where();
        Function eqFunction = ((Function) (whereClause.query()));
        Symbol whereClauseSleepFn = eqFunction.arguments().get(0);
        assertThat(outputs.get(0), Is.is(Matchers.equalTo(whereClauseSleepFn)));
    }

    @Test
    public void testSelectSameTableTwice() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("\"doc.users\" specified more than once in the FROM clause");
        analyze("select * from users, users");
    }

    @Test
    public void testSelectSameTableTwiceWithAndWithoutSchemaName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("\"doc.users\" specified more than once in the FROM clause");
        analyze("select * from doc.users, users");
    }

    @Test
    public void testSelectSameTableTwiceWithSchemaName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("\"sys.nodes\" specified more than once in the FROM clause");
        analyze("select * from sys.nodes, sys.nodes");
    }

    @Test
    public void testSelectHiddenColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column _docid unknown");
        analyze("select _docid + 1 from users");
    }

    @Test
    public void testOrderByHiddenColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column _docid unknown");
        analyze("select * from users order by _docid");
    }

    @Test
    public void testWhereHiddenColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column _docid unknown");
        analyze("select * from users where _docid = 0");
    }

    @Test
    public void testHavingHiddenColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column _docid unknown");
        analyze("select count(*) from users group by id having _docid > 0");
    }

    @Test
    public void testStarToFieldsInMultiSelect() throws Exception {
        QueriedRelation relation = analyze("select jobs.stmt, operations.* from sys.jobs, sys.operations where jobs.id = operations.job_id");
        List<Symbol> joinOutputs = relation.querySpec().outputs();
        QueriedRelation operations = analyze("select * from sys.operations");
        List<Symbol> operationOutputs = operations.querySpec().outputs();
        assertThat(joinOutputs.size(), Is.is(((operationOutputs.size()) + 1)));
    }

    @Test
    public void testSelectStarWithInvalidPrefix() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The relation \"foo\" is not in the FROM clause.");
        analyze("select foo.* from sys.operations");
    }

    @Test
    public void testFullQualifiedStarPrefix() throws Exception {
        QueriedRelation relation = analyze("select sys.jobs.* from sys.jobs");
        List<Symbol> outputs = relation.querySpec().outputs();
        assertThat(outputs.size(), Is.is(5));
        // noinspection unchecked
        assertThat(outputs, Matchers.contains(SymbolMatchers.isReference("id"), SymbolMatchers.isReference("node"), SymbolMatchers.isReference("started"), SymbolMatchers.isReference("stmt"), SymbolMatchers.isReference("username")));
    }

    @Test
    public void testFullQualifiedStarPrefixWithAliasForTable() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The relation \"sys.operations\" is not in the FROM clause.");
        analyze("select sys.operations.* from sys.operations t1");
    }

    @Test
    public void testSelectStarWithTableAliasAsPrefix() throws Exception {
        QueriedRelation relation = analyze("select t1.* from sys.jobs t1");
        List<Symbol> outputs = relation.querySpec().outputs();
        assertThat(outputs.size(), Is.is(5));
        // noinspection unchecked
        assertThat(outputs, Matchers.contains(SymbolMatchers.isReference("id"), SymbolMatchers.isReference("node"), SymbolMatchers.isReference("started"), SymbolMatchers.isReference("stmt"), SymbolMatchers.isReference("username")));
    }

    @Test
    public void testAmbiguousStarPrefix() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The referenced relation \"users\" is ambiguous.");
        analyze("select users.* from doc.users, foo.users");
    }

    @Test
    public void testSelectMatchOnGeoShape() throws Exception {
        QueriedRelation relation = analyze("select * from users where match(shape, 'POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))')");
        assertThat(relation.querySpec().where().query(), SymbolMatchers.isFunction("match"));
    }

    @Test
    public void testSelectMatchOnGeoShapeObjectLiteral() throws Exception {
        QueriedRelation relation = analyze("select * from users where match(shape, {type='Polygon', coordinates=[[[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]]})");
        assertThat(relation.querySpec().where().query(), SymbolMatchers.isFunction("match"));
    }

    @Test
    public void testOrderByGeoShape() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot ORDER BY 'shape': invalid data type 'geo_shape'.");
        analyze("select * from users ORDER BY shape");
    }

    @Test
    public void testSelectStarFromUnnest() throws Exception {
        QueriedRelation relation = analyze("select * from unnest([1, 2], ['Marvin', 'Trillian'])");
        // noinspection generics
        assertThat(relation.querySpec().outputs(), Matchers.contains(SymbolMatchers.isReference("col1"), SymbolMatchers.isReference("col2")));
    }

    @Test
    public void testSelectStarFromUnnestWithInvalidArguments() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast 1 to type undefined_array");
        analyze("select * from unnest(1, 'foo')");
    }

    @Test
    public void testSelectCol1FromUnnest() throws Exception {
        QueriedRelation relation = analyze("select col1 from unnest([1, 2], ['Marvin', 'Trillian'])");
        assertThat(relation.querySpec().outputs(), Matchers.contains(SymbolMatchers.isReference("col1")));
    }

    @Test
    public void testCollectSetCanBeUsedInHaving() throws Exception {
        QueriedRelation relation = analyze(("select collect_set(recovery['size']['percent']), schema_name, table_name " + ((("from sys.shards " + "group by 2, 3 ") + "having collect_set(recovery['size']['percent']) != [100.0] ") + "order by 2, 3")));
        assertThat(relation.querySpec().having(), Matchers.notNullValue());
        assertThat(relation.querySpec().having().query(), TestingHelpers.isSQL("(NOT (collect_set(sys.shards.recovery['size']['percent']) = [100.0]))"));
    }

    @Test
    public void testNegationOfNonNumericLiteralsShouldFail() throws Exception {
        expectedException.expectMessage("Cannot negate 'foo'. You may need to add explicit type casts");
        analyze("select - 'foo'");
    }

    @Test
    public void testSelectFromNonTableFunction() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Non table function 'abs' is not supported in from clause");
        analyze("select * from abs(1)");
    }

    @Test
    public void testMatchInExplicitJoinConditionIsProhibited() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use MATCH predicates on columns of 2 different relations");
        analyze("select * from users u1 inner join users u2 on match((u1.name, u2.name), 'foo')");
    }

    @Test
    public void testUnnestWithMoreThat10Columns() {
        QueriedRelation relation = analyze("select * from unnest(['a'], ['b'], [0], [0], [0], [0], [0], [0], [0], [0], [0])");
        String sqlFields = "col1, col2, col3, col4, " + ("col5, col6, col7, col8, " + "col9, col10, col11");
        assertThat(relation.querySpec().outputs(), TestingHelpers.isSQL(sqlFields));
        assertThat(relation.fields(), TestingHelpers.isSQL(sqlFields));
    }

    @Test
    public void testUnnestWithObjectColumn() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column col1['x'] unknown");
        analyze("select col1['x'] from unnest([{x=1}])");
    }

    @Test
    public void testSubSelectWithAccessToParentRelationThrowsUnsupportedFeature() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use relation \"doc.t1\" in this context. It is only accessible in the parent context");
        analyze("select (select 1 from t1 as ti where ti.x = t1.x) from t1");
    }

    @Test
    public void testSubSelectWithAccessToParentRelationAliasThrowsUnsupportedFeature() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use relation \"tparent\" in this context. It is only accessible in the parent context");
        analyze("select (select 1 from t1 where t1.x = tparent.x) from t1 as tparent");
    }

    @Test
    public void testSubSelectWithAccessToGrandParentRelation() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use relation \"grandparent\" in this context. It is only accessible in the parent context");
        analyze("select (select (select 1 from t1 where grandparent.x = t1.x) from t1 as parent) from t1 as grandparent");
    }

    @Test
    public void testCustomSchemaSubSelectWithAccessToParentRelation() throws Exception {
        DocTableInfo fooTableInfo = TestingTableInfo.builder(new RelationName("foo", "t1"), TableDefinitions.SHARD_ROUTING).add("id", LONG, null).add("name", STRING, null).addPrimaryKey("id").build();
        DocTableInfoFactory fooTableFactory = new TestingDocTableInfoFactory(ImmutableMap.of(fooTableInfo.ident(), fooTableInfo));
        Functions functions = TestingHelpers.getFunctions();
        UserDefinedFunctionService udfService = new UserDefinedFunctionService(clusterService, functions);
        SQLExecutor sqlExecutor2 = SQLExecutor.builder(clusterService).setSearchPath("foo").addSchema(new io.crate.metadata.doc.DocSchemaInfo("foo", clusterService, functions, udfService, ( ident, state) -> null, fooTableFactory)).addDocTable(fooTableInfo).build();
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use relation \"foo.t1\" in this context. It is only accessible in the parent context");
        sqlExecutor2.analyze("select * from t1 where id = (select 1 from t1 as x where x.id = t1.id)");
    }

    @Test
    public void testContextForExplicitJoinsPrecedesImplicitJoins() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use relation \"doc.t1\" in this context. It is only accessible in the parent context");
        // Inner join has to be processed before implicit cross join.
        // Inner join does not know about t1's fields (!)
        analyze("select * from t1, t2 inner join t1 b on b.x = t1.x");
    }

    @Test
    public void testColumnOutputWithSingleRowSubselect() {
        QueriedRelation relation = analyze("select 1 = \n (select \n 2\n)\n");
        assertThat(relation.fields(), TestingHelpers.isSQL("(1 = (SELECT 2))"));
    }

    @Test
    public void testTableAliasIsNotAddressableByColumnNameWithSchema() {
        expectedException.expectMessage("Relation 'doc.a' unknown");
        analyze("select doc.a.x from t1 as a");
    }

    @Test
    public void testUsingTableFunctionInGroupByIsProhibited() {
        expectedException.expectMessage("Table functions are not allowed in GROUP BY");
        analyze("select count(*) from t1 group by unnest([1])");
    }

    @Test
    public void testUsingTableFunctionInHavingIsProhibited() {
        expectedException.expectMessage("Table functions are not allowed in HAVING");
        analyze("select count(*) from t1 having unnest([1]) > 1");
    }

    @Test
    public void testUsingTableFunctionInWhereClauseIsNotAllowed() {
        expectedException.expectMessage("Table functions are not allowed in WHERE");
        analyze("select * from sys.nodes where unnest([1]) = 1");
    }

    @Test
    public void testUsingWindowFunctionInHavingIsProhibited() {
        expectedException.expectMessage("Window functions are not allowed in HAVING");
        analyze("select count(*) from t1 having sum(1) OVER() > 1");
    }

    @Test
    public void testUsingWindowFunctionInWhereClauseIsNotAllowed() {
        expectedException.expectMessage("Window functions are not allowed in WHERE");
        analyze("select count(*) from t1 where sum(1) OVER() = 1");
    }

    @Test
    public void testCastToNestedArrayCanBeUsed() {
        QueriedRelation relation = analyze("select [[1, 2, 3]]::array(array(int))");
        assertThat(relation.outputs().get(0).valueType(), Is.is(new ArrayType(new ArrayType(DataTypes.INTEGER))));
    }
}
