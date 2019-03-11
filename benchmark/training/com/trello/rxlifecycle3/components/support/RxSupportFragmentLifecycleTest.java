/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trello.rxlifecycle3.components.support;


import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxSupportFragmentLifecycleTest {
    private Observable<Object> observable;

    @Test
    public void testRxFragment() {
        testLifecycle(new RxSupportFragmentLifecycleTest.TestRxFragment());
        testBindUntilEvent(new RxSupportFragmentLifecycleTest.TestRxFragment());
        testBindToLifecycle(new RxSupportFragmentLifecycleTest.TestRxFragment());
    }

    @Test
    public void testRxDialogFragment() {
        testLifecycle(new RxSupportFragmentLifecycleTest.TestRxDialogFragment());
        testBindUntilEvent(new RxSupportFragmentLifecycleTest.TestRxDialogFragment());
        testBindToLifecycle(new RxSupportFragmentLifecycleTest.TestRxDialogFragment());
    }

    @Test
    public void testRxAppCompatDialogFragment() {
        // Once Robolectric is less broken we could run these tests
        // Until then, these are identical to RxDialogFragment, so whatever.
        // 
        // testLifecycle(new RxAppCompatDialogFragment());
        // testBindUntilEvent(new RxAppCompatDialogFragment());
        // testBindToLifecycle(new RxAppCompatDialogFragment());
    }

    // These classes are just for testing since components are abstract
    public static class TestRxFragment extends RxFragment {}

    public static class TestRxDialogFragment extends RxDialogFragment {}
}

