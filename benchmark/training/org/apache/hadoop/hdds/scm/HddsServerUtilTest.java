/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm;


import ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY;
import ScmConfigKeys.OZONE_SCM_CLIENT_BIND_HOST_KEY;
import ScmConfigKeys.OZONE_SCM_CLIENT_PORT_DEFAULT;
import ScmConfigKeys.OZONE_SCM_DATANODE_ADDRESS_KEY;
import ScmConfigKeys.OZONE_SCM_DATANODE_BIND_HOST_KEY;
import ScmConfigKeys.OZONE_SCM_DATANODE_PORT_DEFAULT;
import ScmConfigKeys.OZONE_SCM_DEFAULT_PORT;
import ScmConfigKeys.OZONE_SCM_NAMES;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.HddsUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


/**
 * Test the HDDS server side utilities.
 */
public class HddsServerUtilTest {
    @Rule
    public Timeout timeout = new Timeout(300000);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Verify DataNode endpoint lookup failure if neither the client nor
     * datanode endpoint are configured.
     */
    @Test
    public void testMissingScmDataNodeAddress() {
        final Configuration conf = new OzoneConfiguration();
        thrown.expect(IllegalArgumentException.class);
        HddsServerUtil.getScmAddressForDataNodes(conf);
    }

    /**
     * Verify that the datanode endpoint is parsed correctly.
     * This tests the logic used by the DataNodes to determine which address
     * to connect to.
     */
    @Test
    public void testGetScmDataNodeAddress() {
        final Configuration conf = new OzoneConfiguration();
        // First try a client address with just a host name. Verify it falls
        // back to the default port.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4");
        InetSocketAddress addr = HddsServerUtil.getScmAddressForDataNodes(conf);
        Assert.assertThat(addr.getHostString(), Is.is("1.2.3.4"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DATANODE_PORT_DEFAULT));
        // Next try a client address with just a host name and port.
        // Verify the port is ignored and the default DataNode port is used.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        addr = HddsServerUtil.getScmAddressForDataNodes(conf);
        Assert.assertThat(addr.getHostString(), Is.is("1.2.3.4"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DATANODE_PORT_DEFAULT));
        // Set both OZONE_SCM_CLIENT_ADDRESS_KEY and
        // OZONE_SCM_DATANODE_ADDRESS_KEY.
        // Verify that the latter overrides and the port number is still the
        // default.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "5.6.7.8");
        addr = HddsServerUtil.getScmAddressForDataNodes(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DATANODE_PORT_DEFAULT));
        // Set both OZONE_SCM_CLIENT_ADDRESS_KEY and
        // OZONE_SCM_DATANODE_ADDRESS_KEY.
        // Verify that the latter overrides and the port number from the latter is
        // used.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "5.6.7.8:200");
        addr = HddsServerUtil.getScmAddressForDataNodes(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(200));
    }

    /**
     * Verify that the client endpoint bind address is computed correctly.
     * This tests the logic used by the SCM to determine its own bind address.
     */
    @Test
    public void testScmClientBindHostDefault() {
        final Configuration conf = new OzoneConfiguration();
        // The bind host should be 0.0.0.0 unless OZONE_SCM_CLIENT_BIND_HOST_KEY
        // is set differently.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4");
        InetSocketAddress addr = HddsServerUtil.getScmClientBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("0.0.0.0"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_CLIENT_PORT_DEFAULT));
        // The bind host should be 0.0.0.0 unless OZONE_SCM_CLIENT_BIND_HOST_KEY
        // is set differently. The port number from OZONE_SCM_CLIENT_ADDRESS_KEY
        // should be respected.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4:200");
        addr = HddsServerUtil.getScmClientBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("0.0.0.0"));
        Assert.assertThat(addr.getPort(), Is.is(100));
        // OZONE_SCM_CLIENT_BIND_HOST_KEY should be respected.
        // Port number should be default if none is specified via
        // OZONE_SCM_DATANODE_ADDRESS_KEY.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4");
        conf.set(OZONE_SCM_CLIENT_BIND_HOST_KEY, "5.6.7.8");
        addr = HddsServerUtil.getScmClientBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_CLIENT_PORT_DEFAULT));
        // OZONE_SCM_CLIENT_BIND_HOST_KEY should be respected.
        // Port number from OZONE_SCM_CLIENT_ADDRESS_KEY should be
        // respected.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4:200");
        conf.set(OZONE_SCM_CLIENT_BIND_HOST_KEY, "5.6.7.8");
        addr = HddsServerUtil.getScmClientBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(100));
    }

    /**
     * Verify that the DataNode endpoint bind address is computed correctly.
     * This tests the logic used by the SCM to determine its own bind address.
     */
    @Test
    public void testScmDataNodeBindHostDefault() {
        final Configuration conf = new OzoneConfiguration();
        // The bind host should be 0.0.0.0 unless OZONE_SCM_DATANODE_BIND_HOST_KEY
        // is set differently.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4");
        InetSocketAddress addr = HddsServerUtil.getScmDataNodeBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("0.0.0.0"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DATANODE_PORT_DEFAULT));
        // The bind host should be 0.0.0.0 unless OZONE_SCM_DATANODE_BIND_HOST_KEY
        // is set differently. The port number from OZONE_SCM_DATANODE_ADDRESS_KEY
        // should be respected.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4:200");
        addr = HddsServerUtil.getScmDataNodeBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("0.0.0.0"));
        Assert.assertThat(addr.getPort(), Is.is(200));
        // OZONE_SCM_DATANODE_BIND_HOST_KEY should be respected.
        // Port number should be default if none is specified via
        // OZONE_SCM_DATANODE_ADDRESS_KEY.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4");
        conf.set(OZONE_SCM_DATANODE_BIND_HOST_KEY, "5.6.7.8");
        addr = HddsServerUtil.getScmDataNodeBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DATANODE_PORT_DEFAULT));
        // OZONE_SCM_DATANODE_BIND_HOST_KEY should be respected.
        // Port number from OZONE_SCM_DATANODE_ADDRESS_KEY should be
        // respected.
        conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, "1.2.3.4:100");
        conf.set(OZONE_SCM_DATANODE_ADDRESS_KEY, "1.2.3.4:200");
        conf.set(OZONE_SCM_DATANODE_BIND_HOST_KEY, "5.6.7.8");
        addr = HddsServerUtil.getScmDataNodeBindAddress(conf);
        Assert.assertThat(addr.getHostString(), Is.is("5.6.7.8"));
        Assert.assertThat(addr.getPort(), Is.is(200));
    }

    @Test
    public void testGetSCMAddresses() {
        final Configuration conf = new OzoneConfiguration();
        Collection<InetSocketAddress> addresses = null;
        InetSocketAddress addr = null;
        Iterator<InetSocketAddress> it = null;
        // Verify valid IP address setup
        conf.setStrings(OZONE_SCM_NAMES, "1.2.3.4");
        addresses = HddsUtils.getSCMAddresses(conf);
        Assert.assertThat(addresses.size(), Is.is(1));
        addr = addresses.iterator().next();
        Assert.assertThat(addr.getHostName(), Is.is("1.2.3.4"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DEFAULT_PORT));
        // Verify valid hostname setup
        conf.setStrings(OZONE_SCM_NAMES, "scm1");
        addresses = HddsUtils.getSCMAddresses(conf);
        Assert.assertThat(addresses.size(), Is.is(1));
        addr = addresses.iterator().next();
        Assert.assertThat(addr.getHostName(), Is.is("scm1"));
        Assert.assertThat(addr.getPort(), Is.is(OZONE_SCM_DEFAULT_PORT));
        // Verify valid hostname and port
        conf.setStrings(OZONE_SCM_NAMES, "scm1:1234");
        addresses = HddsUtils.getSCMAddresses(conf);
        Assert.assertThat(addresses.size(), Is.is(1));
        addr = addresses.iterator().next();
        Assert.assertThat(addr.getHostName(), Is.is("scm1"));
        Assert.assertThat(addr.getPort(), Is.is(1234));
        final HashMap<String, Integer> hostsAndPorts = new HashMap<String, Integer>();
        hostsAndPorts.put("scm1", 1234);
        hostsAndPorts.put("scm2", 2345);
        hostsAndPorts.put("scm3", 3456);
        // Verify multiple hosts and port
        conf.setStrings(OZONE_SCM_NAMES, "scm1:1234,scm2:2345,scm3:3456");
        addresses = HddsUtils.getSCMAddresses(conf);
        Assert.assertThat(addresses.size(), Is.is(3));
        it = addresses.iterator();
        HashMap<String, Integer> expected1 = new HashMap<>(hostsAndPorts);
        while (it.hasNext()) {
            InetSocketAddress current = it.next();
            Assert.assertTrue(expected1.remove(current.getHostName(), current.getPort()));
        } 
        Assert.assertTrue(expected1.isEmpty());
        // Verify names with spaces
        conf.setStrings(OZONE_SCM_NAMES, " scm1:1234, scm2:2345 , scm3:3456 ");
        addresses = HddsUtils.getSCMAddresses(conf);
        Assert.assertThat(addresses.size(), Is.is(3));
        it = addresses.iterator();
        HashMap<String, Integer> expected2 = new HashMap<>(hostsAndPorts);
        while (it.hasNext()) {
            InetSocketAddress current = it.next();
            Assert.assertTrue(expected2.remove(current.getHostName(), current.getPort()));
        } 
        Assert.assertTrue(expected2.isEmpty());
        // Verify empty value
        conf.setStrings(OZONE_SCM_NAMES, "");
        try {
            addresses = HddsUtils.getSCMAddresses(conf);
            Assert.fail("Empty value should cause an IllegalArgumentException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        // Verify invalid hostname
        conf.setStrings(OZONE_SCM_NAMES, "s..x..:1234");
        try {
            addresses = HddsUtils.getSCMAddresses(conf);
            Assert.fail("An invalid hostname should cause an IllegalArgumentException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        // Verify invalid port
        conf.setStrings(OZONE_SCM_NAMES, "scm:xyz");
        try {
            addresses = HddsUtils.getSCMAddresses(conf);
            Assert.fail("An invalid port should cause an IllegalArgumentException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        // Verify a mixed case (valid and invalid value both appears)
        conf.setStrings(OZONE_SCM_NAMES, "scm1:1234, scm:xyz");
        try {
            addresses = HddsUtils.getSCMAddresses(conf);
            Assert.fail("An invalid value should cause an IllegalArgumentException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }
}

