/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.Gui.Debug.MemoryPanel.Implementations;


import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DetachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SearchReply;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CSearchWaiterTest {
    @Test
    public void testCloseRequested() throws Exception {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        final CSearchWaiter waiter = new CSearchWaiter(debugger, new CAddress(768), 256, new byte[]{ 10 });
        waiter.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    waiter.runExpensiveCommand();
                } catch (final Exception e) {
                }
            }
        }.start();
        Thread.sleep(250);
        waiter.closeRequested();
        Thread.sleep(250);
        debugger.close();
        Assert.assertFalse(waiter.isAlive());
        Assert.assertNull(waiter.getReply());
    }

    @Test
    public void testDetach() throws Exception {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        final CSearchWaiter waiter = new CSearchWaiter(debugger, new CAddress(768), 256, new byte[]{ 10 });
        waiter.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    waiter.runExpensiveCommand();
                } catch (final Exception e) {
                }
            }
        }.start();
        Thread.sleep(250);
        debugger.connection.m_synchronizer.receivedEvent(new DetachReply(0, 0));
        Thread.sleep(250);
        Assert.assertFalse(waiter.isAlive());
        Assert.assertNull(waiter.getReply());
        debugger.close();
    }

    @Test
    public void testFailure() throws Exception {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        final CSearchWaiter waiter = new CSearchWaiter(debugger, new CAddress(768), 256, new byte[]{ 10 });
        waiter.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    waiter.runExpensiveCommand();
                } catch (final Exception e) {
                }
            }
        }.start();
        Thread.sleep(250);
        debugger.connection.m_synchronizer.receivedEvent(new SearchReply(0, 3, null));
        Thread.sleep(250);
        Assert.assertFalse(waiter.isAlive());
        Assert.assertNotNull(waiter.getReply());
        Assert.assertFalse(waiter.getReply().success());
        debugger.close();
    }

    @Test
    public void testSuccess() throws Exception {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        final CSearchWaiter waiter = new CSearchWaiter(debugger, new CAddress(768), 256, new byte[]{ 10 });
        waiter.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    waiter.runExpensiveCommand();
                } catch (final Exception e) {
                }
            }
        }.start();
        Thread.sleep(250);
        debugger.connection.m_synchronizer.receivedEvent(new SearchReply(0, 0, new CAddress(768)));
        Thread.sleep(250);
        Assert.assertFalse(waiter.isAlive());
        Assert.assertNotNull(waiter.getReply());
        Assert.assertTrue(waiter.getReply().success());
        debugger.close();
    }
}

