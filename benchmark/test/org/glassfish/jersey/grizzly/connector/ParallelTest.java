/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.grizzly.connector;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the parallel execution of multiple requests.
 *
 * @author Stepan Kopriva
 */
public class ParallelTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(ParallelTest.class.getName());

    private static final int PARALLEL_CLIENTS = 10;

    private static final String PATH = "test";

    private static final AtomicInteger receivedCounter = new AtomicInteger(0);

    private static final AtomicInteger resourceCounter = new AtomicInteger(0);

    private static final CyclicBarrier startBarrier = new CyclicBarrier(((ParallelTest.PARALLEL_CLIENTS) + 1));

    private static final CountDownLatch doneLatch = new CountDownLatch(ParallelTest.PARALLEL_CLIENTS);

    @Path(ParallelTest.PATH)
    public static class MyResource {
        @GET
        public String get() {
            sleep();
            ParallelTest.resourceCounter.addAndGet(1);
            return "GET";
        }

        private void sleep() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(ParallelTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Test
    public void testParallel() throws InterruptedException, BrokenBarrierException, TimeoutException {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(ParallelTest.PARALLEL_CLIENTS);
        try {
            final WebTarget target = target();
            for (int i = 1; i <= (ParallelTest.PARALLEL_CLIENTS); i++) {
                final int id = i;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ParallelTest.startBarrier.await();
                            Response response;
                            response = target.path(ParallelTest.PATH).request().get();
                            Assert.assertEquals("GET", response.readEntity(String.class));
                            ParallelTest.receivedCounter.incrementAndGet();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            ParallelTest.LOGGER.log(Level.WARNING, (("Client thread " + id) + " interrupted."), ex);
                        } catch (BrokenBarrierException ex) {
                            ParallelTest.LOGGER.log(Level.INFO, (("Client thread " + id) + " failed on broken barrier."), ex);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            ParallelTest.LOGGER.log(Level.WARNING, (("Client thread " + id) + " failed on unexpected exception."), t);
                        } finally {
                            ParallelTest.doneLatch.countDown();
                        }
                    }
                });
            }
            ParallelTest.startBarrier.await(1, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for clients to finish has timed out.", ParallelTest.doneLatch.await((5 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS));
            Assert.assertEquals("Resource counter", ParallelTest.PARALLEL_CLIENTS, ParallelTest.resourceCounter.get());
            Assert.assertEquals("Received counter", ParallelTest.PARALLEL_CLIENTS, ParallelTest.receivedCounter.get());
        } finally {
            executor.shutdownNow();
            Assert.assertTrue("Executor termination", executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}
