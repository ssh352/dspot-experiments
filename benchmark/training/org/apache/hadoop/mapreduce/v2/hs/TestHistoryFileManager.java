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
package org.apache.hadoop.mapreduce.v2.hs;


import FileSystem.FS_DEFAULT_NAME_KEY;
import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import JHAdminConfig.MR_HISTORY_DONE_DIR;
import JHAdminConfig.MR_HISTORY_INTERMEDIATE_DONE_DIR;
import JHAdminConfig.MR_HS_LOADED_JOBS_TASKS_MAX;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobIndexInfo;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;


public class TestHistoryFileManager {
    private static MiniDFSCluster dfsCluster = null;

    private static MiniDFSCluster dfsCluster2 = null;

    private static String coreSitePath;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCreateDirsWithoutFileSystem() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:1");
        testTryCreateHistoryDirs(conf, false);
    }

    @Test
    public void testCreateDirsWithFileSystem() throws Exception {
        TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_LEAVE);
        Assert.assertFalse(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
        testTryCreateHistoryDirs(TestHistoryFileManager.dfsCluster.getConfiguration(0), true);
    }

    @Test
    public void testCreateDirsWithAdditionalFileSystem() throws Exception {
        TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_LEAVE);
        TestHistoryFileManager.dfsCluster2.getFileSystem().setSafeMode(SAFEMODE_LEAVE);
        Assert.assertFalse(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
        Assert.assertFalse(TestHistoryFileManager.dfsCluster2.getFileSystem().isInSafeMode());
        // Set default configuration to the first cluster
        Configuration conf = new Configuration(false);
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, TestHistoryFileManager.dfsCluster.getURI().toString());
        FileOutputStream os = new FileOutputStream(TestHistoryFileManager.coreSitePath);
        conf.writeXml(os);
        os.close();
        testTryCreateHistoryDirs(TestHistoryFileManager.dfsCluster2.getConfiguration(0), true);
        // Directories should be created only in the default file system (dfsCluster)
        Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().exists(new Path(getDoneDirNameForTest())));
        Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().exists(new Path(getIntermediateDoneDirNameForTest())));
        Assert.assertFalse(TestHistoryFileManager.dfsCluster2.getFileSystem().exists(new Path(getDoneDirNameForTest())));
        Assert.assertFalse(TestHistoryFileManager.dfsCluster2.getFileSystem().exists(new Path(getIntermediateDoneDirNameForTest())));
    }

    @Test
    public void testCreateDirsWithFileSystemInSafeMode() throws Exception {
        TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_ENTER);
        Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
        testTryCreateHistoryDirs(TestHistoryFileManager.dfsCluster.getConfiguration(0), false);
    }

    @Test
    public void testCreateDirsWithFileSystemBecomingAvailBeforeTimeout() throws Exception {
        TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_ENTER);
        Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_LEAVE);
                    Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
                } catch (Exception ex) {
                    Assert.fail(ex.toString());
                }
            }
        }.start();
        testCreateHistoryDirs(TestHistoryFileManager.dfsCluster.getConfiguration(0), SystemClock.getInstance());
    }

    @Test(expected = YarnRuntimeException.class)
    public void testCreateDirsWithFileSystemNotBecomingAvailBeforeTimeout() throws Exception {
        TestHistoryFileManager.dfsCluster.getFileSystem().setSafeMode(SAFEMODE_ENTER);
        Assert.assertTrue(TestHistoryFileManager.dfsCluster.getFileSystem().isInSafeMode());
        final ControlledClock clock = new ControlledClock();
        clock.setTime(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    clock.setTime(3000);
                } catch (Exception ex) {
                    Assert.fail(ex.toString());
                }
            }
        }.start();
        testCreateHistoryDirs(TestHistoryFileManager.dfsCluster.getConfiguration(0), clock);
    }

    @Test
    public void testScanDirectory() throws Exception {
        Path p = new Path("any");
        FileContext fc = Mockito.mock(FileContext.class);
        Mockito.when(fc.makeQualified(p)).thenReturn(p);
        Mockito.when(fc.listStatus(p)).thenThrow(new FileNotFoundException());
        List<FileStatus> lfs = HistoryFileManager.scanDirectory(p, fc, null);
        // primarily, succcess is that an exception was not thrown.  Also nice to
        // check this
        Assert.assertNotNull(lfs);
    }

    @Test
    public void testHistoryFileInfoSummaryFileNotExist() throws Exception {
        TestHistoryFileManager.HistoryFileManagerTest hmTest = new TestHistoryFileManager.HistoryFileManagerTest();
        String job = "job_1410889000000_123456";
        Path summaryFile = new Path((job + ".summary"));
        JobIndexInfo jobIndexInfo = new JobIndexInfo();
        jobIndexInfo.setJobId(TypeConverter.toYarn(JobID.forName(job)));
        Configuration conf = TestHistoryFileManager.dfsCluster.getConfiguration(0);
        conf.set(MR_HISTORY_DONE_DIR, ("/" + (UUID.randomUUID())));
        conf.set(MR_HISTORY_INTERMEDIATE_DONE_DIR, ("/" + (UUID.randomUUID())));
        hmTest.serviceInit(conf);
        HistoryFileInfo info = hmTest.getHistoryFileInfo(null, null, summaryFile, jobIndexInfo, false);
        info.moveToDone();
        Assert.assertFalse(info.didMoveFail());
    }

    @Test
    public void testHistoryFileInfoLoadOversizedJobShouldReturnUnParsedJob() throws Exception {
        TestHistoryFileManager.HistoryFileManagerTest hmTest = new TestHistoryFileManager.HistoryFileManagerTest();
        int allowedMaximumTasks = 5;
        Configuration conf = TestHistoryFileManager.dfsCluster.getConfiguration(0);
        conf.setInt(MR_HS_LOADED_JOBS_TASKS_MAX, allowedMaximumTasks);
        hmTest.init(conf);
        // set up a job of which the number of tasks is greater than maximum allowed
        String jobId = "job_1410889000000_123456";
        JobIndexInfo jobIndexInfo = new JobIndexInfo();
        jobIndexInfo.setJobId(TypeConverter.toYarn(JobID.forName(jobId)));
        jobIndexInfo.setNumMaps(allowedMaximumTasks);
        jobIndexInfo.setNumReduces(allowedMaximumTasks);
        HistoryFileInfo info = hmTest.getHistoryFileInfo(null, null, null, jobIndexInfo, false);
        Job job = info.loadJob();
        Assert.assertTrue(("Should return an instance of UnparsedJob to indicate" + " the job history file is not parsed"), (job instanceof UnparsedJob));
    }

    @Test
    public void testHistoryFileInfoLoadNormalSizedJobShouldReturnCompletedJob() throws Exception {
        TestHistoryFileManager.HistoryFileManagerTest hmTest = new TestHistoryFileManager.HistoryFileManagerTest();
        final int numOfTasks = 100;
        Configuration conf = TestHistoryFileManager.dfsCluster.getConfiguration(0);
        conf.setInt(MR_HS_LOADED_JOBS_TASKS_MAX, ((numOfTasks + numOfTasks) + 1));
        hmTest.init(conf);
        // set up a job of which the number of tasks is smaller than the maximum
        // allowed, and therefore will be fully loaded.
        final String jobId = "job_1416424547277_0002";
        JobIndexInfo jobIndexInfo = new JobIndexInfo();
        jobIndexInfo.setJobId(TypeConverter.toYarn(JobID.forName(jobId)));
        jobIndexInfo.setNumMaps(numOfTasks);
        jobIndexInfo.setNumReduces(numOfTasks);
        final String historyFile = getClass().getClassLoader().getResource("job_2.0.3-alpha-FAILED.jhist").getFile();
        final Path historyFilePath = FileSystem.getLocal(conf).makeQualified(new Path(historyFile));
        HistoryFileInfo info = hmTest.getHistoryFileInfo(historyFilePath, null, null, jobIndexInfo, false);
        Job job = info.loadJob();
        Assert.assertTrue(("Should return an instance of CompletedJob as " + "a result of parsing the job history file of the job"), (job instanceof CompletedJob));
    }

    @Test
    public void testHistoryFileInfoShouldReturnCompletedJobIfMaxNotConfiged() throws Exception {
        TestHistoryFileManager.HistoryFileManagerTest hmTest = new TestHistoryFileManager.HistoryFileManagerTest();
        Configuration conf = TestHistoryFileManager.dfsCluster.getConfiguration(0);
        conf.setInt(MR_HS_LOADED_JOBS_TASKS_MAX, (-1));
        hmTest.init(conf);
        final String jobId = "job_1416424547277_0002";
        JobIndexInfo jobIndexInfo = new JobIndexInfo();
        jobIndexInfo.setJobId(TypeConverter.toYarn(JobID.forName(jobId)));
        jobIndexInfo.setNumMaps(100);
        jobIndexInfo.setNumReduces(100);
        final String historyFile = getClass().getClassLoader().getResource("job_2.0.3-alpha-FAILED.jhist").getFile();
        final Path historyFilePath = FileSystem.getLocal(conf).makeQualified(new Path(historyFile));
        HistoryFileInfo info = hmTest.getHistoryFileInfo(historyFilePath, null, null, jobIndexInfo, false);
        Job job = info.loadJob();
        Assert.assertTrue(("Should return an instance of CompletedJob as " + "a result of parsing the job history file of the job"), (job instanceof CompletedJob));
    }

    /**
     * This test sets up a scenario where the history files have already been
     * moved to the "done" directory (so the "intermediate" directory is empty),
     * but then moveToDone() is called again on the same history file. It
     * validates that the second moveToDone() still succeeds rather than throws a
     * FileNotFoundException.
     */
    @Test
    public void testMoveToDoneAlreadyMovedSucceeds() throws Exception {
        TestHistoryFileManager.HistoryFileManagerTest historyFileManager = new TestHistoryFileManager.HistoryFileManagerTest();
        long jobTimestamp = 1535436603000L;
        String job = ("job_" + jobTimestamp) + "_123456789";
        String intermediateDirectory = "/" + (UUID.randomUUID());
        String doneDirectory = "/" + (UUID.randomUUID());
        Configuration conf = TestHistoryFileManager.dfsCluster.getConfiguration(0);
        conf.set(MR_HISTORY_INTERMEDIATE_DONE_DIR, intermediateDirectory);
        conf.set(MR_HISTORY_DONE_DIR, doneDirectory);
        Path intermediateHistoryFilePath = new Path((((intermediateDirectory + "/") + job) + ".jhist"));
        Path intermediateConfFilePath = new Path((((intermediateDirectory + "/") + job) + "_conf.xml"));
        Path doneHistoryFilePath = new Path((((((doneDirectory + "/") + (JobHistoryUtils.timestampDirectoryComponent(jobTimestamp))) + "/123456/") + job) + ".jhist"));
        Path doneConfFilePath = new Path((((((doneDirectory + "/") + (JobHistoryUtils.timestampDirectoryComponent(jobTimestamp))) + "/123456/") + job) + "_conf.xml"));
        TestHistoryFileManager.dfsCluster.getFileSystem().createNewFile(doneHistoryFilePath);
        TestHistoryFileManager.dfsCluster.getFileSystem().createNewFile(doneConfFilePath);
        historyFileManager.serviceInit(conf);
        JobIndexInfo jobIndexInfo = new JobIndexInfo();
        jobIndexInfo.setJobId(TypeConverter.toYarn(JobID.forName(job)));
        jobIndexInfo.setFinishTime(jobTimestamp);
        HistoryFileInfo info = historyFileManager.getHistoryFileInfo(intermediateHistoryFilePath, intermediateConfFilePath, null, jobIndexInfo, false);
        info.moveToDone();
        Assert.assertFalse(info.isMovePending());
        Assert.assertEquals(doneHistoryFilePath.toString(), info.getHistoryFile().toUri().getPath());
        Assert.assertEquals(doneConfFilePath.toString(), info.getConfFile().toUri().getPath());
    }

    static class HistoryFileManagerTest extends HistoryFileManager {
        public HistoryFileManagerTest() {
            super();
        }

        public HistoryFileInfo getHistoryFileInfo(Path historyFile, Path confFile, Path summaryFile, JobIndexInfo jobIndexInfo, boolean isInDone) {
            return new HistoryFileInfo(historyFile, confFile, summaryFile, jobIndexInfo, isInDone);
        }
    }
}
