/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.jimfs;


import AbstractWatchService.Key;
import AbstractWatchService.Key.MAX_QUEUE_SIZE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.util.Arrays.asList;


/**
 * Tests for {@link AbstractWatchService}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class AbstractWatchServiceTest {
    private AbstractWatchService watcher;

    @Test
    public void testNewWatcher() throws IOException {
        assertThat(watcher.isOpen()).isTrue();
        assertThat(watcher.poll()).isNull();
        assertThat(watcher.queuedKeys()).isEmpty();
        watcher.close();
        assertThat(watcher.isOpen()).isFalse();
    }

    @Test
    public void testRegister() throws IOException {
        Watchable watchable = new AbstractWatchServiceTest.StubWatchable();
        AbstractWatchService.Key key = watcher.register(watchable, ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        assertThat(key.isValid()).isTrue();
        assertThat(key.pollEvents()).isEmpty();
        assertThat(key.subscribesTo(StandardWatchEventKinds.ENTRY_CREATE)).isTrue();
        assertThat(key.subscribesTo(StandardWatchEventKinds.ENTRY_DELETE)).isFalse();
        assertThat(key.watchable()).isEqualTo(watchable);
        assertThat(key.state()).isEqualTo(State.READY);
    }

    @Test
    public void testPostEvent() throws IOException {
        AbstractWatchService.Key key = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        AbstractWatchService.Event<Path> event = new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null);
        key.post(event);
        key.signal();
        assertThat(watcher.queuedKeys()).containsExactly(key);
        WatchKey retrievedKey = watcher.poll();
        assertThat(retrievedKey).isEqualTo(key);
        List<WatchEvent<?>> events = retrievedKey.pollEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(event);
        // polling should have removed all events
        assertThat(retrievedKey.pollEvents()).isEmpty();
    }

    @Test
    public void testKeyStates() throws IOException {
        AbstractWatchService.Key key = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        AbstractWatchService.Event<Path> event = new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null);
        assertThat(key.state()).isEqualTo(State.READY);
        key.post(event);
        key.signal();
        assertThat(key.state()).isEqualTo(State.SIGNALLED);
        AbstractWatchService.Event<Path> event2 = new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null);
        key.post(event2);
        assertThat(key.state()).isEqualTo(State.SIGNALLED);
        // key was not queued twice
        assertThat(watcher.queuedKeys()).containsExactly(key);
        assertThat(watcher.poll().pollEvents()).containsExactly(event, event2);
        assertThat(watcher.poll()).isNull();
        key.post(event);
        // still not added to queue; already signalled
        assertThat(watcher.poll()).isNull();
        assertThat(key.pollEvents()).containsExactly(event);
        key.reset();
        assertThat(key.state()).isEqualTo(State.READY);
        key.post(event2);
        key.signal();
        // now that it's reset it can be requeued
        assertThat(watcher.poll()).isEqualTo(key);
    }

    @Test
    public void testKeyRequeuedOnResetIfEventsArePending() throws IOException {
        AbstractWatchService.Key key = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        key.post(new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null));
        key.signal();
        key = ((AbstractWatchService.Key) (watcher.poll()));
        assertThat(watcher.queuedKeys()).isEmpty();
        assertThat(key.pollEvents()).hasSize(1);
        key.post(new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null));
        assertThat(watcher.queuedKeys()).isEmpty();
        key.reset();
        assertThat(key.state()).isEqualTo(State.SIGNALLED);
        assertThat(watcher.queuedKeys()).hasSize(1);
    }

    @Test
    public void testOverflow() throws IOException {
        AbstractWatchService.Key key = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        for (int i = 0; i < ((Key.MAX_QUEUE_SIZE) + 10); i++) {
            key.post(new AbstractWatchService.Event<>(StandardWatchEventKinds.ENTRY_CREATE, 1, null));
        }
        key.signal();
        List<WatchEvent<?>> events = key.pollEvents();
        assertThat(events).hasSize(((Key.MAX_QUEUE_SIZE) + 1));
        for (int i = 0; i < (Key.MAX_QUEUE_SIZE); i++) {
            assertThat(events.get(i).kind()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE);
        }
        WatchEvent<?> lastEvent = events.get(MAX_QUEUE_SIZE);
        assertThat(lastEvent.kind()).isEqualTo(StandardWatchEventKinds.OVERFLOW);
        assertThat(lastEvent.count()).isEqualTo(10);
    }

    @Test
    public void testResetAfterCancelReturnsFalse() throws IOException {
        AbstractWatchService.Key key = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        key.signal();
        key.cancel();
        assertThat(key.reset()).isFalse();
    }

    @Test
    public void testClosedWatcher() throws IOException, InterruptedException {
        AbstractWatchService.Key key1 = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_CREATE));
        AbstractWatchService.Key key2 = watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableSet.of(StandardWatchEventKinds.ENTRY_MODIFY));
        assertThat(key1.isValid()).isTrue();
        assertThat(key2.isValid()).isTrue();
        watcher.close();
        assertThat(key1.isValid()).isFalse();
        assertThat(key2.isValid()).isFalse();
        assertThat(key1.reset()).isFalse();
        assertThat(key2.reset()).isFalse();
        try {
            watcher.poll();
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
        try {
            watcher.poll(10, TimeUnit.SECONDS);
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
        try {
            watcher.take();
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
        try {
            watcher.register(new AbstractWatchServiceTest.StubWatchable(), ImmutableList.<WatchEvent.Kind<?>>of());
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
    }

    // TODO(cgdecker): Test concurrent use of Watcher
    /**
     * A fake {@link Watchable} for testing.
     */
    private static final class StubWatchable implements Watchable {
        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
            return register(watcher, events);
        }

        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
            return ((AbstractWatchService) (watcher)).register(this, asList(events));
        }
    }
}

