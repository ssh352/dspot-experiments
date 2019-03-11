/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;


/**
 * MERGE statement test.
 */
public class JdbcThinMergeStatementSelfTest extends JdbcThinAbstractDmlStatementSelfTest {
    /**
     * SQL query.
     */
    private static final String SQL = "merge into Person(_key, id, firstName, lastName, age) values " + (("('p1', 1, 'John', 'White', 25), " + "('p2', 2, 'Joe', 'Black', 35), ") + "('p3', 3, 'Mike', 'Green', 40)");

    /**
     * SQL query.
     */
    protected static final String SQL_PREPARED = "merge into Person(_key, id, firstName, lastName, age) values " + "(?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";

    /**
     * Statement.
     */
    protected Statement stmt;

    /**
     * Prepared statement.
     */
    protected PreparedStatement prepStmt;

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecuteUpdate() throws SQLException {
        assertEquals(3, stmt.executeUpdate(JdbcThinMergeStatementSelfTest.SQL));
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecute() throws SQLException {
        assertFalse(stmt.execute(JdbcThinMergeStatementSelfTest.SQL));
    }
}

