package io.dropwizard.logging;


import SyslogAppenderFactory.Facility;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.logging.async.AsyncLoggingEventAppenderFactory;
import io.dropwizard.logging.layout.DropwizardLayoutFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


public class SyslogAppenderFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(SyslogAppenderFactory.class);
    }

    @Test
    public void defaultIncludesAppName() throws Exception {
        assertThat(new SyslogAppenderFactory().getLogFormat()).contains("%app");
    }

    @Test
    public void defaultIncludesPid() throws Exception {
        assertThat(new SyslogAppenderFactory().getLogFormat()).contains("%pid");
    }

    @Test
    public void patternIncludesAppNameAndPid() throws Exception {
        final AsyncAppender wrapper = ((AsyncAppender) (new SyslogAppenderFactory().build(new LoggerContext(), "MyApplication", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(getSuffixPattern()).matches("^MyApplication\\[\\d+\\].+");
    }

    @Test
    public void stackTracePatternCanBeSet() throws Exception {
        final SyslogAppenderFactory syslogAppenderFactory = new SyslogAppenderFactory();
        syslogAppenderFactory.setStackTracePrefix("--->");
        final AsyncAppender wrapper = ((AsyncAppender) (syslogAppenderFactory.build(new LoggerContext(), "MyApplication", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(getStackTracePattern()).isEqualTo("--->");
    }

    @Test
    public void appenderContextIsSet() throws Exception {
        final Logger root = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        final SyslogAppenderFactory appenderFactory = new SyslogAppenderFactory();
        final Appender<ILoggingEvent> appender = appenderFactory.build(root.getLoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory());
        assertThat(appender.getContext()).isEqualTo(root.getLoggerContext());
    }

    @Test
    public void appenderNameIsSet() throws Exception {
        final Logger root = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        final SyslogAppenderFactory appenderFactory = new SyslogAppenderFactory();
        final Appender<ILoggingEvent> appender = appenderFactory.build(root.getLoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory());
        assertThat(appender.getName()).isEqualTo("async-syslog-appender");
    }

    @Test
    public void syslogFacilityTest() {
        for (SyslogAppenderFactory.Facility facility : Facility.values()) {
            assertThatCode(() -> SyslogAppender.facilityStringToint(facility.toString().toLowerCase(Locale.ENGLISH))).doesNotThrowAnyException();
        }
    }
}

