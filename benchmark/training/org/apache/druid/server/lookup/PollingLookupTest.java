/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.lookup;


import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.server.lookup.cache.polling.PollingCacheFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PollingLookupTest {
    private static final Map<String, String> firstLookupMap = ImmutableMap.of("foo", "bar", "bad", "bar", "how about that", "foo", "empty string", "");

    private static final Map<String, String> secondLookupMap = ImmutableMap.of("new-foo", "new-bar", "new-bad", "new-bar");

    private static final long POLL_PERIOD = 1000L;

    @JsonTypeName("mock")
    private static class MockDataFetcher implements DataFetcher {
        private int callNumber = 0;

        @Override
        public Iterable fetchAll() {
            if ((callNumber) == 0) {
                (callNumber)++;
                return PollingLookupTest.firstLookupMap.entrySet();
            }
            return PollingLookupTest.secondLookupMap.entrySet();
        }

        @Nullable
        @Override
        public Object fetch(Object key) {
            return null;
        }

        @Override
        public Iterable fetch(Iterable keys) {
            return null;
        }

        @Override
        public List reverseFetchKeys(Object value) {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PollingLookupTest.MockDataFetcher;
        }
    }

    private final PollingCacheFactory pollingCacheFactory;

    private final DataFetcher dataFetcher = new PollingLookupTest.MockDataFetcher();

    private PollingLookup pollingLookup;

    public PollingLookupTest(PollingCacheFactory pollingCacheFactory) {
        this.pollingCacheFactory = pollingCacheFactory;
    }

    @Test(expected = ISE.class)
    public void testClose() {
        pollingLookup.close();
        pollingLookup.apply("key");
    }

    @Test
    public void testApply() {
        assertMapLookup(PollingLookupTest.firstLookupMap, pollingLookup);
    }

    @Test(timeout = (PollingLookupTest.POLL_PERIOD) * 3)
    public void testApplyAfterDataChange() throws InterruptedException {
        assertMapLookup(PollingLookupTest.firstLookupMap, pollingLookup);
        Thread.sleep(((PollingLookupTest.POLL_PERIOD) * 2));
        assertMapLookup(PollingLookupTest.secondLookupMap, pollingLookup);
    }

    @Test
    public void testUnapply() {
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("foo", "bad"), Sets.newHashSet(pollingLookup.unapply("bar")));
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("how about that"), Sets.newHashSet(pollingLookup.unapply("foo")));
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("empty string"), Sets.newHashSet(pollingLookup.unapply("")));
        Assert.assertEquals("reverse lookup of none existing value should be empty list", Collections.EMPTY_LIST, pollingLookup.unapply("does't exist"));
    }

    @Test
    public void testBulkApply() {
        Map<String, String> map = pollingLookup.applyAll(PollingLookupTest.firstLookupMap.keySet());
        Assert.assertEquals(PollingLookupTest.firstLookupMap, Maps.transformValues(map, new Function<String, String>() {
            @Override
            public String apply(String input) {
                // make sure to rewrite null strings as empty.
                return NullHandling.nullToEmptyIfNeeded(input);
            }
        }));
    }

    @Test
    public void testGetCacheKey() {
        PollingLookup pollingLookup2 = new PollingLookup(1L, dataFetcher, pollingCacheFactory);
        Assert.assertFalse(Arrays.equals(pollingLookup2.getCacheKey(), pollingLookup.getCacheKey()));
    }
}

