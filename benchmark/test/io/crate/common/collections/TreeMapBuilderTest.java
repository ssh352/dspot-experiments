/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.common.collections;


import io.crate.test.integration.CrateUnitTest;
import java.util.TreeMap;
import org.hamcrest.Matchers;
import org.junit.Test;


public class TreeMapBuilderTest extends CrateUnitTest {
    @Test
    public void testBuilder() throws Exception {
        TreeMapBuilder<Integer, Integer> builder = TreeMapBuilder.newMapBuilder();
        assertThat(builder.put(1, 1), Matchers.instanceOf(TreeMapBuilder.class));
        assertTrue(builder.map().containsKey(1));
        assertThat(builder.map().get(1), Matchers.is(1));
        assertFalse(builder.map().isEmpty());
        builder.put(1, 1);
        assertThat(builder.map(), Matchers.instanceOf(TreeMap.class));
    }
}
