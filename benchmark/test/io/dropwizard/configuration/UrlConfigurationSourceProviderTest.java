package io.dropwizard.configuration;


import io.dropwizard.util.ByteStreams;
import io.dropwizard.util.Resources;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;


public class UrlConfigurationSourceProviderTest {
    private final ConfigurationSourceProvider provider = new UrlConfigurationSourceProvider();

    @Test
    public void readsFileContents() throws Exception {
        try (InputStream input = provider.open(Resources.getResource("example.txt").toString())) {
            assertThat(new String(ByteStreams.toByteArray(input), StandardCharsets.UTF_8).trim()).isEqualTo("whee");
        }
    }
}

