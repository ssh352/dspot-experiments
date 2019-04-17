package com.alibaba.druid.bvt.sql.eval;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class EvalMethodLogTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.log(1), SQLEvalVisitorUtils.evalExpr(MYSQL, "log(1)"));
        Assert.assertEquals(Math.log(1.001), SQLEvalVisitorUtils.evalExpr(MYSQL, "log(1.001)"));
        Assert.assertEquals(Math.log(0), SQLEvalVisitorUtils.evalExpr(MYSQL, "log(0)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "log()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(MYSQL, "log(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
