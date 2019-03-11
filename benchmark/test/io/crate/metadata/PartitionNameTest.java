/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.metadata;


import IndexParts.PARTITIONED_TABLE_PART;
import com.google.common.collect.ImmutableList;
import io.crate.test.integration.CrateUnitTest;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import static IndexParts.PARTITIONED_TABLE_PART;


public class PartitionNameTest extends CrateUnitTest {
    @Test
    public void testSingleColumn() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test"), ImmutableList.of("1"));
        assertThat(partitionName.values().size(), Is.is(1));
        assertEquals(ImmutableList.of("1"), partitionName.values());
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testSingleColumnSchema() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("schema", "test"), ImmutableList.of("1"));
        assertThat(partitionName.values().size(), Is.is(1));
        assertEquals(ImmutableList.of("1"), partitionName.values());
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testMultipleColumns() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test"), ImmutableList.of("1", "foo"));
        assertThat(partitionName.values().size(), Is.is(2));
        assertEquals(ImmutableList.of("1", "foo"), partitionName.values());
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testMultipleColumnsSchema() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("schema", "test"), ImmutableList.of("1", "foo"));
        assertThat(partitionName.values().size(), Is.is(2));
        assertEquals(ImmutableList.of("1", "foo"), partitionName.values());
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testNull() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test"), Collections.singletonList(null));
        assertThat(partitionName.values().size(), Is.is(1));
        assertEquals(null, partitionName.values().get(0));
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testNullSchema() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("schema", "test"), Collections.singletonList(null));
        assertThat(partitionName.values().size(), Is.is(1));
        assertEquals(null, partitionName.values().get(0));
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testEmptyStringValue() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test"), ImmutableList.of(""));
        assertThat(partitionName.values().size(), Is.is(1));
        assertEquals(ImmutableList.of(""), partitionName.values());
        PartitionName partitionName1 = PartitionName.fromIndexOrTemplate(partitionName.asIndexName());
        assertEquals(partitionName.values(), partitionName1.values());
    }

    @Test
    public void testPartitionNameNotFromTable() throws Exception {
        String partitionName = (PARTITIONED_TABLE_PART) + "test1._1";
        assertFalse(PartitionName.fromIndexOrTemplate(partitionName).relationName().name().equals("test"));
    }

    @Test
    public void testPartitionNameNotFromSchema() throws Exception {
        String partitionName = ("schema1." + (PARTITIONED_TABLE_PART)) + "test1._1";
        assertFalse(PartitionName.fromIndexOrTemplate(partitionName).relationName().schema().equals("schema"));
    }

    @Test
    public void testInvalidValueString() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid partition ident: 1");
        String partitionName = (PARTITIONED_TABLE_PART) + "test.1";
        PartitionName.fromIndexOrTemplate(partitionName).values();
    }

    @Test
    public void testIsPartition() throws Exception {
        assertFalse(IndexParts.isPartitioned("test"));
        assertTrue(IndexParts.isPartitioned(((PARTITIONED_TABLE_PART) + "test.")));
        assertTrue(IndexParts.isPartitioned((("schema." + (PARTITIONED_TABLE_PART)) + "test.")));
        assertFalse(IndexParts.isPartitioned("partitioned.test.dshhjfgjsdh"));
        assertFalse(IndexParts.isPartitioned("schema.partitioned.test.dshhjfgjsdh"));
        assertFalse(IndexParts.isPartitioned(".test.dshhjfgjsdh"));
        assertFalse(IndexParts.isPartitioned("schema.test.dshhjfgjsdh"));
        assertTrue(IndexParts.isPartitioned(".partitioned.test.dshhjfgjsdh"));
        assertTrue(IndexParts.isPartitioned("schema..partitioned.test.dshhjfgjsdh"));
    }

    @Test
    public void testFromIndexOrTemplate() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "t"), Arrays.asList("a", "b"));
        assertThat(partitionName, Matchers.equalTo(PartitionName.fromIndexOrTemplate(partitionName.asIndexName())));
        partitionName = new PartitionName(new RelationName("doc", "t"), Arrays.asList("a", "b"));
        assertThat(partitionName, Matchers.equalTo(PartitionName.fromIndexOrTemplate(partitionName.asIndexName())));
        assertThat(partitionName.ident(), Is.is("081620j2"));
        partitionName = new PartitionName(new RelationName("schema", "t"), Arrays.asList("a", "b"));
        assertThat(partitionName, Matchers.equalTo(PartitionName.fromIndexOrTemplate(partitionName.asIndexName())));
        assertThat(partitionName.ident(), Is.is("081620j2"));
        partitionName = new PartitionName(new RelationName("doc", "t"), Collections.singletonList("hoschi"));
        assertThat(partitionName, Matchers.equalTo(PartitionName.fromIndexOrTemplate(partitionName.asIndexName())));
        assertThat(partitionName.ident(), Is.is("043mgrrjcdk6i"));
        partitionName = new PartitionName(new RelationName("doc", "t"), Collections.singletonList(null));
        assertThat(partitionName, Matchers.equalTo(PartitionName.fromIndexOrTemplate(partitionName.asIndexName())));
        assertThat(partitionName.ident(), Is.is("0400"));
    }

    @Test
    public void splitTemplateName() throws Exception {
        PartitionName partitionName = PartitionName.fromIndexOrTemplate(PartitionName.templateName("schema", "t"));
        assertThat(partitionName.relationName(), Is.is(new RelationName("schema", "t")));
        assertThat(partitionName.ident(), Is.is(""));
    }

    @Test
    public void testSplitInvalid1() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        String part = PARTITIONED_TABLE_PART.substring(0, ((PARTITIONED_TABLE_PART.length()) - 1));
        PartitionName.fromIndexOrTemplate((part + "lalala.n"));
    }

    @Test
    public void testSplitInvalid2() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        PartitionName.fromIndexOrTemplate(((PARTITIONED_TABLE_PART.substring(1)) + "lalala.n"));
    }

    @Test
    public void testSplitInvalid3() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        PartitionName.fromIndexOrTemplate("lalala");
    }

    @Test
    public void testSplitInvalid4() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        PartitionName.fromIndexOrTemplate(((PARTITIONED_TABLE_PART) + "lalala"));
    }

    @Test
    public void testSplitInvalidWithSchema1() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        PartitionName.fromIndexOrTemplate((("schema" + (PARTITIONED_TABLE_PART)) + "lalala"));
    }

    @Test
    public void testSplitInvalidWithSchema2() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid index name");
        PartitionName.fromIndexOrTemplate((("schema." + (PARTITIONED_TABLE_PART)) + "lalala"));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx")).equals(new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx"))));
        assertTrue(new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx")).equals(new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx"))));
        assertFalse(new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx")).equals(new PartitionName(new RelationName("schema", "table"), Arrays.asList("xxx"))));
        PartitionName name = new PartitionName(new RelationName("doc", "table"), Arrays.asList("xxx"));
        assertTrue(name.equals(PartitionName.fromIndexOrTemplate(name.asIndexName())));
    }
}

