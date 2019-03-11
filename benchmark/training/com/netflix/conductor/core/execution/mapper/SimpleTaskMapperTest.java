package com.netflix.conductor.core.execution.mapper;


import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.TerminateWorkflowException;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SimpleTaskMapperTest {
    private ParametersUtils parametersUtils;

    private SimpleTaskMapper simpleTaskMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getMappedTasks() throws Exception {
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setName("simple_task");
        taskToSchedule.setTaskDefinition(new TaskDef("simple_task"));
        String taskId = IDGenerator.generate();
        String retriedTaskId = IDGenerator.generate();
        WorkflowDef wd = new WorkflowDef();
        Workflow w = new Workflow();
        w.setWorkflowDefinition(wd);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(wd).withWorkflowInstance(w).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withTaskInput(new HashMap()).withRetryCount(0).withRetryTaskId(retriedTaskId).withTaskId(taskId).build();
        List<Task> mappedTasks = simpleTaskMapper.getMappedTasks(taskMapperContext);
        Assert.assertNotNull(mappedTasks);
        Assert.assertEquals(1, mappedTasks.size());
    }

    @Test
    public void getMappedTasksException() throws Exception {
        // Given
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setName("simple_task");
        String taskId = IDGenerator.generate();
        String retriedTaskId = IDGenerator.generate();
        WorkflowDef wd = new WorkflowDef();
        Workflow w = new Workflow();
        w.setWorkflowDefinition(wd);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(wd).withWorkflowInstance(w).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withTaskInput(new HashMap()).withRetryCount(0).withRetryTaskId(retriedTaskId).withTaskId(taskId).build();
        // then
        expectedException.expect(TerminateWorkflowException.class);
        expectedException.expectMessage(String.format("Invalid task. Task %s does not have a definition", taskToSchedule.getName()));
        // when
        simpleTaskMapper.getMappedTasks(taskMapperContext);
    }
}

