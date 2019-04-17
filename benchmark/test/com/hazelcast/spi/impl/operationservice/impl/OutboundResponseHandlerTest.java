/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OutboundResponseHandlerTest {
    @Parameterized.Parameter
    public ByteOrder byteOrder;

    private OutboundResponseHandler handler;

    private InternalSerializationService serializationService;

    private ILogger logger = Logger.getLogger(OutboundResponseHandlerTest.class);

    private Address thatAddress;

    private EndpointManager endpointManager;

    private Connection connection;

    @Test
    public void sendResponse_whenNormalResponse() {
        NormalResponse response = new NormalResponse("foo", 10, 1, false);
        Operation op = createDummyOperation(response.getCallId());
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, response);
        // verify that the right object was send
        Assert.assertEquals(serializationService.toData(response), argument.getValue());
    }

    @Test
    public void sendResponse_whenPortable() {
        Object response = new OutboundResponseHandlerTest.PortableAddress("Sesame Street", 1);
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, response);
        // verify that the right object was send
        NormalResponse expected = new NormalResponse(response, op.getCallId(), 0, op.isUrgent());
        Assert.assertEquals(serializationService.toData(expected), argument.getValue());
    }

    @Test
    public void sendResponse_whenOrdinaryValue() {
        Object response = "foobar";
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, response);
        // verify that the right object was send
        NormalResponse expected = new NormalResponse(response, op.getCallId(), 0, op.isUrgent());
        Assert.assertEquals(serializationService.toData(expected), argument.getValue());
    }

    @Test
    public void sendResponse_whenNull() {
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, null);
        // verify that the right object was send
        NormalResponse expected = new NormalResponse(null, op.getCallId(), 0, op.isUrgent());
        Assert.assertEquals(serializationService.toData(expected), argument.getValue());
    }

    @Test
    public void sendResponse_whenTimeoutResponse() {
        CallTimeoutResponse response = new CallTimeoutResponse(10, false);
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, response);
        // verify that the right object was send
        Assert.assertEquals(serializationService.toData(response), argument.getValue());
    }

    @Test
    public void sendResponse_whenErrorResponse() {
        ErrorResponse response = new ErrorResponse(new Exception(), 10, false);
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, response);
        // verify that the right object was send
        Assert.assertEquals(serializationService.toData(response), argument.getValue());
    }

    @Test
    public void sendResponse_whenThrowable() {
        Exception exception = new Exception();
        Operation op = createDummyOperation(10);
        ArgumentCaptor<Packet> argument = ArgumentCaptor.forClass(Packet.class);
        Mockito.when(endpointManager.transmit(argument.capture(), ArgumentMatchers.eq(thatAddress))).thenReturn(true);
        // make the call
        handler.sendResponse(op, exception);
        // verify that the right object was send
        ErrorResponse expectedResponse = new ErrorResponse(exception, op.getCallId(), op.isUrgent());
        Assert.assertEquals(serializationService.toData(expectedResponse), argument.getValue());
    }

    @Test
    public void toBackupAckPacket() {
        testToBackupAckPacket(1, false);
        testToBackupAckPacket(2, true);
    }

    @Test
    public void toNormalResponsePacket_whenNormalValues() {
        testToNormalResponsePacket("foo", 1, 0, false);
        testToNormalResponsePacket("foo", 2, 0, true);
        testToNormalResponsePacket("foo", 3, 2, false);
    }

    @Test
    public void toNormalResponsePacket_whenNullValue() {
        testToNormalResponsePacket(null, 1, 2, false);
    }

    @Test
    public void toNormalResponsePacket_whenDataValue() {
        testToNormalResponsePacket(serializationService.toBytes("foobar"), 1, 2, false);
    }

    static class PortableAddress implements Portable {
        private String street;

        private int no;

        public PortableAddress() {
        }

        public PortableAddress(String street, int no) {
            this.street = street;
            this.no = no;
        }

        @Override
        public int getClassId() {
            return 2;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("no", no);
            writer.writeUTF("street", street);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            street = reader.readUTF("street");
            no = reader.readInt("no");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OutboundResponseHandlerTest.PortableAddress that = ((OutboundResponseHandlerTest.PortableAddress) (o));
            if ((no) != (that.no)) {
                return false;
            }
            if ((street) != null ? !(street.equals(that.street)) : (that.street) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = ((street) != null) ? street.hashCode() : 0;
            result = (31 * result) + (no);
            return result;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }
    }
}
