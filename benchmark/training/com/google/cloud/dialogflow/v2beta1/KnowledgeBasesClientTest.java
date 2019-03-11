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
package com.google.cloud.dialogflow.v2beta1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class KnowledgeBasesClientTest {
    private static MockAgents mockAgents;

    private static MockContexts mockContexts;

    private static MockDocuments mockDocuments;

    private static MockEntityTypes mockEntityTypes;

    private static MockIntents mockIntents;

    private static MockKnowledgeBases mockKnowledgeBases;

    private static MockSessionEntityTypes mockSessionEntityTypes;

    private static MockSessions mockSessions;

    private static MockServiceHelper serviceHelper;

    private KnowledgeBasesClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listKnowledgeBasesTest() {
        String nextPageToken = "";
        KnowledgeBase knowledgeBasesElement = KnowledgeBase.newBuilder().build();
        List<KnowledgeBase> knowledgeBases = Arrays.asList(knowledgeBasesElement);
        ListKnowledgeBasesResponse expectedResponse = ListKnowledgeBasesResponse.newBuilder().setNextPageToken(nextPageToken).addAllKnowledgeBases(knowledgeBases).build();
        KnowledgeBasesClientTest.mockKnowledgeBases.addResponse(expectedResponse);
        ProjectName parent = ProjectName.of("[PROJECT]");
        KnowledgeBasesClient.ListKnowledgeBasesPagedResponse pagedListResponse = client.listKnowledgeBases(parent);
        List<KnowledgeBase> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getKnowledgeBasesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = KnowledgeBasesClientTest.mockKnowledgeBases.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListKnowledgeBasesRequest actualRequest = ((ListKnowledgeBasesRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, ProjectName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listKnowledgeBasesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KnowledgeBasesClientTest.mockKnowledgeBases.addException(exception);
        try {
            ProjectName parent = ProjectName.of("[PROJECT]");
            client.listKnowledgeBases(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getKnowledgeBaseTest() {
        KnowledgeBaseName name2 = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        String displayName = "displayName1615086568";
        KnowledgeBase expectedResponse = KnowledgeBase.newBuilder().setName(name2.toString()).setDisplayName(displayName).build();
        KnowledgeBasesClientTest.mockKnowledgeBases.addResponse(expectedResponse);
        KnowledgeBaseName name = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        KnowledgeBase actualResponse = client.getKnowledgeBase(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KnowledgeBasesClientTest.mockKnowledgeBases.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetKnowledgeBaseRequest actualRequest = ((GetKnowledgeBaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, KnowledgeBaseName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getKnowledgeBaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KnowledgeBasesClientTest.mockKnowledgeBases.addException(exception);
        try {
            KnowledgeBaseName name = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
            client.getKnowledgeBase(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createKnowledgeBaseTest() {
        KnowledgeBaseName name = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        String displayName = "displayName1615086568";
        KnowledgeBase expectedResponse = KnowledgeBase.newBuilder().setName(name.toString()).setDisplayName(displayName).build();
        KnowledgeBasesClientTest.mockKnowledgeBases.addResponse(expectedResponse);
        ProjectName parent = ProjectName.of("[PROJECT]");
        KnowledgeBase knowledgeBase = KnowledgeBase.newBuilder().build();
        KnowledgeBase actualResponse = client.createKnowledgeBase(parent, knowledgeBase);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KnowledgeBasesClientTest.mockKnowledgeBases.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateKnowledgeBaseRequest actualRequest = ((CreateKnowledgeBaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, ProjectName.parse(actualRequest.getParent()));
        Assert.assertEquals(knowledgeBase, actualRequest.getKnowledgeBase());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createKnowledgeBaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KnowledgeBasesClientTest.mockKnowledgeBases.addException(exception);
        try {
            ProjectName parent = ProjectName.of("[PROJECT]");
            KnowledgeBase knowledgeBase = KnowledgeBase.newBuilder().build();
            client.createKnowledgeBase(parent, knowledgeBase);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteKnowledgeBaseTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        KnowledgeBasesClientTest.mockKnowledgeBases.addResponse(expectedResponse);
        KnowledgeBaseName name = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        client.deleteKnowledgeBase(name);
        List<GeneratedMessageV3> actualRequests = KnowledgeBasesClientTest.mockKnowledgeBases.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteKnowledgeBaseRequest actualRequest = ((DeleteKnowledgeBaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, KnowledgeBaseName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteKnowledgeBaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KnowledgeBasesClientTest.mockKnowledgeBases.addException(exception);
        try {
            KnowledgeBaseName name = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
            client.deleteKnowledgeBase(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

