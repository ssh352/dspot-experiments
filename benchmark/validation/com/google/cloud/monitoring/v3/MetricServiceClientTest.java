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
package com.google.cloud.monitoring.v3;


import ListTimeSeriesRequest.TimeSeriesView;
import com.google.api.MetricDescriptor;
import com.google.api.MonitoredResourceDescriptor;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.monitoring.v3.CreateMetricDescriptorRequest;
import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.DeleteMetricDescriptorRequest;
import com.google.monitoring.v3.GetMetricDescriptorRequest;
import com.google.monitoring.v3.GetMonitoredResourceDescriptorRequest;
import com.google.monitoring.v3.ListMetricDescriptorsRequest;
import com.google.monitoring.v3.ListMetricDescriptorsResponse;
import com.google.monitoring.v3.ListMonitoredResourceDescriptorsRequest;
import com.google.monitoring.v3.ListMonitoredResourceDescriptorsResponse;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.ListTimeSeriesResponse;
import com.google.monitoring.v3.MetricDescriptorName;
import com.google.monitoring.v3.MonitoredResourceDescriptorName;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class MetricServiceClientTest {
    private static MockAlertPolicyService mockAlertPolicyService;

    private static MockGroupService mockGroupService;

    private static MockMetricService mockMetricService;

    private static MockNotificationChannelService mockNotificationChannelService;

    private static MockUptimeCheckService mockUptimeCheckService;

    private static MockServiceHelper serviceHelper;

    private MetricServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listMonitoredResourceDescriptorsTest() {
        String nextPageToken = "";
        MonitoredResourceDescriptor resourceDescriptorsElement = MonitoredResourceDescriptor.newBuilder().build();
        List<MonitoredResourceDescriptor> resourceDescriptors = Arrays.asList(resourceDescriptorsElement);
        ListMonitoredResourceDescriptorsResponse expectedResponse = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(nextPageToken).addAllResourceDescriptors(resourceDescriptors).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        MetricServiceClient.ListMonitoredResourceDescriptorsPagedResponse pagedListResponse = client.listMonitoredResourceDescriptors(name);
        List<MonitoredResourceDescriptor> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getResourceDescriptorsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListMonitoredResourceDescriptorsRequest actualRequest = ((ListMonitoredResourceDescriptorsRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listMonitoredResourceDescriptorsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            client.listMonitoredResourceDescriptors(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getMonitoredResourceDescriptorTest() {
        String name2 = "name2-1052831874";
        String type = "type3575610";
        String displayName = "displayName1615086568";
        String description = "description-1724546052";
        MonitoredResourceDescriptor expectedResponse = MonitoredResourceDescriptor.newBuilder().setName(name2).setType(type).setDisplayName(displayName).setDescription(description).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        MonitoredResourceDescriptorName name = MonitoredResourceDescriptorName.of("[PROJECT]", "[MONITORED_RESOURCE_DESCRIPTOR]");
        MonitoredResourceDescriptor actualResponse = client.getMonitoredResourceDescriptor(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetMonitoredResourceDescriptorRequest actualRequest = ((GetMonitoredResourceDescriptorRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, MonitoredResourceDescriptorName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getMonitoredResourceDescriptorExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            MonitoredResourceDescriptorName name = MonitoredResourceDescriptorName.of("[PROJECT]", "[MONITORED_RESOURCE_DESCRIPTOR]");
            client.getMonitoredResourceDescriptor(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listMetricDescriptorsTest() {
        String nextPageToken = "";
        MetricDescriptor metricDescriptorsElement = MetricDescriptor.newBuilder().build();
        List<MetricDescriptor> metricDescriptors = Arrays.asList(metricDescriptorsElement);
        ListMetricDescriptorsResponse expectedResponse = ListMetricDescriptorsResponse.newBuilder().setNextPageToken(nextPageToken).addAllMetricDescriptors(metricDescriptors).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        MetricServiceClient.ListMetricDescriptorsPagedResponse pagedListResponse = client.listMetricDescriptors(name);
        List<MetricDescriptor> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getMetricDescriptorsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListMetricDescriptorsRequest actualRequest = ((ListMetricDescriptorsRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listMetricDescriptorsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            client.listMetricDescriptors(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getMetricDescriptorTest() {
        String name2 = "name2-1052831874";
        String type = "type3575610";
        String unit = "unit3594628";
        String description = "description-1724546052";
        String displayName = "displayName1615086568";
        MetricDescriptor expectedResponse = MetricDescriptor.newBuilder().setName(name2).setType(type).setUnit(unit).setDescription(description).setDisplayName(displayName).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        MetricDescriptorName name = MetricDescriptorName.of("[PROJECT]", "[METRIC_DESCRIPTOR]");
        MetricDescriptor actualResponse = client.getMetricDescriptor(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetMetricDescriptorRequest actualRequest = ((GetMetricDescriptorRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, MetricDescriptorName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getMetricDescriptorExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            MetricDescriptorName name = MetricDescriptorName.of("[PROJECT]", "[METRIC_DESCRIPTOR]");
            client.getMetricDescriptor(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createMetricDescriptorTest() {
        String name2 = "name2-1052831874";
        String type = "type3575610";
        String unit = "unit3594628";
        String description = "description-1724546052";
        String displayName = "displayName1615086568";
        MetricDescriptor expectedResponse = MetricDescriptor.newBuilder().setName(name2).setType(type).setUnit(unit).setDescription(description).setDisplayName(displayName).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        MetricDescriptor metricDescriptor = MetricDescriptor.newBuilder().build();
        MetricDescriptor actualResponse = client.createMetricDescriptor(name, metricDescriptor);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateMetricDescriptorRequest actualRequest = ((CreateMetricDescriptorRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertEquals(metricDescriptor, actualRequest.getMetricDescriptor());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createMetricDescriptorExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            MetricDescriptor metricDescriptor = MetricDescriptor.newBuilder().build();
            client.createMetricDescriptor(name, metricDescriptor);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteMetricDescriptorTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        MetricDescriptorName name = MetricDescriptorName.of("[PROJECT]", "[METRIC_DESCRIPTOR]");
        client.deleteMetricDescriptor(name);
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteMetricDescriptorRequest actualRequest = ((DeleteMetricDescriptorRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, MetricDescriptorName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteMetricDescriptorExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            MetricDescriptorName name = MetricDescriptorName.of("[PROJECT]", "[METRIC_DESCRIPTOR]");
            client.deleteMetricDescriptor(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listTimeSeriesTest() {
        String nextPageToken = "";
        TimeSeries timeSeriesElement = TimeSeries.newBuilder().build();
        List<TimeSeries> timeSeries = Arrays.asList(timeSeriesElement);
        ListTimeSeriesResponse expectedResponse = ListTimeSeriesResponse.newBuilder().setNextPageToken(nextPageToken).addAllTimeSeries(timeSeries).build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        String filter = "filter-1274492040";
        TimeInterval interval = TimeInterval.newBuilder().build();
        ListTimeSeriesRequest.TimeSeriesView view = TimeSeriesView.FULL;
        MetricServiceClient.ListTimeSeriesPagedResponse pagedListResponse = client.listTimeSeries(name, filter, interval, view);
        List<TimeSeries> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getTimeSeriesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListTimeSeriesRequest actualRequest = ((ListTimeSeriesRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertEquals(filter, actualRequest.getFilter());
        Assert.assertEquals(interval, actualRequest.getInterval());
        Assert.assertEquals(view, actualRequest.getView());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listTimeSeriesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            String filter = "filter-1274492040";
            TimeInterval interval = TimeInterval.newBuilder().build();
            ListTimeSeriesRequest.TimeSeriesView view = TimeSeriesView.FULL;
            client.listTimeSeries(name, filter, interval, view);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createTimeSeriesTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        MetricServiceClientTest.mockMetricService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        List<TimeSeries> timeSeries = new ArrayList<>();
        client.createTimeSeries(name, timeSeries);
        List<GeneratedMessageV3> actualRequests = MetricServiceClientTest.mockMetricService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateTimeSeriesRequest actualRequest = ((CreateTimeSeriesRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertEquals(timeSeries, actualRequest.getTimeSeriesList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createTimeSeriesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        MetricServiceClientTest.mockMetricService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            List<TimeSeries> timeSeries = new ArrayList<>();
            client.createTimeSeries(name, timeSeries);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

