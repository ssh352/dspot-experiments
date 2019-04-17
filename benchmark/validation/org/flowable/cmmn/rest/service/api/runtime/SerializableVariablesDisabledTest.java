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
package org.flowable.cmmn.rest.service.api.runtime;


import CmmnRestUrls.URL_CASE_INSTANCE_VARIABLE_COLLECTION;
import CmmnRestUrls.URL_TASK_VARIABLES_COLLECTION;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.rest.service.HttpMultipartHelper;
import org.flowable.cmmn.rest.service.api.CmmnRestUrls;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.task.api.Task;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class SerializableVariablesDisabledTest {
    private CmmnRepositoryService repositoryService;

    private CmmnRuntimeService runtimeService;

    private IdmIdentityService identityService;

    private CmmnTaskService taskService;

    private String serverUrlPrefix;

    private String testUserId;

    private String testGroupId;

    @Test
    public void testCreateSingleSerializableProcessVariable() throws Exception {
        repositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn").deploy();
        CaseInstance caseInstance = runtimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        TestSerializableVariable serializable = new TestSerializableVariable();
        serializable.setSomeField("some value");
        // Serialize object to readable stream for representation
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(serializable);
        output.close();
        InputStream binaryContent = new ByteArrayInputStream(buffer.toByteArray());
        // Add name, type and scope
        Map<String, String> additionalFields = new HashMap<>();
        additionalFields.put("name", "serializableVariable");
        additionalFields.put("type", "serializable");
        // Upload a valid CMMN-file using multipart-data
        HttpPost httpPost = new HttpPost(((serverUrlPrefix) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_INSTANCE_VARIABLE_COLLECTION, caseInstance.getId()))));
        httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("value", "application/x-java-serialized-object", binaryContent, additionalFields));
        // We have serializeable object disabled, we should get a 415.
        assertResponseStatus(httpPost, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testCreateSingleSerializableTaskVariable() throws Exception {
        repositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn").deploy();
        CaseInstance caseInstance = runtimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = taskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        TestSerializableVariable serializable = new TestSerializableVariable();
        serializable.setSomeField("some value");
        // Serialize object to readable stream for representation
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(serializable);
        output.close();
        InputStream binaryContent = new ByteArrayInputStream(buffer.toByteArray());
        // Add name, type and scope
        Map<String, String> additionalFields = new HashMap<>();
        additionalFields.put("name", "serializableVariable");
        additionalFields.put("type", "serializable");
        HttpPost httpPost = new HttpPost(((serverUrlPrefix) + (CmmnRestUrls.createRelativeResourceUrl(URL_TASK_VARIABLES_COLLECTION, task.getId()))));
        httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("value", "application/x-java-serialized-object", binaryContent, additionalFields));
        // We have serializeable object disabled, we should get a 415.
        assertResponseStatus(httpPost, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }
}
