/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.spi.merge;


import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.merge.SplitBrainMergeTypes.MapMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSplitBrainMergePolicyTest {
    private static final SerializationService SERIALIZATION_SERVICE = new DefaultSerializationServiceBuilder().build();

    private static final Data EXISTING = AbstractSplitBrainMergePolicyTest.SERIALIZATION_SERVICE.toData("EXISTING");

    private static final Data MERGING = AbstractSplitBrainMergePolicyTest.SERIALIZATION_SERVICE.toData("MERGING");

    protected SplitBrainMergePolicy<Data, MapMergeTypes> mergePolicy;

    @Test
    public void merge_mergingWins() {
        MapMergeTypes existing = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.EXISTING);
        MapMergeTypes merging = mergingValueWithGivenPropertyAndValue(333, AbstractSplitBrainMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractSplitBrainMergePolicyTest.MERGING, mergePolicy.merge(merging, existing));
    }

    @Test
    public void merge_existingWins() {
        MapMergeTypes existing = mergingValueWithGivenPropertyAndValue(333, AbstractSplitBrainMergePolicyTest.EXISTING);
        MapMergeTypes merging = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractSplitBrainMergePolicyTest.EXISTING, mergePolicy.merge(merging, existing));
    }

    @Test
    public void merge_draw_mergingWins() {
        MapMergeTypes existing = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.EXISTING);
        MapMergeTypes merging = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractSplitBrainMergePolicyTest.MERGING, mergePolicy.merge(merging, existing));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_mergingWins_sinceExistingIsNotExist() {
        MapMergeTypes existing = null;
        MapMergeTypes merging = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractSplitBrainMergePolicyTest.MERGING, mergePolicy.merge(merging, existing));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void merge_existingWins_sinceMergingIsNotExist() {
        MapMergeTypes existing = mergingValueWithGivenPropertyAndValue(1, AbstractSplitBrainMergePolicyTest.EXISTING);
        MapMergeTypes merging = null;
        Assert.assertEquals(AbstractSplitBrainMergePolicyTest.EXISTING, mergePolicy.merge(merging, existing));
    }
}
