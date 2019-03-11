/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.ProcessorStateException;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import static StateDirectory.LOCK_FILE_NAME;


public class StateDirectoryTest {
    private final MockTime time = new MockTime();

    private File stateDir;

    private final String applicationId = "applicationId";

    private StateDirectory directory;

    private File appDir;

    @Test
    public void shouldCreateBaseDirectory() {
        Assert.assertTrue(stateDir.exists());
        Assert.assertTrue(stateDir.isDirectory());
        Assert.assertTrue(appDir.exists());
        Assert.assertTrue(appDir.isDirectory());
    }

    @Test
    public void shouldCreateTaskStateDirectory() {
        final TaskId taskId = new TaskId(0, 0);
        final File taskDirectory = directory.directoryForTask(taskId);
        Assert.assertTrue(taskDirectory.exists());
        Assert.assertTrue(taskDirectory.isDirectory());
    }

    @Test
    public void shouldLockTaskStateDirectory() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        final File taskDirectory = directory.directoryForTask(taskId);
        directory.lock(taskId);
        try (final FileChannel channel = FileChannel.open(new File(taskDirectory, LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            channel.tryLock();
            Assert.fail("shouldn't be able to lock already locked directory");
        } catch (final OverlappingFileLockException e) {
            // swallow
        } finally {
            directory.unlock(taskId);
        }
    }

    @Test
    public void shouldBeTrueIfAlreadyHoldsLock() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        directory.directoryForTask(taskId);
        directory.lock(taskId);
        try {
            Assert.assertTrue(directory.lock(taskId));
        } finally {
            directory.unlock(taskId);
        }
    }

    @Test
    public void shouldThrowProcessorStateException() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        Utils.delete(stateDir);
        try {
            directory.directoryForTask(taskId);
            Assert.fail("Should have thrown ProcessorStateException");
        } catch (final ProcessorStateException expected) {
            // swallow
        }
    }

    @Test
    public void shouldNotLockDeletedDirectory() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        Utils.delete(stateDir);
        Assert.assertFalse(directory.lock(taskId));
    }

    @Test
    public void shouldLockMultipleTaskDirectories() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        final File task1Dir = directory.directoryForTask(taskId);
        final TaskId taskId2 = new TaskId(1, 0);
        final File task2Dir = directory.directoryForTask(taskId2);
        try (final FileChannel channel1 = FileChannel.open(new File(task1Dir, LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);final FileChannel channel2 = FileChannel.open(new File(task2Dir, LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            directory.lock(taskId);
            directory.lock(taskId2);
            channel1.tryLock();
            channel2.tryLock();
            Assert.fail("shouldn't be able to lock already locked directory");
        } catch (final OverlappingFileLockException e) {
            // swallow
        } finally {
            directory.unlock(taskId);
            directory.unlock(taskId2);
        }
    }

    @Test
    public void shouldReleaseTaskStateDirectoryLock() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        final File taskDirectory = directory.directoryForTask(taskId);
        directory.lock(taskId);
        directory.unlock(taskId);
        try (final FileChannel channel = FileChannel.open(new File(taskDirectory, LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            channel.tryLock();
        }
    }

    @Test
    public void shouldCleanUpTaskStateDirectoriesThatAreNotCurrentlyLocked() throws Exception {
        final TaskId task0 = new TaskId(0, 0);
        final TaskId task1 = new TaskId(1, 0);
        try {
            directory.lock(task0);
            directory.lock(task1);
            directory.directoryForTask(new TaskId(2, 0));
            List<File> files = Arrays.asList(Objects.requireNonNull(appDir.listFiles()));
            Assert.assertEquals(3, files.size());
            time.sleep(1000);
            directory.cleanRemovedTasks(0);
            files = Arrays.asList(Objects.requireNonNull(appDir.listFiles()));
            Assert.assertEquals(2, files.size());
            Assert.assertTrue(files.contains(new File(appDir, task0.toString())));
            Assert.assertTrue(files.contains(new File(appDir, task1.toString())));
        } finally {
            directory.unlock(task0);
            directory.unlock(task1);
        }
    }

    @Test
    public void shouldCleanupStateDirectoriesWhenLastModifiedIsLessThanNowMinusCleanupDelay() {
        final File dir = directory.directoryForTask(new TaskId(2, 0));
        final int cleanupDelayMs = 60000;
        directory.cleanRemovedTasks(cleanupDelayMs);
        Assert.assertTrue(dir.exists());
        time.sleep((cleanupDelayMs + 1000));
        directory.cleanRemovedTasks(cleanupDelayMs);
        Assert.assertFalse(dir.exists());
    }

    @Test
    public void shouldNotRemoveNonTaskDirectoriesAndFiles() {
        final File otherDir = TestUtils.tempDirectory(stateDir.toPath(), "foo");
        directory.cleanRemovedTasks(0);
        Assert.assertTrue(otherDir.exists());
    }

    @Test
    public void shouldListAllTaskDirectories() {
        TestUtils.tempDirectory(stateDir.toPath(), "foo");
        final File taskDir1 = directory.directoryForTask(new TaskId(0, 0));
        final File taskDir2 = directory.directoryForTask(new TaskId(0, 1));
        final List<File> dirs = Arrays.asList(directory.listTaskDirectories());
        Assert.assertEquals(2, dirs.size());
        Assert.assertTrue(dirs.contains(taskDir1));
        Assert.assertTrue(dirs.contains(taskDir2));
    }

    @Test
    public void shouldCreateDirectoriesIfParentDoesntExist() {
        final File tempDir = TestUtils.tempDirectory();
        final File stateDir = new File(new File(tempDir, "foo"), "state-dir");
        final StateDirectory stateDirectory = new StateDirectory(new StreamsConfig(new Properties() {
            {
                put(APPLICATION_ID_CONFIG, applicationId);
                put(BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
                put(STATE_DIR_CONFIG, stateDir.getPath());
            }
        }), time, true);
        final File taskDir = stateDirectory.directoryForTask(new TaskId(0, 0));
        Assert.assertTrue(stateDir.exists());
        Assert.assertTrue(taskDir.exists());
    }

    @Test
    public void shouldLockGlobalStateDirectory() throws Exception {
        directory.lockGlobalState();
        try (final FileChannel channel = FileChannel.open(new File(directory.globalStateDir(), LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            channel.lock();
            Assert.fail("Should have thrown OverlappingFileLockException");
        } catch (final OverlappingFileLockException expcted) {
            // swallow
        } finally {
            directory.unlockGlobalState();
        }
    }

    @Test
    public void shouldUnlockGlobalStateDirectory() throws Exception {
        directory.lockGlobalState();
        directory.unlockGlobalState();
        try (final FileChannel channel = FileChannel.open(new File(directory.globalStateDir(), LOCK_FILE_NAME).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            // should lock without any exceptions
            channel.lock();
        }
    }

    @Test
    public void shouldNotLockStateDirLockedByAnotherThread() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        final AtomicReference<IOException> exceptionOnThread = new AtomicReference<>();
        final Thread thread = new Thread(() -> {
            try {
                directory.lock(taskId);
            } catch (final IOException e) {
                exceptionOnThread.set(e);
            }
        });
        thread.start();
        thread.join(30000);
        Assert.assertNull("should not have had an exception during locking on other thread", exceptionOnThread.get());
        Assert.assertFalse(directory.lock(taskId));
    }

    @Test
    public void shouldNotUnLockStateDirLockedByAnotherThread() throws Exception {
        final TaskId taskId = new TaskId(0, 0);
        final CountDownLatch lockLatch = new CountDownLatch(1);
        final CountDownLatch unlockLatch = new CountDownLatch(1);
        final AtomicReference<Exception> exceptionOnThread = new AtomicReference<>();
        final Thread thread = new Thread(() -> {
            try {
                directory.lock(taskId);
                lockLatch.countDown();
                unlockLatch.await();
                directory.unlock(taskId);
            } catch (final Exception e) {
                exceptionOnThread.set(e);
            }
        });
        thread.start();
        lockLatch.await(5, TimeUnit.SECONDS);
        Assert.assertNull("should not have had an exception on other thread", exceptionOnThread.get());
        directory.unlock(taskId);
        Assert.assertFalse(directory.lock(taskId));
        unlockLatch.countDown();
        thread.join(30000);
        Assert.assertNull("should not have had an exception on other thread", exceptionOnThread.get());
        Assert.assertTrue(directory.lock(taskId));
    }

    @Test
    public void shouldCleanupAllTaskDirectoriesIncludingGlobalOne() {
        directory.directoryForTask(new TaskId(1, 0));
        directory.globalStateDir();
        List<File> files = Arrays.asList(Objects.requireNonNull(appDir.listFiles()));
        Assert.assertEquals(2, files.size());
        directory.clean();
        files = Arrays.asList(Objects.requireNonNull(appDir.listFiles()));
        Assert.assertEquals(0, files.size());
    }

    @Test
    public void shouldNotCreateBaseDirectory() throws Exception {
        initializeStateDirectory(false);
        Assert.assertFalse(stateDir.exists());
        Assert.assertFalse(appDir.exists());
    }

    @Test
    public void shouldNotCreateTaskStateDirectory() throws Exception {
        initializeStateDirectory(false);
        final TaskId taskId = new TaskId(0, 0);
        final File taskDirectory = directory.directoryForTask(taskId);
        Assert.assertFalse(taskDirectory.exists());
    }

    @Test
    public void shouldNotCreateGlobalStateDirectory() throws Exception {
        initializeStateDirectory(false);
        final File globalStateDir = directory.globalStateDir();
        Assert.assertFalse(globalStateDir.exists());
    }

    @Test
    public void shouldLockTaskStateDirectoryWhenDirectoryCreationDisabled() throws Exception {
        initializeStateDirectory(false);
        final TaskId taskId = new TaskId(0, 0);
        Assert.assertTrue(directory.lock(taskId));
    }

    @Test
    public void shouldLockGlobalStateDirectoryWhenDirectoryCreationDisabled() throws Exception {
        initializeStateDirectory(false);
        Assert.assertTrue(directory.lockGlobalState());
    }
}

