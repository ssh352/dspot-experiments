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
package com.hazelcast.instance;


import GroupProperty.PREFER_IPv4_STACK;
import com.hazelcast.config.Config;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Preconditions;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests if the {@link DefaultAddressPicker} chooses an expected bind address.
 * <p>
 * This class contains PowerMock driven tests which emulate different NetworkInterfaces configurations.
 * <p>
 * Given: The default Hazelcast Config is used and no Interface definition network configuration is set.
 * The System property {@link DefaultAddressPicker#PREFER_IPV4_STACK} is set to {@code true}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultAddressPicker.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultAddressPickerInterfacesTest {
    private final ILogger logger = Logger.getLogger(AddressPicker.class);

    private final Config config = new Config();

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv4 = OverridePropertyRule.set(DefaultAddressPicker.PREFER_IPV4_STACK, "true");

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv6 = OverridePropertyRule.clear(DefaultAddressPicker.PREFER_IPV6_ADDRESSES);

    @Rule
    public final OverridePropertyRule ruleSysPropHzPreferIpv4 = OverridePropertyRule.set(PREFER_IPv4_STACK.getName(), "false");

    /**
     * When: First network interface is a loopback and the other is a non-loopback.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testLoopbackFirst() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("lo").withLoopback(true).withAddresses("127.0.0.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: Last network interface is a loopback and the other is a non-loopback.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testLoopbackLast() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("lo").withLoopback(true).withAddresses("127.0.0.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: First network interface is DOWN and the other is UP.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testInterfaceDownFirst() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("docker").withUp(false).withAddresses("172.17.0.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: Last network interface is DOWN and the other is UP.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testInterfaceDownLast() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("docker").withUp(false).withAddresses("172.17.0.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: First network interface is virtual and the other is not virtual.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testInterfaceVirtualFirst() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0:0").withVirtual(true).withAddresses("172.17.0.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: Last network interface is virtual and the other is not virtual.<br/>
     * Then: The other interface will be used for address picking.
     */
    @Test
    public void testInterfaceVirtualLast() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0:0").withVirtual(true).withAddresses("172.17.0.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: No network interface is provided.<br/>
     * Then: The address picker returns {@code null} as the picked address.
     */
    @Test
    public void testNoInterface() throws Exception {
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(Collections.<NetworkInterface>emptyList()));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNull("Null InetAddress is expected when NetworkInterface enumeration is empty", inetAddress);
    }

    /**
     * When: There is no available interface/address combination for picking.<br/>
     * Then: The address picker returns {@code null} as the picked address.
     */
    @Test
    public void testNoAddress() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses()));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNull("Null InetAddress is expected when the available NetworkInterface has no address", inetAddress);
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("docker").withUp(false).withAddresses("172.17.0.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNull("Null InetAddress is expected when the available NetworkInterface has no address", inetAddress);
    }

    /**
     * When: Fist network interface is valid for picking, but has no InetAddress.<br/>
     * Then: Another interface will be used for address picking.
     */
    @Test
    public void testNoAddressFirst() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses()));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth1").withAddresses("192.168.1.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: Last network interface is valid for picking, but has no InetAddress.<br/>
     * Then: Another interface will be used for address picking.
     */
    @Test
    public void testNoAddressLast() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth1").withAddresses("192.168.1.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses()));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: A valid Network interface has more addresses.<br/>
     * Then: One of the addresses is picked.
     */
    @Test
    public void testMoreAddresses() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("lo").withLoopback(true).withAddresses("127.0.0.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("192.168.1.1", "172.172.172.172")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertThat(inetAddress.getHostAddress(), CoreMatchers.anyOf(CoreMatchers.equalTo("192.168.1.1"), CoreMatchers.equalTo("172.172.172.172")));
    }

    /**
     * When: Network interface has both IPv4 and IPv6 addresses and IPv4 is preferred.<br/>
     * Then: The IPv4 address is picked.
     */
    @Test
    public void testIPv4Preferred() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("fe80::9711:82f4:383a:e254", "192.168.1.1", "::cace")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.1", inetAddress.getHostAddress());
    }

    /**
     * When: Network interface has both IPv4 and IPv6 addresses and IPv6 is preferred.<br/>
     * Then: The IPv6 address is picked.
     */
    @Test
    public void testIPv6Preferred() throws Exception {
        TestUtil.setSystemProperty(DefaultAddressPicker.PREFER_IPV4_STACK, "false");
        TestUtil.setSystemProperty(DefaultAddressPicker.PREFER_IPV6_ADDRESSES, "true");
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0").withAddresses("fe80::9711:82f4:383a:e254", "172.17.0.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("fe80:0:0:0:9711:82f4:383a:e254", inetAddress.getHostAddress());
    }

    /**
     * When: Multiple interfaces with different configuration is used, but only one IPv4 interface/address combination is
     * pickable.<br/>
     * Then: The correct address is picked.
     */
    @Test
    public void testComplexScenario() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("lo").withLoopback(true).withAddresses("127.0.0.1", "::1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("docker0").withUp(false).withAddresses("172.17.0.1")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("wlp3s0").withUp(false)));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("eth0:0").withVirtual(true).withAddresses("8.8.8.8", "8.8.4.4")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("enp0s25").withAddresses("fe80::9711:82f4:383a:e254", "192.168.1.4")));
        networkInterfaces.add(createNetworkConfig(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.builder().withName("virbr1").withUp(false).withAddresses("192.168.42.1")));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        InetAddress inetAddress = getInetAddressFromDefaultAddressPicker();
        Assert.assertNotNull("Not-null InetAddress is expected", inetAddress);
        Assert.assertEquals("192.168.1.4", inetAddress.getHostAddress());
    }

    /**
     * Configuration object for {@link NetworkInterface} mocking.
     */
    public static class NetworkInterfaceOptions {
        private final String name;

        private final boolean up;

        private final boolean loopback;

        private final boolean virtual;

        private final String[] addresses;

        private NetworkInterfaceOptions(DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder builder) {
            this.name = Preconditions.checkNotNull(builder.name);
            this.up = builder.up;
            this.loopback = builder.loopback;
            this.virtual = builder.virtual;
            this.addresses = Preconditions.checkNotNull(builder.addresses);
        }

        /**
         * Creates builder to build {@link NetworkInterfaceOptions}.
         *
         * @return created builder
         */
        public static DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder builder() {
            return new DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder();
        }

        /**
         * Builder to build {@link NetworkInterfaceOptions}.
         */
        @SuppressWarnings("SameParameterValue")
        public static final class Builder {
            private String name;

            private boolean up = true;

            private boolean loopback = false;

            private boolean virtual = false;

            private String[] addresses = new String[]{  };

            private Builder() {
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder withName(String name) {
                this.name = name;
                return this;
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder withUp(boolean up) {
                this.up = up;
                return this;
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder withLoopback(boolean loopback) {
                this.loopback = loopback;
                return this;
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder withVirtual(boolean virtual) {
                this.virtual = virtual;
                return this;
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions.Builder withAddresses(String... addresses) {
                this.addresses = addresses;
                return this;
            }

            DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions build() {
                return new DefaultAddressPickerInterfacesTest.NetworkInterfaceOptions(this);
            }
        }
    }
}
