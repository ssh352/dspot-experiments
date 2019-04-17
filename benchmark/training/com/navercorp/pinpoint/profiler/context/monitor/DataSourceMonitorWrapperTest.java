/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.monitor;


import ServiceType.UNKNOWN;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class DataSourceMonitorWrapperTest {
    @Test
    public void getServiceType() throws Exception {
        DataSourceMonitor mock = Mockito.mock(DataSourceMonitor.class);
        Mockito.when(mock.getServiceType()).thenReturn(null);
        DataSourceMonitorWrapper dataSourceMonitorWrapper = new DataSourceMonitorWrapper(1, mock);
        Assert.assertEquals(UNKNOWN, dataSourceMonitorWrapper.getServiceType());
    }
}
