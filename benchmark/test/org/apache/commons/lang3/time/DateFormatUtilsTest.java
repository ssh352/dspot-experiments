/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;


/**
 * TestCase for DateFormatUtils.
 */
// tests lots of deprecated items
@SuppressWarnings("deprecation")
public class DateFormatUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new DateFormatUtils());
        final Constructor<?>[] cons = DateFormatUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(DateFormatUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(DateFormatUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFormat() {
        final Calendar c = Calendar.getInstance(FastTimeZone.getGmtTimeZone());
        c.set(2005, Calendar.JANUARY, 1, 12, 0, 0);
        c.setTimeZone(TimeZone.getDefault());
        final StringBuilder buffer = new StringBuilder();
        final int year = c.get(Calendar.YEAR);
        final int month = (c.get(Calendar.MONTH)) + 1;
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        buffer.append(year);
        buffer.append(month);
        buffer.append(day);
        buffer.append(hour);
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime(), "yyyyMdH"));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime().getTime(), "yyyyMdH"));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime(), "yyyyMdH", Locale.US));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime().getTime(), "yyyyMdH", Locale.US));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFormatCalendar() {
        final Calendar c = Calendar.getInstance(FastTimeZone.getGmtTimeZone());
        c.set(2005, Calendar.JANUARY, 1, 12, 0, 0);
        c.setTimeZone(TimeZone.getDefault());
        final StringBuilder buffer = new StringBuilder();
        final int year = c.get(Calendar.YEAR);
        final int month = (c.get(Calendar.MONTH)) + 1;
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        buffer.append(year);
        buffer.append(month);
        buffer.append(day);
        buffer.append(hour);
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c, "yyyyMdH"));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime(), "yyyyMdH"));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c, "yyyyMdH", Locale.US));
        Assertions.assertEquals(buffer.toString(), DateFormatUtils.format(c.getTime(), "yyyyMdH", Locale.US));
    }

    @Test
    public void testFormatUTC() {
        final Calendar c = Calendar.getInstance(FastTimeZone.getGmtTimeZone());
        c.set(2005, Calendar.JANUARY, 1, 12, 0, 0);
        Assertions.assertEquals("2005-01-01T12:00:00", DateFormatUtils.formatUTC(c.getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
        Assertions.assertEquals("2005-01-01T12:00:00", DateFormatUtils.formatUTC(c.getTime().getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
        Assertions.assertEquals("2005-01-01T12:00:00", DateFormatUtils.formatUTC(c.getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(), Locale.US));
        Assertions.assertEquals("2005-01-01T12:00:00", DateFormatUtils.formatUTC(c.getTime().getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(), Locale.US));
    }

    @Test
    public void testDateTimeISO() {
        testGmtMinus3("2002-02-23T09:11:12", DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());
        testGmtMinus3("2002-02-23T09:11:12-03:00", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
        testUTC("2002-02-23T09:11:12Z", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
    }

    @Test
    public void testDateISO() {
        testGmtMinus3("2002-02-23", DateFormatUtils.ISO_DATE_FORMAT.getPattern());
        testGmtMinus3("2002-02-23-03:00", DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.getPattern());
        testUTC("2002-02-23Z", DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.getPattern());
    }

    @Test
    public void testTimeISO() {
        testGmtMinus3("T09:11:12", DateFormatUtils.ISO_TIME_FORMAT.getPattern());
        testGmtMinus3("T09:11:12-03:00", DateFormatUtils.ISO_TIME_TIME_ZONE_FORMAT.getPattern());
        testUTC("T09:11:12Z", DateFormatUtils.ISO_TIME_TIME_ZONE_FORMAT.getPattern());
    }

    @Test
    public void testTimeNoTISO() {
        testGmtMinus3("09:11:12", DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern());
        testGmtMinus3("09:11:12-03:00", DateFormatUtils.ISO_TIME_NO_T_TIME_ZONE_FORMAT.getPattern());
        testUTC("09:11:12Z", DateFormatUtils.ISO_TIME_NO_T_TIME_ZONE_FORMAT.getPattern());
    }

    @DefaultLocale(language = "en")
    @Test
    public void testSMTP() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3");
        Calendar june = createJuneTestDate(timeZone);
        assertFormats("Sun, 08 Jun 2003 10:11:12 -0300", DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern(), timeZone, june);
        timeZone = FastTimeZone.getGmtTimeZone();
        june = createJuneTestDate(timeZone);
        assertFormats("Sun, 08 Jun 2003 10:11:12 +0000", DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern(), timeZone, june);
    }

    @Test
    public void testLANG1000() throws Exception {
        final String date = "2013-11-18T12:48:05Z";
        DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(date);
    }

    @DefaultTimeZone("UTC")
    @Test
    public void testLang530() throws ParseException {
        final Date d = new Date();
        final String isoDateStr = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(d);
        final Date d2 = DateUtils.parseDate(isoDateStr, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
        // the format loses milliseconds so have to reintroduce them
        Assertions.assertEquals(d.getTime(), ((d2.getTime()) + ((d.getTime()) % 1000)), "Date not equal to itself ISO formatted and parsed");
    }

    /**
     * According to LANG-916 (https://issues.apache.org/jira/browse/LANG-916),
     * the format method did contain a bug: it did not use the TimeZone data.
     *
     * This method test that the bug is fixed.
     */
    @Test
    public void testLang916() {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.clear();
        cal.set(2009, 9, 16, 8, 42, 16);
        // Long.
        {
            final String value = DateFormatUtils.format(cal.getTimeInMillis(), DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Europe/Paris"));
            Assertions.assertEquals("2009-10-16T08:42:16+02:00", value, "long");
        }
        {
            final String value = DateFormatUtils.format(cal.getTimeInMillis(), DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Asia/Kolkata"));
            Assertions.assertEquals("2009-10-16T12:12:16+05:30", value, "long");
        }
        {
            final String value = DateFormatUtils.format(cal.getTimeInMillis(), DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Europe/London"));
            Assertions.assertEquals("2009-10-16T07:42:16+01:00", value, "long");
        }
        // Calendar.
        {
            final String value = DateFormatUtils.format(cal, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Europe/Paris"));
            Assertions.assertEquals("2009-10-16T08:42:16+02:00", value, "calendar");
        }
        {
            final String value = DateFormatUtils.format(cal, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Asia/Kolkata"));
            Assertions.assertEquals("2009-10-16T12:12:16+05:30", value, "calendar");
        }
        {
            final String value = DateFormatUtils.format(cal, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone("Europe/London"));
            Assertions.assertEquals("2009-10-16T07:42:16+01:00", value, "calendar");
        }
    }
}
