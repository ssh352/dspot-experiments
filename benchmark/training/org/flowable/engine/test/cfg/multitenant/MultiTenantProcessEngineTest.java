/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.cfg.multitenant;


import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class MultiTenantProcessEngineTest {
    private DummyTenantInfoHolder tenantInfoHolder;

    private MultiSchemaMultiTenantProcessEngineConfiguration config;

    private ProcessEngine processEngine;

    @Test
    public void testStartProcessInstancesWithSharedExecutor() throws Exception {
        setupProcessEngine(true);
        runProcessInstanceTest();
    }

    @Test
    public void testStartProcessInstancesWithExecutorPerTenantAsyncExecutor() throws Exception {
        setupProcessEngine(false);
        runProcessInstanceTest();
    }
}

