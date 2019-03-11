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
package org.graylog2.security.realm;


import DateTimeZone.UTC;
import com.google.common.collect.Maps;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import java.util.Collections;
import java.util.HashMap;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.annotations.LoadSchema;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.graylog2.ApacheDirectoryTestServiceFactory;
import org.graylog2.Configuration;
import org.graylog2.plugin.database.users.User;
import org.graylog2.security.ldap.LdapConnector;
import org.graylog2.security.ldap.LdapSettingsService;
import org.graylog2.shared.security.Permissions;
import org.graylog2.shared.security.ldap.LdapEntry;
import org.graylog2.shared.security.ldap.LdapSettings;
import org.graylog2.shared.users.UserService;
import org.graylog2.users.RoleService;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@CreateDS(name = "LdapUserAuthenticatorTest", factory = ApacheDirectoryTestServiceFactory.class// Ensures a unique storage location
, partitions = { @CreatePartition(name = "example.com", type = AvlPartition.class, suffix = "dc=example,dc=com", contextEntry = @ContextEntry(entryLdif = "dn: dc=example,dc=com\n" + (("dc: example\n" + "objectClass: top\n") + "objectClass: domain\n\n")), indexes = { @CreateIndex(attribute = "objectClass"), @CreateIndex(attribute = "dc"), @CreateIndex(attribute = "ou") }) }, loadedSchemas = { @LoadSchema(name = "nis", enabled = true) })
@ApplyLdifFiles("org/graylog2/security/ldap/base.ldif")
public class LdapUserAuthenticatorTest extends AbstractLdapTestUnit {
    private static final String ADMIN_DN = "uid=admin,ou=system";

    private static final String ADMIN_PASSWORD = "secret";

    private static final AuthenticationToken VALID_TOKEN = new UsernamePasswordToken("john", "test");

    private static final AuthenticationToken INVALID_TOKEN = new UsernamePasswordToken("john", "__invalid__");

    private static final String PASSWORD_SECRET = "r8Om85b0zgHmiGsK86T3ZFlmSIdMd3hcKmOa4T60MSPEobfRCTLNOK4T91GdHbGx";

    private LdapConnector ldapConnector;

    private LdapServer server;

    private LdapSettingsService ldapSettingsService;

    private LdapSettings ldapSettings;

    private Configuration configuration;

    private UserService userService;

    @Test
    public void testDoGetAuthenticationInfo() throws Exception {
        final LdapUserAuthenticator authenticator = Mockito.spy(new LdapUserAuthenticator(ldapConnector, ldapSettingsService, userService, Mockito.mock(RoleService.class), DateTimeZone.UTC));
        Mockito.when(ldapSettingsService.load()).thenReturn(ldapSettings);
        Mockito.doReturn(Mockito.mock(User.class)).when(authenticator).syncFromLdapEntry(ArgumentMatchers.any(LdapEntry.class), ArgumentMatchers.any(LdapSettings.class), ArgumentMatchers.anyString());
        assertThat(authenticator.doGetAuthenticationInfo(LdapUserAuthenticatorTest.VALID_TOKEN)).isNotNull();
        assertThat(authenticator.doGetAuthenticationInfo(LdapUserAuthenticatorTest.INVALID_TOKEN)).isNull();
    }

    @Test
    public void testDoGetAuthenticationInfoDeniesEmptyPassword() throws Exception {
        final LdapUserAuthenticator authenticator = new LdapUserAuthenticator(ldapConnector, ldapSettingsService, userService, Mockito.mock(RoleService.class), DateTimeZone.UTC);
        Mockito.when(ldapSettingsService.load()).thenReturn(ldapSettings);
        assertThat(authenticator.doGetAuthenticationInfo(new UsernamePasswordToken("john", ((char[]) (null))))).isNull();
        assertThat(authenticator.doGetAuthenticationInfo(new UsernamePasswordToken("john", new char[0]))).isNull();
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testSyncFromLdapEntry() {
        final LdapUserAuthenticator authenticator = Mockito.spy(new LdapUserAuthenticator(ldapConnector, ldapSettingsService, userService, Mockito.mock(RoleService.class), DateTimeZone.UTC));
        final LdapEntry userEntry = new LdapEntry();
        final LdapSettings ldapSettings = Mockito.mock(LdapSettings.class);
        Mockito.when(ldapSettings.getDisplayNameAttribute()).thenReturn("displayName");
        Mockito.when(ldapSettings.getDefaultGroupId()).thenReturn("54e3deadbeefdeadbeef0001");
        Mockito.when(ldapSettings.getAdditionalDefaultGroupIds()).thenReturn(Collections.emptySet());
        Mockito.when(userService.create()).thenReturn(new org.graylog2.users.UserImpl(null, new Permissions(Collections.emptySet()), Maps.newHashMap()));
        final User ldapUser = authenticator.syncFromLdapEntry(userEntry, ldapSettings, "user");
        assertThat(ldapUser).isNotNull();
        assertThat(ldapUser.isExternalUser()).isTrue();
        assertThat(ldapUser.getName()).isEqualTo("user");
        assertThat(ldapUser.getEmail()).isEqualTo("user@localhost");
        assertThat(ldapUser.getHashedPassword()).isEqualTo("User synced from LDAP.");
        assertThat(ldapUser.getTimeZone()).isEqualTo(UTC);
        assertThat(ldapUser.getRoleIds()).containsOnly("54e3deadbeefdeadbeef0001");
        assertThat(ldapUser.getPermissions()).isNotEmpty();
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testSyncFromLdapEntryExistingUser() {
        final LdapUserAuthenticator authenticator = Mockito.spy(new LdapUserAuthenticator(ldapConnector, ldapSettingsService, userService, Mockito.mock(RoleService.class), DateTimeZone.UTC));
        final LdapEntry userEntry = new LdapEntry();
        final LdapSettings ldapSettings = Mockito.mock(LdapSettings.class);
        Mockito.when(ldapSettings.getDisplayNameAttribute()).thenReturn("displayName");
        Mockito.when(ldapSettings.getDefaultGroupId()).thenReturn("54e3deadbeefdeadbeef0001");
        Mockito.when(ldapSettings.getAdditionalDefaultGroupIds()).thenReturn(Collections.emptySet());
        final HashMap<String, Object> fields = Maps.newHashMap();
        fields.put("permissions", Collections.singletonList("test:permission:1234"));
        Mockito.when(userService.load(ArgumentMatchers.anyString())).thenReturn(new org.graylog2.users.UserImpl(null, new Permissions(Collections.emptySet()), fields));
        final User ldapUser = authenticator.syncFromLdapEntry(userEntry, ldapSettings, "user");
        assertThat(ldapUser).isNotNull();
        assertThat(ldapUser.getPermissions()).contains("test:permission:1234");
        assertThat(ldapUser.isExternalUser()).isTrue();
        assertThat(ldapUser.getName()).isEqualTo("user");
        assertThat(ldapUser.getEmail()).isEqualTo("user@localhost");
        assertThat(ldapUser.getHashedPassword()).isEqualTo("User synced from LDAP.");
        assertThat(ldapUser.getTimeZone()).isEqualTo(UTC);
        assertThat(ldapUser.getRoleIds()).containsOnly("54e3deadbeefdeadbeef0001");
        assertThat(ldapUser.getPermissions()).isNotEmpty();
    }
}

