package com.iota.iri.conf;


import BaseIotaConfig.Defaults.REMOTE_LIMIT_API_DEFAULT_HOST;
import HashFactory.ADDRESS;
import com.iota.iri.utils.IotaUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigTest {
    private static File configFile;

    /* Test that iterates over common configs. It also attempts to check different types of types (double, boolean, string) */
    @Test
    public void testArgsParsingMainnet() throws UnknownHostException {
        String[] args = new String[]{ "-p", "14000", "-u", "13000", "-t", "27000", "-n", "udp://neighbor1 neighbor, tcp://neighbor2", "--api-host", "1.1.1.1", "--remote-limit-api", "call1 call2, call3", "--remote-trusted-api-hosts", "192.168.0.55, 10.0.0.10", "--max-find-transactions", "500", "--max-requests-list", "1000", "--max-get-trytes", "4000", "--max-body-length", "220", "--remote-auth", "2.2.2.2", "--p-remove-request", "0.23", "--send-limit", "1000", "--max-peers", "10", "--dns-refresher", "false", "--dns-resolution", "false", "--ixi-dir", "/ixi", "--db-path", "/db", "--db-log-path", "/dblog", "--zmq-enabled", // we ignore this on mainnet
        "--mwm", "4", "--testnet-coordinator", "TTTTTTTTT", "--test-no-coo-validation", // this should be ignored everywhere
        "--fake-config" };
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(false);
        Assert.assertThat("wrong config class created", iotaConfig, CoreMatchers.instanceOf(MainnetConfig.class));
        iotaConfig.parseConfigFromArgs(args);
        Assert.assertEquals("port value", 14000, iotaConfig.getPort());
        Assert.assertEquals("udp port", 13000, iotaConfig.getUdpReceiverPort());
        Assert.assertEquals("tcp port", 27000, iotaConfig.getTcpReceiverPort());
        Assert.assertEquals("neighbors", Arrays.asList("udp://neighbor1", "neighbor", "tcp://neighbor2"), iotaConfig.getNeighbors());
        Assert.assertEquals("api host", "1.1.1.1", iotaConfig.getApiHost());
        Assert.assertEquals("remote limit api", Arrays.asList("call1", "call2", "call3"), iotaConfig.getRemoteLimitApi());
        List<InetAddress> expectedTrustedApiHosts = Arrays.asList(InetAddress.getByName("192.168.0.55"), InetAddress.getByName("10.0.0.10"), InetAddress.getByName("127.0.0.1"));
        Assert.assertEquals("remote trusted api hosts", expectedTrustedApiHosts, iotaConfig.getRemoteTrustedApiHosts());
        Assert.assertEquals("max find transactions", 500, iotaConfig.getMaxFindTransactions());
        Assert.assertEquals("max requests list", 1000, iotaConfig.getMaxRequestsList());
        Assert.assertEquals("max get trytes", 4000, iotaConfig.getMaxGetTrytes());
        Assert.assertEquals("max body length", 220, iotaConfig.getMaxBodyLength());
        Assert.assertEquals("remote-auth", "2.2.2.2", iotaConfig.getRemoteAuth());
        Assert.assertEquals("p remove request", 0.23, iotaConfig.getpRemoveRequest(), 0.0);
        Assert.assertEquals("send limit", 1000, iotaConfig.getSendLimit());
        Assert.assertEquals("max peers", 10, iotaConfig.getMaxPeers());
        Assert.assertEquals("dns refresher", false, iotaConfig.isDnsRefresherEnabled());
        Assert.assertEquals("dns resolution", false, iotaConfig.isDnsResolutionEnabled());
        Assert.assertEquals("tip solidification", true, iotaConfig.isTipSolidifierEnabled());
        Assert.assertEquals("ixi-dir", "/ixi", iotaConfig.getIxiDir());
        Assert.assertEquals("db path", "/db", iotaConfig.getDbPath());
        Assert.assertEquals("zmq enabled", true, iotaConfig.isZmqEnabled());
        Assert.assertNotEquals("mwm", 4, iotaConfig.getMwm());
        Assert.assertNotEquals("coo", iotaConfig.getCoordinator(), "TTTTTTTTT");
        Assert.assertEquals("--testnet-no-coo-validation", false, iotaConfig.isDontValidateTestnetMilestoneSig());
        // Test default value
        Assert.assertEquals("--local-snapshots-pruning-delay", 40000, iotaConfig.getLocalSnapshotsPruningDelay());
    }

    @Test
    public void testRemoteFlag() {
        String[] args = new String[]{ "--remote" };
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(false);
        iotaConfig.parseConfigFromArgs(args);
        Assert.assertEquals("The api interface should be open to the public", "0.0.0.0", iotaConfig.getApiHost());
    }

    @Test
    public void testArgsParsingTestnet() {
        String[] args = new String[]{ "-p", "14000", "-u", "13000", "-t", "27000", "-n", "udp://neighbor1 neighbor, tcp://neighbor2", "--api-host", "1.1.1.1", "--remote-limit-api", "call1 call2, call3", "--max-find-transactions", "500", "--max-requests-list", "1000", "--max-get-trytes", "4000", "--max-body-length", "220", "--remote-auth", "2.2.2.2", "--p-remove-request", "0.23", "--send-limit", "1000", "--max-peers", "10", "--dns-refresher", "false", "--dns-resolution", "false", "--tip-solidifier", "false", "--ixi-dir", "/ixi", "--db-path", "/db", "--db-log-path", "/dblog", "--zmq-enabled", // we ignore this on mainnet
        "--mwm", "4", "--testnet-coordinator", "TTTTTTTTT", "--testnet-no-coo-validation", // this should be ignored everywhere
        "--fake-config" };
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(true);
        Assert.assertThat("wrong config class created", iotaConfig, CoreMatchers.instanceOf(TestnetConfig.class));
        iotaConfig.parseConfigFromArgs(args);
        Assert.assertEquals("port value", 14000, iotaConfig.getPort());
        Assert.assertEquals("udp port", 13000, iotaConfig.getUdpReceiverPort());
        Assert.assertEquals("tcp port", 27000, iotaConfig.getTcpReceiverPort());
        Assert.assertEquals("neighbors", Arrays.asList("udp://neighbor1", "neighbor", "tcp://neighbor2"), iotaConfig.getNeighbors());
        Assert.assertEquals("api host", "1.1.1.1", iotaConfig.getApiHost());
        Assert.assertEquals("remote limit api", Arrays.asList("call1", "call2", "call3"), iotaConfig.getRemoteLimitApi());
        Assert.assertEquals("max find transactions", 500, iotaConfig.getMaxFindTransactions());
        Assert.assertEquals("max requests list", 1000, iotaConfig.getMaxRequestsList());
        Assert.assertEquals("max get trytes", 4000, iotaConfig.getMaxGetTrytes());
        Assert.assertEquals("max body length", 220, iotaConfig.getMaxBodyLength());
        Assert.assertEquals("remote-auth", "2.2.2.2", iotaConfig.getRemoteAuth());
        Assert.assertEquals("p remove request", 0.23, iotaConfig.getpRemoveRequest(), 0.0);
        Assert.assertEquals("send limit", 1000, iotaConfig.getSendLimit());
        Assert.assertEquals("max peers", 10, iotaConfig.getMaxPeers());
        Assert.assertEquals("dns refresher", false, iotaConfig.isDnsRefresherEnabled());
        Assert.assertEquals("dns resolution", false, iotaConfig.isDnsResolutionEnabled());
        Assert.assertEquals("tip solidification", false, iotaConfig.isTipSolidifierEnabled());
        Assert.assertEquals("ixi-dir", "/ixi", iotaConfig.getIxiDir());
        Assert.assertEquals("db path", "/db", iotaConfig.getDbPath());
        Assert.assertEquals("zmq enabled", true, iotaConfig.isZmqEnabled());
        Assert.assertEquals("mwm", 4, iotaConfig.getMwm());
        Assert.assertEquals("coo", ADDRESS.create("TTTTTTTTT"), iotaConfig.getCoordinator());
        Assert.assertEquals("--testnet-no-coo-validation", true, iotaConfig.isDontValidateTestnetMilestoneSig());
    }

    @Test
    public void testIniParsingMainnet() throws Exception {
        String iniContent = new StringBuilder().append("[IRI]").append(System.lineSeparator()).append("PORT = 17000").append(System.lineSeparator()).append("NEIGHBORS = udp://neighbor1 neighbor, tcp://neighbor2").append(System.lineSeparator()).append("REMOTE_TRUSTED_API_HOSTS = 192.168.0.55, 10.0.0.10").append(System.lineSeparator()).append("ZMQ_ENABLED = true").append(System.lineSeparator()).append("P_REMOVE_REQUEST = 0.4").append(System.lineSeparator()).append("MWM = 4").append(System.lineSeparator()).append("FAKE").append(System.lineSeparator()).append("FAKE2 = lies").toString();
        try (Writer writer = new FileWriter(ConfigTest.configFile)) {
            writer.write(iniContent);
        }
        IotaConfig iotaConfig = ConfigFactory.createFromFile(ConfigTest.configFile, false);
        Assert.assertThat("Wrong config class created", iotaConfig, CoreMatchers.instanceOf(MainnetConfig.class));
        Assert.assertEquals("PORT", 17000, iotaConfig.getPort());
        Assert.assertEquals("NEIGHBORS", Arrays.asList("udp://neighbor1", "neighbor", "tcp://neighbor2"), iotaConfig.getNeighbors());
        List<InetAddress> expectedTrustedApiHosts = Arrays.asList(InetAddress.getByName("192.168.0.55"), InetAddress.getByName("10.0.0.10"), REMOTE_LIMIT_API_DEFAULT_HOST);
        Assert.assertEquals("REMOTE_TRUSTED_API_HOSTS", expectedTrustedApiHosts, iotaConfig.getRemoteTrustedApiHosts());
        Assert.assertEquals("ZMQ_ENABLED", true, iotaConfig.isZmqEnabled());
        Assert.assertEquals("P_REMOVE_REQUEST", 0.4, iotaConfig.getpRemoveRequest(), 0);
        Assert.assertNotEquals("MWM", 4, iotaConfig.getMwm());
    }

    @Test
    public void testIniParsingTestnet() throws Exception {
        String iniContent = // doesn't do anything
        new StringBuilder().append("[IRI]").append(System.lineSeparator()).append("PORT = 17000").append(System.lineSeparator()).append("NEIGHBORS = udp://neighbor1 neighbor, tcp://neighbor2").append(System.lineSeparator()).append("ZMQ_ENABLED = true").append(System.lineSeparator()).append("DNS_RESOLUTION_ENABLED = TRUE").append(System.lineSeparator()).append("P_REMOVE_REQUEST = 0.4").append(System.lineSeparator()).append("MWM = 4").append(System.lineSeparator()).append("NUMBER_OF_KEYS_IN_A_MILESTONE = 3").append(System.lineSeparator()).append("DONT_VALIDATE_TESTNET_MILESTONE_SIG = true").append(System.lineSeparator()).append("TIPSELECTION_ALPHA = 1.1").append(System.lineSeparator()).append("REMOTE").append("FAKE").append(System.lineSeparator()).append("FAKE2 = lies").toString();
        try (Writer writer = new FileWriter(ConfigTest.configFile)) {
            writer.write(iniContent);
        }
        IotaConfig iotaConfig = ConfigFactory.createFromFile(ConfigTest.configFile, true);
        Assert.assertThat("Wrong config class created", iotaConfig, CoreMatchers.instanceOf(TestnetConfig.class));
        Assert.assertEquals("PORT", 17000, iotaConfig.getPort());
        Assert.assertEquals("NEIGHBORS", Arrays.asList("udp://neighbor1", "neighbor", "tcp://neighbor2"), iotaConfig.getNeighbors());
        Assert.assertEquals("ZMQ_ENABLED", true, iotaConfig.isZmqEnabled());
        Assert.assertEquals("DNS_RESOLUTION_ENABLED", true, iotaConfig.isDnsResolutionEnabled());
        // true by default
        Assert.assertEquals("DNS_REFRESHER_ENABLED", true, iotaConfig.isDnsRefresherEnabled());
        // false by default
        Assert.assertEquals("RESCAN", false, iotaConfig.isRescanDb());
        // false by default
        Assert.assertEquals("REVALIDATE", false, iotaConfig.isRevalidate());
        Assert.assertEquals("P_REMOVE_REQUEST", 0.4, iotaConfig.getpRemoveRequest(), 0);
        Assert.assertEquals("MWM", 4, iotaConfig.getMwm());
        Assert.assertEquals("NUMBER_OF_KEYS_IN_A_MILESTONE", 3, iotaConfig.getNumberOfKeysInMilestone());
        Assert.assertEquals("TIPSELECTION_ALPHA", 1.1, iotaConfig.getAlpha(), 0);
        Assert.assertEquals("DONT_VALIDATE_TESTNET_MILESTONE_SIG", iotaConfig.isDontValidateTestnetMilestoneSig(), true);
        // prove that REMOTE did nothing
        Assert.assertEquals("API_HOST", iotaConfig.getApiHost(), "localhost");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIni() throws IOException {
        String iniContent = new StringBuilder().append("[IRI]").append(System.lineSeparator()).append("REVALIDATE").toString();
        try (Writer writer = new FileWriter(ConfigTest.configFile)) {
            writer.write(iniContent);
        }
        ConfigFactory.createFromFile(ConfigTest.configFile, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pruningSnapshotDelayBelowMin() throws IOException {
        String iniContent = new StringBuilder().append("[IRI]").append(System.lineSeparator()).append("LOCAL_SNAPSHOTS_PRUNING_DELAY = 9999").toString();
        try (Writer writer = new FileWriter(ConfigTest.configFile)) {
            writer.write(iniContent);
        }
        ConfigFactory.createFromFile(ConfigTest.configFile, false);
    }

    @Test
    public void pruningSnapshotDelayIsMin() throws IOException {
        String iniContent = new StringBuilder().append("[IRI]").append(System.lineSeparator()).append("LOCAL_SNAPSHOTS_PRUNING_DELAY = 10000").toString();
        try (Writer writer = new FileWriter(ConfigTest.configFile)) {
            writer.write(iniContent);
        }
        IotaConfig iotaConfig = ConfigFactory.createFromFile(ConfigTest.configFile, false);
        Assert.assertEquals("unexpected pruning delay", 10000, iotaConfig.getLocalSnapshotsPruningDelay());
    }

    @Test
    public void backwardsIniCompatibilityTest() {
        Collection<String> configNames = IotaUtils.getAllSetters(TestnetConfig.class).stream().map(this::deriveNameFromSetter).collect(Collectors.toList());
        // make it explicit that we have removed some configs
        Stream.of(ConfigTest.LegacyDefaultConf.values()).map(Enum::name).filter(( config) -> !(ArrayUtils.contains(new String[]{ "CONFIG", "TESTNET", "DEBUG", "MIN_RANDOM_WALKS", "MAX_RANDOM_WALKS" }, config))).forEach(( config) -> Assert.assertThat(configNames, IsCollectionContaining.hasItem(config)));
    }

    @Test
    public void testDontValidateMIlestoneSigDefaultValue() {
        IotaConfig iotaConfig = ConfigFactory.createIotaConfig(true);
        Assert.assertFalse("By default testnet should be validating milestones", iotaConfig.isDontValidateTestnetMilestoneSig());
    }

    public enum LegacyDefaultConf {

        CONFIG,
        PORT,
        API_HOST,
        UDP_RECEIVER_PORT,
        TCP_RECEIVER_PORT,
        TESTNET,
        DEBUG,
        REMOTE_LIMIT_API,
        REMOTE_AUTH,
        NEIGHBORS,
        IXI_DIR,
        DB_PATH,
        DB_LOG_PATH,
        DB_CACHE_SIZE,
        P_REMOVE_REQUEST,
        P_DROP_TRANSACTION,
        P_SELECT_MILESTONE_CHILD,
        P_SEND_MILESTONE,
        P_REPLY_RANDOM_TIP,
        P_PROPAGATE_REQUEST,
        MAIN_DB,
        SEND_LIMIT,
        MAX_PEERS,
        DNS_RESOLUTION_ENABLED,
        DNS_REFRESHER_ENABLED,
        COORDINATOR,
        DONT_VALIDATE_TESTNET_MILESTONE_SIG,
        REVALIDATE,
        RESCAN_DB,
        MIN_RANDOM_WALKS,
        MAX_RANDOM_WALKS,
        MAX_FIND_TRANSACTIONS,
        MAX_REQUESTS_LIST,
        MAX_GET_TRYTES,
        MAX_BODY_LENGTH,
        MAX_DEPTH,
        MWM,
        ZMQ_ENABLED,
        ZMQ_PORT,
        ZMQ_IPC,
        ZMQ_THREADS,
        Q_SIZE_NODE,
        P_DROP_CACHE_ENTRY,
        CACHE_SIZE_BYTES,
        SNAPSHOT_FILE,
        SNAPSHOT_SIGNATURE_FILE,
        MILESTONE_START_INDEX,
        NUMBER_OF_KEYS_IN_A_MILESTONE,
        TRANSACTION_PACKET_SIZE,
        REQUEST_HASH_SIZE,
        SNAPSHOT_TIME,
        TIPSELECTION_ALPHA,
        BELOW_MAX_DEPTH_TRANSACTION_LIMIT;}
}

