/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.router;


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import GetBlobOptions.OperationType.BlobInfo;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import NonBlockingRouter.currentOperationsCount;
import RouterErrorCode.BlobAuthorizationFailure;
import RouterErrorCode.BlobDoesNotExist;
import RouterErrorCode.OperationTimedOut;
import RouterErrorCode.UnexpectedInternalError;
import ServerErrorCode.Blob_Authorization_Failure;
import ServerErrorCode.Blob_Deleted;
import ServerErrorCode.Blob_Expired;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.No_Error;
import ServerErrorCode.Replica_Unavailable;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.ClusterMapUtils;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.ResponseHandler;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.network.NetworkClient;
import com.github.ambry.network.NetworkClientErrorCode;
import com.github.ambry.network.RequestInfo;
import com.github.ambry.network.ResponseInfo;
import com.github.ambry.protocol.GetResponse;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static RouterErrorCode.AmbryUnavailable;
import static RouterErrorCode.BlobAuthorizationFailure;
import static RouterErrorCode.BlobDeleted;
import static RouterErrorCode.BlobExpired;
import static RouterErrorCode.UnexpectedInternalError;


/**
 * Tests for {@link GetBlobInfoOperation}
 */
@RunWith(Parameterized.class)
public class GetBlobInfoOperationTest {
    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    private static final int BLOB_SIZE = 100;

    private static final int BLOB_USER_METADATA_SIZE = 10;

    private int requestParallelism = 2;

    private int successTarget = 1;

    private RouterConfig routerConfig;

    private NonBlockingRouterMetrics routerMetrics;

    private final MockClusterMap mockClusterMap;

    private final MockServerLayout mockServerLayout;

    private final int replicasCount;

    private final AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<>();

    private final ResponseHandler responseHandler;

    private final MockNetworkClientFactory networkClientFactory;

    private final NetworkClient networkClient;

    private final MockRouterCallback routerCallback;

    private final MockTime time = new MockTime();

    private final Map<Integer, GetOperation> correlationIdToGetOperation = new HashMap<>();

    private final NonBlockingRouter router;

    private final Random random = new Random();

    private final BlobId blobId;

    private final BlobProperties blobProperties;

    private final byte[] userMetadata;

    private final byte[] putContent;

    private final boolean testEncryption;

    private final String operationTrackerType;

    private final GetBlobInfoOperationTest.GetTestRequestRegistrationCallbackImpl requestRegistrationCallback = new GetBlobInfoOperationTest.GetTestRequestRegistrationCallbackImpl();

    private final GetBlobOptionsInternal options;

    private String kmsSingleKey;

    private MockKeyManagementService kms = null;

    private MockCryptoService cryptoService = null;

    private CryptoJobHandler cryptoJobHandler = null;

    private class GetTestRequestRegistrationCallbackImpl implements RequestRegistrationCallback<GetOperation> {
        private List<RequestInfo> requestListToFill;

        @Override
        public void registerRequestToSend(GetOperation getOperation, RequestInfo requestInfo) {
            requestListToFill.add(requestInfo);
            correlationIdToGetOperation.put(getCorrelationId(), getOperation);
        }
    }

    /**
     *
     *
     * @param operationTrackerType
     * 		@param operationTrackerType the type of {@link OperationTracker} to use.
     * @param testEncryption
     * 		{@code true} if blob needs to be encrypted. {@code false} otherwise
     * @throws Exception
     * 		
     */
    public GetBlobInfoOperationTest(String operationTrackerType, boolean testEncryption) throws Exception {
        this.operationTrackerType = operationTrackerType;
        this.testEncryption = testEncryption;
        VerifiableProperties vprops = new VerifiableProperties(getNonBlockingRouterProperties());
        routerConfig = new RouterConfig(vprops);
        mockClusterMap = new MockClusterMap();
        routerMetrics = new NonBlockingRouterMetrics(mockClusterMap);
        options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(BlobInfo).build(), false, routerMetrics.ageAtGet);
        mockServerLayout = new MockServerLayout(mockClusterMap);
        replicasCount = mockClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0).getReplicaIds().size();
        responseHandler = new ResponseHandler(mockClusterMap);
        networkClientFactory = new MockNetworkClientFactory(vprops, mockSelectorState, GetBlobInfoOperationTest.MAX_PORTS_PLAIN_TEXT, GetBlobInfoOperationTest.MAX_PORTS_SSL, GetBlobInfoOperationTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, time);
        if (testEncryption) {
            kmsSingleKey = TestUtils.getRandomKey(SingleKeyManagementServiceTest.DEFAULT_KEY_SIZE_CHARS);
            instantiateCryptoComponents(vprops);
        }
        router = new NonBlockingRouter(new RouterConfig(vprops), new NonBlockingRouterMetrics(mockClusterMap), networkClientFactory, new LoggingNotificationSystem(), mockClusterMap, kms, cryptoService, cryptoJobHandler, new InMemAccountService(false, true), time, MockClusterMap.DEFAULT_PARTITION_CLASS);
        short accountId = Utils.getRandomShort(random);
        short containerId = Utils.getRandomShort(random);
        blobProperties = new BlobProperties((-1), "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, accountId, containerId, testEncryption, null);
        userMetadata = new byte[GetBlobInfoOperationTest.BLOB_USER_METADATA_SIZE];
        random.nextBytes(userMetadata);
        putContent = new byte[GetBlobInfoOperationTest.BLOB_SIZE];
        random.nextBytes(putContent);
        ReadableStreamChannel putChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(putContent));
        String blobIdStr = router.putBlob(blobProperties, userMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        blobId = RouterUtils.getBlobIdFromString(blobIdStr, mockClusterMap);
        networkClient = networkClientFactory.getNetworkClient();
        router.close();
        routerCallback = new MockRouterCallback(networkClient, Collections.EMPTY_LIST);
        if (testEncryption) {
            instantiateCryptoComponents(vprops);
        }
    }

    /**
     * Test {@link GetBlobInfoOperation} instantiation and validate the get methods.
     */
    @Test
    public void testInstantiation() {
        BlobId blobId = new BlobId(routerConfig.routerBlobidCurrentVersion, BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(random), Utils.getRandomShort(random), mockClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        Callback<GetBlobResultInternal> getOperationCallback = ( result, exception) -> {
            // no op.
        };
        // test a good case
        GetBlobInfoOperation op = new GetBlobInfoOperation(routerConfig, routerMetrics, mockClusterMap, responseHandler, blobId, options, getOperationCallback, routerCallback, kms, cryptoService, cryptoJobHandler, time, false);
        Assert.assertEquals("Callback must match", getOperationCallback, op.getCallback());
        Assert.assertEquals("Blob ids must match", blobId.getID(), op.getBlobIdStr());
        // test the case where the tracker type is bad
        Properties properties = getNonBlockingRouterProperties();
        properties.setProperty("router.get.operation.tracker.type", "NonExistentTracker");
        RouterConfig badConfig = new RouterConfig(new VerifiableProperties(properties));
        try {
            new GetBlobInfoOperation(badConfig, routerMetrics, mockClusterMap, responseHandler, blobId, options, getOperationCallback, routerCallback, kms, cryptoService, cryptoJobHandler, time, false);
            Assert.fail("Instantiation of GetBlobInfoOperation with an invalid tracker type must fail");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Test basic successful operation completion, by polling and handing over responses to the BlobInfo operation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPollAndResponseHandling() throws Exception {
        currentOperationsCount.incrementAndGet();
        GetBlobInfoOperation op = new GetBlobInfoOperation(routerConfig, routerMetrics, mockClusterMap, responseHandler, blobId, options, null, routerCallback, kms, cryptoService, cryptoJobHandler, time, false);
        ArrayList<RequestInfo> requestListToFill = new ArrayList<>();
        requestRegistrationCallback.requestListToFill = requestListToFill;
        op.poll(requestRegistrationCallback);
        Assert.assertEquals("There should only be as many requests at this point as requestParallelism", requestParallelism, correlationIdToGetOperation.size());
        CountDownLatch onPollLatch = new CountDownLatch(1);
        if (testEncryption) {
            routerCallback.setOnPollLatch(onPollLatch);
        }
        List<ResponseInfo> responses = sendAndWaitForResponses(requestListToFill);
        for (ResponseInfo responseInfo : responses) {
            GetResponse getResponse = ((responseInfo.getError()) == null) ? GetResponse.readFrom(new DataInputStream(new com.github.ambry.utils.ByteBufferInputStream(responseInfo.getResponse())), mockClusterMap) : null;
            op.handleResponse(responseInfo, getResponse);
            if (op.isOperationComplete()) {
                break;
            }
        }
        if (testEncryption) {
            Assert.assertTrue("Latch should have been zeroed ", onPollLatch.await(500, TimeUnit.MILLISECONDS));
            op.poll(requestRegistrationCallback);
        }
        Assert.assertTrue("Operation should be complete at this time", op.isOperationComplete());
        assertSuccess(op);
        // poll again to make sure that counters aren't triggered again (check in @After)
        op.poll(requestRegistrationCallback);
    }

    /**
     * Test the case where all requests time out within the GetOperation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRouterRequestTimeoutAllFailure() throws Exception {
        currentOperationsCount.incrementAndGet();
        GetBlobInfoOperation op = new GetBlobInfoOperation(routerConfig, routerMetrics, mockClusterMap, responseHandler, blobId, options, null, routerCallback, kms, cryptoService, cryptoJobHandler, time, false);
        requestRegistrationCallback.requestListToFill = new ArrayList();
        op.poll(requestRegistrationCallback);
        while (!(op.isOperationComplete())) {
            time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
            op.poll(requestRegistrationCallback);
        } 
        // At this time requests would have been created for all replicas, as none of them were delivered,
        // and cross-colo proxying is enabled by default.
        Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
        Assert.assertTrue("Operation should be complete at this time", op.isOperationComplete());
        RouterException routerException = ((RouterException) (op.getOperationException()));
        Assert.assertEquals(OperationTimedOut, routerException.getErrorCode());
    }

    /**
     * Test the case where all requests time out within the NetworkClient.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNetworkClientTimeoutAllFailure() throws Exception {
        currentOperationsCount.incrementAndGet();
        GetBlobInfoOperation op = new GetBlobInfoOperation(routerConfig, routerMetrics, mockClusterMap, responseHandler, blobId, options, null, routerCallback, kms, cryptoService, cryptoJobHandler, time, false);
        ArrayList<RequestInfo> requestListToFill = new ArrayList<>();
        requestRegistrationCallback.requestListToFill = requestListToFill;
        while (!(op.isOperationComplete())) {
            op.poll(requestRegistrationCallback);
            for (RequestInfo requestInfo : requestListToFill) {
                ResponseInfo fakeResponse = new ResponseInfo(requestInfo, NetworkClientErrorCode.NetworkError, null);
                op.handleResponse(fakeResponse, null);
                if (op.isOperationComplete()) {
                    break;
                }
            }
            requestListToFill.clear();
        } 
        // At this time requests would have been created for all replicas, as none of them were delivered,
        // and cross-colo proxying is enabled by default.
        Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
        Assert.assertTrue("Operation should be complete at this time", op.isOperationComplete());
        RouterException routerException = ((RouterException) (op.getOperationException()));
        Assert.assertEquals(OperationTimedOut, routerException.getErrorCode());
    }

    /**
     * Test the case where every server returns Blob_Not_Found. All servers must have been contacted,
     * due to cross-colo proxying.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobNotFoundCase() throws Exception {
        mockServerLayout.getMockServers().forEach(( server) -> server.setServerErrorForAllRequests(Blob_Not_Found));
        assertOperationFailure(BlobDoesNotExist);
        Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
    }

    /**
     * Test the case with Blob_Not_Found errors from most servers, and Blob_Deleted, Blob_Expired or
     * Blob_Authorization_Failure at just one server. The latter should be the exception received for the operation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testErrorPrecedenceWithSpecialCase() throws Exception {
        ServerErrorCode[] serverErrorCodesToTest = new ServerErrorCode[]{ ServerErrorCode.Blob_Deleted, ServerErrorCode.Blob_Expired, ServerErrorCode.Blob_Authorization_Failure };
        RouterErrorCode[] routerErrorCodesToExpect = new RouterErrorCode[]{ BlobDeleted, BlobExpired, BlobAuthorizationFailure };
        for (int i = 0; i < (serverErrorCodesToTest.length); i++) {
            int indexToSetCustomError = random.nextInt(replicasCount);
            ServerErrorCode[] serverErrorCodesInOrder = new ServerErrorCode[9];
            for (int j = 0; j < (serverErrorCodesInOrder.length); j++) {
                if (j == indexToSetCustomError) {
                    serverErrorCodesInOrder[j] = serverErrorCodesToTest[i];
                } else {
                    serverErrorCodesInOrder[j] = ServerErrorCode.Blob_Not_Found;
                }
            }
            testErrorPrecedence(serverErrorCodesInOrder, routerErrorCodesToExpect[i]);
        }
    }

    /**
     * Test the case where servers return different {@link ServerErrorCode} or success, and the {@link GetBlobInfoOperation}
     * is able to resolve and conclude the correct {@link RouterErrorCode}. The get blob operation should be able
     * to resolve the router error code as {@code Blob_Authorization_Failure}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobAuthorizationFailureOverrideAll() throws Exception {
        successTarget = 2;// set it to 2 for more coverage

        Properties props = getNonBlockingRouterProperties();
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        serverErrorCodes[0] = ServerErrorCode.Blob_Not_Found;
        serverErrorCodes[1] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[2] = ServerErrorCode.IO_Error;
        serverErrorCodes[3] = ServerErrorCode.Partition_Unknown;
        serverErrorCodes[4] = ServerErrorCode.Disk_Unavailable;
        serverErrorCodes[5] = ServerErrorCode.Blob_Authorization_Failure;
        serverErrorCodes[6] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[7] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[8] = ServerErrorCode.No_Error;
        testErrorPrecedence(serverErrorCodes, BlobAuthorizationFailure);
    }

    /**
     * Tests the case where all servers return the same server level error code
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailureOnServerErrors() throws Exception {
        // set the status to various server level errors (remove all partition level errors or non errors)
        EnumSet<ServerErrorCode> serverErrors = EnumSet.complementOf(EnumSet.of(Blob_Deleted, Blob_Expired, No_Error, Blob_Authorization_Failure, Blob_Not_Found));
        for (ServerErrorCode serverErrorCode : serverErrors) {
            mockServerLayout.getMockServers().forEach(( server) -> server.setServerErrorForAllRequests(serverErrorCode));
            assertOperationFailure((EnumSet.of(Disk_Unavailable, Replica_Unavailable).contains(serverErrorCode) ? AmbryUnavailable : UnexpectedInternalError));
        }
    }

    /**
     * Test failure with KMS
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKMSFailure() throws Exception {
        if (testEncryption) {
            kms.exceptionToThrow.set(PutManagerTest.GSE);
            assertOperationFailure(UnexpectedInternalError);
        }
    }

    /**
     * Test failure with CryptoService
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCryptoServiceFailure() throws Exception {
        if (testEncryption) {
            cryptoService.exceptionOnDecryption.set(PutManagerTest.GSE);
            assertOperationFailure(UnexpectedInternalError);
        }
    }

    /**
     * Test the case with multiple errors (server level and partition level) from multiple servers,
     * with just one server returning a successful response. The operation should succeed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSuccessInThePresenceOfVariousErrors() throws Exception {
        // The put for the blob being requested happened.
        String dcWherePutHappened = routerConfig.routerDatacenterName;
        // test requests coming in from local dc as well as cross-colo.
        Properties props = getNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC1");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        testVariousErrors(dcWherePutHappened);
        props = getNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC2");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        testVariousErrors(dcWherePutHappened);
        props = getNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC3");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        testVariousErrors(dcWherePutHappened);
    }
}

