/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.e2e.server;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * JERSEY-2677 reproducer - test, that {@code Factory.dispose()} is correctly called for both sync and async cases.
 *
 * @author Adam Lindenthal (adam.lindenthal at oracle.com)
 */
public class RequestScopedAndAsyncTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(RequestScopedAndAsyncTest.class.getName());

    // latch to prevent, that the balance is checked before dispose() is called
    private static CountDownLatch cdl;

    public static class Injectable {
        private String message = "Hello";

        public String getMessage() {
            return message;
        }
    }

    public static class InjectableFactory implements DisposableSupplier<RequestScopedAndAsyncTest.Injectable> {
        private static AtomicInteger provided = new AtomicInteger(0);

        private static AtomicInteger balance = new AtomicInteger(0);

        @Override
        public RequestScopedAndAsyncTest.Injectable get() {
            RequestScopedAndAsyncTest.LOGGER.fine("Factory provide() called.");
            RequestScopedAndAsyncTest.InjectableFactory.provided.incrementAndGet();
            RequestScopedAndAsyncTest.InjectableFactory.balance.incrementAndGet();
            return new RequestScopedAndAsyncTest.Injectable();
        }

        @Override
        public void dispose(RequestScopedAndAsyncTest.Injectable i) {
            RequestScopedAndAsyncTest.LOGGER.fine("Factory dispose() called. ");
            RequestScopedAndAsyncTest.InjectableFactory.balance.decrementAndGet();
            RequestScopedAndAsyncTest.cdl.countDown();
        }

        public static void reset() {
            RequestScopedAndAsyncTest.LOGGER.fine("Factory reset() called.");
            RequestScopedAndAsyncTest.InjectableFactory.provided.set(0);
            RequestScopedAndAsyncTest.InjectableFactory.balance.set(0);
            RequestScopedAndAsyncTest.cdl = new CountDownLatch(1);
        }

        public static int getProvidedCount() {
            return RequestScopedAndAsyncTest.InjectableFactory.provided.intValue();
        }

        public static int getBalanceValue() {
            return RequestScopedAndAsyncTest.InjectableFactory.balance.intValue();
        }
    }

    @Path("test")
    public static class TestResource {
        @Inject
        private RequestScopedAndAsyncTest.Injectable injectable;

        @GET
        @Path("sync")
        public Response sync() {
            RequestScopedAndAsyncTest.LOGGER.fine(("Injected message: " + (injectable.getMessage())));
            return Response.noContent().build();
        }

        @GET
        @Path("async")
        public void async(@Suspended
        AsyncResponse ar) {
            RequestScopedAndAsyncTest.LOGGER.fine(("Injected message: " + (injectable.getMessage())));
            ar.resume(Response.noContent().build());
        }
    }

    @Test
    public void testInstanceReleaseAsync() throws InterruptedException, ExecutionException {
        Future<Response> future = target("/test/async").request().async().get();
        Response response = future.get();
        Assert.assertEquals(204, response.getStatus());
        Assert.assertEquals(1, RequestScopedAndAsyncTest.InjectableFactory.getProvidedCount());
        try {
            RequestScopedAndAsyncTest.cdl.await(500, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            RequestScopedAndAsyncTest.LOGGER.log(Level.INFO, "CountDownLatch interrupted: ", e);
        }
        Assert.assertEquals(0, RequestScopedAndAsyncTest.InjectableFactory.getBalanceValue());
    }

    @Test
    public void testInstanceReleaseSync() {
        Assert.assertEquals(204, target("/test/sync").request().get().getStatus());
        Assert.assertEquals(1, RequestScopedAndAsyncTest.InjectableFactory.getProvidedCount());
        Assert.assertEquals(0, RequestScopedAndAsyncTest.InjectableFactory.getBalanceValue());
    }

    @Test
    public void shouldProvideAndDisposeSync2() {
        Assert.assertEquals(204, target("/test/sync").request().get().getStatus());
        Assert.assertEquals(1, RequestScopedAndAsyncTest.InjectableFactory.getProvidedCount());
        Assert.assertEquals(0, RequestScopedAndAsyncTest.InjectableFactory.getBalanceValue());
    }

    @Test
    public void shouldProvideAndDisposeAsync2() throws InterruptedException, ExecutionException {
        Assert.assertEquals(204, target("/test/async").request().get().getStatus());
        Assert.assertEquals(1, RequestScopedAndAsyncTest.InjectableFactory.getProvidedCount());
        try {
            RequestScopedAndAsyncTest.cdl.await(500, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            RequestScopedAndAsyncTest.LOGGER.log(Level.INFO, "CountDownLatch interrupted: ", e);
        }
        Assert.assertEquals(0, RequestScopedAndAsyncTest.InjectableFactory.getBalanceValue());
    }
}

