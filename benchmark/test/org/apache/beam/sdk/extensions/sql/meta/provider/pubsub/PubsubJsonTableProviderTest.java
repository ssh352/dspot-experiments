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
package org.apache.beam.sdk.extensions.sql.meta.provider.pubsub;


import junit.framework.TestCase;
import org.apache.beam.sdk.extensions.sql.BeamSqlTable;
import org.apache.beam.sdk.extensions.sql.impl.utils.CalciteUtils;
import org.apache.beam.sdk.extensions.sql.meta.Table;
import org.apache.beam.sdk.schemas.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link PubsubJsonTableProvider}.
 */
public class PubsubJsonTableProviderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTableTypePubsub() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Assert.assertEquals("pubsub", provider.getTableType());
    }

    @Test
    public void testCreatesTable() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).addRowField("payload", Schema.builder().build()).build();
        Table tableDefinition = PubsubJsonTableProviderTest.tableDefinition().schema(messageSchema).build();
        BeamSqlTable pubsubTable = provider.buildBeamSqlTable(tableDefinition);
        TestCase.assertNotNull(pubsubTable);
        Assert.assertEquals(messageSchema, pubsubTable.getSchema());
    }

    @Test
    public void testThrowsIfTimestampFieldNotProvided() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Schema messageSchema = Schema.builder().addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).addRowField("payload", Schema.builder().build()).build();
        Table tableDefinition = PubsubJsonTableProviderTest.tableDefinition().schema(messageSchema).build();
        thrown.expectMessage("Unsupported");
        thrown.expectMessage("'event_timestamp'");
        provider.buildBeamSqlTable(tableDefinition);
    }

    @Test
    public void testThrowsIfAttributesFieldNotProvided() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addRowField("payload", Schema.builder().build()).build();
        Table tableDefinition = PubsubJsonTableProviderTest.tableDefinition().schema(messageSchema).build();
        thrown.expectMessage("Unsupported");
        thrown.expectMessage("'attributes'");
        provider.buildBeamSqlTable(tableDefinition);
    }

    @Test
    public void testThrowsIfPayloadFieldNotProvided() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).build();
        Table tableDefinition = PubsubJsonTableProviderTest.tableDefinition().schema(messageSchema).build();
        thrown.expectMessage("Unsupported");
        thrown.expectMessage("'payload'");
        provider.buildBeamSqlTable(tableDefinition);
    }

    @Test
    public void testThrowsIfExtraFieldsExist() {
        PubsubJsonTableProvider provider = new PubsubJsonTableProvider();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).addStringField("someField").addRowField("payload", Schema.builder().build()).build();
        Table tableDefinition = PubsubJsonTableProviderTest.tableDefinition().schema(messageSchema).build();
        thrown.expectMessage("Unsupported");
        thrown.expectMessage("'event_timestamp'");
        provider.buildBeamSqlTable(tableDefinition);
    }
}

