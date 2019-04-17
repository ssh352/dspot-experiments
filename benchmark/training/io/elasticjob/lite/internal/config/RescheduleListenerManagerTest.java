/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.internal.config;


import Type.NODE_ADDED;
import Type.NODE_UPDATED;
import io.elasticjob.lite.api.strategy.JobInstance;
import io.elasticjob.lite.event.JobEventBus;
import io.elasticjob.lite.fixture.LiteJsonConstants;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class RescheduleListenerManagerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobEventBus jobEventBus;

    private final RescheduleListenerManager rescheduleListenerManager = new RescheduleListenerManager(null, "test_job");

    @Test
    public void assertStart() {
        rescheduleListenerManager.start();
        Mockito.verify(jobNodeStorage).addDataListener(ArgumentMatchers.<RescheduleListenerManager.CronSettingAndJobEventChangedJobListener>any());
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsNotCronPath() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config/other", NODE_ADDED, LiteJsonConstants.getJobJson());
        Mockito.verify(jobScheduleController, Mockito.times(0)).rescheduleJob(ArgumentMatchers.<String>any());
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathButNotUpdate() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", NODE_ADDED, LiteJsonConstants.getJobJson());
        Mockito.verify(jobScheduleController, Mockito.times(0)).rescheduleJob(ArgumentMatchers.<String>any());
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateButCannotFindJob() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", NODE_UPDATED, LiteJsonConstants.getJobJson());
        Mockito.verify(jobScheduleController, Mockito.times(0)).rescheduleJob(ArgumentMatchers.<String>any());
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateAndFindJob() {
        JobRegistry.getInstance().addJobInstance("test_job", new JobInstance("127.0.0.1@-@0"));
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", NODE_UPDATED, LiteJsonConstants.getJobJson());
        Mockito.verify(jobScheduleController).rescheduleJob("0/1 * * * * ?");
        JobRegistry.getInstance().shutdown("test_job");
    }
}
