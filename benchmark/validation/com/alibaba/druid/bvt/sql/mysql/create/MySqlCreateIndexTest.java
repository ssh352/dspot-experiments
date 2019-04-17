/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.create;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MySqlCreateIndexTest extends MysqlTest {
    @Test
    public void test_one() throws Exception {
        String sql = "create index index1 on t1(col1) comment 'test index' comment 'some test' using btree;";
        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, MYSQL);
        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE INDEX index1 ON t1 (col1) USING BTREE COMMENT 'some test';", output);
    }
}
