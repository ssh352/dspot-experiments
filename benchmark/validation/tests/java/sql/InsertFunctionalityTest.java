/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;


public class InsertFunctionalityTest extends TestCase {
    private static Connection conn = null;

    private static Statement statement = null;

    /**
     * InsertFunctionalityTest#testInsert1(). Attempts to insert row into
     *        table with integrity checking
     */
    public void testInsert1() throws SQLException {
        DatabaseCreator.fillParentTable(InsertFunctionalityTest.conn);
        DatabaseCreator.fillFKStrictTable(InsertFunctionalityTest.conn);
        DatabaseCreator.fillFKCascadeTable(InsertFunctionalityTest.conn);
        InsertFunctionalityTest.statement.execute((("INSERT INTO " + (DatabaseCreator.FKSTRICT_TABLE)) + " VALUES(4, 1, 'testInsert')"));
        InsertFunctionalityTest.statement.execute((("INSERT INTO " + (DatabaseCreator.FKCASCADE_TABLE)) + " VALUES(4, 1, 'testInsert')"));
    }

    /**
     * InsertFunctionalityTest#testInsert2(). Attempts to insert row into
     *        table with integrity checking when row has incorrect foreign key
     *        value - expecting SQLException
     */
    public void testInsert2() throws SQLException {
        DatabaseCreator.fillParentTable(InsertFunctionalityTest.conn);
        DatabaseCreator.fillFKStrictTable(InsertFunctionalityTest.conn);
        DatabaseCreator.fillFKCascadeTable(InsertFunctionalityTest.conn);
        try {
            InsertFunctionalityTest.statement.execute((("INSERT INTO " + (DatabaseCreator.FKSTRICT_TABLE)) + " VALUES(4, 4, 'testInsert')"));
            // TODO Foreign key functionality isn't supported
            // fail("expecting SQLException");
        } catch (SQLException ex) {
            // expected
        }
        try {
            InsertFunctionalityTest.statement.execute((("INSERT INTO " + (DatabaseCreator.FKCASCADE_TABLE)) + " VALUES(4, 4, 'testInsert')"));
            // TODO Foreign key functionality isn't supported
            // fail("expecting SQLException");
        } catch (SQLException ex) {
            // expected
        }
    }

    /**
     * InsertFunctionalityTest#testInsert3(). Tests INSERT ... SELECT
     *        functionality
     */
    public void testInsert3() throws SQLException {
        DatabaseCreator.fillParentTable(InsertFunctionalityTest.conn);
        DatabaseCreator.fillFKStrictTable(InsertFunctionalityTest.conn);
        InsertFunctionalityTest.statement.execute(((((("INSERT INTO " + (DatabaseCreator.TEST_TABLE5)) + " SELECT id AS testId, value AS testValue ") + "FROM ") + (DatabaseCreator.FKSTRICT_TABLE)) + " WHERE name_id = 1"));
        ResultSet r = InsertFunctionalityTest.statement.executeQuery(("SELECT COUNT(*) FROM " + (DatabaseCreator.TEST_TABLE5)));
        r.next();
        TestCase.assertEquals("Should be 2 rows", 2, r.getInt(1));
        r.close();
    }

    /**
     * InsertFunctionalityTest#testInsert4(). Tests INSERT ... SELECT
     *        with expressions in SELECT query
     */
    public void testInsert4() throws SQLException {
        DatabaseCreator.fillSimpleTable1(InsertFunctionalityTest.conn);
        InsertFunctionalityTest.statement.execute(((("INSERT INTO " + (DatabaseCreator.SIMPLE_TABLE2)) + " SELECT id, speed*10 AS speed, size-1 AS size FROM ") + (DatabaseCreator.SIMPLE_TABLE1)));
        ResultSet r = InsertFunctionalityTest.statement.executeQuery((((("SELECT COUNT(*) FROM " + (DatabaseCreator.SIMPLE_TABLE2)) + " AS a JOIN ") + (DatabaseCreator.SIMPLE_TABLE1)) + " AS b ON a.speed = 10*b.speed AND a.size = b.size-1"));
        r.next();
        TestCase.assertEquals("Should be 2 rows", 2, r.getInt(1));
        r.close();
    }

    /**
     * InsertFunctionalityTest#testInsert5(). Inserts multiple rows using
     *        UNION ALL
     */
    public void testInsert5() throws SQLException {
        InsertFunctionalityTest.statement.execute((((("INSERT INTO " + (DatabaseCreator.TEST_TABLE5)) + " SELECT 1 as testId, 2 as testValue ") + "UNION SELECT 2 as testId, 3 as testValue ") + "UNION SELECT 3 as testId, 4 as testValue"));
        ResultSet r = InsertFunctionalityTest.statement.executeQuery(("SELECT COUNT(*) FROM " + (DatabaseCreator.TEST_TABLE5)));
        r.next();
        TestCase.assertEquals("Should be 3 rows", 3, r.getInt(1));
        r.close();
    }

    /**
     * InsertFunctionalityTest#testInsert6(). Tests INSERT with
     *        PreparedStatement
     */
    public void testInsertPrepared() throws SQLException {
        PreparedStatement stat = InsertFunctionalityTest.conn.prepareStatement((("INSERT INTO " + (DatabaseCreator.TEST_TABLE5)) + " VALUES(?, ?)"));
        stat.setInt(1, 1);
        stat.setString(2, "1");
        stat.execute();
        stat.setInt(1, 2);
        stat.setString(2, "3");
        stat.execute();
        ResultSet r = InsertFunctionalityTest.statement.executeQuery(((("SELECT COUNT(*) FROM " + (DatabaseCreator.TEST_TABLE5)) + " WHERE (testId = 1 AND testValue = '1') ") + "OR (testId = 2 AND testValue = '3')"));
        r.next();
        TestCase.assertEquals("Incorrect number of records", 2, r.getInt(1));
        r.close();
        stat.close();
    }
}

