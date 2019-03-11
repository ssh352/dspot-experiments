/**
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.database;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.item.AbstractItemReaderTests;
import org.springframework.batch.item.AbstractItemStreamItemReaderTests;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.sample.Foo;


@RunWith(JUnit4.class)
public class StoredProcedureItemReaderCommonTests extends AbstractDatabaseItemStreamItemReaderTests {
    @Test
    public void testRestartWithDriverSupportsAbsolute() throws Exception {
        testedAsStream().close();
        tested = getItemReader();
        ((StoredProcedureItemReader<Foo>) (tested)).setDriverSupportsAbsolute(true);
        testedAsStream().open(executionContext);
        testedAsStream().close();
        testedAsStream().open(executionContext);
        testRestart();
    }

    @Test(expected = ReaderNotOpenException.class)
    public void testReadBeforeOpen() throws Exception {
        testedAsStream().close();
        tested = getItemReader();
        tested.read();
    }
}

