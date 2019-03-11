/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.watch;


import WatchesSummary.KEY_NUM_CONNECTIONS;
import WatchesSummary.KEY_NUM_PATHS;
import WatchesSummary.KEY_NUM_TOTAL_WATCHES;
import java.util.Map;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;


public class WatchesSummaryTest extends ZKTestCase {
    private WatchesSummary s;

    @Test
    public void testGetters() {
        Assert.assertEquals(1, s.getNumConnections());
        Assert.assertEquals(2, s.getNumPaths());
        Assert.assertEquals(3, s.getTotalWatches());
    }

    @Test
    public void testToMap() {
        Map<String, Object> m = s.toMap();
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(Integer.valueOf(1), m.get(KEY_NUM_CONNECTIONS));
        Assert.assertEquals(Integer.valueOf(2), m.get(KEY_NUM_PATHS));
        Assert.assertEquals(Integer.valueOf(3), m.get(KEY_NUM_TOTAL_WATCHES));
    }
}

