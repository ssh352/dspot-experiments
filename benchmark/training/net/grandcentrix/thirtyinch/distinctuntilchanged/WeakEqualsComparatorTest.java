/**
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.grandcentrix.thirtyinch.distinctuntilchanged;


import org.junit.Test;


public class WeakEqualsComparatorTest {
    @Test
    public void different() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        assertThat(comparator.compareWith(new Object[]{ "arg1" })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ "arg2" })).isFalse();
    }

    @Test
    public void gcClearedReferences() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        final Object arg = new Object();
        assertThat(comparator.compareWith(new Object[]{ arg })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ arg })).isTrue();
        // simulate drop of reference
        comparator.mLastParameters.clear();
        assertThat(comparator.compareWith(new Object[]{ arg })).isFalse();
    }

    @Test
    public void initialize() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        assertThat(comparator.compareWith(new Object[]{ "arg1" })).isFalse();
    }

    @Test
    public void same() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        assertThat(comparator.compareWith(new Object[]{ "arg1" })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ "arg1" })).isTrue();
        assertThat(comparator.compareWith(new Object[]{ "arg1" })).isTrue();
    }

    @Test
    public void sameEquals_differentHashcode() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        final Object arg1 = new Object() {
            @Override
            public boolean equals(final Object obj) {
                return true;
            }
        };
        final Object arg2 = new Object() {
            @Override
            public boolean equals(final Object obj) {
                return true;
            }
        };
        // equal but not same hash code
        assertThat(arg1).isEqualTo(arg2);
        assertThat(arg1.hashCode()).isNotEqualTo(arg2.hashCode());
        assertThat(comparator.compareWith(new Object[]{ arg1 })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ arg2 })).isTrue();
        assertThat(comparator.compareWith(new Object[]{ arg1 })).isTrue();
        assertThat(comparator.compareWith(new Object[]{ arg2 })).isTrue();
    }

    @Test
    public void sameObject_equalsFalse_sameHashcode() throws Exception {
        final WeakEqualsComparator comparator = new WeakEqualsComparator();
        final Object arg1 = new Object() {
            @Override
            public boolean equals(final Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };
        // equal but not same hash code
        assertThat(arg1).isNotEqualTo(arg1);
        assertThat(arg1).isSameAs(arg1);
        assertThat(comparator.compareWith(new Object[]{ arg1 })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ arg1 })).isFalse();
        assertThat(comparator.compareWith(new Object[]{ arg1 })).isFalse();
    }
}

