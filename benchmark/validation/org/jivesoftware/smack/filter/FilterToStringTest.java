/**
 * Copyright 2015 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.filter;


import MessageWithBodiesFilter.INSTANCE;
import org.junit.Assert;
import org.junit.Test;


public class FilterToStringTest {
    @Test
    public void abstractListFilterToStringTest() {
        AndFilter andFilter = new AndFilter();
        andFilter.addFilter(new StanzaIdFilter("foo"));
        andFilter.addFilter(new ThreadFilter("42"));
        andFilter.addFilter(INSTANCE);
        final String res = andFilter.toString();
        Assert.assertEquals("AndFilter: (StanzaIdFilter: id=foo, ThreadFilter: thread=42, MessageWithBodiesFilter)", res);
    }
}

