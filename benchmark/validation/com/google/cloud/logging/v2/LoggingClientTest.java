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
package com.google.cloud.logging.v2;


import com.google.api.MonitoredResource;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.logging.v2.DeleteLogRequest;
import com.google.logging.v2.ListLogEntriesRequest;
import com.google.logging.v2.ListLogEntriesResponse;
import com.google.logging.v2.ListLogsRequest;
import com.google.logging.v2.ListLogsResponse;
import com.google.logging.v2.LogEntry;
import com.google.logging.v2.LogName;
import com.google.logging.v2.LogNames;
import com.google.logging.v2.ParentName;
import com.google.logging.v2.ParentNames;
import com.google.logging.v2.ProjectLogName;
import com.google.logging.v2.ProjectName;
import com.google.logging.v2.WriteLogEntriesRequest;
import com.google.logging.v2.WriteLogEntriesResponse;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class LoggingClientTest {
    private static MockLoggingServiceV2 mockLoggingServiceV2;

    private static MockConfigServiceV2 mockConfigServiceV2;

    private static MockMetricsServiceV2 mockMetricsServiceV2;

    private static MockServiceHelper serviceHelper;

    private LoggingClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void deleteLogTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        LoggingClientTest.mockLoggingServiceV2.addResponse(expectedResponse);
        LogName logName = ProjectLogName.of("[PROJECT]", "[LOG]");
        client.deleteLog(logName);
        List<GeneratedMessageV3> actualRequests = LoggingClientTest.mockLoggingServiceV2.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteLogRequest actualRequest = ((DeleteLogRequest) (actualRequests.get(0)));
        Assert.assertEquals(logName, LogNames.parse(actualRequest.getLogName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteLogExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LoggingClientTest.mockLoggingServiceV2.addException(exception);
        try {
            LogName logName = ProjectLogName.of("[PROJECT]", "[LOG]");
            client.deleteLog(logName);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void writeLogEntriesTest() {
        WriteLogEntriesResponse expectedResponse = WriteLogEntriesResponse.newBuilder().build();
        LoggingClientTest.mockLoggingServiceV2.addResponse(expectedResponse);
        LogName logName = ProjectLogName.of("[PROJECT]", "[LOG]");
        MonitoredResource resource = MonitoredResource.newBuilder().build();
        Map<String, String> labels = new HashMap<>();
        List<LogEntry> entries = new ArrayList<>();
        WriteLogEntriesResponse actualResponse = client.writeLogEntries(logName, resource, labels, entries);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LoggingClientTest.mockLoggingServiceV2.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        WriteLogEntriesRequest actualRequest = ((WriteLogEntriesRequest) (actualRequests.get(0)));
        Assert.assertEquals(logName, LogNames.parse(actualRequest.getLogName()));
        Assert.assertEquals(resource, actualRequest.getResource());
        Assert.assertEquals(labels, actualRequest.getLabelsMap());
        Assert.assertEquals(entries, actualRequest.getEntriesList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void writeLogEntriesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LoggingClientTest.mockLoggingServiceV2.addException(exception);
        try {
            LogName logName = ProjectLogName.of("[PROJECT]", "[LOG]");
            MonitoredResource resource = MonitoredResource.newBuilder().build();
            Map<String, String> labels = new HashMap<>();
            List<LogEntry> entries = new ArrayList<>();
            client.writeLogEntries(logName, resource, labels, entries);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listLogEntriesTest() {
        String nextPageToken = "";
        LogEntry entriesElement = LogEntry.newBuilder().build();
        List<LogEntry> entries = Arrays.asList(entriesElement);
        ListLogEntriesResponse expectedResponse = ListLogEntriesResponse.newBuilder().setNextPageToken(nextPageToken).addAllEntries(entries).build();
        LoggingClientTest.mockLoggingServiceV2.addResponse(expectedResponse);
        List<String> formattedResourceNames = new ArrayList<>();
        String filter = "filter-1274492040";
        String orderBy = "orderBy1234304744";
        LoggingClient.ListLogEntriesPagedResponse pagedListResponse = client.listLogEntries(formattedResourceNames, filter, orderBy);
        List<LogEntry> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getEntriesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = LoggingClientTest.mockLoggingServiceV2.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListLogEntriesRequest actualRequest = ((ListLogEntriesRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedResourceNames, actualRequest.getResourceNamesList());
        Assert.assertEquals(filter, actualRequest.getFilter());
        Assert.assertEquals(orderBy, actualRequest.getOrderBy());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listLogEntriesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LoggingClientTest.mockLoggingServiceV2.addException(exception);
        try {
            List<String> formattedResourceNames = new ArrayList<>();
            String filter = "filter-1274492040";
            String orderBy = "orderBy1234304744";
            client.listLogEntries(formattedResourceNames, filter, orderBy);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listLogsTest() {
        String nextPageToken = "";
        String logNamesElement = "logNamesElement-1079688374";
        List<String> logNames = Arrays.asList(logNamesElement);
        ListLogsResponse expectedResponse = ListLogsResponse.newBuilder().setNextPageToken(nextPageToken).addAllLogNames(logNames).build();
        LoggingClientTest.mockLoggingServiceV2.addResponse(expectedResponse);
        ParentName parent = ProjectName.of("[PROJECT]");
        LoggingClient.ListLogsPagedResponse pagedListResponse = client.listLogs(parent);
        List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getLogNamesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = LoggingClientTest.mockLoggingServiceV2.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListLogsRequest actualRequest = ((ListLogsRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, ParentNames.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listLogsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LoggingClientTest.mockLoggingServiceV2.addException(exception);
        try {
            ParentName parent = ProjectName.of("[PROJECT]");
            client.listLogs(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

