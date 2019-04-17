/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.microprofile.faulttolerance;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This test class should run in its own VM to avoid clashes with system
 * properties. Our Config provider caches system properties so removing a
 * system property does not prevent other tests to be affected by it.
 */
public class ConfigGlobalTest extends FaultToleranceTest {
    static {
        System.setProperty("Retry/maxRetries", "0");
    }

    @Test
    public void testRetryOverrideGlobal() {
        RetryBean bean = newBean(RetryBean.class);
        Assertions.assertThrows(RuntimeException.class, () -> bean.retry());// fails with override

    }
}
