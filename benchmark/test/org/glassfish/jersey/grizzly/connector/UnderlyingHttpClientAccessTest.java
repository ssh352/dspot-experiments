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
package org.glassfish.jersey.grizzly.connector;


import com.ning.http.client.AsyncHttpClient;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of access to the underlying HTTP client instance used by the connector.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class UnderlyingHttpClientAccessTest {
    /**
     * Verifier of JERSEY-2424 fix.
     */
    @Test
    public void testHttpClientInstanceAccess() {
        final Client client = ClientBuilder.newClient(new ClientConfig().connectorProvider(new GrizzlyConnectorProvider()));
        final AsyncHttpClient hcOnClient = GrizzlyConnectorProvider.getHttpClient(client);
        // important: the web target instance in this test must be only created AFTER the client has been pre-initialized
        // (see org.glassfish.jersey.client.Initializable.preInitialize method). This is here achieved by calling the
        // connector provider's static getHttpClient method above.
        final WebTarget target = client.target("http://localhost/");
        final AsyncHttpClient hcOnTarget = GrizzlyConnectorProvider.getHttpClient(target);
        Assert.assertNotNull("HTTP client instance set on JerseyClient should not be null.", hcOnClient);
        Assert.assertNotNull("HTTP client instance set on JerseyWebTarget should not be null.", hcOnTarget);
        Assert.assertSame(("HTTP client instance set on JerseyClient should be the same instance as the one set on JerseyWebTarget" + "(provided the target instance has not been further configured)."), hcOnClient, hcOnTarget);
    }
}

