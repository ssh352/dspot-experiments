package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class SavepointTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_multi_savepoint() throws Exception {
        Connection conn = dataSource.getConnection();
        MockConnection physicalConn = conn.unwrap(MockConnection.class);
        Assert.assertEquals(true, conn.getAutoCommit());
        Assert.assertEquals(true, physicalConn.getAutoCommit());
        conn.setAutoCommit(false);
        Assert.assertEquals(false, conn.getAutoCommit());
        Assert.assertEquals(false, physicalConn.getAutoCommit());
        Savepoint[] savepoints = new Savepoint[100];
        for (int i = 0; i < (savepoints.length); ++i) {
            Statement stmt = conn.createStatement();
            stmt.execute((("insert t (" + i) + ")"));
            stmt.close();
            savepoints[i] = conn.setSavepoint();
            Assert.assertEquals((i + 1), physicalConn.getSavepoints().size());
            for (int j = 0; j <= i; ++j) {
                Assert.assertTrue(physicalConn.getSavepoints().contains(savepoints[j]));
            }
        }
        // rollback single
        conn.rollback(savepoints[99]);
        Assert.assertEquals(99, physicalConn.getSavepoints().size());
        // release single
        conn.releaseSavepoint(savepoints[97]);
        Assert.assertEquals(98, physicalConn.getSavepoints().size());
        // rollback multi
        conn.rollback(savepoints[90]);
        Assert.assertEquals(90, physicalConn.getSavepoints().size());
        // rollback all
        conn.rollback();
        Assert.assertEquals(0, physicalConn.getSavepoints().size());
        conn.close();
    }
}
