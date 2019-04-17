/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.recovery;


import AuthenticationInfo.ANONYMOUS;
import Job.Status.ERROR;
import Job.Status.FINISHED;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.Map;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.zeppelin.interpreter.recovery.StopInterpreter;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.rest.AbstractTestRestApi;
import org.apache.zeppelin.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class RecoveryTest extends AbstractTestRestApi {
    private Gson gson = new Gson();

    private static File recoveryDir = null;

    private Notebook notebook;

    @Test
    public void testRecovery() throws Exception {
        Note note1 = notebook.createNote("note1", ANONYMOUS);
        // run python interpreter and create new variable `user`
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python user='abc'");
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        TestUtils.getInstance(Notebook.class).saveNote(note1, ANONYMOUS);
        // shutdown zeppelin and restart it
        AbstractTestRestApi.shutDown();
        AbstractTestRestApi.startUp(RecoveryTest.class.getSimpleName(), false);
        // run the paragraph again, but change the text to print variable `user`
        note1 = TestUtils.getInstance(Notebook.class).getNote(note1.getId());
        p1 = note1.getParagraph(p1.getId());
        p1.setText("%python print(user)");
        post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals("abc\n", p1.getReturn().message().get(0).getData());
    }

    @Test
    public void testRecovery_2() throws Exception {
        Note note1 = notebook.createNote("note2", ANONYMOUS);
        // run python interpreter and create new variable `user`
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python user='abc'");
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        TestUtils.getInstance(Notebook.class).saveNote(note1, ANONYMOUS);
        // restart the python interpreter
        TestUtils.getInstance(Notebook.class).getInterpreterSettingManager().restart(getInterpreterSetting().getId());
        // shutdown zeppelin and restart it
        AbstractTestRestApi.shutDown();
        AbstractTestRestApi.startUp(RecoveryTest.class.getSimpleName(), false);
        // run the paragraph again, but change the text to print variable `user`.
        // can not recover the python interpreter, because it has been shutdown.
        note1 = TestUtils.getInstance(Notebook.class).getNote(note1.getId());
        p1 = note1.getParagraph(p1.getId());
        p1.setText("%python print(user)");
        post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(ERROR, p1.getStatus());
    }

    @Test
    public void testRecovery_3() throws Exception {
        Note note1 = TestUtils.getInstance(Notebook.class).createNote("note3", ANONYMOUS);
        // run python interpreter and create new variable `user`
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%python user='abc'");
        PostMethod post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertThat(post, isAllowed());
        Map<String, Object> resp = gson.fromJson(post.getResponseBodyAsString(), new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(FINISHED, p1.getStatus());
        TestUtils.getInstance(Notebook.class).saveNote(note1, ANONYMOUS);
        // shutdown zeppelin and restart it
        AbstractTestRestApi.shutDown();
        StopInterpreter.main(new String[]{  });
        AbstractTestRestApi.startUp(RecoveryTest.class.getSimpleName(), false);
        // run the paragraph again, but change the text to print variable `user`.
        // can not recover the python interpreter, because it has been shutdown.
        note1 = TestUtils.getInstance(Notebook.class).getNote(note1.getId());
        p1 = note1.getParagraph(p1.getId());
        p1.setText("%python print(user)");
        post = AbstractTestRestApi.httpPost(("/notebook/job/" + (note1.getId())), "");
        Assert.assertEquals(resp.get("status"), "OK");
        post.releaseConnection();
        Assert.assertEquals(ERROR, p1.getStatus());
    }
}
