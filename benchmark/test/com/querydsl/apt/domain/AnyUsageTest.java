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
package com.querydsl.apt.domain;


import QAnyUsageTest_DealerGroup.dealerGroup.dealers;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static QAnyUsageTest_Dealer.dealer;


public class AnyUsageTest {
    @Entity
    public static class DealerGroup implements Serializable {
        private static final long serialVersionUID = 8001287260658920066L;

        @Id
        @GeneratedValue
        public Long id;

        @OneToMany(mappedBy = "dealerGroup")
        public Set<AnyUsageTest.Dealer> dealers;
    }

    @Entity
    public static class Dealer implements Serializable {
        private static final long serialVersionUID = -6832045219902674887L;

        @Id
        @GeneratedValue
        public Long id;

        @ManyToOne
        public AnyUsageTest.DealerGroup dealerGroup;

        @ManyToOne
        public AnyUsageTest.Company company;
    }

    @Entity
    public static class Company implements Serializable {
        private static final long serialVersionUID = -5369301332567282659L;

        @Id
        @GeneratedValue
        public Long id;
    }

    @Test
    public void test() {
        QAnyUsageTest_Dealer dealer = dealers.any();
        Assert.assertNotNull(dealer);
        Assert.assertNotNull(dealer.company);
    }

    @Test
    public void withQDealer() {
        List<AnyUsageTest.Company> companies = new LinkedList<AnyUsageTest.Company>();
        companies.add(new AnyUsageTest.Company());
        QAnyUsageTest_Dealer qDealer = dealer;
        BooleanExpression expression = qDealer.company.in(companies);
        Assert.assertNotNull(expression);
    }

    @Test
    public void withQDealerGroup() {
        List<AnyUsageTest.Company> companies = new LinkedList<AnyUsageTest.Company>();
        companies.add(new AnyUsageTest.Company());
        QAnyUsageTest_Dealer qDealer = dealers.any();
        BooleanExpression expression = qDealer.company.in(companies);
        Assert.assertNotNull(expression);
    }
}

