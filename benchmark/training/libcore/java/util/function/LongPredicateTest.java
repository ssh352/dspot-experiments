/**
 * Copyright (C) 2016 The Android Open Source Project
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
package libcore.java.util.function;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongPredicate;
import junit.framework.TestCase;


public class LongPredicateTest extends TestCase {
    public void testAnd() throws Exception {
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        LongPredicate alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            return true;
        };
        LongPredicate alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            return true;
        };
        LongPredicate alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            return false;
        };
        LongPredicate alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            return false;
        };
        // true && true
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.and(alwaysTrue2).test(1L));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysTrue2Invoked.get())));
        // true && false
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysTrue.and(alwaysFalse).test(1L));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (alwaysFalseInvoked.get())));
        // false && false
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysFalse2).test(1L));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysFalse2Invoked.get()))));
        // false && true
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.and(alwaysTrue).test(1L));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (!(alwaysTrueInvoked.get()))));
    }

    public void testAnd_null() throws Exception {
        LongPredicate alwaysTrue = ( x) -> true;
        try {
            alwaysTrue.and(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNegate() throws Exception {
        long arg = 5L;
        LongPredicate alwaysTrue = ( x) -> {
            TestCase.assertEquals(x, arg);
            return true;
        };
        TestCase.assertFalse(alwaysTrue.negate().test(arg));
        LongPredicate alwaysFalse = ( x) -> {
            TestCase.assertEquals(x, arg);
            return false;
        };
        TestCase.assertTrue(alwaysFalse.negate().test(arg));
    }

    public void testOr() throws Exception {
        AtomicBoolean alwaysTrueInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysTrue2Invoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalseInvoked = new AtomicBoolean(false);
        AtomicBoolean alwaysFalse2Invoked = new AtomicBoolean(false);
        AtomicBoolean[] invocationState = new AtomicBoolean[]{ alwaysTrueInvoked, alwaysTrue2Invoked, alwaysFalseInvoked, alwaysFalse2Invoked };
        LongPredicate alwaysTrue = ( x) -> {
            alwaysTrueInvoked.set(true);
            return true;
        };
        LongPredicate alwaysTrue2 = ( x) -> {
            alwaysTrue2Invoked.set(true);
            return true;
        };
        LongPredicate alwaysFalse = ( x) -> {
            alwaysFalseInvoked.set(true);
            return false;
        };
        LongPredicate alwaysFalse2 = ( x) -> {
            alwaysFalse2Invoked.set(true);
            return false;
        };
        // true || true
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysTrue2).test(1L));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysTrue2Invoked.get()))));
        // true || false
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysTrue.or(alwaysFalse).test(1L));
        TestCase.assertTrue(((alwaysTrueInvoked.get()) && (!(alwaysFalseInvoked.get()))));
        // false || false
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertFalse(alwaysFalse.or(alwaysFalse2).test(1L));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysFalse2Invoked.get())));
        // false || true
        LongPredicateTest.resetToFalse(invocationState);
        TestCase.assertTrue(alwaysFalse.or(alwaysTrue).test(1L));
        TestCase.assertTrue(((alwaysFalseInvoked.get()) && (alwaysTrueInvoked.get())));
    }

    public void testOr_null() throws Exception {
        LongPredicate alwaysTrue = ( x) -> true;
        try {
            alwaysTrue.or(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

