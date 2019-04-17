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
package org.apache.flink.hdfstests;


import java.io.DataInputStream;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.util.TestLogger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for distributing files with {@link org.apache.flink.api.common.cache.DistributedCache} via HDFS.
 */
public class DistributedCacheDfsTest extends TestLogger {
    private static final String testFileContent = "Goethe - Faust: Der Tragoedie erster Teil\n" + (((((((((((((("Prolog im Himmel.\n" + "Der Herr. Die himmlischen Heerscharen. Nachher Mephistopheles. Die drei\n") + "Erzengel treten vor.\n") + "RAPHAEL: Die Sonne toent, nach alter Weise, In Brudersphaeren Wettgesang,\n") + "Und ihre vorgeschriebne Reise Vollendet sie mit Donnergang. Ihr Anblick\n") + "gibt den Engeln Staerke, Wenn keiner Sie ergruenden mag; die unbegreiflich\n") + "hohen Werke Sind herrlich wie am ersten Tag.\n") + "GABRIEL: Und schnell und unbegreiflich schnelle Dreht sich umher der Erde\n") + "Pracht; Es wechselt Paradieseshelle Mit tiefer, schauervoller Nacht. Es\n") + "schaeumt das Meer in breiten Fluessen Am tiefen Grund der Felsen auf, Und\n") + "Fels und Meer wird fortgerissen Im ewig schnellem Sphaerenlauf.\n") + "MICHAEL: Und Stuerme brausen um die Wette Vom Meer aufs Land, vom Land\n") + "aufs Meer, und bilden wuetend eine Kette Der tiefsten Wirkung rings umher.\n") + "Da flammt ein blitzendes Verheeren Dem Pfade vor des Donnerschlags. Doch\n") + "deine Boten, Herr, verehren Das sanfte Wandeln deines Tags.");

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @ClassRule
    public static final MiniClusterWithClientResource MINI_CLUSTER_RESOURCE = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setNumberTaskManagers(1).setNumberSlotsPerTaskManager(1).build());

    private static MiniDFSCluster hdfsCluster;

    private static Configuration conf = new Configuration();

    private static Path testFile;

    private static Path testDir;

    @Test
    public void testDistributeFileViaDFS() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.registerCachedFile(DistributedCacheDfsTest.testFile.toString(), "test_data", false);
        env.registerCachedFile(DistributedCacheDfsTest.testDir.toString(), "test_dir", false);
        env.fromElements(1).map(new DistributedCacheDfsTest.TestMapFunction()).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        env.execute("Distributed Cache Via Blob Test Program");
    }

    private static class TestMapFunction extends RichMapFunction<Integer, String> {
        private static final long serialVersionUID = -3917258280687242969L;

        @Override
        public String map(Integer value) throws Exception {
            final Path actualFile = new Path(getRuntimeContext().getDistributedCache().getFile("test_data").toURI());
            Path path = new Path(actualFile.toUri());
            Assert.assertFalse(path.getFileSystem().isDistributedFS());
            DataInputStream in = new DataInputStream(actualFile.getFileSystem().open(actualFile));
            String contents = in.readUTF();
            Assert.assertEquals(DistributedCacheDfsTest.testFileContent, contents);
            final Path actualDir = new Path(getRuntimeContext().getDistributedCache().getFile("test_dir").toURI());
            FileStatus fileStatus = actualDir.getFileSystem().getFileStatus(actualDir);
            Assert.assertTrue(fileStatus.isDir());
            FileStatus[] fileStatuses = actualDir.getFileSystem().listStatus(actualDir);
            Assert.assertEquals(2, fileStatuses.length);
            return contents;
        }
    }
}
