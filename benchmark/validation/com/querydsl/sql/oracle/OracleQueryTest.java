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
package com.querydsl.sql.oracle;


import OracleGrammar.rownum;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class OracleQueryTest {
    private OracleQuery<?> query;

    private QSurvey survey = new QSurvey("survey");

    @Test
    public void connectByPrior() {
        query.connectByPrior(survey.name.isNull());
        Assert.assertEquals("from SURVEY survey connect by prior survey.NAME is null order by survey.NAME asc", toString(query));
    }

    @Test
    public void connectBy() {
        query.connectByPrior(survey.name.isNull());
        Assert.assertEquals("from SURVEY survey connect by prior survey.NAME is null order by survey.NAME asc", toString(query));
    }

    @Test
    public void connectByNocyclePrior() {
        query.connectByNocyclePrior(survey.name.isNull());
        Assert.assertEquals("from SURVEY survey connect by nocycle prior survey.NAME is null order by survey.NAME asc", toString(query));
    }

    @Test
    public void startWith() {
        query.startWith(survey.name.isNull());
        Assert.assertEquals("from SURVEY survey start with survey.NAME is null order by survey.NAME asc", toString(query));
    }

    @Test
    public void orderSiblingsBy() {
        query.orderSiblingsBy(survey.name);
        Assert.assertEquals("from SURVEY survey order siblings by survey.NAME order by survey.NAME asc", toString(query));
    }

    @Test
    public void rowNum() {
        query.where(rownum.lt(5));
        Assert.assertEquals("from SURVEY survey where rownum < ? order by survey.NAME asc", toString(query));
    }
}

