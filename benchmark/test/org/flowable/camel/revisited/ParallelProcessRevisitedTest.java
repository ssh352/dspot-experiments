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
package org.flowable.camel.revisited;


import org.apache.camel.CamelContext;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.SpringFlowableTestCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@Tag("camel")
@ContextConfiguration("classpath:generic-camel-flowable-context.xml")
public class ParallelProcessRevisitedTest extends SpringFlowableTestCase {
    @Autowired
    protected CamelContext camelContext;

    @Test
    @Deployment(resources = { "process/revisited/parallel-revisited.bpmn20.xml" })
    public void testRunProcess() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("parallelCamelProcessRevisited");
        Thread.sleep(4000);
        assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
    }
}

