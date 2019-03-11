/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.nio.channels;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import junit.framework.TestCase;


public class OldServerSocketChannelTest extends TestCase {
    private static final int TIME_UNIT = 200;

    private ServerSocketChannel serverChannel;

    private SocketChannel clientChannel;

    // -------------------------------------------------------------------
    // Test for methods in abstract class.
    // -------------------------------------------------------------------
    public void testConstructor() throws IOException {
        ServerSocketChannel channel = SelectorProvider.provider().openServerSocketChannel();
        TestCase.assertNotNull(channel);
        TestCase.assertSame(SelectorProvider.provider(), channel.provider());
    }

    public void testIsOpen() throws Exception {
        TestCase.assertTrue(this.serverChannel.isOpen());
        this.serverChannel.close();
        TestCase.assertFalse(this.serverChannel.isOpen());
    }

    public void test_accept_Block_NoConnect_interrupt() throws IOException {
        TestCase.assertTrue(this.serverChannel.isBlocking());
        ServerSocket gotSocket = this.serverChannel.socket();
        gotSocket.bind(null);
        class MyThread extends Thread {
            public String errMsg = null;

            public void run() {
                try {
                    serverChannel.accept();
                    errMsg = "should throw ClosedByInterruptException";
                } catch (ClosedByInterruptException e) {
                    // expected
                } catch (Exception e) {
                    errMsg = (("caught wrong Exception: " + (e.getClass())) + ": ") + (e.getMessage());
                }
            }
        }
        MyThread thread = new MyThread();
        thread.start();
        try {
            Thread.currentThread().sleep(OldServerSocketChannelTest.TIME_UNIT);
            thread.interrupt();
        } catch (InterruptedException e) {
            TestCase.fail("Should not throw a InterruptedException");
        }
        if ((thread.errMsg) != null) {
            TestCase.fail(thread.errMsg);
        }
    }
}

