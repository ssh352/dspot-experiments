/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v72;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateOrganizationUuidOnUsersTest {
    private static final long PAST = 5000000000L;

    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateOrganizationUuidOnUsersTest.class, "schema.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateOrganizationUuidOnUsersTest.NOW);

    private PopulateOrganizationUuidOnUsers underTest = new PopulateOrganizationUuidOnUsers(db.database(), system2);

    @Test
    public void update_users() throws SQLException {
        // User not migrated
        long userId = insertUser("USER_1", null);
        insertOrganization("ORG_1", userId);
        // User already migrated
        insertOrganization("ORG_2", null);
        insertUser("USER_2", "ORG_2");
        underTest.execute();
        assertUsers(tuple("USER_1", "ORG_1", PopulateOrganizationUuidOnUsersTest.NOW), tuple("USER_2", "ORG_2", PopulateOrganizationUuidOnUsersTest.PAST));
    }

    @Test
    public void does_nothing_when_no_personal_organization() throws SQLException {
        insertUser("USER_1", null);
        insertUser("USER_2", null);
        underTest.execute();
        assertUsers(tuple("USER_1", null, PopulateOrganizationUuidOnUsersTest.PAST), tuple("USER_2", null, PopulateOrganizationUuidOnUsersTest.PAST));
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        long userId = insertUser("USER_1", null);
        insertOrganization("ORG_1", userId);
        insertOrganization("ORG_2", null);
        insertUser("USER_2", "ORG_2");
        underTest.execute();
        underTest.execute();
        assertUsers(tuple("USER_1", "ORG_1", PopulateOrganizationUuidOnUsersTest.NOW), tuple("USER_2", "ORG_2", PopulateOrganizationUuidOnUsersTest.PAST));
    }
}

