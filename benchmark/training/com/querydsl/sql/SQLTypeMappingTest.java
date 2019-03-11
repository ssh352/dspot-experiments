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
package com.querydsl.sql;


import java.lang.reflect.Field;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;


public class SQLTypeMappingTest {
    @Test
    public void get() throws IllegalAccessException, IllegalArgumentException {
        JDBCTypeMapping mapping = new JDBCTypeMapping();
        for (Field field : Types.class.getFields()) {
            if (field.getType().equals(int.class)) {
                int val = field.getInt(null);
                if ((mapping.get(val, 0, 0)) == null) {
                    Assert.fail((((("Got no value for " + (field.getName())) + " (") + val) + ")"));
                }
            }
        }
    }
}

