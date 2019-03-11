/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.grpc;


import Kind.FOO;
import TestService.Callback;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.grpc.test.MD5;
import org.apache.avro.grpc.test.TestError;
import org.apache.avro.grpc.test.TestRecord;
import org.apache.avro.grpc.test.TestService;
import org.apache.avro.ipc.CallFuture;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroProtocolGrpc {
    private final TestRecord record = TestRecord.newBuilder().setName("foo").setKind(FOO).setArrayOfLongs(Arrays.asList(42L, 424L, 4242L)).setHash(new MD5(new byte[]{ 4, 2, 4, 2 })).setNullableHash(null).build();

    private final String declaredErrMsg = "Declared error";

    private final String undeclaredErrMsg = "Undeclared error";

    private final TestError declaredError = TestError.newBuilder().setMessage$(declaredErrMsg).build();

    private final RuntimeException undeclaredError = new RuntimeException(undeclaredErrMsg);

    private CountDownLatch oneWayStart;

    private CountDownLatch oneWayDone;

    private AtomicInteger oneWayCount;

    private TestService stub;

    private Callback callbackStub;

    private Server server;

    private ManagedChannel channel;

    @Test
    public void testEchoRecord() throws Exception {
        TestRecord echoedRecord = stub.echo(record);
        Assert.assertEquals(record, echoedRecord);
    }

    @Test
    public void testMultipleArgsAdd() throws Exception {
        int result = stub.add(3, 5, 2);
        Assert.assertEquals(10, result);
    }

    @Test
    public void testMultipleArgsConcatenate() throws Exception {
        String val1 = "foo-bar";
        Boolean val2 = true;
        long val3 = 123321L;
        int val4 = 42;
        String result = stub.concatenate(val1, val2, val3, val4);
        Assert.assertEquals((((val1 + val2) + val3) + val4), result);
    }

    @Test
    public void testCallbackInterface() throws Exception {
        CallFuture<TestRecord> future = new CallFuture<TestRecord>();
        callbackStub.echo(record, future);
        Assert.assertEquals(record, future.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOneWayRpc() throws Exception {
        oneWayStart = new CountDownLatch(1);
        oneWayDone = new CountDownLatch(3);
        oneWayCount = new AtomicInteger();
        stub.ping();
        stub.ping();
        // client is not stalled while server is waiting for processing requests
        Assert.assertEquals(0, oneWayCount.get());
        oneWayStart.countDown();
        stub.ping();
        oneWayDone.await(1, TimeUnit.SECONDS);
        Assert.assertEquals(3, oneWayCount.get());
    }

    @Test
    public void testDeclaredError() throws Exception {
        try {
            stub.error(true);
            Assert.fail("Expected exception but none thrown");
        } catch (TestError te) {
            Assert.assertEquals(declaredErrMsg, te.getMessage$());
        }
    }

    @Test
    public void testUndeclaredError() throws Exception {
        try {
            stub.error(false);
            Assert.fail("Expected exception but none thrown");
        } catch (AvroRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains(undeclaredErrMsg));
        }
    }

    @Test
    public void testNullableResponse() throws Exception {
        setUpServerAndClient(new TestAvroProtocolGrpc.TestServiceImplBase() {
            @Override
            public String concatenate(String val1, boolean val2, long val3, int val4) throws AvroRemoteException {
                return null;
            }
        });
        String response = stub.concatenate("foo", true, 42L, 42);
        Assert.assertEquals(null, response);
    }

    @Test(expected = AvroRuntimeException.class)
    public void testGrpcConnectionError() throws Exception {
        // close the channel and initiate request
        channel.shutdownNow();
        stub.add(0, 1, 2);
    }

    @Test
    public void testRepeatedRequests() throws Exception {
        TestRecord[] echoedRecords = new TestRecord[5];
        // validate results after all requests are done
        for (int i = 0; i < 5; i++) {
            echoedRecords[i] = stub.echo(record);
        }
        for (TestRecord result : echoedRecords) {
            Assert.assertEquals(record, result);
        }
    }

    @Test
    public void testConcurrentClientAccess() throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<TestRecord>[] records = new Future[5];
        Future<Integer>[] adds = new Future[5];
        // submit requests in parallel
        for (int i = 0; i < 5; i++) {
            records[i] = es.submit(() -> stub.echo(record));
            int j = i;
            adds[i] = es.submit(() -> stub.add(j, (2 * j), (3 * j)));
        }
        // validate all results
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(record, records[i].get());
            Assert.assertEquals((6 * i), ((long) (adds[i].get())));
        }
    }

    @Test
    public void testConcurrentChannels() throws Exception {
        ManagedChannel otherChannel = ManagedChannelBuilder.forAddress("localhost", server.getPort()).usePlaintext(true).build();
        TestService otherStub = AvroGrpcClient.create(otherChannel, TestService.class);
        Future<Integer>[] adds = new Future[5];
        Future<Integer>[] otherAdds = new Future[5];
        ExecutorService es = Executors.newCachedThreadPool();
        // submit requests on clients with different channels
        for (int i = 0; i < 5; i++) {
            int j = i;
            adds[i] = es.submit(() -> stub.add(j, (j - 1), (j - 2)));
            otherAdds[i] = es.submit(() -> otherStub.add(j, (j + 1), (j + 2)));
        }
        // validate all results
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(((3 * i) - 3), ((long) (adds[i].get())));
            Assert.assertEquals(((3 * i) + 3), ((long) (otherAdds[i].get())));
        }
        otherChannel.shutdownNow();
    }

    private class TestServiceImplBase implements TestService {
        @Override
        public TestRecord echo(TestRecord record) throws AvroRemoteException {
            return record;
        }

        @Override
        public int add(int arg1, int arg2, int arg3) throws AvroRemoteException {
            return (arg1 + arg2) + arg3;
        }

        @Override
        public void error(boolean declared) throws AvroRemoteException, TestError {
            if (declared) {
                throw declaredError;
            }
            throw undeclaredError;
        }

        @Override
        public void ping() {
            try {
                oneWayStart.await();
                oneWayCount.incrementAndGet();
                oneWayDone.countDown();
            } catch (InterruptedException e) {
                Assert.fail("thread interrupted when waiting for all one-way messages");
            }
        }

        @Override
        public String concatenate(String val1, boolean val2, long val3, int val4) throws AvroRemoteException {
            return ((val1 + val2) + val3) + val4;
        }
    }
}

