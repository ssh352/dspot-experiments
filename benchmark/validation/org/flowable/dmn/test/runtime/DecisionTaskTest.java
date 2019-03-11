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
package org.flowable.dmn.test.runtime;


import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnRule;
import org.flowable.common.engine.api.FlowableException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author martin.grofcik
 */
public class DecisionTaskTest {
    @Rule
    public FlowableCmmnRule cmmnRule = new FlowableCmmnRule("org/flowable/cmmn/test/runtime/DecisionTaskTest.cfg.xml");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testDecisionServiceTask() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testUnknowPropertyUsedInDmn() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("DMN decision table with key decisionTable execution failed. Cause: Unknown property used in expression: #{testVar == \"test2\"}");
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testDecisionServiceTaskWithoutHitDefaultBehavior() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "noHit").start();
        assertNoResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testHitFlag.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testDoNotThrowErrorOnNoHitWithHit() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().variable("throwErrorOnNoHits", false).variable("testVar", "test2").caseDefinitionKey("myCase").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testHitFlag.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testThrowErrorOnNoHitBooleanExpression() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().variable("throwErrorOnNoHits", Boolean.FALSE).variable("testVar", "test2").caseDefinitionKey("myCase").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testHitFlag.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testThrowErrorOnNoHitWithHit() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().variable("throwErrorOnNoHits", true).variable("testVar", "test2").caseDefinitionKey("myCase").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testHitFlag.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testThrowErrorOnNoHit() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("DMN decision table with key decisionTable did not hit any rules for the provided input.");
        cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().variable("throwErrorOnNoHits", true).variable("testVar", "noHit").caseDefinitionKey("myCase").start();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testHitFlag.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testDoNotThrowErrorOnNoHit() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().variable("throwErrorOnNoHits", false).variable("testVar", "noHitValue").caseDefinitionKey("myCase").start();
        assertNoResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testExpressionReferenceKey.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testExpressionReferenceKey() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").variable("referenceKey", "decisionTable").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testExpressionReferenceKey.cmmn" })
    public void testNullReferenceKey() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Could not execute decision: no externalRef defined");
        cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").variable("referenceKey", null).start();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testExpressionReferenceKey.cmmn" })
    public void testNonStringReferenceKey() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("No decision found for key: 1 and parent deployment id");
        cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").variable("referenceKey", 1).start();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testExpressionReferenceKey.cmmn" })
    public void testNonExistingReferenceKey() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("No decision found for key: NonExistingReferenceKey and parent deployment id");
        cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").variable("referenceKey", "NonExistingReferenceKey").start();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testBlocking.cmmn", "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.dmn" })
    public void testBlocking() {
        // is blocking is not taken into the execution
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("testVar", "test2").variable("referenceKey", "decisionTable").start();
        assertResultVariable(caseInstance);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTaskFallBackToDefaultTenant.cmmn" }, tenantId = "flowable")
    public void testDecisionServiceTaskWithFallback() {
        deployDmnTableAssertCaseStarted();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTaskFallBackToDefaultTenantFalse.cmmn" }, tenantId = "flowable")
    public void testDecisionServiceTaskWithFallbackFalse() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("and tenant id: flowable. There was also no fall back decision table found without parent deployment id.");
        deployDmnTableAssertCaseStarted();
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTask.cmmn" }, tenantId = "flowable")
    public void testDecisionServiceTaskWithGlobalTenantFallback() {
        deployDmnTableWithGlobalTenantFallback("defaultFlowable");
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/DecisionTaskTest.testDecisionServiceTaskFallBackToDefaultTenantFalse.cmmn" }, tenantId = "flowable")
    public void testDecisionServiceTaskWithGlobalTenantFallbackNoDefinition() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("There was also no fall back decision table found for default tenant defaultFlowable");
        deployDmnTableWithGlobalTenantFallback("otherTenant");
    }
}

