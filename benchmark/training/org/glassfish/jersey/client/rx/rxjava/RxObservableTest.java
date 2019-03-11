/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.client.rx.rxjava;


import java.util.concurrent.ExecutorService;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class RxObservableTest {
    private Client client;

    private Client clientWithExecutor;

    private ExecutorService executor;

    @Test
    public void testNotFoundResponse() throws Exception {
        final RxObservableInvoker invoker = client.target("http://jersey.java.net").request().header("Response-Status", 404).rx(RxObservableInvoker.class);
        testInvoker(invoker, 404, false);
    }

    @Test
    public void testNotFoundWithCustomExecutor() throws Exception {
        final RxObservableInvoker invoker = clientWithExecutor.target("http://jersey.java.net").request().header("Response-Status", 404).rx(RxObservableInvoker.class);
        testInvoker(invoker, 404, true);
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundReadEntityViaClass() throws Throwable {
        try {
            client.target("http://jersey.java.net").request().header("Response-Status", 404).rx(RxObservableInvoker.class).get(String.class).toBlocking().toFuture().get();
        } catch (final Exception expected) {
            // java.util.concurrent.ExecutionException
            throw // javax.ws.rs.ProcessingException
            // .getCause()
            // javax.ws.rs.NotFoundException
            expected.getCause();
        }
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundReadEntityViaGenericType() throws Throwable {
        try {
            client.target("http://jersey.java.net").request().header("Response-Status", 404).rx(RxObservableInvoker.class).get(new javax.ws.rs.core.GenericType<String>() {}).toBlocking().toFuture().get();
        } catch (final Exception expected) {
            expected.printStackTrace();
            // java.util.concurrent.ExecutionException
            throw // javax.ws.rs.NotFoundException
            expected.getCause();
        }
    }

    @Test
    public void testReadEntityViaClass() throws Throwable {
        final String response = client.target("http://jersey.java.net").request().rx(RxObservableInvoker.class).get(String.class).toBlocking().toFuture().get();
        MatcherAssert.assertThat(response, Is.is("NO-ENTITY"));
    }

    @Test
    public void testReadEntityViaGenericType() throws Throwable {
        final String response = client.target("http://jersey.java.net").request().rx(RxObservableInvoker.class).get(new javax.ws.rs.core.GenericType<String>() {}).toBlocking().toFuture().get();
        MatcherAssert.assertThat(response, Is.is("NO-ENTITY"));
    }
}

