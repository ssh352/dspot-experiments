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
package org.apache.ignite.spark;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.lang.IgniteOutClosure;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Test;
import scala.Tuple2;


/**
 * Tests for {@link JavaIgniteRDD} (embedded mode).
 */
public class JavaEmbeddedIgniteRDDSelfTest extends GridCommonAbstractTest {
    /**
     * For Ignite instance names generation
     */
    private static AtomicInteger cntr = new AtomicInteger(1);

    /**
     * Ignite instance names.
     */
    private static ThreadLocal<Integer> igniteInstanceNames = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return JavaEmbeddedIgniteRDDSelfTest.cntr.getAndIncrement();
        }
    };

    /**
     * Grid count.
     */
    private static final int GRID_CNT = 3;

    /**
     * Keys count.
     */
    private static final int KEYS_CNT = 10000;

    /**
     * Cache name.
     */
    private static final String PARTITIONED_CACHE_NAME = "partitioned";

    /**
     * Sum function.
     */
    private static final Function2<Integer, Integer, Integer> SUM_F = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer x, Integer y) {
            return x + y;
        }
    };

    /**
     * To pair function.
     */
    private static final PairFunction<Integer, String, String> TO_PAIR_F = new PairFunction<Integer, String, String>() {
        /**
         * {@inheritDoc }
         */
        @Override
        public Tuple2<String, String> call(Integer i) {
            return new Tuple2(String.valueOf(i), ("val" + i));
        }
    };

    /**
     * (String, Integer); pair to Integer value function.
     */
    private static final Function<Tuple2<String, Integer>, Integer> STR_INT_PAIR_TO_INT_F = new JavaEmbeddedIgniteRDDSelfTest.PairToValueFunction();

    /**
     * (String, Entity) pair to Entity value function.
     */
    private static final Function<Tuple2<String, Entity>, Entity> STR_ENTITY_PAIR_TO_ENTITY_F = new JavaEmbeddedIgniteRDDSelfTest.PairToValueFunction();

    /**
     * Integer to entity function.
     */
    private static final PairFunction<Integer, String, Entity> INT_TO_ENTITY_F = new PairFunction<Integer, String, Entity>() {
        @Override
        public Tuple2<String, Entity> call(Integer i) throws Exception {
            return new Tuple2(String.valueOf(i), new Entity(i, ("name" + i), (i * 100)));
        }
    };

    /**
     * Default constructor.
     */
    public JavaEmbeddedIgniteRDDSelfTest() {
        super(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStoreDataToIgnite() throws Exception {
        JavaSparkContext sc = createContext();
        JavaIgniteContext<String, String> ic = null;
        try {
            ic = new JavaIgniteContext(sc, new JavaEmbeddedIgniteRDDSelfTest.IgniteConfigProvider(), false);
            ic.fromCache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME).savePairs(sc.parallelize(F.range(0, JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT), JavaEmbeddedIgniteRDDSelfTest.GRID_CNT).mapToPair(JavaEmbeddedIgniteRDDSelfTest.TO_PAIR_F), true);
            Ignite ignite = ic.ignite();
            IgniteCache<String, String> cache = ignite.cache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME);
            for (int i = 0; i < (JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT); i++) {
                String val = cache.get(String.valueOf(i));
                assertNotNull(("Value was not put to cache for key: " + i), val);
                assertEquals(("Invalid value stored for key: " + i), ("val" + i), val);
            }
        } finally {
            if (ic != null)
                ic.close(true);

            sc.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReadDataFromIgnite() throws Exception {
        JavaSparkContext sc = createContext();
        JavaIgniteContext<String, Integer> ic = null;
        try {
            ic = new JavaIgniteContext(sc, new JavaEmbeddedIgniteRDDSelfTest.IgniteConfigProvider(), false);
            Ignite ignite = ic.ignite();
            IgniteCache<String, Integer> cache = ignite.cache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME);
            for (int i = 0; i < (JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT); i++)
                cache.put(String.valueOf(i), i);

            JavaRDD<Integer> values = ic.fromCache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME).map(JavaEmbeddedIgniteRDDSelfTest.STR_INT_PAIR_TO_INT_F);
            int sum = values.fold(0, JavaEmbeddedIgniteRDDSelfTest.SUM_F);
            int expSum = ((((JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT) * (JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT)) + (JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT)) / 2) - (JavaEmbeddedIgniteRDDSelfTest.KEYS_CNT);
            assertEquals(expSum, sum);
        } finally {
            if (ic != null)
                ic.close(true);

            sc.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryObjectsFromIgnite() throws Exception {
        JavaSparkContext sc = createContext();
        JavaIgniteContext<String, Entity> ic = null;
        try {
            ic = new JavaIgniteContext(sc, new JavaEmbeddedIgniteRDDSelfTest.IgniteConfigProvider(), false);
            JavaIgniteRDD<String, Entity> cache = ic.fromCache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME);
            int cnt = 1001;
            cache.savePairs(sc.parallelize(F.range(0, cnt), JavaEmbeddedIgniteRDDSelfTest.GRID_CNT).mapToPair(JavaEmbeddedIgniteRDDSelfTest.INT_TO_ENTITY_F), true);
            List<Entity> res = cache.objectSql("Entity", "name = ? and salary = ?", "name50", 5000).map(JavaEmbeddedIgniteRDDSelfTest.STR_ENTITY_PAIR_TO_ENTITY_F).collect();
            assertEquals("Invalid result length", 1, res.size());
            assertEquals("Invalid result", 50, res.get(0).id());
            assertEquals("Invalid result", "name50", res.get(0).name());
            assertEquals("Invalid result", 5000, res.get(0).salary());
            // Ignite ignite = ic.ignite();
            // IgniteCache<Object, Object> underCache = ignite.cache(PARTITIONED_CACHE_NAME);
            // assertEquals("Invalid total count", cnt, underCache.size());
            assertEquals("Invalid count", 500, cache.objectSql("Entity", "id > 500").count());
        } finally {
            if (ic != null)
                ic.close(true);

            sc.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryFieldsFromIgnite() throws Exception {
        JavaSparkContext sc = createContext();
        JavaIgniteContext<String, Entity> ic = null;
        try {
            ic = new JavaIgniteContext(sc, new JavaEmbeddedIgniteRDDSelfTest.IgniteConfigProvider(), false);
            JavaIgniteRDD<String, Entity> cache = ic.fromCache(JavaEmbeddedIgniteRDDSelfTest.PARTITIONED_CACHE_NAME);
            cache.savePairs(sc.parallelize(F.range(0, 1001), JavaEmbeddedIgniteRDDSelfTest.GRID_CNT).mapToPair(JavaEmbeddedIgniteRDDSelfTest.INT_TO_ENTITY_F), true);
            Dataset<Row> df = cache.sql("select id, name, salary from Entity where name = ? and salary = ?", "name50", 5000);
            df.printSchema();
            Row[] res = ((Row[]) (df.collect()));
            assertEquals("Invalid result length", 1, res.length);
            assertEquals("Invalid result", 50, res[0].get(0));
            assertEquals("Invalid result", "name50", res[0].get(1));
            assertEquals("Invalid result", 5000, res[0].get(2));
            Column exp = new Column("NAME").equalTo("name50").and(new Column("SALARY").equalTo(5000));
            Dataset<Row> df0 = cache.sql("select id, name, salary from Entity").where(exp);
            df.printSchema();
            Row[] res0 = ((Row[]) (df0.collect()));
            assertEquals("Invalid result length", 1, res0.length);
            assertEquals("Invalid result", 50, res0[0].get(0));
            assertEquals("Invalid result", "name50", res0[0].get(1));
            assertEquals("Invalid result", 5000, res0[0].get(2));
            assertEquals("Invalid count", 500, cache.sql("select id from Entity where id > 500").count());
        } finally {
            if (ic != null)
                ic.close(true);

            sc.stop();
        }
    }

    /**
     * Ignite configiration provider.
     */
    static class IgniteConfigProvider implements IgniteOutClosure<IgniteConfiguration> {
        /**
         * {@inheritDoc }
         */
        @Override
        public IgniteConfiguration apply() {
            try {
                return JavaEmbeddedIgniteRDDSelfTest.getConfiguration(("worker-" + (JavaEmbeddedIgniteRDDSelfTest.igniteInstanceNames.get())), false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *
     *
     * @param <K>
     * 		
     * @param <V>
     * 		
     */
    static class PairToValueFunction<K, V> implements Function<Tuple2<K, V>, V> {
        /**
         * {@inheritDoc }
         */
        @Override
        public V call(Tuple2<K, V> t) throws Exception {
            return t._2();
        }
    }
}
