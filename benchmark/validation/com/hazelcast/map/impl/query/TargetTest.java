/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.query;


import Target.TargetMode;
import Target.TargetMode.PARTITION_OWNER;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.lang.reflect.Constructor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TargetTest {
    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Test
    public void testConstructor_withInvalidPartitionId() throws Exception {
        // retrieve the wanted constructor and make it accessible
        Constructor<Target> constructor = Target.class.getDeclaredConstructor(TargetMode.class, Integer.class);
        constructor.setAccessible(true);
        // we expect an IllegalArgumentException to be thrown
        rule.expect(new RootCauseMatcher(IllegalArgumentException.class));
        constructor.newInstance(PARTITION_OWNER, null);
    }
}
