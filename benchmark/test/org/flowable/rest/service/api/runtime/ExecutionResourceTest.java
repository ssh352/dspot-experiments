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
package org.flowable.rest.service.api.runtime;


import RestUrls.URL_EXECUTION;
import RestUrls.URL_PROCESS_INSTANCE;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for all REST-operations related to a single execution resource.
 *
 * @author Frederik Heremans
 */
public class ExecutionResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting a single execution.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-subprocess.bpmn20.xml" })
    public void testGetExecution() throws Exception {
        Execution processInstanceExecution = runtimeService.startProcessInstanceByKey("processOne");
        Execution subProcessExecution = runtimeService.createExecutionQuery().activityId("subProcess").singleResult();
        Assert.assertNotNull(subProcessExecution);
        Execution childExecution = runtimeService.createExecutionQuery().activityId("processTask").singleResult();
        Assert.assertNotNull(childExecution);
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, processInstanceExecution.getId())))), HttpStatus.SC_OK);
        // Check resulting parent execution
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertEquals(processInstanceExecution.getId(), responseNode.get("id").textValue());
        Assert.assertTrue(responseNode.get("activityId").isNull());
        Assert.assertFalse(responseNode.get("suspended").booleanValue());
        Assert.assertTrue(responseNode.get("parentUrl").isNull());
        Assert.assertFalse(responseNode.get("suspended").booleanValue());
        Assert.assertTrue(responseNode.get("url").asText().endsWith(RestUrls.createRelativeResourceUrl(URL_EXECUTION, processInstanceExecution.getId())));
        Assert.assertTrue(responseNode.get("processInstanceUrl").asText().endsWith(RestUrls.createRelativeResourceUrl(URL_PROCESS_INSTANCE, processInstanceExecution.getId())));
        // Check resulting child execution
        response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, childExecution.getId())))), HttpStatus.SC_OK);
        responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertEquals(childExecution.getId(), responseNode.get("id").textValue());
        Assert.assertEquals("processTask", responseNode.get("activityId").textValue());
        Assert.assertFalse(responseNode.get("suspended").booleanValue());
        Assert.assertFalse(responseNode.get("suspended").booleanValue());
        Assert.assertTrue(responseNode.get("url").asText().endsWith(RestUrls.createRelativeResourceUrl(URL_EXECUTION, childExecution.getId())));
        Assert.assertTrue(responseNode.get("parentUrl").asText().endsWith(RestUrls.createRelativeResourceUrl(URL_EXECUTION, subProcessExecution.getId())));
        Assert.assertTrue(responseNode.get("processInstanceUrl").asText().endsWith(RestUrls.createRelativeResourceUrl(URL_PROCESS_INSTANCE, processInstanceExecution.getId())));
    }

    /**
     * Test getting an unexisting execution.
     */
    @Test
    public void testGetUnexistingExecution() throws Exception {
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, "unexisting")))), HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    /**
     * Test signalling a single execution, without signal name.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-signal.bpmn20.xml" })
    public void testSignalExecution() throws Exception {
        runtimeService.startProcessInstanceByKey("processOne");
        Execution signalExecution = runtimeService.createExecutionQuery().activityId("waitState").singleResult();
        Assert.assertNotNull(signalExecution);
        Assert.assertEquals("waitState", signalExecution.getActivityId());
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "signal");
        // Signalling one causes process to move on to second signal and
        // execution is not finished yet
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, signalExecution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertEquals("anotherWaitState", responseNode.get("activityId").textValue());
        Assert.assertEquals("anotherWaitState", runtimeService.createExecutionQuery().executionId(signalExecution.getId()).singleResult().getActivityId());
        // Signalling again causes process to end
        response = executeRequest(httpPut, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);
        // Check if process is actually ended
        Assert.assertNull(runtimeService.createExecutionQuery().executionId(signalExecution.getId()).singleResult());
    }

    /**
     * Test signalling a single execution, without signal name.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-signal-event.bpmn20.xml" })
    public void testSignalEventExecution() throws Exception {
        Execution signalExecution = runtimeService.startProcessInstanceByKey("processOne");
        Assert.assertNotNull(signalExecution);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "signalEventReceived");
        requestNode.put("signalName", "unexisting");
        Execution waitingExecution = runtimeService.createExecutionQuery().activityId("waitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, waitingExecution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        closeResponse(response);
        requestNode.put("signalName", "alert");
        // Sending signal event causes the execution to end (scope-execution for
        // the catching event)
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        response = executeRequest(httpPut, HttpStatus.SC_OK);
        closeResponse(response);
        // Check if process is moved on to the other wait-state
        waitingExecution = runtimeService.createExecutionQuery().activityId("anotherWaitState").singleResult();
        Assert.assertNotNull(waitingExecution);
    }

    /**
     * Test signalling a single execution, with signal event.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-signal-event.bpmn20.xml" })
    public void testSignalEventExecutionWithvariables() throws Exception {
        Execution signalExecution = runtimeService.startProcessInstanceByKey("processOne");
        Assert.assertNotNull(signalExecution);
        ArrayNode variables = objectMapper.createArrayNode();
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "signalEventReceived");
        requestNode.put("signalName", "alert");
        requestNode.set("variables", variables);
        ObjectNode varNode = objectMapper.createObjectNode();
        variables.add(varNode);
        varNode.put("name", "myVar");
        varNode.put("value", "Variable set when signal event is received");
        Execution waitingExecution = runtimeService.createExecutionQuery().activityId("waitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        // Sending signal event causes the execution to end (scope-execution for
        // the catching event)
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, waitingExecution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
        closeResponse(response);
        // Check if process is moved on to the other wait-state
        waitingExecution = runtimeService.createExecutionQuery().activityId("anotherWaitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        Map<String, Object> vars = runtimeService.getVariables(waitingExecution.getId());
        Assert.assertEquals(1, vars.size());
        Assert.assertEquals("Variable set when signal event is received", vars.get("myVar"));
    }

    /**
     * Test signalling a single execution, without signal event and variables.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-message-event.bpmn20.xml" })
    public void testMessageEventExecution() throws Exception {
        Execution execution = runtimeService.startProcessInstanceByKey("processOne");
        Assert.assertNotNull(execution);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "messageEventReceived");
        requestNode.put("messageName", "unexisting");
        Execution waitingExecution = runtimeService.createExecutionQuery().activityId("waitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, waitingExecution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        closeResponse(response);
        requestNode.put("messageName", "paymentMessage");
        // Sending signal event causes the execution to end (scope-execution for
        // the catching event)
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        response = executeRequest(httpPut, HttpStatus.SC_OK);
        closeResponse(response);
        // Check if process is moved on to the other wait-state
        waitingExecution = runtimeService.createExecutionQuery().activityId("anotherWaitState").singleResult();
        Assert.assertNotNull(waitingExecution);
    }

    /**
     * Test messaging a single execution with variables.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-message-event.bpmn20.xml" })
    public void testMessageEventExecutionWithvariables() throws Exception {
        Execution signalExecution = runtimeService.startProcessInstanceByKey("processOne");
        Assert.assertNotNull(signalExecution);
        ArrayNode variables = objectMapper.createArrayNode();
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "messageEventReceived");
        requestNode.put("messageName", "paymentMessage");
        requestNode.set("variables", variables);
        ObjectNode varNode = objectMapper.createObjectNode();
        variables.add(varNode);
        varNode.put("name", "myVar");
        varNode.put("value", "Variable set when signal event is received");
        Execution waitingExecution = runtimeService.createExecutionQuery().activityId("waitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        // Sending signal event causes the execution to end (scope-execution for
        // the catching event)
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, waitingExecution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
        closeResponse(response);
        // Check if process is moved on to the other wait-state
        waitingExecution = runtimeService.createExecutionQuery().activityId("anotherWaitState").singleResult();
        Assert.assertNotNull(waitingExecution);
        Map<String, Object> vars = runtimeService.getVariables(waitingExecution.getId());
        Assert.assertEquals(1, vars.size());
        Assert.assertEquals("Variable set when signal event is received", vars.get("myVar"));
    }

    /**
     * Test executing an illegal action on an execution.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/runtime/ExecutionResourceTest.process-with-subprocess.bpmn20.xml" })
    public void testIllegalExecutionAction() throws Exception {
        Execution execution = runtimeService.startProcessInstanceByKey("processOne");
        Assert.assertNotNull(execution);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "badaction");
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_EXECUTION, execution.getId()))));
        httpPut.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_BAD_REQUEST);
        closeResponse(response);
    }
}

