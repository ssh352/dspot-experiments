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
package org.activiti.engine.impl.bpmn.parser.handler;


import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.impl.bpmn.behavior.ServiceTaskDelegateExpressionActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ServiceTaskParseHandlerTest {
    @InjectMocks
    private ServiceTaskParseHandler serviceTaskParseHandler;

    @Mock
    private BpmnParse bpmnParse;

    @Mock
    private ActivityBehaviorFactory activityBehaviorFactory;

    @Test
    public void executeParseShouldUseDefaultBehaviorWhenNoInformationIsProvided() throws Exception {
        // given
        ServiceTask serviceTask = new ServiceTask();
        ServiceTaskDelegateExpressionActivityBehavior defaultBehavior = Mockito.mock(ServiceTaskDelegateExpressionActivityBehavior.class);
        BDDMockito.given(activityBehaviorFactory.createDefaultServiceTaskBehavior(serviceTask)).willReturn(defaultBehavior);
        // when
        serviceTaskParseHandler.executeParse(bpmnParse, serviceTask);
        // then
        assertThat(serviceTask.getBehavior()).isEqualTo(defaultBehavior);
    }
}
