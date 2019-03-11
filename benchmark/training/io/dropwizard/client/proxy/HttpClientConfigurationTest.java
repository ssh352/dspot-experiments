package io.dropwizard.client.proxy;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.configuration.ConfigurationParsingException;
import io.dropwizard.jackson.Jackson;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;


public class HttpClientConfigurationTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    private HttpClientConfiguration configuration = new HttpClientConfiguration();

    @Test
    public void testNoProxy() throws Exception {
        load("./yaml/no_proxy.yml");
        assertThat(configuration.getProxyConfiguration()).isNull();
    }

    @Test
    public void testFullConfigBasicProxy() throws Exception {
        load("yaml/proxy.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("https");
        AuthConfiguration auth = Objects.requireNonNull(proxy.getAuth());
        assertThat(auth.getUsername()).isEqualTo("secret");
        assertThat(auth.getPassword()).isEqualTo("stuff");
        List<String> nonProxyHosts = proxy.getNonProxyHosts();
        assertThat(nonProxyHosts).contains("localhost", "192.168.52.*", "*.example.com");
    }

    @Test
    public void testFullConfigNtlmProxy() throws Exception {
        load("yaml/proxy_ntlm.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("https");
        AuthConfiguration auth = Objects.requireNonNull(proxy.getAuth());
        assertThat(auth.getUsername()).isEqualTo("secret");
        assertThat(auth.getPassword()).isEqualTo("stuff");
        assertThat(auth.getAuthScheme()).isEqualTo("NTLM");
        assertThat(auth.getRealm()).isEqualTo("realm");
        assertThat(auth.getHostname()).isEqualTo("workstation");
        assertThat(auth.getDomain()).isEqualTo("HYPERCOMPUGLOBALMEGANET");
        assertThat(auth.getCredentialType()).isEqualTo("NT");
        List<String> nonProxyHosts = proxy.getNonProxyHosts();
        assertThat(nonProxyHosts).contains("localhost", "192.168.52.*", "*.example.com");
    }

    @Test
    public void testNoScheme() throws Exception {
        load("./yaml/no_scheme.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("http");
    }

    @Test
    public void testNoAuth() throws Exception {
        load("./yaml/no_auth.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getHost()).isNotNull();
        assertThat(proxy.getAuth()).isNull();
    }

    @Test
    public void testNoPort() throws Exception {
        load("./yaml/no_port.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getHost()).isNotNull();
        assertThat(proxy.getPort()).isEqualTo((-1));
    }

    @Test
    public void testNoNonProxy() throws Exception {
        load("./yaml/no_port.yml");
        ProxyConfiguration proxy = Objects.requireNonNull(configuration.getProxyConfiguration());
        assertThat(proxy.getNonProxyHosts()).isNull();
    }

    @Test
    public void testNoHost() {
        assertConfigurationValidationException("yaml/bad_host.yml");
    }

    @Test
    public void testBadPort() {
        assertConfigurationValidationException("./yaml/bad_port.yml");
    }

    @Test
    public void testBadScheme() {
        assertThatExceptionOfType(ConfigurationParsingException.class).isThrownBy(() -> load("./yaml/bad_scheme.yml"));
    }

    @Test
    public void testBadAuthUsername() {
        assertConfigurationValidationException("./yaml/bad_auth_username.yml");
    }

    @Test
    public void testBadPassword() {
        assertConfigurationValidationException("./yaml/bad_auth_password.yml");
    }

    @Test
    public void testBadAuthScheme() {
        assertConfigurationValidationException("./yaml/bad_auth_scheme.yml");
    }

    @Test
    public void testBadCredentialType() {
        assertConfigurationValidationException("./yaml/bad_auth_credential_type.yml");
    }
}

