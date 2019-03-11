/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ApplicationTest {
    @Path("test")
    public static class DummyResource {}

    @Test
    public void testGetClassesContainsNull() {
        Application a = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return new HashSet<Class<?>>() {
                    {
                        add(null);
                        add(ApplicationTest.DummyResource.class);
                    }
                };
            }

            @Override
            public Set<Object> getSingletons() {
                return Collections.emptySet();
            }
        };
        new ApplicationHandler(a);
    }

    @Test
    public void testGetSingletonsContainsNull() {
        Application a = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return Collections.emptySet();
            }

            @Override
            public Set<Object> getSingletons() {
                return new HashSet<Object>() {
                    {
                        add(null);
                        add(new ApplicationTest.DummyResource());
                    }
                };
            }
        };
        new ApplicationHandler(a);
    }

    @Test
    public void testGetSingletonsNull() {
        Application a = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return Collections.emptySet();
            }

            @Override
            public Set<Object> getSingletons() {
                return null;
            }
        };
        new ApplicationHandler(a);
    }

    @Test
    public void testGetClassesNull() {
        Application a = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return null;
            }

            @Override
            public Set<Object> getSingletons() {
                return Collections.emptySet();
            }
        };
        new ApplicationHandler(a);
    }
}

