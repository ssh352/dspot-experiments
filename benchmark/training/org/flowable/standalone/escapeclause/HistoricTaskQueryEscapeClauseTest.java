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
package org.flowable.standalone.escapeclause;


import HistoryLevel.ACTIVITY;
import java.util.ArrayList;
import java.util.List;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;


public class HistoricTaskQueryEscapeClauseTest extends AbstractEscapeClauseTestCase {
    private String deploymentOneId;

    private String deploymentTwoId;

    private ProcessInstance processInstance1;

    private ProcessInstance processInstance2;

    private Task task1;

    private Task task2;

    private Task task3;

    private Task task4;

    @Test
    public void testQueryByProcessDefinitionKeyLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // processDefinitionKeyLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionKeyLike("%\\%%").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().processDefinitionKeyLike("%\\_%").list();
            assertEquals(0, list.size());
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().processDefinitionKeyLike("%\\%%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().or().processDefinitionKeyLike("%\\_%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
        }
    }

    @Test
    public void testQueryByProcessDefinitionKeyLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // processDefinitionKeyLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionKeyLikeIgnoreCase("%\\%%").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().processDefinitionKeyLikeIgnoreCase("%\\_%").list();
            assertEquals(0, list.size());
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().processDefinitionKeyLikeIgnoreCase("%\\%%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().or().processDefinitionKeyLikeIgnoreCase("%\\_%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
        }
    }

    @Test
    public void testQueryByProcessDefinitionNameLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // processDefinitionNameLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionNameLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(4, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(4);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            tasks.add(list.get(2).getId());
            tasks.add(list.get(3).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().processDefinitionNameLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(4, list.size());
            tasks = new ArrayList<>(4);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            tasks.add(list.get(2).getId());
            tasks.add(list.get(3).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByProcessInstanceBusinessKeyLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // processInstanceBusinessKeyLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKeyLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKeyLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().processInstanceBusinessKeyLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().processInstanceBusinessKeyLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByProcessInstanceBusinessKeyLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // processInstanceBusinessKeyLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKeyLikeIgnoreCase("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKeyLikeIgnoreCase("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().processInstanceBusinessKeyLikeIgnoreCase("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().processInstanceBusinessKeyLikeIgnoreCase("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskDefinitionKeyLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskDefinitionKeyLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskDefinitionKeyLike("%\\%%").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().taskDefinitionKeyLike("%\\_%").list();
            assertEquals(0, list.size());
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskDefinitionKeyLike("%\\%%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
            list = historyService.createHistoricTaskInstanceQuery().or().taskDefinitionKeyLike("%\\_%").processDefinitionId("undefined").list();
            assertEquals(0, list.size());
        }
    }

    @Test
    public void testQueryByTaskNameLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskNameLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskNameLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskNameLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskNameLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskNameLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskNameLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskNameLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskNameLikeIgnoreCase("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskNameLikeIgnoreCase("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskNameLikeIgnoreCase("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskNameLikeIgnoreCase("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskDescriptionLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskDescriptionLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskDescriptionLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskDescriptionLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskDescriptionLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskDescriptionLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskDescriptionLikeIgnoreCase("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskDescriptionLikeIgnoreCase("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskDescriptionLikeIgnoreCase("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskDescriptionLikeIgnoreCase("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskDeleteReasonLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // make test data
            Task task5 = taskService.newTask("task5");
            taskService.saveTask(task5);
            taskService.deleteTask(task5.getId(), "deleteReason%");
            Task task6 = taskService.newTask("task6");
            taskService.saveTask(task6);
            taskService.deleteTask(task6.getId(), "deleteReason_");
            // taskDeleteReasonLike
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().taskDeleteReasonLike("%\\%%").singleResult();
            assertNotNull(historicTask);
            assertEquals(task5.getId(), historicTask.getId());
            historicTask = historyService.createHistoricTaskInstanceQuery().taskDeleteReasonLike("%\\_%").singleResult();
            assertNotNull(historicTask);
            assertEquals(task6.getId(), historicTask.getId());
            // orQuery
            historicTask = historyService.createHistoricTaskInstanceQuery().or().taskDeleteReasonLike("%\\%%").processDefinitionId("undefined").singleResult();
            assertNotNull(historicTask);
            assertEquals(task5.getId(), historicTask.getId());
            historicTask = historyService.createHistoricTaskInstanceQuery().or().taskDeleteReasonLike("%\\_%").processDefinitionId("undefined").singleResult();
            assertNotNull(historicTask);
            assertEquals(task6.getId(), historicTask.getId());
            // clean
            historyService.deleteHistoricTaskInstance(task5.getId());
            managementService.executeCommand(( commandContext) -> {
                CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTaskLogEntriesForTaskId(task5.getId());
                return null;
            });
            historyService.deleteHistoricTaskInstance(task6.getId());
            managementService.executeCommand(( commandContext) -> {
                CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTaskLogEntriesForTaskId(task6.getId());
                return null;
            });
        }
    }

    @Test
    public void testQueryByTaskOwnerLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskOwnerLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskOwnerLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskOwnerLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskOwnerLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskOwnerLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskOwnerLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskOwnerLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskOwnerLikeIgnoreCase("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskOwnerLikeIgnoreCase("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskOwnerLikeIgnoreCase("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskOwnerLikeIgnoreCase("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskAssigneeLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskAssigneeLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskAssigneeLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskAssigneeLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTaskAssigneeLikeIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // taskAssigneeLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssigneeLikeIgnoreCase("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskAssigneeLikeIgnoreCase("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskAssigneeLikeIgnoreCase("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskAssigneeLikeIgnoreCase("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryByTenantIdLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // tenantIdLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskTenantIdLike("%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskTenantIdLike("%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskTenantIdLike("%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task2.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskTenantIdLike("%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task3.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryLikeByQueryVariableValue() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // variableValueLike
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskVariableValueLike("var1", "%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskVariableValueLike("var1", "%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskVariableValueLike("var1", "%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskVariableValueLike("var1", "%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }

    @Test
    public void testQueryLikeIgnoreCaseByQueryVariableValue() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            // variableValueLikeIgnoreCase
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskVariableValueLikeIgnoreCase("var1", "%\\%%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            // only check for existence and assume that the SQL processing has ordered the values correctly
            // see https://github.com/flowable/flowable-engine/issues/8
            List<String> tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().taskVariableValueLikeIgnoreCase("var1", "%\\_%").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
            // orQuery
            list = historyService.createHistoricTaskInstanceQuery().or().taskVariableValueLikeIgnoreCase("var1", "%\\%%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task1.getId()));
            assertTrue(tasks.contains(task3.getId()));
            list = historyService.createHistoricTaskInstanceQuery().or().taskVariableValueLikeIgnoreCase("var1", "%\\_%").processDefinitionId("undefined").orderByHistoricTaskInstanceStartTime().asc().list();
            assertEquals(2, list.size());
            tasks = new ArrayList<>(2);
            tasks.add(list.get(0).getId());
            tasks.add(list.get(1).getId());
            assertTrue(tasks.contains(task2.getId()));
            assertTrue(tasks.contains(task4.getId()));
        }
    }
}
