package com.baeldung.date;


import java.text.ParseException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DateWithoutTimeUnitTest {
    private static final long MILLISECONDS_PER_DAY = ((24 * 60) * 60) * 1000;

    @Test
    public void whenGettingDateWithoutTimeUsingCalendar_thenReturnDateWithoutTime() {
        Date dateWithoutTime = DateWithoutTime.getDateWithoutTimeUsingCalendar();
        // first check the time is set to 0
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime);
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
        // we get the day of the date
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // if we add the mills of one day minus 1 we should get the same day
        calendar.setTimeInMillis((((dateWithoutTime.getTime()) + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)) - 1));
        Assert.assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
        // if we add one full day in millis we should get a different day
        calendar.setTimeInMillis(((dateWithoutTime.getTime()) + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)));
        Assert.assertNotEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void whenGettingDateWithoutTimeUsingFormat_thenReturnDateWithoutTime() throws ParseException {
        Date dateWithoutTime = DateWithoutTime.getDateWithoutTimeUsingFormat();
        // first check the time is set to 0
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime);
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
        // we get the day of the date
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // if we add the mills of one day minus 1 we should get the same day
        calendar.setTimeInMillis((((dateWithoutTime.getTime()) + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)) - 1));
        Assert.assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
        // if we add one full day in millis we should get a different day
        calendar.setTimeInMillis(((dateWithoutTime.getTime()) + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)));
        Assert.assertNotEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void whenGettingLocalDate_thenReturnDateWithoutTime() {
        // get the local date
        LocalDate localDate = DateWithoutTime.getLocalDate();
        // get the millis of our LocalDate
        long millisLocalDate = localDate.atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        Calendar calendar = Calendar.getInstance();
        // if we add the millis of one day minus 1 we should get the same day
        calendar.setTimeInMillis(((millisLocalDate + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)) - 1));
        Assert.assertEquals(localDate.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        // if we add one full day in millis we should get a different day
        calendar.setTimeInMillis((millisLocalDate + (DateWithoutTimeUnitTest.MILLISECONDS_PER_DAY)));
        Assert.assertNotEquals(localDate.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
    }
}

