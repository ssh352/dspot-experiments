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
package com.hazelcast.replicatedmap.merge;


import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractReplicatedMapMergePolicyTest {
    private static final String EXISTING = "EXISTING";

    private static final String MERGING = "MERGING";

    protected ReplicatedMapMergePolicy policy;

    @Test
    public void merge_mergingWins() {
        ReplicatedMapEntryView existing = entryWithGivenPropertyAndValue(1, AbstractReplicatedMapMergePolicyTest.EXISTING);
        ReplicatedMapEntryView merging = entryWithGivenPropertyAndValue(333, AbstractReplicatedMapMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractReplicatedMapMergePolicyTest.MERGING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_existingWins() {
        ReplicatedMapEntryView existing = entryWithGivenPropertyAndValue(333, AbstractReplicatedMapMergePolicyTest.EXISTING);
        ReplicatedMapEntryView merging = entryWithGivenPropertyAndValue(1, AbstractReplicatedMapMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractReplicatedMapMergePolicyTest.EXISTING, policy.merge("map", merging, existing));
    }

    @Test
    public void merge_draw_mergingWins() {
        ReplicatedMapEntryView existing = entryWithGivenPropertyAndValue(1, AbstractReplicatedMapMergePolicyTest.EXISTING);
        ReplicatedMapEntryView merging = entryWithGivenPropertyAndValue(1, AbstractReplicatedMapMergePolicyTest.MERGING);
        Assert.assertEquals(AbstractReplicatedMapMergePolicyTest.MERGING, policy.merge("map", merging, existing));
    }
}
