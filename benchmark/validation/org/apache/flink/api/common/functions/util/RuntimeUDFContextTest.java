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
package org.apache.flink.api.common.functions.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.TaskInfo;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the {@link RuntimeUDFContext}.
 */
public class RuntimeUDFContextTest {
    private final TaskInfo taskInfo = new TaskInfo("test name", 3, 1, 3, 0);

    @Test
    public void testBroadcastVariableNotFound() {
        try {
            RuntimeUDFContext ctx = new RuntimeUDFContext(taskInfo, getClass().getClassLoader(), new ExecutionConfig(), new HashMap(), new HashMap(), new UnregisteredMetricsGroup());
            Assert.assertFalse(ctx.hasBroadcastVariable("some name"));
            try {
                ctx.getBroadcastVariable("some name");
                Assert.fail("should throw an exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
            try {
                ctx.getBroadcastVariableWithInitializer("some name", new org.apache.flink.api.common.functions.BroadcastVariableInitializer<Object, Object>() {
                    public Object initializeBroadcastVariable(Iterable<Object> data) {
                        return null;
                    }
                });
                Assert.fail("should throw an exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBroadcastVariableSimple() {
        try {
            RuntimeUDFContext ctx = new RuntimeUDFContext(taskInfo, getClass().getClassLoader(), new ExecutionConfig(), new HashMap(), new HashMap(), new UnregisteredMetricsGroup());
            ctx.setBroadcastVariable("name1", Arrays.asList(1, 2, 3, 4));
            ctx.setBroadcastVariable("name2", Arrays.asList(1.0, 2.0, 3.0, 4.0));
            Assert.assertTrue(ctx.hasBroadcastVariable("name1"));
            Assert.assertTrue(ctx.hasBroadcastVariable("name2"));
            List<Integer> list1 = ctx.getBroadcastVariable("name1");
            List<Double> list2 = ctx.getBroadcastVariable("name2");
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list1);
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list2);
            // access again
            List<Integer> list3 = ctx.getBroadcastVariable("name1");
            List<Double> list4 = ctx.getBroadcastVariable("name2");
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list3);
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list4);
            // and again ;-)
            List<Integer> list5 = ctx.getBroadcastVariable("name1");
            List<Double> list6 = ctx.getBroadcastVariable("name2");
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list5);
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list6);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBroadcastVariableWithInitializer() {
        try {
            RuntimeUDFContext ctx = new RuntimeUDFContext(taskInfo, getClass().getClassLoader(), new ExecutionConfig(), new HashMap(), new HashMap(), new UnregisteredMetricsGroup());
            ctx.setBroadcastVariable("name", Arrays.asList(1, 2, 3, 4));
            // access it the first time with an initializer
            List<Double> list = ctx.getBroadcastVariableWithInitializer("name", new RuntimeUDFContextTest.ConvertingInitializer());
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list);
            // access it the second time with an initializer (which might not get executed)
            List<Double> list2 = ctx.getBroadcastVariableWithInitializer("name", new RuntimeUDFContextTest.ConvertingInitializer());
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list2);
            // access it the third time without an initializer (should work by "chance", because the result is a list)
            List<Double> list3 = ctx.getBroadcastVariable("name");
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list3);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testResetBroadcastVariableWithInitializer() {
        try {
            RuntimeUDFContext ctx = new RuntimeUDFContext(taskInfo, getClass().getClassLoader(), new ExecutionConfig(), new HashMap(), new HashMap(), new UnregisteredMetricsGroup());
            ctx.setBroadcastVariable("name", Arrays.asList(1, 2, 3, 4));
            // access it the first time with an initializer
            List<Double> list = ctx.getBroadcastVariableWithInitializer("name", new RuntimeUDFContextTest.ConvertingInitializer());
            Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), list);
            // set it again to something different
            ctx.setBroadcastVariable("name", Arrays.asList(2, 3, 4, 5));
            List<Double> list2 = ctx.getBroadcastVariableWithInitializer("name", new RuntimeUDFContextTest.ConvertingInitializer());
            Assert.assertEquals(Arrays.asList(2.0, 3.0, 4.0, 5.0), list2);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBroadcastVariableWithInitializerAndMismatch() {
        try {
            RuntimeUDFContext ctx = new RuntimeUDFContext(taskInfo, getClass().getClassLoader(), new ExecutionConfig(), new HashMap(), new HashMap(), new UnregisteredMetricsGroup());
            ctx.setBroadcastVariable("name", Arrays.asList(1, 2, 3, 4));
            // access it the first time with an initializer
            int sum = ctx.getBroadcastVariableWithInitializer("name", new RuntimeUDFContextTest.SumInitializer());
            Assert.assertEquals(10, sum);
            // access it the second time with no initializer -> should fail due to type mismatch
            try {
                ctx.getBroadcastVariable("name");
                Assert.fail("should throw an exception");
            } catch (IllegalStateException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    private static final class ConvertingInitializer implements org.apache.flink.api.common.functions.BroadcastVariableInitializer<Integer, List<Double>> {
        @Override
        public List<Double> initializeBroadcastVariable(Iterable<Integer> data) {
            List<Double> list = new ArrayList<>();
            for (Integer i : data) {
                list.add(i.doubleValue());
            }
            return list;
        }
    }

    private static final class SumInitializer implements org.apache.flink.api.common.functions.BroadcastVariableInitializer<Integer, Integer> {
        @Override
        public Integer initializeBroadcastVariable(Iterable<Integer> data) {
            int sum = 0;
            for (Integer i : data) {
                sum += i;
            }
            return sum;
        }
    }
}

