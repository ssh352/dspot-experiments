/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.requests;


import MetadataRequestData.SCHEMAS;
import java.util.Collections;
import org.apache.kafka.common.message.MetadataRequestData;
import org.junit.Assert;
import org.junit.Test;


public class MetadataRequestTest {
    @Test
    public void testEmptyMeansAllTopicsV0() {
        MetadataRequestData data = new MetadataRequestData();
        MetadataRequest parsedRequest = new MetadataRequest(data, ((short) (0)));
        Assert.assertTrue(parsedRequest.isAllTopics());
        Assert.assertNull(parsedRequest.topics());
    }

    @Test
    public void testEmptyMeansEmptyForVersionsAboveV0() {
        for (int i = 1; i < (SCHEMAS.length); i++) {
            MetadataRequestData data = new MetadataRequestData();
            data.setAllowAutoTopicCreation(true);
            MetadataRequest parsedRequest = new MetadataRequest(data, ((short) (i)));
            Assert.assertFalse(parsedRequest.isAllTopics());
            Assert.assertEquals(Collections.emptyList(), parsedRequest.topics());
        }
    }
}
