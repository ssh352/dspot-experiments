/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.elasticsearch;


import QueryBuilders.QueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 * Check that internal queries are correctly converted to ES search query (as JSON)
 */
public class QueryBuildersTest {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Test for simple scalar terms (boolean, int etc.)
     *
     * @throws Exception
     * 		not expected
     */
    @Test
    public void term() throws Exception {
        Assert.assertEquals("{\"term\":{\"foo\":\"bar\"}}", toJson(QueryBuilders.termQuery("foo", "bar")));
        Assert.assertEquals("{\"term\":{\"bar\":\"foo\"}}", toJson(QueryBuilders.termQuery("bar", "foo")));
        Assert.assertEquals("{\"term\":{\"foo\":\"A\"}}", toJson(QueryBuilders.termQuery("foo", 'A')));
        Assert.assertEquals("{\"term\":{\"foo\":true}}", toJson(QueryBuilders.termQuery("foo", true)));
        Assert.assertEquals("{\"term\":{\"foo\":false}}", toJson(QueryBuilders.termQuery("foo", false)));
        Assert.assertEquals("{\"term\":{\"foo\":0}}", toJson(QueryBuilders.termQuery("foo", ((byte) (0)))));
        Assert.assertEquals("{\"term\":{\"foo\":123}}", toJson(QueryBuilders.termQuery("foo", ((long) (123)))));
        Assert.assertEquals("{\"term\":{\"foo\":41}}", toJson(QueryBuilders.termQuery("foo", ((short) (41)))));
        Assert.assertEquals("{\"term\":{\"foo\":42.42}}", toJson(QueryBuilders.termQuery("foo", 42.42)));
        Assert.assertEquals("{\"term\":{\"foo\":1.1}}", toJson(QueryBuilders.termQuery("foo", 1.1F)));
        Assert.assertEquals("{\"term\":{\"foo\":1}}", toJson(QueryBuilders.termQuery("foo", new BigDecimal(1))));
        Assert.assertEquals("{\"term\":{\"foo\":121}}", toJson(QueryBuilders.termQuery("foo", new BigInteger("121"))));
        Assert.assertEquals("{\"term\":{\"foo\":111}}", toJson(QueryBuilders.termQuery("foo", new AtomicLong(111))));
        Assert.assertEquals("{\"term\":{\"foo\":222}}", toJson(QueryBuilders.termQuery("foo", new AtomicInteger(222))));
        Assert.assertEquals("{\"term\":{\"foo\":true}}", toJson(QueryBuilders.termQuery("foo", new AtomicBoolean(true))));
    }

    @Test
    public void terms() throws Exception {
        Assert.assertEquals("{\"terms\":{\"foo\":[]}}", toJson(QueryBuilders.termsQuery("foo", Collections.emptyList())));
        Assert.assertEquals("{\"terms\":{\"bar\":[]}}", toJson(QueryBuilders.termsQuery("bar", Collections.emptySet())));
        Assert.assertEquals("{\"terms\":{\"singleton\":[0]}}", toJson(QueryBuilders.termsQuery("singleton", Collections.singleton(0))));
        Assert.assertEquals("{\"terms\":{\"foo\":[true]}}", toJson(QueryBuilders.termsQuery("foo", Collections.singleton(true))));
        Assert.assertEquals("{\"terms\":{\"foo\":[\"bar\"]}}", toJson(QueryBuilders.termsQuery("foo", Collections.singleton("bar"))));
        Assert.assertEquals("{\"terms\":{\"foo\":[\"bar\"]}}", toJson(QueryBuilders.termsQuery("foo", Collections.singletonList("bar"))));
        Assert.assertEquals("{\"terms\":{\"foo\":[true,false]}}", toJson(QueryBuilders.termsQuery("foo", Arrays.asList(true, false))));
        Assert.assertEquals("{\"terms\":{\"foo\":[1,2,3]}}", toJson(QueryBuilders.termsQuery("foo", Arrays.asList(1, 2, 3))));
        Assert.assertEquals("{\"terms\":{\"foo\":[1.1,2.2,3.3]}}", toJson(QueryBuilders.termsQuery("foo", Arrays.asList(1.1, 2.2, 3.3))));
    }

    @Test
    public void boolQuery() throws Exception {
        QueryBuilders.QueryBuilder q1 = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("foo", "bar"));
        Assert.assertEquals("{\"bool\":{\"must\":{\"term\":{\"foo\":\"bar\"}}}}", toJson(q1));
        QueryBuilders.QueryBuilder q2 = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("f1", "v1")).must(QueryBuilders.termQuery("f2", "v2"));
        Assert.assertEquals("{\"bool\":{\"must\":[{\"term\":{\"f1\":\"v1\"}},{\"term\":{\"f2\":\"v2\"}}]}}", toJson(q2));
        QueryBuilders.QueryBuilder q3 = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("f1", "v1"));
        Assert.assertEquals("{\"bool\":{\"must_not\":{\"term\":{\"f1\":\"v1\"}}}}", toJson(q3));
    }

    @Test
    public void exists() throws Exception {
        Assert.assertEquals("{\"exists\":{\"field\":\"foo\"}}", toJson(QueryBuilders.existsQuery("foo")));
    }

    @Test
    public void range() throws Exception {
        Assert.assertEquals("{\"range\":{\"f\":{\"lt\":0}}}", toJson(QueryBuilders.rangeQuery("f").lt(0)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gt\":0}}}", toJson(QueryBuilders.rangeQuery("f").gt(0)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gte\":0}}}", toJson(QueryBuilders.rangeQuery("f").gte(0)));
        Assert.assertEquals("{\"range\":{\"f\":{\"lte\":0}}}", toJson(QueryBuilders.rangeQuery("f").lte(0)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gt\":1,\"lt\":2}}}", toJson(QueryBuilders.rangeQuery("f").gt(1).lt(2)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gt\":11,\"lt\":0}}}", toJson(QueryBuilders.rangeQuery("f").lt(0).gt(11)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gt\":1,\"lte\":2}}}", toJson(QueryBuilders.rangeQuery("f").gt(1).lte(2)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gte\":1,\"lte\":\"zz\"}}}", toJson(QueryBuilders.rangeQuery("f").gte(1).lte("zz")));
        Assert.assertEquals("{\"range\":{\"f\":{\"gte\":1}}}", toJson(QueryBuilders.rangeQuery("f").gte(1)));
        Assert.assertEquals("{\"range\":{\"f\":{\"gte\":\"zz\"}}}", toJson(QueryBuilders.rangeQuery("f").gte("zz")));
        Assert.assertEquals("{\"range\":{\"f\":{\"gt\":\"a\",\"lt\":\"z\"}}}", toJson(QueryBuilders.rangeQuery("f").gt("a").lt("z")));
        Assert.assertEquals("{\"range\":{\"f\":{\"gte\":3}}}", toJson(QueryBuilders.rangeQuery("f").gt(1).gt(2).gte(3)));
        Assert.assertEquals("{\"range\":{\"f\":{\"lte\":3}}}", toJson(QueryBuilders.rangeQuery("f").lt(1).lt(2).lte(3)));
    }

    @Test
    public void matchAll() throws IOException {
        Assert.assertEquals("{\"match_all\":{}}", toJson(QueryBuilders.matchAll()));
    }
}

/**
 * End QueryBuildersTest.java
 */
