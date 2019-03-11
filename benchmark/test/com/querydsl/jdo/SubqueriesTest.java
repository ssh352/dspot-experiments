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
package com.querydsl.jdo;


import com.querydsl.jdo.test.domain.QProduct;
import org.junit.Assert;
import org.junit.Test;


public class SubqueriesTest extends AbstractJDOTest {
    private QProduct product = QProduct.product;

    private QProduct other = new QProduct("other");

    @Test
    public void list_exists() {
        query().from(product).where(query().from(other).select(other).exists()).select(product).fetch();
    }

    @Test
    public void list_notExists() {
        query().from(product).where(query().from(other).select(other).notExists()).select(product).fetch();
    }

    @Test
    public void list_contains() {
        query().from(product).where(product.name.in(query().from(other).select(other.name))).select(product).fetch();
    }

    @Test
    public void gt_subquery() {
        double avg = query().from(product).select(product.price.avg()).fetchFirst();
        for (double price : query().from(product).where(product.price.gt(query().from(other).select(other.price.avg()))).select(product.price).fetch()) {
            Assert.assertTrue((price > avg));
        }
    }

    @Test
    public void gt_subquery_with_condition() {
        for (double price : query().from(product).where(product.price.gt(query().from(other).where(other.name.eq("XXX")).select(other.price.avg()))).select(product.price).fetch()) {
            System.out.println(price);
        }
    }

    @Test
    public void eq_subquery() {
        double avg = query().from(product).select(product.price.avg()).fetchFirst();
        for (double price : query().from(product).where(product.price.eq(query().from(other).select(other.price.avg()))).select(product.price).fetch()) {
            Assert.assertEquals(avg, price, 1.0E-4);
        }
    }

    @Test
    public void in_subquery() {
        for (double price : query().from(product).where(product.price.in(query().from(other).where(other.name.eq("Some name")).select(other.price))).select(product.price).fetch()) {
            System.out.println(price);
        }
    }

    @Test
    public void count() {
        for (double price : query().from(product).where(query().from(other).where(other.price.gt(product.price)).select(other.count()).gt(0L)).select(product.price).fetch()) {
            System.out.println(price);
        }
    }

    @Test
    public void exists() {
        for (double price : query().from(product).where(query().from(other).where(other.price.gt(product.price)).exists()).select(product.price).fetch()) {
            System.out.println(price);
        }
    }

    @Test
    public void not_exists() {
        for (double price : query().from(product).where(query().from(other).where(other.price.gt(product.price)).notExists()).select(product.price).fetch()) {
            System.out.println(price);
        }
    }
}

