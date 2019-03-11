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


import QQueryEmbedded4Test_User.user.address.city;
import QQueryEmbedded4Test_User.user.address.name;
import QQueryEmbedded4Test_User.user.complex.a;
import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Assert;
import org.junit.Test;


public class QueryEmbedded4Test {
    @QueryEntity
    public static class User {
        @QueryEmbedded
        @QueryInit("city.name")
        QueryEmbedded4Test.Address address;

        @QueryEmbedded
        QueryEmbedded4Test.Complex<String> complex;
    }

    public static class Address {
        @QueryEmbedded
        QueryEmbedded4Test.City city;

        String name;
    }

    public static class City {
        String name;
    }

    public static class Complex<T extends Comparable<T>> implements Comparable<QueryEmbedded4Test.Complex<T>> {
        T a;

        @Override
        public int compareTo(QueryEmbedded4Test.Complex<T> arg0) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Test
    public void user_address_city() {
        Assert.assertNotNull(city);
    }

    @Test
    public void user_address_name() {
        Assert.assertNotNull(name);
    }

    @Test
    public void user_address_city_name() {
        Assert.assertNotNull(QQueryEmbedded4Test_User.user.address.city.name);
    }

    @Test
    public void user_complex_a() {
        Assert.assertNotNull(a);
    }
}

