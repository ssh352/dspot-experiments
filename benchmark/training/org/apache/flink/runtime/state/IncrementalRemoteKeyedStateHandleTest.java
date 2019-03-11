/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class IncrementalRemoteKeyedStateHandleTest {
    /**
     * This test checks, that for an unregistered {@link IncrementalRemoteKeyedStateHandle} all state
     * (including shared) is discarded.
     */
    @Test
    public void testUnregisteredDiscarding() throws Exception {
        IncrementalRemoteKeyedStateHandle stateHandle = IncrementalRemoteKeyedStateHandleTest.create(new Random(42));
        stateHandle.discardState();
        for (StreamStateHandle handle : stateHandle.getPrivateState().values()) {
            Mockito.verify(handle).discardState();
        }
        for (StreamStateHandle handle : stateHandle.getSharedState().values()) {
            Mockito.verify(handle).discardState();
        }
        Mockito.verify(stateHandle.getMetaStateHandle()).discardState();
    }

    /**
     * This test checks, that for a registered {@link IncrementalRemoteKeyedStateHandle} discards respect
     * all shared state and only discard it one all references are released.
     */
    @Test
    public void testSharedStateDeRegistration() throws Exception {
        SharedStateRegistry registry = spy(new SharedStateRegistry());
        // Create two state handles with overlapping shared state
        IncrementalRemoteKeyedStateHandle stateHandle1 = IncrementalRemoteKeyedStateHandleTest.create(new Random(42));
        IncrementalRemoteKeyedStateHandle stateHandle2 = IncrementalRemoteKeyedStateHandleTest.create(new Random(42));
        // Both handles should not be registered and not discarded by now.
        for (Map.Entry<StateHandleID, StreamStateHandle> entry : stateHandle1.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(entry.getKey());
            Mockito.verify(registry, Mockito.times(0)).unregisterReference(registryKey);
            Mockito.verify(entry.getValue(), Mockito.times(0)).discardState();
        }
        for (Map.Entry<StateHandleID, StreamStateHandle> entry : stateHandle2.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(entry.getKey());
            Mockito.verify(registry, Mockito.times(0)).unregisterReference(registryKey);
            Mockito.verify(entry.getValue(), Mockito.times(0)).discardState();
        }
        // Now we register both ...
        stateHandle1.registerSharedStates(registry);
        stateHandle2.registerSharedStates(registry);
        for (Map.Entry<StateHandleID, StreamStateHandle> stateHandleEntry : stateHandle1.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(stateHandleEntry.getKey());
            Mockito.verify(registry).registerReference(registryKey, stateHandleEntry.getValue());
        }
        for (Map.Entry<StateHandleID, StreamStateHandle> stateHandleEntry : stateHandle2.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(stateHandleEntry.getKey());
            Mockito.verify(registry).registerReference(registryKey, stateHandleEntry.getValue());
        }
        // We discard the first
        stateHandle1.discardState();
        // Should be unregistered, non-shared discarded, shared not discarded
        for (Map.Entry<StateHandleID, StreamStateHandle> entry : stateHandle1.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(entry.getKey());
            Mockito.verify(registry, Mockito.times(1)).unregisterReference(registryKey);
            Mockito.verify(entry.getValue(), Mockito.times(0)).discardState();
        }
        for (StreamStateHandle handle : stateHandle2.getSharedState().values()) {
            Mockito.verify(handle, Mockito.times(0)).discardState();
        }
        for (Map.Entry<StateHandleID, StreamStateHandle> handleEntry : stateHandle1.getPrivateState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(handleEntry.getKey());
            Mockito.verify(registry, Mockito.times(0)).unregisterReference(registryKey);
            Mockito.verify(handleEntry.getValue(), Mockito.times(1)).discardState();
        }
        for (Map.Entry<StateHandleID, StreamStateHandle> handleEntry : stateHandle2.getPrivateState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(handleEntry.getKey());
            Mockito.verify(registry, Mockito.times(0)).unregisterReference(registryKey);
            Mockito.verify(handleEntry.getValue(), Mockito.times(0)).discardState();
        }
        Mockito.verify(stateHandle1.getMetaStateHandle(), Mockito.times(1)).discardState();
        Mockito.verify(stateHandle2.getMetaStateHandle(), Mockito.times(0)).discardState();
        // We discard the second
        stateHandle2.discardState();
        // Now everything should be unregistered and discarded
        for (Map.Entry<StateHandleID, StreamStateHandle> entry : stateHandle1.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(entry.getKey());
            Mockito.verify(registry, Mockito.times(2)).unregisterReference(registryKey);
            Mockito.verify(entry.getValue()).discardState();
        }
        for (Map.Entry<StateHandleID, StreamStateHandle> entry : stateHandle2.getSharedState().entrySet()) {
            SharedStateRegistryKey registryKey = stateHandle1.createSharedStateRegistryKeyFromFileName(entry.getKey());
            Mockito.verify(registry, Mockito.times(2)).unregisterReference(registryKey);
            Mockito.verify(entry.getValue()).discardState();
        }
        Mockito.verify(stateHandle1.getMetaStateHandle(), Mockito.times(1)).discardState();
        Mockito.verify(stateHandle2.getMetaStateHandle(), Mockito.times(1)).discardState();
    }

    /**
     * This tests that re-registration of shared state with another registry works as expected. This simulates a
     * recovery from a checkpoint, when the checkpoint coordinator creates a new shared state registry and re-registers
     * all live checkpoint states.
     */
    @Test
    public void testSharedStateReRegistration() throws Exception {
        SharedStateRegistry stateRegistryA = spy(new SharedStateRegistry());
        IncrementalRemoteKeyedStateHandle stateHandleX = IncrementalRemoteKeyedStateHandleTest.create(new Random(1));
        IncrementalRemoteKeyedStateHandle stateHandleY = IncrementalRemoteKeyedStateHandleTest.create(new Random(2));
        IncrementalRemoteKeyedStateHandle stateHandleZ = IncrementalRemoteKeyedStateHandleTest.create(new Random(3));
        // Now we register first time ...
        stateHandleX.registerSharedStates(stateRegistryA);
        stateHandleY.registerSharedStates(stateRegistryA);
        stateHandleZ.registerSharedStates(stateRegistryA);
        try {
            // Second attempt should fail
            stateHandleX.registerSharedStates(stateRegistryA);
            Assert.fail("Should not be able to register twice with the same registry.");
        } catch (IllegalStateException ignore) {
        }
        // Everything should be discarded for this handle
        stateHandleZ.discardState();
        Mockito.verify(stateHandleZ.getMetaStateHandle(), Mockito.times(1)).discardState();
        for (StreamStateHandle stateHandle : stateHandleZ.getSharedState().values()) {
            Mockito.verify(stateHandle, Mockito.times(1)).discardState();
        }
        // Close the first registry
        stateRegistryA.close();
        // Attempt to register to closed registry should trigger exception
        try {
            IncrementalRemoteKeyedStateHandleTest.create(new Random(4)).registerSharedStates(stateRegistryA);
            Assert.fail("Should not be able to register new state to closed registry.");
        } catch (IllegalStateException ignore) {
        }
        // All state should still get discarded
        stateHandleY.discardState();
        Mockito.verify(stateHandleY.getMetaStateHandle(), Mockito.times(1)).discardState();
        for (StreamStateHandle stateHandle : stateHandleY.getSharedState().values()) {
            Mockito.verify(stateHandle, Mockito.times(1)).discardState();
        }
        // This should still be unaffected
        Mockito.verify(stateHandleX.getMetaStateHandle(), Mockito.never()).discardState();
        for (StreamStateHandle stateHandle : stateHandleX.getSharedState().values()) {
            Mockito.verify(stateHandle, Mockito.never()).discardState();
        }
        // We re-register the handle with a new registry
        SharedStateRegistry sharedStateRegistryB = spy(new SharedStateRegistry());
        stateHandleX.registerSharedStates(sharedStateRegistryB);
        stateHandleX.discardState();
        // Should be completely discarded because it is tracked through the new registry
        Mockito.verify(stateHandleX.getMetaStateHandle(), Mockito.times(1)).discardState();
        for (StreamStateHandle stateHandle : stateHandleX.getSharedState().values()) {
            Mockito.verify(stateHandle, Mockito.times(1)).discardState();
        }
        sharedStateRegistryB.close();
    }
}

