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


import alluxio.worker.block.TieredBlockStoreTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link StorageTierView}.
 */
public class StorageTierViewTest {
    private static final int TEST_TIER_LEVEL = 0;

    private StorageTier mTestTier;

    private StorageTierView mTestTierView;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Tests the {@link StorageTierView#getDirViews()} method.
     */
    @Test
    public void getDirViews() {
        Assert.assertEquals(TieredBlockStoreTestUtils.TIER_PATH[StorageTierViewTest.TEST_TIER_LEVEL].length, mTestTierView.getDirViews().size());
    }

    /**
     * Tests the {@link StorageTierView#getDirView(int)} method.
     */
    @Test
    public void getDirView() {
        for (int i = 0; i < (TieredBlockStoreTestUtils.TIER_PATH[StorageTierViewTest.TEST_TIER_LEVEL].length); i++) {
            Assert.assertEquals(i, mTestTierView.getDirView(i).getDirViewIndex());
        }
    }

    /**
     * Tests that an exception is thrown when trying to get a storage directory view with a bad index.
     */
    @Test
    public void getDirViewBadIndex() {
        mThrown.expect(IndexOutOfBoundsException.class);
        int badDirIndex = TieredBlockStoreTestUtils.TIER_PATH[StorageTierViewTest.TEST_TIER_LEVEL].length;
        Assert.assertEquals(badDirIndex, mTestTierView.getDirView(badDirIndex).getDirViewIndex());
    }

    /**
     * Tests the {@link StorageTierView#getTierViewAlias()} method.
     */
    @Test
    public void getTierViewAlias() {
        Assert.assertEquals(mTestTier.getTierAlias(), mTestTierView.getTierViewAlias());
    }

    /**
     * Tests the {@link StorageTierView#getTierViewOrdinal()} method.
     */
    @Test
    public void getTierViewOrdinal() {
        Assert.assertEquals(mTestTier.getTierOrdinal(), mTestTierView.getTierViewOrdinal());
    }
}

