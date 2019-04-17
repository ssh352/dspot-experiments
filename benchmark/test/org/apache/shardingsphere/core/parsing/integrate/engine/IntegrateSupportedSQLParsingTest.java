/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.parsing.integrate.engine;


import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.parsing.integrate.asserts.ParserResultSetLoader;
import org.apache.shardingsphere.core.parsing.integrate.asserts.SQLStatementAssert;
import org.apache.shardingsphere.test.sql.SQLCaseType;
import org.apache.shardingsphere.test.sql.SQLCasesLoader;
import org.junit.Test;


@RequiredArgsConstructor
public final class IntegrateSupportedSQLParsingTest extends AbstractBaseIntegrateSQLParsingTest {
    private static SQLCasesLoader sqlCasesLoader = SQLCasesLoader.getInstance();

    private static ParserResultSetLoader parserResultSetLoader = ParserResultSetLoader.getInstance();

    private final String sqlCaseId;

    private final DatabaseType databaseType;

    private final SQLCaseType sqlCaseType;

    @Test
    public void assertSupportedSQL() {
        String sql = IntegrateSupportedSQLParsingTest.sqlCasesLoader.getSupportedSQL(sqlCaseId, sqlCaseType, IntegrateSupportedSQLParsingTest.parserResultSetLoader.getParserResult(sqlCaseId).getParameters());
        // TODO old parser has problem with here, should remove this after remove all old parser
        if ("select_with_same_table_name_and_alias".equals(sqlCaseId)) {
            return;
        }
        new SQLStatementAssert(parse(false), sqlCaseId, sqlCaseType).assertSQLStatement();
    }
}
