/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.modelling.saga.repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.axonframework.modelling.saga.AssociationValue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class AssociationValueMapTest {
    private AssociationValueMap testSubject;

    @Test
    public void testStoreVarietyOfItems() {
        Assert.assertTrue(testSubject.isEmpty());
        Object anObject = new Object();
        testSubject.add(av("1"), "T", "1");
        testSubject.add(av("1"), "T", "1");
        Assert.assertEquals("Wrong count after adding an object twice", 1, testSubject.size());
        testSubject.add(av("2"), "T", "1");
        Assert.assertEquals("Wrong count after adding two objects", 2, testSubject.size());
        testSubject.add(av("a"), "T", "1");
        testSubject.add(av("a"), "T", "1");
        Assert.assertEquals("Wrong count after adding two identical Strings", 3, testSubject.size());
        testSubject.add(av("b"), "T", "1");
        Assert.assertEquals("Wrong count after adding two identical Strings", 4, testSubject.size());
        testSubject.add(av("a"), "T", "2");
        testSubject.add(av("a"), "Y", "2");
        Assert.assertEquals("Wrong count after adding two identical Strings for different saga", 6, testSubject.size());
        Assert.assertEquals(2, testSubject.findSagas("T", av("a")).size());
    }

    @Test
    public void testRemoveItems() {
        testStoreVarietyOfItems();
        Assert.assertEquals("Wrong initial item count", 6, testSubject.size());
        testSubject.remove(av("a"), "T", "1");
        Assert.assertEquals("Wrong item count", 5, testSubject.size());
        testSubject.remove(av("a"), "T", "2");
        Assert.assertEquals("Wrong item count", 4, testSubject.size());
        testSubject.clear();
        Assert.assertTrue(testSubject.isEmpty());
        Assert.assertEquals("Wrong item count", 0, testSubject.size());
    }

    @Test
    public void testFindAssociations() {
        List<AssociationValue> usedAssociations = new ArrayList<>(1000);
        for (int t = 0; t < 1000; t++) {
            String key = UUID.randomUUID().toString();
            for (int i = 0; i < 10; i++) {
                AssociationValue associationValue = new AssociationValue(key, UUID.randomUUID().toString());
                if ((usedAssociations.size()) < 1000) {
                    usedAssociations.add(associationValue);
                }
                testSubject.add(associationValue, "type", key);
            }
        }
        Assert.assertEquals(10000, testSubject.size());
        for (AssociationValue item : usedAssociations) {
            Set<String> actualResult = testSubject.findSagas("type", item);
            Assert.assertEquals(("Failure on item: " + (usedAssociations.indexOf(item))), 1, actualResult.size());
            Assert.assertEquals(item.getKey(), actualResult.iterator().next());
        }
    }
}

