/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.functions;


import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ClosureCleaner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ClosureCleaner}.
 */
public class ClosureCleanerTest {
    @Test(expected = InvalidProgramException.class)
    public void testNonSerializable() throws Exception {
        MapCreator creator = new NonSerializableMapCreator();
        MapFunction<Integer, Integer> map = creator.getMap();
        ClosureCleaner.ensureSerializable(map);
        int result = map.map(3);
        Assert.assertEquals(result, 4);
    }

    @Test
    public void testCleanedNonSerializable() throws Exception {
        MapCreator creator = new NonSerializableMapCreator();
        MapFunction<Integer, Integer> map = creator.getMap();
        ClosureCleaner.clean(map, true);
        int result = map.map(3);
        Assert.assertEquals(result, 4);
    }

    @Test
    public void testSerializable() throws Exception {
        MapCreator creator = new SerializableMapCreator(1);
        MapFunction<Integer, Integer> map = creator.getMap();
        ClosureCleaner.clean(map, true);
        int result = map.map(3);
        Assert.assertEquals(result, 4);
    }

    @Test
    public void testNestedSerializable() throws Exception {
        MapCreator creator = new NestedSerializableMapCreator(1);
        MapFunction<Integer, Integer> map = creator.getMap();
        ClosureCleaner.clean(map, true);
        ClosureCleaner.ensureSerializable(map);
        int result = map.map(3);
        Assert.assertEquals(result, 4);
    }

    @Test(expected = InvalidProgramException.class)
    public void testNestedNonSerializable() throws Exception {
        MapCreator creator = new NestedNonSerializableMapCreator(1);
        MapFunction<Integer, Integer> map = creator.getMap();
        ClosureCleaner.clean(map, true);
        ClosureCleaner.ensureSerializable(map);
        int result = map.map(3);
        Assert.assertEquals(result, 4);
    }
}

