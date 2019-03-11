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
package com.querydsl.jpa;


import QUser.user;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QEmployee;
import com.querydsl.jpa.hibernate.HibernateQuery;
import org.junit.Assert;
import org.junit.Test;


public class HibernateQueryTest {
    @Test
    public void clone_() {
        QCat cat = QCat.cat;
        BooleanBuilder emptyBooleanBuilder = new BooleanBuilder();
        HibernateQuery<?> hq = new HibernateQuery<Void>().from(cat).where(cat.name.isNull().and(emptyBooleanBuilder));
        HibernateQuery<?> hq2 = hq.clone();
        Assert.assertNotNull(hq2);
    }

    @Test
    public void innerJoin() {
        HibernateQuery<?> hqlQuery = new HibernateQuery<Void>();
        QEmployee employee = QEmployee.employee;
        hqlQuery.from(employee);
        hqlQuery.innerJoin(employee.user, user);
        Assert.assertEquals("select employee\nfrom Employee employee\n  inner join employee.user as user", hqlQuery.toString());
    }
}

