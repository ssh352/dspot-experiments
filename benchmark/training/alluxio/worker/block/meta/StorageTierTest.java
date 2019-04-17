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
package alluxio.worker.block.meta;


import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_PATH;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link StorageTier}.
 */
public class StorageTierTest {
    private static final long TEST_SESSION_ID = 2;

    private static final long TEST_TEMP_BLOCK_ID = 10;

    private static final long TEST_BLOCK_SIZE = 20;

    private static final long TEST_DIR1_CAPACITY = 2000;

    private static final long TEST_DIR2_CAPACITY = 3000;

    private static final int TEST_TIER_ORDINAL = 0;

    private static final String TEST_TIER_ALIAS = "MEM";

    private static final String TEST_WORKER_DATA_DIR = "testworker";

    private static final long[] TIER_CAPACITY_BYTES = new long[]{ StorageTierTest.TEST_DIR1_CAPACITY, StorageTierTest.TEST_DIR2_CAPACITY };

    private StorageTier mTier;

    private StorageDir mDir1;

    private TempBlockMeta mTempBlockMeta;

    private String mTestDirPath1;

    private String mTestDirPath2;

    private String mTestBlockDirPath1;

    private String mTestBlockDirPath2;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Tests the {@link StorageTier#getTierAlias()} method.
     */
    @Test
    public void getTierAlias() {
        Assert.assertEquals(StorageTierTest.TEST_TIER_ALIAS, mTier.getTierAlias());
    }

    /**
     * Tests the {@link StorageTier#getTierOrdinal()} method.
     */
    @Test
    public void getTierLevel() {
        Assert.assertEquals(StorageTierTest.TEST_TIER_ORDINAL, mTier.getTierOrdinal());
    }

    /**
     * Tests the {@link StorageTier#getCapacityBytes()} method.
     */
    @Test
    public void getCapacityBytes() throws Exception {
        Assert.assertEquals(((StorageTierTest.TEST_DIR1_CAPACITY) + (StorageTierTest.TEST_DIR2_CAPACITY)), mTier.getCapacityBytes());
        // Capacity should not change after adding block to a dir.
        mDir1.addTempBlockMeta(mTempBlockMeta);
        Assert.assertEquals(((StorageTierTest.TEST_DIR1_CAPACITY) + (StorageTierTest.TEST_DIR2_CAPACITY)), mTier.getCapacityBytes());
    }

    /**
     * Tests the {@link StorageTier#getAvailableBytes()} method.
     */
    @Test
    public void getAvailableBytes() throws Exception {
        Assert.assertEquals(((StorageTierTest.TEST_DIR1_CAPACITY) + (StorageTierTest.TEST_DIR2_CAPACITY)), mTier.getAvailableBytes());
        // Capacity should subtract block size after adding block to a dir.
        mDir1.addTempBlockMeta(mTempBlockMeta);
        Assert.assertEquals((((StorageTierTest.TEST_DIR1_CAPACITY) + (StorageTierTest.TEST_DIR2_CAPACITY)) - (StorageTierTest.TEST_BLOCK_SIZE)), mTier.getAvailableBytes());
    }

    /**
     * Tests that an exception is thrown when trying to get a directory by a non-existing index.
     */
    @Test
    public void getDir() {
        mThrown.expect(IndexOutOfBoundsException.class);
        StorageDir dir1 = mTier.getDir(0);
        Assert.assertEquals(mTestBlockDirPath1, dir1.getDirPath());
        StorageDir dir2 = mTier.getDir(1);
        Assert.assertEquals(mTestBlockDirPath2, dir2.getDirPath());
        // Get dir by a non-existing index, expect getDir to fail and throw IndexOutOfBoundsException
        mTier.getDir(2);
    }

    /**
     * Tests the {@link StorageTier#getStorageDirs()} method.
     */
    @Test
    public void getStorageDirs() {
        List<StorageDir> dirs = mTier.getStorageDirs();
        Assert.assertEquals(2, dirs.size());
        Assert.assertEquals(mTestBlockDirPath1, dirs.get(0).getDirPath());
        Assert.assertEquals(mTestBlockDirPath2, dirs.get(1).getDirPath());
    }

    @Test
    public void tolerantFailureInStorageDir() throws Exception {
        PropertyKey tierDirPathConf = WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(0);
        ServerConfiguration.set(tierDirPathConf, ("/dev/null/invalid," + (mTestDirPath1)));
        mTier = StorageTier.newStorageTier("MEM");
        List<StorageDir> dirs = mTier.getStorageDirs();
        Assert.assertEquals(1, dirs.size());
        Assert.assertEquals(mTestBlockDirPath1, dirs.get(0).getDirPath());
    }
}
