/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.pattern;


import CoreConstants.LINE_SEPARATOR;
import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.net.SyslogConstants;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.FormatInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;


public class ConverterTest {
    LoggerContext lc = new LoggerContext();

    Logger logger = lc.getLogger(ConverterTest.class);

    LoggingEvent le;

    List<String> optionList = new ArrayList<String>();

    @Test
    public void testLineOfCaller() {
        {
            DynamicConverter<ILoggingEvent> converter = new LineOfCallerConverter();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            // the number below should be the line number of the previous line
            Assert.assertEquals("75", buf.toString());
        }
    }

    @Test
    public void testLevel() {
        {
            DynamicConverter<ILoggingEvent> converter = new LevelConverter();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertEquals("INFO", buf.toString());
        }
        {
            DynamicConverter<ILoggingEvent> converter = new LevelConverter();
            converter.setFormattingInfo(new FormatInfo(1, 1, true, false));
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertEquals("I", buf.toString());
        }
    }

    @Test
    public void testThread() {
        DynamicConverter<ILoggingEvent> converter = new ThreadConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        System.out.println(buf.toString());
        String regex = ClassicTestConstants.NAKED_MAIN_REGEX;
        Assert.assertTrue(buf.toString().matches(regex));
    }

    @Test
    public void testMessage() {
        DynamicConverter<ILoggingEvent> converter = new MessageConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        Assert.assertEquals("Some message", buf.toString());
    }

    @Test
    public void testLineSeparator() {
        DynamicConverter<ILoggingEvent> converter = new LineSeparatorConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        Assert.assertEquals(LINE_SEPARATOR, buf.toString());
    }

    @Test
    public void testException() {
        {
            DynamicConverter<ILoggingEvent> converter = new ThrowableProxyConverter();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
        }
        {
            DynamicConverter<ILoggingEvent> converter = new ThrowableProxyConverter();
            this.optionList.add("3");
            converter.setOptionList(this.optionList);
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
        }
    }

    @Test
    public void testLogger() {
        {
            ClassicConverter converter = new LoggerConverter();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertEquals(this.getClass().getName(), buf.toString());
        }
        {
            ClassicConverter converter = new LoggerConverter();
            this.optionList.add("20");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertEquals("c.q.l.c.p.ConverterTest", buf.toString());
        }
        {
            DynamicConverter<ILoggingEvent> converter = new LoggerConverter();
            this.optionList.clear();
            this.optionList.add("0");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertEquals("ConverterTest", buf.toString());
        }
    }

    @Test
    public void testVeryLongLoggerName() {
        ClassicConverter converter = new LoggerConverter();
        this.optionList.add("5");
        converter.setOptionList(this.optionList);
        converter.start();
        StringBuilder buf = new StringBuilder();
        char c = 'a';
        int extraParts = 3;
        int totalParts = (ClassicConstants.MAX_DOTS) + extraParts;
        StringBuilder loggerNameBuf = new StringBuilder();
        StringBuilder witness = new StringBuilder();
        for (int i = 0; i < totalParts; i++) {
            loggerNameBuf.append(c).append(c).append(c);
            if (i < (ClassicConstants.MAX_DOTS)) {
                witness.append(c);
            } else {
                witness.append(c).append(c).append(c);
            }
            loggerNameBuf.append('.');
            witness.append('.');
        }
        loggerNameBuf.append("zzzzzz");
        witness.append("zzzzzz");
        le.setLoggerName(loggerNameBuf.toString());
        converter.write(buf, le);
        Assert.assertEquals(witness.toString(), buf.toString());
    }

    @Test
    public void testClass() {
        DynamicConverter<ILoggingEvent> converter = new ClassOfCallerConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        Assert.assertEquals(this.getClass().getName(), buf.toString());
    }

    @Test
    public void testMethodOfCaller() {
        DynamicConverter<ILoggingEvent> converter = new MethodOfCallerConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        Assert.assertEquals("testMethodOfCaller", buf.toString());
    }

    @Test
    public void testFileOfCaller() {
        DynamicConverter<ILoggingEvent> converter = new FileOfCallerConverter();
        StringBuilder buf = new StringBuilder();
        converter.write(buf, le);
        Assert.assertEquals("ConverterTest.java", buf.toString());
    }

    @Test
    public void testCallerData() {
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            converter.start();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            if ((buf.length()) < 10) {
                Assert.fail("buf is too short");
            }
        }
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            this.optionList.add("2");
            this.optionList.add("XXX");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            LoggingEvent event = makeLoggingEvent(null);
            event.setMarker(MarkerFactory.getMarker("XXX"));
            converter.write(buf, event);
            if ((buf.length()) < 10) {
                Assert.fail("buf is too short");
            }
        }
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            this.optionList.clear();
            this.optionList.add("2");
            this.optionList.add("XXX");
            this.optionList.add("*");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            LoggingEvent event = makeLoggingEvent(null);
            event.setMarker(MarkerFactory.getMarker("YYY"));
            converter.write(buf, event);
            if ((buf.length()) < 10) {
                Assert.fail("buf is too short");
            }
        }
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            this.optionList.clear();
            this.optionList.add("2");
            this.optionList.add("XXX");
            this.optionList.add("+");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            LoggingEvent event = makeLoggingEvent(null);
            event.setMarker(MarkerFactory.getMarker("YYY"));
            converter.write(buf, event);
            if ((buf.length()) < 10) {
                Assert.fail("buf is too short");
            }
        }
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            this.optionList.clear();
            this.optionList.add("2");
            this.optionList.add("XXX");
            this.optionList.add("*");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            if ((buf.length()) < 10) {
                Assert.fail("buf is too short");
            }
            // System.out.println(buf);
        }
        {
            DynamicConverter<ILoggingEvent> converter = new CallerDataConverter();
            this.optionList.clear();
            this.optionList.add("4..5");
            converter.setOptionList(this.optionList);
            converter.start();
            StringBuilder buf = new StringBuilder();
            converter.write(buf, le);
            Assert.assertTrue("buf is too short", ((buf.length()) >= 10));
            String expectedRegex = "Caller\\+4\t at (java.base\\/)?java.lang.reflect.Method.invoke.*$";
            String actual = buf.toString();
            Assert.assertTrue(("actual: " + actual), Pattern.compile(expectedRegex).matcher(actual).find());
        }
    }

    @Test
    public void testRelativeTime() throws Exception {
        DynamicConverter<ILoggingEvent> converter = new RelativeTimeConverter();
        StringBuilder buf0 = new StringBuilder();
        StringBuilder buf1 = new StringBuilder();
        long timestamp = System.currentTimeMillis();
        LoggingEvent e0 = makeLoggingEvent(null);
        e0.setTimeStamp(timestamp);
        LoggingEvent e1 = makeLoggingEvent(null);
        e1.setTimeStamp(timestamp);
        converter.write(buf0, e0);
        converter.write(buf1, e1);
        Assert.assertEquals(buf0.toString(), buf1.toString());
    }

    @Test
    public void testSyslogStart() throws Exception {
        DynamicConverter<ILoggingEvent> converter = new SyslogStartConverter();
        this.optionList.clear();
        this.optionList.add("MAIL");
        converter.setOptionList(this.optionList);
        converter.start();
        ILoggingEvent event = makeLoggingEvent(null);
        StringBuilder buf = new StringBuilder();
        converter.write(buf, event);
        String expected = ("<" + ((SyslogConstants.LOG_MAIL) + (SyslogConstants.INFO_SEVERITY))) + ">";
        Assert.assertTrue(buf.toString().startsWith(expected));
    }

    @Test
    public void testMDCConverter() throws Exception {
        MDC.clear();
        MDC.put("someKey", "someValue");
        MDCConverter converter = new MDCConverter();
        this.optionList.clear();
        this.optionList.add("someKey");
        converter.setOptionList(optionList);
        converter.start();
        ILoggingEvent event = makeLoggingEvent(null);
        String result = converter.convert(event);
        Assert.assertEquals("someValue", result);
    }

    @Test
    public void contextNameConverter() {
        ClassicConverter converter = new ContextNameConverter();
        // see http://jira.qos.ch/browse/LBCLASSIC-149
        LoggerContext lcOther = new LoggerContext();
        lcOther.setName("another");
        converter.setContext(lcOther);
        lc.setName("aValue");
        ILoggingEvent event = makeLoggingEvent(null);
        String result = converter.convert(event);
        Assert.assertEquals("aValue", result);
    }

    @Test
    public void contextProperty() {
        PropertyConverter converter = new PropertyConverter();
        converter.setContext(lc);
        List<String> ol = new ArrayList<String>();
        ol.add("k");
        converter.setOptionList(ol);
        converter.start();
        lc.setName("aValue");
        lc.putProperty("k", "v");
        ILoggingEvent event = makeLoggingEvent(null);
        String result = converter.convert(event);
        Assert.assertEquals("v", result);
    }
}

