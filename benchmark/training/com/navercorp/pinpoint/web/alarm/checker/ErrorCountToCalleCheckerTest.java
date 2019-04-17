/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.alarm.checker;


import CheckerCategory.ERROR_COUNT_TO_CALLEE;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.alarm.DataCollectorFactory.DataCollectorCategory;
import com.navercorp.pinpoint.web.alarm.collector.MapStatisticsCallerDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.MapStatisticsCallerDao;
import com.navercorp.pinpoint.web.vo.Application;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorCountToCalleCheckerTest {
    private static final Logger logger = LoggerFactory.getLogger(ErrorCountToCalleCheckerTest.class);

    private static final String FROM_SERVICE_NAME = "from_local_service";

    private static final String TO_SERVICE_NAME = "to_local_service";

    private static final String SERVICE_TYPE = "tomcat";

    public static MapStatisticsCallerDao dao;

    @Test
    public void checkTest() {
        Application application = new Application(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ServiceType.STAND_ALONE);
        MapStatisticsCallerDataCollector dataCollector = new MapStatisticsCallerDataCollector(DataCollectorCategory.CALLER_STAT, application, ErrorCountToCalleCheckerTest.dao, System.currentTimeMillis(), 300000);
        Rule rule = new Rule(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ErrorCountToCalleCheckerTest.SERVICE_TYPE, ERROR_COUNT_TO_CALLEE.getName(), 5, "testGroup", false, false, ((ErrorCountToCalleCheckerTest.TO_SERVICE_NAME) + 1));
        ErrorCountToCalleeChecker checker = new ErrorCountToCalleeChecker(dataCollector, rule);
        checker.check();
        Assert.assertTrue(checker.isDetected());
    }

    @Test
    public void checkTest2() {
        Application application = new Application(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ServiceType.STAND_ALONE);
        MapStatisticsCallerDataCollector dataCollector = new MapStatisticsCallerDataCollector(DataCollectorCategory.CALLER_STAT, application, ErrorCountToCalleCheckerTest.dao, System.currentTimeMillis(), 300000);
        Rule rule = new Rule(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ErrorCountToCalleCheckerTest.SERVICE_TYPE, ERROR_COUNT_TO_CALLEE.getName(), 6, "testGroup", false, false, ((ErrorCountToCalleCheckerTest.TO_SERVICE_NAME) + 1));
        ErrorCountToCalleeChecker checker = new ErrorCountToCalleeChecker(dataCollector, rule);
        checker.check();
        Assert.assertFalse(checker.isDetected());
    }

    @Test
    public void checkTest3() {
        Application application = new Application(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ServiceType.STAND_ALONE);
        MapStatisticsCallerDataCollector dataCollector = new MapStatisticsCallerDataCollector(DataCollectorCategory.CALLER_STAT, application, ErrorCountToCalleCheckerTest.dao, System.currentTimeMillis(), 300000);
        Rule rule = new Rule(ErrorCountToCalleCheckerTest.FROM_SERVICE_NAME, ErrorCountToCalleCheckerTest.SERVICE_TYPE, ERROR_COUNT_TO_CALLEE.getName(), 5, "testGroup", false, false, ((ErrorCountToCalleCheckerTest.TO_SERVICE_NAME) + 2));
        ErrorCountToCalleeChecker checker = new ErrorCountToCalleeChecker(dataCollector, rule);
        checker.check();
        Assert.assertTrue(checker.isDetected());
    }
}
