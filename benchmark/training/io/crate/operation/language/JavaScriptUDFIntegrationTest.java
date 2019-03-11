/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.operation.language;


import DataTypes.INTEGER;
import DataTypes.LONG;
import ESIntegTestCase.ClusterScope;
import com.google.common.collect.ImmutableList;
import io.crate.integrationtests.SQLTransportIntegrationTest;
import io.crate.testing.TestingHelpers;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


@ClusterScope(numDataNodes = 2, numClientNodes = 0)
public class JavaScriptUDFIntegrationTest extends SQLTransportIntegrationTest {
    @Test
    public void testJavascriptFunction() throws Exception {
        execute(("CREATE FUNCTION subtract_js(LONG, LONG) " + "RETURNS LONG LANGUAGE JAVASCRIPT AS 'function subtract_js(x, y) { return x-y; }'"));
        assertFunctionIsCreatedOnAll(sqlExecutor.getCurrentSchema(), "subtract_js", ImmutableList.of(LONG, LONG));
        execute("SELECT SUBTRACT_JS(a, b) FROM test ORDER BY a ASC");
        assertThat(response.rowCount(), CoreMatchers.is(2L));
        assertThat(response.rows()[0][0], CoreMatchers.is(2L));
        assertThat(response.rows()[1][0], CoreMatchers.is(3L));
    }

    @Test
    public void testBuiltinFunctionOverloadWithOrderBy() throws Exception {
        // this is a regression test that shows that the correct user-defined function implementations are returned
        // and not the built-in ones
        // the query got stuck because we used on built-in function lookup (return type long) and one udf lookup (return type int)
        // which caused a type mismatch when comparing values in ORDER BY
        execute("CREATE TABLE test.t (a INTEGER, b INTEGER) WITH (number_of_replicas=0)");
        execute("INSERT INTO test.t (a, b) VALUES (1, 1), (2, 1), (3, 1)");
        refresh("test.t");
        execute("CREATE FUNCTION test.subtract(integer, integer) RETURNS INTEGER LANGUAGE javascript AS 'function subtract(x, y){ return x-y; }'");
        assertFunctionIsCreatedOnAll("test", "subtract", ImmutableList.of(INTEGER, INTEGER));
        execute("SELECT test.subtract(a, b) FROM test.t ORDER BY 1");
        assertThat(TestingHelpers.printedTable(response.rows()), CoreMatchers.is("0\n1\n2\n"));
    }
}

