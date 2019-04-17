/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.data;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ReadWriteDataRange_SubrangeTest {
    /**
     * ***********************************************************************************************
     */
    @Test
    public void testSubrange_SingleUnit_Full() {
        long len = 256;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len);
        DataRange range = new ReadWriteDataRange(units, 0, len).getSubrange(0);
        Assert.assertEquals(len, range.length());
        List<UnitAccess> expectedUnits = Collections.singletonList(new UnitAccess(units.get(0), 0, len));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_SingleUnit_WithOffset() {
        long len = 256;
        long off = 16;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len);
        DataRange range = new ReadWriteDataRange(units, 0, len).getSubrange(16);
        Assert.assertEquals((len - off), range.length());
        List<UnitAccess> expectedUnits = Collections.singletonList(new UnitAccess(units.get(0), off, len));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_SingleUnit_WithLength() {
        long len = 256;
        long lim = 192;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len);
        DataRange range = new ReadWriteDataRange(units, 0, len).getSubrange(0, lim);
        Assert.assertEquals(lim, range.length());
        List<UnitAccess> expectedUnits = Collections.singletonList(new UnitAccess(units.get(0), 0, lim));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_SingleUnit_WithOffsetAndLength() {
        long len = 256;
        long off = 16;
        long lim = 192;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len);
        DataRange range = new ReadWriteDataRange(units, 0, len).getSubrange(off, (lim - off));
        Assert.assertEquals((lim - off), range.length());
        List<UnitAccess> expectedUnits = Collections.singletonList(new UnitAccess(units.get(0), off, lim));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    /**
     * ***********************************************************************************************
     */
    @Test
    public void testSubrange_MultipleUnits_Full() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange(0);
        Assert.assertEquals(((len1 + len2) + len3), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(0), 0, len1), new UnitAccess(units.get(1), 0, len2), new UnitAccess(units.get(2), 0, len3));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithOffset() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long off = 32;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange(off);
        Assert.assertEquals((((len1 - off) + len2) + len3), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(0), off, len1), new UnitAccess(units.get(1), 0, len2), new UnitAccess(units.get(2), 0, len3));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithOffsetInSecondUnit() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long off = 32;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange((len1 + off));
        Assert.assertEquals(((len2 - off) + len3), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(1), off, len2), new UnitAccess(units.get(2), 0, len3));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithLength() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long lim = 64;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange(0, ((len1 + len2) + lim));
        Assert.assertEquals(((len1 + len2) + lim), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(0), 0, len1), new UnitAccess(units.get(1), 0, len2), new UnitAccess(units.get(2), 0, lim));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithLength_TrimLastUnit() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long lim = 64;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange(0, (len1 + lim));
        Assert.assertEquals((len1 + lim), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(0), 0, len1), new UnitAccess(units.get(1), 0, lim));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithOffsetAndLength() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long off = 192;
        long lim = 64;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = new ReadWriteDataRange(units, 0, len3).getSubrange(off, (((len1 - off) + len2) + lim));
        Assert.assertEquals((((len1 - off) + len2) + lim), range.length());
        List<UnitAccess> expectedUnits = Arrays.asList(new UnitAccess(units.get(0), off, len1), new UnitAccess(units.get(1), 0, len2), new UnitAccess(units.get(2), 0, lim));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }

    @Test
    public void testSubrange_MultipleUnits_WithOffsetAndLength_TrimFirstAndLastUnits() {
        long len1 = 256;
        long len2 = 64;
        long len3 = 192;
        long off = 32;
        List<StorageUnit> units = ChunkDescriptorTestUtil.mockStorageUnits(len1, len2, len3);
        DataRange range = getSubrange((len1 + off), 1);
        Assert.assertEquals(1, range.length());
        List<UnitAccess> expectedUnits = Collections.singletonList(new UnitAccess(units.get(1), off, (off + 1)));
        ReadWriteDataRangeTest.assertHasUnits(expectedUnits, range);
    }
}
