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
package com.hazelcast.map.impl.query;


import CacheDeserializedValues.ALWAYS;
import GroupProperty.QUERY_PREDICATE_PARALLEL_EVALUATION;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.PortableTest;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.query.SampleTestObjects.State.STATE1;
import static com.hazelcast.query.SampleTestObjects.State.STATE2;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryBasicTest extends HazelcastTestSupport {
    @Test
    public void testPredicatedEvaluatedSingleThreadedByDefault() {
        Config config = getConfig();
        HazelcastProperties properties = new HazelcastProperties(config);
        boolean parallelEvaluation = properties.getBoolean(QUERY_PREDICATE_PARALLEL_EVALUATION);
        Assert.assertFalse(parallelEvaluation);
    }

    @Test(timeout = 1000 * 90)
    public void testInPredicateWithEmptyArray() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        Config cfg = getConfig();
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(cfg);
        final IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        for (int i = 0; i < 10; i++) {
            final SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i), new SampleTestObjects.ValueType(("type" + i)), i);
            map.put(("" + i), v);
        }
        String[] emptyArray = new String[2];
        final Predicate predicate = new PredicateBuilder().getEntryObject().get("name").in(emptyArray);
        final Collection<SampleTestObjects.Value> values = map.values(predicate);
        Assert.assertEquals(values.size(), 0);
    }

    @Test
    public void testQueryIndexNullValues() {
        final HazelcastInstance instance = createHazelcastInstance(getConfig());
        final IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        map.put("first", new SampleTestObjects.Value("first", 1));
        map.put("second", new SampleTestObjects.Value(null, 2));
        map.put("third", new SampleTestObjects.Value(null, 3));
        final Predicate predicate = new SqlPredicate("name=null");
        final Collection<SampleTestObjects.Value> values = map.values(predicate);
        final int[] expectedIndexValues = new int[]{ 2, 3 };
        Assert.assertEquals(expectedIndexValues.length, values.size());
        final int[] actualIndexValues = new int[values.size()];
        int i = 0;
        for (SampleTestObjects.Value value : values) {
            actualIndexValues[(i++)] = value.getIndex();
        }
        Arrays.sort(actualIndexValues);
        Assert.assertArrayEquals(expectedIndexValues, actualIndexValues);
    }

    @Test
    public void testLesserEqual() {
        final HazelcastInstance instance = createHazelcastInstance(getConfig());
        final IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("index", true);
        for (int i = 0; i < 10; i++) {
            map.put(("" + i), new SampleTestObjects.Value(("" + i), i));
        }
        final Predicate predicate = new SqlPredicate("index<=5");
        final Collection<SampleTestObjects.Value> values = map.values(predicate);
        final int[] expectedIndexValues = new int[6];
        for (int i = 0; i < (expectedIndexValues.length); i++) {
            expectedIndexValues[i] = i;
        }
        Assert.assertEquals(expectedIndexValues.length, values.size());
        final int[] actualIndexValues = new int[values.size()];
        int i = 0;
        for (SampleTestObjects.Value value : values) {
            actualIndexValues[(i++)] = value.getIndex();
        }
        Arrays.sort(actualIndexValues);
        Assert.assertArrayEquals(expectedIndexValues, actualIndexValues);
    }

    @Test
    public void testNotEqual() {
        final HazelcastInstance instance = createHazelcastInstance(getConfig());
        final IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        map.put("first", new SampleTestObjects.Value("first", 1));
        map.put("second", new SampleTestObjects.Value(null, 2));
        map.put("third", new SampleTestObjects.Value(null, 3));
        final Predicate predicate = new SqlPredicate("name != null");
        final Collection<SampleTestObjects.Value> values = map.values(predicate);
        final int[] expectedIndexValues = new int[]{ 1 };
        Assert.assertEquals(expectedIndexValues.length, values.size());
        final int[] actualIndexValues = new int[values.size()];
        int i = 0;
        for (SampleTestObjects.Value value : values) {
            actualIndexValues[(i++)] = value.getIndex();
        }
        Arrays.sort(actualIndexValues);
        Assert.assertArrayEquals(expectedIndexValues, actualIndexValues);
    }

    @Test(timeout = 1000 * 90)
    public void issue393SqlIn() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i));
            map.put(("" + i), v);
        }
        Predicate predicate = new SqlPredicate("name IN ('name0', 'name2')");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        String[] expectedValues = new String[]{ "name0", "name2" };
        Assert.assertEquals(expectedValues.length, values.size());
        List<String> names = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            names.add(configObject.getName());
        }
        String[] array = names.toArray(new String[0]);
        Arrays.sort(array);
        Assert.assertArrayEquals(names.toString(), expectedValues, array);
    }

    @Test(timeout = 1000 * 90)
    public void issue393SqlInInteger() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("index", false);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i), new SampleTestObjects.ValueType(("type" + i)), i);
            map.put(("" + i), v);
        }
        Predicate predicate = new SqlPredicate("index IN (0, 2)");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        String[] expectedValues = new String[]{ "name0", "name2" };
        Assert.assertEquals(expectedValues.length, values.size());
        List<String> names = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            names.add(configObject.getName());
        }
        String[] array = names.toArray(new String[0]);
        Arrays.sort(array);
        Assert.assertArrayEquals(names.toString(), expectedValues, array);
    }

    @Test(timeout = 1000 * 90)
    public void testInPredicate() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.ValueType> map = instance.getMap("testIteratorContract");
        map.put("1", new SampleTestObjects.ValueType("one"));
        map.put("2", new SampleTestObjects.ValueType("two"));
        map.put("3", new SampleTestObjects.ValueType("three"));
        map.put("4", new SampleTestObjects.ValueType("four"));
        map.put("5", new SampleTestObjects.ValueType("five"));
        map.put("6", new SampleTestObjects.ValueType("six"));
        map.put("7", new SampleTestObjects.ValueType("seven"));
        Predicate predicate = new SqlPredicate("typeName in ('one','two')");
        for (int i = 0; i < 10; i++) {
            Collection<SampleTestObjects.ValueType> values = map.values(predicate);
            Assert.assertEquals(2, values.size());
        }
    }

    @Test(timeout = 1000 * 90)
    public void testInstanceOfPredicate() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, Object> map = instance.getMap("testInstanceOfPredicate");
        LinkedList linkedList = new LinkedList();
        Predicate linkedListPredicate = Predicates.instanceOf(LinkedList.class);
        map.put("1", "someString");
        map.put("2", new ArrayList());
        map.put("3", linkedList);
        Collection<Object> values = map.values(linkedListPredicate);
        Assert.assertEquals(1, values.size());
        HazelcastTestSupport.assertContains(values, linkedList);
    }

    @Test(timeout = 1000 * 90)
    public void testIteratorContract() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.ValueType> map = instance.getMap("testIteratorContract");
        map.put("1", new SampleTestObjects.ValueType("one"));
        map.put("2", new SampleTestObjects.ValueType("two"));
        map.put("3", new SampleTestObjects.ValueType("three"));
        Predicate predicate = new SqlPredicate("typeName in ('one','two')");
        Assert.assertEquals(2, map.values(predicate).size());
        Assert.assertEquals(2, map.keySet(predicate).size());
        testIterator(map.keySet().iterator(), 3);
        testIterator(map.keySet(predicate).iterator(), 2);
        testIterator(map.entrySet().iterator(), 3);
        testIterator(map.entrySet(predicate).iterator(), 2);
        testIterator(map.values().iterator(), 3);
        testIterator(map.values(predicate).iterator(), 2);
    }

    @Test(timeout = 1000 * 90)
    public void issue393Fail() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("qwe", true);
        SampleTestObjects.Value v = new SampleTestObjects.Value("name");
        try {
            map.put("0", v);
            Assert.fail();
        } catch (Throwable e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "There is no suitable accessor for 'qwe'");
        }
    }

    @Test(timeout = 1000 * 90)
    public void negativeDouble() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("default");
        map.addIndex("salary", false);
        map.put(("" + 4), new SampleTestObjects.Employee(1, "default", 1, true, (-70.0)));
        map.put(("" + 3), new SampleTestObjects.Employee(1, "default", 1, true, (-60.0)));
        map.put(("" + 1), new SampleTestObjects.Employee(1, "default", 1, true, (-10.0)));
        map.put(("" + 2), new SampleTestObjects.Employee(2, "default", 2, true, 10.0));
        Predicate predicate = new SqlPredicate("salary >= -60");
        Collection<SampleTestObjects.Employee> values = map.values(predicate);
        Assert.assertEquals(3, values.size());
        predicate = new SqlPredicate("salary between -20 and 20");
        values = map.values(predicate);
        Assert.assertEquals(2, values.size());
    }

    @Test(timeout = 1000 * 90)
    public void issue393SqlEq() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i));
            map.put(("" + i), v);
        }
        Predicate predicate = new SqlPredicate("name='name0'");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        String[] expectedValues = new String[]{ "name0" };
        Assert.assertEquals(expectedValues.length, values.size());
        List<String> names = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            names.add(configObject.getName());
        }
        String[] array = names.toArray(new String[0]);
        Arrays.sort(array);
        Assert.assertArrayEquals(names.toString(), expectedValues, array);
    }

    @Test(timeout = 1000 * 90)
    public void issue393() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i));
            map.put(("" + i), v);
        }
        Predicate predicate = new PredicateBuilder().getEntryObject().get("name").in("name0", "name2");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        String[] expectedValues = new String[]{ "name0", "name2" };
        Assert.assertEquals(expectedValues.length, values.size());
        List<String> names = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            names.add(configObject.getName());
        }
        String[] array = names.toArray(new String[0]);
        Arrays.sort(array);
        Assert.assertArrayEquals(names.toString(), expectedValues, array);
    }

    @Test(timeout = 1000 * 90)
    public void testWithDashInTheNameAndSqlPredicate() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employee");
        SampleTestObjects.Employee toto = new SampleTestObjects.Employee("toto", 23, true, 165765.0);
        map.put("1", toto);
        SampleTestObjects.Employee toto2 = new SampleTestObjects.Employee("toto-super+hero", 23, true, 165765.0);
        map.put("2", toto2);
        // works well
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("name='toto-super+hero'"));
        Assert.assertTrue(((entries.size()) > 0));
        for (Map.Entry<String, SampleTestObjects.Employee> entry : entries) {
            SampleTestObjects.Employee e = entry.getValue();
            Assert.assertEquals(e, toto2);
        }
    }

    @Test(timeout = 1000 * 90)
    public void queryWithThis() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, String> map = instance.getMap("queryWithThis");
        map.addIndex("this", false);
        for (int i = 0; i < 1000; i++) {
            map.put(("" + i), ("" + i));
        }
        Predicate predicate = new PredicateBuilder().getEntryObject().get("this").equal("10");
        Collection<String> set = map.values(predicate);
        Assert.assertEquals(1, set.size());
        Assert.assertEquals(1, map.values(new SqlPredicate("this=15")).size());
    }

    /**
     * Test for issue 711
     */
    @Test(timeout = 1000 * 90)
    public void testPredicateWithEntryKeyObject() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, Integer> map = instance.getMap("testPredicateWithEntryKeyObject");
        map.put("1", 11);
        map.put("2", 22);
        map.put("3", 33);
        map.put("4", 44);
        map.put("5", 55);
        map.put("6", 66);
        Predicate predicate = new PredicateBuilder().getEntryObject().key().equal("1");
        Assert.assertEquals(1, map.values(predicate).size());
        predicate = new PredicateBuilder().getEntryObject().key().in("2", "3");
        Assert.assertEquals(2, map.keySet(predicate).size());
        predicate = new PredicateBuilder().getEntryObject().key().in("2", "3", "5", "6", "7");
        Assert.assertEquals(4, map.keySet(predicate).size());
    }

    /**
     * Github issues 98 and 131
     */
    @Test(timeout = 1000 * 90)
    public void testPredicateStringAttribute() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, SampleTestObjects.Value> map = instance.getMap("testPredicateStringWithString");
        testPredicateStringAttribute(map);
    }

    /**
     * Github issues 98 and 131
     */
    @Test(timeout = 1000 * 90)
    public void testPredicateStringAttributesWithIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, SampleTestObjects.Value> map = instance.getMap("testPredicateStringWithStringIndex");
        map.addIndex("name", false);
        testPredicateStringAttribute(map);
    }

    @Test(timeout = 1000 * 90)
    public void testPredicateDateAttribute() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, Date> map = instance.getMap("testPredicateDateAttribute");
        testPredicateDateAttribute(map);
    }

    @Test(timeout = 1000 * 90)
    public void testPredicateDateAttributeWithIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, Date> map = instance.getMap("testPredicateDateAttribute");
        map.addIndex("this", true);
        testPredicateDateAttribute(map);
    }

    @Test(timeout = 1000 * 90)
    public void testPredicateEnumAttribute() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, QueryBasicTest.NodeType> map = instance.getMap("testPredicateEnumAttribute");
        testPredicateEnumAttribute(map);
    }

    @Test(timeout = 1000 * 90)
    public void testPredicateEnumAttributeWithIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, QueryBasicTest.NodeType> map = instance.getMap("testPredicateEnumAttribute");
        map.addIndex("this", true);
        testPredicateEnumAttribute(map);
    }

    private enum NodeType {

        MEMBER,
        LITE_MEMBER,
        JAVA_CLIENT,
        CSHARP_CLIENT;}

    @Test(timeout = 1000 * 90)
    public void testPredicateCustomAttribute() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, CustomObject> map = instance.getMap("testPredicateCustomAttribute");
        CustomAttribute attribute = new CustomAttribute(78, 145);
        CustomObject customObject = new CustomObject("name1", UuidUtil.newUnsecureUUID(), attribute);
        map.put(1, customObject);
        CustomObject object2 = new CustomObject("name2", UuidUtil.newUnsecureUUID(), attribute);
        map.put(2, object2);
        Assert.assertEquals(customObject, map.values(new PredicateBuilder().getEntryObject().get("uuid").equal(customObject.getUuid())).iterator().next());
        Assert.assertEquals(2, map.values(new PredicateBuilder().getEntryObject().get("attribute").equal(attribute)).size());
        Assert.assertEquals(object2, map.values(new PredicateBuilder().getEntryObject().get("uuid").in(object2.getUuid())).iterator().next());
        Assert.assertEquals(2, map.values(new PredicateBuilder().getEntryObject().get("attribute").in(attribute)).size());
    }

    @Test(timeout = 1000 * 90)
    public void testInvalidSqlPredicate() {
        Config cfg = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(cfg);
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap("employee");
        map.put(1, new SampleTestObjects.Employee("e", 1, false, 0));
        map.put(2, new SampleTestObjects.Employee("e2", 1, false, 0));
        try {
            map.values(new SqlPredicate("invalid_sql"));
            Assert.fail("Should fail because of invalid SQL!");
        } catch (RuntimeException e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "There is no suitable accessor for 'invalid_sql'");
        }
        try {
            map.values(new SqlPredicate("invalid sql"));
            Assert.fail("Should fail because of invalid SQL!");
        } catch (RuntimeException e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "Invalid SQL: [invalid sql]");
        }
        try {
            map.values(new SqlPredicate("invalid and sql"));
            Assert.fail("Should fail because of invalid SQL!");
        } catch (RuntimeException e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "There is no suitable accessor for 'invalid'");
        }
        try {
            map.values(new SqlPredicate("invalid sql and"));
            Assert.fail("Should fail because of invalid SQL!");
        } catch (RuntimeException e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "There is no suitable accessor for 'invalid'");
        }
        try {
            map.values(new SqlPredicate(""));
            Assert.fail("Should fail because of invalid SQL!");
        } catch (RuntimeException e) {
            HazelcastTestSupport.assertContains(e.getMessage(), "Invalid SQL: []");
        }
        Assert.assertEquals(2, map.values(new SqlPredicate("age=1 and name like 'e%'")).size());
    }

    @Test(timeout = 1000 * 90)
    public void testIndexingEnumAttributeIssue597() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("state", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(((i % 2) == 0 ? STATE1 : STATE2), new SampleTestObjects.ValueType(), i);
            map.put(i, v);
        }
        Predicate predicate = new PredicateBuilder().getEntryObject().get("state").equal(STATE1);
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        int[] expectedValues = new int[]{ 0, 2 };
        Assert.assertEquals(expectedValues.length, values.size());
        int[] indexes = new int[2];
        int index = 0;
        for (SampleTestObjects.Value configObject : values) {
            indexes[(index++)] = configObject.getIndex();
        }
        Arrays.sort(indexes);
        Assert.assertArrayEquals(indexes, expectedValues);
    }

    /**
     * see pull request 616
     */
    @Test(timeout = 1000 * 90)
    public void testIndexingEnumAttributeWithSqlIssue597() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("state", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(((i % 2) == 0 ? STATE1 : STATE2), new SampleTestObjects.ValueType(), i);
            map.put(i, v);
        }
        Collection<SampleTestObjects.Value> values = map.values(new SqlPredicate("state = 'STATE1'"));
        int[] expectedValues = new int[]{ 0, 2 };
        Assert.assertEquals(expectedValues.length, values.size());
        int[] indexes = new int[2];
        int index = 0;
        for (SampleTestObjects.Value configObject : values) {
            indexes[(index++)] = configObject.getIndex();
        }
        Arrays.sort(indexes);
        Assert.assertArrayEquals(indexes, expectedValues);
    }

    @Test(timeout = 1000 * 90)
    public void testMultipleOrPredicatesIssue885WithoutIndex() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = factory.newHazelcastInstance(getConfig());
        factory.newHazelcastInstance(new Config());
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap("default");
        testMultipleOrPredicates(map);
    }

    @Test(timeout = 1000 * 90)
    public void testMultipleOrPredicatesIssue885WithIndex() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = factory.newHazelcastInstance(getConfig());
        factory.newHazelcastInstance(new Config());
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap("default");
        map.addIndex("name", true);
        testMultipleOrPredicates(map);
    }

    @Test(timeout = 1000 * 90)
    public void testMultipleOrPredicatesIssue885WithDoubleIndex() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = factory.newHazelcastInstance(getConfig());
        factory.newHazelcastInstance(new Config());
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap("default");
        map.addIndex("name", true);
        map.addIndex("city", true);
        testMultipleOrPredicates(map);
    }

    @Test
    public void testSqlQueryUsing__KeyField() {
        Config config = getConfig();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance2.getMap(HazelcastTestSupport.randomMapName());
        Object key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        Object value = "value";
        map.put(key, value);
        Collection<Object> values = map.values(new SqlPredicate((("__key = '" + key) + "'")));
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(value, values.iterator().next());
    }

    @Test
    public void testSqlQueryUsingNested__KeyField() {
        Config config = getConfig();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        factory.newHazelcastInstance(config);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(HazelcastTestSupport.randomMapName());
        Object key = new CustomAttribute(12, 123L);
        Object value = "value";
        map.put(key, value);
        Collection<Object> values = map.values(new SqlPredicate("__key.age = 12 and __key.height = 123"));
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(value, values.iterator().next());
    }

    @Test
    public void testSqlQueryUsingPortable__KeyField() {
        Config config = getConfig();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        factory.newHazelcastInstance(config);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(HazelcastTestSupport.randomMapName());
        Object key = new PortableTest.ChildPortableObject(123L);
        Object value = "value";
        map.put(key, value);
        Collection<Object> values = map.values(new SqlPredicate("__key.timestamp = 123"));
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(value, values.iterator().next());
    }

    @Test
    public void testQueryPortableObject_serial() {
        Config config = getConfig();
        testQueryUsingPortableObject(config, HazelcastTestSupport.randomMapName());
    }

    @Test
    public void testQueryPortableObject_parallel() {
        Config config = getConfig();
        config.setProperty(QUERY_PREDICATE_PARALLEL_EVALUATION.getName(), "true");
        testQueryUsingPortableObject(config, HazelcastTestSupport.randomMapName());
    }

    @Test
    public void testQueryPortableObjectAndAlwaysCacheValues() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.addMapConfig(new MapConfig(name).setCacheDeserializedValues(ALWAYS));
        testQueryUsingPortableObject(config, name);
    }

    @Test(expected = QueryException.class)
    public void testQueryPortableField() {
        Config config = getConfig();
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.put(1, new PortableTest.GrandParentPortableObject(1, new PortableTest.ParentPortableObject(1L, new PortableTest.ChildPortableObject(1L))));
        Collection<Object> values = map.values(new SqlPredicate("child > 0"));
        values.size();
    }

    @Test
    public void testQueryUsingNestedPortableObject() {
        Config config = getConfig();
        testQueryUsingNestedPortableObject(config, HazelcastTestSupport.randomMapName());
    }

    @Test
    public void testQueryUsingNestedPortableObjectWithIndex() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.addMapConfig(new MapConfig(name).addMapIndexConfig(new MapIndexConfig("child.timestamp", false)).addMapIndexConfig(new MapIndexConfig("child.child.timestamp", true)));
        testQueryUsingNestedPortableObject(config, name);
    }

    @Test
    public void testQueryPortableObjectWithIndex() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.addMapConfig(new MapConfig(name).addMapIndexConfig(new MapIndexConfig("timestamp", true)));
        testQueryUsingPortableObject(config, name);
    }

    @Test
    public void testQueryPortableObjectWithIndexAndAlwaysCacheValues() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.addMapConfig(new MapConfig(name).setCacheDeserializedValues(ALWAYS).addMapIndexConfig(new MapIndexConfig("timestamp", true)));
        testQueryUsingPortableObject(config, name);
    }
}
