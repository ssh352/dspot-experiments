/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * See https://issues.jboss.org/browse/UNDERTOW-1011
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ChunkedRequestNotConsumedTestCase {
    private static final String MESSAGE = "My HTTP Request!";

    @Test
    public void testChunkedRequestNotConsumed() throws IOException {
        HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
        TestHttpClient client = new TestHttpClient();
        try {
            final Random random = new Random();
            final int seed = random.nextInt();
            System.out.print(("Using Seed " + seed));
            random.setSeed(seed);
            for (int i = 0; i < 3; ++i) {
                post.setEntity(new StringEntity("") {
                    @Override
                    public long getContentLength() {
                        return -1;
                    }

                    @Override
                    public boolean isChunked() {
                        return true;
                    }

                    @Override
                    public void writeTo(OutputStream outstream) throws IOException {
                        outstream.flush();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        outstream.write(ChunkedRequestNotConsumedTestCase.MESSAGE.getBytes(StandardCharsets.US_ASCII));
                    }
                });
                HttpResponse result = client.execute(post);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                HttpClientUtils.readResponse(result);
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

