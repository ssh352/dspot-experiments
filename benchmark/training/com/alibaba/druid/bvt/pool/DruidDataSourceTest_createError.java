package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????createError
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_createError extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.init();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
