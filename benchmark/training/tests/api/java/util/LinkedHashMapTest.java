/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.util;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import junit.framework.TestCase;
import tests.support.Support_MapTest2;
import tests.support.Support_UnmodifiableCollectionTest;


/**
 * java.util.LinkedHashMap
 */
public class LinkedHashMapTest extends TestCase {
    LinkedHashMap hm;

    static final int hmSize = 1000;

    Object[] objArray;

    Object[] objArray2;

    static final class CacheMap extends LinkedHashMap {
        protected boolean removeEldestEntry(Map.Entry e) {
            return (size()) > 5;
        }
    }

    private static class MockMapNull extends AbstractMap {
        @Override
        public Set entrySet() {
            return null;
        }

        @Override
        public int size() {
            return 10;
        }
    }

    /**
     * java.util.LinkedHashMap#LinkedHashMap()
     */
    public void test_Constructor() {
        // Test for method java.util.LinkedHashMap()
        new Support_MapTest2(new LinkedHashMap()).runTest();
        LinkedHashMap hm2 = new LinkedHashMap();
        TestCase.assertEquals("Created incorrect LinkedHashMap", 0, hm2.size());
    }

    /**
     * java.util.LinkedHashMap#LinkedHashMap(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.LinkedHashMap(int)
        LinkedHashMap hm2 = new LinkedHashMap(5);
        TestCase.assertEquals("Created incorrect LinkedHashMap", 0, hm2.size());
        try {
            new LinkedHashMap((-1));
            TestCase.fail(("Failed to throw IllegalArgumentException for initial " + "capacity < 0"));
        } catch (IllegalArgumentException e) {
            // expected
        }
        LinkedHashMap empty = new LinkedHashMap(0);
        TestCase.assertNull("Empty LinkedHashMap access", empty.get("nothing"));
        empty.put("something", "here");
        TestCase.assertTrue("cannot get element", ((empty.get("something")) == "here"));
    }

    /**
     * java.util.LinkedHashMap#LinkedHashMap(int, float)
     */
    public void test_ConstructorIF() {
        // Test for method java.util.LinkedHashMap(int, float)
        LinkedHashMap hm2 = new LinkedHashMap(5, ((float) (0.5)));
        TestCase.assertEquals("Created incorrect LinkedHashMap", 0, hm2.size());
        try {
            new LinkedHashMap(0, 0);
            TestCase.fail(("Failed to throw IllegalArgumentException for initial " + "load factor <= 0"));
        } catch (IllegalArgumentException e) {
            // expected
        }
        LinkedHashMap empty = new LinkedHashMap(0, 0.75F);
        TestCase.assertNull("Empty hashtable access", empty.get("nothing"));
        empty.put("something", "here");
        TestCase.assertTrue("cannot get element", ((empty.get("something")) == "here"));
    }

    /**
     * java.util.LinkedHashMap#LinkedHashMap(java.util.Map)
     */
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.LinkedHashMap(java.util.Map)
        Map myMap = new TreeMap();
        for (int counter = 0; counter < (LinkedHashMapTest.hmSize); counter++)
            myMap.put(objArray2[counter], objArray[counter]);

        LinkedHashMap hm2 = new LinkedHashMap(myMap);
        for (int counter = 0; counter < (LinkedHashMapTest.hmSize); counter++)
            TestCase.assertTrue("Failed to construct correct LinkedHashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

    }

    /**
     * java.util.LinkedHashMap#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.LinkedHashMap.get(java.lang.Object)
        TestCase.assertNull("Get returned non-null for non existent key", hm.get("T"));
        hm.put("T", "HELLO");
        TestCase.assertEquals("Get returned incorecct value for existing key", "HELLO", hm.get("T"));
        LinkedHashMap m = new LinkedHashMap();
        m.put(null, "test");
        TestCase.assertEquals("Failed with null key", "test", m.get(null));
        TestCase.assertNull("Failed with missing key matching null hash", m.get(new Integer(0)));
    }

    /**
     * java.util.LinkedHashMap#put(java.lang.Object, java.lang.Object)
     */
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.LinkedHashMap.put(java.lang.Object, java.lang.Object)
        hm.put("KEY", "VALUE");
        TestCase.assertEquals("Failed to install key/value pair", "VALUE", hm.get("KEY"));
        LinkedHashMap m = new LinkedHashMap();
        m.put(new Short(((short) (0))), "short");
        m.put(null, "test");
        m.put(new Integer(0), "int");
        TestCase.assertEquals("Failed adding to bucket containing null", "short", m.get(new Short(((short) (0)))));
        TestCase.assertEquals("Failed adding to bucket containing null2", "int", m.get(new Integer(0)));
    }

    public void test_putPresent() {
        Map<String, String> m = new LinkedHashMap<String, String>(8, 0.75F, true);
        m.put("KEY", "VALUE");
        m.put("WOMBAT", "COMBAT");
        m.put("KEY", "VALUE");
        Map.Entry newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("KEY", newest.getKey());
        TestCase.assertEquals("VALUE", newest.getValue());
    }

    /**
     * java.util.LinkedHashMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.LinkedHashMap.putAll(java.util.Map)
        LinkedHashMap hm2 = new LinkedHashMap();
        hm2.putAll(hm);
        for (int i = 0; i < 1000; i++)
            TestCase.assertTrue("Failed to clear all elements", hm2.get(new Integer(i).toString()).equals(new Integer(i)));

    }

    /**
     * java.util.LinkedHashMap#putAll(java.util.Map)
     */
    public void test_putAll_Ljava_util_Map_Null() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap.putAll(new LinkedHashMapTest.MockMapNull());
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            linkedHashMap = new LinkedHashMap(new LinkedHashMapTest.MockMapNull());
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
    }

    /**
     * java.util.LinkedHashMap#entrySet()
     */
    public void test_entrySet() {
        // Test for method java.util.Set java.util.LinkedHashMap.entrySet()
        Set s = hm.entrySet();
        Iterator i = s.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((hm.size()) == (s.size())));
        while (i.hasNext()) {
            Map.Entry m = ((Map.Entry) (i.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((hm.containsKey(m.getKey())) && (hm.containsValue(m.getValue()))));
        } 
    }

    public void test_entrySetRemove() {
        entrySetRemoveHelper("military", "intelligence");
        entrySetRemoveHelper(null, "hypothesis");
    }

    /**
     * java.util.LinkedHashMap#keySet()
     */
    public void test_keySet() {
        // Test for method java.util.Set java.util.LinkedHashMap.keySet()
        Set s = hm.keySet();
        TestCase.assertTrue("Returned set of incorrect size()", ((s.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++)
            TestCase.assertTrue("Returned set does not contain all keys", s.contains(objArray[i].toString()));

        LinkedHashMap m = new LinkedHashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.keySet().contains(null));
        TestCase.assertNull("Failed with null key", m.keySet().iterator().next());
        Map map = new LinkedHashMap(101);
        map.put(new Integer(1), "1");
        map.put(new Integer(102), "102");
        map.put(new Integer(203), "203");
        Iterator it = map.keySet().iterator();
        Integer remove1 = ((Integer) (it.next()));
        it.hasNext();
        it.remove();
        Integer remove2 = ((Integer) (it.next()));
        it.remove();
        ArrayList list = new ArrayList(Arrays.asList(new Integer[]{ new Integer(1), new Integer(102), new Integer(203) }));
        list.remove(remove1);
        list.remove(remove2);
        TestCase.assertTrue("Wrong result", it.next().equals(list.get(0)));
        TestCase.assertEquals("Wrong size", 1, map.size());
        TestCase.assertTrue("Wrong contents", map.keySet().iterator().next().equals(list.get(0)));
        Map map2 = new LinkedHashMap(101);
        map2.put(new Integer(1), "1");
        map2.put(new Integer(4), "4");
        Iterator it2 = map2.keySet().iterator();
        Integer remove3 = ((Integer) (it2.next()));
        Integer next;
        if ((remove3.intValue()) == 1)
            next = new Integer(4);
        else
            next = new Integer(1);

        it2.hasNext();
        it2.remove();
        TestCase.assertTrue("Wrong result 2", it2.next().equals(next));
        TestCase.assertEquals("Wrong size 2", 1, map2.size());
        TestCase.assertTrue("Wrong contents 2", map2.keySet().iterator().next().equals(next));
    }

    /**
     * java.util.LinkedHashMap#values()
     */
    public void test_values() {
        // Test for method java.util.Collection java.util.LinkedHashMap.values()
        Collection c = hm.values();
        TestCase.assertTrue("Returned collection of incorrect size()", ((c.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++)
            TestCase.assertTrue("Returned collection does not contain all keys", c.contains(objArray[i]));

        LinkedHashMap myLinkedHashMap = new LinkedHashMap();
        for (int i = 0; i < 100; i++)
            myLinkedHashMap.put(objArray2[i], objArray[i]);

        Collection values = myLinkedHashMap.values();
        new Support_UnmodifiableCollectionTest("Test Returned Collection From LinkedHashMap.values()", values).runTest();
        values.remove(new Integer(0));
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myLinkedHashMap.containsValue(new Integer(0)))));
    }

    /**
     * java.util.LinkedHashMap#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.LinkedHashMap.remove(java.lang.Object)
        int size = hm.size();
        Integer y = new Integer(9);
        Integer x = ((Integer) (hm.remove(y.toString())));
        TestCase.assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
        TestCase.assertNull("Failed to remove given key", hm.get(new Integer(9)));
        TestCase.assertTrue("Failed to decrement size", ((hm.size()) == (size - 1)));
        TestCase.assertNull("Remove of non-existent key returned non-null", hm.remove("LCLCLC"));
        LinkedHashMap m = new LinkedHashMap();
        m.put(null, "test");
        TestCase.assertNull("Failed with same hash as null", m.remove(new Integer(0)));
        TestCase.assertEquals("Failed with null key", "test", m.remove(null));
    }

    /**
     * java.util.LinkedHashMap#clear()
     */
    public void test_clear() {
        // Test for method void java.util.LinkedHashMap.clear()
        hm.clear();
        TestCase.assertEquals("Clear failed to reset size", 0, hm.size());
        for (int i = 0; i < (LinkedHashMapTest.hmSize); i++)
            TestCase.assertNull("Failed to clear all elements", hm.get(objArray2[i]));

    }

    /**
     * java.util.LinkedHashMap#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.LinkedHashMap.clone()
        LinkedHashMap hm2 = ((LinkedHashMap) (hm.clone()));
        TestCase.assertTrue("Clone answered equivalent LinkedHashMap", (hm2 != (hm)));
        for (int counter = 0; counter < (LinkedHashMapTest.hmSize); counter++)
            TestCase.assertTrue("Clone answered unequal LinkedHashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

        LinkedHashMap map = new LinkedHashMap();
        map.put("key", "value");
        // get the keySet() and values() on the original Map
        Set keys = map.keySet();
        Collection values = map.values();
        TestCase.assertEquals("values() does not work", "value", values.iterator().next());
        TestCase.assertEquals("keySet() does not work", "key", keys.iterator().next());
        AbstractMap map2 = ((AbstractMap) (map.clone()));
        map2.put("key", "value2");
        Collection values2 = map2.values();
        TestCase.assertTrue("values() is identical", (values2 != values));
        // values() and keySet() on the cloned() map should be different
        TestCase.assertEquals("values() was not cloned", "value2", values2.iterator().next());
        map2.clear();
        map2.put("key2", "value3");
        Set key2 = map2.keySet();
        TestCase.assertTrue("keySet() is identical", (key2 != keys));
        TestCase.assertEquals("keySet() was not cloned", "key2", key2.iterator().next());
    }

    /**
     * java.util.LinkedHashMap#clone()
     */
    public void test_clone_ordered() {
        // Test for method java.lang.Object java.util.LinkedHashMap.clone()
        LinkedHashMap<String, String> hm1 = new LinkedHashMap<String, String>(10, 0.75F, true);
        hm1.put("a", "a");
        hm1.put("b", "b");
        hm1.put("c", "c");
        LinkedHashMap<String, String> hm2 = ((LinkedHashMap<String, String>) (hm1.clone()));
        hm1.get("a");
        Map.Entry<String, String>[] set = new Map.Entry[3];
        Iterator<Map.Entry<String, String>> iterator = hm1.entrySet().iterator();
        TestCase.assertEquals("b", iterator.next().getKey());
        TestCase.assertEquals("c", iterator.next().getKey());
        TestCase.assertEquals("a", iterator.next().getKey());
        iterator = hm2.entrySet().iterator();
        TestCase.assertEquals("a", iterator.next().getKey());
        TestCase.assertEquals("b", iterator.next().getKey());
        TestCase.assertEquals("c", iterator.next().getKey());
    }

    // regresion test for HARMONY-4603
    public void test_clone_Mock() {
        LinkedHashMap hashMap = new LinkedHashMapTest.MockMap();
        String value = "value a";
        hashMap.put("key", value);
        LinkedHashMapTest.MockMap cloneMap = ((LinkedHashMapTest.MockMap) (hashMap.clone()));
        TestCase.assertEquals(value, cloneMap.get("key"));
        TestCase.assertEquals(hashMap, cloneMap);
        TestCase.assertEquals(1, cloneMap.num);
        hashMap.put("key", "value b");
        TestCase.assertFalse(hashMap.equals(cloneMap));
    }

    class MockMap extends LinkedHashMap {
        int num;

        public Object put(Object k, Object v) {
            (num)++;
            return super.put(k, v);
        }

        protected boolean removeEldestEntry(Map.Entry e) {
            return (num) > 1;
        }
    }

    /**
     * put/get interaction in access-order map where removeEldest
     * returns true.
     */
    public void test_removeEldestFromSameBucketAsNewEntry() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(6, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Entry<String, String> e) {
                return true;
            }
        };
        map.put("N", "E");
        map.put("F", "I");
        TestCase.assertEquals(null, map.get("N"));
    }

    /**
     * java.util.LinkedHashMap#containsKey(java.lang.Object)
     */
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.LinkedHashMap.containsKey(java.lang.Object)
        TestCase.assertTrue("Returned false for valid key", hm.containsKey(new Integer(876).toString()));
        TestCase.assertTrue("Returned true for invalid key", (!(hm.containsKey("KKDKDKD"))));
        LinkedHashMap m = new LinkedHashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.containsKey(null));
        TestCase.assertTrue("Failed with missing key matching null hash", (!(m.containsKey(new Integer(0)))));
    }

    /**
     * java.util.LinkedHashMap#containsValue(java.lang.Object)
     */
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.LinkedHashMap.containsValue(java.lang.Object)
        TestCase.assertTrue("Returned false for valid value", hm.containsValue(new Integer(875)));
        TestCase.assertTrue("Returned true for invalid valie", (!(hm.containsValue(new Integer((-9))))));
    }

    /**
     * java.util.LinkedHashMap#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.LinkedHashMap.isEmpty()
        TestCase.assertTrue("Returned false for new map", new LinkedHashMap().isEmpty());
        TestCase.assertTrue("Returned true for non-empty", (!(hm.isEmpty())));
    }

    /**
     * java.util.LinkedHashMap#size()
     */
    public void test_size() {
        // Test for method int java.util.LinkedHashMap.size()
        TestCase.assertTrue("Returned incorrect size", ((hm.size()) == ((objArray.length) + 2)));
    }

    /**
     * java.util.LinkedHashMap#entrySet()
     */
    public void test_ordered_entrySet() {
        int i;
        int sz = 100;
        LinkedHashMap lhm = new LinkedHashMap();
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lhm.put(ii, ii.toString());
        }
        Set s1 = lhm.entrySet();
        Iterator it1 = s1.iterator();
        TestCase.assertTrue("Returned set of incorrect size 1", ((lhm.size()) == (s1.size())));
        for (i = 0; it1.hasNext(); i++) {
            Map.Entry m = ((Map.Entry) (it1.next()));
            Integer jj = ((Integer) (m.getKey()));
            TestCase.assertTrue("Returned incorrect entry set 1", ((jj.intValue()) == i));
        }
        LinkedHashMap lruhm = new LinkedHashMap(200, 0.75F, true);
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lruhm.put(ii, ii.toString());
        }
        Set s3 = lruhm.entrySet();
        Iterator it3 = s3.iterator();
        TestCase.assertTrue("Returned set of incorrect size 2", ((lruhm.size()) == (s3.size())));
        for (i = 0; (i < sz) && (it3.hasNext()); i++) {
            Map.Entry m = ((Map.Entry) (it3.next()));
            Integer jj = ((Integer) (m.getKey()));
            TestCase.assertTrue("Returned incorrect entry set 2", ((jj.intValue()) == i));
        }
        /* fetch the even numbered entries to affect traversal order */
        int p = 0;
        for (i = 0; i < sz; i += 2) {
            String ii = ((String) (lruhm.get(new Integer(i))));
            p = p + (Integer.parseInt(ii));
        }
        TestCase.assertEquals("invalid sum of even numbers", 2450, p);
        Set s2 = lruhm.entrySet();
        Iterator it2 = s2.iterator();
        TestCase.assertTrue("Returned set of incorrect size 3", ((lruhm.size()) == (s2.size())));
        for (i = 1; (i < sz) && (it2.hasNext()); i += 2) {
            Map.Entry m = ((Map.Entry) (it2.next()));
            Integer jj = ((Integer) (m.getKey()));
            TestCase.assertTrue("Returned incorrect entry set 3", ((jj.intValue()) == i));
        }
        for (i = 0; (i < sz) && (it2.hasNext()); i += 2) {
            Map.Entry m = ((Map.Entry) (it2.next()));
            Integer jj = ((Integer) (m.getKey()));
            TestCase.assertTrue("Returned incorrect entry set 4", ((jj.intValue()) == i));
        }
        TestCase.assertTrue("Entries left to iterate on", (!(it2.hasNext())));
    }

    /**
     * java.util.LinkedHashMap#keySet()
     */
    public void test_ordered_keySet() {
        int i;
        int sz = 100;
        LinkedHashMap lhm = new LinkedHashMap();
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lhm.put(ii, ii.toString());
        }
        Set s1 = lhm.keySet();
        Iterator it1 = s1.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((lhm.size()) == (s1.size())));
        for (i = 0; it1.hasNext(); i++) {
            Integer jj = ((Integer) (it1.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == i));
        }
        LinkedHashMap lruhm = new LinkedHashMap(200, 0.75F, true);
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lruhm.put(ii, ii.toString());
        }
        Set s3 = lruhm.keySet();
        Iterator it3 = s3.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((lruhm.size()) == (s3.size())));
        for (i = 0; (i < sz) && (it3.hasNext()); i++) {
            Integer jj = ((Integer) (it3.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == i));
        }
        /* fetch the even numbered entries to affect traversal order */
        int p = 0;
        for (i = 0; i < sz; i += 2) {
            String ii = ((String) (lruhm.get(new Integer(i))));
            p = p + (Integer.parseInt(ii));
        }
        TestCase.assertEquals("invalid sum of even numbers", 2450, p);
        Set s2 = lruhm.keySet();
        Iterator it2 = s2.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((lruhm.size()) == (s2.size())));
        for (i = 1; (i < sz) && (it2.hasNext()); i += 2) {
            Integer jj = ((Integer) (it2.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == i));
        }
        for (i = 0; (i < sz) && (it2.hasNext()); i += 2) {
            Integer jj = ((Integer) (it2.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == i));
        }
        TestCase.assertTrue("Entries left to iterate on", (!(it2.hasNext())));
    }

    /**
     * java.util.LinkedHashMap#values()
     */
    public void test_ordered_values() {
        int i;
        int sz = 100;
        LinkedHashMap lhm = new LinkedHashMap();
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lhm.put(ii, new Integer((i * 2)));
        }
        Collection s1 = lhm.values();
        Iterator it1 = s1.iterator();
        TestCase.assertTrue("Returned set of incorrect size 1", ((lhm.size()) == (s1.size())));
        for (i = 0; it1.hasNext(); i++) {
            Integer jj = ((Integer) (it1.next()));
            TestCase.assertTrue("Returned incorrect entry set 1", ((jj.intValue()) == (i * 2)));
        }
        LinkedHashMap lruhm = new LinkedHashMap(200, 0.75F, true);
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lruhm.put(ii, new Integer((i * 2)));
        }
        Collection s3 = lruhm.values();
        Iterator it3 = s3.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((lruhm.size()) == (s3.size())));
        for (i = 0; (i < sz) && (it3.hasNext()); i++) {
            Integer jj = ((Integer) (it3.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == (i * 2)));
        }
        // fetch the even numbered entries to affect traversal order
        int p = 0;
        for (i = 0; i < sz; i += 2) {
            Integer ii = ((Integer) (lruhm.get(new Integer(i))));
            p = p + (ii.intValue());
        }
        TestCase.assertTrue("invalid sum of even numbers", (p == (2450 * 2)));
        Collection s2 = lruhm.values();
        Iterator it2 = s2.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((lruhm.size()) == (s2.size())));
        for (i = 1; (i < sz) && (it2.hasNext()); i += 2) {
            Integer jj = ((Integer) (it2.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == (i * 2)));
        }
        for (i = 0; (i < sz) && (it2.hasNext()); i += 2) {
            Integer jj = ((Integer) (it2.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((jj.intValue()) == (i * 2)));
        }
        TestCase.assertTrue("Entries left to iterate on", (!(it2.hasNext())));
    }

    /**
     * java.util.LinkedHashMap#removeEldestEntry(java.util.Map$Entry)
     */
    public void test_remove_eldest() {
        int i;
        int sz = 10;
        LinkedHashMapTest.CacheMap lhm = new LinkedHashMapTest.CacheMap();
        for (i = 0; i < sz; i++) {
            Integer ii = new Integer(i);
            lhm.put(ii, new Integer((i * 2)));
        }
        Collection s1 = lhm.values();
        Iterator it1 = s1.iterator();
        TestCase.assertTrue("Returned set of incorrect size 1", ((lhm.size()) == (s1.size())));
        for (i = 5; it1.hasNext(); i++) {
            Integer jj = ((Integer) (it1.next()));
            TestCase.assertTrue("Returned incorrect entry set 1", ((jj.intValue()) == (i * 2)));
        }
        TestCase.assertTrue("Entries left in map", (!(it1.hasNext())));
    }
}

