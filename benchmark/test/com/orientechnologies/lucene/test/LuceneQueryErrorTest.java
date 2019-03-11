/**
 * * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.lucene.test;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


/**
 * Created by enricorisa on 02/10/14.
 */
public class LuceneQueryErrorTest extends BaseLuceneTest {
    public LuceneQueryErrorTest() {
    }

    @Test
    public void testQueryError() {
        String query = "select * from Song where [title] LUCENE \"\" ";
        List<?> result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>(query));
        Assertions.assertThat(result).isEmpty();
    }
}

