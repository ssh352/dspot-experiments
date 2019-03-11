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
package org.apache.ignite.internal.processors.cache.distributed.near;


import java.util.Arrays;
import java.util.List;
import javax.cache.CacheException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests distributed SQL query cancel related scenarios.
 */
public class IgniteCacheDistributedQueryCancelSelfTest extends GridCommonAbstractTest {
    /**
     * Grids count.
     */
    private static final int GRIDS_COUNT = 3;

    /**
     * Cache size.
     */
    public static final int CACHE_SIZE = 10000;

    /**
     * Value size.
     */
    public static final int VAL_SIZE = 16;

    /**
     *
     */
    private static final String QUERY = "select a._val, b._val from String a, String b";

    /**
     *
     */
    @Test
    public void testQueryCancelsOnGridShutdown() throws Exception {
        try (Ignite client = startGrid("client")) {
            IgniteCache<Object, Object> cache = client.cache(DEFAULT_CACHE_NAME);
            assertEquals(0, cache.localSize());
            int p = 1;
            for (int i = 1; i <= (IgniteCacheDistributedQueryCancelSelfTest.CACHE_SIZE); i++) {
                char[] tmp = new char[IgniteCacheDistributedQueryCancelSelfTest.VAL_SIZE];
                Arrays.fill(tmp, ' ');
                cache.put(i, new String(tmp));
                if ((i / ((float) (IgniteCacheDistributedQueryCancelSelfTest.CACHE_SIZE))) >= (p / 10.0F)) {
                    log().info(((("Loaded " + i) + " of ") + (IgniteCacheDistributedQueryCancelSelfTest.CACHE_SIZE)));
                    p++;
                }
            }
            SqlFieldsQuery qry = new SqlFieldsQuery(IgniteCacheDistributedQueryCancelSelfTest.QUERY);
            IgniteInternalFuture<?> fut = multithreadedAsync(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new IgniteException(e);
                    }
                    for (Ignite g : G.allGrids())
                        if (!(g.configuration().isClientMode()))
                            stopGrid(g.name(), true);


                }
            }, 1);
            try {
                final QueryCursor<List<?>> cursor = cache.query(qry);
                cursor.iterator();
            } catch (CacheException ignored) {
                // No-op.
            }
            fut.get();
            // Test must exit gracefully.
        }
    }

    /**
     *
     */
    @Test
    public void testQueryResponseFailCode() throws Exception {
        try (Ignite client = startGrid("client")) {
            CacheConfiguration<Integer, Integer> cfg = new CacheConfiguration(DEFAULT_CACHE_NAME);
            cfg.setSqlFunctionClasses(IgniteCacheDistributedQueryCancelSelfTest.Functions.class);
            cfg.setIndexedTypes(Integer.class, Integer.class);
            cfg.setName("test");
            IgniteCache<Integer, Integer> cache = client.getOrCreateCache(cfg);
            cache.put(1, 1);
            QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("select fail() from Integer"));
            try {
                qry.getAll();
                fail();
            } catch (Exception e) {
                assertTrue((e instanceof CacheException));
            }
        }
    }

    /**
     *
     */
    public static class Functions {
        /**
         *
         */
        @QuerySqlFunction
        public static int fail() {
            throw new IllegalArgumentException();
        }
    }
}

