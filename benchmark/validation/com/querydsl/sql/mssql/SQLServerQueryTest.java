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
package com.querydsl.sql.mssql;


import SQLServerTableHints.NOLOCK;
import SQLServerTableHints.NOWAIT;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class SQLServerQueryTest {
    private static final QSurvey survey = QSurvey.survey;

    @Test
    public void tableHints_single() {
        SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
        query.from(SQLServerQueryTest.survey).tableHints(NOWAIT).where(SQLServerQueryTest.survey.name.isNull());
        Assert.assertEquals("from SURVEY SURVEY with (NOWAIT)\nwhere SURVEY.NAME is null", query.toString());
    }

    @Test
    public void tableHints_multiple() {
        SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
        query.from(SQLServerQueryTest.survey).tableHints(NOWAIT, NOLOCK).where(SQLServerQueryTest.survey.name.isNull());
        Assert.assertEquals("from SURVEY SURVEY with (NOWAIT, NOLOCK)\nwhere SURVEY.NAME is null", query.toString());
    }

    @Test
    public void tableHints_multiple2() {
        QSurvey survey2 = new QSurvey("survey2");
        SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
        query.from(SQLServerQueryTest.survey).tableHints(NOWAIT).from(survey2).tableHints(NOLOCK).where(SQLServerQueryTest.survey.name.isNull());
        Assert.assertEquals("from SURVEY SURVEY with (NOWAIT), SURVEY survey2 with (NOLOCK)\nwhere SURVEY.NAME is null", query.toString());
    }
}

