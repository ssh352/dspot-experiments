/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.common.collections;


import io.crate.test.integration.CrateUnitTest;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class MapComparatorTest extends CrateUnitTest {
    @Test
    public void testCompareNullMaps() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("map is null");
        MapComparator.compareMaps(null, null);
    }

    @Test
    public void testCompareMapsWithNullValues() {
        Map<String, Integer> map1 = new HashMap<String, Integer>() {
            {
                put("str1", 1);
                put("str2", null);
                put("str3", 3);
            }
        };
        Map<String, Integer> map2 = new HashMap<String, Integer>() {
            {
                put("str1", 1);
                put("str2", 2);
                put("str3", 3);
            }
        };
        assertThat(MapComparator.compareMaps(map1, map2), Matchers.is(1));
        assertThat(MapComparator.compareMaps(map2, map1), Matchers.is((-1)));
        map2.put("str2", null);
        assertThat(MapComparator.compareMaps(map2, map1), Matchers.is(0));
    }

    @Test
    public void testCompareMapsWithValuesOfTheSameClass() {
        Map<String, Integer> map1 = new HashMap<String, Integer>() {
            {
                put("str1", 1);
                put("str2", 2);
                put("str3", 3);
            }
        };
        Map<String, Integer> map2 = new HashMap<String, Integer>() {
            {
                put("str1", 1);
                put("str2", 2);
                put("str3", 3);
            }
        };
        assertThat(MapComparator.compareMaps(map1, map2), Matchers.is(0));
        assertThat(MapComparator.compareMaps(map2, map1), Matchers.is(0));
        map2.put("str2", 5);
        assertThat(MapComparator.compareMaps(map1, map2), Matchers.is(1));
        assertThat(MapComparator.compareMaps(map2, map1), Matchers.is(1));
    }
}

