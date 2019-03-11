package com.baeldung.java9.time;


import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TimeApiUnitTest {
    @Test
    public void givenGetDatesBetweenWithUsingJava7_WhenStartEndDate_thenDatesList() {
        Date startDate = Calendar.getInstance().getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 2);
        Date endDate = endCalendar.getTime();
        List<Date> dates = TimeApi.getDatesBetweenUsingJava7(startDate, endDate);
        Assert.assertEquals(dates.size(), 2);
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        Assert.assertEquals(dates.get(0).getDay(), date1.getDay());
        Assert.assertEquals(dates.get(0).getMonth(), date1.getMonth());
        Assert.assertEquals(dates.get(0).getYear(), date1.getYear());
        calendar.add(Calendar.DATE, 1);
        Date date2 = calendar.getTime();
        Assert.assertEquals(dates.get(1).getDay(), date2.getDay());
        Assert.assertEquals(dates.get(1).getMonth(), date2.getMonth());
        Assert.assertEquals(dates.get(1).getYear(), date2.getYear());
    }

    @Test
    public void givenGetDatesBetweenWithUsingJava8_WhenStartEndDate_thenDatesList() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);
        List<LocalDate> dates = TimeApi.getDatesBetweenUsingJava8(startDate, endDate);
        Assert.assertEquals(dates.size(), 2);
        Assert.assertEquals(dates.get(0), LocalDate.now());
        Assert.assertEquals(dates.get(1), LocalDate.now().plusDays(1));
    }

    @Test
    public void givenGetDatesBetweenWithUsingJava9_WhenStartEndDate_thenDatesList() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);
        List<LocalDate> dates = TimeApi.getDatesBetweenUsingJava9(startDate, endDate);
        Assert.assertEquals(dates.size(), 2);
        Assert.assertEquals(dates.get(0), LocalDate.now());
        Assert.assertEquals(dates.get(1), LocalDate.now().plusDays(1));
    }
}

