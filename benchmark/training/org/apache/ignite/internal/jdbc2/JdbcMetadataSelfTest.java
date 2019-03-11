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
package org.apache.ignite.internal.jdbc2;


import IgniteVersionUtils.VER;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Metadata tests.
 */
public class JdbcMetadataSelfTest extends GridCommonAbstractTest {
    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=pers@modules/clients/src/test/config/jdbc-config.xml";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testResultSetMetaData() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select p.name, o.id as orgId from \"pers\".Person p, \"org\".Organization o where p.orgId = o.id");
            assertNotNull(rs);
            ResultSetMetaData meta = rs.getMetaData();
            assertNotNull(meta);
            assertEquals(2, meta.getColumnCount());
            assertTrue("Person".equalsIgnoreCase(meta.getTableName(1)));
            assertTrue("name".equalsIgnoreCase(meta.getColumnName(1)));
            assertTrue("name".equalsIgnoreCase(meta.getColumnLabel(1)));
            assertEquals(Types.VARCHAR, meta.getColumnType(1));
            assertEquals("VARCHAR", meta.getColumnTypeName(1));
            assertEquals("java.lang.String", meta.getColumnClassName(1));
            assertTrue("Organization".equalsIgnoreCase(meta.getTableName(2)));
            assertTrue("orgId".equalsIgnoreCase(meta.getColumnName(2)));
            assertTrue("orgId".equalsIgnoreCase(meta.getColumnLabel(2)));
            assertEquals(Types.INTEGER, meta.getColumnType(2));
            assertEquals("INTEGER", meta.getColumnTypeName(2));
            assertEquals("java.lang.Integer", meta.getColumnClassName(2));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDecimalAndDateTypeMetaData() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select t.decimal, t.date from \"metaTest\".MetaTest as t");
            assert rs != null;
            ResultSetMetaData meta = rs.getMetaData();
            assert meta != null;
            assert (meta.getColumnCount()) == 2;
            assert "METATEST".equalsIgnoreCase(meta.getTableName(1));
            assert "DECIMAL".equalsIgnoreCase(meta.getColumnName(1));
            assert "DECIMAL".equalsIgnoreCase(meta.getColumnLabel(1));
            assert (meta.getColumnType(1)) == (Types.DECIMAL);
            assert "DECIMAL".equals(meta.getColumnTypeName(1));
            assert "java.math.BigDecimal".equals(meta.getColumnClassName(1));
            assert "METATEST".equalsIgnoreCase(meta.getTableName(2));
            assert "DATE".equalsIgnoreCase(meta.getColumnName(2));
            assert "DATE".equalsIgnoreCase(meta.getColumnLabel(2));
            assert (meta.getColumnType(2)) == (Types.DATE);
            assert "DATE".equals(meta.getColumnTypeName(2));
            assert "java.sql.Date".equals(meta.getColumnClassName(2));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetTables() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, "pers", "%", new String[]{ "TABLE" });
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals("TABLE", rs.getString("TABLE_TYPE"));
            assertEquals(JdbcUtils.CATALOG_NAME, rs.getString("TABLE_CAT"));
            assertEquals("PERSON", rs.getString("TABLE_NAME"));
            rs = meta.getTables(null, "org", "%", new String[]{ "TABLE" });
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals("TABLE", rs.getString("TABLE_TYPE"));
            assertEquals(JdbcUtils.CATALOG_NAME, rs.getString("TABLE_CAT"));
            assertEquals("ORGANIZATION", rs.getString("TABLE_NAME"));
            rs = meta.getTables(null, "pers", "%", null);
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals("TABLE", rs.getString("TABLE_TYPE"));
            assertEquals(JdbcUtils.CATALOG_NAME, rs.getString("TABLE_CAT"));
            assertEquals("PERSON", rs.getString("TABLE_NAME"));
            rs = meta.getTables(null, "org", "%", null);
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals("TABLE", rs.getString("TABLE_TYPE"));
            assertEquals(JdbcUtils.CATALOG_NAME, rs.getString("TABLE_CAT"));
            assertEquals("ORGANIZATION", rs.getString("TABLE_NAME"));
            rs = meta.getTables(null, "PUBLIC", "", new String[]{ "WRONG" });
            assertFalse(rs.next());
        }
    }

    /**
     * Negative scenarios for catalog name.
     * Perform metadata lookups, that use incorrect catalog names.
     */
    @Test
    public void testCatalogWithNotExistingName() throws SQLException {
        checkNoEntitiesFoundForCatalog("");
        checkNoEntitiesFoundForCatalog("NOT_EXISTING_CATALOG");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetColumns() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, "pers", "PERSON", "%");
            assertNotNull(rs);
            assertEquals(24, rs.getMetaData().getColumnCount());
            Collection<String> names = new ArrayList<>(2);
            names.add("NAME");
            names.add("AGE");
            names.add("ORGID");
            int cnt = 0;
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                assertTrue(names.remove(name));
                if ("NAME".equals(name)) {
                    assertEquals(Types.VARCHAR, rs.getInt("DATA_TYPE"));
                    assertEquals("VARCHAR", rs.getString("TYPE_NAME"));
                    assertEquals(0, rs.getInt("NULLABLE"));
                    assertEquals(0, rs.getInt(11));// nullable column by index

                    assertEquals("NO", rs.getString("IS_NULLABLE"));
                } else
                    if ("AGE".equals(name)) {
                        assertEquals(Types.INTEGER, rs.getInt("DATA_TYPE"));
                        assertEquals("INTEGER", rs.getString("TYPE_NAME"));
                        assertEquals(0, rs.getInt("NULLABLE"));
                        assertEquals(0, rs.getInt(11));// nullable column by index

                        assertEquals("NO", rs.getString("IS_NULLABLE"));
                    } else
                        if ("ORGID".equals(name)) {
                            assertEquals(Types.INTEGER, rs.getInt("DATA_TYPE"));
                            assertEquals("INTEGER", rs.getString("TYPE_NAME"));
                            assertEquals(1, rs.getInt("NULLABLE"));
                            assertEquals(1, rs.getInt(11));// nullable column by index

                            assertEquals("YES", rs.getString("IS_NULLABLE"));
                        }


                cnt++;
            } 
            assertTrue(names.isEmpty());
            assertEquals(3, cnt);
            rs = meta.getColumns(null, "org", "ORGANIZATION", "%");
            assertNotNull(rs);
            names.add("ID");
            names.add("NAME");
            cnt = 0;
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                assertTrue(names.remove(name));
                if ("id".equals(name)) {
                    assertEquals(Types.INTEGER, rs.getInt("DATA_TYPE"));
                    assertEquals("INTEGER", rs.getString("TYPE_NAME"));
                    assertEquals(0, rs.getInt("NULLABLE"));
                    assertEquals(0, rs.getInt(11));// nullable column by index

                    assertEquals("NO", rs.getString("IS_NULLABLE"));
                } else
                    if ("name".equals(name)) {
                        assertEquals(Types.VARCHAR, rs.getInt("DATA_TYPE"));
                        assertEquals("VARCHAR", rs.getString("TYPE_NAME"));
                        assertEquals(1, rs.getInt("NULLABLE"));
                        assertEquals(1, rs.getInt(11));// nullable column by index

                        assertEquals("YES", rs.getString("IS_NULLABLE"));
                    }

                cnt++;
            } 
            assertTrue(names.isEmpty());
            assertEquals(2, cnt);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMetadataResultSetClose() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL);ResultSet tbls = conn.getMetaData().getTables(null, null, "%", null)) {
            int colCnt = tbls.getMetaData().getColumnCount();
            while (tbls.next()) {
                for (int i = 0; i < colCnt; i++)
                    tbls.getObject((i + 1));

            } 
        } catch (Exception ignored) {
            fail();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIndexMetadata() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL);ResultSet rs = conn.getMetaData().getIndexInfo(null, "pers", "PERSON", false, false)) {
            int cnt = 0;
            while (rs.next()) {
                String idxName = rs.getString("INDEX_NAME");
                String field = rs.getString("COLUMN_NAME");
                String ascOrDesc = rs.getString("ASC_OR_DESC");
                assertEquals(DatabaseMetaData.tableIndexOther, rs.getInt("TYPE"));
                if ("PERSON_ORGID_ASC_IDX".equals(idxName)) {
                    assertEquals("ORGID", field);
                    assertEquals("A", ascOrDesc);
                } else
                    if ("PERSON_NAME_ASC_AGE_DESC_IDX".equals(idxName)) {
                        if ("NAME".equals(field))
                            assertEquals("A", ascOrDesc);
                        else
                            if ("AGE".equals(field))
                                assertEquals("D", ascOrDesc);
                            else
                                fail(("Unexpected field: " + field));


                    } else
                        fail(("Unexpected index: " + idxName));


                cnt++;
            } 
            assertEquals(3, cnt);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrimaryKeyMetadata() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, null);
            // TABLE_SCHEM.TABLE_NAME.PK_NAME.COLUMN_NAME
            Set<String> expectedPks = new HashSet<>(Arrays.asList("org.ORGANIZATION.PK_org_ORGANIZATION._KEY", "pers.PERSON.PK_pers_PERSON._KEY", "dep.DEPARTMENT.PK_dep_DEPARTMENT._KEY", "PUBLIC.TEST.PK_PUBLIC_TEST.ID", "PUBLIC.TEST.PK_PUBLIC_TEST.NAME", "PUBLIC.Quoted.PK_PUBLIC_Quoted.Id", "PUBLIC.TEST_DECIMAL_COLUMN.ID.ID", "metaTest.METATEST.PK_metaTest_METATEST._KEY"));
            Set<String> actualPks = new HashSet<>(expectedPks.size());
            while (rs.next()) {
                actualPks.add((((((((rs.getString("TABLE_SCHEM")) + '.') + (rs.getString("TABLE_NAME"))) + '.') + (rs.getString("PK_NAME"))) + '.') + (rs.getString("COLUMN_NAME"))));
            } 
            assertEquals("Metadata contains unexpected primary keys info.", expectedPks, actualPks);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParametersMetadata() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            conn.setSchema("pers");
            PreparedStatement stmt = conn.prepareStatement("select orgId from Person p where p.name > ? and p.orgId > ?");
            ParameterMetaData meta = stmt.getParameterMetaData();
            assertNotNull(meta);
            assertEquals(2, meta.getParameterCount());
            assertEquals(Types.VARCHAR, meta.getParameterType(1));
            assertEquals(ParameterMetaData.parameterNullableUnknown, meta.isNullable(1));
            assertEquals(Integer.MAX_VALUE, meta.getPrecision(1));
            assertEquals(Types.INTEGER, meta.getParameterType(2));
            assertEquals(ParameterMetaData.parameterNullableUnknown, meta.isNullable(2));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchemasMetadata() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            ResultSet rs = conn.getMetaData().getSchemas();
            Set<String> expectedSchemas = new HashSet<>(Arrays.asList("pers", "org", "metaTest", "dep", "PUBLIC"));
            Set<String> schemas = new HashSet<>();
            while (rs.next()) {
                schemas.add(rs.getString(1));
                assertEquals("There is only one possible catalog.", JdbcUtils.CATALOG_NAME, rs.getString(2));
            } 
            assertEquals(expectedSchemas, schemas);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testVersions() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcMetadataSelfTest.BASE_URL)) {
            assertEquals("Apache Ignite", conn.getMetaData().getDatabaseProductName());
            assertEquals(JdbcDatabaseMetadata.DRIVER_NAME, conn.getMetaData().getDriverName());
            assertEquals(VER.toString(), conn.getMetaData().getDatabaseProductVersion());
            assertEquals(VER.toString(), conn.getMetaData().getDriverVersion());
            assertEquals(VER.major(), conn.getMetaData().getDatabaseMajorVersion());
            assertEquals(VER.major(), conn.getMetaData().getDriverMajorVersion());
            assertEquals(VER.minor(), conn.getMetaData().getDatabaseMinorVersion());
            assertEquals(VER.minor(), conn.getMetaData().getDriverMinorVersion());
            assertEquals(4, conn.getMetaData().getJDBCMajorVersion());
            assertEquals(1, conn.getMetaData().getJDBCMinorVersion());
        }
    }

    /**
     * Person.
     */
    private static class Person implements Serializable {
        /**
         * Name.
         */
        @QuerySqlField(index = false)
        private final String name;

        /**
         * Age.
         */
        @QuerySqlField
        private final int age;

        /**
         * Organization ID.
         */
        @QuerySqlField
        private final int orgId;

        /**
         *
         *
         * @param name
         * 		Name.
         * @param age
         * 		Age.
         * @param orgId
         * 		Organization ID.
         */
        private Person(String name, int age, int orgId) {
            assert !(F.isEmpty(name));
            assert age > 0;
            assert orgId > 0;
            this.name = name;
            this.age = age;
            this.orgId = orgId;
        }
    }

    /**
     * Organization.
     */
    private static class Organization implements Serializable {
        /**
         * ID.
         */
        @QuerySqlField
        private final int id;

        /**
         * Name.
         */
        @QuerySqlField(index = false)
        private final String name;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        private Organization(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /**
     * Meta Test.
     */
    private static class MetaTest implements Serializable {
        /**
         * ID.
         */
        @QuerySqlField
        private final int id;

        /**
         * Date.
         */
        @QuerySqlField
        private final Date date;

        /**
         * decimal.
         */
        @QuerySqlField
        private final BigDecimal decimal;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param date
         * 		Date.
         */
        private MetaTest(int id, Date date, BigDecimal decimal) {
            this.id = id;
            this.date = date;
            this.decimal = decimal;
        }
    }

    /**
     * Department.
     */
    @SuppressWarnings("UnusedDeclaration")
    private static class Department implements Serializable {
        /**
         * ID.
         */
        @QuerySqlField
        private final int id;

        /**
         * Name.
         */
        @QuerySqlField(precision = 43)
        private final String name;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        private Department(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

