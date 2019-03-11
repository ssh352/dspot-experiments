/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.http.rest.json;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.exception.KairosDBException;
import org.kairosdb.testing.BeanValidationHelper;


public class RelativeTimeTest {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SS", Locale.ENGLISH);

    private long timeRelativeTo;

    @Test(expected = IllegalArgumentException.class)
    public void test_getTimeRelativeTo_invalidUnit() throws KairosDBException {
        RelativeTime time = new RelativeTime(5, "QUARTERS");
        time.getTimeRelativeTo(System.currentTimeMillis());
    }

    @Test
    public void test_getTimeRelativeTo_milliseconds() throws ParseException {
        RelativeTime time = new RelativeTime(5, "MILLISECONDS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 4:55:12.17").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_seconds() throws ParseException {
        RelativeTime time = new RelativeTime(5, "SECONDS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 4:55:07.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_minutes() throws ParseException {
        RelativeTime time = new RelativeTime(5, "MINUTES");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 4:50:12.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_hours() throws ParseException {
        RelativeTime time = new RelativeTime(3, "HOURS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 1:55:12.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_days() throws ParseException {
        RelativeTime time = new RelativeTime(3, "DAYS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-15 4:55:12.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_weeks() throws ParseException {
        RelativeTime time = new RelativeTime(3, "WEEKS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2012-Dec-28 4:55:12.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_Months() throws ParseException {
        RelativeTime time = new RelativeTime(2, "MONTHS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2012-Nov-18 4:55:12.22").getTime()));
    }

    @Test
    public void test_getTimeRelativeTo_years() throws ParseException {
        RelativeTime time = new RelativeTime(3, "YEARS");
        Assert.assertThat(time.getTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2010-JAN-18 4:55:12.22").getTime()));
    }

    @Test
    public void testValueZeroInvalid() {
        RelativeTime time = new RelativeTime(0, "days");
        Set<ConstraintViolation<RelativeTime>> violations = BeanValidationHelper.VALIDATOR.validate(time);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violationMessages.get(0), CoreMatchers.equalTo("value must be greater than or equal to 1"));
    }

    @Test
    public void testUnitNullInvalid() {
        RelativeTime time = new RelativeTime();
        Set<ConstraintViolation<RelativeTime>> violations = BeanValidationHelper.VALIDATOR.validate(time);
        List<String> violationMessages = BeanValidationHelper.messagesFor(violations);
        Assert.assertThat(violationMessages.size(), CoreMatchers.equalTo(2));
        /* assertThat(violationMessages.get(1), equalTo("value must be greater than or equal to 1"));
        assertThat(violationMessages.get(0), equalTo("unit may not be null"));
         */
    }

    @Test
    public void test_getFutureTimeRelativeTo_milliseconds() throws ParseException {
        RelativeTime time = new RelativeTime(5, "MILLISECONDS");
        Assert.assertThat(time.getFutureTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 4:55:12.27").getTime()));
    }

    @Test
    public void test_getFutureTimeRelativeTo_seconds() throws ParseException {
        RelativeTime time = new RelativeTime(5, "SECONDS");
        Assert.assertThat(time.getFutureTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 4:55:17.22").getTime()));
    }

    @Test
    public void test_getFutureTimeRelativeTo_minutes() throws ParseException {
        RelativeTime time = new RelativeTime(5, "MINUTES");
        Assert.assertThat(time.getFutureTimeRelativeTo(timeRelativeTo), CoreMatchers.equalTo(RelativeTimeTest.dateFormat.parse("2013-Jan-18 5:00:12.22").getTime()));
    }
}

