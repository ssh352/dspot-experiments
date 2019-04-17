/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.io.util;


import Dictionary.NOT_IN_DICTIONARY;
import HConstants.CATALOG_FAMILY;
import HConstants.EMPTY_BYTE_ARRAY;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests LRUDictionary
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestLRUDictionary {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLRUDictionary.class);

    LRUDictionary testee;

    @Test
    public void TestContainsNothing() {
        Assert.assertTrue(TestLRUDictionary.isDictionaryEmpty(testee));
    }

    /**
     * Assert can't add empty array.
     */
    @Test
    public void testPassingEmptyArrayToFindEntry() {
        Assert.assertEquals(NOT_IN_DICTIONARY, testee.findEntry(EMPTY_BYTE_ARRAY, 0, 0));
        Assert.assertEquals(NOT_IN_DICTIONARY, testee.addEntry(EMPTY_BYTE_ARRAY, 0, 0));
    }

    @Test
    public void testPassingSameArrayToAddEntry() {
        // Add random predefined byte array, in this case a random byte array from
        // HConstants.  Assert that when we add, we get new index.  Thats how it
        // works.
        int len = CATALOG_FAMILY.length;
        int index = testee.addEntry(CATALOG_FAMILY, 0, len);
        Assert.assertFalse((index == (testee.addEntry(CATALOG_FAMILY, 0, len))));
        Assert.assertFalse((index == (testee.addEntry(CATALOG_FAMILY, 0, len))));
    }

    @Test
    public void testBasic() {
        Random rand = new Random();
        byte[] testBytes = new byte[10];
        rand.nextBytes(testBytes);
        // Verify that our randomly generated array doesn't exist in the dictionary
        Assert.assertEquals((-1), testee.findEntry(testBytes, 0, testBytes.length));
        // now since we looked up an entry, we should have added it to the
        // dictionary, so it isn't empty
        Assert.assertFalse(TestLRUDictionary.isDictionaryEmpty(testee));
        // Check if we can find it using findEntry
        short t = testee.findEntry(testBytes, 0, testBytes.length);
        // Making sure we do find what we're looking for
        Assert.assertTrue((t != (-1)));
        byte[] testBytesCopy = new byte[20];
        Bytes.putBytes(testBytesCopy, 10, testBytes, 0, testBytes.length);
        // copy byte arrays, make sure that we check that equal byte arrays are
        // equal without just checking the reference
        Assert.assertEquals(testee.findEntry(testBytesCopy, 10, testBytes.length), t);
        // make sure the entry retrieved is the same as the one put in
        Assert.assertTrue(Arrays.equals(testBytes, testee.getEntry(t)));
        testee.clear();
        // making sure clear clears the dictionary
        Assert.assertTrue(TestLRUDictionary.isDictionaryEmpty(testee));
    }

    @Test
    public void TestLRUPolicy() {
        // start by filling the dictionary up with byte arrays
        for (int i = 0; i < (Short.MAX_VALUE); i++) {
            testee.findEntry(BigInteger.valueOf(i).toByteArray(), 0, BigInteger.valueOf(i).toByteArray().length);
        }
        // check we have the first element added
        Assert.assertTrue(((testee.findEntry(BigInteger.ZERO.toByteArray(), 0, BigInteger.ZERO.toByteArray().length)) != (-1)));
        // check for an element we know isn't there
        Assert.assertTrue(((testee.findEntry(BigInteger.valueOf(Integer.MAX_VALUE).toByteArray(), 0, BigInteger.valueOf(Integer.MAX_VALUE).toByteArray().length)) == (-1)));
        // since we just checked for this element, it should be there now.
        Assert.assertTrue(((testee.findEntry(BigInteger.valueOf(Integer.MAX_VALUE).toByteArray(), 0, BigInteger.valueOf(Integer.MAX_VALUE).toByteArray().length)) != (-1)));
        // test eviction, that the least recently added or looked at element is
        // evicted.  We looked at ZERO so it should be in the dictionary still.
        Assert.assertTrue(((testee.findEntry(BigInteger.ZERO.toByteArray(), 0, BigInteger.ZERO.toByteArray().length)) != (-1)));
        // Now go from beyond 1 to the end.
        for (int i = 1; i < (Short.MAX_VALUE); i++) {
            Assert.assertTrue(((testee.findEntry(BigInteger.valueOf(i).toByteArray(), 0, BigInteger.valueOf(i).toByteArray().length)) == (-1)));
        }
        // check we can find all of these.
        for (int i = 0; i < (Short.MAX_VALUE); i++) {
            Assert.assertTrue(((testee.findEntry(BigInteger.valueOf(i).toByteArray(), 0, BigInteger.valueOf(i).toByteArray().length)) != (-1)));
        }
    }
}
