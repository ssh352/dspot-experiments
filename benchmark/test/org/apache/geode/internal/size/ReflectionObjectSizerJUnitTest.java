/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.size;


import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.logging.LogService;
import org.junit.Test;
import org.mockito.Mockito;


public class ReflectionObjectSizerJUnitTest {
    @Test
    public void skipsSizingDistributedSystem() {
        Object referenceObject = Mockito.mock(InternalDistributedSystem.class);
        checkSizeDoesNotChange(referenceObject);
    }

    @Test
    public void skipsSizingClassLoader() {
        checkSizeDoesNotChange(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void skipsSizingLogger() {
        checkSizeDoesNotChange(LogService.getLogger());
    }

    private static class TestObject {
        public TestObject(final Object reference) {
            this.reference = reference;
        }

        Object reference = null;
    }
}
