/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.models;


import com.google.bigtable.v2.ReadModifyWriteRowRequest;
import com.google.bigtable.v2.ReadModifyWriteRule;
import com.google.cloud.bigtable.data.v2.internal.NameUtil;
import com.google.cloud.bigtable.data.v2.internal.RequestContext;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;


public class ReadModifyWriteRowTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private static final String APP_PROFILE_ID = "fake-profile";

    private static final RequestContext REQUEST_CONTEXT = RequestContext.create(ReadModifyWriteRowTest.PROJECT_ID, ReadModifyWriteRowTest.INSTANCE_ID, ReadModifyWriteRowTest.APP_PROFILE_ID);

    @Test
    public void testAppend() {
        ReadModifyWriteRow mutation = ReadModifyWriteRow.create(ReadModifyWriteRowTest.TABLE_ID, "fake-key").append("fake-family", ByteString.copyFromUtf8("fake-qualifier"), ByteString.copyFromUtf8("fake-value")).append("fake-family", "fake-qualifier-str", "fake-value-str");
        ReadModifyWriteRowRequest actualProto = mutation.toProto(ReadModifyWriteRowTest.REQUEST_CONTEXT);
        ReadModifyWriteRowRequest expected = ReadModifyWriteRowRequest.newBuilder().setTableName(NameUtil.formatTableName(ReadModifyWriteRowTest.PROJECT_ID, ReadModifyWriteRowTest.INSTANCE_ID, ReadModifyWriteRowTest.TABLE_ID)).setAppProfileId(ReadModifyWriteRowTest.APP_PROFILE_ID).setRowKey(ByteString.copyFromUtf8("fake-key")).addRules(ReadModifyWriteRule.newBuilder().setFamilyName("fake-family").setColumnQualifier(ByteString.copyFromUtf8("fake-qualifier")).setAppendValue(ByteString.copyFromUtf8("fake-value"))).addRules(ReadModifyWriteRule.newBuilder().setFamilyName("fake-family").setColumnQualifier(ByteString.copyFromUtf8("fake-qualifier-str")).setAppendValue(ByteString.copyFromUtf8("fake-value-str"))).build();
        assertThat(actualProto).isEqualTo(expected);
    }

    @Test
    public void testIncrement() {
        ReadModifyWriteRow mutation = ReadModifyWriteRow.create(ReadModifyWriteRowTest.TABLE_ID, "fake-key").increment("fake-family", ByteString.copyFromUtf8("fake-qualifier"), 1).increment("fake-family", "fake-qualifier-str", 2);
        ReadModifyWriteRowRequest actualProto = mutation.toProto(ReadModifyWriteRowTest.REQUEST_CONTEXT);
        assertThat(actualProto).isEqualTo(ReadModifyWriteRowRequest.newBuilder().setTableName(NameUtil.formatTableName(ReadModifyWriteRowTest.PROJECT_ID, ReadModifyWriteRowTest.INSTANCE_ID, ReadModifyWriteRowTest.TABLE_ID)).setAppProfileId(ReadModifyWriteRowTest.APP_PROFILE_ID).setRowKey(ByteString.copyFromUtf8("fake-key")).addRules(ReadModifyWriteRule.newBuilder().setFamilyName("fake-family").setColumnQualifier(ByteString.copyFromUtf8("fake-qualifier")).setIncrementAmount(1)).addRules(ReadModifyWriteRule.newBuilder().setFamilyName("fake-family").setColumnQualifier(ByteString.copyFromUtf8("fake-qualifier-str")).setIncrementAmount(2)).build());
    }

    @Test
    public void serializationTest() throws IOException, ClassNotFoundException {
        ReadModifyWriteRow expected = ReadModifyWriteRow.create(ReadModifyWriteRowTest.TABLE_ID, "fake-key").increment("fake-family", ByteString.copyFromUtf8("fake-qualifier"), 1).append("fake-family", "a", "b");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(expected);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ReadModifyWriteRow actual = ((ReadModifyWriteRow) (ois.readObject()));
        assertThat(actual.toProto(ReadModifyWriteRowTest.REQUEST_CONTEXT)).isEqualTo(expected.toProto(ReadModifyWriteRowTest.REQUEST_CONTEXT));
    }
}

