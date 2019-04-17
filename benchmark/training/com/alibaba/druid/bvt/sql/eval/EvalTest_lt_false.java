package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalTest_lt_false extends TestCase {
    public void test_long() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? < ?", ((long) (10)), ((byte) (2))));
    }

    public void test_int() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? < ?", ((int) (10)), ((byte) (2))));
    }

    public void test_short() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? < ?", ((short) (10)), ((byte) (2))));
    }

    public void test_byte() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "? < ?", ((byte) (10)), ((byte) (2))));
    }

    public void test_BigInteger() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", BigInteger.TEN, ((byte) (2))));
    }

    public void test_BigDecimal() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", BigDecimal.TEN, ((byte) (2))));
    }

    public void test_float() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", ((float) (3)), ((byte) (2))));
    }

    public void test_double() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", ((double) (3)), ((byte) (2))));
    }

    public void test_String() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", "3", "2"));
    }

    public void test_Date() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(MYSQL, "?<?", new Date(System.currentTimeMillis()), new Date(((System.currentTimeMillis()) - 10))));
    }
}
