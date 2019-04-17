/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.StringExpression;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class PathMetadataTest {
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {
        Field field = ConstantImpl.class.getDeclaredField("STRINGS");
        field.setAccessible(true);
        Map<String, StringExpression> cache = ((Map) (field.get(null)));
        System.out.println(((cache.size()) + " entries in ConstantImpl string cache"));
        // numbers
        Assert.assertTrue(cache.containsKey("0"));
        Assert.assertTrue(cache.containsKey("10"));
        // variables
        Assert.assertTrue(cache.containsKey("animal"));
        Assert.assertTrue(cache.containsKey("cat"));
        Assert.assertTrue(cache.containsKey("category"));
        Assert.assertTrue(cache.containsKey("simpleTypes"));
        // properties
        Assert.assertTrue(cache.containsKey("mate"));
    }
}
