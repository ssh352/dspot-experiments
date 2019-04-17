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
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;


public class HistoricVariableInstanceEscapeClauseTest extends AbstractEscapeClauseTestCase {
    private String deploymentOneId;

    private String deploymentTwoId;

    private ProcessInstance processInstance1;

    private ProcessInstance processInstance2;

    @Test
    public void testQueryByVariableNameLike() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            HistoricVariableInstance historicVariable = historyService.createHistoricVariableInstanceQuery().variableNameLike("%\\%%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance1.getId(), historicVariable.getProcessInstanceId());
            assertEquals("One%", historicVariable.getValue());
            historicVariable = historyService.createHistoricVariableInstanceQuery().variableNameLike("%\\_%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance2.getId(), historicVariable.getProcessInstanceId());
            assertEquals("Two_", historicVariable.getValue());
        }
    }

    @Test
    public void testQueryLikeByQueryVariableValue() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            HistoricVariableInstance historicVariable = historyService.createHistoricVariableInstanceQuery().variableValueLike("var%", "%\\%%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance1.getId(), historicVariable.getProcessInstanceId());
            historicVariable = historyService.createHistoricVariableInstanceQuery().variableValueLike("var_", "%\\_%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance2.getId(), historicVariable.getProcessInstanceId());
        }
    }

    @Test
    public void testQueryLikeByQueryVariableValueIgnoreCase() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(ACTIVITY, processEngineConfiguration)) {
            HistoricVariableInstance historicVariable = historyService.createHistoricVariableInstanceQuery().variableValueLikeIgnoreCase("var%", "%\\%%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance1.getId(), historicVariable.getProcessInstanceId());
            historicVariable = historyService.createHistoricVariableInstanceQuery().variableValueLikeIgnoreCase("var_", "%\\_%").singleResult();
            assertNotNull(historicVariable);
            assertEquals(processInstance2.getId(), historicVariable.getProcessInstanceId());
        }
    }
}
