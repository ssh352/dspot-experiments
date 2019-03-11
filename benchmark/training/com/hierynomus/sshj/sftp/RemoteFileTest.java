/**
 * Copyright (C)2009 - SSHJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hierynomus.sshj.sftp;


import OpenMode.CREAT;
import OpenMode.WRITE;
import com.hierynomus.sshj.test.SshFixture;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Random;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPEngine;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RemoteFileTest {
    @Rule
    public SshFixture fixture = new SshFixture();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void shouldNotGoOutOfBoundsInReadAheadInputStream() throws IOException {
        SSHClient ssh = fixture.setupConnectedDefaultClient();
        ssh.authPassword("test", "test");
        SFTPEngine sftp = init();
        RemoteFile rf;
        File file = temp.newFile("SftpReadAheadTest.bin");
        rf = sftp.open(file.getPath(), EnumSet.of(WRITE, CREAT));
        byte[] data = new byte[8192];
        new Random(53).nextBytes(data);
        data[3072] = 1;
        rf.write(0, data, 0, data.length);
        rf.close();
        MatcherAssert.assertThat("The file should exist", file.exists());
        rf = sftp.open(file.getPath());
        InputStream rs = /* maxUnconfirmedReads */
        rf.new ReadAheadRemoteFileInputStream(16);
        byte[] test = new byte[4097];
        int n = 0;
        while (n < 2048) {
            n += rs.read(test, n, (2048 - n));
        } 
        while (n < 3072) {
            n += rs.read(test, n, (3072 - n));
        } 
        MatcherAssert.assertThat("buffer overrun", ((test[3072]) == 0));
        n += rs.read(test, n, ((test.length) - n));// --> ArrayIndexOutOfBoundsException

        byte[] test2 = new byte[data.length];
        System.arraycopy(test, 0, test2, 0, test.length);
        while (n < (data.length)) {
            n += rs.read(test2, n, ((data.length) - n));
        } 
        MatcherAssert.assertThat("The written and received data should match", data, CoreMatchers.equalTo(test2));
    }
}

