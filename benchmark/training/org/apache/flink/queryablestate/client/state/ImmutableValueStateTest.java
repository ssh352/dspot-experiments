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
package org.apache.flink.queryablestate.client.state;


import java.io.IOException;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link ImmutableValueState}.
 */
public class ImmutableValueStateTest {
    private final ValueStateDescriptor<Long> valueStateDesc = new ValueStateDescriptor("test", BasicTypeInfo.LONG_TYPE_INFO);

    private ValueState<Long> valueState;

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() throws IOException {
        long value = valueState.value();
        Assert.assertEquals(42L, value);
        valueState.update(54L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClear() throws IOException {
        long value = valueState.value();
        Assert.assertEquals(42L, value);
        valueState.clear();
    }
}

