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
package org.apache.ignite.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class JdbcNoDefaultCacheTest extends GridCommonAbstractTest {
    /**
     * First cache name.
     */
    private static final String CACHE1_NAME = "cache1";

    /**
     * Second cache name.
     */
    private static final String CACHE2_NAME = "cache2";

    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite://127.0.0.1/";

    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaults() throws Exception {
        String url = getUrl();
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
        }
        try (Connection conn = DriverManager.getConnection((url + '/'))) {
            assertNotNull(conn);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoCacheNameQuery() throws Exception {
        Statement stmt;
        try (Connection conn = DriverManager.getConnection(getUrl())) {
            stmt = conn.createStatement();
            assertNotNull(stmt);
            assertFalse(stmt.isClosed());
            stmt.execute("select t._key, t._val from \"cache1\".Integer t");
            ResultSet rs = stmt.getResultSet();
            while (rs.next())
                assertEquals(rs.getInt(2), ((rs.getInt(1)) * 2));

            stmt.execute("select t._key, t._val from \"cache2\".Integer t");
            rs = stmt.getResultSet();
            while (rs.next())
                assertEquals(rs.getInt(2), ((rs.getInt(1)) * 3));

            stmt.execute(("select t._key, t._val, v._val " + "from \"cache1\".Integer t join \"cache2\".Integer v on t._key = v._key"));
            rs = stmt.getResultSet();
            while (rs.next()) {
                assertEquals(rs.getInt(2), ((rs.getInt(1)) * 2));
                assertEquals(rs.getInt(3), ((rs.getInt(1)) * 3));
            } 
            stmt.close();
        }
    }
}

