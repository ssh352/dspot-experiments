package com.alibaba.druid.bvt.sql.hive;


import JdbcConstants.HIVE;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class HiveSelectTest_2_lateralview extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT pageid, adid\n" + "FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid;";// 

        TestCase.assertEquals(("SELECT pageid, adid\n" + ("FROM pageAds\n" + "\tLATERAL VIEW explode(adid_list) adTable AS adid;")), SQLUtils.formatHive(sql));
        TestCase.assertEquals(("select pageid, adid\n" + ("from pageAds\n" + "\tlateral view explode(adid_list) adTable as adid;")), SQLUtils.formatHive(sql, DEFAULT_LCASE_FORMAT_OPTION));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, HIVE);
        SQLStatement stmt = statementList.get(0);
        TestCase.assertEquals(1, statementList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(HIVE);
        stmt.accept(visitor);
        System.out.println(("Tables : " + (visitor.getTables())));
        System.out.println(("fields : " + (visitor.getColumns())));
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        TestCase.assertEquals(1, visitor.getTables().size());
        TestCase.assertEquals(2, visitor.getColumns().size());
        TestCase.assertEquals(0, visitor.getConditions().size());
        TestCase.assertTrue(visitor.containsColumn("pageAds", "adid_list"));
        TestCase.assertTrue(visitor.containsColumn("pageAds", "pageid"));
    }
}
