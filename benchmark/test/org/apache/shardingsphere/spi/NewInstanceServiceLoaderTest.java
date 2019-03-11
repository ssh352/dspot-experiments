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
package org.apache.shardingsphere.spi;


import java.util.Collection;
import org.apache.shardingsphere.core.spi.NewInstanceServiceLoader;
import org.apache.shardingsphere.spi.hook.ParsingHook;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class NewInstanceServiceLoaderTest {
    @Test
    public void assertNewServiceInstanceWhenIsNotExist() {
        NewInstanceServiceLoader.register(Collection.class);
        Collection collection = NewInstanceServiceLoader.newServiceInstances(Collection.class);
        Assert.assertTrue(collection.isEmpty());
    }

    @Test
    public void assertNewServiceInstanceWhenIsExist() {
        NewInstanceServiceLoader.register(ParsingHook.class);
        Collection collection = NewInstanceServiceLoader.newServiceInstances(ParsingHook.class);
        Assert.assertThat(collection.size(), CoreMatchers.is(1));
    }
}

