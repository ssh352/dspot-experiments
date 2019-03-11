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
package org.apache.flink.util;


import java.io.InputStream;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class contains tests for the {@link org.apache.flink.util.AbstractID} class.
 */
public class AbstractIDTest extends TestLogger {
    /**
     * Tests the serialization/deserialization of an abstract ID.
     */
    @Test
    public void testSerialization() throws Exception {
        final AbstractID origID = new AbstractID();
        final AbstractID copyID = CommonTestUtils.createCopySerializable(origID);
        Assert.assertEquals(origID.hashCode(), copyID.hashCode());
        Assert.assertEquals(origID, copyID);
    }

    @Test
    public void testConvertToBytes() throws Exception {
        final AbstractID origID = new AbstractID();
        AbstractID copy1 = new AbstractID(origID);
        AbstractID copy2 = new AbstractID(origID.getBytes());
        AbstractID copy3 = new AbstractID(origID.getLowerPart(), origID.getUpperPart());
        Assert.assertEquals(origID, copy1);
        Assert.assertEquals(origID, copy2);
        Assert.assertEquals(origID, copy3);
    }

    @Test
    public void testCompare() throws Exception {
        AbstractID id1 = new AbstractID(0, 0);
        AbstractID id2 = new AbstractID(1, 0);
        AbstractID id3 = new AbstractID(0, 1);
        AbstractID id4 = new AbstractID((-1), 0);
        AbstractID id5 = new AbstractID(0, (-1));
        AbstractID id6 = new AbstractID((-1), (-1));
        AbstractID id7 = new AbstractID(Long.MAX_VALUE, Long.MAX_VALUE);
        AbstractID id8 = new AbstractID(Long.MIN_VALUE, Long.MIN_VALUE);
        AbstractID id9 = new AbstractID(Long.MAX_VALUE, Long.MIN_VALUE);
        AbstractID id10 = new AbstractID(Long.MIN_VALUE, Long.MAX_VALUE);
        // test self equality
        Assert.assertEquals(0, id1.compareTo(CommonTestUtils.createCopySerializable(id1)));
        Assert.assertEquals(0, id2.compareTo(CommonTestUtils.createCopySerializable(id2)));
        Assert.assertEquals(0, id3.compareTo(CommonTestUtils.createCopySerializable(id3)));
        Assert.assertEquals(0, id4.compareTo(CommonTestUtils.createCopySerializable(id4)));
        Assert.assertEquals(0, id5.compareTo(CommonTestUtils.createCopySerializable(id5)));
        Assert.assertEquals(0, id6.compareTo(CommonTestUtils.createCopySerializable(id6)));
        Assert.assertEquals(0, id7.compareTo(CommonTestUtils.createCopySerializable(id7)));
        Assert.assertEquals(0, id8.compareTo(CommonTestUtils.createCopySerializable(id8)));
        Assert.assertEquals(0, id9.compareTo(CommonTestUtils.createCopySerializable(id9)));
        Assert.assertEquals(0, id10.compareTo(CommonTestUtils.createCopySerializable(id10)));
        // test order
        AbstractIDTest.assertCompare(id1, id2, (-1));
        AbstractIDTest.assertCompare(id1, id3, (-1));
        AbstractIDTest.assertCompare(id1, id4, 1);
        AbstractIDTest.assertCompare(id1, id5, 1);
        AbstractIDTest.assertCompare(id1, id6, 1);
        AbstractIDTest.assertCompare(id2, id5, 1);
        AbstractIDTest.assertCompare(id3, id5, 1);
        AbstractIDTest.assertCompare(id2, id6, 1);
        AbstractIDTest.assertCompare(id3, id6, 1);
        AbstractIDTest.assertCompare(id1, id7, (-1));
        AbstractIDTest.assertCompare(id1, id8, 1);
        AbstractIDTest.assertCompare(id7, id8, 1);
        AbstractIDTest.assertCompare(id9, id10, (-1));
        AbstractIDTest.assertCompare(id7, id9, 1);
        AbstractIDTest.assertCompare(id7, id10, 1);
        AbstractIDTest.assertCompare(id8, id9, (-1));
        AbstractIDTest.assertCompare(id8, id10, (-1));
    }

    /**
     * FLINK-10412 marks the {@link AbstractID#toString} field as transient. This tests ensures
     * that {@link AbstractID} which have been serialized with the toString field can still
     * be deserialized. For that purpose the files abstractID-with-toString-field and
     * abstractID-with-toString-field-set have been created with the serialized data.
     */
    @Test
    public void testOldAbstractIDDeserialization() throws Exception {
        final long lowerPart = 42L;
        final long upperPart = 1337L;
        final AbstractID expectedAbstractId = new AbstractID(lowerPart, upperPart);
        final String resourceName1 = "abstractID-with-toString-field";
        try (final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourceName1)) {
            final AbstractID deserializedAbstractId = InstantiationUtil.deserializeObject(resourceAsStream, getClass().getClassLoader());
            Assert.assertThat(deserializedAbstractId, Matchers.is(Matchers.equalTo(expectedAbstractId)));
        }
        final String resourceName2 = "abstractID-with-toString-field-set";
        try (final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourceName2)) {
            final AbstractID deserializedAbstractId = InstantiationUtil.deserializeObject(resourceAsStream, getClass().getClassLoader());
            Assert.assertThat(deserializedAbstractId, Matchers.is(Matchers.equalTo(expectedAbstractId)));
        }
    }
}

