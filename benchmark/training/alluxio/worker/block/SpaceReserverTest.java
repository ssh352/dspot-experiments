/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.worker.block;


import HeartbeatContext.WORKER_SPACE_RESERVER;
import Sessions.MIGRATE_DATA_SESSION_ID;
import alluxio.conf.ServerConfiguration;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link SpaceReserver}.
 */
public class SpaceReserverTest {
    private ExecutorService mExecutorService;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTempFolder = new TemporaryFolder();

    @Rule
    public ManuallyScheduleHeartbeat mSchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.WORKER_SPACE_RESERVER);

    @Test
    public void reserveCorrectAmountsOfSpace() throws Exception {
        BlockWorker blockWorker = Mockito.mock(BlockWorker.class);
        BlockStoreMeta storeMeta = Mockito.mock(BlockStoreMeta.class);
        Mockito.when(blockWorker.getStoreMeta()).thenReturn(storeMeta);
        Map<String, Long> capacityBytesOnTiers = ImmutableMap.of("MEM", 400L, "HDD", 1000L);
        Mockito.when(storeMeta.getCapacityBytesOnTiers()).thenReturn(capacityBytesOnTiers);
        String tmpFolderPath = mTempFolder.newFolder().getAbsolutePath();
        // Create two tiers named "MEM" and "HDD" with aliases 0 and 1.
        TieredBlockStoreTestUtils.setupConfWithMultiTier(tmpFolderPath, new int[]{ 0, 1 }, new String[]{ "MEM", "HDD" }, new String[][]{ new String[]{ "/a" }, new String[]{ "/b" } }, new long[][]{ new long[]{ 0 }, new long[]{ 0 } }, "/");
        try (Closeable c = toResource()) {
            SpaceReserver spaceReserver = new SpaceReserver(blockWorker);
            mExecutorService.submit(new alluxio.heartbeat.HeartbeatThread(HeartbeatContext.WORKER_SPACE_RESERVER, spaceReserver, 0, ServerConfiguration.global()));
            // Run the space reserver once.
            HeartbeatScheduler.execute(WORKER_SPACE_RESERVER);
            // 400 * 0.2 = 80
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 80L, "MEM");
            // 400 * 0.2 + 1000 * 0.3 = 380
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 380L, "HDD");
        }
    }

    @Test
    public void testLowWatermark() throws Exception {
        BlockWorker blockWorker = Mockito.mock(BlockWorker.class);
        BlockStoreMeta storeMeta = Mockito.mock(BlockStoreMeta.class);
        Mockito.when(blockWorker.getStoreMeta()).thenReturn(storeMeta);
        Map<String, Long> capacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 200L, "HDD", 1000L);
        Map<String, Long> usedCapacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 200L, "HDD", 1000L);
        Mockito.when(storeMeta.getCapacityBytesOnTiers()).thenReturn(capacityBytesOnTiers);
        Mockito.when(storeMeta.getUsedBytesOnTiers()).thenReturn(usedCapacityBytesOnTiers);
        String tmpFolderPath = mTempFolder.newFolder().getAbsolutePath();
        // Create two tiers named "MEM", "SSD" and "HDD" with aliases 0, 1 and 2.
        TieredBlockStoreTestUtils.setupConfWithMultiTier(tmpFolderPath, new int[]{ 0, 1, 2 }, new String[]{ "MEM", "SSD", "HDD" }, new String[][]{ new String[]{ "/a" }, new String[]{ "/b" }, new String[]{ "/c" } }, new long[][]{ new long[]{ 0 }, new long[]{ 0 }, new long[]{ 0 } }, "/");
        try (Closeable c = toResource()) {
            SpaceReserver spaceReserver = new SpaceReserver(blockWorker);
            mExecutorService.submit(new alluxio.heartbeat.HeartbeatThread(HeartbeatContext.WORKER_SPACE_RESERVER, spaceReserver, 0, ServerConfiguration.global()));
            // Run the space reserver once.
            HeartbeatScheduler.execute(WORKER_SPACE_RESERVER);
            // 1000 * 0.4 + 200 * 0.3 + 100 * 0.2 = 480
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 480L, "HDD");
            // 200 * 0.3 + 100 * 0.2 = 80
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 80L, "SSD");
            // 100 * 0.2 = 20
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 20L, "MEM");
        }
    }

    @Test
    public void testHighWatermark() throws Exception {
        BlockWorker blockWorker = Mockito.mock(BlockWorker.class);
        BlockStoreMeta storeMeta = Mockito.mock(BlockStoreMeta.class);
        Mockito.when(blockWorker.getStoreMeta()).thenReturn(storeMeta);
        Map<String, Long> capacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 200L, "HDD", 1000L);
        Map<String, Long> usedCapacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 100L, "HDD", 0L);
        Mockito.when(storeMeta.getCapacityBytesOnTiers()).thenReturn(capacityBytesOnTiers);
        Mockito.when(storeMeta.getUsedBytesOnTiers()).thenReturn(usedCapacityBytesOnTiers);
        String tmpFolderPath = mTempFolder.newFolder().getAbsolutePath();
        // Create two tiers named "MEM", "SSD" and "HDD" with aliases 0, 1 and 2.
        TieredBlockStoreTestUtils.setupConfWithMultiTier(tmpFolderPath, new int[]{ 0, 1, 2 }, new String[]{ "MEM", "SSD", "HDD" }, new String[][]{ new String[]{ "/a" }, new String[]{ "/b" }, new String[]{ "/c" } }, new long[][]{ new long[]{ 0 }, new long[]{ 0 }, new long[]{ 0 } }, "/");
        try (Closeable c = toResource()) {
            SpaceReserver spaceReserver = new SpaceReserver(blockWorker);
            mExecutorService.submit(new alluxio.heartbeat.HeartbeatThread(HeartbeatContext.WORKER_SPACE_RESERVER, spaceReserver, 0, ServerConfiguration.global()));
            // Run the space reserver once.
            HeartbeatScheduler.execute(WORKER_SPACE_RESERVER);
            // 1000 * 0.4 + 200 * 0.3 + 100 * 0.2 = 480
            Mockito.verify(blockWorker, Mockito.never()).freeSpace(Matchers.eq(MIGRATE_DATA_SESSION_ID), Matchers.anyLong(), Matchers.eq("HDD"));
            // 200 * 0.3 + 100 * 0.2 = 80
            Mockito.verify(blockWorker, Mockito.never()).freeSpace(Matchers.eq(MIGRATE_DATA_SESSION_ID), Matchers.anyLong(), Matchers.eq("SSD"));
            // 100 * 0.2 = 20
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 20L, "MEM");
        }
    }

    @Test
    public void smallWatermarkValues() throws Exception {
        BlockWorker blockWorker = Mockito.mock(BlockWorker.class);
        BlockStoreMeta storeMeta = Mockito.mock(BlockStoreMeta.class);
        Mockito.when(blockWorker.getStoreMeta()).thenReturn(storeMeta);
        Map<String, Long> capacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 200L, "HDD", 1000L);
        Map<String, Long> usedCapacityBytesOnTiers = ImmutableMap.of("MEM", 100L, "SSD", 100L, "HDD", 0L);
        Mockito.when(storeMeta.getCapacityBytesOnTiers()).thenReturn(capacityBytesOnTiers);
        Mockito.when(storeMeta.getUsedBytesOnTiers()).thenReturn(usedCapacityBytesOnTiers);
        String tmpFolderPath = mTempFolder.newFolder().getAbsolutePath();
        // Create two tiers named "MEM", "SSD" and "HDD" with aliases 0, 1 and 2.
        TieredBlockStoreTestUtils.setupConfWithMultiTier(tmpFolderPath, new int[]{ 0, 1, 2 }, new String[]{ "MEM", "SSD", "HDD" }, new String[][]{ new String[]{ "/a" }, new String[]{ "/b" }, new String[]{ "/c" } }, new long[][]{ new long[]{ 0 }, new long[]{ 0 }, new long[]{ 0 } }, "/");
        try (Closeable c = toResource()) {
            SpaceReserver spaceReserver = new SpaceReserver(blockWorker);
            mExecutorService.submit(new alluxio.heartbeat.HeartbeatThread(HeartbeatContext.WORKER_SPACE_RESERVER, spaceReserver, 0, ServerConfiguration.global()));
            // Run the space reserver once.
            HeartbeatScheduler.execute(WORKER_SPACE_RESERVER);
            // 1000 * 0.1 + 200 = 300
            Mockito.verify(blockWorker, Mockito.never()).freeSpace(Matchers.eq(MIGRATE_DATA_SESSION_ID), Matchers.anyLong(), Matchers.eq("HDD"));
            // 200 * 0.8 + 100 * 0.7 = 230 -> 200
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 200L, "SSD");
            // 100 * 0.7 = 70
            Mockito.verify(blockWorker).freeSpace(MIGRATE_DATA_SESSION_ID, 70L, "MEM");
        }
    }
}

