package io.dropwizard.http2;


import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON;
import io.dropwizard.Configuration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class Http2IntegrationTest extends AbstractHttp2Test {
    @Rule
    public final DropwizardAppExtension<Configuration> appRule = new DropwizardAppExtension(FakeApplication.class, ResourceHelpers.resourceFilePath("test-http2.yml"), Optional.of("tls_http2"), ConfigOverride.config("tls_http2", "server.connector.keyStorePath", ResourceHelpers.resourceFilePath("stores/http2_server.jks")), ConfigOverride.config("tls_http2", "server.connector.trustStorePath", ResourceHelpers.resourceFilePath("stores/http2_client.jts")));

    @Test
    public void testHttp11() throws Exception {
        final String hostname = "localhost";
        final int port = appRule.getLocalPort();
        final JerseyClient http11Client = new JerseyClientBuilder().sslContext(sslContextFactory.getSslContext()).build();
        final Response response = http11Client.target((((("https://" + hostname) + ":") + port) + "/api/test")).request().get();
        assertThat(response.getHeaderString(CONTENT_TYPE)).isEqualTo(APPLICATION_JSON);
        assertThat(response.readEntity(String.class)).isEqualTo(FakeApplication.HELLO_WORLD);
        http11Client.close();
    }

    @Test
    public void testHttp2() throws Exception {
        AbstractHttp2Test.assertResponse(client.GET((("https://localhost:" + (appRule.getLocalPort())) + "/api/test")));
    }

    @Test
    public void testHttp2ManyRequests() throws Exception {
        performManyAsyncRequests(client, (("https://localhost:" + (appRule.getLocalPort())) + "/api/test"));
    }
}

