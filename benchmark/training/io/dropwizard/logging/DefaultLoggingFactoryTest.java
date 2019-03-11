package io.dropwizard.logging;


import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.filter.FilterFactory;
import io.dropwizard.util.Lists;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import java.util.List;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;


public class DefaultLoggingFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    private final YamlConfigurationFactory<DefaultLoggingFactory> factory = new YamlConfigurationFactory(DefaultLoggingFactory.class, BaseValidator.newValidator(), objectMapper, "dw");

    private DefaultLoggingFactory config;

    @Test
    public void hasADefaultLevel() throws Exception {
        assertThat(config.getLevel()).isEqualTo("INFO");
    }

    @Test
    public void canParseNewLoggerFormat() throws Exception {
        final DefaultLoggingFactory config = factory.build(new File(Resources.getResource("yaml/logging_advanced.yml").toURI()));
        assertThat(config.getLoggers()).contains(MapEntry.entry("com.example.app", new TextNode("INFO")));
        final JsonNode newApp = config.getLoggers().get("com.example.newApp");
        assertThat(newApp).isNotNull();
        final LoggerConfiguration newAppConfiguration = objectMapper.treeToValue(newApp, LoggerConfiguration.class);
        assertThat(newAppConfiguration.getLevel()).isEqualTo("DEBUG");
        assertThat(newAppConfiguration.getAppenders()).hasSize(1);
        final AppenderFactory<ILoggingEvent> appenderFactory = newAppConfiguration.getAppenders().get(0);
        assertThat(appenderFactory).isInstanceOf(FileAppenderFactory.class);
        final FileAppenderFactory<ILoggingEvent> fileAppenderFactory = ((FileAppenderFactory<ILoggingEvent>) (appenderFactory));
        assertThat(fileAppenderFactory.getCurrentLogFilename()).isEqualTo("${new_app}.log");
        assertThat(fileAppenderFactory.getArchivedLogFilenamePattern()).isEqualTo("${new_app}-%d.log.gz");
        assertThat(fileAppenderFactory.getArchivedFileCount()).isEqualTo(5);
        assertThat(fileAppenderFactory.getBufferSize().toKilobytes()).isEqualTo(256);
        final List<FilterFactory<ILoggingEvent>> filterFactories = fileAppenderFactory.getFilterFactories();
        assertThat(filterFactories).hasSize(2);
        assertThat(filterFactories.get(0)).isExactlyInstanceOf(TestFilterFactory.class);
        assertThat(filterFactories.get(1)).isExactlyInstanceOf(SecondTestFilterFactory.class);
        final JsonNode legacyApp = config.getLoggers().get("com.example.legacyApp");
        assertThat(legacyApp).isNotNull();
        final LoggerConfiguration legacyAppConfiguration = objectMapper.treeToValue(legacyApp, LoggerConfiguration.class);
        assertThat(legacyAppConfiguration.getLevel()).isEqualTo("DEBUG");
        // We should not create additional appenders, if they are not specified
        assertThat(legacyAppConfiguration.getAppenders()).isEmpty();
    }

    @Test
    public void testResetAppenders() throws Exception {
        final String configPath = Resources.getResource("yaml/logging.yml").getFile();
        final DefaultLoggingFactory config = factory.build(new FileConfigurationSourceProvider(), configPath);
        config.configure(new MetricRegistry(), "test-logger");
        config.reset();
        // There should be exactly one appender configured, a ConsoleAppender
        final Logger logger = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        final List<Appender<ILoggingEvent>> appenders = Lists.of(logger.iteratorForAppenders());
        assertThat(appenders).hasAtLeastOneElementOfType(ConsoleAppender.class);
        assertThat(appenders).as("context").allMatch((Appender<?> a) -> (a.getContext()) != null);
        assertThat(appenders).as("started").allMatch(LifeCycle::isStarted);
        assertThat(appenders).hasSize(1);
    }

    @Test
    public void testToStringIsImplented() {
        assertThat(config.toString()).startsWith("DefaultLoggingFactory{level=INFO, loggers={com.example.app=\"DEBUG\"}, appenders=");
    }
}

