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
package org.glassfish.jersey.ext.cdi1x.internal;


import javax.annotation.Priority;
import javax.enterprise.inject.spi.BeanManager;
import mockit.Mock;
import org.glassfish.jersey.ext.cdi1x.internal.spi.BeanManagerProvider;
import org.glassfish.jersey.ext.cdi1x.internal.spi.InjectionManagerStore;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link org.glassfish.jersey.ext.cdi1x.internal.CdiUtil}.
 *
 * @author Michal Gajdos
 */
public class CdiUtilTest {
    public static class TestBeanManagerProvider implements BeanManagerProvider {
        @Override
        public BeanManager getBeanManager() {
            throw new RuntimeException("BeanManager!");
        }
    }

    @Priority(500)
    public static class MyServiceOne implements MyService {}

    @Priority(100)
    public static class MyServiceTwo implements MyService {}

    @Priority(300)
    public static class MyServiceThree implements MyService {}

    @Test
    public void testLookupService() throws Exception {
        Assert.assertThat(CdiUtil.lookupService(MyService.class), CoreMatchers.instanceOf(CdiUtilTest.MyServiceTwo.class));
    }

    @Test
    public void testLookupServiceNegative() throws Exception {
        Assert.assertThat(CdiUtil.lookupService(CdiUtil.class), CoreMatchers.nullValue());
    }

    public static class TestInjectionManagerStore implements InjectionManagerStore {
        @Override
        public void registerInjectionManager(final InjectionManager injectionManager) {
        }

        @Override
        public InjectionManager getEffectiveInjectionManager() {
            return null;
        }
    }

    @Test
    public void createHk2LocatorManagerCustom() throws Exception {
        Assert.assertThat(CdiUtil.createHk2InjectionManagerStore(), CoreMatchers.instanceOf(CdiUtilTest.TestInjectionManagerStore.class));
    }

    @Test
    public void createHk2LocatorManagerDefault() throws Exception {
        new mockit.MockUp<CdiUtil>() {
            @Mock
            @SuppressWarnings("UnusedDeclaration")
            <T> T lookupService(final Class<T> clazz) {
                return null;
            }
        };
        Assert.assertThat(CdiUtil.createHk2InjectionManagerStore(), CoreMatchers.instanceOf(SingleInjectionManagerStore.class));
    }
}

