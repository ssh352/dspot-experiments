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
package org.apache.beam.sdk.io.gcp.spanner;


import org.junit.Assert;
import org.junit.Test;


/**
 * A test of {@link SpannerSchema}.
 */
public class SpannerSchemaTest {
    @Test
    public void testSingleTable() throws Exception {
        SpannerSchema schema = SpannerSchema.builder().addColumn("test", "pk", "STRING(48)").addKeyPart("test", "pk", false).addColumn("test", "maxKey", "STRING(MAX)").build();
        Assert.assertEquals(1, schema.getTables().size());
        Assert.assertEquals(2, schema.getColumns("test").size());
        Assert.assertEquals(1, schema.getKeyParts("test").size());
    }

    @Test
    public void testTwoTables() throws Exception {
        SpannerSchema schema = SpannerSchema.builder().addColumn("test", "pk", "STRING(48)").addKeyPart("test", "pk", false).addColumn("test", "maxKey", "STRING(MAX)").addColumn("other", "pk", "INT64").addKeyPart("other", "pk", true).addColumn("other", "maxKey", "STRING(MAX)").build();
        Assert.assertEquals(2, schema.getTables().size());
        Assert.assertEquals(2, schema.getColumns("test").size());
        Assert.assertEquals(1, schema.getKeyParts("test").size());
        Assert.assertEquals(2, schema.getColumns("other").size());
        Assert.assertEquals(1, schema.getKeyParts("other").size());
    }
}
