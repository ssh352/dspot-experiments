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
package org.flowable.engine.test.db;


import HistoryLevel.ACTIVITY;
import java.util.List;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.cmd.SetProcessDefinitionVersionCmd;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Falko Menge
 */
public class ProcessInstanceMigrationTest extends PluggableFlowableTestCase {
    private static final String TEST_PROCESS_WITH_PARALLEL_GATEWAY = "org/flowable/examples/bpmn/gateway/ParallelGatewayTest.testForkJoin.bpmn20.xml";

    private static final String TEST_PROCESS = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.testSetProcessDefinitionVersion.bpmn20.xml";

    private static final String TEST_PROCESS_ACTIVITY_MISSING = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.testSetProcessDefinitionVersionActivityMissing.bpmn20.xml";

    private static final String TEST_PROCESS_CALL_ACTIVITY = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.withCallActivity.bpmn20.xml";

    private static final String TEST_PROCESS_USER_TASK_V1 = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.testSetProcessDefinitionVersionWithTask.bpmn20.xml";

    private static final String TEST_PROCESS_USER_TASK_V2 = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.testSetProcessDefinitionVersionWithTaskV2.bpmn20.xml";

    private static final String TEST_PROCESS_NESTED_SUB_EXECUTIONS = "org/flowable/engine/test/db/ProcessInstanceMigrationTest.testSetProcessDefinitionVersionSubExecutionsNested.bpmn20.xml";

    @Test
    public void testSetProcessDefinitionVersionEmptyArguments() {
        try {
            new SetProcessDefinitionVersionCmd(null, 23);
            fail("ActivitiException expected");
        } catch (FlowableIllegalArgumentException ae) {
            assertTextPresent("The process instance id is mandatory, but 'null' has been provided.", ae.getMessage());
        }
        try {
            new SetProcessDefinitionVersionCmd("", 23);
            fail("ActivitiException expected");
        } catch (FlowableIllegalArgumentException ae) {
            assertTextPresent("The process instance id is mandatory, but '' has been provided.", ae.getMessage());
        }
        try {
            new SetProcessDefinitionVersionCmd("42", null);
            fail("ActivitiException expected");
        } catch (FlowableIllegalArgumentException ae) {
            assertTextPresent("The process definition version is mandatory, but 'null' has been provided.", ae.getMessage());
        }
        try {
            new SetProcessDefinitionVersionCmd("42", (-1));
            fail("ActivitiException expected");
        } catch (FlowableIllegalArgumentException ae) {
            assertTextPresent("The process definition version must be positive, but '-1' has been provided.", ae.getMessage());
        }
    }

    @Test
    public void testSetProcessDefinitionVersionNonExistingPI() {
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        try {
            commandExecutor.execute(new SetProcessDefinitionVersionCmd("42", 23));
            fail("ActivitiException expected");
        } catch (FlowableObjectNotFoundException ae) {
            assertTextPresent("No process instance found for id = '42'.", ae.getMessage());
            assertEquals(ProcessInstance.class, ae.getObjectClass());
        }
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS_WITH_PARALLEL_GATEWAY })
    public void testSetProcessDefinitionVersionPIIsSubExecution() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("forkJoin");
        Execution execution = runtimeService.createExecutionQuery().activityId("receivePayment").singleResult();
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        SetProcessDefinitionVersionCmd command = new SetProcessDefinitionVersionCmd(execution.getId(), 1);
        try {
            commandExecutor.execute(command);
            fail("ActivitiException expected");
        } catch (FlowableException ae) {
            assertTextPresent((((((("A process instance id is required, but the provided id '" + (execution.getId())) + "' points to a child execution of process instance '") + (pi.getId())) + "'. Please invoke the ") + (command.getClass().getSimpleName())) + " with a root execution id."), ae.getMessage());
        }
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS })
    public void testSetProcessDefinitionVersionNonExistingPD() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("receiveTask");
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        try {
            commandExecutor.execute(new SetProcessDefinitionVersionCmd(pi.getId(), 23));
            fail("ActivitiException expected");
        } catch (FlowableObjectNotFoundException ae) {
            assertTextPresent("no processes deployed with key = 'receiveTask' and version = '23'", ae.getMessage());
            assertEquals(ProcessDefinition.class, ae.getObjectClass());
        }
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS })
    public void testSetProcessDefinitionVersionActivityMissing() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("receiveTask");
        // check that receive task has been reached
        Execution execution = runtimeService.createExecutionQuery().activityId("waitState1").singleResult();
        assertNotNull(execution);
        // deploy new version of the process definition
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS_ACTIVITY_MISSING).deploy();
        assertEquals(2, repositoryService.createProcessDefinitionQuery().count());
        // migrate process instance to new process definition version
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        SetProcessDefinitionVersionCmd setProcessDefinitionVersionCmd = new SetProcessDefinitionVersionCmd(pi.getId(), 2);
        try {
            commandExecutor.execute(setProcessDefinitionVersionCmd);
            fail("ActivitiException expected");
        } catch (FlowableException ae) {
            assertTextPresent("The new process definition (key = 'receiveTask') does not contain the current activity (id = 'waitState1') of the process instance (id = '", ae.getMessage());
        }
        // undeploy "manually" deployed process definition
        repositoryService.deleteDeployment(deployment.getId(), true);
    }

    @Test
    @Deployment
    public void testSetProcessDefinitionVersion() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("receiveTask");
        // check that receive task has been reached
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).activityId("waitState1").singleResult();
        assertNotNull(execution);
        // deploy new version of the process definition
        repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS).deploy();
        assertEquals(2, repositoryService.createProcessDefinitionQuery().count());
        // migrate process instance to new process definition version
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        commandExecutor.execute(new SetProcessDefinitionVersionCmd(pi.getId(), 2));
        // signal process instance
        runtimeService.trigger(execution.getId());
        // check that the instance now uses the new process definition version
        ProcessDefinition newProcessDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionVersion(2).singleResult();
        pi = runtimeService.createProcessInstanceQuery().processInstanceId(pi.getId()).singleResult();
        assertEquals(newProcessDefinition.getId(), pi.getProcessDefinitionId());
        // check history
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            HistoricProcessInstance historicPI = historyService.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).singleResult();
            assertEquals(newProcessDefinition.getId(), historicPI.getProcessDefinitionId());
            List<HistoricActivityInstance> historicActivities = historyService.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).unfinished().list();
            assertEquals(1, historicActivities.size());
            assertEquals(newProcessDefinition.getId(), getProcessDefinitionId());
        }
        deleteDeployments();
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS_WITH_PARALLEL_GATEWAY })
    public void testSetProcessDefinitionVersionSubExecutions() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("forkJoin");
        // check that the user tasks have been reached
        assertEquals(2, taskService.createTaskQuery().count());
        // deploy new version of the process definition
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS_WITH_PARALLEL_GATEWAY).deploy();
        assertEquals(2, repositoryService.createProcessDefinitionQuery().count());
        // migrate process instance to new process definition version
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        commandExecutor.execute(new SetProcessDefinitionVersionCmd(pi.getId(), 2));
        // check that all executions of the instance now use the new process
        // definition version
        ProcessDefinition newProcessDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionVersion(2).singleResult();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).list();
        for (Execution execution : executions) {
            assertEquals(newProcessDefinition.getId(), getProcessDefinitionId());
        }
        // undeploy "manually" deployed process definition
        repositoryService.deleteDeployment(deployment.getId(), true);
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS_CALL_ACTIVITY })
    public void testSetProcessDefinitionVersionWithCallActivity() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("parentProcess");
        // check that receive task has been reached
        Execution execution = runtimeService.createExecutionQuery().activityId("waitState1").processDefinitionKey("childProcess").singleResult();
        assertNotNull(execution);
        // deploy new version of the process definition
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS_CALL_ACTIVITY).deploy();
        assertEquals(2, repositoryService.createProcessDefinitionQuery().processDefinitionKey("parentProcess").count());
        // migrate process instance to new process definition version
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        commandExecutor.execute(new SetProcessDefinitionVersionCmd(pi.getId(), 2));
        // signal process instance
        runtimeService.trigger(execution.getId());
        // should be finished now
        assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(pi.getId()).count());
        // undeploy "manually" deployed process definition
        repositoryService.deleteDeployment(deployment.getId(), true);
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS_USER_TASK_V1 })
    public void testSetProcessDefinitionVersionWithWithTask() {
        try {
            // start process instance
            ProcessInstance pi = runtimeService.startProcessInstanceByKey("userTask");
            // check that user task has been reached
            assertEquals(1, taskService.createTaskQuery().processInstanceId(pi.getId()).count());
            // deploy new version of the process definition
            repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS_USER_TASK_V2).deploy();
            assertEquals(2, repositoryService.createProcessDefinitionQuery().processDefinitionKey("userTask").count());
            ProcessDefinition newProcessDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("userTask").processDefinitionVersion(2).singleResult();
            // migrate process instance to new process definition version
            processEngineConfiguration.getCommandExecutor().execute(new SetProcessDefinitionVersionCmd(pi.getId(), 2));
            // check UserTask
            Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
            assertEquals(newProcessDefinition.getId(), task.getProcessDefinitionId());
            assertEquals("testFormKey", formService.getTaskFormData(task.getId()).getFormKey());
            if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
                HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().processInstanceId(pi.getId()).singleResult();
                assertEquals(newProcessDefinition.getId(), historicTask.getProcessDefinitionId());
                assertEquals("testFormKey", formService.getTaskFormData(historicTask.getId()).getFormKey());
            }
            // continue
            taskService.complete(task.getId());
            assertProcessEnded(pi.getId());
            deleteDeployments();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Deployment(resources = { ProcessInstanceMigrationTest.TEST_PROCESS_NESTED_SUB_EXECUTIONS })
    public void testSetProcessDefinitionVersionSubExecutionsNested() {
        // start process instance
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("forkJoinNested");
        // check that the user tasks have been reached
        assertEquals(2, taskService.createTaskQuery().count());
        // deploy new version of the process definition
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource(ProcessInstanceMigrationTest.TEST_PROCESS_NESTED_SUB_EXECUTIONS).deploy();
        assertEquals(2, repositoryService.createProcessDefinitionQuery().count());
        // migrate process instance to new process definition version
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        commandExecutor.execute(new SetProcessDefinitionVersionCmd(pi.getId(), 2));
        // check that all executions of the instance now use the new process
        // definition version
        ProcessDefinition newProcessDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionVersion(2).singleResult();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).list();
        for (Execution execution : executions) {
            assertEquals(newProcessDefinition.getId(), getProcessDefinitionId());
        }
        // undeploy "manually" deployed process definition
        repositoryService.deleteDeployment(deployment.getId(), true);
    }
}

