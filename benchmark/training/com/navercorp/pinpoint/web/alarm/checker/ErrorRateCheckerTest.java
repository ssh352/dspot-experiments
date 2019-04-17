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


import CheckerCategory.ERROR_RATE;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.alarm.DataCollectorFactory.DataCollectorCategory;
import com.navercorp.pinpoint.web.alarm.collector.ResponseTimeDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.MapResponseDao;
import com.navercorp.pinpoint.web.vo.Application;
import org.junit.Assert;
import org.junit.Test;


public class ErrorRateCheckerTest {
    private static final String SERVICE_NAME = "local_service";

    private static final String SERVICE_TYPE = "tomcat";

    private static MapResponseDao mockMapResponseDAO;

    /* alert conditions not satisfied */
    @Test
    public void checkTest1() {
        Application application = new Application(ErrorRateCheckerTest.SERVICE_NAME, ServiceType.STAND_ALONE);
        ResponseTimeDataCollector collector = new ResponseTimeDataCollector(DataCollectorCategory.RESPONSE_TIME, application, ErrorRateCheckerTest.mockMapResponseDAO, System.currentTimeMillis(), 300000);
        Rule rule = new Rule(ErrorRateCheckerTest.SERVICE_NAME, ErrorRateCheckerTest.SERVICE_TYPE, ERROR_RATE.getName(), 60, "testGroup", false, false, "");
        ErrorRateChecker filter = new ErrorRateChecker(collector, rule);
        filter.check();
        Assert.assertTrue(filter.isDetected());
    }

    /* alert conditions not satisfied */
    @Test
    public void checkTest2() {
        Application application = new Application(ErrorRateCheckerTest.SERVICE_NAME, ServiceType.STAND_ALONE);
        ResponseTimeDataCollector collector = new ResponseTimeDataCollector(DataCollectorCategory.RESPONSE_TIME, application, ErrorRateCheckerTest.mockMapResponseDAO, System.currentTimeMillis(), 300000);
        Rule rule = new Rule(ErrorRateCheckerTest.SERVICE_NAME, ErrorRateCheckerTest.SERVICE_TYPE, ERROR_RATE.getName(), 61, "testGroup", false, false, "");
        ErrorRateChecker filter = new ErrorRateChecker(collector, rule);
        filter.check();
        Assert.assertFalse(filter.isDetected());
    }
}
