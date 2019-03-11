/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.pipeline;


import io.crate.data.Bucket;
import io.crate.data.CollectionBucket;
import io.crate.data.Input;
import io.crate.data.Row;
import io.crate.data.RowN;
import io.crate.expression.symbol.Literal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TableFunctionApplierTest {
    @Test
    public void testFunctionsAreApplied() {
        Input<Bucket> fstFunc = () -> new CollectionBucket(Arrays.asList(new Object[]{ 1 }, new Object[]{ 2 }, new Object[]{ 3 }));
        Input<Bucket> sndFunc = () -> new CollectionBucket(Arrays.asList(new Object[]{ 4 }, new Object[]{ 5 }));
        TableFunctionApplier tableFunctionApplier = new TableFunctionApplier(Arrays.asList(fstFunc, sndFunc), Collections.singletonList(Literal.of(10)), Collections.emptyList());
        Iterator<Row> iterator = tableFunctionApplier.apply(new RowN(0));
        Assert.assertThat(iterator.next().materialize(), Matchers.is(new Object[]{ 1, 4, 10 }));
        Assert.assertThat(iterator.next().materialize(), Matchers.is(new Object[]{ 2, 5, 10 }));
        Assert.assertThat(iterator.next().materialize(), Matchers.is(new Object[]{ 3, null, 10 }));
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
    }
}

