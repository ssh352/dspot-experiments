package org.activiti.spring.boot.process;


import SpringBootTest.WebEnvironment;
import java.util.HashMap;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.spring.boot.RuntimeTestConfiguration;
import org.activiti.spring.boot.security.util.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProcessRuntimeEvents {
    private static final String SINGLE_TASK_PROCESS = "SingleTaskProcess";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Test
    public void shouldGetSameProcessInstanceIfForAllSequenceFlowTakenEvents() {
        // given
        securityUtil.logInAs("salaboy");
        // when
        ProcessInstance categorizeProcess = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(ProcessRuntimeEvents.SINGLE_TASK_PROCESS).withVariable("name", "peter").build());
        // then
        assertThat(RuntimeTestConfiguration.sequenceFlowTakenEvents).extracting(( event) -> event.getProcessInstanceId()).contains(categorizeProcess.getId());
    }

    @Test
    public void shouldGetSameProcessInstanceIfForAllVariableCreatedEvents() {
        // given
        securityUtil.logInAs("salaboy");
        // when
        ProcessInstance categorizeProcess = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(ProcessRuntimeEvents.SINGLE_TASK_PROCESS).withVariable("name", "peter").build());
        // then
        assertThat(RuntimeTestConfiguration.variableCreatedEventsFromProcessInstance).extracting(( event) -> event.getProcessInstanceId()).contains(categorizeProcess.getId());
    }

    @Test
    public void shouldGetJustOneVariableCreatedEvent() {
        // given
        securityUtil.logInAs("salaboy");
        // when
        ProcessInstance categorizeProcess = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(ProcessRuntimeEvents.SINGLE_TASK_PROCESS).withVariable("name", "peter").build());
        // then
        assertThat(RuntimeTestConfiguration.variableCreatedEventsFromProcessInstance).isNotEmpty().hasSize(1);
    }

    @Test
    public void shouldGetJustThreeVariableCreatedEvent() {
        // given
        securityUtil.logInAs("salaboy");
        // when
        ProcessInstance categorizeProcess = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey(ProcessRuntimeEvents.SINGLE_TASK_PROCESS).withVariables(new HashMap<String, Object>() {
            {
                put("name", "peter");
                put("surname", "peterson");
                put("age", 25);
            }
        }).build());
        // then
        assertThat(RuntimeTestConfiguration.variableCreatedEventsFromProcessInstance).isNotEmpty().hasSize(3);
    }
}

