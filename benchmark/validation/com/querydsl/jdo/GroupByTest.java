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


public class GroupByTest extends AbstractJDOTest {
    private QProduct product = QProduct.product;

    @Test
    public void distinct() {
        Assert.assertEquals(3, query().from(product).distinct().select(product.description).fetch().size());
        Assert.assertEquals(3, query().from(product).distinct().select(product.price).fetch().size());
    }

    @Test
    public void groupBy() {
        Assert.assertEquals(3, query().from(product).groupBy(product.description).select(product.description).fetch().size());
        Assert.assertEquals(3, query().from(product).groupBy(product.price).select(product.price).fetch().size());
    }

    @Test
    public void having() {
        Assert.assertEquals(3, query().from(product).groupBy(product.description).having(product.description.ne("XXX")).select(product.description).fetch().size());
        Assert.assertEquals(3, query().from(product).groupBy(product.price).having(product.price.gt(0)).select(product.price).fetch().size());
    }
}

