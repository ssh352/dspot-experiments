/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.querycache;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryEventTypesTest;
import com.hazelcast.map.QueryCache;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test basic QueryCache operation: create a map, put/update/remove values and assert size of query cache.
 * Parametrized with QueryCache option includeValues true/false & using default and query-cache-natural filtering strategies.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheBasicTest extends HazelcastTestSupport {
    private static final String TEST_MAP_NAME = "EntryListenerEventTypesTestMap";

    private static final String QUERY_CACHE_NAME = "query-cache";

    private Predicate predicate = new SqlPredicate("age > 50");

    private HazelcastInstance instance;

    private IMap<Integer, AbstractEntryEventTypesTest.Person> map;

    private QueryCache queryCache;

    @Parameterized.Parameter
    public boolean includeValues;

    @Parameterized.Parameter(1)
    public boolean useQueryCacheNaturalFilteringStrategy;

    @Parameterized.Parameter(2)
    public boolean useNearCache;

    @Test
    public void entryAdded_whenValueMatchesPredicate() {
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 75));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 1);
    }

    @Test
    public void entryAdded_whenValueOutsidePredicate() {
        // when a value not matching predicate is put
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 25));
        // then querycache does not contain any elements
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 0);
    }

    @Test
    public void entryRemoved_whenValueMatchesPredicate() {
        // when 2 values matching predicate are put & 1 is removed
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 75));
        map.put(2, new AbstractEntryEventTypesTest.Person("a", 95));
        map.remove(1);
        // then size of querycache is 1
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 1);
    }

    @Test
    public void entryRemoved_whenValueOutsidePredicate() {
        // when 2 values not matching predicate are put & 1 is removed
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 15));
        map.put(2, new AbstractEntryEventTypesTest.Person("a", 25));
        map.remove(1);
        // then size of querycache is 0
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 0);
    }

    @Test
    public void entryUpdated_whenOldValueOutside_newValueMatchesPredicate() {
        // when a value not matching predicate is put and is updated to match the predicate
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 15));
        map.replace(1, new AbstractEntryEventTypesTest.Person("a", 85));
        // then size of querycache is 1
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 1);
    }

    @Test
    public void entryUpdated_whenOldValueOutside_newValueOutsidePredicate() {
        // when a value not matching predicate is put and is updated to another value that does not match the predicate
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 15));
        map.replace(1, new AbstractEntryEventTypesTest.Person("a", 25));
        // then size of querycache is 0
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 0);
    }

    @Test
    public void entryUpdated_whenOldValueMatches_newValueMatchesPredicate() {
        // when a value matching predicate is put and is updated to another value that matches the predicate
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 55));
        map.replace(1, new AbstractEntryEventTypesTest.Person("a", 56));
        // then size of querycache is 1
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 1);
    }

    @Test
    public void entryUpdated_whenOldValueMatches_newValueOutsidePredicate() {
        // when a value matching predicate is put and is updated to another value that does not match the predicate
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 55));
        map.replace(1, new AbstractEntryEventTypesTest.Person("a", 15));
        // then size of querycache is 0
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.size();
            }
        }, 0);
    }

    @Test
    public void testKeySet_withFullKeyScan() {
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 55));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.keySet().size();
            }
        }, 1);
    }

    @Test
    public void testEntrySet_withFullKeyScan() {
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 55));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return queryCache.entrySet().size();
            }
        }, 1);
    }
}
