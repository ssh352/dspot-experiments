package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.SQLException;
import junit.framework.TestCase;


public class UrlNotSetTest extends TestCase {
    protected DruidDataSource dataSource;

    public void test_wait() throws Exception {
        Exception error = null;
        try {
            DruidPooledConnection conn = dataSource.getConnection();
            conn.close();
        } catch (SQLException ex) {
            error = ex;
        }
        TestCase.assertEquals("url not set", error.getMessage());
    }
}
