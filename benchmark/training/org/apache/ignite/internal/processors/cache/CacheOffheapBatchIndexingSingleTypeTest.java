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
package org.apache.ignite.internal.processors.cache;


import java.util.List;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.junit.Test;


/**
 * Tests various cache operations with indexing enabled.
 * Cache contains single type.
 */
public class CacheOffheapBatchIndexingSingleTypeTest extends CacheOffheapBatchIndexingBaseTest {
    /**
     * Tests removal using EntryProcessor.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBatchRemove() throws Exception {
        Ignite ignite = grid(0);
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(new Class<?>[]{ Integer.class, CacheOffheapBatchIndexingBaseTest.Organization.class });
        final IgniteCache<Object, Object> cache = ignite.createCache(ccfg);
        try {
            int iterations = 50;
            while ((iterations--) >= 0) {
                int total = 1000;
                for (int id = 0; id < total; id++)
                    cache.put(id, new CacheOffheapBatchIndexingBaseTest.Organization(id, ("Organization " + id)));

                cache.invoke(0, new org.apache.ignite.cache.CacheEntryProcessor<Object, Object, Object>() {
                    @Override
                    public Object process(MutableEntry<Object, Object> entry, Object... args) {
                        entry.remove();
                        return null;
                    }
                });
                QueryCursor<List<?>> q = cache.query(new SqlFieldsQuery("select _key,_val from Organization where id=0"));
                assertEquals(0, q.getAll().size());
                q = cache.query(new SqlFieldsQuery("select _key,_val from Organization where id=1"));
                assertEquals(1, q.getAll().size());
                assertEquals((total - 1), cache.size());
                cache.removeAll();
            } 
        } finally {
            cache.destroy();
        }
    }

    /**
     *
     */
    @Test
    public void testPutAllAndStreamer() {
        doStreamerBatchTest(50, 1000, new Class<?>[]{ Integer.class, CacheOffheapBatchIndexingBaseTest.Organization.class }, true);
    }

    /**
     *
     */
    @Test
    public void testPuAllSingleEntity() {
        doStreamerBatchTest(50, 1000, new Class<?>[]{ Integer.class, CacheOffheapBatchIndexingBaseTest.Organization.class }, false);
    }
}

