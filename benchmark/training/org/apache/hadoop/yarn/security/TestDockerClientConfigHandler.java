/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.security;


import DockerCredentialTokenIdentifier.KIND;
import java.io.File;
import java.nio.ByteBuffer;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.util.DockerClientConfigHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the functionality of the DockerClientConfigHandler.
 */
public class TestDockerClientConfigHandler {
    public static final String JSON = "{\"auths\": " + ((("{\"https://index.docker.io/v1/\": " + "{\"auth\": \"foobarbaz\"},") + "\"registry.example.com\": ") + "{\"auth\": \"bazbarfoo\"}}}");

    private static final String APPLICATION_ID = "application_2313_2131341";

    private File file;

    private Configuration conf = new Configuration();

    @Test
    public void testReadCredentialsFromConfigFile() throws Exception {
        Credentials credentials = DockerClientConfigHandler.readCredentialsFromConfigFile(new Path(file.toURI()), conf, TestDockerClientConfigHandler.APPLICATION_ID);
        Token token1 = credentials.getToken(new Text(("https://index.docker.io/v1/-" + (TestDockerClientConfigHandler.APPLICATION_ID))));
        Assert.assertEquals(KIND, token1.getKind());
        Assert.assertEquals("foobarbaz", new String(token1.getPassword()));
        DockerCredentialTokenIdentifier ti1 = ((DockerCredentialTokenIdentifier) (token1.decodeIdentifier()));
        Assert.assertEquals("https://index.docker.io/v1/", ti1.getRegistryUrl());
        Assert.assertEquals(TestDockerClientConfigHandler.APPLICATION_ID, ti1.getApplicationId());
        Token token2 = credentials.getToken(new Text(("registry.example.com-" + (TestDockerClientConfigHandler.APPLICATION_ID))));
        Assert.assertEquals(KIND, token2.getKind());
        Assert.assertEquals("bazbarfoo", new String(token2.getPassword()));
        DockerCredentialTokenIdentifier ti2 = ((DockerCredentialTokenIdentifier) (token2.decodeIdentifier()));
        Assert.assertEquals("registry.example.com", ti2.getRegistryUrl());
        Assert.assertEquals(TestDockerClientConfigHandler.APPLICATION_ID, ti2.getApplicationId());
    }

    @Test
    public void testGetCredentialsFromTokensByteBuffer() throws Exception {
        Credentials credentials = DockerClientConfigHandler.readCredentialsFromConfigFile(new Path(file.toURI()), conf, TestDockerClientConfigHandler.APPLICATION_ID);
        DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        ByteBuffer tokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        Credentials credentialsOut = DockerClientConfigHandler.getCredentialsFromTokensByteBuffer(tokens);
        Assert.assertEquals(credentials.numberOfTokens(), credentialsOut.numberOfTokens());
        for (Token<? extends TokenIdentifier> tkIn : credentials.getAllTokens()) {
            DockerCredentialTokenIdentifier ti = ((DockerCredentialTokenIdentifier) (tkIn.decodeIdentifier()));
            Token tkOut = credentialsOut.getToken(new Text((((ti.getRegistryUrl()) + "-") + (ti.getApplicationId()))));
            Assert.assertEquals(tkIn.getKind(), tkOut.getKind());
            Assert.assertEquals(new String(tkIn.getIdentifier()), new String(tkOut.getIdentifier()));
            Assert.assertEquals(new String(tkIn.getPassword()), new String(tkOut.getPassword()));
            Assert.assertEquals(tkIn.getService(), tkOut.getService());
        }
    }

    @Test
    public void testWriteDockerCredentialsToPath() throws Exception {
        File outFile = File.createTempFile("docker-client-config", "out");
        outFile.deleteOnExit();
        Credentials credentials = DockerClientConfigHandler.readCredentialsFromConfigFile(new Path(file.toURI()), conf, TestDockerClientConfigHandler.APPLICATION_ID);
        Assert.assertTrue(DockerClientConfigHandler.writeDockerCredentialsToPath(outFile, credentials));
        Assert.assertTrue(outFile.exists());
        String fileContents = FileUtils.readFileToString(outFile);
        Assert.assertTrue(fileContents.contains("auths"));
        Assert.assertTrue(fileContents.contains("registry.example.com"));
        Assert.assertTrue(fileContents.contains("https://index.docker.io/v1/"));
        Assert.assertTrue(fileContents.contains("foobarbaz"));
        Assert.assertTrue(fileContents.contains("bazbarfoo"));
    }
}

