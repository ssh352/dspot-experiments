/**
 * Copyright 2018 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.core.execution.mapper;


import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.TerminateWorkflowException;
import com.netflix.conductor.core.utils.IDGenerator;
import com.netflix.conductor.dao.MetadataDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DynamicTaskMapperTest {
    private ParametersUtils parametersUtils;

    private MetadataDAO metadataDAO;

    private DynamicTaskMapper dynamicTaskMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Test
    public void getMappedTasks() {
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("DynoTask");
        workflowTask.setDynamicTaskNameParam("dynamicTaskName");
        TaskDef taskDef = new TaskDef();
        taskDef.setName("DynoTask");
        workflowTask.setTaskDefinition(taskDef);
        Map<String, Object> taskInput = new HashMap<>();
        taskInput.put("dynamicTaskName", "DynoTask");
        Mockito.when(parametersUtils.getTaskInput(ArgumentMatchers.anyMap(), ArgumentMatchers.any(Workflow.class), ArgumentMatchers.any(TaskDef.class), ArgumentMatchers.anyString())).thenReturn(taskInput);
        String taskId = IDGenerator.generate();
        Workflow workflow = new Workflow();
        WorkflowDef workflowDef = new WorkflowDef();
        workflow.setWorkflowDefinition(workflowDef);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowInstance(workflow).withWorkflowDefinition(workflowDef).withTaskDefinition(workflowTask.getTaskDefinition()).withTaskToSchedule(workflowTask).withTaskInput(taskInput).withRetryCount(0).withTaskId(taskId).build();
        Mockito.when(metadataDAO.getTaskDef("DynoTask")).thenReturn(new TaskDef());
        List<Task> mappedTasks = dynamicTaskMapper.getMappedTasks(taskMapperContext);
        Assert.assertEquals(1, mappedTasks.size());
        Task dynamicTask = mappedTasks.get(0);
        Assert.assertEquals(taskId, dynamicTask.getTaskId());
    }

    @Test
    public void getDynamicTaskName() {
        Map<String, Object> taskInput = new HashMap<>();
        taskInput.put("dynamicTaskName", "DynoTask");
        String dynamicTaskName = dynamicTaskMapper.getDynamicTaskName(taskInput, "dynamicTaskName");
        Assert.assertEquals("DynoTask", dynamicTaskName);
    }

    @Test
    public void getDynamicTaskNameNotAvailable() {
        Map<String, Object> taskInput = new HashMap<>();
        expectedException.expect(TerminateWorkflowException.class);
        expectedException.expectMessage(String.format(("Cannot map a dynamic task based on the parameter and input. " + "Parameter= %s, input= %s"), "dynamicTaskName", taskInput));
        dynamicTaskMapper.getDynamicTaskName(taskInput, "dynamicTaskName");
    }

    @Test
    public void getDynamicTaskDefinition() {
        // Given
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("Foo");
        TaskDef taskDef = new TaskDef();
        taskDef.setName("Foo");
        workflowTask.setTaskDefinition(taskDef);
        Mockito.when(metadataDAO.getTaskDef(ArgumentMatchers.any())).thenReturn(new TaskDef());
        // when
        TaskDef dynamicTaskDefinition = dynamicTaskMapper.getDynamicTaskDefinition(workflowTask);
        Assert.assertEquals(dynamicTaskDefinition, taskDef);
    }

    @Test
    public void getDynamicTaskDefinitionNull() {
        // Given
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("Foo");
        expectedException.expect(TerminateWorkflowException.class);
        expectedException.expectMessage(String.format("Invalid task specified.  Cannot find task by name %s in the task definitions", workflowTask.getName()));
        dynamicTaskMapper.getDynamicTaskDefinition(workflowTask);
    }
}

