/**
 * *****************************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * *****************************************************************************
 */
package org.apache.hadoop.yarn.server.resourcemanager.reservation;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ReservationAllocationState;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.junit.Assert;
import org.junit.Test;


public class TestReservationSystemUtil {
    @Test
    public void testConvertAllocationsToReservationInfo() {
        long startTime = new Date().getTime();
        long step = 10000;
        int[] alloc = new int[]{ 10, 10, 10 };
        ReservationId id = ReservationSystemTestUtil.getNewReservationId();
        ReservationAllocation allocation = createReservationAllocation(startTime, (startTime + (10 * step)), step, alloc, id, createResource(4000, 2));
        List<ReservationAllocationState> infoList = ReservationSystemUtil.convertAllocationsToReservationInfo(Collections.singleton(allocation), true);
        Assert.assertEquals(infoList.size(), 1);
        Assert.assertEquals(infoList.get(0).getReservationId().toString(), id.toString());
        Assert.assertFalse(infoList.get(0).getResourceAllocationRequests().isEmpty());
    }

    @Test
    public void testConvertAllocationsToReservationInfoNoAllocations() {
        long startTime = new Date().getTime();
        long step = 10000;
        int[] alloc = new int[]{ 10, 10, 10 };
        ReservationId id = ReservationSystemTestUtil.getNewReservationId();
        ReservationAllocation allocation = createReservationAllocation(startTime, (startTime + (10 * step)), step, alloc, id, createResource(4000, 2));
        List<ReservationAllocationState> infoList = ReservationSystemUtil.convertAllocationsToReservationInfo(Collections.singleton(allocation), false);
        Assert.assertEquals(infoList.size(), 1);
        Assert.assertEquals(infoList.get(0).getReservationId().toString(), id.toString());
        Assert.assertTrue(infoList.get(0).getResourceAllocationRequests().isEmpty());
    }

    @Test
    public void testConvertAllocationsToReservationInfoEmptyAllocations() {
        long startTime = new Date().getTime();
        long step = 10000;
        int[] alloc = new int[]{  };
        ReservationId id = ReservationSystemTestUtil.getNewReservationId();
        ReservationAllocation allocation = createReservationAllocation(startTime, (startTime + (10 * step)), step, alloc, id, createResource(4000, 2));
        List<ReservationAllocationState> infoList = ReservationSystemUtil.convertAllocationsToReservationInfo(Collections.singleton(allocation), false);
        Assert.assertEquals(infoList.size(), 1);
        Assert.assertEquals(infoList.get(0).getReservationId().toString(), id.toString());
        Assert.assertTrue(infoList.get(0).getResourceAllocationRequests().isEmpty());
    }

    @Test
    public void testConvertAllocationsToReservationInfoEmptySet() {
        List<ReservationAllocationState> infoList = ReservationSystemUtil.convertAllocationsToReservationInfo(Collections.<ReservationAllocation>emptySet(), false);
        Assert.assertEquals(infoList.size(), 0);
    }
}

