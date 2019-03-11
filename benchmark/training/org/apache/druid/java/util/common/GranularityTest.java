/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.java.util.common;


import Granularity.Formatter.DEFAULT;
import Granularity.Formatter.HIVE;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.java.util.common.granularity.GranularityType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Test;


public class GranularityTest {
    final Granularity SECOND = Granularities.SECOND;

    final Granularity MINUTE = Granularities.MINUTE;

    final Granularity HOUR = Granularities.HOUR;

    final Granularity SIX_HOUR = Granularities.SIX_HOUR;

    final Granularity FIFTEEN_MINUTE = Granularities.FIFTEEN_MINUTE;

    final Granularity DAY = Granularities.DAY;

    final Granularity WEEK = Granularities.WEEK;

    final Granularity MONTH = Granularities.MONTH;

    final Granularity YEAR = Granularities.YEAR;

    @Test
    public void testHiveFormat() {
        GranularityTest.PathDate[] secondChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "dt=2011-03-15-20-50-43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "/dt=2011-03-15-20-50-43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "valid/dt=2011-03-15-20-50-43/Test1"), new GranularityTest.PathDate(null, null, "valid/dt=2011-03-15-20-50/Test2"), new GranularityTest.PathDate(null, null, "valid/dt=2011-03-15-20/Test3"), new GranularityTest.PathDate(null, null, "valid/dt=2011-03-15/Test4"), new GranularityTest.PathDate(null, null, "valid/dt=2011-03/Test5"), new GranularityTest.PathDate(null, null, "valid/dt=2011/Test6"), new GranularityTest.PathDate(null, null, "null/dt=----/Test7"), new GranularityTest.PathDate(null, null, "null/10-2011-23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/dt=2011-10-20-20-42-72/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/dt=2011-10-20-42-90-24/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/dt=2011-10-33-20-42-24/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/dt=2011-13-20-20-42-24/Test11") };
        checkToDate(SECOND, HIVE, secondChecks);
    }

    @Test
    public void testSecondToDate() {
        GranularityTest.PathDate[] secondChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 43, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(SECOND, DEFAULT, secondChecks);
    }

    @Test
    public void testMinuteToDate() {
        GranularityTest.PathDate[] minuteChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 20, 20, 42, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(MINUTE, DEFAULT, minuteChecks);
    }

    @Test
    public void testFifteenMinuteToDate() {
        GranularityTest.PathDate[] minuteChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=00/Test2a"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=14/Test2b"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 15, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=15/Test2c"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 15, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=29/Test2d"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 30, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=30/Test2e"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 30, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=44/Test2f"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=45/Test2g"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 45, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=59/Test2h"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 20, 20, 30, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(FIFTEEN_MINUTE, DEFAULT, minuteChecks);
    }

    @Test
    public void testHourToDate() {
        GranularityTest.PathDate[] hourChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 20, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 20, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(HOUR, DEFAULT, hourChecks);
    }

    @Test
    public void testSixHourToDate() {
        GranularityTest.PathDate[] hourChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 20, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 18, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=00/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=02/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 6, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=06/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 6, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=11/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 12, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=12/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 12, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=13/M=90/S=24/Test12"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(SIX_HOUR, DEFAULT, hourChecks);
    }

    @Test
    public void testDayToDate() {
        GranularityTest.PathDate[] dayChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(null, null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 20, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 20, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(DAY, DEFAULT, dayChecks);
    }

    @Test
    public void testMonthToDate() {
        GranularityTest.PathDate[] monthChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(new DateTime(2011, 3, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(null, null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(null, null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 10, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(new DateTime(2011, 10, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 10, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(new DateTime(2011, 10, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(null, IllegalFieldValueException.class, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(MONTH, DEFAULT, monthChecks);
    }

    @Test
    public void testYearToDate() {
        GranularityTest.PathDate[] yearChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/Test2"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/Test3"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/Test4"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/Test5"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/Test6"), new GranularityTest.PathDate(null, null, "null/y=/m=/d=/Test7"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "null/m=10/y=2011/d=23/Test8"), new GranularityTest.PathDate(null, null, "null/Test9"), new GranularityTest.PathDate(null, null, "")// Test10 Intentionally empty.
        , new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=42/S=72/Test11"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=20/M=90/S=24/Test12"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=20/H=42/M=42/S=24/Test13"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=10/d=33/H=20/M=42/S=24/Test14"), new GranularityTest.PathDate(new DateTime(2011, 1, 1, 0, 0, 0, 0, ISOChronology.getInstanceUTC()), null, "error/y=2011/m=13/d=20/H=20/M=42/S=24/Test15") };
        checkToDate(YEAR, DEFAULT, yearChecks);
    }

    @Test
    public void testBucket() {
        DateTime dt = DateTimes.of("2011-02-03T04:05:06.100");
        Assert.assertEquals(Intervals.of("2011-01-01/2012-01-01"), YEAR.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-02-01/2011-03-01"), MONTH.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-01-31/2011-02-07"), WEEK.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-02-03/2011-02-04"), DAY.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-02-03T04/2011-02-03T05"), HOUR.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-02-03T04:05:00/2011-02-03T04:06:00"), MINUTE.bucket(dt));
        Assert.assertEquals(Intervals.of("2011-02-03T04:05:06/2011-02-03T04:05:07"), SECOND.bucket(dt));
        // Test with aligned DateTime
        Assert.assertEquals(Intervals.of("2011-01-01/2011-01-02"), DAY.bucket(DateTimes.of("2011-01-01")));
    }

    @Test
    public void testTruncate() {
        DateTime date = DateTimes.of("2011-03-15T22:42:23.898");
        Assert.assertEquals(DateTimes.of("2011-01-01T00:00:00.000"), YEAR.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-01T00:00:00.000"), MONTH.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-14T00:00:00.000"), WEEK.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-15T00:00:00.000"), DAY.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-15T22:00:00.000"), HOUR.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-15T22:42:00.000"), MINUTE.bucketStart(date));
        Assert.assertEquals(DateTimes.of("2011-03-15T22:42:23.000"), SECOND.bucketStart(date));
    }

    @Test
    public void testGetIterable() {
        DateTime start = DateTimes.of("2011-01-01T00:00:00");
        DateTime end = DateTimes.of("2011-01-14T00:00:00");
        Iterator<Interval> intervals = DAY.getIterable(new Interval(start, end)).iterator();
        Assert.assertEquals(Intervals.of("2011-01-01/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-02/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-03/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-04/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-05/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-06/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-07/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-08/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-09/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-10/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-11/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-12/P1d"), intervals.next());
        Assert.assertEquals(Intervals.of("2011-01-13/P1d"), intervals.next());
        try {
            intervals.next();
        } catch (NoSuchElementException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCustomPeriodToDate() {
        GranularityTest.PathDate[] customChecks = new GranularityTest.PathDate[]{ new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 42, 0, ISOChronology.getInstanceUTC()), null, "y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 42, 0, ISOChronology.getInstanceUTC()), null, "/y=2011/m=03/d=15/H=20/M=50/S=43/Test0"), new GranularityTest.PathDate(new DateTime(2011, 3, 15, 20, 50, 42, 0, ISOChronology.getInstanceUTC()), null, "valid/y=2011/m=03/d=15/H=20/M=50/S=43/Test1") };
        checkToDate(new org.apache.druid.java.util.common.granularity.PeriodGranularity(new Period("PT2S"), null, DateTimeZone.UTC), DEFAULT, customChecks);
    }

    @Test
    public void testCustomNestedPeriodFail() {
        try {
            Period p = Period.years(6).withMonths(3).withSeconds(23);
            GranularityType.fromPeriod(p);
            Assert.fail("Complicated period creation should fail b/c of unsupported granularity type.");
        } catch (IAE e) {
            // pass
        }
    }

    private static class PathDate {
        public final String path;

        public final DateTime date;

        public final Class<? extends Exception> exception;

        private PathDate(DateTime date, Class<? extends Exception> exception, String path) {
            this.path = path;
            this.date = date;
            this.exception = exception;
        }
    }
}

