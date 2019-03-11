/**
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.spring.boot.process;


import SpringBootTest.WebEnvironment;
import java.io.File;
import java.util.List;
import org.activiti.api.process.model.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class ProcessDeployedEventIT {
    private static final String CATEGORIZE_PROCESS = "categorizeProcess";

    private static final String CATEGORIZE_HUMAN_PROCESS = "categorizeHumanProcess";

    private static final String ONE_STEP_PROCESS = "OneStepProcess";

    @Autowired
    private DeployedProcessesListener listener;

    @Test
    public void shouldTriggerProcessDeployedEvents() {
        // when
        List<ProcessDefinition> deployedProcesses = listener.getDeployedProcesses();
        // then
        assertThat(deployedProcesses).extracting(ProcessDefinition::getKey).contains(ProcessDeployedEventIT.CATEGORIZE_PROCESS, ProcessDeployedEventIT.CATEGORIZE_HUMAN_PROCESS, ProcessDeployedEventIT.ONE_STEP_PROCESS);
        assertThat(listener.getProcessModelContents().get(ProcessDeployedEventIT.CATEGORIZE_PROCESS)).isNotEmpty().isXmlEqualToContentOf(new File("src/test/resources/processes/categorize-image.bpmn20.xml"));
    }
}

