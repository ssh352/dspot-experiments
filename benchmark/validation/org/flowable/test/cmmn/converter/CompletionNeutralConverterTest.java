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
package org.flowable.test.cmmn.converter;


import java.util.List;
import java.util.function.Consumer;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.ExtensionElement;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.cmmn.model.PlanItemDefinition;
import org.flowable.cmmn.model.Stage;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dennis Federico
 */
public class CompletionNeutralConverterTest extends AbstractConverterTest {
    @Test
    public void completionNeutralDefinedAtPlanItem() throws Exception {
        String cmmnResource = "org/flowable/test/cmmn/converter/completionNeutralAtPlanItem.cmmn";
        Consumer<CmmnModel> modelValidator = ( cmmnModel) -> {
            Assert.assertNotNull(cmmnModel);
            Stage planModel = cmmnModel.getPrimaryCase().getPlanModel();
            List<PlanItem> planItems = planModel.getPlanItems();
            Assert.assertEquals(4, planItems.size());
            planItems.forEach(( planItem) -> {
                assertNotNull(planItem.getItemControl());
                assertNotNull(planItem.getItemControl().getCompletionNeutralRule());
                assertNotNull(planItem.getItemControl().getCompletionNeutralRule().getCondition());
                assertEquals((("${" + (planItem.getId())) + "}"), planItem.getItemControl().getCompletionNeutralRule().getCondition());
            });
            Stage stageOne = ((Stage) (cmmnModel.getPrimaryCase().getPlanModel().findPlanItemDefinitionInStageOrDownwards("stageOne")));
            List<PlanItem> planItems1 = stageOne.getPlanItems();
            Assert.assertEquals(1, planItems1.size());
            PlanItem planItem = planItems1.get(0);
            Assert.assertNotNull(planItem.getItemControl());
            Assert.assertNotNull(planItem.getItemControl().getCompletionNeutralRule());
            Assert.assertNull(planItem.getItemControl().getCompletionNeutralRule().getCondition());
            Assert.assertEquals(1, planItem.getExtensionElements().size());
            List<ExtensionElement> extensionElements = planItem.getExtensionElements().get("planItemTest");
            Assert.assertEquals(1, extensionElements.size());
            ExtensionElement extensionElement = extensionElements.get(0);
            Assert.assertEquals("planItemTest", extensionElement.getName());
            Assert.assertEquals("hello", extensionElement.getElementText());
        };
        validateModel(cmmnResource, modelValidator);
    }

    @Test
    public void completionNeutralDefinedAtPlanItemDefinition() throws Exception {
        String cmmnResource = "org/flowable/test/cmmn/converter/completionNeutralAtPlanItemDefinition.cmmn";
        Consumer<CmmnModel> modelValidator = ( cmmnModel) -> {
            Assert.assertNotNull(cmmnModel);
            Stage planModel = cmmnModel.getPrimaryCase().getPlanModel();
            List<PlanItemDefinition> planItemDefinitions = planModel.getPlanItemDefinitions();
            Assert.assertEquals(4, planItemDefinitions.size());
            planItemDefinitions.forEach(( definition) -> {
                assertNotNull(definition.getDefaultControl());
                assertNotNull(definition.getDefaultControl().getCompletionNeutralRule());
                assertNotNull(definition.getDefaultControl().getCompletionNeutralRule().getCondition());
                assertEquals((("${" + (definition.getId())) + "}"), definition.getDefaultControl().getCompletionNeutralRule().getCondition());
            });
            PlanItemDefinition planItemDef = cmmnModel.findPlanItemDefinition("taskTwo");
            Assert.assertEquals(1, planItemDef.getExtensionElements().size());
            List<ExtensionElement> extensionElements = planItemDef.getExtensionElements().get("taskTest");
            Assert.assertEquals(1, extensionElements.size());
            ExtensionElement extensionElement = extensionElements.get(0);
            Assert.assertEquals("taskTest", extensionElement.getName());
            Assert.assertEquals("hello", extensionElement.getElementText());
        };
        validateModel(cmmnResource, modelValidator);
    }
}

