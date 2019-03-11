/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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


import DataTypes.BOOLEAN;
import DataTypes.DOUBLE;
import DataTypes.LONG;
import DataTypes.STRING;
import ObjectType.ID;
import SymbolType.LITERAL;
import com.google.common.collect.ImmutableMap;
import io.crate.exceptions.ConversionException;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.SymbolMatchers;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.LongType;
import io.crate.types.UndefinedType;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CompoundLiteralTest extends CrateUnitTest {
    private SqlExpressions expressions;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testObjectConstruction() throws Exception {
        Symbol s = expressions.asSymbol("{}");
        assertThat(s, CoreMatchers.instanceOf(Literal.class));
        Literal l = ((Literal) (s));
        assertThat(l.value(), Matchers.is(((Object) (new HashMap<String, Object>()))));
        Literal objectLiteral = ((Literal) (expressions.normalize(expressions.asSymbol("{ident='value'}"))));
        assertThat(objectLiteral.symbolType(), Matchers.is(LITERAL));
        assertThat(objectLiteral.valueType().id(), Matchers.is(ID));
        assertThat(objectLiteral.value(), Matchers.is(((Object) (new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("ident", "value").map()))));
        Literal multipleObjectLiteral = ((Literal) (expressions.normalize(expressions.asSymbol("{\"Ident\"=123.4, a={}, ident=\'string\'}"))));
        Map<String, Object> values = ((Map<String, Object>) (multipleObjectLiteral.value()));
        assertThat(values, Matchers.is(new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("Ident", 123.4).put("a", new HashMap<String, Object>()).put("ident", "string").map()));
    }

    @Test
    public void testObjectConstructionWithExpressionsAsValues() throws Exception {
        Literal objectLiteral = ((Literal) (expressions.normalize(expressions.asSymbol("{name = 1 + 2}"))));
        assertThat(objectLiteral.symbolType(), Matchers.is(LITERAL));
        assertThat(objectLiteral.value(), Matchers.is(new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("name", 3L).map()));
        Literal nestedObjectLiteral = ((Literal) (expressions.normalize(expressions.asSymbol("{a = {name = concat('foo', 'bar')}}"))));
        @SuppressWarnings("unchecked")
        Map<String, Object> values = ((Map<String, Object>) (nestedObjectLiteral.value()));
        assertThat(values, Matchers.is(new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("a", new HashMap<String, Object>() {
            {
                put("name", "foobar");
            }
        }).map()));
    }

    @Test
    public void testObjectConstructionWithParameterExpression() throws Exception {
        assertThat(expressions.asSymbol("{ident=?}"), SymbolMatchers.isFunction("_map"));
    }

    @Test
    public void testObjectConstructionFailsOnDuplicateKeys() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("object contains duplicate keys");
        analyzeExpression("{a=1, a=2}");
    }

    @Test
    public void testArrayConstructionWithOnlyLiterals() throws Exception {
        Literal emptyArray = ((Literal) (analyzeExpression("[]")));
        assertThat(((Object[]) (emptyArray.value())), Matchers.is(new Object[0]));
        assertThat(emptyArray.valueType(), Matchers.is(((DataType) (new ArrayType(UndefinedType.INSTANCE)))));
        Literal singleArray = ((Literal) (analyzeExpression("[1]")));
        assertThat(singleArray.valueType(), Matchers.is(((DataType) (new ArrayType(LongType.INSTANCE)))));
        assertThat(((Object[]) (singleArray.value())).length, Matchers.is(1));
        assertThat(((Object[]) (singleArray.value()))[0], Matchers.is(((Object) (1L))));
        Literal multiArray = ((Literal) (analyzeExpression("[1, 2, 3]")));
        assertThat(multiArray.valueType(), Matchers.is(((DataType) (new ArrayType(LongType.INSTANCE)))));
        assertThat(((Object[]) (multiArray.value())).length, Matchers.is(3));
        assertThat(((Object[]) (multiArray.value())), Matchers.is(new Object[]{ 1L, 2L, 3L }));
    }

    @Test
    public void testArrayConstructionWithParameterExpression() throws Exception {
        Symbol array = expressions.asSymbol("[1, ?]");
        assertThat(array, SymbolMatchers.isFunction("_array"));
        assertThat(arguments().size(), Matchers.is(2));
    }

    @Test
    public void testArrayDifferentTypes() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast 'string' to type long");
        analyzeExpression("[1, 'string']");
    }

    @Test
    public void testNestedArrayLiteral() throws Exception {
        Map<String, DataType> expected = ImmutableMap.<String, DataType>builder().put("'string'", STRING).put("0", LONG).put("1.8", DOUBLE).put("TRUE", BOOLEAN).build();
        for (Map.Entry<String, DataType> entry : expected.entrySet()) {
            Symbol nestedArraySymbol = analyzeExpression((("[[" + (entry.getKey())) + "]]"));
            assertThat(nestedArraySymbol, Matchers.instanceOf(Literal.class));
            Literal nestedArray = ((Literal) (nestedArraySymbol));
            assertThat(nestedArray.valueType(), Matchers.is(((DataType) (new ArrayType(new ArrayType(entry.getValue()))))));
        }
    }
}

