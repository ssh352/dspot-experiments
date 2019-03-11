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
package org.flowable.spring.test.autodeployment;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CaseDefinitionQuery;
import org.flowable.cmmn.api.repository.CmmnDeploymentQuery;
import org.flowable.common.engine.impl.util.IoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class SpringAutoDeployTest {
    protected static final String CTX_PATH = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-context.xml";

    protected static final String CTX_NO_DROP_PATH = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-no-drop-context.xml";

    protected static final String CTX_CREATE_DROP_CLEAN_DB = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-create-drop-clean-db-context.xml";

    protected static final String CTX_DEPLOYMENT_MODE_DEFAULT = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-default-context.xml";

    protected static final String CTX_DEPLOYMENT_MODE_SINGLE_RESOURCE = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-single-resource-context.xml";

    protected static final String CTX_DEPLOYMENT_MODE_RESOURCE_PARENT_FOLDER = "org/flowable/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-resource-parent-folder-context.xml";

    protected ApplicationContext applicationContext;

    protected CmmnRepositoryService repositoryService;

    @Test
    public void testBasicActivitiSpringIntegration() {
        createAppContext("org/flowable/spring/test/autodeployment/SpringAutoDeployTest-context.xml");
        List<CaseDefinition> caseDefinitions = repositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().list();
        Set<String> caseDefinitionKeys = new HashSet<>();
        for (CaseDefinition caseDefinition : caseDefinitions) {
            caseDefinitionKeys.add(caseDefinition.getKey());
        }
        Set<String> expectedCaseDefinitionKeys = new HashSet<>();
        expectedCaseDefinitionKeys.add("myCase");
        Assert.assertEquals(expectedCaseDefinitionKeys, caseDefinitionKeys);
    }

    @Test
    public void testNoRedeploymentForSpringContainerRestart() throws Exception {
        createAppContext(SpringAutoDeployTest.CTX_PATH);
        CmmnDeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        Assert.assertEquals(1, deploymentQuery.count());
        CaseDefinitionQuery caseDefinitionQuery = repositoryService.createCaseDefinitionQuery();
        Assert.assertEquals(1, caseDefinitionQuery.count());
        // Creating a new app context with same resources doesn't lead to more deployments
        new ClassPathXmlApplicationContext(SpringAutoDeployTest.CTX_NO_DROP_PATH);
        Assert.assertEquals(1, deploymentQuery.count());
        Assert.assertEquals(1, caseDefinitionQuery.count());
    }

    // Updating the form file should lead to a new deployment when restarting the Spring container
    @Test
    public void testResourceRedeploymentAfterCaseDefinitionChange() throws Exception {
        createAppContext(SpringAutoDeployTest.CTX_PATH);
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
        destroy();
        String filePath = "org/flowable/spring/test/autodeployment/simple-case.cmmn";
        String originalCaseFileContent = IoUtil.readFileAsString(filePath);
        String updatedCaseFileContent = originalCaseFileContent.replace("Case 1", "My simple case");
        Assert.assertTrue(((updatedCaseFileContent.length()) > (originalCaseFileContent.length())));
        IoUtil.writeStringToFile(updatedCaseFileContent, filePath);
        // Classic produced/consumer problem here:
        // The file is already written in Java, but not yet completely persisted by the OS
        // Constructing the new app context reads the same file which is sometimes not yet fully written to disk
        Thread.sleep(2000);
        try {
            applicationContext = new ClassPathXmlApplicationContext(SpringAutoDeployTest.CTX_NO_DROP_PATH);
            repositoryService = ((CmmnRepositoryService) (applicationContext.getBean("cmmnRepositoryService")));
        } finally {
            // Reset file content such that future test are not seeing something funny
            IoUtil.writeStringToFile(originalCaseFileContent, filePath);
        }
        // Assertions come AFTER the file write! Otherwise the form file is
        // messed up if the assertions fail.
        Assert.assertEquals(2, repositoryService.createDeploymentQuery().count());
        Assert.assertEquals(2, repositoryService.createCaseDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithCreateDropOnCleanDb() {
        createAppContext(SpringAutoDeployTest.CTX_CREATE_DROP_CLEAN_DB);
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
        Assert.assertEquals(1, repositoryService.createCaseDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeDefault() {
        createAppContext(SpringAutoDeployTest.CTX_DEPLOYMENT_MODE_DEFAULT);
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
        Assert.assertEquals(1, repositoryService.createCaseDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeSingleResource() {
        createAppContext(SpringAutoDeployTest.CTX_DEPLOYMENT_MODE_SINGLE_RESOURCE);
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
        Assert.assertEquals(1, repositoryService.createCaseDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeResourceParentFolder() {
        createAppContext(SpringAutoDeployTest.CTX_DEPLOYMENT_MODE_RESOURCE_PARENT_FOLDER);
        Assert.assertEquals(2, repositoryService.createDeploymentQuery().count());
        Assert.assertEquals(2, repositoryService.createCaseDefinitionQuery().count());
    }
}

