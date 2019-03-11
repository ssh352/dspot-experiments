/**
 * Copyright (C) 2008 The Android Open Source Project
 *
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
package libcore.java.net;


import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.DatagramSocketImplFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import junit.framework.TestCase;


public class OldDatagramSocketImplFactoryTest extends TestCase {
    DatagramSocketImplFactory oldFactory = null;

    Field factoryField = null;

    boolean isTestable = false;

    boolean isDatagramSocketImplCalled = false;

    boolean isCreateDatagramSocketImpl = false;

    public void test_createDatagramSocketImpl() throws IOException, IllegalArgumentException {
        if (isTestable) {
            DatagramSocketImplFactory factory = new OldDatagramSocketImplFactoryTest.TestDatagramSocketImplFactory();
            TestCase.assertFalse(isCreateDatagramSocketImpl);
            DatagramSocket.setDatagramSocketImplFactory(factory);
            try {
                new DatagramSocket();
                TestCase.assertTrue(isCreateDatagramSocketImpl);
                TestCase.assertTrue(isDatagramSocketImplCalled);
            } catch (Exception e) {
                TestCase.fail(("Exception during test : " + (e.getMessage())));
            }
            try {
                DatagramSocket.setDatagramSocketImplFactory(factory);
                TestCase.fail("SocketException was not thrown.");
            } catch (SocketException se) {
                // expected
            }
            try {
                DatagramSocket.setDatagramSocketImplFactory(null);
                TestCase.fail("SocketException was not thrown.");
            } catch (SocketException se) {
                // expected
            }
        } else {
            OldDatagramSocketImplFactoryTest.TestDatagramSocketImplFactory dsf = new OldDatagramSocketImplFactoryTest.TestDatagramSocketImplFactory();
            DatagramSocketImpl dsi = dsf.createDatagramSocketImpl();
            try {
                TestCase.assertNull(dsi.getOption(0));
            } catch (SocketException e) {
                TestCase.fail("SocketException was thrown.");
            }
        }
    }

    class TestDatagramSocketImplFactory implements DatagramSocketImplFactory {
        public DatagramSocketImpl createDatagramSocketImpl() {
            isCreateDatagramSocketImpl = true;
            return new OldDatagramSocketImplFactoryTest.TestDatagramSocketImpl();
        }
    }

    class TestDatagramSocketImpl extends DatagramSocketImpl {
        @Override
        protected void bind(int arg0, InetAddress arg1) throws SocketException {
        }

        @Override
        protected void close() {
        }

        @Override
        protected void create() throws SocketException {
            isDatagramSocketImplCalled = true;
        }

        @Override
        protected byte getTTL() throws IOException {
            return 0;
        }

        @Override
        protected int getTimeToLive() throws IOException {
            return 0;
        }

        @Override
        protected void join(InetAddress arg0) throws IOException {
        }

        @Override
        protected void joinGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
        }

        @Override
        protected void leave(InetAddress arg0) throws IOException {
        }

        @Override
        protected void leaveGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
        }

        @Override
        public int peek(InetAddress arg0) throws IOException {
            return 10;
        }

        @Override
        protected int peekData(DatagramPacket arg0) throws IOException {
            return 0;
        }

        @Override
        protected void receive(DatagramPacket arg0) throws IOException {
        }

        @Override
        protected void send(DatagramPacket arg0) throws IOException {
        }

        @Override
        protected void setTTL(byte arg0) throws IOException {
        }

        @Override
        protected void setTimeToLive(int arg0) throws IOException {
        }

        public Object getOption(int arg0) throws SocketException {
            return null;
        }

        public void setOption(int arg0, Object arg1) throws SocketException {
        }
    }
}

