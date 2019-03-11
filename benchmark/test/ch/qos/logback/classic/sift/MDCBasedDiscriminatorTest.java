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
package ch.qos.logback.classic.sift;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.testUtil.RandomUtil;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class MDCBasedDiscriminatorTest {
    static String DEFAULT_VAL = "DEFAULT_VAL";

    MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();

    LoggerContext context = new LoggerContext();

    Logger logger = context.getLogger(this.getClass());

    int diff = RandomUtil.getPositiveInt();

    String key = "MDCBasedDiscriminatorTest_key" + (diff);

    String value = "MDCBasedDiscriminatorTest_val" + (diff);

    LoggingEvent event;

    @Test
    public void smoke() {
        MDC.put(key, value);
        event = new LoggingEvent("a", logger, Level.DEBUG, "", null, null);
        String discriminatorValue = discriminator.getDiscriminatingValue(event);
        Assert.assertEquals(value, discriminatorValue);
    }

    @Test
    public void nullMDC() {
        event = new LoggingEvent("a", logger, Level.DEBUG, "", null, null);
        Assert.assertEquals(new HashMap<String, String>(), event.getMDCPropertyMap());
        String discriminatorValue = discriminator.getDiscriminatingValue(event);
        Assert.assertEquals(MDCBasedDiscriminatorTest.DEFAULT_VAL, discriminatorValue);
    }
}

