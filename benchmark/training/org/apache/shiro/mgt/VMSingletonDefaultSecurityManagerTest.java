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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.mgt;


import Ini.Section;
import IniRealm.USERS_SECTION_NAME;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since May 8, 2008 12:26:23 AM
 */
public class VMSingletonDefaultSecurityManagerTest {
    @Test
    public void testVMSingleton() {
        DefaultSecurityManager sm = new DefaultSecurityManager();
        Ini ini = new Ini();
        Ini.Section section = ini.addSection(USERS_SECTION_NAME);
        section.put("guest", "guest");
        sm.setRealm(new org.apache.shiro.realm.text.IniRealm(ini));
        SecurityUtils.setSecurityManager(sm);
        try {
            Subject subject = SecurityUtils.getSubject();
            AuthenticationToken token = new UsernamePasswordToken("guest", "guest");
            subject.login(token);
            subject.getSession().setAttribute("key", "value");
            Assert.assertTrue(subject.getSession().getAttribute("key").equals("value"));
            subject = SecurityUtils.getSubject();
            Assert.assertTrue(subject.isAuthenticated());
            Assert.assertTrue(subject.getSession().getAttribute("key").equals("value"));
        } finally {
            sm.destroy();
            // SHIRO-270:
            SecurityUtils.setSecurityManager(null);
        }
    }
}
