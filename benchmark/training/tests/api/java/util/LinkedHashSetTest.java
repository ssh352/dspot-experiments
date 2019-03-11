/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import junit.framework.TestCase;


/**
 * java.util.LinkedHashSet
 */
public class LinkedHashSetTest extends TestCase {
    LinkedHashSet hs;

    Object[] objArray;

    /**
     * java.util.LinkedHashSet#LinkedHashSet()
     */
    public void test_Constructor() {
        // Test for method java.util.LinkedHashSet()
        LinkedHashSet hs2 = new LinkedHashSet();
        TestCase.assertEquals("Created incorrect LinkedHashSet", 0, hs2.size());
    }

    /**
     * java.util.LinkedHashSet#LinkedHashSet(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.LinkedHashSet(int)
        LinkedHashSet hs2 = new LinkedHashSet(5);
        TestCase.assertEquals("Created incorrect LinkedHashSet", 0, hs2.size());
        try {
            new LinkedHashSet((-1));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedHashSet#LinkedHashSet(int, float)
     */
    public void test_ConstructorIF() {
        // Test for method java.util.LinkedHashSet(int, float)
        LinkedHashSet hs2 = new LinkedHashSet(5, ((float) (0.5)));
        TestCase.assertEquals("Created incorrect LinkedHashSet", 0, hs2.size());
        try {
            new LinkedHashSet((-1), 0.5F);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new LinkedHashSet(1, (-0.5F));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new LinkedHashSet(1, 0.0F);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedHashSet#LinkedHashSet(java.util.Collection)
     */
    public void test_ConstructorLjava_util_Collection() {
        // Test for method java.util.LinkedHashSet(java.util.Collection)
        LinkedHashSet hs2 = new LinkedHashSet(Arrays.asList(objArray));
        for (int counter = 0; counter < (objArray.length); counter++)
            TestCase.assertTrue("LinkedHashSet does not contain correct elements", hs.contains(objArray[counter]));

        TestCase.assertTrue("LinkedHashSet created from collection incorrect size", ((hs2.size()) == (objArray.length)));
        try {
            new LinkedHashSet(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedHashSet#add(java.lang.Object)
     */
    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.LinkedHashSet.add(java.lang.Object)
        int size = hs.size();
        hs.add(new Integer(8));
        TestCase.assertTrue("Added element already contained by set", ((hs.size()) == size));
        hs.add(new Integer((-9)));
        TestCase.assertTrue("Failed to increment set size after add", ((hs.size()) == (size + 1)));
        TestCase.assertTrue("Failed to add element to set", hs.contains(new Integer((-9))));
    }

    /**
     * java.util.LinkedHashSet#clear()
     */
    public void test_clear() {
        // Test for method void java.util.LinkedHashSet.clear()
        Set orgSet = ((Set) (hs.clone()));
        hs.clear();
        Iterator i = orgSet.iterator();
        TestCase.assertEquals("Returned non-zero size after clear", 0, hs.size());
        while (i.hasNext())
            TestCase.assertTrue("Failed to clear set", (!(hs.contains(i.next()))));

    }

    /**
     * java.util.LinkedHashSet#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.LinkedHashSet.clone()
        LinkedHashSet hs2 = ((LinkedHashSet) (hs.clone()));
        TestCase.assertTrue("clone returned an equivalent LinkedHashSet", ((hs) != hs2));
        TestCase.assertTrue("clone did not return an equal LinkedHashSet", hs.equals(hs2));
    }

    /**
     * java.util.LinkedHashSet#contains(java.lang.Object)
     */
    public void test_containsLjava_lang_Object() {
        // Test for method boolean
        // java.util.LinkedHashSet.contains(java.lang.Object)
        TestCase.assertTrue("Returned false for valid object", hs.contains(objArray[90]));
        TestCase.assertTrue("Returned true for invalid Object", (!(hs.contains(new Object()))));
        LinkedHashSet s = new LinkedHashSet();
        s.add(null);
        TestCase.assertTrue("Cannot handle null", s.contains(null));
    }

    /**
     * java.util.LinkedHashSet#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.LinkedHashSet.isEmpty()
        TestCase.assertTrue("Empty set returned false", new LinkedHashSet().isEmpty());
        TestCase.assertTrue("Non-empty set returned true", (!(hs.isEmpty())));
    }

    /**
     * java.util.LinkedHashSet#iterator()
     */
    public void test_iterator() {
        // Test for method java.util.Iterator java.util.LinkedHashSet.iterator()
        Iterator i = hs.iterator();
        int x = 0;
        int j;
        for (j = 0; i.hasNext(); j++) {
            Object oo = i.next();
            if (oo != null) {
                Integer ii = ((Integer) (oo));
                TestCase.assertTrue("Incorrect element found", ((ii.intValue()) == j));
            } else {
                TestCase.assertTrue("Cannot find null", hs.contains(oo));
            }
            ++x;
        }
        TestCase.assertTrue("Returned iteration of incorrect size", ((hs.size()) == x));
        LinkedHashSet s = new LinkedHashSet();
        s.add(null);
        TestCase.assertNull("Cannot handle null", s.iterator().next());
    }

    /**
     * java.util.LinkedHashSet#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method boolean
        // java.util.LinkedHashSet.remove(java.lang.Object)
        int size = hs.size();
        hs.remove(new Integer(98));
        TestCase.assertTrue("Failed to remove element", (!(hs.contains(new Integer(98)))));
        TestCase.assertTrue("Failed to decrement set size", ((hs.size()) == (size - 1)));
        LinkedHashSet s = new LinkedHashSet();
        s.add(null);
        TestCase.assertTrue("Cannot handle null", s.remove(null));
    }

    /**
     * java.util.LinkedHashSet#size()
     */
    public void test_size() {
        // Test for method int java.util.LinkedHashSet.size()
        TestCase.assertTrue("Returned incorrect size", ((hs.size()) == ((objArray.length) + 1)));
        hs.clear();
        TestCase.assertEquals("Cleared set returned non-zero size", 0, hs.size());
    }

    class Mock_LinkedHashSet extends LinkedHashSet {
        @Override
        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }
    }

    public void test_retainAllLjava_util_Collection() {
        LinkedHashSet<Integer> lhs = new LinkedHashSet<Integer>();
        Vector v = new Vector<Float>();
        v.add(new Float(3.14));
        lhs.add(new Integer(1));
        TestCase.assertEquals(1, lhs.size());
        lhs.retainAll(v);
        TestCase.assertEquals(0, lhs.size());
        v = new Vector<Integer>();
        v.add(new Integer(1));
        v.add(new Integer(2));
        v.add(new Integer(3));
        v.add(new Integer(4));
        v.add(new Integer(5));
        v.add(new Integer(6));
        lhs.add(new Integer(1));
        lhs.add(new Integer(6));
        lhs.add(new Integer(7));
        lhs.add(new Integer(8));
        lhs.add(new Integer(9));
        lhs.add(new Integer(10));
        lhs.add(new Integer(11));
        lhs.add(new Integer(12));
        lhs.add(new Integer(13));
        TestCase.assertEquals(9, lhs.size());
        lhs.retainAll(v);
        TestCase.assertEquals(2, lhs.size());
        try {
            lhs.retainAll(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        lhs = new LinkedHashSetTest.Mock_LinkedHashSet();
        try {
            lhs.retainAll(v);
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void test_toArray() {
        LinkedHashSet<Integer> lhs = new LinkedHashSet<Integer>();
        lhs.add(new Integer(1));
        lhs.add(new Integer(6));
        lhs.add(new Integer(7));
        lhs.add(new Integer(8));
        lhs.add(new Integer(9));
        lhs.add(new Integer(10));
        lhs.add(new Integer(11));
        lhs.add(new Integer(12));
        lhs.add(new Integer(13));
        Object[] o = lhs.toArray();
        for (int i = 0; i < (o.length); i++) {
            TestCase.assertTrue(lhs.contains(o[i]));
        }
        TestCase.assertEquals(lhs.size(), o.length);
    }

    public void test_toArray$Ljava_lang_Object() {
        LinkedHashSet<Integer> lhs = new LinkedHashSet<Integer>();
        lhs.add(new Integer(1));
        lhs.add(new Integer(6));
        lhs.add(new Integer(7));
        lhs.add(new Integer(8));
        lhs.add(new Integer(9));
        lhs.add(new Integer(10));
        lhs.add(new Integer(11));
        lhs.add(new Integer(12));
        lhs.add(new Integer(13));
        Object[] o1 = new Object[lhs.size()];
        Object[] o2 = new Double[lhs.size()];
        lhs.toArray(o1);
        for (int i = 0; i < (o1.length); i++) {
            TestCase.assertTrue(lhs.contains(o1[i]));
        }
        try {
            lhs.toArray(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            lhs.toArray(o2);
            TestCase.fail("ArrayStoreException expected");
        } catch (ArrayStoreException e) {
            // expected
        }
    }
}

