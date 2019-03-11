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
import com.google.cloud.compute.v1.stub.FirewallStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class FirewallClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteFirewallMethodDescriptor, getFirewallMethodDescriptor, insertFirewallMethodDescriptor, listFirewallsMethodDescriptor, patchFirewallMethodDescriptor, updateFirewallMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(FirewallClientTest.METHOD_DESCRIPTORS, FirewallStubSettings.getDefaultEndpoint());

    private static FirewallClient client;

    private static FirewallSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteFirewallTest() {
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
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
        Operation actualResponse = FirewallClientTest.client.deleteFirewall(firewall);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteFirewallExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
            FirewallClientTest.client.deleteFirewall(firewall);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getFirewallTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String direction = "direction-962590849";
        Boolean disabled = true;
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        ProjectGlobalNetworkName network = ProjectGlobalNetworkName.of("[PROJECT]", "[NETWORK]");
        Integer priority = 1165461084;
        String selfLink = "selfLink-1691268851";
        Firewall expectedResponse = Firewall.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setDirection(direction).setDisabled(disabled).setId(id).setKind(kind).setName(name).setNetwork(network.toString()).setPriority(priority).setSelfLink(selfLink).build();
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
        Firewall actualResponse = FirewallClientTest.client.getFirewall(firewall);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getFirewallExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
            FirewallClientTest.client.getFirewall(firewall);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertFirewallTest() {
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
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        Firewall firewallResource = Firewall.newBuilder().build();
        Operation actualResponse = FirewallClientTest.client.insertFirewall(project, firewallResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertFirewallExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            Firewall firewallResource = Firewall.newBuilder().build();
            FirewallClientTest.client.insertFirewall(project, firewallResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listFirewallsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Firewall itemsElement = Firewall.newBuilder().build();
        List<Firewall> items = Arrays.asList(itemsElement);
        FirewallList expectedResponse = FirewallList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        FirewallClient.ListFirewallsPagedResponse pagedListResponse = FirewallClientTest.client.listFirewalls(project);
        List<Firewall> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listFirewallsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            FirewallClientTest.client.listFirewalls(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void patchFirewallTest() {
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
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
        Firewall firewallResource = Firewall.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = FirewallClientTest.client.patchFirewall(firewall, firewallResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void patchFirewallExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
            Firewall firewallResource = Firewall.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            FirewallClientTest.client.patchFirewall(firewall, firewallResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateFirewallTest() {
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
        FirewallClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
        Firewall firewallResource = Firewall.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = FirewallClientTest.client.updateFirewall(firewall, firewallResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = FirewallClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = FirewallClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateFirewallExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        FirewallClientTest.mockService.addException(exception);
        try {
            ProjectGlobalFirewallName firewall = ProjectGlobalFirewallName.of("[PROJECT]", "[FIREWALL]");
            Firewall firewallResource = Firewall.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            FirewallClientTest.client.updateFirewall(firewall, firewallResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

