package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.mock.MockNClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import junit.framework.TestCase;
import org.junit.Assert;


public class FilterChainImplTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_size() {
        Assert.assertEquals(dataSource.getProxyFilters().size(), getFilterSize());
    }

    public void test_unwrap() throws Exception {
        Assert.assertNull(unwrap(null, null));
    }

    public void test_unwrap_5() throws Exception {
        Assert.assertNull(new com.alibaba.druid.filter.FilterChainImpl(dataSource).wrap(((ConnectionProxy) (dataSource.getConnection().getConnection())), ((java.sql.Clob) (null))));
    }

    public void test_unwrap_6() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertTrue(((new com.alibaba.druid.filter.FilterChainImpl(dataSource).wrap(((ConnectionProxy) (dataSource.getConnection().getConnection())), new MockNClob())) instanceof NClob));
        conn.close();
    }

    public void test_unwrap_8() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertTrue(((new com.alibaba.druid.filter.FilterChainImpl(dataSource).wrap(((ConnectionProxy) (dataSource.getConnection().getConnection())), ((java.sql.Clob) (new MockNClob())))) instanceof NClob));
        conn.close();
    }

    public void test_unwrap_7() throws Exception {
        Assert.assertNull(new com.alibaba.druid.filter.FilterChainImpl(dataSource).wrap(((ConnectionProxy) (dataSource.getConnection().getConnection())), ((NClob) (null))));
    }

    public void test_unwrap_9() throws Exception {
        Assert.assertNull(wrap(((com.alibaba.druid.proxy.jdbc.StatementProxy) (null)), ((NClob) (null))));
    }

    public void test_getUnicodeStream() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getUnicodeStream(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getUnicodeStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getUnicodeStream("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getRef() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getRef(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getRef_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getRef("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getArray() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getArray(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getArray_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getArray("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getURL() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getURL(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getURL_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getURL("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getRowId() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getRowId(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getRowId_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getRowId("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNClob() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNClob(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNClob_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNClob("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getSQLXML() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getSQLXML(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getSQLXML_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getSQLXML("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNString() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNString(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNString_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNString("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNCharacterStream() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNCharacterStream(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getNCharacterStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getNCharacterStream("1"));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getObject() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getObject(1));
        rs.close();
        stmt.close();
        conn.close();
    }

    public void test_getObject_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertNull(rs.getObject("1"));
        rs.close();
        stmt.close();
        conn.close();
    }
}
