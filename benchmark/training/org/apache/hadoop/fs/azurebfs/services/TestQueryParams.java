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
package org.apache.hadoop.fs.azurebfs.services;


import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.fs.azurebfs.oauth2.QueryParams;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test query params serialization.
 */
public class TestQueryParams {
    private static final String SEPARATOR = "&";

    private static final String[][] PARAM_ARRAY = new String[][]{ new String[]{ "K0", "V0" }, new String[]{ "K1", "V1" }, new String[]{ "K2", "V2" } };

    @Test
    public void testOneParam() {
        String key = TestQueryParams.PARAM_ARRAY[0][0];
        String value = TestQueryParams.PARAM_ARRAY[0][1];
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(key, value);
        QueryParams qp = new QueryParams();
        qp.add(key, value);
        Assert.assertEquals(((key + "=") + value), qp.serialize());
    }

    @Test
    public void testMultipleParams() {
        QueryParams qp = new QueryParams();
        for (String[] entry : TestQueryParams.PARAM_ARRAY) {
            qp.add(entry[0], entry[1]);
        }
        Map<String, String> paramMap = constructMap(qp.serialize());
        Assert.assertEquals(TestQueryParams.PARAM_ARRAY.length, paramMap.size());
        for (String[] entry : TestQueryParams.PARAM_ARRAY) {
            Assert.assertTrue(paramMap.containsKey(entry[0]));
            Assert.assertEquals(entry[1], paramMap.get(entry[0]));
        }
    }
}

