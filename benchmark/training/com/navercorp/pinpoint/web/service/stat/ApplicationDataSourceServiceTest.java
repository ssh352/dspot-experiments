/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.web.service.stat;


import JoinDataSourceListBo.DataSourceKey;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinDataSourceListBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinDataSourceBo;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class ApplicationDataSourceServiceTest {
    @Test
    public void classifyByDataSourceUrlTest() throws Exception {
        final String id = "test_app";
        long timestamp = new Date().getTime();
        ApplicationDataSourceService applicationDataSourceService = new ApplicationDataSourceService();
        Map<JoinDataSourceListBo.DataSourceKey, List<AggreJoinDataSourceBo>> dataSourceKeyListMap = applicationDataSourceService.classifyByDataSourceUrl(createJoinDataSourceListBoList(id, timestamp));
        Assert.assertEquals(dataSourceKeyListMap.size(), 5);
    }
}
