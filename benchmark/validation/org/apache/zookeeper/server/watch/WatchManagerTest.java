/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.watch;


import EventType.NodeCreated;
import EventType.NodeDataChanged;
import EventType.NodeDeleted;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.server.DumbWatcher;
import org.apache.zookeeper.server.ServerMetrics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class WatchManagerTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(WatchManagerTest.class);

    private static final String PATH_PREFIX = "path";

    private ConcurrentHashMap<Integer, DumbWatcher> watchers;

    private Random r;

    private String className;

    public WatchManagerTest(String className) {
        this.className = className;
    }

    public class AddWatcherWorker extends Thread {
        private final IWatchManager manager;

        private final int paths;

        private final int watchers;

        private final AtomicInteger watchesAdded;

        private volatile boolean stopped = false;

        public AddWatcherWorker(IWatchManager manager, int paths, int watchers, AtomicInteger watchesAdded) {
            this.manager = manager;
            this.paths = paths;
            this.watchers = watchers;
            this.watchesAdded = watchesAdded;
        }

        @Override
        public void run() {
            while (!(stopped)) {
                String path = (WatchManagerTest.PATH_PREFIX) + (r.nextInt(paths));
                Watcher watcher = createOrGetWatcher(r.nextInt(watchers));
                if (manager.addWatch(path, watcher)) {
                    watchesAdded.addAndGet(1);
                }
            } 
        }

        public void shutdown() {
            stopped = true;
        }
    }

    public class WatcherTriggerWorker extends Thread {
        private final IWatchManager manager;

        private final int paths;

        private final AtomicInteger triggeredCount;

        private volatile boolean stopped = false;

        public WatcherTriggerWorker(IWatchManager manager, int paths, AtomicInteger triggeredCount) {
            this.manager = manager;
            this.paths = paths;
            this.triggeredCount = triggeredCount;
        }

        @Override
        public void run() {
            while (!(stopped)) {
                String path = (WatchManagerTest.PATH_PREFIX) + (r.nextInt(paths));
                WatcherOrBitSet s = manager.triggerWatch(path, NodeDeleted);
                if (s != null) {
                    triggeredCount.addAndGet(s.size());
                }
                try {
                    Thread.sleep(r.nextInt(10));
                } catch (InterruptedException e) {
                }
            } 
        }

        public void shutdown() {
            stopped = true;
        }
    }

    public class RemoveWatcherWorker extends Thread {
        private final IWatchManager manager;

        private final int paths;

        private final int watchers;

        private final AtomicInteger watchesRemoved;

        private volatile boolean stopped = false;

        public RemoveWatcherWorker(IWatchManager manager, int paths, int watchers, AtomicInteger watchesRemoved) {
            this.manager = manager;
            this.paths = paths;
            this.watchers = watchers;
            this.watchesRemoved = watchesRemoved;
        }

        @Override
        public void run() {
            while (!(stopped)) {
                String path = (WatchManagerTest.PATH_PREFIX) + (r.nextInt(paths));
                Watcher watcher = createOrGetWatcher(r.nextInt(watchers));
                if (manager.removeWatcher(path, watcher)) {
                    watchesRemoved.addAndGet(1);
                }
                try {
                    Thread.sleep(r.nextInt(10));
                } catch (InterruptedException e) {
                }
            } 
        }

        public void shutdown() {
            stopped = true;
        }
    }

    public class CreateDeadWatchersWorker extends Thread {
        private final IWatchManager manager;

        private final int watchers;

        private final Set<Watcher> removedWatchers;

        private volatile boolean stopped = false;

        public CreateDeadWatchersWorker(IWatchManager manager, int watchers, Set<Watcher> removedWatchers) {
            this.manager = manager;
            this.watchers = watchers;
            this.removedWatchers = removedWatchers;
        }

        @Override
        public void run() {
            while (!(stopped)) {
                DumbWatcher watcher = createOrGetWatcher(r.nextInt(watchers));
                watcher.setStale();
                manager.removeWatcher(watcher);
                synchronized(removedWatchers) {
                    removedWatchers.add(watcher);
                }
                try {
                    Thread.sleep(r.nextInt(10));
                } catch (InterruptedException e) {
                }
            } 
        }

        public void shutdown() {
            stopped = true;
        }
    }

    /**
     * Concurrently add and trigger watch, make sure the watches triggered
     * are the same as the number added.
     */
    @Test(timeout = 90000)
    public void testAddAndTriggerWatcher() throws IOException {
        IWatchManager manager = getWatchManager();
        int paths = 1;
        int watchers = 10000;
        // 1. start 5 workers to trigger watchers on that path
        // count all the watchers have been fired
        AtomicInteger watchTriggered = new AtomicInteger();
        List<WatchManagerTest.WatcherTriggerWorker> triggerWorkers = new ArrayList<WatchManagerTest.WatcherTriggerWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.WatcherTriggerWorker worker = new WatchManagerTest.WatcherTriggerWorker(manager, paths, watchTriggered);
            triggerWorkers.add(worker);
            worker.start();
        }
        // 2. start 5 workers to add different watchers on the same path
        // count all the watchers being added
        AtomicInteger watchesAdded = new AtomicInteger();
        List<WatchManagerTest.AddWatcherWorker> addWorkers = new ArrayList<WatchManagerTest.AddWatcherWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.AddWatcherWorker worker = new WatchManagerTest.AddWatcherWorker(manager, paths, watchers, watchesAdded);
            addWorkers.add(worker);
            worker.start();
        }
        while ((watchesAdded.get()) < 100000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        } 
        // 3. stop all the addWorkers
        for (WatchManagerTest.AddWatcherWorker worker : addWorkers) {
            worker.shutdown();
        }
        // 4. running the trigger worker a bit longer to make sure
        // all watchers added are fired
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        // 5. stop all triggerWorkers
        for (WatchManagerTest.WatcherTriggerWorker worker : triggerWorkers) {
            worker.shutdown();
        }
        // 6. make sure the total watch triggered is same as added
        Assert.assertTrue(((watchesAdded.get()) > 0));
        Assert.assertEquals(watchesAdded.get(), watchTriggered.get());
    }

    /**
     * Concurrently add and remove watch, make sure the watches left +
     * the watches removed are equal to the total added watches.
     */
    @Test(timeout = 90000)
    public void testRemoveWatcherOnPath() throws IOException {
        IWatchManager manager = getWatchManager();
        int paths = 10;
        int watchers = 10000;
        // 1. start 5 workers to remove watchers on those path
        // record the watchers have been removed
        AtomicInteger watchesRemoved = new AtomicInteger();
        List<WatchManagerTest.RemoveWatcherWorker> removeWorkers = new ArrayList<WatchManagerTest.RemoveWatcherWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.RemoveWatcherWorker worker = new WatchManagerTest.RemoveWatcherWorker(manager, paths, watchers, watchesRemoved);
            removeWorkers.add(worker);
            worker.start();
        }
        // 2. start 5 workers to add different watchers on different path
        // record the watchers have been added
        AtomicInteger watchesAdded = new AtomicInteger();
        List<WatchManagerTest.AddWatcherWorker> addWorkers = new ArrayList<WatchManagerTest.AddWatcherWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.AddWatcherWorker worker = new WatchManagerTest.AddWatcherWorker(manager, paths, watchers, watchesAdded);
            addWorkers.add(worker);
            worker.start();
        }
        while ((watchesAdded.get()) < 100000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        } 
        // 3. stop all workers
        for (WatchManagerTest.RemoveWatcherWorker worker : removeWorkers) {
            worker.shutdown();
        }
        for (WatchManagerTest.AddWatcherWorker worker : addWorkers) {
            worker.shutdown();
        }
        // 4. sleep for a while to make sure all the thread exited
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        // 5. make sure left watches + removed watches = added watches
        Assert.assertTrue(((watchesAdded.get()) > 0));
        Assert.assertTrue(((watchesRemoved.get()) > 0));
        Assert.assertTrue(((manager.size()) > 0));
        Assert.assertEquals(watchesAdded.get(), ((watchesRemoved.get()) + (manager.size())));
    }

    /**
     * Concurrently add watch while close the watcher to simulate the
     * client connections closed on prod.
     */
    @Test(timeout = 90000)
    public void testDeadWatchers() throws IOException {
        System.setProperty("zookeeper.watcherCleanThreshold", "10");
        System.setProperty("zookeeper.watcherCleanIntervalInSeconds", "1");
        IWatchManager manager = getWatchManager();
        int paths = 1;
        int watchers = 100000;
        // 1. start 5 workers to randomly mark those watcher as dead
        // and remove them from watch manager
        Set<Watcher> deadWatchers = new HashSet<Watcher>();
        List<WatchManagerTest.CreateDeadWatchersWorker> deadWorkers = new ArrayList<WatchManagerTest.CreateDeadWatchersWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.CreateDeadWatchersWorker worker = new WatchManagerTest.CreateDeadWatchersWorker(manager, watchers, deadWatchers);
            deadWorkers.add(worker);
            worker.start();
        }
        // 2. start 5 workers to add different watchers on the same path
        AtomicInteger watchesAdded = new AtomicInteger();
        List<WatchManagerTest.AddWatcherWorker> addWorkers = new ArrayList<WatchManagerTest.AddWatcherWorker>();
        for (int i = 0; i < 5; i++) {
            WatchManagerTest.AddWatcherWorker worker = new WatchManagerTest.AddWatcherWorker(manager, paths, watchers, watchesAdded);
            addWorkers.add(worker);
            worker.start();
        }
        while ((watchesAdded.get()) < 50000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        } 
        // 3. stop all workers
        for (WatchManagerTest.CreateDeadWatchersWorker worker : deadWorkers) {
            worker.shutdown();
        }
        for (WatchManagerTest.AddWatcherWorker worker : addWorkers) {
            worker.shutdown();
        }
        // 4. sleep for a while to make sure all the thread exited
        // the cleaner may wait as long as CleanerInterval+CleanerInterval/2+1
        // So need to sleep as least that long
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        // 5. make sure the dead watchers are not in the existing watchers
        WatchesReport existingWatchers = manager.getWatches();
        for (Watcher w : deadWatchers) {
            Assert.assertFalse(existingWatchers.hasPaths(getSessionId()));
        }
    }

    @Test
    public void testWatcherMetrics() throws IOException {
        IWatchManager manager = getWatchManager();
        ServerMetrics.resetAll();
        DumbWatcher watcher1 = new DumbWatcher(1);
        DumbWatcher watcher2 = new DumbWatcher(2);
        final String path1 = "/path1";
        final String path2 = "/path2";
        final String path3 = "/path3";
        // both wather1 and wather2 are watching path1
        manager.addWatch(path1, watcher1);
        manager.addWatch(path1, watcher2);
        // path2 is watched by watcher1
        manager.addWatch(path2, watcher1);
        manager.triggerWatch(path3, NodeCreated);
        // path3 is not being watched so metric is 0
        checkMetrics("node_created_watch_count", 0L, 0L, 0.0, 0L, 0L);
        // path1 is watched by two watchers so two fired
        manager.triggerWatch(path1, NodeCreated);
        checkMetrics("node_created_watch_count", 2L, 2L, 2.0, 1L, 2L);
        // path2 is watched by one watcher so one fired now total is 3
        manager.triggerWatch(path2, NodeCreated);
        checkMetrics("node_created_watch_count", 1L, 2L, 1.5, 2L, 3L);
        // watches on path1 are no longer there so zero fired
        manager.triggerWatch(path1, NodeDataChanged);
        checkMetrics("node_changed_watch_count", 0L, 0L, 0.0, 0L, 0L);
        // both wather1 and wather2 are watching path1
        manager.addWatch(path1, watcher1);
        manager.addWatch(path1, watcher2);
        // path2 is watched by watcher1
        manager.addWatch(path2, watcher1);
        manager.triggerWatch(path1, NodeDataChanged);
        checkMetrics("node_changed_watch_count", 2L, 2L, 2.0, 1L, 2L);
        manager.triggerWatch(path2, NodeDeleted);
        checkMetrics("node_deleted_watch_count", 1L, 1L, 1.0, 1L, 1L);
        // make sure that node created watch count is not impacted by the fire of other event types
        checkMetrics("node_created_watch_count", 1L, 2L, 1.5, 2L, 3L);
    }
}
