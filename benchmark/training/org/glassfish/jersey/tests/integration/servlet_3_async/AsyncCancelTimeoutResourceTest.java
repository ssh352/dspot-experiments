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
package org.glassfish.jersey.tests.integration.servlet_3_async;


import Response.Status.NO_CONTENT;
import Response.Status.SERVICE_UNAVAILABLE;
import java.util.concurrent.Future;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Asynchronous servlet-deployed resource for testing {@link javax.ws.rs.container.AsyncResponse async response} timeouts.
 *
 * @author Michal Gajdos
 */
public class AsyncCancelTimeoutResourceTest extends JerseyTest {
    public AsyncCancelTimeoutResourceTest() {
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Test
    public void testTimeout() throws Exception {
        final WebTarget resourceTarget = target("cancel-timeout");
        final Future<Response> suspend = resourceTarget.path("suspend").request().async().get();
        final Future<Response> timeout = resourceTarget.path("timeout").request().async().post(Entity.text(0));
        Assert.assertThat(timeout.get().getStatus(), CoreMatchers.is(NO_CONTENT.getStatusCode()));
        Assert.assertThat(suspend.get().getStatus(), CoreMatchers.is(SERVICE_UNAVAILABLE.getStatusCode()));
    }
}

