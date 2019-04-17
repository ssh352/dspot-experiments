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
package io.crate.analyze.relations;


import Operation.READ;
import io.crate.analyze.ValuesResolver;
import io.crate.expression.symbol.Field;
import io.crate.expression.symbol.Literal;
import io.crate.sql.tree.QualifiedName;
import io.crate.testing.SymbolMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ExcludedFieldProviderTest {
    @Test
    public void testResolveFieldsAndValues() {
        QualifiedName normalField1 = QualifiedName.of("field1");
        QualifiedName normalField2 = QualifiedName.of("normal", "field2");
        QualifiedName excludedName = QualifiedName.of("excluded", "field3");
        FieldProvider fieldProvider = ( qualifiedName, path, operation) -> {
            return new Field(Mockito.mock(.class), qualifiedName::toString, DataTypes.INTEGER);
        };
        ValuesResolver valuesResolver = ( argumentColumn) -> {
            assertThat(argumentColumn.path().outputName(), is("field3"));
            return Literal.of(42);
        };
        ExcludedFieldProvider excludedFieldProvider = new ExcludedFieldProvider(fieldProvider, valuesResolver);
        Assert.assertThat(excludedFieldProvider.resolveField(normalField1, null, READ), SymbolMatchers.isField(normalField1.toString()));
        Assert.assertThat(excludedFieldProvider.resolveField(normalField2, null, READ), SymbolMatchers.isField(normalField2.toString()));
        Assert.assertThat(excludedFieldProvider.resolveField(excludedName, null, READ), SymbolMatchers.isLiteral(42));
    }
}
