/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.opentracing.propagation;


import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CamelMessagingHeadersInjectAdapterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<String, Object> map;

    @Test
    public void cannotGetIterator() {
        CamelMessagingHeadersInjectAdapter adapter = new CamelMessagingHeadersInjectAdapter(map, true);
        thrown.expect(UnsupportedOperationException.class);
        adapter.iterator();
    }

    @Test
    public void putProperties() {
        CamelMessagingHeadersInjectAdapter adapter = new CamelMessagingHeadersInjectAdapter(map, true);
        adapter.put("key1", "value1");
        adapter.put("key2", "value2");
        adapter.put("key1", "value3");
        Assert.assertEquals("value3", map.get("key1"));
        Assert.assertEquals("value2", map.get("key2"));
    }

    @Test
    public void propertyWithDash() {
        CamelMessagingHeadersInjectAdapter adapter = new CamelMessagingHeadersInjectAdapter(map, true);
        adapter.put("-key-1-", "value1");
        Assert.assertEquals("value1", map.get((((((CamelMessagingHeadersInjectAdapter.JMS_DASH) + "key") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)) + "1") + (CamelMessagingHeadersInjectAdapter.JMS_DASH))));
    }

    @Test
    public void propertyWithoutDashEncoding() {
        CamelMessagingHeadersInjectAdapter adapter = new CamelMessagingHeadersInjectAdapter(map, false);
        adapter.put("-key-1-", "value1");
        Assert.assertEquals(null, map.get((((((CamelMessagingHeadersInjectAdapter.JMS_DASH) + "key") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)) + "1") + (CamelMessagingHeadersInjectAdapter.JMS_DASH))));
    }
}
