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
package org.apache.hadoop.hdfs.server.namenode;


import org.junit.Assert;
import org.junit.Test;


public class TestNameNodeResourcePolicy {
    @Test
    public void testSingleRedundantResource() {
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(1, 0, 0, 0, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(1, 0, 1, 0, 1));
    }

    @Test
    public void testSingleRequiredResource() {
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(0, 1, 0, 0, 0));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(0, 1, 0, 1, 0));
    }

    @Test
    public void testMultipleRedundantResources() {
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 0, 0, 4));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 1, 0, 4));
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 1, 0, 3));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 2, 0, 3));
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 2, 0, 2));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 3, 0, 2));
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 3, 0, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(4, 0, 4, 0, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(1, 0, 0, 0, 2));
    }

    @Test
    public void testMultipleRequiredResources() {
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(0, 3, 0, 0, 0));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(0, 3, 0, 1, 0));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(0, 3, 0, 2, 0));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(0, 3, 0, 3, 0));
    }

    @Test
    public void testRedundantWithRequiredResources() {
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 0, 0, 1));
        Assert.assertTrue(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 1, 0, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 2, 0, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 0, 1, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 1, 1, 1));
        Assert.assertFalse(TestNameNodeResourcePolicy.testResourceScenario(2, 2, 2, 1, 1));
    }
}

