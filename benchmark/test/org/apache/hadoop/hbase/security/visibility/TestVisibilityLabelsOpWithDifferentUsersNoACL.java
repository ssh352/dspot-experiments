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
package org.apache.hadoop.hbase.security.visibility;


import com.google.protobuf.ByteString;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityLabelsOpWithDifferentUsersNoACL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsOpWithDifferentUsersNoACL.class);

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    @Rule
    public final TestName TEST_NAME = new TestName();

    private static User SUPERUSER;

    private static User NORMAL_USER;

    private static User NORMAL_USER1;

    @Test
    public void testLabelsTableOpsWithDifferentUsers() throws Throwable {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsOpWithDifferentUsersNoACL.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityLabelsOpWithDifferentUsersNoACL.CONFIDENTIAL, TestVisibilityLabelsOpWithDifferentUsersNoACL.PRIVATE }, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        VisibilityLabelsResponse response = TestVisibilityLabelsOpWithDifferentUsersNoACL.SUPERUSER.runAs(action);
        Assert.assertTrue(response.getResult(0).getException().getValue().isEmpty());
        Assert.assertTrue(response.getResult(1).getException().getValue().isEmpty());
        // Ideally this should not be allowed.  this operation should fail or do nothing.
        action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsOpWithDifferentUsersNoACL.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ TestVisibilityLabelsOpWithDifferentUsersNoACL.CONFIDENTIAL, TestVisibilityLabelsOpWithDifferentUsersNoACL.PRIVATE }, "user3");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        response = TestVisibilityLabelsOpWithDifferentUsersNoACL.NORMAL_USER1.runAs(action);
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(0).getException().getName());
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(1).getException().getName());
        PrivilegedExceptionAction<GetAuthsResponse> action1 = new PrivilegedExceptionAction<GetAuthsResponse>() {
            @Override
            public GetAuthsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsOpWithDifferentUsersNoACL.conf)) {
                    return VisibilityClient.getAuths(conn, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        GetAuthsResponse authsResponse = TestVisibilityLabelsOpWithDifferentUsersNoACL.NORMAL_USER.runAs(action1);
        Assert.assertTrue(authsResponse.getAuthList().isEmpty());
        authsResponse = TestVisibilityLabelsOpWithDifferentUsersNoACL.NORMAL_USER1.runAs(action1);
        Assert.assertTrue(authsResponse.getAuthList().isEmpty());
        authsResponse = TestVisibilityLabelsOpWithDifferentUsersNoACL.SUPERUSER.runAs(action1);
        List<String> authsList = new java.util.ArrayList(authsResponse.getAuthList().size());
        for (ByteString authBS : authsResponse.getAuthList()) {
            authsList.add(Bytes.toString(authBS.toByteArray()));
        }
        Assert.assertEquals(2, authsList.size());
        Assert.assertTrue(authsList.contains(TestVisibilityLabelsOpWithDifferentUsersNoACL.CONFIDENTIAL));
        Assert.assertTrue(authsList.contains(TestVisibilityLabelsOpWithDifferentUsersNoACL.PRIVATE));
        PrivilegedExceptionAction<VisibilityLabelsResponse> action2 = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabelsOpWithDifferentUsersNoACL.conf)) {
                    return VisibilityClient.clearAuths(conn, new String[]{ TestVisibilityLabelsOpWithDifferentUsersNoACL.CONFIDENTIAL, TestVisibilityLabelsOpWithDifferentUsersNoACL.PRIVATE }, "user1");
                } catch (Throwable e) {
                }
                return null;
            }
        };
        response = TestVisibilityLabelsOpWithDifferentUsersNoACL.NORMAL_USER1.runAs(action2);
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(0).getException().getName());
        Assert.assertEquals("org.apache.hadoop.hbase.security.AccessDeniedException", response.getResult(1).getException().getName());
        response = TestVisibilityLabelsOpWithDifferentUsersNoACL.SUPERUSER.runAs(action2);
        Assert.assertTrue(response.getResult(0).getException().getValue().isEmpty());
        Assert.assertTrue(response.getResult(1).getException().getValue().isEmpty());
        authsResponse = TestVisibilityLabelsOpWithDifferentUsersNoACL.SUPERUSER.runAs(action1);
        Assert.assertTrue(authsResponse.getAuthList().isEmpty());
    }
}
