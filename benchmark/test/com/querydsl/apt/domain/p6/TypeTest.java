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
package com.querydsl.apt.domain.p6;


import org.junit.Assert;
import org.junit.Test;

import static QType1.type1;
import static QType2.type2;


public class TypeTest {
    @Test
    public void test() {
        QType1 type1 = type1;
        QType2 type2 = type2;
        Assert.assertEquals(type2.getType(), type1.property.getType());
        Assert.assertEquals(type2.getClass(), type1.property.getClass());
    }
}

