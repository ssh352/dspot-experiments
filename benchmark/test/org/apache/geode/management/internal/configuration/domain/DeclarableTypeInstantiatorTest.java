/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.configuration.domain;


import java.util.Properties;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.configuration.DeclarableType;
import org.apache.geode.cache.configuration.ParameterType;
import org.junit.Test;


public class DeclarableTypeInstantiatorTest {
    private Cache cache;

    public static class MyDeclarable implements Declarable {
        Properties props;

        Cache cache;

        @Override
        public void init(Properties props) {
            this.props = props;
        }

        @Override
        public void initialize(Cache cache, Properties props) {
            init(props);
            this.cache = cache;
        }
    }

    public static class MyOuterDeclarable extends DeclarableTypeInstantiatorTest.MyDeclarable {}

    @Test
    public void createDeclarableInstanceFromDeclarableType() {
        DeclarableType declarableType = new DeclarableType(DeclarableTypeInstantiatorTest.MyDeclarable.class.getName(), "{\"value1\":5,\"value2\":\"some string\"}");
        DeclarableTypeInstantiatorTest.MyDeclarable result = DeclarableTypeInstantiator.newInstance(declarableType, cache);
        assertThat(result).isNotNull().isInstanceOf(DeclarableTypeInstantiatorTest.MyDeclarable.class);
        assertThat(result.props).isNotNull();
        assertThat(result.props.getProperty("value1")).isEqualTo("5");
        assertThat(result.props.getProperty("value2")).isEqualTo("some string");
    }

    @Test
    public void createDeclarableInstancedFromNestedDeclarableType() {
        DeclarableType declarableType = new DeclarableType(DeclarableTypeInstantiatorTest.MyDeclarable.class.getName(), "{\"value1\":5,\"value2\":\"some string\"}");
        ParameterType parameterType = new ParameterType("inner-prop", declarableType);
        DeclarableType outerDeclarable = new DeclarableType(DeclarableTypeInstantiatorTest.MyOuterDeclarable.class.getName());
        outerDeclarable.getParameters().add(parameterType);
        DeclarableTypeInstantiatorTest.MyOuterDeclarable result = DeclarableTypeInstantiator.newInstance(outerDeclarable, cache);
        assertThat(result.props).isNotNull();
        DeclarableTypeInstantiatorTest.MyDeclarable innerDeclarable = ((DeclarableTypeInstantiatorTest.MyDeclarable) (result.props.get("inner-prop")));
        assertThat(innerDeclarable.props.getProperty("value1")).isEqualTo("5");
        assertThat(innerDeclarable.props.getProperty("value2")).isEqualTo("some string");
    }
}
