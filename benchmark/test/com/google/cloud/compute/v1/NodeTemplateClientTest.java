/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.v1;


import Code.INVALID_ARGUMENT;
import com.google.api.gax.httpjson.ApiMethodDescriptor;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.compute.v1.stub.NodeTemplateStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class NodeTemplateClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListNodeTemplatesMethodDescriptor, deleteNodeTemplateMethodDescriptor, getNodeTemplateMethodDescriptor, getIamPolicyNodeTemplateMethodDescriptor, insertNodeTemplateMethodDescriptor, listNodeTemplatesMethodDescriptor, setIamPolicyNodeTemplateMethodDescriptor, testIamPermissionsNodeTemplateMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(NodeTemplateClientTest.METHOD_DESCRIPTORS, NodeTemplateStubSettings.getDefaultEndpoint());

    private static NodeTemplateClient client;

    private static NodeTemplateSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeTemplatesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeTemplatesScopedList itemsItem = NodeTemplatesScopedList.newBuilder().build();
        Map<String, NodeTemplatesScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        NodeTemplateAggregatedList expectedResponse = NodeTemplateAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        NodeTemplateClient.AggregatedListNodeTemplatesPagedResponse pagedListResponse = NodeTemplateClientTest.client.aggregatedListNodeTemplates(project);
        List<NodeTemplatesScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeTemplatesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            NodeTemplateClientTest.client.aggregatedListNodeTemplates(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodeTemplateTest() {
        String clientOperationId = "clientOperationId-239630617";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String endTime = "endTime1725551537";
        String httpErrorMessage = "httpErrorMessage1276263769";
        Integer httpErrorStatusCode = 1386087020;
        String id = "id3355";
        String insertTime = "insertTime-103148397";
        String kind = "kind3292052";
        String name = "name3373707";
        String operationType = "operationType-1432962286";
        Integer progress = 1001078227;
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone.toString()).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionNodeTemplateName nodeTemplate = ProjectRegionNodeTemplateName.of("[PROJECT]", "[REGION]", "[NODE_TEMPLATE]");
        Operation actualResponse = NodeTemplateClientTest.client.deleteNodeTemplate(nodeTemplate);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionNodeTemplateName nodeTemplate = ProjectRegionNodeTemplateName.of("[PROJECT]", "[REGION]", "[NODE_TEMPLATE]");
            NodeTemplateClientTest.client.deleteNodeTemplate(nodeTemplate);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeTemplateTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        ProjectZoneNodeTypeName nodeType = ProjectZoneNodeTypeName.of("[PROJECT]", "[ZONE]", "[NODE_TYPE]");
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        NodeTemplate expectedResponse = NodeTemplate.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setNodeType(nodeType.toString()).setRegion(region.toString()).setSelfLink(selfLink).setStatus(status).setStatusMessage(statusMessage).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionNodeTemplateName nodeTemplate = ProjectRegionNodeTemplateName.of("[PROJECT]", "[REGION]", "[NODE_TEMPLATE]");
        NodeTemplate actualResponse = NodeTemplateClientTest.client.getNodeTemplate(nodeTemplate);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionNodeTemplateName nodeTemplate = ProjectRegionNodeTemplateName.of("[PROJECT]", "[REGION]", "[NODE_TEMPLATE]");
            NodeTemplateClientTest.client.getNodeTemplate(nodeTemplate);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyNodeTemplateTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
        Policy actualResponse = NodeTemplateClientTest.client.getIamPolicyNodeTemplate(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
            NodeTemplateClientTest.client.getIamPolicyNodeTemplate(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertNodeTemplateTest() {
        String clientOperationId = "clientOperationId-239630617";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String endTime = "endTime1725551537";
        String httpErrorMessage = "httpErrorMessage1276263769";
        Integer httpErrorStatusCode = 1386087020;
        String id = "id3355";
        String insertTime = "insertTime-103148397";
        String kind = "kind3292052";
        String name = "name3373707";
        String operationType = "operationType-1432962286";
        Integer progress = 1001078227;
        ProjectRegionName region2 = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region2.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone.toString()).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        NodeTemplate nodeTemplateResource = NodeTemplate.newBuilder().build();
        Operation actualResponse = NodeTemplateClientTest.client.insertNodeTemplate(region, nodeTemplateResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            NodeTemplate nodeTemplateResource = NodeTemplate.newBuilder().build();
            NodeTemplateClientTest.client.insertNodeTemplate(region, nodeTemplateResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeTemplatesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeTemplate itemsElement = NodeTemplate.newBuilder().build();
        List<NodeTemplate> items = Arrays.asList(itemsElement);
        NodeTemplateList expectedResponse = NodeTemplateList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        NodeTemplateClient.ListNodeTemplatesPagedResponse pagedListResponse = NodeTemplateClientTest.client.listNodeTemplates(region);
        List<NodeTemplate> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeTemplatesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            NodeTemplateClientTest.client.listNodeTemplates(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyNodeTemplateTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
        RegionSetPolicyRequest regionSetPolicyRequestResource = RegionSetPolicyRequest.newBuilder().build();
        Policy actualResponse = NodeTemplateClientTest.client.setIamPolicyNodeTemplate(resource, regionSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
            RegionSetPolicyRequest regionSetPolicyRequestResource = RegionSetPolicyRequest.newBuilder().build();
            NodeTemplateClientTest.client.setIamPolicyNodeTemplate(resource, regionSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsNodeTemplateTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        NodeTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = NodeTemplateClientTest.client.testIamPermissionsNodeTemplate(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsNodeTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTemplateClientTest.mockService.addException(exception);
        try {
            ProjectRegionNodeTemplateResourceName resource = ProjectRegionNodeTemplateResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            NodeTemplateClientTest.client.testIamPermissionsNodeTemplate(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

