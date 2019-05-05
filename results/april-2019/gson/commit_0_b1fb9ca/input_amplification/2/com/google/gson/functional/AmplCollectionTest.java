package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.common.TestTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;


public class AmplCollectionTest extends TestCase {
    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gson = new Gson();
    }

    static class HasArrayListField {
        ArrayList<Long> longs = new ArrayList<Long>();
    }

    @SuppressWarnings("rawtypes")
    private static int[] toIntArray(Collection collection) {
        int[] ints = new int[collection.size()];
        int i = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ++i) {
            Object obj = iterator.next();
            if (obj instanceof Integer) {
                ints[i] = ((Integer) (obj)).intValue();
            } else {
                if (obj instanceof Long) {
                    ints[i] = ((Long) (obj)).intValue();
                }
            }
        }
        return ints;
    }

    private static class ObjectWithWildcardCollection {
        private final Collection<? extends TestTypes.BagOfPrimitives> collection;

        public ObjectWithWildcardCollection(Collection<? extends TestTypes.BagOfPrimitives> collection) {
            this.collection = collection;
        }

        public Collection<? extends TestTypes.BagOfPrimitives> getCollection() {
            return collection;
        }
    }

    private static class Entry {
        int value;

        Entry(int value) {
            this.value = value;
        }
    }

    public void testSetSerialization_add20_literalMutationString162() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add20__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add20__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add20__9 = json.contains("[");
        boolean o_testSetSerialization_add20__10 = json.contains("2");
        boolean o_testSetSerialization_add20__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber32740_add33258() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber32740__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber32740__6 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationNumber32740_add33258__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", o_testSetSerialization_literalMutationNumber32740_add33258__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber32740__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber32740__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", o_testSetSerialization_literalMutationNumber32740_add33258__12);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString10() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString10__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__3);
        boolean o_testSetSerialization_literalMutationString10__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString10__9 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__9);
        boolean o_testSetSerialization_literalMutationString10__10 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__10);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__9);
    }

    public void testSetSerialization_literalMutationString13() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString13__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__3);
        boolean o_testSetSerialization_literalMutationString13__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString13__9 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__9);
        boolean o_testSetSerialization_literalMutationString13__10 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__10);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13__9);
    }

    public void testSetSerialization_literalMutationNumber32738_add33288() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber32738__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber32738__6 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationNumber32738_add33288__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", o_testSetSerialization_literalMutationNumber32738_add33288__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber32738__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber32738__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", o_testSetSerialization_literalMutationNumber32738_add33288__12);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }
}
