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
package org.flowable.examples.bpmn.tasklistener;


import java.util.HashMap;
import java.util.Map;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 * @author Falko Menge <falko.menge@camunda.com>
 * @author Frederik Heremans
 */
public class CustomTaskAssignmentTest extends PluggableFlowableTestCase {
    @Test
    @Deployment
    public void testCandidateGroupAssignment() {
        runtimeService.startProcessInstanceByKey("customTaskAssignment");
        assertEquals(1, taskService.createTaskQuery().taskCandidateGroup("management").count());
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser("kermit").count());
        assertEquals(0, taskService.createTaskQuery().taskCandidateUser("fozzie").count());
    }

    @Test
    @Deployment
    public void testCandidateUserAssignment() {
        runtimeService.startProcessInstanceByKey("customTaskAssignment");
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser("kermit").count());
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser("fozzie").count());
        assertEquals(0, taskService.createTaskQuery().taskCandidateUser("gonzo").count());
    }

    @Test
    @Deployment
    public void testAssigneeAssignment() {
        runtimeService.startProcessInstanceByKey("setAssigneeInListener");
        assertNotNull(taskService.createTaskQuery().taskAssignee("kermit").singleResult());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("fozzie").count());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("gonzo").count());
    }

    @Test
    @Deployment
    public void testOverwriteExistingAssignments() {
        runtimeService.startProcessInstanceByKey("overrideAssigneeInListener");
        assertNotNull(taskService.createTaskQuery().taskAssignee("kermit").singleResult());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("fozzie").count());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("gonzo").count());
    }

    @Test
    @Deployment
    public void testOverwriteExistingAssignmentsFromVariable() {
        // prepare variables
        Map<String, String> assigneeMappingTable = new HashMap<>();
        assigneeMappingTable.put("fozzie", "gonzo");
        Map<String, Object> variables = new HashMap<>();
        variables.put("assigneeMappingTable", assigneeMappingTable);
        // start process instance
        runtimeService.startProcessInstanceByKey("customTaskAssignment", variables);
        // check task lists
        assertNotNull(taskService.createTaskQuery().taskAssignee("gonzo").singleResult());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("fozzie").count());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("kermit").count());
    }

    @Test
    @Deployment
    public void testReleaseTask() throws Exception {
        runtimeService.startProcessInstanceByKey("releaseTaskProcess");
        Task task = taskService.createTaskQuery().taskAssignee("fozzie").singleResult();
        assertNotNull(task);
        String taskId = task.getId();
        // Set assignee to null
        taskService.setAssignee(taskId, null);
        task = taskService.createTaskQuery().taskAssignee("fozzie").singleResult();
        assertNull(task);
        task = taskService.createTaskQuery().taskId(taskId).singleResult();
        assertNotNull(task);
        assertNull(task.getAssignee());
    }
}

