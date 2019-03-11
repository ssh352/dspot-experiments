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
package org.apache.hadoop.security.authentication.util;


import KerberosName.MECHANISM_HADOOP;
import KerberosName.MECHANISM_MIT;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestKerberosName {
    @Test
    public void testRules() throws Exception {
        checkTranslation(("omalley@" + (KerberosTestUtils.getRealm())), "omalley");
        checkTranslation(("hdfs/10.0.0.1@" + (KerberosTestUtils.getRealm())), "hdfs");
        checkTranslation("oom@YAHOO.COM", "oom");
        checkTranslation("johndoe/zoo@FOO.COM", "guest");
        checkTranslation("joe/admin@FOO.COM", "joe");
        checkTranslation("joe/root@FOO.COM", "root");
    }

    @Test
    public void testAntiPatterns() throws Exception {
        KerberosName.setRuleMechanism(MECHANISM_HADOOP);
        checkBadName("owen/owen/owen@FOO.COM");
        checkBadName("owen@foo/bar.com");
        checkBadTranslation("foo@ACME.COM");
        checkBadTranslation("root/joe@FOO.COM");
        KerberosName.setRuleMechanism(MECHANISM_MIT);
        checkTranslation("foo@ACME.COM", "foo@ACME.COM");
        checkTranslation("root/joe@FOO.COM", "root/joe@FOO.COM");
    }

    @Test
    public void testParsing() throws Exception {
        final String principalNameFull = "HTTP/abc.com@EXAMPLE.COM";
        final String principalNameWoRealm = "HTTP/abc.com";
        final String principalNameWoHost = "HTTP@EXAMPLE.COM";
        final KerberosName kerbNameFull = new KerberosName(principalNameFull);
        Assert.assertEquals("HTTP", kerbNameFull.getServiceName());
        Assert.assertEquals("abc.com", kerbNameFull.getHostName());
        Assert.assertEquals("EXAMPLE.COM", kerbNameFull.getRealm());
        final KerberosName kerbNamewoRealm = new KerberosName(principalNameWoRealm);
        Assert.assertEquals("HTTP", kerbNamewoRealm.getServiceName());
        Assert.assertEquals("abc.com", kerbNamewoRealm.getHostName());
        Assert.assertEquals(null, kerbNamewoRealm.getRealm());
        final KerberosName kerbNameWoHost = new KerberosName(principalNameWoHost);
        Assert.assertEquals("HTTP", kerbNameWoHost.getServiceName());
        Assert.assertEquals(null, kerbNameWoHost.getHostName());
        Assert.assertEquals("EXAMPLE.COM", kerbNameWoHost.getRealm());
    }

    @Test
    public void testToLowerCase() throws Exception {
        String rules = "RULE:[1:$1]/L\n" + ((("RULE:[2:$1]/L\n" + "RULE:[2:$1;$2](^.*;admin$)s/;admin$///L\n") + "RULE:[2:$1;$2](^.*;guest$)s/;guest$//g/L\n") + "DEFAULT");
        KerberosName.setRules(rules);
        KerberosName.printRules();
        checkTranslation("Joe@FOO.COM", "joe");
        checkTranslation("Joe/root@FOO.COM", "joe");
        checkTranslation("Joe/admin@FOO.COM", "joe");
        checkTranslation("Joe/guestguest@FOO.COM", "joe");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRuleMechanism() throws Exception {
        KerberosName.setRuleMechanism("INVALID_MECHANISM");
    }
}

