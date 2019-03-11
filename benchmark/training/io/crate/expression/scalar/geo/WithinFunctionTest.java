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
package io.crate.expression.scalar.geo;


import DataTypes.GEO_POINT;
import DataTypes.GEO_SHAPE;
import DataTypes.LONG;
import DataTypes.STRING;
import SymbolType.FUNCTION;
import SymbolType.LITERAL;
import WithinFunction.NAME;
import com.google.common.collect.ImmutableMap;
import io.crate.exceptions.ConversionException;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import static WithinFunction.NAME;


public class WithinFunctionTest extends AbstractScalarFunctionsTest {
    private static final String FNAME = NAME;

    @Test
    public void testEvaluateWithNullArgs() throws Exception {
        assertEvaluate("within(geopoint, geoshape)", null, Literal.newGeoPoint(null), Literal.newGeoShape("POINT (10 10)"));
        assertEvaluate("within(geopoint, geoshape)", null, Literal.newGeoPoint("POINT (10 10)"), Literal.newGeoShape(null));
    }

    @Test
    public void testEvaluatePointLiteralWithinPolygonLiteral() {
        assertEvaluate("within(geopoint, geoshape)", true, Literal.of(GEO_SHAPE, GEO_SHAPE.value("POINT (10 10)")), Literal.of(GEO_SHAPE, GEO_SHAPE.value("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))")));
    }

    @Test
    public void testEvaluateShapeWithinShape() {
        assertEvaluate("within(geoshape, geoshape)", true, Literal.of(GEO_SHAPE, GEO_SHAPE.value("LINESTRING (8 15, 13 24)")), Literal.of(GEO_SHAPE, GEO_SHAPE.value("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))")));
    }

    @Test
    public void testEvaluateShapeIsNotWithinShape() {
        assertEvaluate("within(geoshape, geoshape)", false, Literal.of(GEO_SHAPE, GEO_SHAPE.value("LINESTRING (8 15, 40 74)")), Literal.of(GEO_SHAPE, GEO_SHAPE.value("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))")));
    }

    @Test
    public void testEvaluateObjectWithinShape() {
        assertEvaluate("within(geopoint, geoshape)", true, Literal.of(ImmutableMap.<String, Object>of("type", "Point", "coordinates", new double[]{ 10.0, 10.0 })), Literal.of(GEO_SHAPE, GEO_SHAPE.value("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))")));
    }

    @Test
    public void testNormalizeWithReferenceAndLiteral() throws Exception {
        Symbol normalizedSymbol = normalize(WithinFunctionTest.FNAME, TestingHelpers.createReference("foo", GEO_POINT), Literal.newGeoShape("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))"));
        assertThat(normalizedSymbol, SymbolMatchers.isFunction(WithinFunctionTest.FNAME));
    }

    @Test
    public void testNormalizeWithTwoStringLiterals() throws Exception {
        assertNormalize("within('POINT (10 10)', 'POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))')", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNormalizeWithStringLiteralAndReference() throws Exception {
        Symbol normalized = normalize(WithinFunctionTest.FNAME, TestingHelpers.createReference("point", GEO_POINT), Literal.of("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))"));
        assertThat(normalized, Matchers.instanceOf(Function.class));
        Function function = ((Function) (normalized));
        Symbol symbol = function.arguments().get(1);
        assertThat(symbol.valueType(), IsEqual.equalTo(GEO_SHAPE));
    }

    @Test
    public void testNormalizeWithFirstArgAsStringReference() throws Exception {
        Symbol normalized = normalize(WithinFunctionTest.FNAME, TestingHelpers.createReference("location", STRING), Literal.newGeoShape("POLYGON ((5 5, 20 5, 30 30, 5 30, 5 5))"));
        assertThat(normalized.symbolType(), Is.is(FUNCTION));
    }

    @Test
    public void testNormalizeWithSecondArgAsStringReference() throws Exception {
        Symbol normalized = normalize(WithinFunctionTest.FNAME, Literal.of(GEO_POINT, new Double[]{ 0.0, 0.0 }), TestingHelpers.createReference("location", STRING));
        assertThat(normalized.symbolType(), Is.is(FUNCTION));
        assertThat(info().ident().name(), Is.is(NAME));
    }

    @Test
    public void testFirstArgumentWithInvalidType() throws Exception {
        expectedException.expect(ConversionException.class);
        getFunction(WithinFunctionTest.FNAME, LONG, GEO_POINT);
    }

    @Test
    public void testSecondArgumentWithInvalidType() throws Exception {
        expectedException.expect(ConversionException.class);
        getFunction(WithinFunctionTest.FNAME, GEO_POINT, LONG);
    }

    @Test
    public void testNormalizeFromObject() throws Exception {
        Symbol normalized = normalize(WithinFunctionTest.FNAME, Literal.of("POINT (1.0 0.0)"), Literal.of(ImmutableMap.<String, Object>of("type", "Point", "coordinates", new double[]{ 0.0, 1.0 })));
        assertThat(normalized.symbolType(), Is.is(LITERAL));
        assertThat(value(), Is.is(Boolean.FALSE));
    }
}

