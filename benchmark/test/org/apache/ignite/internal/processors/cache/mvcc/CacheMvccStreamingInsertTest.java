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
package org.apache.ignite.internal.processors.cache.mvcc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.IgniteCache;
import org.junit.Test;


/**
 *
 */
public class CacheMvccStreamingInsertTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private IgniteCache<Object, Object> sqlNexus;

    /**
     *
     */
    private Connection conn;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStreamingInsertWithoutOverwrite() throws Exception {
        conn.createStatement().execute(("SET STREAMING 1 BATCH_SIZE 2 ALLOW_OVERWRITE 0 " + " PER_NODE_BUFFER_SIZE 1000 FLUSH_FREQUENCY 100"));
        sqlNexus.query(CacheMvccStreamingInsertTest.q("insert into person values(1, 'ivan')"));
        PreparedStatement batchStmt = conn.prepareStatement("insert into person values(?, ?)");
        batchStmt.setInt(1, 1);
        batchStmt.setString(2, "foo");
        batchStmt.addBatch();
        batchStmt.setInt(1, 2);
        batchStmt.setString(2, "bar");
        batchStmt.addBatch();
        TimeUnit.MILLISECONDS.sleep(500);
        List<List<?>> rows = sqlNexus.query(CacheMvccStreamingInsertTest.q("select * from person")).getAll();
        List<List<?>> exp = Arrays.asList(Arrays.asList(1, "ivan"), Arrays.asList(2, "bar"));
        assertEquals(exp, rows);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateWithOverwrite() throws Exception {
        conn.createStatement().execute(("SET STREAMING 1 BATCH_SIZE 2 ALLOW_OVERWRITE 1 " + " PER_NODE_BUFFER_SIZE 1000 FLUSH_FREQUENCY 100"));
        sqlNexus.query(CacheMvccStreamingInsertTest.q("insert into person values(1, 'ivan')"));
        PreparedStatement batchStmt = conn.prepareStatement("insert into person values(?, ?)");
        batchStmt.setInt(1, 1);
        batchStmt.setString(2, "foo");
        batchStmt.addBatch();
        batchStmt.setInt(1, 2);
        batchStmt.setString(2, "bar");
        batchStmt.addBatch();
        TimeUnit.MILLISECONDS.sleep(500);
        List<List<?>> rows = sqlNexus.query(CacheMvccStreamingInsertTest.q("select * from person")).getAll();
        List<List<?>> exp = Arrays.asList(Arrays.asList(1, "foo"), Arrays.asList(2, "bar"));
        assertEquals(exp, rows);
    }
}

