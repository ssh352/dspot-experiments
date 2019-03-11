/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.resources;


import org.apache.shiro.subject.Subject;
import org.graylog2.shared.rest.resources.RestResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RestResourceBaseTest {
    @Test
    public void testisAnyPermitted() {
        final RestResourceBaseTest.PermissionDeniedResource failingResource = new RestResourceBaseTest.PermissionDeniedResource();
        final RestResourceBaseTest.AllPermissionsGrantedResource allGranted = new RestResourceBaseTest.AllPermissionsGrantedResource();
        final RestResourceBaseTest.SomePermissionsGrantedResource someGranted = new RestResourceBaseTest.SomePermissionsGrantedResource();
        Assert.assertFalse("User doesn't have any permissions", failingResource.runCheck());
        Assert.assertTrue("User has all permissions", allGranted.runCheck());
        Assert.assertTrue("User has some permissions", someGranted.runCheck());
    }

    private static class PermissionDeniedResource extends RestResource {
        @Override
        protected Subject getSubject() {
            final Subject mock = Mockito.mock(Subject.class);
            Mockito.when(mock.isPermitted(((String[]) (ArgumentMatchers.any())))).thenReturn(new boolean[]{ false, false });
            return mock;
        }

        public boolean runCheck() {
            return isAnyPermitted(new String[]{ "a:b", "a:c" }, "instance");
        }
    }

    private static class AllPermissionsGrantedResource extends RestResource {
        @Override
        protected Subject getSubject() {
            final Subject mock = Mockito.mock(Subject.class);
            Mockito.when(mock.isPermitted(((String[]) (ArgumentMatchers.any())))).thenReturn(new boolean[]{ true, true });
            return mock;
        }

        public boolean runCheck() {
            return isAnyPermitted(new String[]{ "a:b", "a:c" }, "instance");
        }
    }

    private static class SomePermissionsGrantedResource extends RestResource {
        @Override
        protected Subject getSubject() {
            final Subject mock = Mockito.mock(Subject.class);
            Mockito.when(mock.isPermitted(((String[]) (ArgumentMatchers.any())))).thenReturn(new boolean[]{ false, true });
            return mock;
        }

        public boolean runCheck() {
            return isAnyPermitted(new String[]{ "a:b", "a:c" }, "instance");
        }
    }
}

