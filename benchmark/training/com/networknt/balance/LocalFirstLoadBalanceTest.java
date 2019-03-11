/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.balance;


import com.networknt.registry.URL;
import com.networknt.registry.URLImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dan on 2016-12-29
 */
public class LocalFirstLoadBalanceTest {
    LoadBalance loadBalance = new LocalFirstLoadBalance();

    @Test
    public void testSelectFirstThenRoundRobin() throws Exception {
        List<URL> urls = new ArrayList<>();
        urls.add(new URLImpl("http", "127.0.0.10", 8081, "v1", new HashMap<String, String>()));
        urls.add(new URLImpl("http", "127.0.0.10", 8082, "v1", new HashMap<String, String>()));
        urls.add(new URLImpl("http", "127.0.0.10", 8083, "v1", new HashMap<String, String>()));
        urls.add(new URLImpl("http", "127.0.0.10", 8084, "v1", new HashMap<String, String>()));
        // no local host URL available, go round-robin
        URL url = loadBalance.select(urls, null);
        Assert.assertTrue(urls.contains(url));
    }

    @Test
    public void testSelectWithEmptyList() throws Exception {
        List<URL> urls = new ArrayList<>();
        URL url = loadBalance.select(urls, null);
        Assert.assertNull(url);
    }
}

