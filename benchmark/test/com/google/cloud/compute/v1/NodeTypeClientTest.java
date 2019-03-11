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
import com.google.cloud.compute.v1.stub.NodeTypeStubSettings;
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
public class NodeTypeClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListNodeTypesMethodDescriptor, getNodeTypeMethodDescriptor, listNodeTypesMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(NodeTypeClientTest.METHOD_DESCRIPTORS, NodeTypeStubSettings.getDefaultEndpoint());

    private static NodeTypeClient client;

    private static NodeTypeSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeTypesScopedList itemsItem = NodeTypesScopedList.newBuilder().build();
        Map<String, NodeTypesScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        NodeTypeAggregatedList expectedResponse = NodeTypeAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        NodeTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        NodeTypeClient.AggregatedListNodeTypesPagedResponse pagedListResponse = NodeTypeClientTest.client.aggregatedListNodeTypes(project);
        List<NodeTypesScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = NodeTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTypeClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            NodeTypeClientTest.client.aggregatedListNodeTypes(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeTypeTest() {
        String cpuPlatform = "cpuPlatform947156266";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        Integer guestCpus = 1754126894;
        String id = "id3355";
        String kind = "kind3292052";
        Integer localSsdGb = 1281375158;
        Integer memoryMb = 1726613907;
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        NodeType expectedResponse = NodeType.newBuilder().setCpuPlatform(cpuPlatform).setCreationTimestamp(creationTimestamp).setDescription(description).setGuestCpus(guestCpus).setId(id).setKind(kind).setLocalSsdGb(localSsdGb).setMemoryMb(memoryMb).setName(name).setSelfLink(selfLink).setZone(zone.toString()).build();
        NodeTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeTypeName nodeType = ProjectZoneNodeTypeName.of("[PROJECT]", "[ZONE]", "[NODE_TYPE]");
        NodeType actualResponse = NodeTypeClientTest.client.getNodeType(nodeType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeTypeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeTypeName nodeType = ProjectZoneNodeTypeName.of("[PROJECT]", "[ZONE]", "[NODE_TYPE]");
            NodeTypeClientTest.client.getNodeType(nodeType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeType itemsElement = NodeType.newBuilder().build();
        List<NodeType> items = Arrays.asList(itemsElement);
        NodeTypeList expectedResponse = NodeTypeList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        NodeTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        NodeTypeClient.ListNodeTypesPagedResponse pagedListResponse = NodeTypeClientTest.client.listNodeTypes(zone);
        List<NodeType> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = NodeTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            NodeTypeClientTest.client.listNodeTypes(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

