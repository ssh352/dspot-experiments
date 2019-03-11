/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.support.builder;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;


/**
 *
 *
 * @author Glenn Renfro
 */
public class SingleItemPeekableItemReaderBuilderTests {
    /**
     * Test method to validate builder creates a
     * {@link org.springframework.batch.item.support.SingleItemPeekableItemReader#peek()}
     * with expected peek and read behavior.
     */
    @Test
    public void testPeek() throws Exception {
        SingleItemPeekableItemReader<String> reader = new SingleItemPeekableItemReaderBuilder<String>().delegate(new org.springframework.batch.item.support.ListItemReader(Arrays.asList("a", "b"))).build();
        Assert.assertEquals("a", reader.peek());
        Assert.assertEquals("a", reader.read());
        Assert.assertEquals("b", reader.read());
        Assert.assertEquals(null, reader.peek());
        Assert.assertEquals(null, reader.read());
    }

    /**
     * Test method to validate that an {@link IllegalArgumentException} is thrown if the
     * delegate is not set in the builder.
     */
    @Test
    public void testValidation() {
        try {
            new SingleItemPeekableItemReaderBuilder<org.springframework.batch.item.sample.Foo>().build();
            Assert.fail("A delegate is required");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A delegate is required", iae.getMessage());
        }
    }
}

