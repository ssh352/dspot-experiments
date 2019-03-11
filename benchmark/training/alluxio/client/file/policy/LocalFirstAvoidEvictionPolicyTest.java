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
package alluxio.client.file.policy;


import Constants.GB;
import Constants.MB;
import alluxio.ConfigurationTestUtils;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.conf.InstancedConfiguration;
import alluxio.network.TieredIdentityFactory;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.WorkerNetAddress;
import com.google.common.testing.EqualsTester;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link LocalFirstAvoidEvictionPolicy}. The class delegates to {@link LocalFirstPolicy}, so
 * most of its functionality is tested in {@link LocalFirstPolicyTest}.
 */
public class LocalFirstAvoidEvictionPolicyTest {
    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Test
    public void chooseClosestTierAvoidEviction() throws Exception {
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, MB, "node2", "rack3"));
        workers.add(worker(GB, 0, "node3", "rack2"));
        workers.add(worker(GB, 0, "node4", "rack3"));
        FileWriteLocationPolicy policy;
        WorkerNetAddress chosen;
        // local rack with enough availability
        policy = new LocalFirstAvoidEvictionPolicy(TieredIdentityFactory.fromString("node=node2,rack=rack3", mConf), mConf);
        chosen = policy.getWorkerForNextBlock(workers, GB);
        Assert.assertEquals("node4", chosen.getTieredIdentity().getTier(0).getValue());
    }

    /**
     * Tests that another worker is picked in case the local host does not have enough availability.
     */
    @Test
    public void getOthersWhenNotEnoughAvailabilityOnLocal() {
        String localhostName = NetworkAddressUtils.getLocalHostName(1000);
        FileWriteLocationPolicy policy = new LocalFirstAvoidEvictionPolicy(mConf);
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, 0, "worker1", ""));
        workers.add(worker(MB, MB, localhostName, ""));
        Assert.assertEquals("worker1", policy.getWorkerForNextBlock(workers, MB).getHost());
    }

    /**
     * Tests that local host is picked if none of the workers has enough availability.
     */
    @Test
    public void getLocalWhenNoneHasAvailability() {
        String localhostName = NetworkAddressUtils.getLocalHostName(1000);
        FileWriteLocationPolicy policy = new LocalFirstAvoidEvictionPolicy(mConf);
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, MB, "worker1", ""));
        workers.add(worker(GB, MB, localhostName, ""));
        Assert.assertEquals(localhostName, policy.getWorkerForNextBlock(workers, GB).getHost());
    }

    @Test
    public void equalsTest() throws Exception {
        new EqualsTester().addEqualityGroup(new LocalFirstAvoidEvictionPolicy(mConf), new LocalFirstAvoidEvictionPolicy(mConf)).testEquals();
    }
}

