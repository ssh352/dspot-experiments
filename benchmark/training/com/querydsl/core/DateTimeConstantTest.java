/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core;


import com.querydsl.core.types.dsl.DateTimeExpression;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DateTimeConstantTest {
    @Test
    public void test() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 12);
        cal.set(Calendar.MILLISECOND, 3);
        DateTimeExpression<Date> date = DateTimeConstant.create(cal.getTime());
        Assert.assertEquals("1", date.dayOfMonth().toString());
        Assert.assertEquals("1", date.month().toString());
        Assert.assertEquals("2000", date.year().toString());
        Assert.assertEquals("7", date.dayOfWeek().toString());
        Assert.assertEquals("1", date.dayOfYear().toString());
        Assert.assertEquals("13", date.hour().toString());
        Assert.assertEquals("30", date.minute().toString());
        Assert.assertEquals("12", date.second().toString());
        Assert.assertEquals("3", date.milliSecond().toString());
    }
}

