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
package org.apache.activemq.plugin;


import org.junit.Test;


/**
 * Tests that presence of wildcard characters is correctly identified by SubQueueSelectorCacheBroker
 */
public class SubQueueSelectorCacheBrokerWildcardTest {
    @Test
    public void testSimpleWildcardEvaluation() {
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "modelInstanceId = '170' AND modelClassId LIKE 'com.whatever.something.%'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "JMSMessageId LIKE '%'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "modelClassId = 'com.whatever.something.%'");
    }

    @Test
    public void testEscapedWildcardEvaluation() {
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "foo LIKE '!_%' ESCAPE '!'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "_foo__ LIKE '!_!%' ESCAPE '!'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "_foo_ LIKE '_%' ESCAPE '.'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "JMSMessageId LIKE '%' ESCAPE '.'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "_foo_ LIKE \'\\_\\%\' ESCAPE \'\\\'");
    }

    @Test
    public void testNonWildard() {
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "type = 'UPDATE_ENTITY'");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "a_property = 1");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(false, "percentage = '100%'");
    }

    @Test
    public void testApostrophes() {
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "quote LIKE '''In G_d We Trust'''");
        SubQueueSelectorCacheBrokerWildcardTest.assertWildcard(true, "quote LIKE '''In Gd We Trust''' OR quote not like '''In G_d We Trust'''");
    }
}

