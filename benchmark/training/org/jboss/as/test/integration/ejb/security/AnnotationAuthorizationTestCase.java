/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.security;


import java.util.Date;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.ejb.security.authorization.AttendanceRegistry;
import org.jboss.as.test.integration.ejb.security.authorization.AttendanceRegistrySLSB;
import org.jboss.as.test.integration.ejb.security.authorization.DenyAllOverrideBean;
import org.jboss.as.test.integration.ejb.security.authorization.PermitAllOverrideBean;
import org.jboss.as.test.integration.ejb.security.authorization.RolesAllowedOverrideBean;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test case to test the general authorization requirements for annotated beans, more specific requirements such as RunAs
 * handling will be in their own test case.
 * <p/>
 * EJB 3.1 Section 17.3.2.1
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ EjbSecurityDomainSetup.class })
@Category(CommonCriteria.class)
public class AnnotationAuthorizationTestCase {
    private static final Logger log = Logger.getLogger(AnnotationAuthorizationTestCase.class.getName());

    @EJB(mappedName = "java:global/ejb3security/RolesAllowedOverrideBean")
    private RolesAllowedOverrideBean rolesAllowedOverridenBean;

    @EJB(mappedName = "java:global/ejb3security/AttendanceRegistrySLSB!org.jboss.as.test.integration.ejb.security.authorization.AttendanceRegistry")
    private AttendanceRegistry attendanceRegistryBean;

    /* Test overrides within a bean annotated @RolesAllowed at bean level. */
    @Test
    public void testRolesAllowedOverriden_NoUser() throws Exception {
        try {
            rolesAllowedOverridenBean.defaultEcho("1");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        try {
            rolesAllowedOverridenBean.denyAllEcho("2");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        String response = rolesAllowedOverridenBean.permitAllEcho("3");
        Assert.assertEquals("3", response);
        try {
            rolesAllowedOverridenBean.role2Echo("4");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testRolesAllowedOverriden_User1() throws Exception {
        final Callable<Void> callable = () -> {
            String response = rolesAllowedOverridenBean.defaultEcho("1");
            Assert.assertEquals("1", response);
            try {
                rolesAllowedOverridenBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            response = rolesAllowedOverridenBean.permitAllEcho("3");
            Assert.assertEquals("3", response);
            try {
                rolesAllowedOverridenBean.role2Echo("4");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testRolesAllowedOverridenInBaseClass_Admin() throws Exception {
        final Callable<Void> callable = () -> {
            try {
                rolesAllowedOverridenBean.aMethod("aMethod");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = rolesAllowedOverridenBean.bMethod("bMethod");
            Assert.assertEquals("bMethod", response);
            return null;
        };
        Util.switchIdentity("admin", "admin", callable);
    }

    @Test
    public void testRolesAllowedOverridenInBaseClass_HR() throws Exception {
        final Callable<Void> callable = () -> {
            String response = rolesAllowedOverridenBean.aMethod("aMethod");
            Assert.assertEquals("aMethod", response);
            try {
                rolesAllowedOverridenBean.bMethod("bMethod");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            return null;
        };
        Util.switchIdentity("hr", "hr", callable);
    }

    @Test
    public void testRolesAllowedOverriden_User2() throws Exception {
        final Callable<Void> callable = () -> {
            try {
                rolesAllowedOverridenBean.defaultEcho("1");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            try {
                rolesAllowedOverridenBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = rolesAllowedOverridenBean.permitAllEcho("3");
            Assert.assertEquals("3", response);
            response = rolesAllowedOverridenBean.role2Echo("4");
            Assert.assertEquals("4", response);
            return null;
        };
        Util.switchIdentity("user2", "password2", callable);
    }

    /* Test overrides of bean annotated at bean level with @PermitAll */
    @EJB(mappedName = "java:global/ejb3security/PermitAllOverrideBean")
    private PermitAllOverrideBean permitAllOverrideBean;

    @Test
    public void testPermitAllOverride_NoUser() throws Exception {
        String response = permitAllOverrideBean.defaultEcho("1");
        Assert.assertEquals("1", response);
        try {
            permitAllOverrideBean.denyAllEcho("2");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        try {
            permitAllOverrideBean.role1Echo("3");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testPermitAllOverride_User1() throws Exception {
        final Callable<Void> callable = () -> {
            String response = permitAllOverrideBean.defaultEcho("1");
            Assert.assertEquals("1", response);
            try {
                permitAllOverrideBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            response = permitAllOverrideBean.role1Echo("3");
            Assert.assertEquals("3", response);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    /* Test overrides of ben annotated at bean level with @DenyAll */
    @EJB(mappedName = "java:global/ejb3security/DenyAllOverrideBean")
    private DenyAllOverrideBean denyAllOverrideBean;

    @Test
    public void testDenyAllOverride_NoUser() throws Exception {
        try {
            denyAllOverrideBean.defaultEcho("1");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        String response = denyAllOverrideBean.permitAllEcho("2");
        Assert.assertEquals("2", response);
        try {
            denyAllOverrideBean.role1Echo("3");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testDenyAllOverride_User1() throws Exception {
        final Callable<Void> callable = () -> {
            try {
                denyAllOverrideBean.defaultEcho("1");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = denyAllOverrideBean.permitAllEcho("2");
            Assert.assertEquals("2", response);
            response = denyAllOverrideBean.role1Echo("3");
            Assert.assertEquals("3", response);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    /**
     * Tests that a method which accepts an array as a parameter and is marked with @PermitAll is allowed to be invoked by clients.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPermitAllMethodWithArrayParams() throws Exception {
        final Callable<Void> callable = () -> {
            final String[] messages = new String[]{ "foo", "bar" };
            final String[] echoes = denyAllOverrideBean.permitAllEchoWithArrayParams(messages);
            Assert.assertArrayEquals("Unexpected echoes returned by bean method", messages, echoes);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    /**
     * Tests that, when a EJB has overloaded methods with the same number of arguments but with different parameter types
     * and when there's a {@link Method#isBridge() bridge method} involved (due to Java generics), then the security annotations
     * on such methods are properly processed and the right method is assigned for the correct set of allowed access roles.
     *
     * @throws Exception
     * 		
     * @see <a href="https://issues.jboss.org/browse/WFLY-8548">WFLY-8548</a> for more details
     */
    @Test
    public void testOverloadedMethodsWithDifferentAuthorization() throws Exception {
        final String user = "Jane Doe";
        final Date date = new Date();
        // expected to pass through fine (since the invocation is expected to happen on a @PermitAll method)
        final String entryForPermitAll = attendanceRegistryBean.recordEntry(user, new AttendanceRegistrySLSB.DefaultTimeProvider(date));
        Assert.assertEquals("Unexpected entry returned for @PermitAll invocation", ((("(PermitAll) - User " + user) + " logged in at ") + (date.getTime())), entryForPermitAll);
        // now call the (overloaded) method on the bean, after switching to a specific role that's allowed to access that method
        final Callable<String> specificRoleMethodCall = () -> attendanceRegistryBean.recordEntry(user, date.getTime());
        final String entryForSpecificRole = Util.switchIdentity("user2", "password2", specificRoleMethodCall);
        Assert.assertEquals("Unexpected entry returned for @RolesAllowed invocation", ((("User " + user) + " logged in at ") + (date.getTime())), entryForSpecificRole);
    }
}

