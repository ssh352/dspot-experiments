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
package org.apache.activemq.filter;


import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;


public class DestinationFilterTest extends TestCase {
    public void testPrefixFilter() throws Exception {
        DestinationFilter filter = DestinationFilter.parseFilter(new ActiveMQQueue(">"));
        TestCase.assertTrue(("Filter not parsed well: " + (filter.getClass())), (filter instanceof PrefixDestinationFilter));
        System.out.println(filter);
        TestCase.assertFalse("Filter matched wrong destination type", filter.matches(new ActiveMQTopic(">")));
    }

    public void testWildcardFilter() throws Exception {
        DestinationFilter filter = DestinationFilter.parseFilter(new ActiveMQQueue("A.*"));
        TestCase.assertTrue(("Filter not parsed well: " + (filter.getClass())), (filter instanceof WildcardDestinationFilter));
        TestCase.assertFalse("Filter matched wrong destination type", filter.matches(new ActiveMQTopic("A.B")));
    }

    public void testCompositeFilter() throws Exception {
        DestinationFilter filter = DestinationFilter.parseFilter(new ActiveMQQueue("A.B,B.C"));
        TestCase.assertTrue(("Filter not parsed well: " + (filter.getClass())), (filter instanceof CompositeDestinationFilter));
        TestCase.assertFalse("Filter matched wrong destination type", filter.matches(new ActiveMQTopic("A.B")));
    }

    public void testMatchesChild() throws Exception {
        DestinationFilter filter = DestinationFilter.parseFilter(new ActiveMQQueue("A.*.C"));
        TestCase.assertFalse("Filter matched wrong destination type", filter.matches(new ActiveMQTopic("A.B")));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A.B.C")));
        filter = DestinationFilter.parseFilter(new ActiveMQQueue("A.*"));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A.B")));
        TestCase.assertFalse("Filter did match", filter.matches(new ActiveMQQueue("A")));
    }

    public void testMatchesAny() throws Exception {
        DestinationFilter filter = DestinationFilter.parseFilter(new ActiveMQQueue("A.>.>"));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A.C")));
        TestCase.assertFalse("Filter did match", filter.matches(new ActiveMQQueue("B")));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A.B")));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A.B.C.D.E.F")));
        TestCase.assertTrue("Filter did not match", filter.matches(new ActiveMQQueue("A")));
    }
}

