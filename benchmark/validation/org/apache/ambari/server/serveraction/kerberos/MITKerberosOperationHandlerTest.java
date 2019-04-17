/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.kerberos;


import KDCKerberosOperationHandler.InteractivePasswordHandler;
import MITKerberosOperationHandler.KERBEROS_ENV_KADMIN_PRINCIPAL_NAME;
import MITKerberosOperationHandler.KERBEROS_ENV_KDC_CREATE_ATTRIBUTES;
import ShellCommandUtil.Result;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.security.credential.PrincipalKeyCredential;
import org.apache.ambari.server.utils.ShellCommandUtil;
import org.easymock.Capture;
import org.junit.Test;


public class MITKerberosOperationHandlerTest extends KDCKerberosOperationHandlerTest {
    private static Method methodIsOpen;

    private static Method methodPrincipalExists;

    private static Method methodInvokeKAdmin;

    private static final Map<String, String> KERBEROS_ENV_MAP;

    static {
        Map<String, String> map = new HashMap<>(KerberosOperationHandlerTest.DEFAULT_KERBEROS_ENV_MAP);
        map.put(KERBEROS_ENV_KDC_CREATE_ATTRIBUTES, "-attr1 -attr2 foo=345");
        KERBEROS_ENV_MAP = Collections.unmodifiableMap(map);
    }

    private Injector injector;

    @Test
    public void testSetPrincipalPassword() throws Exception {
        MITKerberosOperationHandler handler = createMockedHandler(MITKerberosOperationHandlerTest.methodIsOpen, MITKerberosOperationHandlerTest.methodPrincipalExists);
        expect(handler.isOpen()).andReturn(true).atLeastOnce();
        expect(handler.principalExists(KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, false)).andReturn(true).atLeastOnce();
        expect(handler.principalExists(null, false)).andReturn(false).atLeastOnce();
        expect(handler.principalExists("", false)).andReturn(false).atLeastOnce();
        replayAll();
        Integer expected = 0;
        // setPrincipalPassword should always return 0
        Assert.assertEquals(expected, handler.setPrincipalPassword(KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, null, false));
        Assert.assertEquals(expected, handler.setPrincipalPassword(KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, "", false));
        try {
            handler.setPrincipalPassword(null, KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD, false);
            Assert.fail("Expected KerberosPrincipalDoesNotExistException");
        } catch (KerberosPrincipalDoesNotExistException e) {
            // Expected...
        }
        try {
            handler.setPrincipalPassword("", KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD, false);
            Assert.fail("Expected KerberosPrincipalDoesNotExistException");
        } catch (KerberosPrincipalDoesNotExistException e) {
            // Expected...
        }
        verifyAll();
    }

    @Test
    public void testCreateServicePrincipal_AdditionalAttributes() throws Exception {
        Capture<? extends String> query = newCapture();
        ShellCommandUtil.Result result1 = createNiceMock(Result.class);
        expect(result1.getStderr()).andReturn("").anyTimes();
        expect(result1.getStdout()).andReturn((("Principal \"" + (KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL)) + "\" created\"")).anyTimes();
        ShellCommandUtil.Result result2 = createNiceMock(Result.class);
        expect(result2.getStderr()).andReturn("").anyTimes();
        expect(result2.getStdout()).andReturn("Key: vno 1").anyTimes();
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true);
        MITKerberosOperationHandler handler = createMockedHandler(MITKerberosOperationHandlerTest.methodInvokeKAdmin, KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).once();
        expect(handler.invokeKAdmin(capture(query))).andReturn(result1).once();
        replayAll();
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, MITKerberosOperationHandlerTest.KERBEROS_ENV_MAP);
        handler.createPrincipal(KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD, false);
        handler.close();
        verifyAll();
        Assert.assertTrue(query.getValue().contains(((" " + (MITKerberosOperationHandlerTest.KERBEROS_ENV_MAP.get(KERBEROS_ENV_KDC_CREATE_ATTRIBUTES))) + " ")));
    }

    @Test
    public void testCreateServicePrincipalExceptions() throws Exception {
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true);
        MITKerberosOperationHandler handler = createMockedHandler(KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).once();
        replayAll();
        handler.open(new PrincipalKeyCredential(KerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD), KerberosOperationHandlerTest.DEFAULT_REALM, MITKerberosOperationHandlerTest.KERBEROS_ENV_MAP);
        try {
            handler.createPrincipal(null, KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD, false);
            Assert.fail("KerberosOperationException not thrown for null principal");
        } catch (Throwable t) {
            Assert.assertEquals(KerberosOperationException.class, t.getClass());
        }
        try {
            handler.createPrincipal("", KerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD, false);
            Assert.fail("KerberosOperationException not thrown for empty principal");
        } catch (Throwable t) {
            Assert.assertEquals(KerberosOperationException.class, t.getClass());
        }
        verifyAll();
    }

    @Test(expected = KerberosKDCConnectionException.class)
    public void testKDCConnectionException() throws Exception {
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true).anyTimes();
        ShellCommandUtil.Result kadminResult = createMock(Result.class);
        expect(kadminResult.getExitCode()).andReturn(1).anyTimes();
        expect(kadminResult.isSuccessful()).andReturn(false).anyTimes();
        expect(kadminResult.getStderr()).andReturn("kadmin: Cannot contact any KDC for requested realm while initializing kadmin interface").anyTimes();
        expect(kadminResult.getStdout()).andReturn("Authenticating as principal admin/admin with password.").anyTimes();
        MITKerberosOperationHandler handler = createMockedHandler(KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).once();
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kadminResult).once();
        replayAll();
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, MITKerberosOperationHandlerTest.KERBEROS_ENV_MAP);
        handler.testAdministratorCredentials();
        handler.close();
        verifyAll();
    }

    @Test(expected = KerberosKDCConnectionException.class)
    public void testTestAdministratorCredentialsKDCConnectionException2() throws Exception {
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true).anyTimes();
        ShellCommandUtil.Result kadminResult = createMock(Result.class);
        expect(kadminResult.getExitCode()).andReturn(1).anyTimes();
        expect(kadminResult.isSuccessful()).andReturn(false).anyTimes();
        expect(kadminResult.getStderr()).andReturn("kadmin: Cannot resolve network address for admin server in requested realm while initializing kadmin interface").anyTimes();
        expect(kadminResult.getStdout()).andReturn("Authenticating as principal admin/admin with password.").anyTimes();
        MITKerberosOperationHandler handler = createMockedHandler(KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).once();
        expect(handler.executeCommand(anyObject(String[].class), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kadminResult).once();
        replayAll();
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, MITKerberosOperationHandlerTest.KERBEROS_ENV_MAP);
        handler.testAdministratorCredentials();
        handler.close();
        verifyAll();
    }

    @Test
    public void testGetAdminServerHost() throws KerberosOperationException {
        ShellCommandUtil.Result kinitResult = createMock(Result.class);
        expect(kinitResult.isSuccessful()).andReturn(true).anyTimes();
        Capture<String[]> capturedKinitCommand = newCapture(CaptureType.ALL);
        MITKerberosOperationHandler handler = createMockedHandler(KDCKerberosOperationHandlerTest.methodExecuteCommand);
        expect(handler.executeCommand(capture(capturedKinitCommand), anyObject(Map.class), anyObject(InteractivePasswordHandler.class))).andReturn(kinitResult).anyTimes();
        Map<String, String> config = new HashMap<>();
        config.put("encryption_types", "aes des3-cbc-sha1 rc4 des-cbc-md5");
        config.put(KERBEROS_ENV_KADMIN_PRINCIPAL_NAME, "kadmin/kdc.example.com");
        replayAll();
        config.put("admin_server_host", "kdc.example.com");
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, config);
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(false));
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(true));
        handler.close();
        config.put("admin_server_host", "kdc.example.com:749");
        handler.open(getAdminCredentials(), KerberosOperationHandlerTest.DEFAULT_REALM, config);
        Assert.assertEquals("kdc.example.com", handler.getAdminServerHost(false));
        Assert.assertEquals("kdc.example.com:749", handler.getAdminServerHost(true));
        handler.close();
        verifyAll();
        Assert.assertTrue(capturedKinitCommand.hasCaptured());
        List<String[]> capturedValues = capturedKinitCommand.getValues();
        Assert.assertEquals(2, capturedValues.size());
        // The capture values will be an array of strings used to build the command:
        // ["/usr/bin/kinit", "-c", "SOME_FILE_PATH", "-S", "SERVER_PRINCIPAL", "CLIENT_PRINCIPAL"]
        // We are interested in the 4th item in the array - the service's principal.
        // It must not contain the port else authentication will fail
        Assert.assertEquals("kadmin/kdc.example.com", capturedValues.get(0)[4]);
        Assert.assertEquals("kadmin/kdc.example.com", capturedValues.get(1)[4]);
    }
}
