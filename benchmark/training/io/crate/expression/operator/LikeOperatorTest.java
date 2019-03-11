/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.expression.operator;


import Literal.NULL;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class LikeOperatorTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeSymbolEqual() {
        assertNormalize("'foo' like 'foo'", SymbolMatchers.isLiteral(true));
        assertNormalize("'notFoo' like 'foo'", SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testPatternIsNoLiteral() throws Exception {
        assertEvaluate("name like timezone", false, Literal.of("foo"), Literal.of("bar"));
        assertEvaluate("name like name", true, Literal.of("foo"), Literal.of("foo"));
    }

    @Test
    public void testNormalizeSymbolLikeZeroOrMore() {
        // Following tests: wildcard: '%' ... zero or more characters (0...N)
        assertNormalize("'foobar' like '%bar'", SymbolMatchers.isLiteral(true));
        assertNormalize("'bar' like '%bar'", SymbolMatchers.isLiteral(true));
        assertNormalize("'ar' like '%bar'", SymbolMatchers.isLiteral(false));
        assertNormalize("'foobar' like 'foo%'", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo' like 'foo%'", SymbolMatchers.isLiteral(true));
        assertNormalize("'fo' like 'foo%'", SymbolMatchers.isLiteral(false));
        assertNormalize("'foobar' like '%oob%'", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNormalizeSymbolLikeExactlyOne() {
        // Following tests: wildcard: '_' ... any single character (exactly one)
        assertNormalize("'bar' like '_ar'", SymbolMatchers.isLiteral(true));
        assertNormalize("'bar' like '_bar'", SymbolMatchers.isLiteral(false));
        assertNormalize("'foo' like 'fo_'", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo' like 'foo_'", SymbolMatchers.isLiteral(false));
        assertNormalize("'foo' like '_o_'", SymbolMatchers.isLiteral(true));
        assertNormalize("'foobar' like '_foobar_'", SymbolMatchers.isLiteral(false));
    }

    // Following tests: mixed wildcards:
    @Test
    public void testNormalizeSymbolLikeMixed() {
        assertNormalize("'foobar' like '%o_ar'", SymbolMatchers.isLiteral(true));
        assertNormalize("'foobar' like '%a_'", SymbolMatchers.isLiteral(true));
        assertNormalize("'foobar' like '%o_a%'", SymbolMatchers.isLiteral(true));
        assertNormalize("'Lorem ipsum dolor...' like '%i%m%'", SymbolMatchers.isLiteral(true));
        assertNormalize("'Lorem ipsum dolor...' like '%%%sum%%'", SymbolMatchers.isLiteral(true));
        assertNormalize("'Lorem ipsum dolor...' like '%i%m'", SymbolMatchers.isLiteral(false));
    }

    // Following tests: escaping wildcards
    @Test
    public void testExpressionToRegexExactlyOne() {
        String expression = "fo_bar";
        assertEquals("^fo.bar$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testLikeOnMultilineStatement() throws Exception {
        String stmt = "SELECT date_trunc(\'day\', ts), sum(num_steps) as num_steps, count(*) as num_records \n" + (("FROM steps\n" + "WHERE month_partition = \'201409\'\n") + "GROUP BY 1 ORDER BY 1 DESC limit 100");
        assertEvaluate("name like '  SELECT%'", false, Literal.of(stmt));
        assertEvaluate("name like 'SELECT%'", true, Literal.of(stmt));
        assertEvaluate("name like 'SELECT date_trunc%'", true, Literal.of(stmt));
        assertEvaluate("name like '% date_trunc%'", true, Literal.of(stmt));
    }

    @Test
    public void testExpressionToRegexZeroOrMore() {
        String expression = "fo%bar";
        assertEquals("^fo.*bar$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testExpressionToRegexEscapingPercent() {
        String expression = "fo\\%bar";
        assertEquals("^fo%bar$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testExpressionToRegexEscapingUnderline() {
        String expression = "fo\\_bar";
        assertEquals("^fo_bar$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testExpressionToRegexEscaping() {
        String expression = "fo\\\\_bar";
        assertEquals("^fo\\\\.bar$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testExpressionToRegexEscapingMutli() {
        String expression = "%%\\%sum%%";
        assertEquals("^.*.*%sum.*.*$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testExpressionToRegexMaliciousPatterns() {
        String expression = "fo(ooo)o[asdf]o\\bar^$.*";
        assertEquals("^fo\\(ooo\\)o\\[asdf\\]obar\\^\\$\\.\\*$", LikeOperator.patternToRegex(expression, LikeOperator.DEFAULT_ESCAPE, true));
    }

    @Test
    public void testLikeOperator() {
        assertEvaluate("'foobarbaz' like 'foo%baz'", true);
        assertEvaluate("'foobarbaz' like 'foo_baz'", false);
        assertEvaluate("'characters' like 'charac%'", true);
        assertEvaluate("'foobarbaz' like name", null, NULL);
        assertEvaluate("name like 'foobarbaz'", null, NULL);
    }
}

