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
package org.apache.hadoop.yarn.logaggregation;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.nativeio.NativeIO;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.TestContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogKey;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogReader;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogValue;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat.LogWriter;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAggregatedLogFormat {
    private static final File testWorkDir = new File("target", "TestAggregatedLogFormat");

    private static final Configuration conf = new Configuration();

    private static final FileSystem fs;

    private static final char filler = 'x';

    private static final Logger LOG = LoggerFactory.getLogger(TestAggregatedLogFormat.class);

    static {
        try {
            fs = FileSystem.get(TestAggregatedLogFormat.conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Test for Corrupted AggregatedLogs. The Logs should not write more data
    // if Logvalue.write() is called and the application is still
    // appending to logs
    @Test
    public void testForCorruptedAggregatedLogs() throws Exception {
        Configuration conf = new Configuration();
        File workDir = new File(TestAggregatedLogFormat.testWorkDir, "testReadAcontainerLogs1");
        Path remoteAppLogFile = new Path(workDir.getAbsolutePath(), "aggregatedLogFile");
        Path srcFileRoot = new Path(workDir.getAbsolutePath(), "srcFiles");
        ContainerId testContainerId = TestContainerId.newContainerId(1, 1, 1, 1);
        Path t = new Path(srcFileRoot, testContainerId.getApplicationAttemptId().getApplicationId().toString());
        Path srcFilePath = new Path(t, testContainerId.toString());
        long numChars = 950000;
        writeSrcFileAndALog(srcFilePath, "stdout", numChars, remoteAppLogFile, srcFileRoot, testContainerId);
        LogReader logReader = new LogReader(conf, remoteAppLogFile);
        LogKey rLogKey = new LogKey();
        DataInputStream dis = logReader.next(rLogKey);
        Writer writer = new StringWriter();
        try {
            LogReader.readAcontainerLogs(dis, writer);
        } catch (Exception e) {
            if (e.toString().contains("NumberFormatException")) {
                Assert.fail("Aggregated logs are corrupted.");
            }
        }
    }

    @Test
    public void testReadAcontainerLogs1() throws Exception {
        // Verify the output generated by readAContainerLogs(DataInputStream, Writer, logUploadedTime)
        testReadAcontainerLog(true);
        // Verify the output generated by readAContainerLogs(DataInputStream, Writer)
        testReadAcontainerLog(false);
    }

    @Test(timeout = 10000)
    public void testContainerLogsFileAccess() throws IOException {
        // This test will run only if NativeIO is enabled as SecureIOUtils
        // require it to be enabled.
        Assume.assumeTrue(NativeIO.isAvailable());
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        File workDir = new File(TestAggregatedLogFormat.testWorkDir, "testContainerLogsFileAccess1");
        Path remoteAppLogFile = new Path(workDir.getAbsolutePath(), "aggregatedLogFile");
        Path srcFileRoot = new Path(workDir.getAbsolutePath(), "srcFiles");
        String data = "Log File content for container : ";
        // Creating files for container1. Log aggregator will try to read log files
        // with illegal user.
        ApplicationId applicationId = ApplicationId.newInstance(1, 1);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ContainerId testContainerId1 = ContainerId.newContainerId(applicationAttemptId, 1);
        Path appDir = new Path(srcFileRoot, testContainerId1.getApplicationAttemptId().getApplicationId().toString());
        Path srcFilePath1 = new Path(appDir, testContainerId1.toString());
        String stdout = "stdout";
        String stderr = "stderr";
        writeSrcFile(srcFilePath1, stdout, ((data + (testContainerId1.toString())) + stdout));
        writeSrcFile(srcFilePath1, stderr, ((data + (testContainerId1.toString())) + stderr));
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        try (LogWriter logWriter = new LogWriter()) {
            logWriter.initialize(conf, remoteAppLogFile, ugi);
            LogKey logKey = new LogKey(testContainerId1);
            String randomUser = "randomUser";
            LogValue logValue = Mockito.spy(new LogValue(Collections.singletonList(srcFileRoot.toString()), testContainerId1, randomUser));
            // It is trying simulate a situation where first log file is owned by
            // different user (probably symlink) and second one by the user itself.
            // The first file should not be aggregated. Because this log file has
            // the invalid user name.
            Mockito.when(logValue.getUser()).thenReturn(randomUser).thenReturn(ugi.getShortUserName());
            logWriter.append(logKey, logValue);
        }
        BufferedReader in = new BufferedReader(new FileReader(new File(remoteAppLogFile.toUri().getRawPath())));
        String line;
        StringBuffer sb = new StringBuffer("");
        while ((line = in.readLine()) != null) {
            TestAggregatedLogFormat.LOG.info(line);
            sb.append(line);
        } 
        line = sb.toString();
        String expectedOwner = ugi.getShortUserName();
        if (Path.WINDOWS) {
            final String adminsGroupString = "Administrators";
            if (Arrays.asList(ugi.getGroupNames()).contains(adminsGroupString)) {
                expectedOwner = adminsGroupString;
            }
        }
        // This file: stderr should not be aggregated.
        // And we will not aggregate the log message.
        String stdoutFile1 = StringUtils.join(File.separator, Arrays.asList(new String[]{ workDir.getAbsolutePath(), "srcFiles", testContainerId1.getApplicationAttemptId().getApplicationId().toString(), testContainerId1.toString(), stderr }));
        // The file: stdout is expected to be aggregated.
        String stdoutFile2 = StringUtils.join(File.separator, Arrays.asList(new String[]{ workDir.getAbsolutePath(), "srcFiles", testContainerId1.getApplicationAttemptId().getApplicationId().toString(), testContainerId1.toString(), stdout }));
        String message2 = ((((("Owner '" + expectedOwner) + "' for path ") + stdoutFile2) + " did not match expected owner '") + (ugi.getShortUserName())) + "'";
        Assert.assertFalse(line.contains(message2));
        Assert.assertFalse(line.contains(((data + (testContainerId1.toString())) + stderr)));
        Assert.assertTrue(line.contains(((data + (testContainerId1.toString())) + stdout)));
    }
}
