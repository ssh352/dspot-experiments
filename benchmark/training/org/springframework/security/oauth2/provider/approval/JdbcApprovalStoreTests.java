/**
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.approval;


import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;


/**
 *
 *
 * @author Dave Syer
 */
public class JdbcApprovalStoreTests extends AbstractTestApprovalStore {
    private JdbcApprovalStore store;

    private EmbeddedDatabase db;

    @Test
    public void testRevokeByExpiry() {
        store.setHandleRevocationsAsExpiry(true);
        Approval approval1 = new Approval("user", "client", "read", 10000, ApprovalStatus.APPROVED);
        Approval approval2 = new Approval("user", "client", "write", 10000, ApprovalStatus.APPROVED);
        Assert.assertTrue(store.addApprovals(Arrays.<Approval>asList(approval1, approval2)));
        store.revokeApprovals(Arrays.asList(approval1));
        Assert.assertEquals(2, store.getApprovals("user", "client").size());
        Assert.assertEquals(new Integer(1), queryForObject("SELECT COUNT(*) from oauth_approvals where userId='user' AND expiresAt < ?", Integer.class, new Date(((System.currentTimeMillis()) + 1000))));
    }
}

