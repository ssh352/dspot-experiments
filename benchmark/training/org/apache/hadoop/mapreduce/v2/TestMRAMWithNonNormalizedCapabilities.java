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
package org.apache.hadoop.mapreduce.v2;


import JobStatus.State.SUCCEEDED;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.SleepJob;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRAMWithNonNormalizedCapabilities {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRAMWithNonNormalizedCapabilities.class);

    private static FileSystem localFs;

    protected static MiniMRYarnCluster mrCluster = null;

    private static Configuration conf = new Configuration();

    static {
        try {
            TestMRAMWithNonNormalizedCapabilities.localFs = FileSystem.getLocal(TestMRAMWithNonNormalizedCapabilities.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static Path TEST_ROOT_DIR = new Path("target", ((TestMRAMWithNonNormalizedCapabilities.class.getName()) + "-tmpDir")).makeQualified(TestMRAMWithNonNormalizedCapabilities.localFs.getUri(), TestMRAMWithNonNormalizedCapabilities.localFs.getWorkingDirectory());

    static Path APP_JAR = new Path(TestMRAMWithNonNormalizedCapabilities.TEST_ROOT_DIR, "MRAppJar.jar");

    /**
     * To ensure nothing broken after we removed normalization
     * from the MRAM side
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJobWithNonNormalizedCapabilities() throws Exception {
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRAMWithNonNormalizedCapabilities.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        JobConf jobConf = new JobConf(getConfig());
        jobConf.setInt("mapreduce.map.memory.mb", 700);
        jobConf.setInt("mapred.reduce.memory.mb", 1500);
        SleepJob sleepJob = new SleepJob();
        sleepJob.setConf(jobConf);
        Job job = sleepJob.createJob(3, 2, 1000, 1, 500, 1);
        job.setJarByClass(SleepJob.class);
        job.addFileToClassPath(TestMRAMWithNonNormalizedCapabilities.APP_JAR);// The AppMaster jar itself.

        job.submit();
        boolean completed = job.waitForCompletion(true);
        Assert.assertTrue("Job should be completed", completed);
        Assert.assertEquals("Job should be finished successfully", SUCCEEDED, job.getJobState());
    }
}

