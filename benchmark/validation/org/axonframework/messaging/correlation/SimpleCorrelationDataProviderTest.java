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
package org.axonframework.messaging.correlation;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.axonframework.messaging.Message;
import org.junit.Assert;
import org.junit.Test;


public class SimpleCorrelationDataProviderTest {
    @Test
    public void testResolveCorrelationData() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("key1", "value1");
        metaData.put("key2", "value2");
        metaData.put("key3", "value3");
        Message message = new org.axonframework.messaging.GenericMessage("payload", metaData);
        Assert.assertEquals(Collections.singletonMap("key1", "value1"), new SimpleCorrelationDataProvider("key1").correlationDataFor(message));
        final Map<String, ?> actual2 = new SimpleCorrelationDataProvider("key1", "key2", "noExist", null).correlationDataFor(message);
        Assert.assertEquals("value1", actual2.get("key1"));
        Assert.assertEquals("value2", actual2.get("key2"));
    }
}

