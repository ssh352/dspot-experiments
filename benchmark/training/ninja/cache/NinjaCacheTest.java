/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.cache;


import ninja.utils.TimeUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NinjaCacheTest {
    @Mock
    Cache cache;

    @InjectMocks
    NinjaCache ninjaCache;

    @Test
    public void testAdd() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.add(key, value, expiration);
        Mockito.verify(cache).add(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testAddChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.add(null, notSerializable, null);
    }

    @Test
    public void testSafeAdd() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.safeAdd(key, value, expiration);
        Mockito.verify(cache).safeAdd(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testSafeAddChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.safeAdd(null, notSerializable, null);
    }

    @Test
    public void testAddKeyValue() {
        String key = "key";
        String value = "value";
        ninjaCache.add(key, value);
        Mockito.verify(cache).add(key, value, TimeUtil.parseDuration(null));
    }

    @Test(expected = CacheException.class)
    public void testAddKeyValueChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.add(null, notSerializable);
    }

    @Test
    public void testSetKeyValueExpiration() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.set(key, value, expiration);
        Mockito.verify(cache).set(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testSetKeyValueExpirationChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.set(null, notSerializable, null);
    }

    @Test
    public void testSafeSetKeyValue() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.safeSet(key, value, expiration);
        Mockito.verify(cache).safeSet(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testSafeSetChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.safeSet(null, notSerializable, null);
    }

    @Test
    public void testSetKeyValue() {
        String key = "key";
        String value = "value";
        ninjaCache.set(key, value);
        Mockito.verify(cache).set(key, value, TimeUtil.parseDuration(null));
    }

    @Test(expected = CacheException.class)
    public void testSetKeyValueChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.set(null, notSerializable);
    }

    @Test
    public void testReplaceKeyValueExpiration() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.replace(key, value, expiration);
        Mockito.verify(cache).replace(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testReplaceKeyValueExpirationChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.replace(null, notSerializable);
    }

    @Test
    public void testSafeReplaceKeyValueExpiration() {
        String key = "key";
        String value = "value";
        String expiration = "10s";
        ninjaCache.safeReplace(key, value, expiration);
        Mockito.verify(cache).safeReplace(key, value, TimeUtil.parseDuration(expiration));
    }

    @Test(expected = CacheException.class)
    public void testSafeReplaceKeyValueExpirationChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.safeReplace(null, notSerializable, null);
    }

    @Test
    public void testReplaceKeyValue() {
        String key = "key";
        String value = "value";
        ninjaCache.replace(key, value);
        Mockito.verify(cache).replace(key, value, TimeUtil.parseDuration(null));
    }

    @Test(expected = CacheException.class)
    public void testReplaceKeyValueChecksSerializable() {
        Object notSerializable = new Object();
        ninjaCache.replace(null, notSerializable);
    }

    @Test
    public void testInrKeyBy() {
        String key = "key";
        int by = 99;
        ninjaCache.incr(key, by);
        Mockito.verify(cache).incr(key, by);
    }

    @Test
    public void testIncr() {
        String key = "key";
        ninjaCache.incr(key);
        Mockito.verify(cache).incr(key, 1);
    }

    @Test
    public void testDecrKeyBy() {
        String key = "key";
        int by = 99;
        ninjaCache.decr(key, by);
        Mockito.verify(cache).decr(key, by);
    }

    @Test
    public void testDecr() {
        String key = "key";
        ninjaCache.decr(key);
        Mockito.verify(cache).decr(key, 1);
    }

    @Test
    public void testGet() {
        String key = "key";
        ninjaCache.get(key);
        Mockito.verify(cache).get(key);
    }

    @Test
    public void testGetArray() {
        String[] keys = new String[]{ "key1", "key2", "key3" };
        ninjaCache.get(keys);
        Mockito.verify(cache).get(keys);
    }

    @Test
    public void testDelete() {
        String key = "key";
        ninjaCache.delete(key);
        Mockito.verify(cache).delete(key);
    }

    @Test
    public void testSafeDelete() {
        String key = "key";
        ninjaCache.safeDelete(key);
        Mockito.verify(cache).safeDelete(key);
    }

    @Test
    public void testClear() {
        ninjaCache.clear();
        Mockito.verify(cache).clear();
    }

    @Test
    public void testGetKeyClass() {
        String key = "key";
        Mockito.when(cache.get(key)).thenReturn("a string");
        String result = ninjaCache.get(key, String.class);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo("a string"));
    }
}

