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
package org.apache.camel.util;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.camel.TestSupport;
import org.apache.camel.support.LRUSoftCache;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class LRUSoftCacheTest extends TestSupport {
    @Test
    public void testLRUSoftCacheGetAndPut() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        cache.put(1, "foo");
        cache.put(2, "bar");
        Assert.assertEquals("foo", cache.get(1));
        Assert.assertEquals("bar", cache.get(2));
        Assert.assertEquals(null, cache.get(3));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testLRUSoftCacheHitsAndMisses() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        cache.put(1, "foo");
        cache.put(2, "bar");
        Assert.assertEquals(0, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get(1);
        Assert.assertEquals(1, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get(3);
        Assert.assertEquals(1, cache.getHits());
        Assert.assertEquals(1, cache.getMisses());
        cache.get(2);
        Assert.assertEquals(2, cache.getHits());
        Assert.assertEquals(1, cache.getMisses());
    }

    @Test
    public void testLRUSoftCachePutOverride() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        Object old = cache.put(1, "foo");
        Assert.assertNull(old);
        old = cache.put(2, "bar");
        Assert.assertNull(old);
        Assert.assertEquals("foo", cache.get(1));
        Assert.assertEquals("bar", cache.get(2));
        old = cache.put(1, "changed");
        Assert.assertEquals("foo", old);
        Assert.assertEquals("changed", cache.get(1));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testLRUSoftCachePutAll() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, "foo");
        map.put(2, "bar");
        cache.putAll(map);
        Assert.assertEquals("foo", cache.get(1));
        Assert.assertEquals("bar", cache.get(2));
        Assert.assertEquals(null, cache.get(3));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testLRUSoftCachePutAllAnotherLRUSoftCache() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        LRUSoftCache<Integer, Object> cache2 = new LRUSoftCache(1000);
        cache2.put(1, "foo");
        cache2.put(2, "bar");
        cache.putAll(cache2);
        Assert.assertEquals("foo", cache.get(1));
        Assert.assertEquals("bar", cache.get(2));
        Assert.assertEquals(null, cache.get(3));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testLRUSoftCacheRemove() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        cache.put(1, "foo");
        cache.put(2, "bar");
        Assert.assertEquals("bar", cache.get(2));
        cache.remove(2);
        Assert.assertEquals(null, cache.get(2));
    }

    @Test
    public void testLRUSoftCacheValues() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        cache.put(1, "foo");
        cache.put(2, "bar");
        Collection<Object> col = cache.values();
        Assert.assertEquals(2, col.size());
        Iterator<Object> it = col.iterator();
        Assert.assertEquals("foo", it.next());
        Assert.assertEquals("bar", it.next());
    }

    @Test
    public void testLRUSoftCacheEmpty() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        Assert.assertTrue(cache.isEmpty());
        cache.put(1, "foo");
        Assert.assertFalse(cache.isEmpty());
        cache.put(2, "bar");
        Assert.assertFalse(cache.isEmpty());
        cache.remove(2);
        Assert.assertFalse(cache.isEmpty());
        cache.clear();
        Assert.assertTrue(cache.isEmpty());
    }

    @Test
    public void testLRUSoftCacheContainsKey() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        Assert.assertFalse(cache.containsKey(1));
        cache.put(1, "foo");
        Assert.assertTrue(cache.containsKey(1));
        Assert.assertFalse(cache.containsKey(2));
        cache.put(2, "foo");
        Assert.assertTrue(cache.containsKey(2));
        cache.clear();
        Assert.assertFalse(cache.containsKey(1));
        Assert.assertFalse(cache.containsKey(2));
    }

    @Test
    public void testLRUSoftCacheKeySet() throws Exception {
        LRUSoftCache<Integer, Object> cache = new LRUSoftCache(1000);
        cache.put(1, "foo");
        cache.put(2, "foo");
        Set<Integer> keys = cache.keySet();
        Assert.assertEquals(2, keys.size());
        Iterator<Integer> it = keys.iterator();
        Assert.assertEquals(1, it.next().intValue());
        Assert.assertEquals(2, it.next().intValue());
    }
}
