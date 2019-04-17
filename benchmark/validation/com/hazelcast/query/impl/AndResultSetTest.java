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
package com.hazelcast.query.impl;


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings("unchecked")
public class AndResultSetTest extends HazelcastTestSupport {
    // https://github.com/hazelcast/hazelcast/issues/1501
    @Test
    public void iteratingOver_noException() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        AndResultSet resultSet = new AndResultSet(entries, null, asList(new AndResultSetTest.FalsePredicate()));
        Iterator it = resultSet.iterator();
        boolean result = it.hasNext();
        Assert.assertFalse(result);
    }

    // https://github.com/hazelcast/hazelcast/issues/9614
    @Test
    public void size_nonMatchingPredicate() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        AndResultSet resultSet = new AndResultSet(entries, null, asList(new AndResultSetTest.FalsePredicate()));
        int size = resultSet.size();
        int countedSize = 0;
        for (QueryableEntry queryableEntry : resultSet) {
            countedSize++;
        }
        Assert.assertEquals(0, countedSize);
        Assert.assertEquals(size, countedSize);
    }

    // https://github.com/hazelcast/hazelcast/issues/9614
    @Test
    public void size_matchingPredicate_notInResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        otherIndexedResults.add(Collections.<QueryableEntry>emptySet());
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        int size = resultSet.size();
        int countedSize = 0;
        for (QueryableEntry queryableEntry : resultSet) {
            countedSize++;
        }
        Assert.assertEquals(0, countedSize);
        Assert.assertEquals(size, countedSize);
    }

    // https://github.com/hazelcast/hazelcast/issues/9614
    @Test
    public void size_matchingPredicate_noOtherResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        int size = resultSet.size();
        int countedSize = 0;
        for (QueryableEntry queryableEntry : resultSet) {
            countedSize++;
        }
        Assert.assertEquals(100000, countedSize);
        Assert.assertEquals(size, countedSize);
    }

    // https://github.com/hazelcast/hazelcast/issues/9614
    @Test
    public void size_matchingPredicate_inOtherResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        Set<QueryableEntry> otherIndexResult = new HashSet<QueryableEntry>();
        otherIndexResult.add(entries.iterator().next());
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        otherIndexedResults.add(otherIndexResult);
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        int size = resultSet.size();
        int countedSize = 0;
        for (QueryableEntry queryableEntry : resultSet) {
            countedSize++;
        }
        Assert.assertEquals(1, countedSize);
        Assert.assertEquals(size, countedSize);
    }

    @Test
    public void contains_nonMatchingPredicate() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        AndResultSet resultSet = new AndResultSet(entries, null, asList(new AndResultSetTest.FalsePredicate()));
        HazelcastTestSupport.assertNotContains(resultSet, entries.iterator().next());
    }

    @Test
    public void contains_matchingPredicate_notInResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        otherIndexedResults.add(Collections.<QueryableEntry>emptySet());
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        HazelcastTestSupport.assertNotContains(resultSet, entries.iterator().next());
    }

    @Test
    public void contains_matchingPredicate_noOtherResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        for (QueryableEntry entry : entries) {
            HazelcastTestSupport.assertContains(resultSet, entry);
        }
    }

    @Test
    public void contains_matchingPredicate_inOtherResult() {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        Set<QueryableEntry> otherIndexResult = new HashSet<QueryableEntry>();
        otherIndexResult.add(entries.iterator().next());
        List<Set<QueryableEntry>> otherIndexedResults = new ArrayList<Set<QueryableEntry>>();
        otherIndexedResults.add(otherIndexResult);
        AndResultSet resultSet = new AndResultSet(entries, otherIndexedResults, asList(new TruePredicate()));
        Iterator<QueryableEntry> it = entries.iterator();
        HazelcastTestSupport.assertContains(resultSet, it.next());
        while (it.hasNext()) {
            HazelcastTestSupport.assertNotContains(resultSet, it.next());
        } 
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeUnsupported() throws IOException {
        Set<QueryableEntry> entries = AndResultSetTest.generateEntries(100000);
        AndResultSet resultSet = new AndResultSet(entries, null, asList(new TruePredicate()));
        resultSet.remove(resultSet.iterator().next());
    }

    private static class FalsePredicate implements Predicate {
        @Override
        public boolean apply(Map.Entry mapEntry) {
            return false;
        }
    }

    private static class DummyEntry extends QueryableEntry {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public Comparable getAttributeValue(String attributeName) throws QueryException {
            return null;
        }

        @Override
        public Object getTargetObject(boolean key) {
            return null;
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

        @Override
        public Data getKeyData() {
            return null;
        }

        @Override
        public Data getValueData() {
            return null;
        }
    }
}
