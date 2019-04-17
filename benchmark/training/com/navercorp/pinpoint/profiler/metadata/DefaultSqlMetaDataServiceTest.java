/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.metadata;


import com.navercorp.pinpoint.bootstrap.context.ParsingResult;
import com.navercorp.pinpoint.profiler.sender.EnhancedDataSender;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class DefaultSqlMetaDataServiceTest {
    @Test
    public void cacheSql() throws Exception {
        final EnhancedDataSender dataSender = Mockito.mock(EnhancedDataSender.class);
        final SqlMetaDataService sqlMetaDataService = new DefaultSqlMetaDataService(dataSender, 100);
        final String sql = "select * from A";
        final ParsingResult parsingResult = sqlMetaDataService.parseSql(sql);
        boolean newValue = sqlMetaDataService.cacheSql(parsingResult);
        Assert.assertTrue(newValue);
        Mockito.verify(dataSender, Mockito.times(1)).request(ArgumentMatchers.any(SqlMetaData.class));
        boolean notNewValue = sqlMetaDataService.cacheSql(parsingResult);
        Assert.assertFalse(notNewValue);
        Mockito.verify(dataSender, Mockito.times(1)).request(ArgumentMatchers.any(SqlMetaData.class));
    }
}
