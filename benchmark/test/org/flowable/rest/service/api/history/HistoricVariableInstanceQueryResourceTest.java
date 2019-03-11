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
package org.flowable.rest.service.api.history;


import RestUrls.URL_HISTORIC_VARIABLE_INSTANCE_QUERY;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import org.apache.http.HttpStatus;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Test;


/**
 * Test for REST-operation related to the historic variable instance query resource.
 *
 * @author Tijs Rademakers
 */
public class HistoricVariableInstanceQueryResourceTest extends BaseSpringRestTestCase {
    /**
     * Test querying historic variable instance. POST query/historic-variable-instances
     */
    @Test
    @Deployment
    public void testQueryVariableInstances() throws Exception {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("stringVar", "Azerty");
        processVariables.put("intVar", 67890);
        processVariables.put("booleanVar", false);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.setVariableLocal(task.getId(), "taskVariable", "test");
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        String url = RestUrls.createRelativeResourceUrl(URL_HISTORIC_VARIABLE_INSTANCE_QUERY);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("variableName", "stringVar");
        assertResultsPresentInDataResponse(url, requestNode, 2, "stringVar", "Azerty");
        requestNode = objectMapper.createObjectNode();
        requestNode.put("variableName", "booleanVar");
        assertResultsPresentInDataResponse(url, requestNode, 2, "booleanVar", false);
        requestNode = objectMapper.createObjectNode();
        requestNode.put("variableName", "booleanVar2");
        assertResultsPresentInDataResponse(url, requestNode, 0, null, null);
        requestNode = objectMapper.createObjectNode();
        requestNode.put("processInstanceId", processInstance.getId());
        assertResultsPresentInDataResponse(url, requestNode, 4, "taskVariable", "test");
        requestNode = objectMapper.createObjectNode();
        requestNode.put("processInstanceId", processInstance.getId());
        requestNode.put("excludeTaskVariables", true);
        assertResultsPresentInDataResponse(url, requestNode, 3, "intVar", 67890);
        requestNode = objectMapper.createObjectNode();
        requestNode.put("processInstanceId", processInstance2.getId());
        assertResultsPresentInDataResponse(url, requestNode, 3, "stringVar", "Azerty");
        requestNode = objectMapper.createObjectNode();
        requestNode.put("taskId", task.getId());
        assertResultsPresentInDataResponse(url, requestNode, 1, "taskVariable", "test");
        requestNode = objectMapper.createObjectNode();
        requestNode.put("taskId", task.getId());
        requestNode.put("variableName", "booleanVar");
        assertResultsPresentInDataResponse(url, requestNode, 0, null, null);
        requestNode = objectMapper.createObjectNode();
        requestNode.put("variableNameLike", "%Var");
        assertResultsPresentInDataResponse(url, requestNode, 6, "stringVar", "Azerty");
        requestNode = objectMapper.createObjectNode();
        requestNode.put("variableNameLike", "%Var2");
        assertResultsPresentInDataResponse(url, requestNode, 0, null, null);
        requestNode = objectMapper.createObjectNode();
        ArrayNode variableArray = objectMapper.createArrayNode();
        ObjectNode variableNode = objectMapper.createObjectNode();
        variableArray.add(variableNode);
        requestNode.set("variables", variableArray);
        variableNode.put("name", "stringVar");
        variableNode.put("value", "Azerty");
        variableNode.put("operation", "equals");
        assertResultsPresentInDataResponse(url, requestNode, 2, "stringVar", "Azerty");
        variableNode.removeAll();
        requestNode.set("variables", variableArray);
        variableNode.put("name", "taskVariable");
        variableNode.put("value", "test");
        variableNode.put("operation", "equals");
        assertResultsPresentInDataResponse(url, requestNode, 1, "taskVariable", "test");
        variableNode.removeAll();
        requestNode.set("variables", variableArray);
        variableNode.put("name", "taskVariable");
        variableNode.put("value", "test");
        variableNode.put("operation", "notEquals");
        assertErrorResult(url, requestNode, HttpStatus.SC_BAD_REQUEST);
    }
}

