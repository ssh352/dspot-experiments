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
package org.flowable.dmn.engine.test;


import java.util.Collections;
import java.util.List;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.dmn.api.DmnDecisionTable;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.engine.DmnEngines;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Yvo Swillens
 */
public class MixedDeploymentTest extends AbstractFlowableDmnEngineConfiguratorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml", "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void deploySingleProcessAndDecisionTable() {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("oneDecisionTaskProcess").singleResult();
            Assert.assertNotNull(processDefinition);
            Assert.assertEquals("oneDecisionTaskProcess", processDefinition.getKey());
            DmnRepositoryService dmnRepositoryService = DmnEngines.getDefaultDmnEngine().getDmnRepositoryService();
            DmnDecisionTable decisionTable = dmnRepositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision1").singleResult();
            Assert.assertNotNull(decisionTable);
            Assert.assertEquals("decision1", decisionTable.getKey());
            List<DmnDecisionTable> decisionTableList = repositoryService.getDecisionTablesForProcessDefinition(processDefinition.getId());
            Assert.assertEquals(1L, decisionTableList.size());
            Assert.assertEquals("decision1", decisionTableList.get(0).getKey());
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml", "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void testDecisionTaskExecution() {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", ((Object) (1))));
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).orderByVariableName().asc().list();
            Assert.assertEquals("inputVariable1", variables.get(0).getVariableName());
            Assert.assertEquals(1, variables.get(0).getValue());
            Assert.assertEquals("outputVariable1", variables.get(1).getVariableName());
            Assert.assertEquals("result1", variables.get(1).getValue());
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml", "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void testFailedDecisionTask() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess");
            Assert.fail("Expected DMN failure due to missing variable");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Unknown property used in expression: #{inputVariable1"));
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskNoHitsErrorProcess.bpmn20.xml", "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void testNoHitsDecisionTask() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", ((Object) (2))));
            Assert.fail("Expected Exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("did not hit any rules for the provided input"));
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskNoHitsErrorProcess.bpmn20.xml" })
    public void testDecisionNotFound() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", ((Object) (2))));
            Assert.fail("Expected Exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Decision table for key [decision1] was not found"));
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" }, tenantId = "flowable")
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenant() {
        deployDecisionAndAssertProcessExecuted();
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" }, tenantId = "someTenant")
    public void testDecisionTaskExecutionWithGlobalTenantFallback() {
        deployDecisionWithGlobalTenantFallback();
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml" })
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantDefaultBehavior() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("Process definition with key 'oneDecisionTaskProcess' and tenantId 'flowable' was not found");
        deployDecisionAndAssertProcessExecuted();
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenantFalse.bpmn20.xml" }, tenantId = "flowable")
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFalse() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Decision table for key [decision1] and tenantId [flowable] was not found");
        deployDecisionAndAssertProcessExecuted();
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenantFalse.bpmn20.xml" }, tenantId = "flowable")
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFallbackFalseWithoutDeployment() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Decision table for key [decision1] and tenantId [flowable] was not found");
        deleteAllDmnDeployments();
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource("org/flowable/dmn/engine/test/deployment/simple.dmn").tenantId("anotherTenant").deploy();
        try {
            assertDmnProcessExecuted();
        } finally {
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" }, tenantId = "flowable")
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFallbackTrueWithoutDeployment() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("No decision found for key: decision1. There was also no fall back decision table found without tenant.");
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment().addClasspathResource("org/flowable/dmn/engine/test/deployment/simple.dmn").tenantId("anotherTenant").deploy();
        try {
            assertDmnProcessExecuted();
        } finally {
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }
}

