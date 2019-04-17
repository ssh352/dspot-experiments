/**
 * Copyright (C) 2016 Piotr Wittchen
 *
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
package com.github.pwittchen.reactivenetwork.library.rx2;


import NetworkInfo.State.CONNECTED;
import android.app.Application;
import android.content.Context;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway")
public class ReactiveNetworkTest {
    private static final String TEST_VALID_HOST = "www.test.com";

    private static final int TEST_VALID_PORT = 80;

    private static final int TEST_VALID_TIMEOUT = 1000;

    private static final int TEST_VALID_INTERVAL = 1000;

    private static final int TEST_VALID_INITIAL_INTERVAL = 1000;

    private static final int TEST_VALID_HTTP_RESPONSE = 204;

    @Test
    public void testReactiveNetworkObjectShouldNotBeNull() {
        // given
        ReactiveNetwork reactiveNetwork;
        // when
        reactiveNetwork = ReactiveNetwork.create();
        // then
        assertThat(reactiveNetwork).isNotNull();
    }

    @Test
    public void observeNetworkConnectivityShouldNotBeNull() {
        // given
        networkConnectivityObservableShouldNotBeNull();
    }

    @Test
    @Config(sdk = 23)
    public void observeNetworkConnectivityShouldNotBeNullForMarshmallow() {
        // given
        networkConnectivityObservableShouldNotBeNull();
    }

    @Test
    @Config(sdk = 21)
    public void observeNetworkConnectivityShouldNotBeNullForLollipop() {
        networkConnectivityObservableShouldNotBeNull();
    }

    @Test
    public void observeNetworkConnectivityWithStrategyShouldNotBeNull() {
        // given
        final Context context = RuntimeEnvironment.application;
        NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();
        // when
        Observable<Connectivity> observable;
        observable = ReactiveNetwork.observeNetworkConnectivity(context, strategy);
        // then
        assertThat(observable).isNotNull();
    }

    @Test
    public void observeInternetConnectivityDefaultShouldNotBeNull() {
        // given
        Observable<Boolean> observable;
        // when
        observable = ReactiveNetwork.observeInternetConnectivity();
        // then
        assertThat(observable).isNotNull();
    }

    @Test
    public void observeNetworkConnectivityShouldBeConnectedOnStartWhenNetworkIsAvailable() {
        // given
        final Application context = RuntimeEnvironment.application;
        // when
        Connectivity connectivity = ReactiveNetwork.observeNetworkConnectivity(context).blockingFirst();
        // then
        assertThat(connectivity.state()).isEqualTo(CONNECTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void observeNetworkConnectivityShouldThrowAnExceptionForNullContext() {
        // given
        final Context context = null;
        final NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();
        // when
        ReactiveNetwork.observeNetworkConnectivity(context, strategy);
        // then an exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void observeNetworkConnectivityShouldThrowAnExceptionForNullStrategy() {
        // given
        final Context context = RuntimeEnvironment.application;
        final NetworkObservingStrategy strategy = null;
        // when
        ReactiveNetwork.observeNetworkConnectivity(context, strategy);
        // then an exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void observeInternetConnectivityShouldThrowAnExceptionWhenStrategyIsNull() {
        // given
        final InternetObservingStrategy strategy = null;
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        // when
        ReactiveNetwork.observeInternetConnectivity(strategy, ReactiveNetworkTest.TEST_VALID_INITIAL_INTERVAL, ReactiveNetworkTest.TEST_VALID_INTERVAL, ReactiveNetworkTest.TEST_VALID_HOST, ReactiveNetworkTest.TEST_VALID_PORT, ReactiveNetworkTest.TEST_VALID_TIMEOUT, ReactiveNetworkTest.TEST_VALID_HTTP_RESPONSE, errorHandler);
        // then an exception is thrown
    }

    @Test
    public void observeInternetConnectivityShouldNotThrowAnExceptionWhenStrategyIsNotNull() {
        // given
        final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        // when
        final Observable<Boolean> observable = ReactiveNetwork.observeInternetConnectivity(strategy, ReactiveNetworkTest.TEST_VALID_INITIAL_INTERVAL, ReactiveNetworkTest.TEST_VALID_INTERVAL, ReactiveNetworkTest.TEST_VALID_HOST, ReactiveNetworkTest.TEST_VALID_PORT, ReactiveNetworkTest.TEST_VALID_TIMEOUT, ReactiveNetworkTest.TEST_VALID_HTTP_RESPONSE, errorHandler);
        // then
        assertThat(observable).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkInternetConnectivityShouldThrowAnExceptionWhenStrategyIsNull() {
        // given
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        // when
        ReactiveNetwork.checkInternetConnectivity(null, ReactiveNetworkTest.TEST_VALID_HOST, ReactiveNetworkTest.TEST_VALID_PORT, ReactiveNetworkTest.TEST_VALID_TIMEOUT, ReactiveNetworkTest.TEST_VALID_HTTP_RESPONSE, errorHandler);
        // then an exception is thrown
    }

    @Test
    public void checkInternetConnectivityShouldNotThrowAnExceptionWhenStrategyIsNotNull() {
        // given
        final InternetObservingStrategy strategy = new SocketInternetObservingStrategy();
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        // when
        final Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity(strategy, ReactiveNetworkTest.TEST_VALID_HOST, ReactiveNetworkTest.TEST_VALID_PORT, ReactiveNetworkTest.TEST_VALID_TIMEOUT, ReactiveNetworkTest.TEST_VALID_HTTP_RESPONSE, errorHandler);
        // then
        assertThat(single).isNotNull();
    }

    @Test
    public void shouldObserveInternetConnectivityWithCustomSettings() {
        // given
        final int initialInterval = 1;
        final int interval = 2;
        final String host = "www.test.com";
        int port = 90;
        int timeout = 3;
        ErrorHandler testErrorHandler = createTestErrorHandler();
        InternetObservingStrategy strategy = createTestInternetObservingStrategy();
        // when
        InternetObservingSettings settings = InternetObservingSettings.builder().initialInterval(initialInterval).interval(interval).host(host).port(port).timeout(timeout).errorHandler(testErrorHandler).strategy(strategy).build();
        // then
        Observable<Boolean> observable = ReactiveNetwork.observeInternetConnectivity(settings);
        assertThat(observable).isNotNull();
    }

    @Test
    public void shouldCheckInternetConnectivityWithCustomSettings() {
        // given
        final int initialInterval = 1;
        final int interval = 2;
        final String host = "www.test.com";
        int port = 90;
        int timeout = 3;
        int httpResponse = 200;
        ErrorHandler testErrorHandler = createTestErrorHandler();
        InternetObservingStrategy strategy = createTestInternetObservingStrategy();
        // when
        InternetObservingSettings settings = InternetObservingSettings.builder().initialInterval(initialInterval).interval(interval).host(host).port(port).timeout(timeout).httpResponse(httpResponse).errorHandler(testErrorHandler).strategy(strategy).build();
        // then
        Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity(settings);
        assertThat(single).isNotNull();
    }

    @Test
    public void shouldHaveJustSevenMethodsInPublicApi() {
        // given
        Class<? extends ReactiveNetwork> clazz = ReactiveNetwork.create().getClass();
        final int predefinedNumberOfMethods = 9;
        final int publicMethodsInApi = 7;// this number can be increased only in reasonable case

        // when
        Method[] methods = clazz.getMethods();
        // then
        assertThat(methods.length).isEqualTo((predefinedNumberOfMethods + publicMethodsInApi));
    }
}
