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
package io.crate.expression.scalar.arithmetic;


import io.crate.exceptions.ConversionException;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class Ignore3vlFunctionTest extends AbstractScalarFunctionsTest {
    @Test
    public void testIgnore3vlFunction() {
        assertEvaluate("ignore3vl(false)", false);
        assertEvaluate("ignore3vl(true)", true);
        assertEvaluate("ignore3vl(null)", false);
    }

    @Test
    public void testWrongType() {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast 'foo' to type boolean");
        assertEvaluate("ignore3vl('foo')", null);
    }

    @Test
    public void testNormalizeReference() {
        assertNormalize("ignore3vl(is_awesome)", SymbolMatchers.isFunction("ignore3vl"));
    }

    @Test
    public void testNormalizeNull() {
        assertNormalize("ignore3vl(null)", SymbolMatchers.isLiteral(false));
    }
}

